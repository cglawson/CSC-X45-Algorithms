/*
 *   Caleb Lawson
 *   CSC 445
 *   Completed 3/4/2015
 *
 *   Test1Problem2 computes the convex of a set of points with the QuickHull algorithm repeatedly and displays the result.
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

public class Test1Problem2 extends JFrame {

    ArrayList<PointList> freeList = new ArrayList<>();
    ArrayList<PointList> usedList = new ArrayList<>();

    // Create the window and display the frame.
    public Test1Problem2() {
        this.setSize(666, 572);
        this.setTitle("CSC 445 - Test1Problem2");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(new Surface(), BorderLayout.CENTER);
        this.setVisible(true);
    }

    // Read in points from file to memory.
    private void initPointList() {
        try {
            Scanner input = new Scanner(new File("rtest3.dat"));
            int numRows = input.nextInt();

            for (int i = 0; i < numRows; i++) {
                freeList.add(new PointList(new Point(input.nextInt(), input.nextInt()), null));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not Found.");
            System.out.print(System.getProperty("user.dir"));
        }

    }

    private double pathLength(ArrayList<PointList> a) {
        double sum = 0.0;
        for (int x = 0; x < a.size() - 1; x++) {
            sum += a.get(x).findDistanceTo(a.get(x + 1).getPoint());
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

    // Determine what side a given point is along a line segment.
    // False = left, True = right.
    private boolean pointSide(PointList a, PointList b, PointList p) {
        return (((b.getX() - a.getX()) * (p.getY() - a.getY()) - (b.getY() - a.getY()) * (p.getX() - a.getX())) > 0);
    }

    // Determine how far away a point p is from line segment ab.
    private double lineDistance(PointList a, PointList b, PointList p) {
        double abx = b.getX() - a.getX();
        double aby = b.getY() - a.getY();

        double result = ((abx * (a.getY() - p.getY())) - (aby * (a.getX() - p.getX())));
        if (result < 0) {
            result -= result;
        }
        return result;
    }

    // Find the hull of freeList;
    public void quickHull() {
        usedList.clear();

        double minX = Double.MAX_VALUE;
        int minXIndex = -1;
        double maxX = Double.MIN_VALUE;
        int maxXIndex = -1;

        for (PointList p : freeList) {
            // Find point with smallest X.
            if (p.getX() < minX) {
                minX = p.getX();
                minXIndex = freeList.indexOf(p);
            }
            //Find point with largest X.
            if (p.getX() > maxX) {
                maxX = p.getX();
                maxXIndex = freeList.indexOf(p);
            }
        }

        PointList a = freeList.get(minXIndex);
        PointList b = freeList.get(maxXIndex);

        usedList.add(a);
        usedList.add(b);
        freeList.remove(a);
        freeList.remove(b);

        ArrayList<PointList> left = new ArrayList<>();
        ArrayList<PointList> right = new ArrayList<>();

        // Fill sets with points either left or right of line segment ab.
        for (PointList p : freeList) {
            if (pointSide(a, b, p)) {
                right.add(p);
            } else {
                left.add(p);
            }
        }

        // Recurse over the left and right sets.
        findHull(b, a, right);
        findHull(a, b, left);
        usedList.add(usedList.get(0)); // This is to connect the last point to the first point on the hull.

    }

    // Find points on the convex hull on a  set of points.
    private void findHull(PointList a, PointList b, ArrayList<PointList> set) {
        int insertIndex = usedList.indexOf(b);

        if (set.isEmpty()) { // Termination case.
            //usedList.add(usedList.get(0)); // This is to connect the last point to the first point on the hull.
            //System.out.print("touch");
            return;
        }
        if (set.size() == 1) { // If there's only one point left in the set, insert it where it belongs and return.
            PointList p = set.get(0);
            set.remove(p);
            freeList.remove(p);
            usedList.add(insertIndex, p);
            return;
        }

        // Find the farthest point from the line segment ab.
        double greatestDist = Double.MIN_VALUE;
        int greatestDistIndex = -1;

        for (PointList p : set) {
            double dist = lineDistance(a, b, p);
            if (dist > greatestDist) {
                greatestDist = dist;
                greatestDistIndex = set.indexOf(p);
            }
        }

        if (greatestDistIndex > -1) {

            // Add the farthest point from line segment ab to create triangle abc.
            PointList c = set.get(greatestDistIndex);
            set.remove(greatestDistIndex);
            freeList.remove(c);
            usedList.add(insertIndex, c);

            // Who is left of line segment ac?
            ArrayList<PointList> leftAC = new ArrayList<>();
            for (PointList p : set) {
                if (!pointSide(a, c, p)) {
                    leftAC.add(p);
                }
            }

            // Who is left of line segment cb?
            ArrayList<PointList> leftCB = new ArrayList<>();
            for (PointList p : set) {
                if (!pointSide(c, b, p)) {
                    leftCB.add(p);
                }
            }

            // Points not left of line segments ac or cb can be ignored.
            // Recurse over the points left of line segments ac or cb.
            findHull(a, c, leftAC);
            findHull(c, b, leftCB);
        }
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

        // Linear search, HUGE performance hog.
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
            double pointShiftX = 10.0;
            double pointShiftY = 48.0;

            // Time the path-finding algorithm.
            long startTime;
            long endTime;

            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            initPointList();

            // Draw Points.
            graphics.setPaint(Color.BLACK);
            Shape s;
            for (PointList p : freeList) {
                s = new Ellipse2D.Double(p.getX() + pointShiftX - 2, p.getY() + pointShiftY - 2, 4, 4);
                graphics.fill(s);
            }

            popDistList();

            try {
                PrintStream out = new PrintStream(new FileOutputStream("output.dat"));

                startTime = System.currentTimeMillis();
                int count = 0;
                
                out.println("Outermost hulls to innermost hulls, seperated by newlines.\n");
                
                while (freeList.size() > 2) {

                    //Find the hull.
                    quickHull();

                    //Draw Lines.
                    if (count % 2 == 0) {
                        graphics.setPaint(Color.MAGENTA);
                    } else {
                        graphics.setPaint(Color.GREEN);

                    }

                    for (int x = 0; x < usedList.size() - 1; x++) {
                        s = new Line2D.Double(usedList.get(x).getX() + pointShiftX, usedList.get(x).getY() + pointShiftY, usedList.get(x + 1).getX() + pointShiftX, usedList.get(x + 1).getY() + pointShiftY);
                        out.print("(" + (int) usedList.get(x).getX() + ", " + (int) usedList.get(x).getY() + ") ");
                        graphics.draw(s);
                    }

                    out.println("\n");
                    s = new Line2D.Double(usedList.get(0).getX() + pointShiftX, usedList.get(0).getY() + pointShiftY, usedList.get(usedList.size() - 1).getX() + pointShiftX, usedList.get(usedList.size() - 1).getY() + pointShiftY);
                    graphics.draw(s);

                    count++;
                }
                endTime = System.currentTimeMillis();

                if (!freeList.isEmpty()) {
                    out.println("\nPoints not belonging to a hull: ");
                    for (PointList p : freeList) {
                        out.print("(" + (int) p.getX() + ", " + (int) p.getY() + ") ");
                    }
                }

                // Display run time of algorithm.
                graphics.setPaint(Color.BLACK);
                graphics.setFont(new Font("Monospace", Font.PLAIN, 12));
                graphics.drawString("Run Time: " + (endTime - startTime) + " ms", 0, 12);

                out.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not Found.");
                System.out.print(System.getProperty("user.dir"));
            }
        }
    }
}
