package org.genku.touchauth.Util;

/**
 * Created by genku on 4/17/2017.
 */

public class DataUtils {

    public static double[][] cleanData(double[][] vectors) {
        int[] flags = new int[vectors.length];
        int cnt = 0, i = 0;
        for (double[] vector : vectors) {
            int flag = 1;
            for (double num : vector) {
                if (num == 0 || num != num) {
                    flag = 0;
                    break;
                }
            }
            flags[i] = flag;
            if (flag == 1) ++cnt;
            ++i;
        }
        double[][] ans = new double[cnt][vectors[0].length];
        for (int x = 0, y = 0; x < vectors.length; ++x) {
            if (flags[x] == 1) {
                ans[y++] = vectors[x];
            }
        }
        return ans;
    }
}
