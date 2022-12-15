package com.florian.bayesianensemble.webservice.performance.tests;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceTestBase;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceThreeWayTestBase;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AutismTest {
    private static final String SOURCE = "resources/Experiments/autism/autism.arff";
    private static final String TARGET = "Class/ASD";
    private static final int FOLDS = 10;
    private static final int ROUNDS = 10;

    public static Performance testPerformanceAutomaticUnknown(double treshold) throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        assertEquals(p.getWeightedAUCEnsemble(), p.getWeightedAUCCentral(), 0.3);
        return p;
    }

    public static Performance testPerformanceThreeWayAutomaticUnknown(double treshold) throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        assertEquals(p.getWeightedAUCEnsemble(), p.getWeightedAUCCentral(), 0.3);
        return p;
    }

    public static Performance testPerformanceManualUnknown(double treshold) throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.manualSplit(leftManual(), rightManual());
        if (treshold == 0.05) {
            assertEquals(p.getWeightedAUCEnsemble(), 0.9, 0.1);
        } else if (treshold == 0.1) {
            assertEquals(p.getWeightedAUCEnsemble(), 0.7, 0.1);
        } else if (treshold == 0.3) {
            assertEquals(p.getWeightedAUCEnsemble(), 0.6, 0.1);
        }
        return p;
    }

    public static Performance testPerformanceThreeWayManualUnknown(double treshold) throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.manualSplit(leftManual(), rightThreeWayManual(), centerManual());
        assertEquals(p.getWeightedAUCEnsemble(), 0.90, 0.1);
        return p;
    }

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

    public static Performance testPerformanceThreeWayAutomatic() throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        assertEquals(p.getWeightedAUCEnsemble(), p.getWeightedAUCCentral(), 0.15);
        return p;
    }

    public static Performance testPerformanceThreeWayManual() throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.manualSplit(leftManual(), rightThreeWayManual(), centerManual());
        assertEquals(p.getWeightedAUCEnsemble(), 0.90, 0.1);
        return p;
    }

    private static Set<String> rightThreeWayManual() {
        Set<String> right = new HashSet<>();
        right.add("age");
        right.add("gender");
        right.add("ethnicity");
        right.add("jundice");
        right.add("austim");
        return right;
    }

    private static Set<String> centerManual() {
        Set<String> center = new HashSet<>();
        center.add("contry_of_res");
        center.add("used_app_before");
        center.add("age_desc");
        center.add("relation");
        center.add("Class/ASD");
        return center;
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

