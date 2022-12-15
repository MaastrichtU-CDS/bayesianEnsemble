package com.florian.bayesianensemble.webservice.performance.tests;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceTestBase;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceThreeWayTestBase;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiabetesTest {
    private static final String SOURCE = "resources/Experiments/Diabetes/diabetesWeka.arff";
    private static final String TARGET = "Outcome";
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

    public static Performance testPerformanceThreeWayAutomatic() throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        assertEquals(p.getWeightedAUCEnsemble(), p.getWeightedAUCCentral(), 0.1);
        return p;
    }

    private static Set<String> leftManual() {
        Set<String> left = new HashSet<>();
        left.add("Pregnancies");
        left.add("Glucose");
        left.add("BloodPressure");
        left.add("SkinThickness");
        left.add("Insulin");
        left.add("BMI");
        left.add("DiabetesPedigreeFunction");
        left.add("Age");
        left.add("Outcome");
        return left;
    }

    private static Set<String> rightManual() {
        Set<String> right = new HashSet<>();
        return right;
    }
}

