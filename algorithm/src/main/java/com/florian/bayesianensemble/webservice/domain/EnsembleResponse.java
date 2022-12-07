package com.florian.bayesianensemble.webservice.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnsembleResponse {
    private List<String> networks = new ArrayList<>();
    private Map<String, Double> aucs = new HashMap<>();
    private double weightedAUC;

    public double getWeightedAUC() {
        return weightedAUC;
    }

    public void setWeightedAUC(double weightedAUC) {
        this.weightedAUC = weightedAUC;
    }

    public List<String> getNetworks() {
        return networks;
    }

    public void setNetworks(List<String> networks) {
        this.networks = networks;
    }

    public Map<String, Double> getAucs() {
        return aucs;
    }

    public void setAucs(Map<String, Double> aucs) {
        this.aucs = aucs;
    }
}
