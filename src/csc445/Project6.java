/*
 *   Caleb Lawson
 *   CSC 445
 *   Completed 3/31/2015
 *
 *   Project5 reads in and displays a file containing point coordinates, then draws
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

public class Project6 extends JFrame {

    ArrayList<Point> freeList = new ArrayList<>();
    ArrayList<Point> usedList = new ArrayList<>();

    // Create the window and display the frame.
    public Project6() {
        this.setSize(666, 666);
        this.setTitle("CSC 445 - Project6");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(new Surface(), BorderLayout.CENTER);
        this.setVisible(true);
    }

    // Read in points from file to memory.
    private void initPointList() {
        try {
            Scanner input = new Scanner(new File("challenge1.dat"));
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

    private ArrayList<ArrayList<Point>> grouperizer() {
        ArrayList<ArrayList<Point>> groups = new ArrayList<>();
        ArrayList<Double> distList = distanceList(usedList);
        double mean = mean(distList);
        double standardDeviation = standardDeviation(distList);

        int lastIndex = 0;

        for (int x = 0; x < usedList.size() - 1; x++) {
            if (distList.get(x) > mean + standardDeviation) {
                ArrayList<Point> group = new ArrayList<>();

                while (lastIndex < x) {
                    if (lastIndex < usedList.size()) {
                        group.add(usedList.get(lastIndex));
                    }
                    lastIndex++;
                }

                if (!group.isEmpty()) {
                    groups.add(group);
                }
            }
        }

        ArrayList<Point> group = new ArrayList<>();

        while (lastIndex < usedList.size()) {
            group.add(usedList.get(lastIndex));
            lastIndex++;
        }

        if (!group.isEmpty()) {
            groups.add(group);
        }

        System.out.print("num of groups " + groups.size());
        return groups;
    }

    private ArrayList<ArrayList<Point>> groupOptimizer(ArrayList<ArrayList<Point>> groups) {
        for (ArrayList<Point> l : groups) {
            boolean sbChanged;
            boolean dcChanged;

            do { // While a change is detected by either optimization algorithm, continue running.
                sbChanged = false;
                dcChanged = false;

                sbChanged = switchBack(l);
                dcChanged = decross(l);
            } while (sbChanged || dcChanged);
        }

        return groups;
    }

    private void groupReduce(ArrayList<ArrayList<Point>> groups) {
        usedList.clear();

        usedList.addAll(groups.get(0));
        groups.remove(0);

        while (!groups.isEmpty()) {
            double minDist = Double.MAX_VALUE;
            int insertIndex = 0;
            ArrayList<Point> group = groups.get(0);

            for (int x = 0; x < usedList.size(); x++) {

                if (x == 0) {
                    double dist = distance(group.get(group.size() - 1), usedList.get(x));
                    if (dist < minDist) {
                        minDist = dist;
                        insertIndex = x;

                    }
                }
                if (x < usedList.size() - 1) {
                    double dist = distance(group.get(0), usedList.get(x)) + distance(group.get(group.size() - 1), usedList.get(x + 1));

                    if (dist < minDist) {
                        minDist = dist;
                        insertIndex = x + 1;

                    }
                } else {
                    double dist = distance(group.get(0), usedList.get(x));
                    if (dist < minDist) {
                        minDist = dist;
                        insertIndex = x + 1;

                    }
                }
            }

            usedList.addAll(insertIndex, group);
            groups.remove(0);
            System.out.println();
        }

    }

    private void groupReduce2(ArrayList<ArrayList<Point>> groups) {
        usedList.clear();

        while (!groups.isEmpty()) {
            ArrayList<Point> group = groups.get(0);

            System.out.print(group.size() + " ");

            ArrayList<Point> groupPart1 = new ArrayList<>();
            ArrayList<Point> groupPart2 = new ArrayList<>();

            for (int x = 0; x < group.size(); x++) {
                if (x < (group.size() - 1) / 2) {
                    groupPart1.add(group.get(x));
                } else {
                    groupPart2.add(group.get(x));
                }
            }

            System.out.print(" " + (groupPart1.size() + groupPart2.size()) + " ");

            ArrayList<Point> merged = new ArrayList<>();

            for (int x = 0; x <= group.size() + 1; x++) {
                if (x == 0 || x % 2 == 0) {
                    if (!groupPart1.isEmpty()) {
                        merged.add(groupPart1.get(0));
                        groupPart1.remove(0);
                    }
                } else {
                    if (!groupPart2.isEmpty()) {
                        merged.add(groupPart2.get(0));
                        groupPart2.remove(0);
                    }
                }
            }

            System.out.println(merged.size());

            usedList.addAll(merged);
            groups.remove(0);
        }

    }

    private ArrayList<Double> distanceList(ArrayList<Point> pointList) {
        ArrayList<Double> distList = new ArrayList<>();

        for (int x = 0; x < pointList.size() - 1; x++) {
            distList.add(distance(pointList.get(x), pointList.get(x + 1)));
        }

        return distList;
    }

    private double mean(ArrayList<Double> distList) {
        double mean = 0.0;
        double sum = 0.0;

        for (double d : distList) {
            sum += d;
        }

        mean = sum / distList.size();

        return mean;
    }

    private double variance(ArrayList<Double> distList) {
        double variance = 0.0;
        double mean = mean(distList);

        for (double d : distList) {
            variance += Math.pow(d - mean, 2);
        }

        variance /= (distList.size() - 1);

        return variance;
    }

    private double standardDeviation(ArrayList<Double> distList) {
        double standardDeviation = 0.0;
        double variance = variance(distList);

        standardDeviation = Math.sqrt(variance);

        return standardDeviation;
    }

    // Java implementation of Dr. Pilgrim's switch_back procedure from his CSC 445 Lecture 02 slides.
    private boolean switchBack(ArrayList<Point> list) {
        boolean changed = false;

        for (int x = 0; x < list.size() - 3; x++) {
            if (distance(list.get(x), list.get(x + 1)) + distance(list.get(x + 2), list.get(x + 3))
                    > distance(list.get(x), list.get(x + 2)) + distance(list.get(x + 1), list.get(x + 3))) { // If shorter config found, swap.
                Collections.swap(list, x + 1, x + 2);
                changed = true;
            }
        }

        return changed;
    }

    // Java implementation of Bob Pilgrim's cross procedure from his CSC 445 Lecture 02 slides.
    private boolean decross(ArrayList<Point> list) {
        boolean changed = false;

        for (int x = 0; x < list.size() - 3; x++) {
            for (int y = x + 2; y < list.size() - 1; y++) {
                if (distance(list.get(x), list.get(x + 1)) + distance(list.get(y), list.get(y + 1))
                        > distance(list.get(x), list.get(y)) + distance(list.get(x + 1), list.get(y + 1))) {

                    java.util.List<Point> temp = list.subList(x + 1, y + 1); // Copy the sublist to be reversed.
                    Collections.reverse(temp);

                    for (int i = 0; i < temp.size(); i++) {  // Insert the reversed values into the array.
                        list.set(i + x + 1, temp.get(i));
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

            //randLocalIntelInsert();
            seqLocalIntelInsert();
            groupReduce2(grouperizer());

            endTime = System.currentTimeMillis();

            double distGreedyPath = pathLength(usedList);

            boolean sbChanged;
            boolean dcChanged;
            int timesOptimization = 0;
            do { // While a change is detected by either optimization algorithm, continue running.
                sbChanged = false;
                dcChanged = false;
                timesOptimization++;

                sbChanged = switchBack(usedList);
                dcChanged = decross(usedList);
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
            graphics.drawString("Insertion sort dist =>" + distGreedyPath, 0, 24);
            graphics.drawString("Dist after optimization =>" + distPostOptimization, 0, 36);
            graphics.drawString("Optimization loop iterations =>" + timesOptimization, 0, 48);

            outputPointList();
        }
    }
}
