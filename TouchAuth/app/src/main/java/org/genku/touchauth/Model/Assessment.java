package org.genku.touchauth.Model;

class Assessment {
    double[][] centroids;
    int[] clusterMark;
    double[] clusterError;

    public Assessment() {
        centroids = new double[0][];
        clusterMark = new int[0];
        clusterError = new double[0];
    }
}
