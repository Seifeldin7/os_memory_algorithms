package nru;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;

class Page {

    private int ID;
    private boolean rBit = true;//reference bit
    private boolean mBit = false;//modification bit
    private long lastReference;//for the LRU
    private long useCount = 0;

    Page(int ID) {
        this.ID = ID;
    }

    public boolean isReferenced() {
        return rBit;
    }

    public void referenced(boolean r_bit) {
        this.rBit = r_bit;
    }

    public boolean isModified() {
        return mBit;
    }

    public void modified(boolean m_bit) {
        this.mBit = m_bit;
    }

    public long getLastReference() {
        return lastReference;
    }

    public void setLastReference(long currentTime) {
        this.lastReference = currentTime;
    }

    public long getUseCount() {
        return useCount;
    }

    public void incrementUseCount() {
        useCount++;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}

class Enhanced2ndChance {

    private int memorySize;
    private List<Integer> referenceList;
    private List<Page> memory;

    public Enhanced2ndChance(int memorySize, List<Integer> referenceList) {
        memory = new ArrayList<>(memorySize);
        this.memorySize = memorySize;
        this.referenceList = new ArrayList<>(referenceList);
    }

    /**
     * this method searches for a page in memory
     *
     * @param pageID the id of the page to be searched
     * @return true if it is found
     */
    // second chance algorithm implementation 
    public void ESC() {
        String name = "Enhanced Second Chance";
        printinit(name);
        int faultCount = 0;
        int pointer = 0;
        for (int i = 0; i < referenceList.size(); i++) {
            printMemoryContents();//printing statement
            printMemoryRefrenceBit();//printing statement
            printMemoryModifiedBit();//printing statement
            int pageID = referenceList.get(i);
            if (pageInMemory(pageID)) {
                printHit(pageID);//printing statement
                Random rand = new Random();
                int index = getIndex(pageID);

                memory.get(index).referenced(true);
                if (!memory.get(index).isModified()) {
                    memory.get(index).modified(rand.nextBoolean());
                }
            } else {
                //algorithm
                printFault(pageID);//printing statement
                faultCount++;

                if (fullMemory()) {
                    Page page = new Page(pageID);
                    page.referenced(false);
                    page.modified(false);

                    boolean first_cycle = true;
                    int full_cycle = pointer;
                    while (true) {
                        if (memory.get(pointer).isReferenced()) {
                            memory.get(pointer).referenced(false);
                        } else {
                            if (!memory.get(pointer).isModified()) {
                                break;
                            } else if (memory.get(pointer).isModified() && !first_cycle) {
                                break;
                            }
                        }
                        pointer = (pointer == memory.size() - 1) ? 0 : pointer + 1; //cycle again
                        if (full_cycle == pointer) {
                            first_cycle = true;
                        }
                    }
                    printVictimPage(memory.get(pointer).getID());
                    //to be able to use FIFO
                    memory.set(pointer, page);
                    pointer = (pointer == memory.size() - 1) ? 0 : pointer + 1; //start from next frame
                } else {
                    Page page = new Page(pageID);
                    page.referenced(false);
                    page.modified(false);
                    memory.add(page);
                }
            }
        }
        printFaultCount(faultCount, name);
    }

    private void printHit(int pageID) {
        System.out.println("The page no. " + pageID + " was found in memory");
    }

    private int getIndex(int pageID) {
        for (int i = 0; i < memory.size(); i++) {
            if (memory.get(i).getID() == pageID) {
                return i;
            }
        }
        return -1;
    }

    private boolean pageInMemory(int pageID) {
        for (Page p : memory) {
            if (pageID == p.getID()) {
                return true;
            }
        }
        return false;
    }

    private void printMemoryModifiedBit() {
        if (memory.size() == 0) {
            return;
        }
        System.out.println("        the memory Modified bit are:");
        System.out.print("        ");
        for (Page p : memory) {
            if (p.isModified()) {
                System.out.print(1 + " ");
            } else {
                System.out.print(0 + " ");
            }
        }
        System.out.println();
    }

    private void printMemoryRefrenceBit() {
        if (memory.size() == 0) {
            return;
        }
        System.out.println("        the memory Reference Bit are:");
        System.out.print("        ");
        for (Page p : memory) {
            if (p.isReferenced()) {
                System.out.print(1 + " ");
            } else {
                System.out.print(0 + " ");
            }
        }
        System.out.println();
    }

    private boolean fullMemory() {
        return memory.size() >= memorySize;
    }

    private void printFault(int pageID) {
        System.out.println("The page no. " + pageID + " faulted");
    }

    private void printVictimPage(int pageID) {
        System.out.println("Page no. " + pageID + " had to be removed for new page");
    }

    private void printinit(String algorithmName) {
        System.out.println("*********" + algorithmName + "*********");
        System.out.println("Memory size: " + memorySize + " pages");
        System.out.println("Reference String: ");
        for (Integer i : referenceList) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    private void printMemoryContents() {
        if (memory.size() == 0) {
            System.out.println("still empty memory");
            return;
        }
        System.out.println("the memory has the following pages:");
        for (Page p : memory) {
            System.out.print(p.getID() + " ");
        }
        System.out.println();
    }

    private void printFaultCount(int faultCount, String algorithmName) {
        System.out.println("There has been " + faultCount + " faults using " + algorithmName);
    }

}

class SC {

    //number of frames
    int numberOfFrames;
    //actual frames
    char[] frame;
    //reference bit for each frame
    boolean[] referenceBit;
    //sequence of reference string
    String sequence = new String();
    //number of page faults
    public int pageFault = 0;
    //current page string
    char page;
    //flag to check if pointer should point to 1st frame (queue rotation)
    boolean flag = false;
    //scanner to get input from user
    Scanner sc = new Scanner(System.in);

    // second chance algorithm implementation 
    public int getFaults() {
        return this.pageFault;
    }

    public void arrayTraverse() {
        //run the algorithm till the reference string ends
        while (!sequence.isEmpty()) {
            for (int pointer = 0; pointer < numberOfFrames; pointer++) {
                //make sure reference string is not empty
                if (!sequence.isEmpty()) {

                    //check if pointer finishes one complete cycle on queue
                    if (flag) {
                        //set pointer to 1st frame in queue
                        pointer = 0;
                        flag = false;
                    }
                    //load 1st page from reference string
                    page = sequence.charAt(0);
                    //change queue of frames to string to check if page already exsists in queue
                    String test = new String(frame);
                    //returns -1 if page doesn't exsist in the queue
                    int t = test.indexOf(page);

                    //check if the frame is empty
                    if (frame[pointer] == 0 && t == -1) {
                        //add page to frame
                        addPage(pointer);
                    } //check if the page already exists
                    else if (t != -1) {

                        //set corresponding reference bit into true
                        referenceBit[t] = true;
                        //decrement the pointer to stay on the same frame
                        if (pointer == 0) {
                            flag = true;
                        } else {
                            pointer--;
                        }

                        //remove page from reference string
                        sequence = sequence.substring(1);

                    } //check if the old page has a second chance
                    else if (referenceBit[pointer] == true) {
                        //set the corresponding reference bit to false                    
                        referenceBit[pointer] = false;
                        //break out of the current loop & check next frame
                        printFrames(page, pointer + 1);
                        continue;
                    } //page doesn't exsist in frame, exsisting page has no second chance 
                    else {
                        //add candidate page to the frame, remove old page
                        addPage(pointer);
                    }
                    printFrames(page, (flag == true) ? pointer : pointer + 1);

                }
            }

        }
    }// arrayTraverse

    public void addPage(int index) {
        //add page to frame
        frame[index] = page;
        //set corresponding reference bit into false
        referenceBit[index] = false;
        //remove page from reference string
        sequence = sequence.substring(1);
        //increment page faults
        pageFault++;
    }

    public void inputReferenceString() {
        System.out.println("Please enter the reference string");
        sequence = sc.next();
    }

    public void inputNumberOfFrames(int no) {
        System.out.println("Please enter the number of frames");
        numberOfFrames = no;
        frame = new char[numberOfFrames];
        referenceBit = new boolean[numberOfFrames];
    }

    public void printFrames(char page, int pointer) {
        System.out.println("For Page: " + page);
        System.out.println(" -----------");
        for (int i = 0; i < frame.length; i++) {
            System.out.print("| " + frame[i] + " | " + referenceBit[i] + " |");
            if (i == pointer || (pointer == frame.length && i == 0)) {
                System.out.print("<--" + "\n");
            } else {
                System.out.print("\n");
            }
            System.out.println(" -----------");
        }
        System.out.println("\n");
        System.out.println("\n");
    }
}

public class NRU {

    public static void LFU(int[] pageRef, int[] frames, int[] counter, int[] frequency) {
        int count = 0;
        int pageFaults = 0;
        for (int i = 0; i < pageRef.length; i++) {
            boolean exist = false;
            for (int j = 0; j < frames.length; j++) {
                if (frames[j] == pageRef[i]) {
                    counter[frames[j]] = 0;
                    frequency[pageRef[i]]++;
                    exist = true;
                    break;
                }
            }

            for (int j = 0; j < frames.length; j++) {
                if (frames[j] != -1) {
                    counter[frames[j]]++;
                }
            }

            for (int j = 0; j < frames.length; j++) {
                if (frames[j] == -1 && !exist) {
                    frames[j] = pageRef[i];
                    frequency[pageRef[i]]++;
                    pageFaults++;
                    counter[frames[j]]++;
                    exist = true;
                }
            }
            print_frames(frames);
            count++;
            if (frames[frames.length - 1] != -1) {
                break;
            }
        }

        int maxCounter = 0;
        for (int i = count; i < pageRef.length; i++) {
            boolean exist = false;
            for (int j = 0; j < frames.length; j++) {
                if (frames[j] == pageRef[i]) {
                    exist = true;
                    break;
                }
            }

            for (int j = 0; j < frames.length; j++) {
                if (frames[j] != -1) {
                    counter[frames[j]]++;
                }
            }

            int leastFrequency = 100;
            for (int j = 0; j < 100; j++) {
                if (frequency[j] < leastFrequency && frequency[j] != 0) {
                    //System.out.println("frequency["+j+"] = "+frequency[j]);
                    leastFrequency = frequency[j];
                    //System.out.println("Least frequency = "+leastFrequency);
                }
            }

            if (!exist) {
                int sameFreqCount = 0;
                for (int j = 0; j < frequency.length; j++) {
                    if (frequency[j] == leastFrequency && frequency[j] != 0) {
                        sameFreqCount++;
                    }
                }
                //System.out.println("sameFreqCount = "+sameFreqCount);

                if (sameFreqCount == 1) //LFU
                {
                    for (int j = 0; j < frames.length; j++) {
                        if (i > frames.length - 1) {
                            if (frequency[frames[j]] == leastFrequency) {
                                counter[frames[j]] = 0;
                                frequency[frames[j]] = 0;
                                frames[j] = pageRef[i];
                                pageFaults++;
                                frequency[pageRef[i]]++;
                                //printFreq();
                                break;
                            }
                        }
                    }
                } else //LRU
                {
                    for (int j = 0; j < 100; j++) {
                        if (frequency[j] == leastFrequency && counter[j] > counter[maxCounter]) {
                            maxCounter = j;
                        }
                    }

                    for (int j = 0; j < frames.length; j++) {
                        if (i > frames.length - 1) {
                            if (frames[j] == maxCounter) {
                                counter[frames[j]] = 0;
                                frequency[frames[j]] = 0;
                                frames[j] = pageRef[i];
                                pageFaults++;
                                frequency[pageRef[i]]++;
                                //printFreq();
                                break;
                            }
                        }
                    }
                }
                print_frames(frames);
            } else //if page exists
            {
                frequency[pageRef[i]]++; //increment frequency of this page
                counter[pageRef[i]] = 0; //reset page counter because now it has been recently used
                //printFreq();
            }
        }
        System.out.println("Page Faults of LFU are " + pageFaults);
    }

    public static void print_frames(int[] frames) {
        for (int i = 0; i < frames.length; i++) {
            System.out.print(frames[i] + " ");
        }
        System.out.println();
    }

    public static void LRU(int[] pageRef, int[] frames, int[] counter) {
        int count = 0;
        int pageFaults = 0;
        for (int i = 0; i < pageRef.length; i++) {
            boolean exist = false;
            for (int j = 0; j < frames.length; j++) {
                if (frames[j] == pageRef[i]) {
                    counter[frames[j]] = 0;
                    exist = true;
                    break;
                }
            }

            for (int j = 0; j < frames.length; j++) {
                if (frames[j] != -1) {
                    counter[frames[j]]++;
                }
            }

            for (int j = 0; j < frames.length; j++) {
                if (frames[j] == -1 && !exist) {
                    frames[j] = pageRef[i];
                    pageFaults++;
                    counter[frames[j]]++;
                    exist = true;
                }
            }
            print_frames(frames);
            count++;
            if (frames[frames.length - 1] != -1) {
                break;
            }
        }

        int maxCounter = counter[0];
        for (int i = count; i < pageRef.length; i++) {
            boolean exist = false;

            for (int j = 0; j < frames.length; j++) //check if page already exists
            {
                if (frames[j] == pageRef[i]) {
                    exist = true;
                    break;
                }
            }

            for (int j = 0; j < frames.length; j++) //increment counters of all pages
            {
                if (frames[j] != -1) {
                    counter[frames[j]]++;
                }
            }

            for (int j = 0; j < 100/*counter.length*/; j++) {
                if (counter[j] > counter[maxCounter]) {
                    maxCounter = j;
                }
            }

            if (!exist) //if page doesn't exist
            {
                for (int j = 0; j < frames.length; j++) {
                    if (i > frames.length - 1) {
                        if (frames[j] == maxCounter) //page that has largest counter means it has been
                        {                           //around for many cycles and not been recently used so it will be removed
                            counter[frames[j]] = 0;
                            frames[j] = pageRef[i];
                            pageFaults++;
                            break;
                        }
                    }
                }
                print_frames(frames);
            } else //if page exists
            {
                counter[pageRef[i]] = 0; //reset counter because now page has been recently used
            }
        }
        System.out.println("Page Faults of LRU are " + pageFaults);
    }

    public static void FIFO(int[] string, int[] frames) {
        int faults = 0;
        int counter = 0;
        List<Integer> fifo_frames = new ArrayList<>();
        for (int i = 0; i < string.length; i++) {
            boolean exist = false;
            if (counter < frames.length) {
                if (fifo_frames.contains(string[i])) {
                    print_frames(frames);
                    continue;
                }
                frames[counter] = string[i];
                faults++;
                counter++;
                fifo_frames.add(string[i]);
                print_frames(frames);
                continue;
            }
            for (int j = 0; j < frames.length; j++) {
                if (frames[j] == string[i]) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                faults++;
                for (int k = 0; k < frames.length; k++) {
                    if (frames[k] == fifo_frames.get(0)) {
                        frames[k] = string[i];
                    }
                }
                fifo_frames.remove(fifo_frames.get(0));
                fifo_frames.add(string[i]);

            }
            print_frames(frames);
        }
        System.out.println("No of faults = " + faults);
    }

    public static void Optimal(int[] string, int[] frames) {
        int faults = 0;
        int count = 0;
        List<Integer> optimal_frames = new ArrayList<>();
        List<Integer> optimalframes = new ArrayList<>();
        for (int i = 0; i < string.length; i++) {
            boolean exist = false;
            int counter = 0;
            if (count < frames.length) {
                if (optimalframes.contains(string[i])) {
                    print_frames(frames);
                    continue;
                }
                frames[count] = string[i];
                faults++;
                count++;
                optimalframes.add(string[i]);
                print_frames(frames);
                continue;
            }
            for (int j = 0; j < frames.length; j++) {
                if (frames[j] == string[i]) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                faults++;
                for (int j = i + 1; j < string.length; j++) {
                    boolean in_frames = false;
                    for (int k = 0; k < frames.length; k++) {
                        if (string[j] == frames[k]) {
                            in_frames = true;
                        }
                    }
                    if (!optimal_frames.contains(string[j]) && in_frames) {
                        optimal_frames.add(string[j]);
                        counter++;
                    }
                    if (counter == frames.length) {
                        for (int k = 0; k < frames.length; k++) {
                            if (frames[k] == optimal_frames.get(optimal_frames.size() - 1)) {
                                frames[k] = string[i];
                                break;
                            }
                        }
                    }
                    if (j == string.length - 1 && optimal_frames.size() < frames.length) {
                        for (int k = 0; k < frames.length; k++) {
                            if (!optimal_frames.contains(frames[k])) {
                                frames[k] = string[i];
                                break;
                            }
                        }
                    }

                }

            }
            optimal_frames.clear();
            print_frames(frames);
        }
        System.out.println("No of faults = " + faults);
    }

    public static void main(String[] args) {
        int choice, string_length, no_of_frames;
        int Ref_string[], frames[];
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();
        int[] counter = new int[100];
        int[] frequency = new int[100];
        while (true) {
            System.out.println("Enter 0 for FIFO 1 for Optimal 2 for Lru 3 for LFU , 4 for Esc 5 for SC:");
            choice = sc.nextInt();
            System.out.println("Enter the length of the page-reference string:");
            string_length = sc.nextInt();
            Ref_string = new int[string_length];
            do {
                no_of_frames = rand.nextInt(21);
            } while (no_of_frames == 0);
            // no_of_frames = 4; for test
            frames = new int[no_of_frames];
            Arrays.fill(frames, -1);
            for (int i = 0; i < Ref_string.length; i++) {
                Ref_string[i] = rand.nextInt(100);
            }
            for (int i = 0; i < Ref_string.length; i++) {
                System.out.print(Ref_string[i] + " ");
            }
            System.out.println();
            switch (choice) {
                case 0:
                    System.out.println("FIFO:");
                    Arrays.fill(frames, -1);
                    FIFO(Ref_string, frames);
                    break;
                case 1:
                    System.out.println("Optimal:");
                    Arrays.fill(frames, -1);
                    Optimal(Ref_string, frames);
                    break;
                case 2:
                    System.out.println("LRU:");
                    Arrays.fill(frames, -1);
                    LRU(Ref_string, frames, counter);
                    break;
                case 3:
                    System.out.println("LFU:");
                    Arrays.fill(frames, -1);
                    LFU(Ref_string, frames, counter, frequency);
                    break;
                case 4:
                    System.out.println("ESC:");
                    List list = new ArrayList(100);
                    for (int i = 0; i < Ref_string.length; i++) {
                        list.add(i);
                    }
                    Enhanced2ndChance m = new Enhanced2ndChance(no_of_frames, list);
                    m.ESC();
                    break;
                case 5:
                    SC s = new SC();
                    s.inputReferenceString();
                    s.inputNumberOfFrames(no_of_frames);
                    s.arrayTraverse();
                    System.out.println("Page Faults: " + s.pageFault);
                    break;
                default:
                    System.out.println("You have entered the wrong number");
                    break;
            }

        }
    }
}
