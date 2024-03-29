package com.florian.bayesianensemble.webservice.domain.internal;

import java.util.List;
import java.util.Map;

public class ValidateRequest {
    private Map<String, String> networks;
    private String target;
    private List<double[]> probabilities;

    public Map<String, String> getNetworks() {
        return networks;
    }

    public void setNetworks(Map<String, String> networks) {
        this.networks = networks;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<double[]> getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(List<double[]> probabilities) {
        this.probabilities = probabilities;
    }
}
