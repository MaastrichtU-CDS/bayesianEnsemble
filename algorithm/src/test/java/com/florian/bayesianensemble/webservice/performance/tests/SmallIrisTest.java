package com.florian.bayesianensemble.webservice.performance.tests;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceTestBase;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SmallIrisTest {
    private static final String SOURCE = "resources/Experiments/iris/iris.arff";
    private static final String TARGET = "label";
    private static final int FOLDS = 2;
    private static final int ROUNDS = 2;

    public static Performance testPerformanceAutomatic() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        assertEquals(p.getWeightedAUCEnsemble(), p.getWeightedAUCCentral(), 0.15);
        return p;
    }

    public static Performance testPerformanceManual() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.manualSplit(leftManual(), rightManual());
        assertEquals(p.getWeightedAUCEnsemble(), p.getWeightedAUCCentral(), 0.075);
        return p;
    }

    private static Set<String> leftManual() {
        Set<String> left = new HashSet<>();
        left.add("sepallength");
        left.add("sepalwidth");
        return left;
    }

    private static Set<String> rightManual() {
        Set<String> right = new HashSet<>();
        right.add("petallength");
        right.add("petalwidth");
        right.add("label");
        return right;
    }
}

