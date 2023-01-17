package com.florian.bayesianensemble.webservice.performance.tests;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceTestBase;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceThreeWayTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SmallDiabetesTest {
    private static final String SOURCE = "resources/Experiments/Diabetes/diabetesWeka.arff";
    private static final String TARGET = "Outcome";
    private static final int FOLDS = 2;
    private static final int ROUNDS = 2;


    public static Performance testPerformanceThreeWayHybridUnknown(double treshold) throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.hybridSplit();
        return p;
    }

    public static Performance testPerformanceThreeWayHybrid() throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.hybridSplit();
        return p;
    }

    public static Performance testPerformanceAutomaticUnknown(double treshold) throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        return p;
    }

    public static Performance testPerformanceThreeWayAutomaticUnknown(double treshold) throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        return p;
    }

    public static Performance testPerformanceThreeWayPopulation() throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.populationSplit();
        return p;
    }

    public static Performance testPerformancePopulation() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.populationSplit();
        assertEquals(p.getWeightedAUCEnsemble(), 0.78, 0.05);
        assertEquals(p.getWeightedAUCCentral(), 0.78, 0.05);
        assertEquals(p.getVertibayesPerformance(), 0.78, 0.05);
        return p;
    }

    public static Performance testPerformanceAutomatic() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        return p;
    }
}

