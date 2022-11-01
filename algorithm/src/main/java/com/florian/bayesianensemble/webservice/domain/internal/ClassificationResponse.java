package com.florian.bayesianensemble.webservice.domain.internal;

import java.util.ArrayList;
import java.util.List;

public class ClassificationResponse {
    private List<Probability> probabilities = new ArrayList<>();

    public List<Probability> getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(List<Probability> probabilities) {
        this.probabilities = probabilities;
    }
}
