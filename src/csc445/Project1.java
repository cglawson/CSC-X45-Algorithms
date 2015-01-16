/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc445;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Project1 extends JFrame {

    ArrayList<Point> pointList = new ArrayList<Point>();
    ArrayList<ArrayList> distList = new ArrayList<ArrayList>();

    public Project1() {
        this.setSize(666, 524);
        this.setTitle("CSC 445 - Project1");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(new Surface(), BorderLayout.CENTER);
        this.setVisible(true);
    }

    private void initPointList() {
        try {
            Scanner input = new Scanner(new File("rtest3.dat"));
            int numRows = input.nextInt();

            for (int i = 0; i < numRows; i++) {
                pointList.add(new Point(input.nextInt(), input.nextInt()));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not Found.");
            System.out.print(System.getProperty("user.dir"));
        }

    }

    private double distance(double x1, double x2, double y1, double y2) {
        return Math.sqrt((Math.pow(x2 - x1, 2)) + Math.pow(y2 - y1, 2));
    }

    private void popDistTable() {
        for (Point p : pointList) {
            ArrayList<Double> row = new ArrayList<Double>();
            for (Point q : pointList) {
                row.add(distance(p.getX(), q.getX(), p.getY(), q.getY()));
            }
            distList.add(row);
        }
    }

    private class Surface extends JComponent {

        public void paint(Graphics g) {
            Graphics2D graphics = (Graphics2D) g;

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            initPointList();

            Shape s;
            for (Point p : pointList) {
                s = new Ellipse2D.Double(p.getX() + 10, p.getY() + 10, 3, 3);
                graphics.fill(s);
            }

            popDistTable();
        }
    }
}
