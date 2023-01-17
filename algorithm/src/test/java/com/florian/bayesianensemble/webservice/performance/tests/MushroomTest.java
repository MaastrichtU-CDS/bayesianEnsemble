package com.florian.bayesianensemble.webservice.performance.tests;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceTestBase;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceThreeWayTestBase;

import java.util.HashSet;
import java.util.Set;

public class MushroomTest {
    private static final String SOURCE = "resources/Experiments/Mushrooms/agaricus-lepiota.arff";
    private static final String TARGET = "class";
    private static final int FOLDS = 10;
    private static final int ROUNDS = 1;

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
        Performance p = test.manualSplit(leftThreeWayManual(), rightManual(), centerManual());
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

    public static Performance testPerformanceThreeWayManual() throws Exception {
        PerformanceThreeWayTestBase test = new PerformanceThreeWayTestBase(SOURCE, TARGET, ROUNDS, FOLDS);
        Performance p = test.manualSplit(leftThreeWayManual(), rightManual(), centerManual());
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

    private static Set<String> leftThreeWayManual() {
        Set<String> left = new HashSet<>();
        left.add("cap-shape");
        left.add("cap-surface");
        left.add("cap-color");
        left.add("gill-attachment");
        left.add("gill-spacing");
        left.add("gill-size");
        left.add("gill-color");
        return left;
    }

    private static Set<String> centerManual() {
        Set<String> left = new HashSet<>();
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

