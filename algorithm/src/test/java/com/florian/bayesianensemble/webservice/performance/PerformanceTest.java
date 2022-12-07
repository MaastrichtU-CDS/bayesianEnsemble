package com.florian.bayesianensemble.webservice.performance;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.tests.IrisTest;
import com.florian.bayesianensemble.webservice.performance.tests.SmallIrisTest;
import org.junit.jupiter.api.Test;

public class PerformanceTest {
    public static final boolean SMALLTEST = true;

    @Test
    public void testPerformance() throws Exception {
        Performance p = SmallIrisTest.testPerformance();
        printPerformance(p);
        if (!SMALLTEST) {
            p = IrisTest.testPerformance();
            printPerformance(p);
        }
    }

    private void printPerformance(Performance p) {
        System.out.println("EnsembleAUC, LeftAUC, RightAUC, CentralAUC, VertiBayesTime, EnsembleTime");
        System.out.println(
                p.getWeightedAUCEnsemble() + ", " + p.getWeightedAUCLeft() + ", " + p.getWeightedAUCRight() + ", " +
                        p.getWeightedAUCCentral() + ", " + p.getAverageTime() + ", " + p.getVertibayesTime());
    }
}
