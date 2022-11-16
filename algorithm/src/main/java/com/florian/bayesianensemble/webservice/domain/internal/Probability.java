package com.florian.bayesianensemble.webservice.domain.internal;

import java.math.BigInteger;

public class Probability {
    private double[] probability;
    private BigInteger[] encryptedProbability;
    private boolean locallyPresent;
    private boolean isActive;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public double[] getProbability() {
        return probability;
    }

    public void setProbability(double[] probability) {
        this.probability = probability;
    }

    public boolean isLocallyPresent() {
        return locallyPresent;
    }

    public void setLocallyPresent(boolean locallyPresent) {
        this.locallyPresent = locallyPresent;
    }

    public BigInteger[] getEncryptedProbability() {
        return encryptedProbability;
    }

    public void setEncryptedProbability(BigInteger[] encryptedProbability) {
        this.encryptedProbability = encryptedProbability;
    }
}
