package com.florian.bayesianensemble.webservice.domain.internal;

import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

public class ValidateResponse {
    Map<String, Double> aucs = new HashedMap();

    public Map<String, Double> getAucs() {
        return aucs;
    }

    public void setAucs(Map<String, Double> aucs) {
        this.aucs = aucs;
    }
}
