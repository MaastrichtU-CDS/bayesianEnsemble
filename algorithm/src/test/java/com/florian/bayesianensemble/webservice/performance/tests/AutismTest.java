package com.florian.bayesianensemble.webservice.performance.tests;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceTestBase;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AutismTest {
    private static final String SOURCE = "resources/Experiments/autism/autism.arff";
    private static final String TARGET = "Class/ASD";
    private static final int FOLDS = 10;
    private static final int ROUNDS = 10;

    public static Performance testPerformanceAutomatic() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        assertEquals(p.getWeightedAUCEnsemble(), p.getWeightedAUCCentral(), 0.1);
        return p;
    }

    public static Performance testPerformanceManual() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.manualSplit(leftManual(), rightManual());
        assertEquals(p.getWeightedAUCEnsemble(), 0.90, 0.1);
        return p;
    }

    private static Set<String> leftManual() {
        Set<String> left = new HashSet<>();
        left.add("A1_Score");
        left.add("A2_Score");
        left.add("A3_Score");
        left.add("A4_Score");
        left.add("A5_Score");
        left.add("A6_Score");
        left.add("A7_Score");
        left.add("A8_Score");
        left.add("A9_Score");
        left.add("A10_Score");
        return left;
    }

    private static Set<String> rightManual() {
        Set<String> right = new HashSet<>();
        right.add("age");
        right.add("gender");
        right.add("ethnicity");
        right.add("jundice");
        right.add("austim");
        right.add("contry_of_res");
        right.add("used_app_before");
        right.add("age_desc");
        right.add("relation");
        right.add("Class/ASD");
        return right;
    }
}

