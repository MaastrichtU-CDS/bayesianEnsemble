package com.florian.bayesianensemble.webservice.performance;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.tests.AutismTest;
import com.florian.bayesianensemble.webservice.performance.tests.IrisTest;
import com.florian.bayesianensemble.webservice.performance.tests.SmallIrisTest;
import org.junit.jupiter.api.Test;

public class PerformanceTest {
    public static final boolean SMALLTEST = true;

    @Test
    public void blub() throws Exception {
        printPerformance("autism manual", AutismTest.testPerformanceManual());
    }

    @Test
    public void testPerformance() throws Exception {
        printPerformance("smallIris automatic", SmallIrisTest.testPerformanceAutomatic());
        printPerformance("smallIris manual", SmallIrisTest.testPerformanceManual());

        if (!SMALLTEST) {
            printPerformance("Iris automatic", IrisTest.testPerformanceAutomatic());
            printPerformance("Iris manual", IrisTest.testPerformanceManual());
        }
    }

    private void printPerformance(String name, Performance p) {
        System.out.println(name);
        System.out.println("EnsembleAUC; LeftAUC; RightAUC; CentralAUC; VertiBayesAUC; VertiBayesTime; EnsembleTime");
        System.out.println(
                p.getWeightedAUCEnsemble() + "; " + p.getWeightedAUCLeft() + "; " + p.getWeightedAUCRight() + "; " +
                        p.getWeightedAUCCentral() + "; " + p.getVertibayesPerformance() + "; " + p.getAverageTime() + "; " + p.getVertibayesTime());
    }
}
