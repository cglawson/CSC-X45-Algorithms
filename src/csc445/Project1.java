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

    public Project1() {
        this.setSize(660, 510);
        this.setTitle("CSC 445 - Project1");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(new Surface(), BorderLayout.CENTER);
        this.setVisible(true);
    }

    private void initList() {
        try {
            Scanner input = new Scanner(new File("rtest1.dat"));
            int numRows = input.nextInt();

            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;

            for (int i = 0; i < numRows; i++) {
                int x = input.nextInt();
                int y = input.nextInt();

                pointList.add(new Point(x, y));

                if (x > maxX) {
                    maxX = x;
                }
                if (x > maxY) {
                    maxY = y;
                }

            }

            System.out.print("Max X: " + maxX);
            System.out.print("Max Y: " + maxY);

            
        } catch (FileNotFoundException e) {
            System.out.println("File not Found.");
            System.out.print(System.getProperty("user.dir"));
        }

    }

    private class Surface extends JComponent {

        public void paint(Graphics g) {
            Graphics2D graphics = (Graphics2D) g;

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            initList();

            Shape s;
            for (Point p : pointList) {
                s = new Ellipse2D.Double(p.getX(), p.getY(), 3, 3);
                graphics.fill(s);
            }
        }
    }
}
