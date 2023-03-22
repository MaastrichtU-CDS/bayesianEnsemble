package com.florian.bayesianensemble.webservice.performance;

import com.florian.nscalarproduct.data.Attribute;
import com.florian.nscalarproduct.data.Data;
import com.florian.nscalarproduct.data.Parser;
import com.florian.nscalarproduct.error.InvalidDataFormatException;
import com.florian.nscalarproduct.webservice.ServerEndpoint;
import com.florian.vertibayes.webservice.BayesServer;
import com.florian.vertibayes.webservice.VertiBayesCentralServer;
import com.florian.vertibayes.webservice.VertiBayesEndpoint;
import com.florian.vertibayes.webservice.domain.CreateNetworkRequest;
import com.florian.vertibayes.webservice.domain.external.ExpectationMaximizationResponse;
import com.florian.vertibayes.webservice.domain.external.WebBayesNetwork;
import com.florian.vertibayes.webservice.domain.external.WebNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.florian.bayesianensemble.webservice.performance.base.Util.dataToArff;

public class test {

    final String LEFT = "resources/Experiments/left.arff";
    final String RIGHT = "resources/Experiments/right.arff";

    @Test
    public void test() throws Exception {
        String SOURCE = "resources/Experiments/Autism/autism.arff";
        String TARGET = "Class/ASD";
        splitSource(SOURCE);
        ExpectationMaximizationResponse x = vertiBayesComparison(TARGET);
        int probs = 0;
        for (WebNode n : x.getNodes()) {
            probs += (n.getProbabilities().size());
        }
        System.out.println("Autism " + probs);

        SOURCE = "resources/Experiments/Mushrooms/agaricus-lepiota.arff";
        TARGET = "class";
        splitSource(SOURCE);
        x = vertiBayesComparison(TARGET);
        probs = 0;
        for (WebNode n : x.getNodes()) {
            probs += (n.getProbabilities().size());
        }
        System.out.println("Mushrooms " + probs);

        SOURCE = "resources/Experiments/Autism/autism_missing_0_1.arff";
        TARGET = "Class/ASD";
        splitSource(SOURCE);
        x = vertiBayesComparison(TARGET);
        probs = 0;
        for (WebNode n : x.getNodes()) {
            probs += (n.getProbabilities().size());
        }
        System.out.println("Autism missing" + probs);

        SOURCE = "resources/Experiments/Mushrooms/agaricus-lepiota_missing_0_1.arff";
        TARGET = "class";
        splitSource(SOURCE);
        x = vertiBayesComparison(TARGET);
        probs = 0;
        for (WebNode n : x.getNodes()) {
            probs += (n.getProbabilities().size());
        }
        System.out.println("Mushrooms missing" + probs);

        SOURCE = "resources/Experiments/Asia/Asia10kWeka_missing_0_1.arff";
        TARGET = "lung";
        splitSource(SOURCE);
        x = vertiBayesComparison(TARGET);
        probs = 0;
        for (WebNode n : x.getNodes()) {
            probs += (n.getProbabilities().size());
        }
        System.out.println("Asia " + probs);

        SOURCE = "resources/Experiments/iris/iris_missing_0_1.arff";
        TARGET = "label";
        splitSource(SOURCE);
        x = vertiBayesComparison(TARGET);
        probs = 0;
        for (WebNode n : x.getNodes()) {
            probs += (n.getProbabilities().size());
        }
        System.out.println("Iris " + probs);

        SOURCE = "resources/Experiments/Alarm/ALARM10kWeka_missing_0_1.arff";
        TARGET = "BP";
        splitSource(SOURCE);
        x = vertiBayesComparison(TARGET);
        probs = 0;
        for (WebNode n : x.getNodes()) {
            probs += (n.getProbabilities().size());
        }
        System.out.println("Alarm " + probs);

        SOURCE = "resources/Experiments/Diabetes/diabetesWeka_missing_0_1.arff";
        TARGET = "Outcome";
        splitSource(SOURCE);
        x = vertiBayesComparison(TARGET);
        probs = 0;
        for (WebNode n : x.getNodes()) {
            probs += (n.getProbabilities().size());
        }
        System.out.println("Diabetes " + probs);
    }

    private void splitSource(String SOURCE) throws IOException, InvalidDataFormatException {
        Data data = Parser.parseData(SOURCE, 0);

        List<List<Attribute>> left = new ArrayList<>();
        List<List<Attribute>> right = new ArrayList<>();

        Random r = new Random();
        boolean valid = false;
        while (!valid) {
            left = new ArrayList<>();
            right = new ArrayList<>();
            for (int i = 0; i < data.getData().size(); i++) {
                if (i == data.getIdColumn()) {
                    left.add(0, data.getData().get(i));
                    right.add(0, data.getData().get(i));
                } else if (r.nextDouble() >= 0.5) {
                    left.add(data.getData().get(i));
                } else {
                    right.add(data.getData().get(i));
                }
            }
            if (left.size() > 2 && right.size() > 2) {
                //check if both slits have at least 2 attribute + ID.
                valid = true;
            }
        }
        dataToArff(new Data(0, -1, left), LEFT);
        dataToArff(new Data(0, -1, right), RIGHT);

    }

    private ExpectationMaximizationResponse vertiBayesComparison(String TARGET) throws Exception {
        BayesServer station1 = new BayesServer(LEFT, "1");
        BayesServer station2 = new BayesServer(RIGHT, "2");
        VertiBayesEndpoint endpoint1 = new VertiBayesEndpoint(station1);
        VertiBayesEndpoint endpoint2 = new VertiBayesEndpoint(station2);
        BayesServer secret = new BayesServer("4", Arrays.asList(endpoint1, endpoint2));

        ServerEndpoint secretEnd = new ServerEndpoint(secret);

        List<ServerEndpoint> all = new ArrayList<>();
        all.add(endpoint1);
        all.add(endpoint2);
        all.add(secretEnd);
        secret.setEndpoints(all);
        station1.setEndpoints(all);
        station2.setEndpoints(all);

        VertiBayesCentralServer central = new VertiBayesCentralServer();
        central.initEndpoints(Arrays.asList(endpoint1, endpoint2), secretEnd);


        CreateNetworkRequest r = new CreateNetworkRequest();
        r.setMinPercentage(10);

        WebBayesNetwork req = new WebBayesNetwork();
        req.setNodes(central.buildNetwork(r).getNodes());
        req.setTarget(TARGET);
        req.setFolds(1);

        return central.expectationMaximization(req);
    }
}
