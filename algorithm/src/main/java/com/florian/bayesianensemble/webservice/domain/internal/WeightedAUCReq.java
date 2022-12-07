package com.florian.bayesianensemble.webservice.domain.internal;

import com.florian.nscalarproduct.encryption.PublicPaillierKey;

public class WeightedAUCReq {
    private String attributeName;
    private String attributeValue;
    private double auc;
    private PublicPaillierKey key;
    private int precision;

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public PublicPaillierKey getKey() {
        return key;
    }

    public void setKey(PublicPaillierKey key) {
        this.key = key;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public double getAuc() {
        return auc;
    }

    public void setAuc(double auc) {
        this.auc = auc;
    }
}
