package org.genku.touchauth.Model;

import org.genku.touchauth.Util.DataUtils;

import java.util.List;

/**
 * Created by genku on 4/22/2017.
 */

public class SensorFeatureExtraction {


    private static int VECTORS_PER_SECOND = 50;
    private static int VECTORS_PER_WINDOW = 100;
    private static double STEP_OFFSET = 0.5;


    public static double[][] extract(double totalTime, double windowTime,
                                     List<List<Double>> accelerometer,
                                     List<List<Double>> orientation,
                                     List<List<Double>> magnetic,
                                     List<List<Double>> gyroscope) {
        return extract(totalTime, windowTime,
                DataUtils.listToArray(accelerometer),
                DataUtils.listToArray(orientation),
                DataUtils.listToArray(magnetic),
                DataUtils.listToArray(gyroscope));
    }

    public static double[][] extract(double totalTime, double windowTime,
                                     double[][] accelerometer,
                                     double[][] orientation,
                                     double[][] magnetic,
                                     double[][] gyroscope) {
        SensorWindow[] windows = getWindows(totalTime,
                windowTime, accelerometer, orientation, magnetic,gyroscope);
        return getAllFeatureVectors(windows);
    }

    private static SensorWindow[] getWindows(double totalTime,
                                     double windowTime,
                                     double[][] accelerometer,
                                     double[][] orientation,
                                     double[][] magnetic,
                                     double[][] gyroscope) {
        VECTORS_PER_WINDOW = (int) (VECTORS_PER_SECOND * windowTime);
        int k = (int) ((totalTime * VECTORS_PER_SECOND - STEP_OFFSET *
                VECTORS_PER_WINDOW) / ((1 - STEP_OFFSET) * VECTORS_PER_WINDOW));
        SensorWindow[] windows = new SensorWindow[k];
        int p = 0;
        for (int i = 0; i < k; ++i) {
            double[][] tempAcc = new double[4][VECTORS_PER_WINDOW];
            double[][] tempOri = new double[4][VECTORS_PER_WINDOW];
            double[][] tempMag = new double[4][VECTORS_PER_WINDOW];
            double[][] tempGyr = new double[4][VECTORS_PER_WINDOW];
            for(int j = 0; j < VECTORS_PER_WINDOW; ++j) {
                for (int m = 0; m < 4; ++m) {
                    if (p >= accelerometer[m].length) {
                        break;
                    } else {
                        tempAcc[m][j] = accelerometer[m][p];
                        tempOri[m][j] = orientation[m][p];
                        tempMag[m][j] = magnetic[m][p];
                        tempGyr[m][j] = gyroscope[m][p];
                    }
                }
                ++p;
            }
            windows[i] = new SensorWindow(tempAcc, tempOri, tempMag, tempGyr);
            p = (int) (p - STEP_OFFSET * VECTORS_PER_WINDOW + 1);
        }
        return windows;
    }

    private static double[][] getAllFeatureVectors(SensorWindow[] windows) {
        int N = windows.length;
        int M = windows[0].getFeatureVector().length;
        double[][] ans = new double[N][M];
        for (int i = 0; i < N; ++i) {
            double[] temp = windows[i].getFeatureVector();
            System.arraycopy(temp, 0, ans[i], 0, M);
        }
        return ans;
    }
}
