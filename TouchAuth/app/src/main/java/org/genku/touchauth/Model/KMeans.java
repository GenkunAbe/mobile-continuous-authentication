package org.genku.touchauth.Model;

public class KMeans {

    public static Assessment bisectingKMeans(int k, double[][] vectors) {
        int numberOfVectors = vectors.length;
        int dimension = vectors[0].length;
        int numberOfCurrentCentroids;
        double[] firstCentroid;
        Assessment assessment = new Assessment();
        assessment.centroids = new double[1][dimension];
        assessment.clusterMark = new int[numberOfVectors];

        // step 1: the init cluster is the whole data set
        firstCentroid = newCentroid(vectors, assessment.clusterMark, 0);
        System.arraycopy(firstCentroid, 0, assessment.centroids[0], 0, dimension);
        numberOfCurrentCentroids = assessment.centroids.length;
        assessment.clusterError = calculateClusterError(vectors, assessment.centroids, assessment.clusterMark);
        while(numberOfCurrentCentroids < k) {

            // min sum of square error
            double minSSE = Double.MAX_VALUE;

            // for each cluster
            int bestCentroidToSplit = 0;
            double[][] bestNewCentroids = new double[2][dimension];
            int[] bestClusterMark = new int[1];
            double[] bestClusterError = new double[1];
            for (int i = 0; i < numberOfCurrentCentroids; ++i) {

                // step 2: get samples in cluster i
                double[][] pointsInCurrentCluster = pointsInCurrCluster(vectors, assessment.clusterMark, i);


                if (pointsInCurrentCluster.length != 1) {

                    // step 3: cluster it to 2 sub-clusters using k-means
                    Assessment splitAssessment;
                    splitAssessment = kMeansPlusPlus(pointsInCurrentCluster, 2);

                    // step 4: calculate the sum of square error after split this cluster
                    double splitSSE = sum(splitAssessment.clusterError);
                    double notSplitSSE = sumNotSplitSSE(assessment.clusterMark, assessment.clusterError, i);
                    double currSplitSSE = splitSSE + notSplitSSE;

                    // step 5: find the best split cluster which has the min sum of square error
                    if (currSplitSSE < minSSE) {
                        minSSE = currSplitSSE;
                        bestCentroidToSplit = i;
                        bestNewCentroids = copy(splitAssessment.centroids);
                        bestClusterMark = copy(splitAssessment.clusterMark);
                        bestClusterError = copy(splitAssessment.clusterError);
                    }

                }

            }

            // step 6: modify the cluster index for adding new cluster
            for (int i = 0; i < bestClusterMark.length; ++i) {
                if (bestClusterMark[i] == 0) {
                    bestClusterMark[i] = bestCentroidToSplit;
                } else if (bestClusterMark[i] == 1) {
                    bestClusterMark[i] = numberOfCurrentCentroids;
                }
            }

            // step 7: update and append the centroids of the new 2 sub-cluster
            assessment.centroids[bestCentroidToSplit] = copy(bestNewCentroids[0]);
            assessment.centroids = add(assessment.centroids, bestNewCentroids[1]);

            // step 8: update the index and error of the samples
            int j = 0;
            for (int i = 0; i < assessment.clusterMark.length; ++i) {
                if (assessment.clusterMark[i] == bestCentroidToSplit) {
                    assessment.clusterMark[i] = bestClusterMark[j];
                    assessment.clusterError[i] = bestClusterError[j];
                    ++j;
                }
            }

            numberOfCurrentCentroids = assessment.centroids.length;
        }
        return assessment;
    }

    public static double[][] add(double[][] vectors, double[] vector) {
        double[][] ans = new double[vectors.length + 1][vectors[0].length];
        for (int i = 0; i < vectors.length; ++i) {
            System.arraycopy(vectors[i], 0, ans[i], 0, vectors[0].length);
        }
        System.arraycopy(vector, 0, ans[vectors.length], 0, vector.length);
        return ans;
    }

    public static double[] calculateClusterError(double[][] vectors, double[][] centroids, int[] clusterMark) {
        double[] ans = new double[vectors.length];
        for (int i = 0; i < vectors.length; ++i) {
            for (int j = 0; j < vectors[0].length; ++j) {
                ans[i] = ans[i] + Math.pow(vectors[i][j] - centroids[clusterMark[i]][j], 2);
            }
        }
        return ans;
    }

    public static double[][] pointsInCurrCluster(double[][] vectors, int[] clusterMark, int currClusterIndex) {
        int n = 0;
        for (int aClusterMark : clusterMark) {
            if (aClusterMark == currClusterIndex) {
                ++n;
            }
        }
        double[][] ans = new double[n][vectors[0].length];
        int k = 0;
        for (int i = 0; i < clusterMark.length; ++i) {
            if (clusterMark[i] == currClusterIndex) {
                System.arraycopy(vectors[i], 0, ans[k], 0, vectors[i].length);
                ++k;
            }
        }
        return ans;
    }

    public static double sum(double[] vector) {
        double ans = 0;
        for (double value : vector) {
            ans = ans + value;
        }
        return ans;
    }

    public static double sumNotSplitSSE(int[] clusterMark, double[] clusterError, int currClusterIndex) {
        double ans = 0;
        for (int i = 0; i < clusterMark.length; ++i) {
            if (clusterMark[i] != currClusterIndex) {
                ans = ans + clusterError[i];
            }
        }
        return ans;
    }

    private static double[][] copy(double[][] vectors) {
        double[][] ans = new double[vectors.length][vectors[0].length];
        for (int i =0; i < vectors.length; ++i) {
            System.arraycopy(vectors[i], 0, ans[i], 0, vectors[i].length);
        }
        return ans;
    }

    private static int[] copy(int[] vector) {
        int[] ans = new int[vector.length];
        System.arraycopy(vector, 0, ans, 0, vector.length);
        return ans;
    }

    private static double[] copy(double[] vector) {
        double[] ans = new double[vector.length];
        System.arraycopy(vector, 0, ans, 0, vector.length);
        return ans;
    }

    public static Assessment kMeansPlusPlus(double[][] vectors, int k) {
        return cluster(kMeansPlusPlusSeed(vectors, k), vectors);
    }

    public static Assessment cluster(double[][] initCentroids, double[][] vectors) {
        Assessment assessment = new Assessment();
        assessment.centroids = initCentroids;
        assessment.clusterMark = new int[vectors.length];
        assessment.clusterError = calculateClusterError(vectors, assessment.centroids, assessment.clusterMark);
        boolean clusterChanged = true;
        while (clusterChanged) {
            clusterChanged = false;
            for (int i = 0; i < vectors.length; ++i) {
                int minIndex = findNearestCentroidIndex(i, vectors, assessment.centroids);
                if (assessment.clusterMark[i] != minIndex) {
                    clusterChanged = true;
                    assessment.clusterMark[i] = minIndex;
                    assessment.clusterError[i] = Math.pow(distance(vectors[i],
                            assessment.centroids[minIndex]), 2);
                    //assessment.clusterError[i] = Math.pow(distance(vectors, i,
                    //        assessment.centroids[minIndex]), 2);
                }
            }
            assessment.centroids = newCentroids(vectors, initCentroids.length, assessment.clusterMark);
        }
        return assessment;
    }

    public static double[][] kMeansPlusPlusSeed(double[][] vectors, int k) {
        if (vectors.length == 1) {
            return vectors;
        }
        double[][] centroids = new double[k][vectors[0].length];
        int a = -1;
        while (a < 0 || a >= vectors.length) {
            a = (int) (Math.random() * vectors.length);
        }
        System.arraycopy(vectors[a], 0, centroids[0], 0, vectors[a].length);
        for (int x = 1; x < k; ++x) {
            double[] D = new double[vectors.length];
            double sum = 0;
            for (int i = 0; i < vectors.length; ++i) {
                D[i] = findNearestCentroidDistance(i, vectors, centroids);
                sum = sum + D[i];
            }
            double random = sum * Math.random();
            int i = -1;
            while (random >= 0) {
                ++i;
                random = random - D[i];
            }
            System.arraycopy(vectors[i], 0, centroids[x], 0, vectors[i].length);
        }
        return centroids;
    }

    public static double[] newCentroid(double[][] vectors, int[] clusterMark, int centroidIndex) {
        double[] centroid = new double[vectors[0].length];
        int n = 0;
        for (int i = 0; i < clusterMark.length; ++i) {
            if (clusterMark[i] == centroidIndex) {
                for (int j = 0; j < centroid.length; ++j) {
                    centroid[j] = centroid[j] + vectors[i][j];
                }
                ++n;
            }
        }
        if (n != 0) {
            for (int i = 0; i < centroid.length; ++i) {
                centroid[i] = centroid[i] / n;
            }
        }
        return centroid;
    }

    public static double[][] newCentroids(double[][] vectors, int k, int[] mark) {
        double[][] centroids = new double[k][vectors[0].length];
        for (int i = 0; i < k; ++i) {
            centroids[i] = newCentroid(vectors, mark, i);
        }
        return centroids;
    }

    private static double random(double max, double min) {
        return min + (max - min) * Math.random();
    }

    public static double[][] randomSeed(double[][] vectors, int k) {
        int M = vectors[0].length;
        double[][] ans = new double[k][M];
        for (int j = 0; j < M; ++j) {
            double max = -Double.MAX_VALUE;
            double min = Double.MAX_VALUE;
            for (double[] vector : vectors) {
                if (max < vector[j]) {
                    max = vector[j];
                }
                if (min > vector[j]) {
                    min = vector[j];
                }
            }
            for (int i = 0; i < k; ++i) {
                ans[i][j] = random(max, min);
            }
        }
        return ans;
    }

    public static Assessment kMeansClassical(double[][] vectors, int k) {
        return cluster(randomSeed(vectors, k), vectors);
    }


    /*public static int findNearestCentroidIndex(int k, double[][] vectors, double[][] centroids) {
        Geodesic geodesic = new Geodesic(vectors);
        return geodesic.findNearestCentroidIndex(k, centroids);
    }

    public static double findNearestCentroidDistance(int k, double[][] vectors, double[][] centroids) {
        Geodesic geodesic = new Geodesic(vectors);
        return geodesic.findNearestCentroidDistance(k, centroids);
    }

    public static double distance(int i, double[][] vectors, double[] centroid) {
        Geodesic geodesic = new Geodesic(vectors);
        return geodesic.geodesicDistance(i, centroid);
    }

    public static double distance(double[][] vectors, int k, double[] b) {
        Geodesic geodesic = new Geodesic(vectors);
        return geodesic.geodesicDistance(k, b);
    }*/

    public static int findNearestCentroidIndex(int k, double[][] vectors, double[][] centroids) {
        double min = Double.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < centroids.length; ++i) {
            double temp = distance(k, vectors, centroids[i]);
            if (min > temp) {
                min = temp;
                index = i;
            }
        }
        return index;
    }

    public static double findNearestCentroidDistance(int k, double[][] vectors, double[][] centroids) {
        double min = Double.MAX_VALUE;
        for (double[] centroid : centroids) {
            double temp = distance(k, vectors, centroid);
            if (min > temp) {
                min = temp;
            }
        }
        return min;
    }

    public static double distance(int i, double[][] vectors, double[] centroid) {
        double d = 0;
        for (int k = 0; k < centroid.length; ++k) {
            d = d + (centroid[k] - vectors[i][k]) * (centroid[k] - vectors[i][k]);
        }
        return Math.sqrt(d);
    }

    public static double distance(double[] a, double[] b) {
        double ans = 0;
        for (int i = 0; i < a.length; ++i) {
            ans = ans + (a[i] - b[i]) * (a[i] - b[i]);
        }
        return Math.sqrt(ans);
    }
}
