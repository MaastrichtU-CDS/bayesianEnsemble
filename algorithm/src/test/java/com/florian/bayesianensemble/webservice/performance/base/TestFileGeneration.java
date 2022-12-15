package com.florian.bayesianensemble.webservice.performance.base;

import com.florian.nscalarproduct.data.Attribute;
import com.florian.nscalarproduct.data.Data;
import com.florian.nscalarproduct.error.InvalidDataFormatException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.florian.bayesianensemble.util.Util.createArrfString;
import static com.florian.nscalarproduct.data.Parser.parseData;

public class TestFileGeneration {

    @Test
    public void generate() {
        List<Double> tresholds = Arrays.asList(0.05, 0.1, 0.3);
        for (Double treshold : tresholds) {
            String source = "resources/Experiments/Alarm/ALARM10kWeka.arff";
            String output = "resources/Experiments/Alarm/ALARM10kWeka_missing_" + String.valueOf(treshold)
                    .replace(".", "_") + ".arff";
            String target = "BP";

            generateMissingData(source, output, target, treshold);

            source = "resources/Experiments/Asia/Asia10kWeka.arff";
            output = "resources/Experiments/Asia/Asia10kWeka_missing_" + String.valueOf(treshold)
                    .replace(".", "_") + ".arff";
            target = "lung";

            generateMissingData(source, output, target, treshold);

            source = "resources/Experiments/autism/autism.arff";
            output = "resources/Experiments/autism/autism_missing_" + String.valueOf(treshold)
                    .replace(".", "_") + ".arff";
            target = "Class/ASD";

            generateMissingData(source, output, target, treshold);

            source = "resources/Experiments/Diabetes/diabetesWeka.arff";
            output = "resources/Experiments/Diabetes/diabetesWeka_missing_" + String.valueOf(treshold)
                    .replace(".", "_") + ".arff";
            target = "Outcome";

            generateMissingData(source, output, target, treshold);

            source = "resources/Experiments/iris/iris.arff";
            output = "resources/Experiments/iris/iris_missing_" + String.valueOf(treshold)
                    .replace(".", "_") + ".arff";
            target = "label";

            generateMissingData(source, output, target, treshold);

            source = "resources/Experiments/Mushrooms/agaricus-lepiota.arff";
            output = "resources/Experiments/Mushrooms/agaricus-lepiota_missing_" + String.valueOf(treshold)
                    .replace(".", "_") + ".arff";
            target = "class";

            generateMissingData(source, output, target, treshold);
        }
    }

    private void generateMissingData(String path, String output, String target, double treshold) {
        Data d = null;
        try {
            d = parseData(path, 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidDataFormatException e) {
            e.printStackTrace();
        }
        Random r = new Random();
        for (List<Attribute> attribute : d.getData()) {
            for (Attribute a : attribute) {
                if (a.getAttributeName().equals("ID")) {
                    break;
                } else {
                    if (a.getType() == Attribute.AttributeType.bool) {
                        //this is needed to turn it into a valid weka file.
                        a.setValue(a.getValue().toLowerCase());
                    }
                    if (r.nextDouble() < treshold) {
                        a.setValue("?");
                    }
                }
            }
        }


        printARFF(createArrfString(d, target), output);
    }

    private void printARFF(String data, String path) {
        File csvOutputFile = new File(path);
        data.substring(0, data.lastIndexOf("\n"));

        try {
            PrintWriter pw = new PrintWriter(csvOutputFile);
            try {
                pw.println(data);
            } catch (Throwable var7) {
                try {
                    pw.close();
                } catch (Throwable var6) {
                    var7.addSuppressed(var6);
                }

                throw var7;
            }

            pw.close();
        } catch (FileNotFoundException var8) {
            var8.printStackTrace();
        }

    }
}
