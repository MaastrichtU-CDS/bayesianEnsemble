package com.florian.bayesianensemble.webservice.domain.internal;

import java.util.Map;

public class ClassifyRequest {
    private Map<String, String> networks;
    private String target;

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
}
