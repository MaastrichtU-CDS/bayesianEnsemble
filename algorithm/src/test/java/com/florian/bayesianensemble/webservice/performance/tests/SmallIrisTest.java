package com.florian.bayesianensemble.webservice.performance.tests;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.base.PerformanceTestBase;
import com.florian.nscalarproduct.data.Attribute;
import com.florian.vertibayes.webservice.domain.external.WebNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.florian.bayesianensemble.webservice.performance.base.Util.createWebNode;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SmallIrisTest {
    private static final String SOURCE = "resources/Experiments/iris/iris.arff";
    private static final String TARGET = "label";
    private static final int FOLDS = 2;
    private static final int ROUNDS = 2;

    public static Performance testPerformance() throws Exception {
        PerformanceTestBase test = new PerformanceTestBase(SOURCE, TARGET, createNetwork(), ROUNDS, FOLDS);
        Performance p = test.tests();
        assertEquals(p.getWeightedAUCEnsemble(), p.getWeightedAUCRight(), 0.15);
        assertEquals(p.getWeightedAUCEnsemble(), p.getWeightedAUCLeft(), 0.15);
        assertEquals(p.getWeightedAUCEnsemble(), p.getWeightedAUCCentral(), 0.15);
        return p;
    }

    public static List<WebNode> createNetwork() {
        WebNode label = createWebNode("label", Attribute.AttributeType.string, new ArrayList<>());
        WebNode petallength = createWebNode("petallength", Attribute.AttributeType.real,
                                            Arrays.asList(label.getName()));
        WebNode petalwidth = createWebNode("petalwidth", Attribute.AttributeType.real,
                                           Arrays.asList(label.getName()));
        WebNode sepallength = createWebNode("sepallength", Attribute.AttributeType.real,
                                            Arrays.asList(label.getName()));
        WebNode sepalwidth = createWebNode("sepalwidth", Attribute.AttributeType.real,
                                           Arrays.asList(label.getName()));

        //list nodes in the order you want the attributes printed
        return Arrays.asList(sepallength, sepalwidth, petallength, petalwidth, label);
    }
}

