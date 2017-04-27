package org.genku.touchauth.Model;

import org.genku.touchauth.Util.AlgorithmUtils;
import org.genku.touchauth.Util.Jama.Complex;
import org.genku.touchauth.Util.Jama.FFT;
import org.genku.touchauth.Util.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SensorWindow {

    // Select Features By These Tables
    private static final int[] MEAN_FLAG                = { 0,0,0, 1,1,1, 1,1,1, 1,1,1 };
    private static final int[] MEDIAN_FLAG              = { 0,0,0, 0,1,0, 0,0,0, 0,0,0 };
    private static final int[] VAR_FLAG                 = { 0,0,0, 0,0,0, 0,0,0, 0,0,0 };
    private static final int[] MODE_FLAG                = { 0,0,0, 0,0,0, 0,0,0, 0,0,0 };
    private static final int[] SKEWNESS_FLAG            = { 0,0,0, 0,1,1, 0,0,0, 0,0,0 };
    private static final int[] KURTOSIS_FLAG            = { 1,1,1, 1,0,0, 0,1,0, 1,1,1 };
    private static final int[] MAX_FLAG                 = { 1,1,1, 1,1,1, 1,1,1, 1,1,1 };
    private static final int[] MIN_FLAG                 = { 0,0,1, 1,1,0, 1,1,1, 1,1,1 };
    private static final int[] NUMOFLOCALPEAKS_FLAG     = { 1,1,1, 1,1,1, 1,1,1, 1,1,1 };
    private static final int[] NUMOFLOCALCRESTS_FLAG    = { 1,1,1, 1,1,1, 1,1,1, 1,1,1 };
    private static final int[] PRCTILE_FLAG             = { 0,0,0,0,0, 0,0,1,0,1, 1,0,1,0,1,
                                                            1,0,0,0,1, 1,1,0,1,1, 1,0,0,0,1,
                                                            0,0,0,0,1, 1,1,1,1,1, 0,0,1,1,1,
                                                            0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0 };
    private static final int[] COMBSINGNALMAGI_FLAG     = { 1, 0, 0, 0 };
    private static final int[] FFT_FLAG                 = { 1,1,1,0,1,1,1,0, 1,0,0,0,0,0,0,0, 1,1,0,0,0,0,0,0,
                                                            1,1,0,0,0,0,0,0, 1,1,0,0,0,0,0,0, 1,0,0,1,0,0,0,0,
                                                            1,1,0,0,0,0,0,0, 1,1,1,1,1,1,1,1, 1,1,1,1,0,0,0,0,
                                                            1,1,1,0,1,1,1,1, 0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0 };
    private double[] featureVector;

    public SensorWindow(double[][] acc, double[][] ori, double[][] mag, double[][] gyr) {

        // Shape of data: 12 * length
        // lines: accX1, accX2, ..., accXn
        //        accY1, accY2, ..., accXn
        //        ...
        //        gyrZ1, gyrZ2, ..., gyrZn
        List<double[]> data = preProcessing(acc, ori, mag, gyr);

        int sensorNum = 4;
        int dimensionPreSensor = 3;
        int prctileNum = 5;
        int fftNum = 8;

        double[] mean           = new double[sensorNum * dimensionPreSensor];
        double[] median         = new double[sensorNum * dimensionPreSensor];
        double[] var            = new double[sensorNum * dimensionPreSensor];
        double[] mode           = new double[sensorNum * dimensionPreSensor];
        double[] skewness       = new double[sensorNum * dimensionPreSensor];
        double[] kurtosis       = new double[sensorNum * dimensionPreSensor];
        double[] max            = new double[sensorNum * dimensionPreSensor];
        double[] min            = new double[sensorNum * dimensionPreSensor];
        double[] numOfLocPeaks  = new double[sensorNum * dimensionPreSensor];
        double[] numOfLocCrests = new double[sensorNum * dimensionPreSensor];
        double[] prctile        = new double[sensorNum * dimensionPreSensor * prctileNum];
        double[] combSignalMagi = new double[sensorNum];
        double[] fft            = new double[sensorNum * dimensionPreSensor * fftNum];

        int n = mean.length;
        for (int i = 0; i < n; ++i) {
            double[] line = data.get(i);
            mean[i] = MathUtils.mean(line);
            median[i] = MathUtils.median(line);
            var[i] = MathUtils.variance(line, mean[i]);
            max[i] = MathUtils.max(line);
            min[i] = MathUtils.min(line);
            mode[i] = MathUtils.mode(line, max[i], min[i]);
            skewness[i] = MathUtils.skewness(line, mean[i], median[i], var[i]);
            kurtosis[i] = MathUtils.kurtosis(line, mean[i], var[i]);
            numOfLocPeaks[i] = MathUtils.numberOfLocalPeaks(line);
            numOfLocCrests[i] = MathUtils.numberOfLocalCrests(line);

            double[] tmpPrctile = MathUtils.prctile(line);
            System.arraycopy(tmpPrctile, 0, prctile, i * prctileNum, prctileNum);

            double[] tmpFFT = MathUtils.fft(line);
            System.arraycopy(tmpFFT, 0, fft, i * fftNum, fftNum);
        }
        for (int i = 0; i < 4; ++i) {
            combSignalMagi[i] = MathUtils.combSignalMagi(data.get(i * 3), data.get(i * 3 + 1), data.get(i * 3 + 2));
        }

        List<Double> tmpFeatureVectors = new ArrayList<>();

        n = mean.length;
        for (int i = 0; i < n; ++i) {
            if (MEAN_FLAG[i] == 1) {
                tmpFeatureVectors.add(mean[i]);
            }
        }
        n = median.length;
        for (int i = 0; i < n; ++i) {
            if (MEDIAN_FLAG[i] == 1) {
                tmpFeatureVectors.add(median[i]);
            }
        }
        n = var.length;
        for (int i = 0; i < n; ++i) {
            if (VAR_FLAG[i] == 1) {
                tmpFeatureVectors.add(var[i]);
            }
        }

        n = mode.length;
        for (int i = 0; i < n; ++i) {
            if (MODE_FLAG[i] == 1) {
                tmpFeatureVectors.add(mode[i]);
            }
        }
        n = skewness.length;
        for (int i = 0; i < n; ++i) {
            if (SKEWNESS_FLAG[i] == 1) {
                tmpFeatureVectors.add(skewness[i]);
            }
        }
        n = kurtosis.length;
        for (int i = 0; i < n; ++i) {
            if (KURTOSIS_FLAG[i] == 1) {
                tmpFeatureVectors.add(kurtosis[i]);
            }
        }
        n = max.length;
        for (int i = 0; i < n; ++i) {
            if (MAX_FLAG[i] == 1) {
                tmpFeatureVectors.add(max[i]);
            }
        }
        n = min.length;
        for (int i = 0; i < n; ++i) {
            if (MIN_FLAG[i] == 1) {
                tmpFeatureVectors.add(min[i]);
            }
        }
        n = numOfLocPeaks.length;
        for (int i = 0; i < n; ++i) {
            if (NUMOFLOCALPEAKS_FLAG[i] == 1) {
                tmpFeatureVectors.add(numOfLocPeaks[i]);
            }
        }
        n = numOfLocCrests.length;
        for (int i = 0; i < n; ++i) {
            if (NUMOFLOCALCRESTS_FLAG[i] == 1) {
                tmpFeatureVectors.add(numOfLocCrests[i]);
            }
        }
        n = prctile.length;
        for (int i = 0; i < n; ++i) {
            if (PRCTILE_FLAG[i] == 1) {
                tmpFeatureVectors.add(prctile[i]);
            }
        }
        n = combSignalMagi.length;
        for (int i = 0; i < n; ++i) {
            if (COMBSINGNALMAGI_FLAG[i] == 1) {
                tmpFeatureVectors.add(combSignalMagi[i]);
            }
        }
        n = fft.length;
        for (int i = 0; i < n; ++i) {
            if (FFT_FLAG[i] == 1) {
                tmpFeatureVectors.add(fft[i]);
            }
        }

        featureVector = new double[tmpFeatureVectors.size()];
        for (int i = 0; i < tmpFeatureVectors.size(); ++i) {
            featureVector[i] = tmpFeatureVectors.get(i);
        }
    }

    public double[] getFeatureVectors() {
        return featureVector;
    }

    private List<double[]> preProcessing(double[][] acc, double[][] ori, double[][] mag, double[][] gyr) {
        acc = setStandard(acc);
        ori = setStandard(ori);
        mag = setStandard(mag);
        gyr = setStandard(gyr);
        List<double[]> data = new ArrayList<>();
        Collections.addAll(data, acc);
        Collections.addAll(data, ori);
        Collections.addAll(data, mag);
        Collections.addAll(data, gyr);
        return data;
    }

    private double[][] setStandard(double[][] vectors) {
        for (int i = 0; i < 3; ++i) {
            double mean = 0;
            for (int j = 0; j < vectors[i].length; ++j) {
                mean += vectors[i][j];
            }
            mean /= vectors[i].length;
            double std = 0;
            for (int j = 0; j < vectors[i].length; ++j) {
                std += Math.pow(vectors[i][j] - mean, 2);
            }
            std = Math.sqrt(std / vectors[i].length);
            for (int j = 0; j < vectors[i].length; ++j) {
                vectors[i][j] = (vectors[i][j] - mean) / std;
            }
        }
        return vectors;
    }
}
