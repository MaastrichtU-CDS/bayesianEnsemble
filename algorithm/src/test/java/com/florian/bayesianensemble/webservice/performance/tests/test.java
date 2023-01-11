package com.florian.bayesianensemble.webservice.performance.tests;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import org.junit.jupiter.api.Test;

public class test {

    @Test
    public void test() throws Exception {
        printPerformance("", DiabetesTest.testPerformancePopulation(), true);
        printThreeWayPerformance("", DiabetesTest.testPerformanceThreeWayPopulationUnknown(0.05), true);
        printThreeWayPerformance("", DiabetesTest.testPerformanceThreeWayPopulation(), false);
        printPerformance("", DiabetesTest.testPerformancePopulation(), true);
        printPerformance("", DiabetesTest.testPerformancePopulationUnknown(0.05), false);
    }

    private void printHeader() {
        System.out.println(
                "Name; EnsembleAUC; LeftAUC; RightAUC; CentralAUC; VertiBayesAUC; EnsembleTime; MinTime; MaxTime; " +
                        "VertiBayesTime");
    }

    private void printPerformance(String name, Performance p, boolean printheader) {
        if (printheader) {
            printHeader();
        }
        System.out.println(
                name + "; " + p.getWeightedAUCEnsemble() + "; " + p.getWeightedAUCLeft() + "; " + p.getWeightedAUCRight()
                        + "; " + p.getWeightedAUCCentral() + "; " + p.getVertibayesPerformance() + "; " + p.getAverageTime()
                        + "; " + p.getMinTime() + "; " + p.getMaxTime() + "; " + p.getVertibayesTime());
    }

    private void printThreeWayHeader() {
        System.out.println(
                "Name; EnsembleAUC; LeftAUC; RightAUC; CenterAUC; CentralAUC; VertiBayesAUC; EnsembleTime; MinTime; " +
                        "MaxTime; " +
                        "VertiBayesTime");
    }


    private void printThreeWayPerformance(String name, Performance p, boolean printheader) {
        if (printheader) {
            printThreeWayHeader();
        }
        System.out.println(
                name + "; " + p.getWeightedAUCEnsemble() + "; " + p.getWeightedAUCLeft() + "; " + p.getWeightedAUCRight()
                        + "; " + p.getWeightedAUCCenter() + "; " + p.getWeightedAUCCentral() + "; " + p.getVertibayesPerformance()
                        + "; " + p.getAverageTime() + "; " + p.getMinTime() + "; " + p.getMaxTime() + "; " + p.getVertibayesTime());
    }
}
