package com.florian.bayesianensemble.webservice.performance.base;

import java.util.HashMap;
import java.util.Map;

public class Performance {
    private Map<String, Double> leftAuc = new HashMap<>();
    private Map<String, Double> rightAuc = new HashMap<>();
    private Map<String, Double> ensembleAuc = new HashMap<>();
    private Map<String, Double> ensembleAucMin;
    private Map<String, Double> ensembleAucMax;

    private Map<String, Double> leftAucMin;
    private Map<String, Double> leftAucMax;

    private Map<String, Double> rightAucMin;
    private Map<String, Double> rightAucMax;

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
        if (leftAucMin == null) {
            leftAucMin = leftAuc;
        } else {
            if (compare(leftAuc, leftAucMin) < 0) {
                leftAucMin = leftAuc;
            }
        }

        if (leftAucMax == null) {
            leftAucMax = leftAuc;
        } else {
            if (compare(leftAuc, leftAucMax) > 0) {
                leftAucMax = leftAuc;
            }
        }


        for (String key : leftAuc.keySet()) {
            if (this.leftAuc.get(key) != null) {
                this.leftAuc.put(key, this.leftAuc.get(key) + leftAuc.get(key));
            } else {
                this.leftAuc.put(key, leftAuc.get(key));
            }
        }
    }

    public void addRightAuc(Map<String, Double> rightAuc) {
        if (rightAucMin == null) {
            rightAucMin = rightAuc;
        } else {
            if (compare(rightAuc, rightAucMin) < 0) {
                rightAucMin = rightAuc;
            }
        }

        if (rightAucMax == null) {
            rightAucMax = rightAuc;
        } else {
            if (compare(rightAuc, rightAucMax) > 0) {
                rightAucMax = rightAuc;
            }
        }

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

    public Map<String, Double> getLeftAucMin() {
        return leftAucMin;
    }

    public void setLeftAucMin(Map<String, Double> leftAucMin) {
        this.leftAucMin = leftAucMin;
    }

    public Map<String, Double> getLeftAucMax() {
        return leftAucMax;
    }

    public void setLeftAucMax(Map<String, Double> leftAucMax) {
        this.leftAucMax = leftAucMax;
    }

    public Map<String, Double> getRightAucMin() {
        return rightAucMin;
    }

    public void setRightAucMin(Map<String, Double> rightAucMin) {
        this.rightAucMin = rightAucMin;
    }

    public Map<String, Double> getRightAucMax() {
        return rightAucMax;
    }

    public void setRightAucMax(Map<String, Double> rightAucMax) {
        this.rightAucMax = rightAucMax;
    }

    public Map<String, Double> getEnsembleAucMin() {
        return ensembleAucMin;
    }

    public void setEnsembleAucMin(Map<String, Double> ensembleAucMin) {
        this.ensembleAucMin = ensembleAucMin;
    }

    public Map<String, Double> getEnsembleAucMax() {
        return ensembleAucMax;
    }

    public void setEnsembleAucMax(Map<String, Double> ensembleAucMax) {
        this.ensembleAucMax = ensembleAucMax;
    }

    public static int compare(Map<String, Double> first, Map<String, Double> second) {
        double left = 0;
        double right = 0;
        for (String key : first.keySet()) {
            left += first.get(key);
            right += second.get(key);
        }
        if (left < right) {
            return -1;
        } else if (left == right) {
            return 0;
        } else {
            return 1;
        }
    }
}
