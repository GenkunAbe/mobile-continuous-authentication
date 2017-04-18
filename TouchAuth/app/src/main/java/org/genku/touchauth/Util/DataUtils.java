package org.genku.touchauth.Util;

import java.util.Arrays;

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

    public static double[][] scaleData(double[][] vectors, String coefsFilename, boolean isRestore) {
        if (vectors == null || vectors.length == 0) {
            return vectors;
        }

        final double lower = -1;
        final double upper = 1;

        int n = vectors.length;
        int m = vectors[0].length;
        double[][] values;

        if (isRestore) {
            values = TextFile.readFileToMatrix(coefsFilename);
            if (values[0].length != m) {
                return null;
            }
        }
        else {
            values = new double[n][m];
            Arrays.fill(values[0], -Double.MAX_VALUE);
            Arrays.fill(values[1], Double.MAX_VALUE);
            for (double[] vector : vectors) {
                for (int i = 0; i < vector.length; ++i) {
                    if (values[0][i] < vector[i]) {
                        values[0][i] = vector[i];
                    }
                    if (values[1][i] > vector[i]) {
                        values[1][i] = vector[i];
                    }
                }
            }
            TextFile.writeFileFromNums(coefsFilename, values[0], false, false, 0);
            TextFile.writeFileFromNums(coefsFilename, values[1], true, false, 0);
        }

        double[][] ans = new double[n][m];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                double max = values[0][j];
                double min = values[1][j];
                ans[i][j] = lower + (vectors[i][j] - min) / (max - min) * (upper - lower);
            }
        }

        return ans;
    }

}
