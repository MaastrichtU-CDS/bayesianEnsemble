package com.florian.bayesianensemble.webservice.performance.tests;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceTestBase;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AsiaTest {
    private static final String SOURCE = "resources/Experiments/Asia/Asia10kWeka.arff";
    private static final String TARGET = "lung";
    private static final int FOLDS = 10;
    private static final int ROUNDS = 10;

    public static Performance testPerformanceAutomatic() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        assertEquals(p.getWeightedAUCEnsemble(), p.getWeightedAUCCentral(), 0.1);
        return p;
    }

    public static Performance testPerformanceManual() throws Exception {
        //there is no logical way to split this dataset, so don't use this
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.manualSplit(leftManual(), rightManual());
        assertEquals(p.getWeightedAUCEnsemble(), 0.90, 0.1);
        return p;
    }

    private static Set<String> leftManual() {
        Set<String> left = new HashSet<>();
        left.add("asia");
        left.add("tub");
        left.add("smoke");
        left.add("bronc");
        left.add("either");
        left.add("xray");
        left.add("dysp");
        left.add("lung");
        return left;
    }

    private static Set<String> rightManual() {
        Set<String> right = new HashSet<>();
        return right;
    }
}

