package com.florian.bayesianensemble.webservice.performance.tests;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceTestBase;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceThreeWayTestBase;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SmallIrisTest {
    private static final String SOURCE = "resources/Experiments/iris/iris.arff";
    private static final String TARGET = "label";
    private static final int FOLDS = 2;
    private static final int ROUNDS = 2;

    public static Performance testPerformanceAutomaticUnknown(double treshold) throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        if (treshold == 0.05) {
            assertEquals(p.getWeightedAUCEnsemble(), 0.9, 0.1);
        } else if (treshold == 0.1) {
            assertEquals(p.getWeightedAUCEnsemble(), 0.7, 0.1);
        } else if (treshold == 0.3) {
            assertEquals(p.getWeightedAUCEnsemble(), 0.6, 0.1);
        }
        return p;
    }

    public static Performance testPerformanceThreeWayAutomaticUnknown(double treshold) throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        assertEquals(p.getWeightedAUCEnsemble(), p.getWeightedAUCCentral(), 0.1);
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

    public static Performance testPerformanceAutomatic() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        assertEquals(p.getWeightedAUCEnsemble(), p.getWeightedAUCCentral(), 0.15);
        return p;
    }

    public static Performance testPerformanceManual() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.manualSplit(leftManual(), rightManual());
        assertEquals(p.getWeightedAUCEnsemble(), 0.93, 0.075);
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

