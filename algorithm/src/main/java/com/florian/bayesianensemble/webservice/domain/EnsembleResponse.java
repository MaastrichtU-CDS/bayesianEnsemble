package com.florian.bayesianensemble.webservice.domain;

import com.florian.vertibayes.webservice.domain.external.ExpectationMaximizationResponse;

import java.util.ArrayList;
import java.util.List;

public class EnsembleResponse {
    List<ExpectationMaximizationResponse> networks = new ArrayList<>();

    public List<ExpectationMaximizationResponse> getNetworks() {
        return networks;
    }

    public void setNetworks(
            List<ExpectationMaximizationResponse> networks) {
        this.networks = networks;
    }
}
