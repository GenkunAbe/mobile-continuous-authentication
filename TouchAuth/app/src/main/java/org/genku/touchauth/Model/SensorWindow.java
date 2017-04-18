package org.genku.touchauth.Model;

import org.genku.touchauth.Util.AlgorithmUtils;
import org.genku.touchauth.Util.Jama.Complex;
import org.genku.touchauth.Util.Jama.FFT;

public class SensorWindow {

    private double[][] accelerometer;
    private double[][] orientation;
    private double[][] magnetic;
    private double[][] gyroscope;
    private double[][] accFft;
    private double[][] oriFft;
    private double[][] magFft;
    private double[][] gyrFft;
    private double[][] mean;
    private double[][] median;
    private double[][] var;
    private double[][] mode;
    private double[][] skewness;
    private double[][] kurtosis;
    private double[][][] prctile;
    private double[][] max;
    private double[][] min;
    private double[][] avgSampChange;
    private int[][] numOfLocPeaks;
    private int[][] numOfLocCrests;
    private double[] combSignalMagi;
    private double[] featureVector;

    public SensorWindow(double[][] acc, double[][] ori, double[][] mag, double[][] gyr) {
        //accelerometer = acc;
        //orientation = ori;
        //magnetic = mag;
        //gyroscope = gyr;

        accelerometer = setStandard(acc);
        orientation = setStandard(ori);
        magnetic = setStandard(mag);
        gyroscope = setStandard(gyr);

        mean = setMean();
        median = setMedian();
        var = setVar();
        mode = setMode();
        skewness = setSkewness();
        kurtosis = setKurtosis();
        prctile = setPrctile();
        max = setMax();
        min = setMin();
        accFft = setXk(accelerometer);
        oriFft = setXk(orientation);
        magFft = setXk(magnetic);
        gyrFft = setXk(gyroscope);
        avgSampChange = setAvgSampChange();
        numOfLocPeaks = setNumOfLocPeaks();
        numOfLocCrests = setNumOfLocCrests();
        combSignalMagi = setCombSignalMagi();

        featureVector = setFeatureVector();
    }

    private double[] setFeatureVector() {
        double[] featureVector = new double[87];
        int k = 0;
        int n = 1;
        for (double[] vector : mean) {
            for (double value : vector) {
                if (4 >= n && n <= 8) {
                    featureVector[k] = value;
                    ++k;
                }
                ++n;
            }
        }
        n = 1;
        for (double[] vector : median) {
            for (double value : vector) {
                if (n == 5) {
                    featureVector[k] = value;
                    ++k;
                }
                ++n;
            }
        }
        n = 1;
        for (double[] vector : skewness) {
            for (double value : vector) {
                if (n == 5 || n == 6) {
                    featureVector[k] = value;
                    ++k;
                }
                ++n;
            }
        }
        n = 1;
        for (double[] vector : kurtosis) {
            for (double value : vector) {
                if (n != 5 && n != 6 && n != 7 && n != 9 && n < 10) {
                    featureVector[k] = value;
                    ++k;
                }
                ++n;
            }
        }
        n = 1;
        for (double[] vector : max) {
            for (double value : vector) {
                if (n >=1 && n <= 9) {
                    featureVector[k] = value;
                    ++k;
                }
                ++n;
            }
        }
        n = 1;
        for (double[] vector : min) {
            for (double value : vector) {
                if (n >= 3 && n != 6 && n < 10) {
                    featureVector[k] = value;
                    ++k;
                }
                ++n;
            }
        }
        n = 1;
        for (int[] vector : numOfLocPeaks) {
            for (int value : vector) {
                if (n >= 1 && n <= 9) {
                    featureVector[k] = value;
                    ++k;
                }
                ++n;
            }
        }
        n = 1;
        for (int[] vector : numOfLocCrests) {
            for (int value : vector) {
                if (n >= 1 && n <= 9) {
                    featureVector[k] = value;
                    ++k;
                }
                ++n;
            }
        }
        n = 1;
        for (int i = 1; i < 3; ++i) {
            for (double[] vector : prctile[i]) {
                for (double value : vector) {
                    if (n == 8 || n == 10 || n == 11 || n == 13 || n == 15 || n == 16 || n == 20
                            || n == 21 || n == 22 || n == 24 || n == 25 || n == 26 || n == 30
                            || (n >= 35 && n <= 40) || n == 43 || n == 44 || n == 45) {
                        featureVector[k] = value;
                        ++k;
                    }
                    ++n;
                }
            }
        }
        n = 1;
        for (double value : combSignalMagi) {
            if (n == 1) {
                featureVector[k] = value;
                ++k;
            }
            ++n;
        }
        n = 1;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 8; ++j) {
                if ((n >= 1 && n <= 3) || (n >= 5 && n <= 7) || n == 9 || n == 17 || n == 18) {
                    featureVector[k] = accFft[i][j];
                    ++k;
                }
                ++n;
            }
        }
        n = 1;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (n == 1 || n == 2 || n == 9 || n == 10 || n == 17 || n == 20) {
                    featureVector[k] = oriFft[i][j];
                    ++k;
                }
                ++n;
            }
        }
        n = 1;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (n == 1 || n == 2 || n == 9 || (n >= 10 && n <= 20 && n != 15)) {
                    featureVector[k] = magFft[i][j];
                    ++k;
                }
                ++n;
            }
        }
        /*n = 1;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (n >= 1 && n <= 8 && n != 4) {
                    featureVector[k] = gyrFft[i][j];
                    ++k;
                }
                ++n;
            }
        }*/
        return featureVector;
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



    private int[][] setNumOfLocCrests() {
        int[][] nolci = new int[4][3];
        for (int i = 0; i < 3; ++i) {
            nolci[0][i] = nolc(accelerometer[i]);
            nolci[1][i] = nolc(orientation[i]);
            nolci[2][i] = nolc(magnetic[i]);
            nolci[3][i] = nolc(gyroscope[i]);
        }
        return nolci;
    }

    private int[][] setNumOfLocPeaks() {
        int[][] nolpi = new int[4][3];
        for (int i = 0; i < 3; ++i) {
            nolpi[0][i] = nolp(accelerometer[i]);
            nolpi[1][i] = nolp(orientation[i]);
            nolpi[2][i] = nolp(magnetic[i]);
            nolpi[3][i] = nolp(gyroscope[i]);
        }
        return nolpi;
    }

    private double[][] setAvgSampChange() {
        double[][] asbsci = new double[4][3];
        for (int i = 0; i < 3; ++i) {
            asbsci[0][i] = asbsc(accelerometer[i]);
            asbsci[1][i] = asbsc(orientation[i]);
            asbsci[2][i] = asbsc(magnetic[i]);
            asbsci[3][i] = asbsc(gyroscope[i]);
        }
        return asbsci;
    }

    private double[][] setMin() {
        double[][] mini = new double[4][3];
        for (int i = 0; i < 3; ++i) {
            mini[0][i] = min(accelerometer[i]);
            mini[1][i] = min(orientation[i]);
            mini[2][i] = min(magnetic[i]);
            mini[3][i] = min(gyroscope[i]);
        }
        return mini;
    }

    private double[][] setMax() {
        double[][] maxi = new double[4][3];
        for (int i = 0; i < 3; ++i) {
            maxi[0][i] = max(accelerometer[i]);
            maxi[1][i] = max(orientation[i]);
            maxi[2][i] = max(magnetic[i]);
            maxi[3][i] = max(gyroscope[i]);
        }
        return maxi;
    }

    private double[][] setXk(double[][] vectors) {
        int N = 128;
        double[][] xk = new double[3][8];
        for (int i = 0; i < 3; ++i) {

            Complex[] complexes = new Complex[N];
            for (int j = 0; j < N; ++j) {
                if (j < vectors[i].length) {
                    complexes[j] = new Complex(vectors[i][j], 0);
                } else {
                    complexes[j] = new Complex(0, 0);
                }
            }
            Complex[] tmp = FFT.fft(complexes);
            for (int j = 0; j < xk[i].length; ++j) {
                xk[i][j] = tmp[j].scale();
            }
        }
        return xk;
    }

    private double[][][] setPrctile() {
        double[][][] prctile = new double[4][3][5];
        for (int i = 0; i < 3; ++i) {
            prctile[0][i] = prctile(accelerometer[i]);
            prctile[1][i] = prctile(orientation[i]);
            prctile[2][i] = prctile(magnetic[i]);
            prctile[3][i] = prctile(gyroscope[i]);
        }
        return prctile;
    }


    private double[][] setKurtosis() {
        double[][] kurtosis = new double[4][3];
        for (int i = 0; i < 3; ++i) {
            kurtosis[0][i] = kurtosis(accelerometer[i]);
            kurtosis[1][i] = kurtosis(orientation[i]);
            kurtosis[2][i] = kurtosis(magnetic[i]);
            kurtosis[3][i] = kurtosis(gyroscope[i]);
        }
        return kurtosis;
    }

    private double[][] setSkewness() {
        double[][] skewness = new double[4][3];
        for (int i = 0; i < 3; ++i) {
            skewness[0][i] = skewness(accelerometer[i]);
            skewness[1][i] = skewness(orientation[i]);
            skewness[2][i] = skewness(magnetic[i]);
            skewness[3][i] = skewness(gyroscope[i]);
        }
        return skewness;
    }

    private double[][] setMode() {
        double[][] mode = new double[4][3];
        for (int i = 0; i < 3; ++i) {
            mode[0][i] = mode(accelerometer[i]);
            mode[1][i] = mode(orientation[i]);
            mode[2][i] = mode(magnetic[i]);
            mode[3][i] = mode(gyroscope[i]);
        }
        return mode;
    }

    private double[][] setVar() {
        double[][] var = new double[4][3];
        for (int i = 0; i < 3; ++i) {
            var[0][i] = var(accelerometer[i]);
            var[1][i] = var(orientation[i]);
            var[2][i] = var(magnetic[i]);
            var[3][i] = var(gyroscope[i]);
        }
        return var;
    }

    private double[][] setMedian() {
        double[][] median = new double[4][3];
        for (int i = 0; i < 3; ++i) {
            median[0][i] = median(accelerometer[i]);
            median[1][i] = median(orientation[i]);
            median[2][i] = median(magnetic[i]);
            median[3][i] = median(gyroscope[i]);
        }
        return median;
    }

    public double[][] setMean() {
        double[][] meansi = new double[4][3];
        for (int i = 0; i < 3; ++i) {
            meansi[0][i] = mean(accelerometer[i]);
            meansi[1][i] = mean(orientation[i]);
            meansi[2][i] = mean(magnetic[i]);
            meansi[3][i] = mean(gyroscope[i]);
        }
        return meansi;
    }

    private double mean(double[] vector) {
        double mean = 0;
        for (double value : vector) {
            mean += value;
        }
        mean /= vector.length;
        return mean;
    }

    private double var(double[] vector) {
        double var = 0;
        double mean = mean(vector);
        for (double value : vector) {
            var += Math.pow(value - mean, 2);
        }
        var /= vector.length;
        return var;
    }

    private double median(double[] vector) {
        double[] tmp = new double[vector.length];
        System.arraycopy(vector, 0, tmp, 0, vector.length);
        AlgorithmUtils.quickSort(tmp, 0, tmp.length - 1);
        return tmp[tmp.length / 2];
    }

    private double mode(double[] vector) {
        double mode;
        int n = 5;
        double max = max(vector);
        double min = min(vector);
        double step = (max - min) / n;
        if (step < 1e-6) {
            mode = vector[0];
            return mode;
        }
        int[] number = new int[n];
        for (double value : vector) {
            for (int j = 0; j < n; ++j) {
                if (value >= (min + j * step) && value < (min + (j + 1) * step)) {
                    ++number[j];
                }
            }
        }
        int maxIndex = maxIndex(number);

        if (maxIndex == 0) {
            mode = min + step / 2;
            return mode;
        }
        if (maxIndex == number.length - 1) {
            mode = max - step / 2;
            return mode;
        }

        for (int i = 0; i < number.length; ++i) {
            for (int j = i + 1; j < number.length; ++j) {
                if (number[i] == number[j] && (i == maxIndex || j == maxIndex)) {
                    if (Math.abs(i - j) == 1) {
                        mode = j * step;
                        return mode;
                    } else {
                        if (Math.random() > 0.5) {
                            mode = min + step * (i + 0.5);
                        } else {
                            mode = min + step * (j + 0.5);
                        }
                        return mode;
                    }
                }
            }
        }

        double L = min + maxIndex * step;
        if (number[maxIndex - 1] + number[maxIndex + 1] == 0) {
            mode = L + 0.5 * step;
        } else {
            mode = L + number[maxIndex + 1] / (number[maxIndex - 1] + number[maxIndex + 1]) * step;
        }
        return mode;
    }

    private double skewness(double[] vector) {
        double mean = mean(vector);
        double median = median(vector);
        double var = var(vector);
        return 3 * (mean - median) / Math.sqrt(var);
    }

    private double kurtosis(double[] vector) {
        double kurtosis = 0;
        double mean = mean(vector);
        double std = Math.sqrt(var(vector));
        for (double value : vector) {
            kurtosis += Math.pow(value - mean, 4);
        }
        kurtosis /= (vector.length * Math.pow(std, 4));
        return kurtosis;
    }

    private double[] prctile(double[] vector) {
        double[] tmp = new double[vector.length];
        System.arraycopy(vector, 0, tmp, 0, vector.length);
        AlgorithmUtils.quickSort(tmp, 0, tmp.length - 1);
        double[] prctile = new double[5];
        for (int i = 0; i < 5; ++i) {
            double tempIndex = (tmp.length - 1) * 0.25 * i;
            int index = (int) tempIndex;
            if (tempIndex - index < 1e-6) {
                prctile[i] = tmp[index];
            } else {
                if (index + 1 < tmp.length) {
                    prctile[i] = (tmp[index] + tmp[index + 1]) / 2;
                } else {
                    prctile[i] = tmp[index];
                }

            }
        }
        return prctile;
    }

    private double max(double[] vector) {
        double m = vector[0];
        for (double value : vector) {
            if (m < value) {
                m = value;
            }
        }
        return m;
    }

    private int maxIndex(int[] vector) {
        int m = vector[0];
        int index = 0;
        for (int i = 0; i < vector.length; ++i) {
            if (m < vector[i]) {
                m = vector[i];
                index = i;
            }
        }
        return index;
    }

    private double min(double[] vector) {
        double m = vector[0];
        for (double value : vector) {
            if (m > value) {
                m = value;
            }
        }
        return m;
    }

    private double asbsc(double[] vector) {
        double asbsc = 0;
        for (int i = 0; i < vector.length - 1; ++i) {
            asbsc = asbsc + (vector[i + 1] - vector[i]) / vector[i];
        }
        asbsc = asbsc / (vector.length - 1);
        return asbsc;
    }

    private int nolc(double[] v) {
        int ans = 0;
        int n = v.length - 1;
        for(int i = 1; i < n; ++i) {
            if(v[i-1] > v[i] && v[i] < v[i+1]) {
                ++ans;
            }
        }
        return ans;
    }

    private int nolp(double[] v) {
        int ans = 0;
        int n = v.length - 1;
        for(int i = 1; i < n; ++i) {
            if(v[i-1] < v[i] && v[i] > v[i+1]) {
                ++ans;
            }
        }
        return ans;
    }

    private double csm(double[][] vectors) {
        double csm = 0;

        double max = -Double.MAX_VALUE;
        double min = Double.MAX_VALUE;

        for (int i = 0; i < 3; ++i) {
            for (double value : vectors[i]) {
                if (max < value) {
                    max = value;
                }
                if (min > value) {
                    min = value;
                }
            }
        }

        for (int i = 0; i < 3; ++i) {
            for (double value : vectors[i]) {
                double newValue = (value - min) / (max - min);
                csm = csm + newValue * newValue;
            }
        }

        return csm;
    }

    private double[] setCombSignalMagi() {
        double[] csmi = new double[4];
        csmi[0] = csm(accelerometer);
        csmi[1] = csm(orientation);
        csmi[2] = csm(magnetic);
        csmi[3] = csm(gyroscope);
        return csmi;
    }

    public double[] getFeatureVector() {
        return featureVector;
    }
}
