package com.florian.bayesianensemble.webservice.performance.base;

import java.util.HashMap;
import java.util.Map;

public class Performance {
    private Map<String, Double> leftAuc = new HashMap<>();
    private Map<String, Double> rightAuc = new HashMap<>();
    private Map<String, Double> ensembleAuc = new HashMap<>();
    private Map<String, Double> centralAuc = new HashMap<>();


    private double weightedAUCEnsemble;
    private double weightedAUCLeft;
    private double weightedAUCRight;
    private double weightedAUCCentral;
    private double vertibayesPerformance;

    private long averageTime;
    private long vertibayesTime;

    public double getVertibayesPerformance() {
        return vertibayesPerformance;
    }

    public void setVertibayesPerformance(double vertibayesPerformance) {
        this.vertibayesPerformance = vertibayesPerformance;
    }

    public double getWeightedAUCEnsemble() {
        return weightedAUCEnsemble;
    }

    public void setWeightedAUCEnsemble(double weightedAUCEnsemble) {
        this.weightedAUCEnsemble = weightedAUCEnsemble;
    }

    public double getWeightedAUCLeft() {
        return weightedAUCLeft;
    }

    public void setWeightedAUCLeft(double weightedAUCLeft) {
        this.weightedAUCLeft = weightedAUCLeft;
    }

    public double getWeightedAUCRight() {
        return weightedAUCRight;
    }

    public void setWeightedAUCRight(double weightedAUCRight) {
        this.weightedAUCRight = weightedAUCRight;
    }

    public double getWeightedAUCCentral() {
        return weightedAUCCentral;
    }

    public void setWeightedAUCCentral(double weightedAUCCentral) {
        this.weightedAUCCentral = weightedAUCCentral;
    }

    public long getVertibayesTime() {
        return vertibayesTime;
    }

    public void setVertibayesTime(long vertibayesTime) {
        this.vertibayesTime = vertibayesTime;
    }

    public long getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(long averageTime) {
        this.averageTime = averageTime;
    }

    public Map<String, Double> getCentralAuc() {
        return centralAuc;
    }

    public void setCentralAuc(Map<String, Double> centralAuc) {
        this.centralAuc = centralAuc;
    }

    public Map<String, Double> getLeftAuc() {
        return leftAuc;
    }

    public void setLeftAuc(Map<String, Double> leftAuc) {
        this.leftAuc = leftAuc;
    }

    public Map<String, Double> getRightAuc() {
        return rightAuc;
    }

    public void setRightAuc(Map<String, Double> rightAuc) {
        this.rightAuc = rightAuc;
    }

    public Map<String, Double> getEnsembleAuc() {
        return ensembleAuc;
    }

    public void setEnsembleAuc(Map<String, Double> ensembleAuc) {
        this.ensembleAuc = ensembleAuc;
    }

    public void addLeftAuc(Map<String, Double> leftAuc) {
        for (String key : leftAuc.keySet()) {
            if (this.leftAuc.get(key) != null) {
                this.leftAuc.put(key, this.leftAuc.get(key) + leftAuc.get(key));
            } else {
                this.leftAuc.put(key, leftAuc.get(key));
            }
        }
    }

    public void addRightAuc(Map<String, Double> rightAuc) {
        for (String key : rightAuc.keySet()) {
            if (this.rightAuc.get(key) != null) {
                this.rightAuc.put(key, this.rightAuc.get(key) + rightAuc.get(key));
            } else {
                this.rightAuc.put(key, rightAuc.get(key));
            }
        }
    }

    public void normalize(int folds) {
        for (String k : rightAuc.keySet()) {
            this.rightAuc.put(k, this.rightAuc.get(k) / folds);
        }
        for (String k : leftAuc.keySet()) {
            this.leftAuc.put(k, this.leftAuc.get(k) / folds);
        }
    }
}
