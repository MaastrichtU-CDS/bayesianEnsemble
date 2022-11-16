package com.florian.bayesianensemble.webservice.domain.internal;

import com.florian.nscalarproduct.encryption.PublicPaillierKey;

import java.util.Map;

public class ClassifyRequest {
    private Map<String, String> networks;
    private String target;
    private int precision;
    private PublicPaillierKey key;

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

    public PublicPaillierKey getKey() {
        return key;
    }

    public void setKey(PublicPaillierKey key) {
        this.key = key;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }
}
