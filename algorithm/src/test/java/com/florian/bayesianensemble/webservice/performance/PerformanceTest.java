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
    public void testHorizontalBiassed() throws Exception {
        if (!SMALLTEST) {
            List<Double> bias = Arrays.asList(0.75, 0.85, 0.95);
            for (Double b : bias) {
                printPerformance("Iris biassed: " + b + " horizontal", IrisTest.testPerformancePopulationBiassed(b),
                                 true);
                printPerformance("Diabetes biassed: " + b + " horizontal",
                                 DiabetesTest.testPerformancePopulationBiassed(b), false);
                printPerformance("Autism biassed: " + b + " horizontal",
                                 AutismTest.testPerformancePopulationBiassed(b),
                                 false);
                printPerformance("Asia biassed: " + b + " horizontal", AsiaTest.testPerformancePopulationBiassed(b),
                                 false);
                // three way split
                printThreeWayPerformance("Diabetes biassed threeway: " + b + " threeway horizontal ",
                                         DiabetesTest.testPerformancePopulationBiassedThreeway(b), true);
                printThreeWayPerformance("Autism biassed threeway: " + b + " threeway horizontal ",
                                         AutismTest.testPerformancePopulationBiassedThreeway(b), false);
                printThreeWayPerformance("Asia biassed threeway: " + b + " threeway horizontal ",
                                         AsiaTest.testPerformancePopulationBiassedThreeway(b),
                                         false);
            }

            for (Double b : bias) {
                for (Double treshold : tresholds) {
                    printPerformance("Diabetes biassed: " + b + " horizontal unknown:" + treshold,
                                     DiabetesTest.testPerformancePopulationBiassedUnknown(treshold, b), true);
                    printPerformance("Iris biassed: " + b + " horizontal unknown:" + treshold,
                                     IrisTest.testPerformancePopulationBiassedUnknown(treshold, b),
                                     false);
                    printPerformance("Autism biassed: " + b + " horizontal unknown:" + treshold,
                                     AutismTest.testPerformancePopulationBiassedUnknown(treshold, b), false);
                    printPerformance("Asia biassed: " + b + " horizontal unknown:" + treshold,
                                     AsiaTest.testPerformancePopulationBiassedUnknown(treshold, b),
                                     false);

                    //threeway split:
                    printThreeWayPerformance("Diabetes biassed: " + b + " threeway horizontal unknown:" + treshold,
                                             DiabetesTest.testPerformanceThreeWayPopulationBiassedUnknown(treshold,
                                                                                                          b),
                                             true);
                    printThreeWayPerformance("Autism biassed: " + b + " threeway horizontal unknown:" + treshold,
                                             AutismTest.testPerformanceThreeWayPopulationBiassedUnknown(treshold,
                                                                                                        b),
                                             false);
                    printThreeWayPerformance("Asia biassed: " + b + " threeway horizontal unknown:" + treshold,
                                             AsiaTest.testPerformanceThreeWayPopulationBiassedUnknown(treshold, b),
                                             false);
                }
            }

        }

    }

    @Test
    public void testHorizontalBasic() throws Exception {
        if (!SMALLTEST) {
            if (false) {
                printPerformance("Diabetes horizontal", DiabetesTest.testPerformancePopulation(), true);
                printPerformance("Iris horizontal", IrisTest.testPerformancePopulation(), false);
                printPerformance("Autism horizontal", AutismTest.testPerformancePopulation(), false);
                printPerformance("Asia horizontal", AsiaTest.testPerformancePopulation(), false);
                printPerformance("Alarm horizontal", AlarmTest.testPerformancePopulation(), false);
                printPerformance("Mushroom horizontal", MushroomTest.testPerformancePopulation(), false);

//             three way split
                printThreeWayPerformance("Diabetes threeways horizontal",
                                         DiabetesTest.testPerformanceThreeWayPopulation(),
                                         true);
                printThreeWayPerformance("Asia threeways horizontal",
                                         AsiaTest.testPerformanceThreeWayPopulation(), false);
                printThreeWayPerformance("Autism threeways horizontal",
                                         AutismTest.testPerformanceThreeWayPopulation(),
                                         false);
                printThreeWayPerformance("Iris threeways horizontal",
                                         IrisTest.testPerformanceThreeWayPopulation(), false);
//            printThreeWayPerformance("Alarm threeways horizontal",
//                                     AlarmTest.testPerformanceThreeWayPopulation(), false);
                printThreeWayPerformance("MushroomTest threeways horizontal",
                                         MushroomTest.testPerformanceThreeWayPopulation(), false);
            }
            for (Double treshold : tresholds) {
                printPerformance("Diabetes horizontal " + "unknown:" + treshold,
                                 DiabetesTest.testPerformancePopulationUnknown(treshold), true);

                printPerformance("Iris horizontal " + "unknown:" + treshold,
                                 IrisTest.testPerformancePopulationUnknown(treshold),
                                 false);
                printPerformance("Autism horizontal " + "unknown:" + treshold,
                                 AutismTest.testPerformancePopulationUnknown(treshold), false);
                printPerformance("Asia horizontal " + "unknown:" + treshold,
                                 AsiaTest.testPerformancePopulationUnknown(treshold),
                                 false);
//                printPerformance("Alarm horizontal",
//                                 AlarmTest.testPerformancePopulationUnknown(treshold), false);
//                printPerformance("Mushroom horizontal",
//                                 MushroomTest.testPerformancePopulationUnknown(treshold), false);

                // three way split
                printThreeWayPerformance("Diabetes threeways horizontal " + "unknown:" + treshold,
                                         DiabetesTest.testPerformanceThreeWayPopulationUnknown(treshold),
                                         true);
                printThreeWayPerformance("Asia threeways horizontal " + "unknown:" + treshold,
                                         AsiaTest.testPerformanceThreeWayPopulationUnknown(treshold), false);
                printThreeWayPerformance("Autism threeways horizontal " + "unknown:" + treshold,
                                         AutismTest.testPerformanceThreeWayPopulationUnknown(treshold),
                                         false);
                printThreeWayPerformance("Iris threeways horizontal " + "unknown:" + treshold,
                                         IrisTest.testPerformanceThreeWayPopulationUnknown(treshold), false);
//                printThreeWayPerformance("Alarm threeways horizontal",
//                                         AlarmTest.testPerformanceThreeWayPopulationUnknown(treshold), false);
//                printThreeWayPerformance("MushroomTest threeways horizontal",
//                                         MushroomTest.testPerformanceThreeWayPopulationUnknown(treshold),
//                                         false);
            }
        }

    }


    @Test
    public void testHybrid() throws Exception {
        if (SMALLTEST) {
            printPerformance("SmallDiabetes automatic hybrid " + true,
                             SmallDiabetesTest.testPerformanceThreeWayHybrid(true),
                             true);
            printPerformance("SmallDiabetes automatic hybrid " + false,
                             SmallDiabetesTest.testPerformanceThreeWayHybrid(false),
                             true);
        }

        if (!SMALLTEST) {
            List<Boolean> hybrid = Arrays.asList(true, false);
            // three way split
            for (Boolean b : hybrid) {
                if (b == false) {
                    continue;
                }
                printThreeWayPerformance("Diabetes automatic threeways hybrid model:" + b,
                                         DiabetesTest.testPerformanceThreeWayHybrid(b),
                                         true);
                printThreeWayPerformance("Asia automatic threeways hybrid model:" + b,
                                         AsiaTest.testPerformanceThreeWayHybrid(b), false);
                printThreeWayPerformance("Autism automatic threeways hybrid model:" + b,
                                         AutismTest.testPerformanceThreeWayHybrid(b),
                                         false);
                printThreeWayPerformance("Iris automatic threeways hybrid model:" + b,
                                         IrisTest.testPerformanceThreeWayHybrid(b), false);


                for (Double treshold : tresholds) {
                    printThreeWayPerformance("Iris automatic threeways unknown hybrid model:" + b + treshold,
                                             IrisTest.testPerformanceThreeWayHybridUnknown(treshold, b), true);
                    printThreeWayPerformance(
                            "Diabetes automatic threeways unknown hybrid model:" + b + treshold,
                            DiabetesTest.testPerformanceThreeWayHybridUnknown(treshold, b),
                            false);
                    printThreeWayPerformance("Autism automatic threeways unknown hybrid model:" + b + treshold,
                                             AutismTest.testPerformanceThreeWayHybridUnknown(treshold, b),
                                             false);
                    printThreeWayPerformance("Asia automatic threeways unknown hybrid model:" + b + treshold,
                                             AsiaTest.testPerformanceThreeWayHybridUnknown(treshold, b), false);
                }
            }

            for (Boolean b : hybrid) {

                for (int i = 0; i < 10; i++) {
                    printThreeWayPerformance("Mushroom automatic threeways hybrid model:" + b,
                                             MushroomTest.testPerformanceThreeWayHybrid(b),
                                             false);
                    printThreeWayPerformance("Alarm automatic threeways hybrid model:" + b,
                                             AlarmTest.testPerformanceThreeWayHybrid(b),
                                             false);
                }

                for (Double treshold : tresholds) {
                    printThreeWayPerformance("Mushroom manual threeways unknown " + treshold,
                                             MushroomTest.testPerformanceThreeWayHybridUnknown(treshold, b), true);
                }

                for (int i = 0; i < 10; i++) {
                    printPerformance("Alarm automatic unknown " + 0.05,
                                     AlarmTest.testPerformanceThreeWayHybridUnknown(0.05, b), true);
                    printPerformance("Mushroom automatic unknown " + 0.05,
                                     MushroomTest.testPerformanceThreeWayHybridUnknown(0.05, b), false);
                }

                for (int i = 0; i < 10; i++) {
                    printPerformance("Alarm automatic unknown " + 0.1,
                                     AlarmTest.testPerformanceThreeWayHybridUnknown(0.1, b), true);
                    printPerformance("Mushroom automatic unknown " + 0.1,
                                     MushroomTest.testPerformanceThreeWayHybridUnknown(0.1, b), false);
                }

                for (int i = 0; i < 10; i++) {
                    printPerformance("Alarm automatic unknown " + 0.3,
                                     AlarmTest.testPerformanceThreeWayHybridUnknown(0.3, b), true);
                    printPerformance("Mushroom automatic unknown " + 0.3,
                                     MushroomTest.testPerformanceThreeWayHybridUnknown(0.3, b), false);
                }
            }

        }
    }

    @Test
    public void testHorizontal() throws Exception {
        if (SMALLTEST) {
            printPerformance("SmallDiabetes automatic horizontal", SmallDiabetesTest.testPerformancePopulation(), true);
        }

        if (!SMALLTEST) {
            printPerformance("Diabetes automatic horizontal", DiabetesTest.testPerformancePopulation(), true);
            printPerformance("Iris automatic horizontal", IrisTest.testPerformancePopulation(), false);
            printPerformance("Autism automatic horizontal", AutismTest.testPerformancePopulation(), false);
            printPerformance("Asia automatic horizontal", AsiaTest.testPerformancePopulation(), false);

            // three way split
            printThreeWayPerformance("Diabetes automatic threeways horizontal",
                                     DiabetesTest.testPerformanceThreeWayPopulation(),
                                     true);
            printThreeWayPerformance("Asia automatic threeways horizontal",
                                     AsiaTest.testPerformanceThreeWayPopulation(), false);
            printThreeWayPerformance("Autism automatic threeways horizontal",
                                     AutismTest.testPerformanceThreeWayPopulation(),
                                     false);
            printThreeWayPerformance("Iris automatic threeways horizontal",
                                     IrisTest.testPerformanceThreeWayPopulation(), false);

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
                printThreeWayPerformance("Iris automatic threeways unknown horizontal" + treshold,
                                         IrisTest.testPerformanceThreeWayPopulationUnknown(treshold), false);


                printThreeWayPerformance("Asia automatic threeways unknown horizontal" + treshold,
                                         AsiaTest.testPerformanceThreeWayPopulationUnknown(treshold), false);
                printThreeWayPerformance("Autism automatic threeways unknown horizontal" + treshold,
                                         AutismTest.testPerformanceThreeWayPopulationUnknown(treshold),
                                         false);
            }


            for (int i = 0; i < 10; i++) {
                printThreeWayPerformance("Mushroom automatic threeways horizontal",
                                         MushroomTest.testPerformanceThreeWayPopulation(),
                                         false);
                printThreeWayPerformance("Alarm automatic threeways horizontal",
                                         AlarmTest.testPerformanceThreeWayPopulation(),
                                         false);

                printThreeWayPerformance("Mushroom automatic threeways horizontal",
                                         MushroomTest.testPerformancePopulation(),
                                         false);
                printThreeWayPerformance("Alarm automatic threeways horizontal",
                                         AlarmTest.testPerformancePopulation(),
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
            printPerformance("SmallDiabetesTest automatic", SmallDiabetesTest.testPerformanceAutomatic(), true);

            printPerformance("SmallDiabetesTest automatic unknown " + 0.05,
                             SmallDiabetesTest.testPerformanceAutomaticUnknown(0.05), false);
        }

        if (!SMALLTEST) {
            //two-way split
            printPerformance("Diabetes automatic", DiabetesTest.testPerformanceAutomatic(), true);
            printPerformance("Iris automatic", IrisTest.testPerformanceAutomatic(), false);
            printPerformance("Iris manual", IrisTest.testPerformanceManual(), false);
            printPerformance("Autism automatic", AutismTest.testPerformanceAutomatic(), false);
            printPerformance("Autism manual", AutismTest.testPerformanceManual(), false);
            printPerformance("Mushroom automatic", MushroomTest.testPerformanceAutomatic(), false);
            printPerformance("Mushroom manual", MushroomTest.testPerformanceManual(), false);
            printPerformance("Asia automatic", AsiaTest.testPerformanceAutomatic(), false);
            printPerformance("Alarm automatic", AlarmTest.testPerformanceAutomatic(), false);

            // three way split
            printThreeWayPerformance("Diabetes automatic threeways", DiabetesTest.testPerformanceThreeWayAutomatic(),
                                     true);
            printThreeWayPerformance("Asia automatic threeways", AsiaTest.testPerformanceThreeWayAutomatic(), false);
            printThreeWayPerformance("Autism automatic threeways", AutismTest.testPerformanceThreeWayAutomatic(),
                                     false);
            printThreeWayPerformance("Autism manual threeways", AutismTest.testPerformanceThreeWayManual(), false);
            printThreeWayPerformance("Mushroom automatic threeways", MushroomTest.testPerformanceThreeWayAutomatic(),
                                     false);
            printThreeWayPerformance("Mushroom manual threeways", MushroomTest.testPerformanceThreeWayManual(),
                                     false);
            printThreeWayPerformance("Alarm automatic threeways", AlarmTest.testPerformanceThreeWayAutomatic(),
                                     false);

            for (Double treshold : tresholds) {
                //two-way split
                printPerformance("Diabetes automatic unknown " + treshold,
                                 DiabetesTest.testPerformanceAutomaticUnknown(treshold), true);
                printPerformance("Iris automatic unknown " + treshold,
                                 IrisTest.testPerformanceAutomaticUnknown(treshold), false);
                printPerformance("Iris manual unknown " + treshold, IrisTest.testPerformanceManualUnknown
                                         (treshold),
                                 false);
                printPerformance("Autism automatic unknown " + treshold,
                                 AutismTest.testPerformanceAutomaticUnknown(treshold), false);
                printPerformance("Autism manual unknown " + treshold,
                                 AutismTest.testPerformanceManualUnknown(treshold),
                                 false);


                printPerformance("Asia automatic unknown " + treshold,
                                 AsiaTest.testPerformanceAutomaticUnknown(treshold), true);


                //three way split
                printThreeWayPerformance("Diabetes automatic threeways unknown " + treshold,
                                         DiabetesTest.testPerformanceThreeWayAutomaticUnknown(treshold),
                                         true);
                printThreeWayPerformance("Asia automatic threeways unknown " + treshold,
                                         AsiaTest.testPerformanceThreeWayAutomaticUnknown(treshold), false);
                printThreeWayPerformance("Autism automatic threeways unknown " + treshold,
                                         AutismTest.testPerformanceThreeWayAutomaticUnknown(treshold),
                                         false);
                printThreeWayPerformance("Autism manual threeways unknown " + treshold,
                                         AutismTest.testPerformanceThreeWayManualUnknown(treshold), false);
            }

            for (Double treshold : tresholds) {
                printPerformance("Mushroom manual unknown " + treshold,
                                 MushroomTest.testPerformanceManualUnknown(treshold), true);
                printThreeWayPerformance("Mushroom manual threeways unknown " + treshold,
                                         MushroomTest.testPerformanceThreeWayManualUnknown(treshold), true);
            }

            for (int i = 0; i < 1; i++) {
                printPerformance("Alarm automatic unknown " + 0.05,
                                 AlarmTest.testPerformanceAutomaticUnknown(0.05), true);
                printPerformance("Mushroom automatic unknown " + 0.05,
                                 MushroomTest.testPerformanceAutomaticUnknown(0.05), false);
            }

            for (int i = 0; i < 3; i++) {
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

            for (int i = 0; i < 10; i++) {
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
