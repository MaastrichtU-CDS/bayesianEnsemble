package com.florian.bayesianensemble.webservice.domain.internal;

import java.math.BigInteger;

public class DecryptionRequest {
    private String name;
    private BigInteger value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }
}
