/*
 *   Caleb Lawson
 *   CSC 445
 *   Completed 3/5/2015
 *
 *   Test1Problem2 reads in and displays a file containing point coordinates, then draws
 *   a path based on point proximity.
 */
package csc445;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class Project5 extends JFrame {

    ArrayList<Point> freeList = new ArrayList<>();
    ArrayList<Point> usedList = new ArrayList<>();

    // Create the window and display the frame.
    public Project5() {
        this.setSize(666, 666);
        this.setTitle("CSC 445 - Project5");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(new Surface(), BorderLayout.CENTER);
        this.setVisible(true);
    }

    // Read in points from file to memory.
    private void initPointList() {
        try {
            Scanner input = new Scanner(new File("output.dat"));
            int numRows = input.nextInt();

            for (int i = 0; i < numRows; i++) {
                //freeList.add(new PointList(new Point(input.nextInt(), input.nextInt()), null));
                freeList.add(new Point(input.nextInt(), input.nextInt()));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not Found.");
            System.out.print(System.getProperty("user.dir"));
        }

    }

    // Write results to file.
    private void outputPointList() {
        try {
            PrintStream out = new PrintStream(new FileOutputStream("output.dat"));

            out.println(usedList.size());

            for (Point p : usedList) {
                out.println((int) p.getX() + " " + (int) p.getY());
            }

            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not Found.");
            System.out.print(System.getProperty("user.dir"));
        }
    }

    private double pathLength(ArrayList<Point> a) {
        double sum = 0.0;
        for (int x = 0; x < a.size() - 1; x++) {
            sum += distance(a.get(x), a.get(x + 1));
        }
        return sum;
    }

    // Find the distance between two Point objects.
    private double distance(Point p, Point q) {
        return Math.sqrt((Math.pow(q.getX() - p.getX(), 2)) + Math.pow(q.getY() - p.getY(), 2));
    }

    // Find the point that is closest to all other points.
    private int intelligentStart() {
        double minDistance = Double.MAX_VALUE;
        int index = 0;
        int minDistanceIndex = 0;

        for (Point p : freeList) {
            double distSum = 0.0;

            for (Point q : freeList) {
                distSum += distance(p, q);
            }

            if (distSum < minDistance) {
                minDistance = distSum;
                minDistanceIndex = index;
            }
            index++;
        }
        return minDistanceIndex;
    }

    // Find the point that is furthest from all other points.
    private int intelligentStart2() {
        double maxDistance = Double.MIN_VALUE;
        int index = 0;
        int maxDistanceIndex = 0;

        for (Point p : freeList) {
            double distSum = 0.0;

            for (Point q : freeList) {
                distSum += distance(p, q);
            }

            if (distSum > maxDistance) {
                maxDistance = distSum;
                maxDistanceIndex = index;
            }
            index++;
        }
        return maxDistanceIndex;
    }

    // Greedy approach to forging a path with the nearest neighbor to a given point.
    private void nearestNeighbor() {
        //PointList current = freeList.get(0); // Start with the first point.
        Point current = freeList.get(intelligentStart()); // Start with the point that is closest to all other points.
        Point temp;

        while (freeList.size() > 1) {
            int index = 0;
            int minIndex = Integer.MAX_VALUE;
            double minDistance = Double.MAX_VALUE;

            for (Point p : freeList) {
                double dist = distance(p, current);

                if (dist != 0.0 && dist < minDistance) { // Can't choose self as closest.
                    minDistance = dist;
                    minIndex = index;
                }

                index++;
            }

            usedList.add(current); // Copy current to the usedList.
            temp = current; // Be sure that the index is not messed up for next assignment.
            current = freeList.get(minIndex); // New current is the closest point to the previous current.
            freeList.remove(temp); // Remove previous current from the freeList.
        }
        // Move the last point to the usedList.

        usedList.add(current);
        freeList.remove(current);
    }

    //Just a shuffle.
    private void randSelect() {
        Point current = freeList.get(0); // Start with the first point.
        int randomIndex;
        Point temp;

        while (freeList.size() > 1) {

            randomIndex = ThreadLocalRandom.current().nextInt(freeList.size());

            usedList.add(current); // Copy current to the usedList.
            temp = current; // Be sure that the index is not messed up for next assignment.
            current = freeList.get(randomIndex); // New current is the closest point to the previous current.
            freeList.remove(temp); // Remove previous current from the freeList.
        }
        // Move the last point to the usedList.     
        do {
            usedList.add(current);
            freeList.remove(current);
        } while (freeList.contains(current));
    }

    //Fast, interesting, slightly worse results that NN.
    private void randLocalIntelInsert() {
        usedList.add(freeList.get(ThreadLocalRandom.current().nextInt(freeList.size())));
        freeList.remove(usedList.get(0));

        while (!freeList.isEmpty()) {
            double minDist = Double.MAX_VALUE;
            int insertIndex = usedList.size();
            Point randPoint = freeList.get(ThreadLocalRandom.current().nextInt(freeList.size()));

            for (int x = 0; x < usedList.size(); x++) {

                if (x == 0) {
                    double dist = distance(randPoint, usedList.get(x));
                    if (dist < minDist) {
                        minDist = dist;
                        insertIndex = x;
                    }
                }
                if (x < usedList.size() - 1) {
                    double dist = distance(randPoint, usedList.get(x)) + distance(randPoint, usedList.get(x + 1));

                    if (dist <= minDist) {
                        minDist = dist;
                        insertIndex = x + 1;
                    }

                } else {
                    double dist = distance(randPoint, usedList.get(x));
                    if (dist <= minDist) {
                        minDist = dist;
                        insertIndex = x + 1;
                    }
                }
            }

            do {
                usedList.add(insertIndex, randPoint);
                freeList.remove(randPoint);
            } while (freeList.contains(randPoint));
        }

    }

//Fast and useful when repeated.
    private void seqLocalIntelInsert() {
        usedList.add(freeList.get(intelligentStart())); //Initialize insert list.
        freeList.remove(usedList.get(0));

        while (!freeList.isEmpty()) {
            double minDist = Double.MAX_VALUE;
            int insertIndex = usedList.size();
            Point randPoint = freeList.get(0);

            for (int x = 0; x < usedList.size(); x++) {

                if (x == 0) {
                    double dist = distance(randPoint, usedList.get(x));
                    if (dist < minDist) {
                        minDist = dist;
                        insertIndex = x;
                    }
                }
                if (x < usedList.size() - 1) {
                    double dist = distance(randPoint, usedList.get(x)) + distance(randPoint, usedList.get(x + 1));

                    if (dist < minDist) {
                        minDist = dist;
                        insertIndex = x + 1;
                    }
                } else {
                    double dist = distance(randPoint, usedList.get(x));
                    if (dist < minDist) {
                        minDist = dist;
                        insertIndex = x + 1;
                    }
                }
            }

            do {
                usedList.add(insertIndex, randPoint);
                freeList.remove(randPoint);
            } while (freeList.contains(randPoint));
        }

    }

    //Inefficient.
    private void randomSmaller() {
        Collections.sort(freeList, new Comparator<Point>() {

            public int compare(Point o1, Point o2) {
                return Double.compare(o1.getX(), o2.getX());
            }
        });
        usedList = freeList;

        long count = 10000000;

        while (count >= -10000000) {
            double dist = pathLength(usedList);

            int x = ThreadLocalRandom.current().nextInt(freeList.size());
            int y = ThreadLocalRandom.current().nextInt(freeList.size());

            Collections.swap(freeList, x, y);

            if (pathLength(freeList) < dist) {
                usedList = freeList;
                dist = pathLength(usedList);
                System.out.println(dist + " ");
            } else {
                Collections.swap(freeList, x, y);
            }

            count--;
            //System.out.println(count);
        }

    }

    // Java implementation of Dr. Pilgrim's switch_back procedure from his CSC 445 Lecture 02 slides.
    private boolean switchBack() {
        boolean changed = false;

        for (int x = 0; x < usedList.size() - 3; x++) {
            if (distance(usedList.get(x), usedList.get(x + 1)) + distance(usedList.get(x + 2), usedList.get(x + 3))
                    > distance(usedList.get(x), usedList.get(x + 2)) + distance(usedList.get(x + 1), usedList.get(x + 3))) { // If shorter config found, swap.
                Collections.swap(usedList, x + 1, x + 2);
                changed = true;
            }
        }

        return changed;
    }

    // Java implementation of Bob Pilgrim's cross procedure from his CSC 445 Lecture 02 slides.
    private boolean decross() {
        boolean changed = false;

        for (int x = 0; x < usedList.size() - 3; x++) {
            for (int y = x + 2; y < usedList.size() - 1; y++) {
                if (distance(usedList.get(x), usedList.get(x + 1)) + distance(usedList.get(y), usedList.get(y + 1))
                        > distance(usedList.get(x), usedList.get(y)) + distance(usedList.get(x + 1), usedList.get(y + 1))) {

                    java.util.List<Point> temp = usedList.subList(x + 1, y + 1); // Copy the sublist to be reversed.
                    Collections.reverse(temp);

                    for (int i = 0; i < temp.size(); i++) {  // Insert the reversed values into the array.
                        usedList.set(i + x + 1, temp.get(i));
                    }

                    changed = true;
                }
            }
        }

        return changed;
    }

    private class Surface extends JComponent {

        @Override
        public void paint(Graphics g) {
            double pointShiftX = 0.0;
            double pointShiftY = 36.0;

            // Time the path-finding algorithm.
            long startTime;
            long endTime;

            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            initPointList();

            // Draw Points.
            graphics.setPaint(Color.RED);
            Shape s;
            for (Point p : freeList) {
                s = new Ellipse2D.Double(p.getX() + pointShiftX - 2, p.getY() + pointShiftY - 2, 4, 4);
                graphics.fill(s);
            }

            startTime = System.currentTimeMillis();

            //nearestNeighbor();
            //randSelect();
            //randLocalIntelInsert();
            seqLocalIntelInsert();
            //usedList = freeList;
            //sequentialSmaller();
            //randomSmaller();
            
            double distGreedyPath = pathLength(usedList);

            endTime = System.currentTimeMillis();

            boolean sbChanged;
            boolean dcChanged;
            int timesOptimization = 0;
            do { // While a change is detected by either optimization algorithm, continue running.
                sbChanged = false;
                dcChanged = false;
                timesOptimization++;

                sbChanged = switchBack();
                dcChanged = decross();
            } while (sbChanged || dcChanged);
            double distPostOptimization = pathLength(usedList);

            //Draw Lines
            graphics.setPaint(Color.BLACK);
            for (int x = 0; x < usedList.size() - 1; x++) {
                s = new Line2D.Double(usedList.get(x).getX() + pointShiftX, usedList.get(x).getY() + pointShiftY, usedList.get(x + 1).getX() + pointShiftX, usedList.get(x + 1).getY() + pointShiftY);
                graphics.draw(s);
            }

            // Display run time of algorithm.
            graphics.setPaint(Color.BLACK);
            graphics.setFont(new Font("Monospace", Font.PLAIN, 12));
            graphics.drawString("Run Time: " + (endTime - startTime) + " ms", 0, 12);
            graphics.drawString("Nearest neighbor dist =>" + distGreedyPath, 0, 24);
            graphics.drawString("Dist after optimization =>" + distPostOptimization, 0, 36);
            graphics.drawString("Optimization loop iterations =>" + timesOptimization, 0, 48);

            outputPointList();
        }
    }
}
