/*
 *   Caleb Lawson
 *   CSC 445
 *   Completed 1/17/2015
 *
 *   Project1 reads in and displays a file containing point coordinates, then draws
 *   a path based on point proximity.
 */
package csc445;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

public class Project1 extends JFrame {

    ArrayList<PointList> freeList = new ArrayList<>();
    ArrayList<PointList> usedList = new ArrayList<>();

    // Time the path-finding algorithm.
    long startTime;
    long endTime;

    int runCount;

    // Create the window and display the frame.
    public Project1() {
        this.setSize(666, 524);
        this.setTitle("CSC 445 - Project1");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(new Surface(), BorderLayout.CENTER);
        this.setVisible(true);
    }

    // Read in points from file to memory.
    private void initPointList() {
        try {
            Scanner input = new Scanner(new File("rtest1.dat"));
            int numRows = input.nextInt();

            for (int i = 0; i < numRows; i++) {
                freeList.add(new PointList(new Point(input.nextInt(), input.nextInt()), null));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not Found.");
            System.out.print(System.getProperty("user.dir"));
        }

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

    // Greedy, approach to forging a path with the nearest neighbor to a given point.
    private void nearestNeighbor() {
        startTime = System.currentTimeMillis();

        PointList current = freeList.get(0);
        PointList temp;

        while (freeList.size() > 1) {
            int index = 0;
            int minIndex = Integer.MAX_VALUE;
            double minDistance = Double.MAX_VALUE;

            for (PointList p : freeList) {
                double dist = p.findDistanceTo(current.getPoint());

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

        endTime = System.currentTimeMillis();
    }

    // Java implementation of Dr. Pilgrim's switch_back procedure from his CSC 445 Lecture 02 slides.
    private void switchBack() {

        boolean changed;

        do {
            changed = false;
            for (int x = 0; x < usedList.size() - 3; x++) {
                if (usedList.get(x).findDistanceTo(usedList.get(x + 1).getPoint()) + usedList.get(x + 2).findDistanceTo(usedList.get(x + 3).getPoint())
                        > usedList.get(x).findDistanceTo(usedList.get(x + 2).getPoint()) + usedList.get(x + 1).findDistanceTo(usedList.get(x + 3).getPoint())) {
                    Collections.swap(usedList, x + 1, x + 2);
                    changed = true;
                }
            }
            runCount++;
        } while (changed);
    }

    // Java implementation of Bob Pilgrim's cross procedure from his CSC 445 Lecture 02 slides.
    private void decross() {

        boolean changed;
        int times = 0;

        do {
            times++;
            changed = false;
            for (int x = 0; x < usedList.size() - 3; x++) {
                for (int y = x + 2; y < usedList.size() - 1; y++) {
                    if (usedList.get(x).findDistanceTo(usedList.get(x + 1).getPoint()) + usedList.get(y).findDistanceTo(usedList.get(y + 1).getPoint())
                            > usedList.get(x).findDistanceTo(usedList.get(y).getPoint()) + usedList.get(x + 1).findDistanceTo(usedList.get(y + 1).getPoint())) {

                        java.util.List<PointList> temp = usedList.subList(x + 1, y + 1);
                        Collections.reverse(temp);
                        
                        for(int i = 0; i < temp.size(); i++){
                            usedList.set(i + x + 1, temp.get(i));
                        }

                        changed = true;
                    }
                }
            }
            System.out.print(times);
        } while (changed);
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

        public double findDistanceTo(Point p) {
            for (PointDistance pd : row) {
                if (p == pd.getPoint()) {
                    return pd.getDistance();
                }
            }
            return 0.0;
        }
    }

    private class Surface extends JComponent {

        @Override
        public void paint(Graphics g) {
            double pointShift = 10.0;

            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            initPointList();

            // Draw Points.
            graphics.setPaint(Color.RED);
            Shape s;
            for (PointList p : freeList) {
                s = new Ellipse2D.Double(p.getX() + pointShift, p.getY() + pointShift, 3, 3);
                graphics.fill(s);
            }

            popDistList();
            nearestNeighbor();
            //usedList=freeList;

            // Draw Lines.
//            graphics.setPaint(Color.BLACK);
//            for (int x = 0; x < usedList.size() - 1; x++) {
//                s = new Line2D.Double(usedList.get(x).getX() + pointShift, usedList.get(x).getY() + pointShift, usedList.get(x + 1).getX() + pointShift, usedList.get(x + 1).getY() + pointShift);
//                graphics.draw(s);
//            }
            
            double sum = 0.0;
            for(int x = 0; x < usedList.size() - 1; x++){
                sum += usedList.get(x).findDistanceTo(usedList.get(x+1).getPoint());
            }

            switchBack();

//            graphics.setPaint(Color.GREEN);
//            for (int x = 0; x < usedList.size() - 1; x++) {
//                s = new Line2D.Double(usedList.get(x).getX() + pointShift, usedList.get(x).getY() + pointShift, usedList.get(x + 1).getX() + pointShift, usedList.get(x + 1).getY() + pointShift);
//                graphics.draw(s);
//            }
            
            double sum2 = 0.0;
            for(int x = 0; x < usedList.size() - 1; x++){
                sum2 += usedList.get(x).findDistanceTo(usedList.get(x+1).getPoint());
            }

            decross();

            graphics.setPaint(Color.ORANGE);
            for (int x = 0; x < usedList.size() - 1; x++) {
                s = new Line2D.Double(usedList.get(x).getX() + pointShift, usedList.get(x).getY() + pointShift, usedList.get(x + 1).getX() + pointShift, usedList.get(x + 1).getY() + pointShift);
                graphics.draw(s);
            }
            
            double sum3 = 0.0;
            for(int x = 0; x < usedList.size() - 1; x++){
                sum3 += usedList.get(x).findDistanceTo(usedList.get(x+1).getPoint());
            }

            // Display run time of algorithm.
            graphics.setPaint(Color.BLACK);
            graphics.setFont(new Font("Monospace", Font.PLAIN, 12));
            graphics.drawString("NN Run Time: " + (endTime - startTime) + " ms", 0, 12);
            graphics.drawString("Distance 1 =>" + sum, 0, 24);
            graphics.drawString("Distance 2 =>" + sum2, 0, 36);
            graphics.drawString("Distance 3 =>" + sum3, 0, 48);
        }
    }
}
