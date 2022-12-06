package com.florian.bayesianensemble.webservice.performance;

import org.junit.jupiter.api.Test;

public class test {

    @Test
    public void test() throws Exception {
        PerformanceTest test = new PerformanceTest("resources/Experiments/iris/iris.arff", "label");
        Performance p = test.tests();
    }
}
