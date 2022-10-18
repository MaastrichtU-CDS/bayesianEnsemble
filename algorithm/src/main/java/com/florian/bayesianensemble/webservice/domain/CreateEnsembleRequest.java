package com.florian.bayesianensemble.webservice.domain;

import com.florian.vertibayes.webservice.domain.external.WebNode;

import java.util.List;

public class CreateEnsembleRequest {
    private String target;
    private List<WebNode> networks;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<WebNode> getNetworks() {
        return networks;
    }

    public void setNetworks(List<WebNode> networks) {
        this.networks = networks;
    }
}
