package com.florian.bayesianensemble.webservice.performance;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.tests.*;
import org.junit.jupiter.api.Test;

public class PerformanceTest {
    public static final boolean SMALLTEST = false;

    @Test
    public void testPerformance() throws Exception {
        printPerformance("smallIris automatic", SmallIrisTest.testPerformanceAutomatic(), true);
        printPerformance("smallIris manual", SmallIrisTest.testPerformanceManual(), false);

        if (!SMALLTEST) {
            //two-way split
            printPerformance("Diabetes automatic", DiabetesTest.testPerformanceAutomatic(), false);
            printPerformance("Iris automatic", IrisTest.testPerformanceAutomatic(), false);
            printPerformance("Iris manual", IrisTest.testPerformanceManual(), false);
            printPerformance("Autism automatic", AutismTest.testPerformanceAutomatic(), false);
            printPerformance("Autism manual", AutismTest.testPerformanceManual(), false);
            printPerformance("Mushroom automatic", MushroomTest.testPerformanceAutomatic(), false);
            printPerformance("Mushroom manual", MushroomTest.testPerformanceManual(), false);
            printPerformance("Asia automatic", AsiaTest.testPerformanceAutomatic(), false);
            printPerformance("Alarm automatic", AlarmTest.testPerformanceAutomatic(), false);

            //three way split
            printThreeWayPerformance("Diabetes automatic threeways", DiabetesTest.testPerformanceThreeWayAutomatic(),
                                     true);
            printThreeWayPerformance("Asia automatic threeways", AsiaTest.testPerformanceThreeWayAutomatic(), false);
            printThreeWayPerformance("Autism automatic threeways", AutismTest.testPerformanceThreeWayAutomatic(),
                                     false);
            printThreeWayPerformance("Autism manual threeways", AutismTest.testPerformanceThreeWayManual(), false);
            printThreeWayPerformance("Mushroom automatic threeways", MushroomTest.testPerformanceThreeWayAutomatic(),
                                     false);
            printThreeWayPerformance("Mushroom manual threeways", MushroomTest.testPerformanceThreeWayManual(), false);
            printThreeWayPerformance("Alarm automatic threeways", AlarmTest.testPerformanceThreeWayAutomatic(), false);
        }
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
