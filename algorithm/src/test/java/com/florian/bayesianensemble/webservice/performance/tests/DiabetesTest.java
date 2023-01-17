package com.florian.bayesianensemble.webservice.performance.tests;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceTestBase;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceThreeWayTestBase;


public class DiabetesTest {
    private static final String SOURCE = "resources/Experiments/Diabetes/diabetesWeka.arff";
    private static final String TARGET = "Outcome";
    private static final int FOLDS = 10;
    private static final int ROUNDS = 10;

    public static Performance testPerformanceThreeWayHybridUnknown(double treshold, boolean hybrid) throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.hybridSplit(hybrid);
        return p;
    }

    public static Performance testPerformanceThreeWayHybrid(boolean hybrid) throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.hybridSplit(hybrid);
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

    public static Performance testPerformanceAutomatic() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        return p;
    }

    public static Performance testPerformancePopulationUnknown(double treshold) throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.populationSplit();
        return p;
    }

    public static Performance testPerformanceThreeWayPopulationUnknown(double treshold) throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.populationSplit();
        return p;
    }

    public static Performance testPerformancePopulation() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.populationSplit();
        return p;
    }

    public static Performance testPerformanceThreeWayPopulation() throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.populationSplit();
        return p;
    }

    public static Performance testPerformanceThreeWayAutomatic() throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        return p;
    }

}

