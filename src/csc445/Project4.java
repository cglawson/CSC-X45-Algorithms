/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc445;

/**
 *
 * @author cglawson
 */
import java.util.ArrayList;

public class Project4 {

    static int count = 0;

    static final boolean t = true;
    static final boolean f = false;
    static final boolean[][] adjList = {
        {f, t, f, f, f},
        {f, f, t, t, f},
        {f, f, f, t, t},
        {f, t, t, f, f},
        {t, f, f, f, f}
    };

    static final int numNodes = adjList.length;

    static final int start = 2;
    static final int end = 2;

    ArrayList<Integer> visited = new ArrayList<>();

    public Project4() {
        DFT(start, visited);
        System.out.println("There are " + count + " paths from node " + start
        + " to node " + end + ".");
    }

    private void DFT(int node, ArrayList<Integer> v) {
        v.add(node);

        if (node == end) {
            count++;
            System.out.println(v);
            return;
        }
          
        for (int x = 0; x < numNodes; x++) {
            if (!v.contains(x) && adjList[node][x]) {
                DFT(x, (ArrayList<Integer>) v.clone());
            }
        }
    }
}
