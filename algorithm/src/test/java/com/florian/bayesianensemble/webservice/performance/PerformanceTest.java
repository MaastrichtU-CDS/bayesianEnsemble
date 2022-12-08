package com.florian.bayesianensemble.webservice.performance;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.tests.IrisTest;
import com.florian.bayesianensemble.webservice.performance.tests.SmallIrisTest;
import org.junit.jupiter.api.Test;

public class PerformanceTest {
    public static final boolean SMALLTEST = true;

    @Test
    public void testPerformance() throws Exception {
        printPerformance(SmallIrisTest.testPerformance());

        if (!SMALLTEST) {
            printPerformance(IrisTest.testPerformance());
        }
    }

    private void printPerformance(Performance p) {
        System.out.println("EnsembleAUC, LeftAUC, RightAUC, CentralAUC, VertiBayesAUC, VertiBayesTime, EnsembleTime");
        System.out.println(
                p.getWeightedAUCEnsemble() + ", " + p.getWeightedAUCLeft() + ", " + p.getWeightedAUCRight() + ", " +
                        p.getWeightedAUCCentral() + ", " + p.getVertibayesPerformance() + ", " + p.getAverageTime() + ", " + p.getVertibayesTime());
    }
}
