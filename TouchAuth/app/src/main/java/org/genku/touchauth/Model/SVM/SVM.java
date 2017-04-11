package org.genku.touchauth.Model.SVM;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

/**
 * Created by genku on 4/9/2017.
 */

public class SVM {


    private svm_parameter param;
    private svm_model model;

    private svm_problem problem;

    public SVM() {
        param = new svm_parameter();
        param.svm_type = svm_parameter.ONE_CLASS;
        param.kernel_type = svm_parameter.LINEAR;
        param.cache_size = 100;
        param.eps = 0.00001;
        param.C = 1;
    }

    public void train(double[][] vectors, double[] labels) {

        int rows = vectors.length;
        int cols = vectors[0].length;

        // SVM Problem
        svm_node[][] data = new svm_node[rows][cols];
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                data[i][j] = new svm_node();
                data[i][j].index = j + 1;
                data[i][j].value = vectors[i][j];
            }
        }
        svm_problem problem = new svm_problem();
        problem.l = rows;
        problem.x = data;
        problem.y = labels;

        model = svm.svm_train(problem, param);
    }

    public double predict(double[] vector) {

        int cols = vector.length;

        svm_node[] data = new svm_node[cols];
        for (int i = 0; i < cols; ++i) {
            data[i] = new svm_node();
            data[i].index = i + 1;
            data[i].value = vector[i];
        }

        double[] probs = new double[2];
        double label = svm.svm_predict_probability(model, data, probs);

        return label;
    }
}
