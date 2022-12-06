package com.florian.bayesianensemble.webservice.performance.base;

import com.florian.bayesianensemble.webservice.EnsembleCentralServer;
import com.florian.bayesianensemble.webservice.EnsembleEndpoint;
import com.florian.bayesianensemble.webservice.EnsembleServer;
import com.florian.bayesianensemble.webservice.domain.CreateEnsembleRequest;
import com.florian.bayesianensemble.webservice.domain.EnsembleResponse;
import com.florian.nscalarproduct.data.Attribute;
import com.florian.nscalarproduct.data.Data;
import com.florian.nscalarproduct.data.Parser;
import com.florian.nscalarproduct.error.InvalidDataFormatException;
import com.florian.nscalarproduct.webservice.ServerEndpoint;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.estimate.SimpleEstimator;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.florian.bayesianensemble.webservice.performance.base.Performance.compare;
import static com.florian.bayesianensemble.webservice.performance.base.Util.dataToArff;

public class PerformanceTestBase {
    private String source;
    private static final String LEFT = "resources/Experiments/left.arff";
    private static final String LEFT_WEKA = "resources/Experiments/leftweka.arff";
    private static final String RIGHT = "resources/Experiments/right.arff";
    private static final String RIGHT_WEKA = "resources/Experiments/rightweka.arff";
    private final String TARGET;
    private static final int ROUNDS = 10;
    private static final int FOLDS = 10;

    public PerformanceTestBase(String source, String target) {
        this.source = source;
        this.TARGET = target;
    }

    public Performance tests() throws Exception {
        Map<String, Double> aucs = new HashMap<>();
        Performance p = new Performance();
        for (int i = 0; i < ROUNDS; i++) {
            splitSource();
            Map<String, Double> results = trainModel();

            p.addLeftAuc(validateAgainstWeka(LEFT_WEKA));
            p.addRightAuc(validateAgainstWeka(RIGHT_WEKA));
            if (p.getEnsembleAucMax() == null) {
                p.setEnsembleAucMax(results);
                p.setEnsembleAucMin(results);
            } else {
                if (compare(results, p.getEnsembleAucMax()) > 0) {
                    p.setEnsembleAucMax(results);
                }
                if (compare(results, p.getEnsembleAucMin()) < 0) {
                    p.setEnsembleAucMin(results);
                }
            }

            for (String key : results.keySet()) {
                if (aucs.containsKey(key)) {
                    aucs.put(key, aucs.get(key) + results.get(key));
                } else {
                    aucs.put(key, results.get(key));
                }
            }
        }
        for (String key : aucs.keySet()) {
            aucs.put(key, aucs.get(key) / ROUNDS);
        }
        p.normalize(FOLDS);
        p.setEnsembleAuc(aucs);
        return p;
    }

    public Map<String, Double> validateAgainstWeka(String source) throws Exception {
        Performance p = new Performance();

        BayesNet network = new BayesNet();
        Instances data = new Instances(
                new BufferedReader(new FileReader(source)));

        for (int i = 0; i < data.numAttributes(); i++) {
            if (data.attribute(i).name().equals(TARGET)) {
                data.setClassIndex(i);
                break;
            }
        }

        network.setEstimator(new SimpleEstimator());
        network.buildClassifier(data);

        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(network, data, FOLDS, new Random(1));

        Map<String, Double> aucs = new HashMap<>();
        for (int i = 0; i < data.attribute(data.classIndex()).numValues(); i++) {
            aucs.put(data.attribute(data.classIndex()).value(i), eval.areaUnderROC(i));
        }
        return aucs;
    }

    private Map<String, Double> trainModel() throws Exception {
        EnsembleServer station1 = new EnsembleServer(LEFT, "1");
        EnsembleServer station2 = new EnsembleServer(RIGHT, "2");
        EnsembleEndpoint endpoint1 = new EnsembleEndpoint(station1);
        EnsembleEndpoint endpoint2 = new EnsembleEndpoint(station2);
        EnsembleServer secret = new EnsembleServer("4", Arrays.asList(endpoint1, endpoint2));

        ServerEndpoint secretEnd = new ServerEndpoint(secret);

        List<ServerEndpoint> all = new ArrayList<>();
        all.add(endpoint1);
        all.add(endpoint2);
        all.add(secretEnd);
        secret.setEndpoints(all);
        station1.setEndpoints(all);
        station2.setEndpoints(all);

        EnsembleCentralServer central = new EnsembleCentralServer();
        central.initEndpoints(Arrays.asList(endpoint1, endpoint2), secretEnd);

        CreateEnsembleRequest req = new CreateEnsembleRequest();
        req.setTarget(TARGET);

        EnsembleResponse response = central.createEnsemble(req);
        return response.getAucs();
    }

    private void splitSource() throws IOException, InvalidDataFormatException {
        Data data = Parser.parseData(source, 0);

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
            if (left.size() >= 2 && right.size() >= 2) {
                //check if both slits have at least 1 attribute + ID.
                valid = true;
            }
        }
        dataToArff(new Data(0, -1, left), LEFT);
        dataToArff(new Data(0, -1, right), RIGHT);

        createWEKA(left, right);
    }

    private void createWEKA(List<List<Attribute>> left, List<List<Attribute>> right) {
        int leftTarget = -1;
        int rightTarget = -1;
        for (int i = 0; i < left.size(); i++) {
            if (left.get(i).get(0).getAttributeName().equals(TARGET)) {
                leftTarget = i;
            }
        }
        for (int i = 0; i < right.size(); i++) {
            if (right.get(i).get(0).getAttributeName().equals(TARGET)) {
                rightTarget = i;
            }
        }
        if (leftTarget < 0) {
            left.add(right.get(rightTarget));
        }
        if (rightTarget < 0) {
            right.add(left.get(leftTarget));
        }

        dataToArff(new Data(0, -1, left), LEFT_WEKA);
        dataToArff(new Data(0, -1, right), RIGHT_WEKA);
    }
}
