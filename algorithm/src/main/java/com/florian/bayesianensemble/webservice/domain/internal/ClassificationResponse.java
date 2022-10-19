package com.florian.bayesianensemble.webservice.domain.internal;

import java.util.ArrayList;
import java.util.List;

public class ClassificationResponse {
    List<double[]> probabilities = new ArrayList<>();

    public List<double[]> getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(List<double[]> probabilities) {
        this.probabilities = probabilities;
    }
}
