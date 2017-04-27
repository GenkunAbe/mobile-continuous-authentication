package org.genku.touchauth.Util;

import org.genku.touchauth.Util.Jama.Complex;
import org.genku.touchauth.Util.Jama.FFT;

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

    public static double variance(double[] xs, double mean) {
        double sum = 0;
        for (double x : xs) {
            sum += (x - mean) * (x - mean);
        }
        return sum / xs.length;
    }

    public static double variance(double[] xs) {
        double mean = mean(xs);
        return variance(xs, mean);
    }

    public static double median(double[] vector) {
        double[] tmp = new double[vector.length];
        System.arraycopy(vector, 0, tmp, 0, vector.length);
        AlgorithmUtils.quickSort(tmp, 0, tmp.length - 1);
        return tmp[tmp.length / 2];
    }



    public static int maxIndex(int[] vector) {
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

    public static double mode(double[] vector, double max, double min) {
        double mode;
        int n = 5;

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

    public static double mode(double[] vector) {

        double max = max(vector);
        double min = min(vector);

        return mode(vector, max, min);
    }

    public static double skewness(double[] vector) {
        double mean = mean(vector);
        double median = median(vector);
        double var = variance(vector);
        return skewness(vector, mean, median, var);
    }

    public static double skewness(double[] xs, double mean, double median, double var) {
        return 3 * (mean - median) / Math.sqrt(var);
    }

    public static double kurtosis(double[] vector) {
        double mean = mean(vector);
        double var = variance(vector);
        return kurtosis(vector, mean, var);
    }

    public static double kurtosis(double[] vector, double mean, double var) {
        double kurtosis = 0;
        double std = Math.sqrt(var);
        for (double value : vector) {
            kurtosis += Math.pow(value - mean, 4);
        }
        kurtosis /= (vector.length * Math.pow(std, 4));
        return kurtosis;
    }

    public static int numberOfLocalPeaks(double[] v) {
        int ans = 0;
        int n = v.length - 1;
        for(int i = 1; i < n; ++i) {
            if(v[i-1] > v[i] && v[i] < v[i+1]) {
                ++ans;
            }
        }
        return ans;
    }

    public static int numberOfLocalCrests(double[] v) {
        int ans = 0;
        int n = v.length - 1;
        for(int i = 1; i < n; ++i) {
            if(v[i-1] < v[i] && v[i] > v[i+1]) {
                ++ans;
            }
        }
        return ans;
    }

    public static double[] prctile(double[] vector) {
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

    public static double combSignalMagi(double[] xs, double[] ys, double[] zs) {
        double csm = 0;

        double max = max(max(xs), max(ys), max(zs));
        double min = min(min(xs), min(ys), min(zs));

        for (double value : xs) {
            double newValue = (value - min) / (max - min);
            csm = csm + newValue * newValue;
        }
        for (double value : ys) {
            double newValue = (value - min) / (max - min);
            csm = csm + newValue * newValue;
        }
        for (double value : zs) {
            double newValue = (value - min) / (max - min);
            csm = csm + newValue * newValue;
        }

        return csm;
    }

    public static double[] fft(double[] vector) {
        int N = 128;
        double[] xk = new double[8];
        Complex[] complexes = new Complex[N];
        for (int j = 0; j < N; ++j) {
            if (j < vector.length) {
                complexes[j] = new Complex(vector[j], 0);
            } else {
                complexes[j] = new Complex(0, 0);
            }
        }
        Complex[] tmp = FFT.fft(complexes);
        for (int j = 0; j < xk.length; ++j) {
            xk[j] = tmp[j].scale();
        }
        return xk;
    }

    public static double max(double... args) {
        double m = -Double.MAX_VALUE;
        for (double value : args) {
            if (m < value) {
                m = value;
            }
        }
        return m;
    }

    public static double min(double... args) {
        double m = Double.MAX_VALUE;
        for (double value : args) {
            if (m > value) {
                m = value;
            }
        }
        return m;
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
