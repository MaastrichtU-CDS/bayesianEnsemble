package com.florian.bayesianensemble.webservice.performance;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.tests.*;
import org.junit.jupiter.api.Test;

public class PerformanceTest {
    public static final boolean SMALLTEST = false;

    @Test
    public void testPerformance() throws Exception {
        printPerformance("smallIris automatic", SmallIrisTest.testPerformanceAutomatic());
        printPerformance("smallIris manual", SmallIrisTest.testPerformanceManual());

        if (!SMALLTEST) {
            printPerformance("Iris automatic", IrisTest.testPerformanceAutomatic());
            printPerformance("Iris manual", IrisTest.testPerformanceManual());
            printPerformance("Autism automatic", AutismTest.testPerformanceAutomatic());
            printPerformance("Autism manual", AutismTest.testPerformanceManual());
            printPerformance("Mushroom automatic", MushroomTest.testPerformanceAutomatic());
            printPerformance("Mushroom manual", MushroomTest.testPerformanceManual());
            printPerformance("Asia automatic", AsiaTest.testPerformanceAutomatic());
            printPerformance("Alarm automatic", AlarmTest.testPerformanceAutomatic());
            printPerformance("Diabetes automatic", DiabetesTest.testPerformanceAutomatic());
        }
    }

    private void printPerformance(String name, Performance p) {
        System.out.println(name);
        System.out.println(
                "EnsembleAUC; LeftAUC; RightAUC; CentralAUC; VertiBayesAUC; EnsembleTime; MinTime; MaxTime; " +
                        "VertiBayesTime");
        System.out.println(
                p.getWeightedAUCEnsemble() + "; " + p.getWeightedAUCLeft() + "; " + p.getWeightedAUCRight() + "; "
                        + p.getWeightedAUCCentral() + "; " + p.getVertibayesPerformance() + "; " + p.getAverageTime()
                        + "; " + p.getMinTime() + "; " + p.getMaxTime() + "; " + p.getVertibayesTime());
    }
}
