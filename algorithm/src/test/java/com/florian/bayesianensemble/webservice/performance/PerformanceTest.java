package com.florian.bayesianensemble.webservice.performance;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.tests.MushroomTest;
import com.florian.bayesianensemble.webservice.performance.tests.SmallIrisTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class PerformanceTest {
    public static final boolean SMALLTEST = false;
    public static final List<Double> tresholds = Arrays.asList(0.05, 0.1, 0.3);

    @Test
    public void testPerformance() throws Exception {
        if (SMALLTEST) {
            printPerformance("smallIris automatic", SmallIrisTest.testPerformanceAutomatic(), true);
            printPerformance("smallIris manual", SmallIrisTest.testPerformanceManual(), false);

            for (Double treshold : tresholds) {
                printPerformance("smallIris automatic unknown " + treshold,
                                 SmallIrisTest.testPerformanceAutomaticUnknown(treshold), false);
                printPerformance("smallIris manual unknown " + treshold,
                                 SmallIrisTest.testPerformanceManualUnknown(treshold), false);
            }
        }

        if (!SMALLTEST) {
            //two-way split
//            printPerformance("Diabetes automatic", DiabetesTest.testPerformanceAutomatic(), true);
//            printPerformance("Iris automatic", IrisTest.testPerformanceAutomatic(), false);
//            printPerformance("Iris manual", IrisTest.testPerformanceManual(), false);
//            printPerformance("Autism automatic", AutismTest.testPerformanceAutomatic(), false);
//            printPerformance("Autism manual", AutismTest.testPerformanceManual(), false);
//            printPerformance("Mushroom automatic", MushroomTest.testPerformanceAutomatic(), false);
//            printPerformance("Mushroom manual", MushroomTest.testPerformanceManual(), false);
//            printPerformance("Asia automatic", AsiaTest.testPerformanceAutomatic(), false);
//            printPerformance("Alarm automatic", AlarmTest.testPerformanceAutomatic(), false);

            //three way split
//            printThreeWayPerformance("Diabetes automatic threeways", DiabetesTest.testPerformanceThreeWayAutomatic(),
//                                     true);
//            printThreeWayPerformance("Asia automatic threeways", AsiaTest.testPerformanceThreeWayAutomatic(), false);
//            printThreeWayPerformance("Autism automatic threeways", AutismTest.testPerformanceThreeWayAutomatic(),
//                                     false);
//            printThreeWayPerformance("Autism manual threeways", AutismTest.testPerformanceThreeWayManual(), false);
//            printThreeWayPerformance("Mushroom automatic threeways", MushroomTest.testPerformanceThreeWayAutomatic(),
//                                     false);
//            printThreeWayPerformance("Mushroom manual threeways", MushroomTest.testPerformanceThreeWayManual(),
//            false);
//            printThreeWayPerformance("Alarm automatic threeways", AlarmTest.testPerformanceThreeWayAutomatic(),
//            false);

//            for (Double treshold : tresholds) {
//                //two-way split
//                if (treshold > 0.05) {
//                    printPerformance("Diabetes automatic unknown " + treshold,
//                                     DiabetesTest.testPerformanceAutomaticUnknown(treshold), true);
//                    printPerformance("Iris automatic unknown " + treshold,
//                                     IrisTest.testPerformanceAutomaticUnknown(treshold), false);
//                    printPerformance("Iris manual unknown " + treshold, IrisTest.testPerformanceManualUnknown
//                    (treshold),
//                                     false);
//                    printPerformance("Autism automatic unknown " + treshold,
//                                     AutismTest.testPerformanceAutomaticUnknown(treshold), false);
//                    printPerformance("Autism manual unknown " + treshold,
//                                     AutismTest.testPerformanceManualUnknown(treshold),
//                                     false);
//                }
//
//                printPerformance("Asia automatic unknown " + treshold,
//                                 AsiaTest.testPerformanceAutomaticUnknown(treshold), true);
//
//
//                //three way split
//                printThreeWayPerformance("Diabetes automatic threeways unknown " + treshold,
//                                         DiabetesTest.testPerformanceThreeWayAutomaticUnknown(treshold),
//                                         true);
//                printThreeWayPerformance("Asia automatic threeways unknown " + treshold,
//                                         AsiaTest.testPerformanceThreeWayAutomaticUnknown(treshold), false);
//                printThreeWayPerformance("Autism automatic threeways unknown " + treshold,
//                                         AutismTest.testPerformanceThreeWayAutomaticUnknown(treshold),
//                                         false);
//                printThreeWayPerformance("Autism manual threeways unknown " + treshold,
//                                         AutismTest.testPerformanceThreeWayManualUnknown(treshold), false);
//            }

            for (Double treshold : tresholds) {
                printPerformance("Mushroom manual unknown " + treshold,
                                 MushroomTest.testPerformanceManualUnknown(treshold), true);
                printThreeWayPerformance("Mushroom manual threeways unknown " + treshold,
                                         MushroomTest.testPerformanceThreeWayManualUnknown(treshold), true);
            }

            for (int i = 0; i < 10; i++) {
//                for (Double treshold : tresholds) {
                double treshold = 0.05;
                if (treshold != 0.05 && i != 0) {
//                        printPerformance("Alarm automatic unknown " + treshold,
//                                         AlarmTest.testPerformanceAutomaticUnknown(treshold), true);
                    printPerformance("Mushroom automatic unknown " + treshold,
                                     MushroomTest.testPerformanceAutomaticUnknown(treshold), true);
                }
                printThreeWayPerformance("Mushroom automatic threeways unknown " + treshold,
                                         MushroomTest.testPerformanceThreeWayAutomaticUnknown(treshold),
                                         true);
//                    printThreeWayPerformance("Alarm automatic threeways unknown " + treshold,
//                                             AlarmTest.testPerformanceThreeWayAutomaticUnknown(treshold), false);
//                }
            }
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
