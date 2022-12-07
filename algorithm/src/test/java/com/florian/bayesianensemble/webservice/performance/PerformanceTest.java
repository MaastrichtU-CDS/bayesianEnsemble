package com.florian.bayesianensemble.webservice.performance;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import org.junit.jupiter.api.Test;

public class PerformanceTest {

    @Test
    public void testPerformance() throws Exception {
        Performance p = IrisTest.testPerformance();
        printPerformance(p);
    }

    private void printPerformance(Performance p) {
        System.out.println("EnsembleAUC, LeftAUC, RightAUC, CentralAUC, VertiBayesTime, EnsembleTime");
        System.out.println(
                p.getWeightedAUCEnsemble() + ", " + p.getWeightedAUCLeft() + ", " + p.getWeightedAUCRight() + ", " +
                        p.getWeightedAUCCentral() + ", " + p.getAverageTime() + ", " + p.getVertibayesTime());
    }
}
