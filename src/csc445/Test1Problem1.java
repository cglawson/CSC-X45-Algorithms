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

public class Test1Problem1 extends JFrame {

    ArrayList<PointList> freeList = new ArrayList<>();
    ArrayList<PointList> usedList = new ArrayList<>();

    // Create the window and display the frame.
    public Test1Problem1() {
        this.setSize(666, 666);
        this.setTitle("CSC 445 - Test1Problem1");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(new Surface(), BorderLayout.CENTER);
        this.setVisible(true);
    }

    // Read in points from file to memory.
    private void initPointList() {
        try {
            Scanner input = new Scanner(new File("challenge4.dat"));
            int numRows = input.nextInt();

            for (int i = 0; i < numRows; i++) {
                freeList.add(new PointList(new Point(input.nextInt(), input.nextInt()), null));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not Found.");
            System.out.print(System.getProperty("user.dir"));
        }

    }

    // Write results to file.
    private void outputPointList() {
        try {
            PrintStream out = new PrintStream(new FileOutputStream("challenge4_lawson_output_max.dat"));

            out.println(usedList.size());

            for (PointList p : usedList) {
                out.println((int) p.getX() + " " + (int) p.getY());
            }

            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not Found.");
            System.out.print(System.getProperty("user.dir"));
        }
    }

    private double pathLength(ArrayList<PointList> a) {
        double sum = 0.0;
        for (int x = 0; x < a.size() - 1; x++) {
            sum += distance(a.get(x).getPoint(), a.get(x + 1).getPoint());
        }
        return sum;
    }

    // Find the distance between two Point objects.
    private double distance(Point p, Point q) {
        return Math.sqrt((Math.pow(q.getX() - p.getX(), 2)) + Math.pow(q.getY() - p.getY(), 2));
    }

    // Populate the distance list of PointList objects.
    private void popDistList() {
        for (PointList p : freeList) {
            ArrayList<PointDistance> row = new ArrayList<>();
            for (PointList q : freeList) {
                row.add(new PointDistance(q.getPoint(), distance(p.getPoint(), q.getPoint())));
            }
            p.setArrayList(row);
        }
    }

    // Find the point that is closest to all other points.
    private int intelligentStart() {
        double minDistance = Double.MAX_VALUE;
        int index = 0;
        int minDistanceIndex = 0;

        for (PointList p : freeList) {
            double distSum = 0.0;
            
            for (PointList q : freeList) {
               distSum += distance(p.getPoint(),q.getPoint());
            }
            
            if(distSum < minDistance){
                minDistance = distSum;
                minDistanceIndex = index;
            }
            index++;
        }
        return minDistanceIndex;
    }

    // Greedy approach to forging a path with the nearest neighbor to a given point.
    private void nearestNeighbor() {
        //PointList current = freeList.get(0); // Start with the first point.
        PointList current = freeList.get(intelligentStart()); // Start with the point that is closest to all other points.
        PointList temp;

        while (freeList.size() > 1) {
            int index = 0;
            int minIndex = Integer.MAX_VALUE;
            double minDistance = Double.MAX_VALUE;

            for (PointList p : freeList) {
                double dist = distance(p.getPoint(), current.getPoint());

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

    // Java implementation of Dr. Pilgrim's switch_back procedure from his CSC 445 Lecture 02 slides.
    private boolean switchBack() {
        boolean changed = false;

        for (int x = 0; x < usedList.size() - 3; x++) {
            if (distance(usedList.get(x).getPoint(), usedList.get(x + 1).getPoint()) + distance(usedList.get(x + 2).getPoint(), usedList.get(x + 3).getPoint())
                    > distance(usedList.get(x).getPoint(), usedList.get(x + 2).getPoint()) + distance(usedList.get(x + 1).getPoint(), usedList.get(x + 3).getPoint())) { // If shorter config found, swap.
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
                if (distance(usedList.get(x).getPoint(), usedList.get(x + 1).getPoint()) + distance(usedList.get(y).getPoint(), usedList.get(y + 1).getPoint())
                        > distance(usedList.get(x).getPoint(), usedList.get(y).getPoint()) + distance(usedList.get(x + 1).getPoint(), usedList.get(y + 1).getPoint())) {

                    java.util.List<PointList> temp = usedList.subList(x + 1, y + 1); // Copy the sublist to be reversed.
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

    // This class holds a point and its distance in relation to another point.
    private class PointDistance {

        Point point;
        Double distance;

        PointDistance(Point p, Double d) {
            point = p;
            distance = d;
        }

        public Point getPoint() {
            return point;
        }

        public double getDistance() {
            return distance;
        }
    }

    // This class holds a point and a list of distances to all other points.
    private class PointList {

        Point point;
        ArrayList<PointDistance> row = new ArrayList<>();

        PointList(Point p, ArrayList<PointDistance> a) {
            point = p;
            row = a;
        }

        public Point getPoint() {
            return point;
        }

        public double getX() {
            return point.getX();
        }

        public double getY() {
            return point.getY();
        }

        public ArrayList<PointDistance> getArrayList() {
            return row;
        }

        public void setArrayList(ArrayList<PointDistance> a) {
            row = a;
        }
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
            for (PointList p : freeList) {
                s = new Ellipse2D.Double(p.getX() + pointShiftX - 2, p.getY() + pointShiftY - 2, 4, 4);
                graphics.fill(s);
            }

            popDistList();

            startTime = System.currentTimeMillis();

            nearestNeighbor();
            double distGreedyPath = pathLength(usedList);

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

            endTime = System.currentTimeMillis();

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
