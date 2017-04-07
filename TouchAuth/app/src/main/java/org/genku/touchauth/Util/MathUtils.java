package org.genku.touchauth.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by genku on 4/1/2017.
 */

public class MathUtils {

    public static int hexToDec(String hex) {
        int k = 1, ans = 0;
        for (int i = hex.length() - 1; i >= 0; --i) {
            ans += nums.get(hex.charAt(i)) * k;
            k *= 16;
        }
        return ans;
    }

    public static double displacement(double x1, double y1, double x2, double y2) {
        return norm2(x1 - x2, y1 - y2);
    }

    public static double norm2(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    public static double height(double x1, double y1, double x2, double y2, double x3, double y3) {
        return norm2((x2 - x3) * (y1 - y3), (x1 - x3) * (y2 - y3)) / norm2(x2 - x1, y2 - y1) / 2;
    }

    public static double angle(double x1, double y1, double x2, double y2) {
        return Math.atan((y2 - y1) / (x2 - x1));
    }

    public static double mean(double[] xs) {
        double sum = 0;
        for (double x : xs) {
            sum += x;
        }
        return sum / xs.length;
    }

    public static double variance(double[] xs) {
        double m = mean(xs);
        double sum = 0;
        for (double x : xs) {
            sum += (x - m) * (x - m);
        }
        return sum / xs.length;
    }

    private static final Map<Character, Integer> nums = new HashMap<Character, Integer>(){{
        put('0', 0);
        put('1', 1);
        put('2', 2);
        put('3', 3);
        put('4', 4);
        put('5', 5);
        put('6', 6);
        put('7', 7);
        put('8', 8);
        put('9', 9);
        put('a', 10);
        put('b', 11);
        put('c', 12);
        put('d', 13);
        put('e', 14);
        put('f', 15);
        put('A', 10);
        put('B', 11);
        put('C', 12);
        put('D', 13);
        put('E', 14);
        put('F', 15);
    }};
}
