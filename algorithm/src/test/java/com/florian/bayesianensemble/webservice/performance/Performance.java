package com.florian.bayesianensemble.webservice.performance;

import java.util.Map;

public class Performance {
    private double leftAuc;
    private double rightAuc;
    private Map<String, Double> ensembleAuc;

    public double getLeftAuc() {
        return leftAuc;
    }

    public void setLeftAuc(double leftAuc) {
        this.leftAuc = leftAuc;
    }

    public double getRightAuc() {
        return rightAuc;
    }

    public void setRightAuc(double rightAuc) {
        this.rightAuc = rightAuc;
    }

    public Map<String, Double> getEnsembleAuc() {
        return ensembleAuc;
    }

    public void setEnsembleAuc(Map<String, Double> ensembleAuc) {
        this.ensembleAuc = ensembleAuc;
    }

    public void addLeftAuc(double leftAuc) {
        this.leftAuc += leftAuc;
    }

    public void addRightAuc(double rightAuc) {
        this.rightAuc += rightAuc;
    }

    public void normalize(int folds) {
        this.rightAuc /= folds;
        this.leftAuc /= folds;
    }
}
