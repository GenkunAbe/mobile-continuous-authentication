package org.genku.touchauth.Model;

import org.genku.touchauth.Util.MathUtils;

/**
 * Created by genku on 4/1/2017.
 */

public class FeatureExtraction {

    public static double[] extract(TouchEvent event) {
        if (event == null) return null;
        if (event.midData.length == 0) {
            return extractClickEvent(event);
        }
        double startTime = Double.parseDouble(event.prevData[0][0]);
        double endTime = Double.parseDouble(event.postData[0][0]);
        if (Math.abs(endTime - startTime) < 0.05) {
            event.midData = null;
            return extractClickEvent(event);
        }
        else {
            return extractSlideEvent(event);
        }
    }

    private static double[] extractClickEvent(TouchEvent event) {
        double p = MathUtils.hexToDec(event.prevData[0][3]);
        double startTime = Double.parseDouble(event.prevData[0][0]);
        double endTime = Double.parseDouble(event.postData[0][0]);
        double dt = endTime - startTime;

        double[] features = new double[2];
        features[0] = p;
        features[1] = dt;
        return features;
    }

    private static double[] extractSlideEvent(TouchEvent event) {

        double[] features = new double[16];

        int numOfMid = event.midData.length;

        // all value from String to double
        double[] p = new double[1 + numOfMid];
        double[] x = new double[1 + numOfMid];
        double[] y = new double[1 + numOfMid];
        double[] t = new double[1 + numOfMid];
        double[] v = new double[1 + numOfMid];
        p[0] = MathUtils.hexToDec(event.prevData[0][3]);
        x[0] = MathUtils.hexToDec(event.prevData[1][3]);
        y[0] = MathUtils.hexToDec(event.prevData[2][3]);
        t[0] = Double.parseDouble(event.prevData[0][0]);
        for (int i = 1; i <= numOfMid; ++i) {
            p[i] = MathUtils.hexToDec(event.midData[i][0][3]);
            x[i] = MathUtils.hexToDec(event.midData[i][1][3]);
            y[i] = MathUtils.hexToDec(event.midData[i][2][3]);
            t[i] = Double.parseDouble(event.midData[i][0][0]);
            v[i] = MathUtils.displacement(x[i], y[i], x[i - 1], y[i - 1]) / (t[i] - t[i - 1]);
        }



        // pressure at start point
        double ps = p[0];
        features[0] = ps;

        // velocity at start point
        double vs = v[1];
        features[1] = vs;

        // pressure at finishing point
        double pt = p[numOfMid];
        features[2] = pt;

        // velocity at finishing point
        double vt = v[numOfMid];
        features[3] = vt;

        // Calculate MidPoint
        int midPointIndex = getMidPoint(x, y);

        // pressure at mid point
        double pm = p[midPointIndex];
        features[4] = pm;

        // velocity at mid point
        double vm = v[midPointIndex + 1];
        features[5] = vm;

        // displacement between start point and finishing point
        double deltaX = x[numOfMid] - x[0];
        double deltaY = y[numOfMid] - y[0];
        double d = MathUtils.norm2(deltaX, deltaY);
        features[6] = deltaX;
        features[7] = deltaY;
        features[8] = d;

        // angle of start point and finishing point
        double thetaST = MathUtils.angle(x[0], y[0], x[numOfMid], y[numOfMid]);
        features[9] = thetaST;

        // angle of start point and mid point
        double thetaSM = MathUtils.angle(x[0], y[0], x[midPointIndex], y[midPointIndex]);
        features[10] = thetaSM;

        // angle of mid point and finishing point
        double thetaMT = MathUtils.angle(x[midPointIndex], y[midPointIndex], x[numOfMid], y[numOfMid]);
        features[11] = thetaMT;

        // mean of velocity
        double vM = MathUtils.mean(v);
        features[12] = vM;

        // variance of velocity
        double vD = MathUtils.variance(v);
        features[13] = vD;

        // mean of pressure
        double pM = MathUtils.mean(p);
        features[14] = pM;

        // variance of pressure
        double pD = MathUtils.variance(p);
        features[15] = pD;

        return features;
    }

    private static int getMidPoint(double[] x, double[] y) {
        double maxDist = -Double.MAX_VALUE;
        int maxIndex = -1;
        double x1 = x[0], y1 = y[0], x2 = x[x.length - 1], y2 = y[y.length - 1];
        for (int i = 1; i < x.length - 1; ++i) {
            double dist = MathUtils.height(x1, y1, x2, y2, x[i], y[i]);
            if (dist > maxDist) {
                maxDist = dist;
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
