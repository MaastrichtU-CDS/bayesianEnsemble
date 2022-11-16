package com.florian.bayesianensemble.webservice.domain;

import com.florian.vertibayes.webservice.domain.external.WebNode;

import java.util.List;
import java.util.Map;

public class CreateEnsembleRequest {
    private String target;
    private Map<String, List<WebNode>> networks;
    private List<WebNode> binned;
    private boolean hybrid;
    private int minPercentage;

    private int folds = 1;
    private static final int MIN_FOLDS = 1;
    private static final int MAX_FOLDS = 10;

    public void setNetworks(
            Map<String, List<WebNode>> networks) {
        this.networks = networks;
    }

    public Map<String, List<WebNode>> getNetworks() {
        return networks;
    }

    public int getFolds() {
        return folds;
    }


    public int getMinPercentage() {
        return minPercentage;
    }

    public void setMinPercentage(int minPercentage) {
        this.minPercentage = minPercentage;
    }

    public void setFolds(int folds) {
        if (folds > MAX_FOLDS) {
            // max folds = 10
            this.folds = MAX_FOLDS;
        } else if (folds < MIN_FOLDS) {
            this.folds = MIN_FOLDS;
        } else {
            this.folds = folds;
        }
    }

    public boolean isHybrid() {
        return hybrid;
    }

    public void setHybrid(boolean hybrid) {
        this.hybrid = hybrid;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<WebNode> getBinned() {
        return binned;
    }

    public void setBinned(List<WebNode> binned) {
        this.binned = binned;
    }
}
