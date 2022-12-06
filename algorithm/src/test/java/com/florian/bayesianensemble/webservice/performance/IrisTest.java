package com.florian.bayesianensemble.webservice.performance;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceTestBase;
import org.junit.jupiter.api.Test;

public class IrisTest {
    private static final String SOURCE = "resources/Experiments/iris/iris.arff";
    private static final String TARGET = "label";

    @Test
    public void testPerformance() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET);
        Performance p = test.tests();
        for (String key : p.getEnsembleAuc().keySet()) {
            System.out.println("Ensemble " + key + " " + p.getEnsembleAuc().get(key));
            System.out.println("Min " + key + " " + p.getEnsembleAucMin().get(key));
            System.out.println("Max " + key + " " + p.getEnsembleAucMax().get(key));
            System.out.println("Left " + key + " " + p.getLeftAuc().get(key));
            System.out.println("Left min " + key + " " + p.getLeftAucMin().get(key));
            System.out.println("Left max " + key + " " + p.getLeftAucMax().get(key));
            System.out.println("Right " + key + " " + p.getRightAuc().get(key));
            System.out.println("Right min " + key + " " + p.getRightAucMin().get(key));
            System.out.println("Right max " + key + " " + p.getRightAucMax().get(key));
        }
    }
}
