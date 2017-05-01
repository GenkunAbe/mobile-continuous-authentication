package org.genku.touchauth.Model;

import org.genku.touchauth.Util.TextFile;

/**
 * Created by genku on 4/27/2017.
 */

public class NGramModel {

    private int numOfClusters;
    private int numOfFeatures;
    private int N;

    private Assessment assessment;
    private double[][] model;

    public NGramModel(int numOfClusters, int N) {
        this.numOfClusters = numOfClusters;
        this.N = N;
    }

    public NGramModel(double[][] model, double[][] centroids, int N) {
        this.model = model;
        this.assessment.centroids = centroids;
        this.numOfClusters = centroids.length;
        this.N = N;
    }

    public void train(double[][] featureVectors) {
        this.numOfFeatures = featureVectors[0].length;
        assessment = KMeans.kMeansPlusPlus(featureVectors, numOfClusters);
        int[] dictionary = buildDictionary(featureVectors, assessment.centroids);
        model = buildModel(dictionary);
    }

    public double predict(double[][] featureVectors) {
        int[] seq = buildDictionary(featureVectors, assessment.centroids);
        double[] probabilities = new double[seq.length - N + 1];
        for (int i = 0; i < probabilities.length; ++i) {
            int[] temp = new int[N];
            System.arraycopy(seq, i, temp, 0, N);
            probabilities[i] = getProbability(temp);
        }
        double ans = 0;
        for (double probability : probabilities) {
            ans += Math.log10(probability);
        }
        return ans;
    }

    public double[][] getModel() {
        return model;
    }

    public void saveModel(String filename) {
        TextFile.writeFileFromNums(filename, model, false);
    }

    private double getProbability(int[] seq) {
        double ans = 1;
        for (int i = 0; i < seq.length - 1; ++i) {
            ans = ans * model[seq[i]][seq[i + 1]];
        }
        return ans;
    }

    private double[][] buildModel(int[] dictionary) {
        double[][] ans = new double[numOfFeatures][numOfFeatures];
        for (int i = 0; i < numOfFeatures; ++i) {
            double denominator = (double) frequencyInDict(i, dictionary);

            if (denominator < 1e-6) {
                for (int j = 0; j < numOfFeatures; ++j) {
                    ans[i][j] = 1.0 / numOfFeatures;
                }
                continue;
            }

            for (int j = 0; j < numOfFeatures; ++j) {
                ans[i][j] = frequencyInDict(i, j, dictionary) / denominator;
                if (ans[i][j] < 1e-6) {
                    ans[i][j] = 1e-3;
                }
            }
        }
        return ans;
    }

    private static int frequencyInDict(int target, int[] dictionary) {
        int ans = 0;
        for (int x : dictionary) {
            if (x == target) {
                ++ans;
            }
        }
        return ans;
    }

    private static int frequencyInDict(int seq1, int seq2, int[] dictionary) {
        int ans = 0;
        for (int i = 0; i < dictionary.length - 1; ++i) {
            if (dictionary[i] == seq1 && dictionary[i + 1] == seq2) {
                ++ans;
            }
        }
        return ans;
    }

    private static int[] buildDictionary(double[][] featureVectors, double[][] centroids) {
        int[] rawDict = new int[featureVectors.length];

        for (int i = 0; i < featureVectors.length; ++i) {
            rawDict[i] = findNearestCentroid(featureVectors[i], centroids);
        }

        return rawDict;
    }

    private static int findNearestCentroid(double[] element, double[][] centroids) {
        int minimumIndex = 0;
        double minimumDistance = Double.MAX_VALUE;
        for (int i = 0; i < centroids.length; ++i) {
            double distance = distance(element, centroids[i]);
            if (minimumDistance > distance) {
                minimumDistance = distance;
                minimumIndex = i;
            } else if (minimumDistance == distance && Math.random() < 0.5) {
                minimumIndex = i;
            }
        }
        return minimumIndex;
    }

    private static double distance(double[] a, double[] b) {
        if (a.length != b.length) {
            return -1;
        } else {
            double tempSum = 0;
            for(int i = 0; i < a.length; ++i) {
                tempSum = tempSum + (a[i] - b[i]) * (a[i] - b[i]);
            }
            return Math.sqrt(tempSum);
        }
    }

}
