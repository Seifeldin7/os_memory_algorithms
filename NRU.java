/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nru;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;

public class NRU {

    public static void print_frames(int[] frames) {
        for (int i = 0; i < frames.length; i++) {
            System.out.print(frames[i] + " ");
        }
        System.out.println();
    }

    public static void FIFO(int[] string, int[] frames) {
        int faults = 0;
        List<Integer> fifo_frames = new ArrayList<>();
        for (int i = 0; i < string.length; i++) {
            boolean exist = false;
            if (i < frames.length) {
                frames[i] = string[i];
                faults++;
                fifo_frames.add(string[i]);
                continue;
            }
            for (int j = 0; j < frames.length; j++) {
                if (frames[j] == string[i]) {
                    exist = true;
                    break;
                }
            }
            if (!exist || (string[i] == 0 && i < frames.length)) {
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
        List<Integer> optimal_frames = new ArrayList<>();
        for (int i = 0; i < string.length; i++) {
            boolean exist = false;
            int counter = 0;
            if (i < frames.length) {
                frames[i] = string[i];
                faults++;
                print_frames(frames);
                continue;
            }
            for (int j = 0; j < frames.length; j++) {
                if (frames[j] == string[i]) {
                    exist = true;
                    break;
                }
            }
            if (!exist || (string[i] == 0 && i < frames.length)) {
                faults++;
                for (int j = i+1; j < string.length && counter != frames.length ; j++) {
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
                    if (counter == frames.length ) {
                        for (int k = 0; k < frames.length; k++) {
                            if (frames[k] == optimal_frames.get(optimal_frames.size() - 1)) {
                                frames[k] = string[i];
                            }
                        }
                    }
                    if(j==string.length-1 && optimal_frames.size()<frames.length){
                        for (int k = 0; k < frames.length; k++) {
                            if(!optimal_frames.contains(frames[k])){
                                frames[k] = string[i];
                            }
                        }
                    }
                }

            }
            print_frames(frames);
        }
        System.out.println("No of faults = " + faults);
    }

    public static void main(String[] args) {
        int choice, string_length, no_of_frames;
        int Ref_string[], frames[];
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();
        int stop = 1;
        while (stop != 0) {
            System.out.println("Enter the length of the page-reference string:");
            string_length = sc.nextInt();
            System.out.println("Enter 0 for FIFO, 1 for optimal, 2 for NRU");
            choice = sc.nextInt();
            Ref_string = new int[string_length];
            /*do{
            no_of_frames =rand.nextInt(21);
            }while(no_of_frames!=0);*/
            no_of_frames = 3;
            frames = new int[no_of_frames];
            for (int i = 0; i < Ref_string.length; i++) {
                Ref_string[i] = sc.nextInt();
            }
            for (int i = 0; i < Ref_string.length; i++) {
                System.out.print(Ref_string[i] + " ");
            }
            System.out.println();
            switch (choice) {
                case 0:
                    FIFO(Ref_string, frames);
                case 1:
                    Optimal(Ref_string, frames);
            }
            System.out.println("Press 0 to stop");
            stop = sc.nextInt();
        }
    }

}
