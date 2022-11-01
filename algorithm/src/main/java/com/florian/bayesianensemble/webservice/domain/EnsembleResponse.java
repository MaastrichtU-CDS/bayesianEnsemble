package com.florian.bayesianensemble.webservice.domain;

import com.florian.vertibayes.webservice.domain.external.WebNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnsembleResponse {
    private List<List<WebNode>> networks = new ArrayList<>();
    private Map<String, Double> aucs = new HashMap<>();
    private String openMarkov;

    public Map<String, Double> getAucs() {
        return aucs;
    }

    public void setAucs(Map<String, Double> aucs) {
        this.aucs = aucs;
    }

    public List<List<WebNode>> getNetworks() {
        return networks;
    }

    public void setNetworks(List<List<WebNode>> networks) {
        this.networks = networks;
    }

    public String getOpenMarkov() {
        return openMarkov;
    }

    public void setOpenMarkov(String openMarkov) {
        this.openMarkov = openMarkov;
    }
}
