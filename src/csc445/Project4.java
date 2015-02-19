/*
 *   Caleb Lawson
 *   CSC 445
 *   Completed 2/18/2015
 *
 *   Project4 counts the number of paths from a starting node to an ending node
 *   on a graph.
 */
package csc445;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Project4 {

    int count = 0; // Number of paths from starting node to ending node.
    int size; // Size of matrix.
    int start;
    int end;
    boolean fileLoaded = false;

    boolean[][] adjMat; // Adjacency matrix.
    ArrayList<Integer> visited = new ArrayList<>(); // Visited node placeholder.

    public Project4() {
        long startTime;
        long endTime;
        Scanner input = new Scanner(System.in);

        do { // If the file doesn't exist, try again.
            System.out.print("Enter the filename of the matrix: ");
            String fileName = input.next();
            getFile(fileName);
        } while (!fileLoaded);

        do { // If the value is out of range, try again.
            System.out.print("Please choose a starting node from 0-" + (size - 1) + ": ");
            start = Math.abs(input.nextInt());
        } while (start >= size);

        do { // If the value is out of range, try again.
            System.out.print("Please choose a starting node from 0-" + (size - 1) + ": ");
            end = input.nextInt();
        } while (end >= size);

        startTime = System.currentTimeMillis();
        DFT(start, visited);
        endTime = System.currentTimeMillis();

        System.out.println("There are " + count + " paths from node " + start
                + " to node " + end + ".");
        System.out.println("It took " + (endTime - startTime) + " millisecond(s) to find them.");
    }

    private void getFile(String fileName) {
        try {
            Scanner input = new Scanner(new File(fileName));
            size = input.nextInt();

            adjMat = new boolean[size][size];

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    if (input.hasNextInt()) {
                        if (input.nextInt() == 0) { // Anything other than 0 is true.
                            adjMat[x][y] = false;
                        } else {
                            adjMat[x][y] = true;
                        }
                    }
                }
            }
            fileLoaded = true;
        } catch (FileNotFoundException e) {
            System.out.println("File not Found.");
            System.out.println(System.getProperty("user.dir"));
        }
    }

// Based on hhtp://csclab.murraystate.edu/bob.pilgrim/445/assignments/counting_paths.pdf
    private void DFT(int node, ArrayList<Integer> v) {
        v.add(node); // Add the node to the visited list.

        if (node == end) { // Is a path from start node to end node.
            count++;
            System.out.println(v);
            return;
        }

        for (int x = 0; x < size; x++) {
            if (!v.contains(x) && adjMat[node][x]) { // If a node has not been visited and is connected to the current node, travel to it.
                DFT(x, (ArrayList<Integer>) v.clone()); // Recursion.
            }
        }
    }
}
