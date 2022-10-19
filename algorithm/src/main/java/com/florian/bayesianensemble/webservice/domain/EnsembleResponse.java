package com.florian.bayesianensemble.webservice.domain;

import com.florian.vertibayes.webservice.domain.external.ExpectationMaximizationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnsembleResponse {
    List<ExpectationMaximizationResponse> networks = new ArrayList<>();
    Map<String, Double> aucs = new HashMap<>();

    public Map<String, Double> getAucs() {
        return aucs;
    }

    public void setAucs(Map<String, Double> aucs) {
        this.aucs = aucs;
    }

    public List<ExpectationMaximizationResponse> getNetworks() {
        return networks;
    }

    public void setNetworks(
            List<ExpectationMaximizationResponse> networks) {
        this.networks = networks;
    }
}
