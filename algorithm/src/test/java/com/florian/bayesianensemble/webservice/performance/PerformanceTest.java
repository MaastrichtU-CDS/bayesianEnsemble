package com.florian.bayesianensemble.webservice.performance;

import com.florian.bayesianensemble.webservice.performance.base.Performance;
import com.florian.bayesianensemble.webservice.performance.tests.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class PerformanceTest {
    public static final boolean SMALLTEST = false;
    public static final List<Double> tresholds = Arrays.asList(0.05, 0.1, 0.3);

    @Test
    public void testHorizontal() throws Exception {
        if (SMALLTEST) {
            printPerformance("Diabetes automatic horizontal", DiabetesTest.testPerformancePopulation(), true);
        }

        if (!SMALLTEST) {
            printPerformance("Diabetes automatic horizontal", DiabetesTest.testPerformancePopulation(), true);
            printPerformance("Iris automatic horizontal", IrisTest.testPerformancePopulation(), false);
            printPerformance("Autism automatic horizontal", AutismTest.testPerformancePopulation(), false);
            printPerformance("Mushroom automatic horizontal", MushroomTest.testPerformancePopulation(), false);
            printPerformance("Asia automatic horizontal", AsiaTest.testPerformancePopulation(), false);
            printPerformance("Alarm automatic horizontal", AlarmTest.testPerformancePopulation(), false);

            // three way split
            printThreeWayPerformance("Diabetes automatic threeways horizontal",
                                     DiabetesTest.testPerformanceThreeWayPopulation(),
                                     true);
            printThreeWayPerformance("Asia automatic threeways horizontal",
                                     AsiaTest.testPerformanceThreeWayPopulation(), false);
            printThreeWayPerformance("Autism automatic threeways horizontal",
                                     AutismTest.testPerformanceThreeWayPopulation(),
                                     false);
            printThreeWayPerformance("Mushroom automatic threeways horizontal",
                                     MushroomTest.testPerformanceThreeWayPopulation(),
                                     false);
            printThreeWayPerformance("Alarm automatic threeways horizontal",
                                     AlarmTest.testPerformanceThreeWayPopulation(),
                                     false);

            for (Double treshold : tresholds) {
                //two-way split
                printPerformance("Diabetes automatic unknown horizontal" + treshold,
                                 DiabetesTest.testPerformancePopulationUnknown(treshold), true);
                printPerformance("Iris automatic unknown horizontal" + treshold,
                                 IrisTest.testPerformancePopulationUnknown(treshold), false);
                printPerformance("Autism automatic unknown horizontal" + treshold,
                                 AutismTest.testPerformancePopulationUnknown(treshold), false);
                printPerformance("Asia automatic unknown horizontal" + treshold,
                                 AsiaTest.testPerformancePopulationUnknown(treshold), true);

                //three way split
                printThreeWayPerformance("Diabetes automatic threeways unknown horizontal" + treshold,
                                         DiabetesTest.testPerformanceThreeWayPopulationUnknown(treshold),
                                         true);
                printThreeWayPerformance("Asia automatic threeways unknown horizontal" + treshold,
                                         AsiaTest.testPerformanceThreeWayPopulationUnknown(treshold), false);
                printThreeWayPerformance("Autism automatic threeways unknown horizontal" + treshold,
                                         AutismTest.testPerformanceThreeWayPopulationUnknown(treshold),
                                         false);
            }
            for (Double treshold : tresholds) {
                printPerformance("Mushroom manual unknown " + treshold,
                                 MushroomTest.testPerformancePopulationUnknown(treshold), true);
                printThreeWayPerformance("Mushroom manual threeways unknown " + treshold,
                                         MushroomTest.testPerformanceThreeWayPopulationUnknown(treshold), true);
            }

            for (int i = 0; i < 10; i++) {
                printPerformance("Alarm automatic unknown " + 0.05,
                                 AlarmTest.testPerformancePopulationUnknown(0.05), true);
                printPerformance("Mushroom automatic unknown " + 0.05,
                                 MushroomTest.testPerformancePopulationUnknown(0.05), false);
            }

            for (int i = 0; i < 10; i++) {
                printPerformance("Alarm automatic unknown " + 0.1,
                                 AlarmTest.testPerformancePopulationUnknown(0.1), true);
                printPerformance("Mushroom automatic unknown " + 0.1,
                                 MushroomTest.testPerformancePopulationUnknown(0.1), false);
            }

            for (int i = 0; i < 10; i++) {
                printPerformance("Alarm automatic unknown " + 0.3,
                                 AlarmTest.testPerformancePopulationUnknown(0.3), true);
                printPerformance("Mushroom automatic unknown " + 0.3,
                                 MushroomTest.testPerformancePopulationUnknown(0.3), false);
            }

            for (int i = 0; i < 10; i++) {
                printPerformance("Alarm automatic unknown " + 0.05,
                                 AlarmTest.testPerformanceThreeWayPopulationUnknown(0.05), true);
                printPerformance("Mushroom automatic unknown " + 0.05,
                                 MushroomTest.testPerformanceThreeWayPopulationUnknown(0.05), false);
            }

            for (int i = 0; i < 10; i++) {
                printPerformance("Alarm automatic unknown " + 0.1,
                                 AlarmTest.testPerformanceThreeWayPopulationUnknown(0.1), true);
                printPerformance("Mushroom automatic unknown " + 0.1,
                                 MushroomTest.testPerformanceThreeWayPopulationUnknown(0.1), false);
            }

            for (int i = 0; i < 10; i++) {
                printPerformance("Alarm automatic unknown " + 0.3,
                                 AlarmTest.testPerformanceThreeWayPopulationUnknown(0.3), true);
                printPerformance("Mushroom automatic unknown " + 0.3,
                                 MushroomTest.testPerformanceThreeWayPopulationUnknown(0.3), false);
            }

        }
    }

    @Test
    public void testPerformance() throws Exception {
        if (SMALLTEST) {
            printPerformance("smallIris automatic", SmallIrisTest.testPerformanceAutomatic(), true);
            printPerformance("smallIris manual", SmallIrisTest.testPerformanceManual(), false);

            printPerformance("smallIris automatic unknown " + 0.05,
                             SmallIrisTest.testPerformanceAutomaticUnknown(0.05), false);
            printPerformance("smallIris manual unknown " + 0.05,
                             SmallIrisTest.testPerformanceManualUnknown(0.05), false);

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
//
//            // three way split
//            printThreeWayPerformance("Diabetes automatic threeways", DiabetesTest.testPerformanceThreeWayAutomatic(),
//                                     true);
//            printThreeWayPerformance("Asia automatic threeways", AsiaTest.testPerformanceThreeWayAutomatic(), false);
//            printThreeWayPerformance("Autism automatic threeways", AutismTest.testPerformanceThreeWayAutomatic(),
//                                     false);
//            printThreeWayPerformance("Autism manual threeways", AutismTest.testPerformanceThreeWayManual(), false);
//            printThreeWayPerformance("Mushroom automatic threeways", MushroomTest.testPerformanceThreeWayAutomatic(),
//                                     false);
//            printThreeWayPerformance("Mushroom manual threeways", MushroomTest.testPerformanceThreeWayManual(),
//                                     false);
//            printThreeWayPerformance("Alarm automatic threeways", AlarmTest.testPerformanceThreeWayAutomatic(),
//                                     false);
//
//            for (Double treshold : tresholds) {
//                //two-way split
//                    printPerformance("Diabetes automatic unknown " + treshold,
//                                     DiabetesTest.testPerformanceAutomaticUnknown(treshold), true);
//                    printPerformance("Iris automatic unknown " + treshold,
//                                     IrisTest.testPerformanceAutomaticUnknown(treshold), false);
//                    printPerformance("Iris manual unknown " + treshold, IrisTest.testPerformanceManualUnknown
//                                             (treshold),
//                                     false);
//                    printPerformance("Autism automatic unknown " + treshold,
//                                     AutismTest.testPerformanceAutomaticUnknown(treshold), false);
//                    printPerformance("Autism manual unknown " + treshold,
//                                     AutismTest.testPerformanceManualUnknown(treshold),
//                                     false);
//
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
//
//            for (Double treshold : tresholds) {
//                printPerformance("Mushroom manual unknown " + treshold,
//                                 MushroomTest.testPerformanceManualUnknown(treshold), true);
//                printThreeWayPerformance("Mushroom manual threeways unknown " + treshold,
//                                         MushroomTest.testPerformanceThreeWayManualUnknown(treshold), true);
//            }

            for (int i = 0; i < 1; i++) {
                printPerformance("Alarm automatic unknown " + 0.05,
                                 AlarmTest.testPerformanceAutomaticUnknown(0.05), true);
                printPerformance("Mushroom automatic unknown " + 0.05,
                                 MushroomTest.testPerformanceAutomaticUnknown(0.05), false);
            }

            for (int i = 0; i < 10; i++) {
                printPerformance("Alarm automatic unknown " + 0.1,
                                 AlarmTest.testPerformanceAutomaticUnknown(0.1), true);
                printPerformance("Mushroom automatic unknown " + 0.1,
                                 MushroomTest.testPerformanceAutomaticUnknown(0.1), false);
            }

            for (int i = 0; i < 10; i++) {
                printPerformance("Alarm automatic unknown " + 0.3,
                                 AlarmTest.testPerformanceAutomaticUnknown(0.3), true);
                printPerformance("Mushroom automatic unknown " + 0.3,
                                 MushroomTest.testPerformanceAutomaticUnknown(0.3), false);
            }

            for (int i = 0; i < 4; i++) {
                printPerformance("Alarm automatic unknown " + 0.05,
                                 AlarmTest.testPerformanceThreeWayAutomaticUnknown(0.05), true);
                printPerformance("Mushroom automatic unknown " + 0.05,
                                 MushroomTest.testPerformanceThreeWayAutomaticUnknown(0.05), false);
            }

            for (int i = 0; i < 10; i++) {
                printPerformance("Alarm automatic unknown " + 0.1,
                                 AlarmTest.testPerformanceThreeWayAutomaticUnknown(0.1), true);
                printPerformance("Mushroom automatic unknown " + 0.1,
                                 MushroomTest.testPerformanceThreeWayAutomaticUnknown(0.1), false);
            }

            for (int i = 0; i < 10; i++) {
                printPerformance("Alarm automatic unknown " + 0.3,
                                 AlarmTest.testPerformanceThreeWayAutomaticUnknown(0.3), true);
                printPerformance("Mushroom automatic unknown " + 0.3,
                                 MushroomTest.testPerformanceThreeWayAutomaticUnknown(0.3), false);
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
