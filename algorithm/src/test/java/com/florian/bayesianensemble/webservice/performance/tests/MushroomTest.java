package com.florian.bayesianensemble.webservice.performance.tests;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceTestBase;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MushroomTest {
    private static final String SOURCE = "resources/Experiments/Mushrooms/agaricus-lepiota.arff";
    private static final String TARGET = "class";
    private static final int FOLDS = 10;
    private static final int ROUNDS = 10;

    public static Performance testPerformanceAutomatic() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        assertEquals(p.getWeightedAUCEnsemble(), p.getWeightedAUCCentral(), 0.05);
        return p;
    }

    public static Performance testPerformanceManual() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.manualSplit(leftManual(), rightManual());
        assertEquals(p.getWeightedAUCEnsemble(), 0.90, 0.075);
        return p;
    }

    private static Set<String> leftManual() {
        Set<String> left = new HashSet<>();
        left.add("cap-shape");
        left.add("cap-surface");
        left.add("cap-color");
        left.add("gill-attachment");
        left.add("gill-spacing");
        left.add("gill-size");
        left.add("gill-color");
        left.add("stalk-shape");
        left.add("stalk-root");
        left.add("stalk-surface-above-ring");
        left.add("stalk-surface-below-ring");
        left.add("stalk-color-above-ring");
        left.add("stalk-color-below-ring");
        left.add("veil-type");
        left.add("veil-color");
        return left;
    }

    private static Set<String> rightManual() {
        Set<String> right = new HashSet<>();
        right.add("bruises");
        right.add("odor");
        right.add("ring-number");
        right.add("ring-type");
        right.add("spore-print-color");
        right.add("population");
        right.add("habitat");
        right.add("class");
        return right;
    }
}

