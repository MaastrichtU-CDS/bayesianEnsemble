package com.florian.bayesianensemble.webservice.domain.internal;

public class Probability {
    private double[] probability;
    private boolean locallyPresent;

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
}
