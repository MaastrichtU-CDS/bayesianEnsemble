package com.florian.bayesianensemble.webservice.domain.internal;

public class Probability {
    private double[] probability;
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
}
