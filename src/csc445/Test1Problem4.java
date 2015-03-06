/*
 *   Caleb Lawson
 *   CSC 445
 *   Completed 3/6/2015
 *
 *   Test1Problem4 finds the minial distance cycle of 10 cities out of 16 using brute force permutations and calculations.
 */
package csc445;

public class Test1Problem4 {

    static long count = 0;
    static long numPerms;
    static String shortestPath;
    static int shortestDistance = Integer.MAX_VALUE;

    static final int[][] distMatrix
            = {{-1, 1075, 716, 792, 1425, 1369, 740, 802, 531, 383, 811, 2211, 661, 870, 999, 772},
            {1075, -1, 1015, 1770, 2403, 1662, 870, 1858, 941, 1426, 1437, 3026, 1486, 211, 1463, 314},
            {716, 1015, -1, 928, 1483, 646, 390, 1085, 185, 749, 530, 2034, 1377, 821, 471, 772},
            {792, 1770, 928, -1, 633, 1089, 1111, 246, 908, 409, 495, 1447, 1317, 1565, 672, 1470},
            {1425, 2403, 1483, 633, -1, 9999, 1630, 752, 1432, 9999, 931, 814, 1938, 2198, 1016, 2103},
            {1369, 1662, 646, 1089, 9999, -1, 820, 1335, 832, 9999, 605, 1839, 2030, 1468, 421, 1419},
            {740, 870, 390, 1111, 1630, 820, -1, 1224, 360, 965, 690, 2197, 1480, 750, 630, 705},
            {802, 1858, 1085, 246, 752, 1335, 1224, -1, 1021, 442, 737, 1566, 1190, 1653, 918, 1558},
            {531, 941, 185, 908, 1432, 832, 360, 1021, -1, 685, 496, 2088, 1192, 736, 616, 656},
            {383, 1426, 749, 409, 9999, 9999, 965, 442, 685, -1, 738, 1858, 1938, 1221, 926, 1126},
            {811, 1437, 530, 495, 931, 605, 690, 737, 496, 738, -1, 1631, 1472, 1232, 188, 1152},
            {2211, 3026, 2034, 1447, 814, 1839, 2197, 1566, 2088, 1858, 1631, -1, 2752, 2824, 1563, 2744},
            {661, 1486, 1377, 1317, 1938, 2030, 1480, 1190, 1192, 1938, 1472, 2752, -1, 1281, 1660, 1183},
            {870, 211, 821, 1565, 2198, 1468, 750, 1653, 736, 1221, 1232, 2824, 1281, -1, 1269, 109},
            {999, 1463, 471, 672, 1016, 421, 630, 918, 616, 926, 188, 1563, 1660, 1269, -1, 1220},
            {772, 314, 772, 1470, 2103, 1419, 705, 1558, 656, 1126, 1152, 2744, 1183, 109, 1220, -1}};

    private static void permuteK(char[] a, int n, int r) {     // From Princeton.edu
        if (r == 0) {
            count++;
            String path = "";
            int dist = 0;

            for (int i = n; i < a.length; i++) {
                if (i < a.length - 1) {
                    if (distMatrix[charToIndex(a[i])][charToIndex(a[i + 1])] > 0) {
                        dist += distMatrix[charToIndex(a[i])][charToIndex(a[i + 1])];
                    }
                    //System.out.print(""+distMatrix[charToIndex(a[i])][charToIndex(a[i + 1])] + " + "); // For Debugging.
                }
                path += a[i];
            }

            dist += distMatrix[charToIndex(path.charAt(0))][charToIndex(path.charAt(path.length() - 1))];
            //System.out.print(distMatrix[charToIndex(path.charAt(0))][charToIndex(path.charAt(path.length()-1))] + " = "); // For Debugging.

            if (dist < shortestDistance) {
                shortestDistance = dist;
                shortestPath = path;

                System.out.print("#" + count + " " + path + " Dist: " + dist + " <= NEW SHORTEST PATH \n");
            } else {
                //System.out.print(path + " " + dist + "\n"); // Kills CPU.
                if (count % 10000000 == 0) {
                    System.out.printf("%.2f%s", ((double) count / numPerms) * 100.0, "% done.\n");
                }
            }
            return;
        }
        for (int i = 0; i < n; i++) {
            swap(a, i, n - 1);
            permuteK(a, n - 1, r - 1);
            swap(a, i, n - 1);
        }
    }

    // helper function that swaps a[i] and a[j]
    public static void swap(char[] a, int i, int j) {     // From Princeton.edu
        char temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public static long factorial(int n) {
        long fact = 1; // this  will be the result
        for (long i = 1; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }

    static public int charToIndex(char c) {
        int index = -1;
        switch (c) {
            case 'A':
                index = 0;
                break;
            case 'B':
                index = 1;
                break;
            case 'C':
                index = 2;
                break;
            case 'D':
                index = 3;
                break;
            case 'E':
                index = 4;
                break;
            case 'F':
                index = 5;
                break;
            case 'G':
                index = 6;
                break;
            case 'H':
                index = 7;
                break;
            case 'I':
                index = 8;
                break;
            case 'J':
                index = 9;
                break;
            case 'K':
                index = 10;
                break;
            case 'L':
                index = 11;
                break;
            case 'M':
                index = 12;
                break;
            case 'N':
                index = 13;
                break;
            case 'O':
                index = 14;
                break;
            case 'P':
                index = 15;
                break;
            default:
                index = -1;
                break;
        }
        return index;
    }

    public Test1Problem4() {
        long startTime;
        long endTime;
        long elapsedTime;

        long secondInMillis = 1000;
        long minuteInMillis = secondInMillis * 60;
        long hourInMillis = minuteInMillis * 60;

        String cities = "ABCDEFGHIJKLMNOP";
        char[] c = cities.toCharArray();

        numPerms = factorial(c.length) / factorial(c.length - 10);
        System.out.println("There are " + numPerms + " permutations.");

        startTime = System.currentTimeMillis();
        permuteK(c, c.length, 10);
        endTime = System.currentTimeMillis();

        elapsedTime = endTime - startTime;

        long elapsedHours = elapsedTime / hourInMillis;
        elapsedTime -= elapsedHours * hourInMillis;
        long elapsedMinutes = elapsedTime / minuteInMillis;

        System.out.println("\nThe shortest path is " + shortestPath + ". Distance: " + shortestDistance);
        System.out.println("It took " + elapsedHours + " hours and " + elapsedMinutes + " minutes to find the solution.");
    }
}
