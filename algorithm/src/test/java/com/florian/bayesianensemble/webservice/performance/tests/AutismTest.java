package com.florian.bayesianensemble.webservice.performance.tests;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceTestBase;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceThreeWayTestBase;

import java.util.HashSet;
import java.util.Set;


public class AutismTest {
    private static final String SOURCE = "resources/Experiments/autism/autism.arff";
    private static final String TARGET = "Class/ASD";
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

    public static Performance testPerformanceManualUnknown(double treshold) throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.manualSplit(leftManual(), rightManual());
        return p;
    }

    public static Performance testPerformanceThreeWayManualUnknown(double treshold) throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.manualSplit(leftManual(), rightThreeWayManual(), centerManual());
        return p;
    }

    public static Performance testPerformanceAutomatic() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        return p;
    }

    public static Performance testPerformanceManual() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.manualSplit(leftManual(), rightManual());
        return p;
    }

    public static Performance testPerformanceThreeWayAutomatic() throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.automaticSplit();
        return p;
    }

    public static Performance testPerformanceThreeWayManual() throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.manualSplit(leftManual(), rightThreeWayManual(), centerManual());
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

    public static Performance testPerformancePopulationBiassedUnknown(double treshold, double bias) throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.populationBiassedSplit(TARGET, bias);
        return p;
    }

    public static Performance testPerformancePopulationBiassed(double bias) throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.populationBiassedSplit(TARGET, bias);
        return p;
    }

    public static Performance testPerformancePopulationBiassedThreeway(double bias) throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.populationBiassedSplit(TARGET, bias);
        return p;
    }

    public static Performance testPerformanceThreeWayPopulationBiassedUnknown(double treshold, double bias)
            throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(
                SOURCE.replace(".arff", "_missing_" + String.valueOf(treshold).replace(".", "_") +
                        ".arff"), TARGET, ROUNDS, FOLDS);
        Performance p = test.populationBiassedSplit(TARGET, bias);
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

