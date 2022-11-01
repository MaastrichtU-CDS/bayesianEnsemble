package com.florian.bayesianensemble.webservice.domain.internal;

import weka.classifiers.bayes.BayesNet;

import java.util.Map;

public class ValidateRequest {
    private Map<String, BayesNet> networks;
    private String target;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Map<String, BayesNet> getNetworks() {
        return networks;
    }

    public void setNetworks(Map<String, BayesNet> networks) {
        this.networks = networks;
    }
}
