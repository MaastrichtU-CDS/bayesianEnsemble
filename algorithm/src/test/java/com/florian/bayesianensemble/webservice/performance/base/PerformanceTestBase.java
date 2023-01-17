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
import com.florian.vertibayes.webservice.BayesServer;
import com.florian.vertibayes.webservice.VertiBayesCentralServer;
import com.florian.vertibayes.webservice.VertiBayesEndpoint;
import com.florian.vertibayes.webservice.domain.CreateNetworkRequest;
import com.florian.vertibayes.webservice.domain.external.ExpectationMaximizationResponse;
import com.florian.vertibayes.webservice.domain.external.WebBayesNetwork;

import java.io.IOException;
import java.util.*;

import static com.florian.bayesianensemble.webservice.performance.base.Util.dataToArff;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PerformanceTestBase {
    private final String SOURCE;
    private final String TARGET;
    private final int ROUNDS;
    private final int FOLDS;
    private static final String LEFT = "resources/Experiments/left.arff";
    private static final String LEFT_LOCAL = "resources/Experiments/leftlocal.arff";
    private static final String RIGHT = "resources/Experiments/right.arff";
    private static final String RIGHT_LOCAL = "resources/Experiments/rightlocal.arff";


    public PerformanceTestBase(String source, String target, int rounds, int folds) {
        this.SOURCE = source;
        this.TARGET = target;
        this.ROUNDS = rounds;
        this.FOLDS = folds;
    }

    public Performance manualSplit(Set<String> leftManual, Set<String> rightManual) throws Exception {
        Map<String, Double> aucs = new HashMap<>();
        double weightedAUCEnsemble = 0;
        double weightedAUCLeft = 0;
        double weightedAUCRight = 0;

        Performance p = new Performance();
        long time = 0;
        splitSourceManually(leftManual, rightManual);

        long start = System.currentTimeMillis();
        EnsembleResponse e = trainModel();
        time += System.currentTimeMillis() - start;
        weightedAUCEnsemble += e.getWeightedAUC();
        EnsembleResponse left = validateAgainstLocal(LEFT_LOCAL);
        weightedAUCLeft += left.getWeightedAUC();
        p.addLeftAuc(left.getAucs());
        EnsembleResponse right = validateAgainstLocal(RIGHT_LOCAL);
        weightedAUCRight += right.getWeightedAUC();
        p.addRightAuc(right.getAucs());

        for (String key : e.getAucs().keySet()) {
            if (aucs.containsKey(key)) {
                aucs.put(key, aucs.get(key) + e.getAucs().get(key));
            } else {
                aucs.put(key, e.getAucs().get(key));
            }
        }

        p.setEnsembleAuc(aucs);

        p.setAverageTime(time);

        p.setWeightedAUCRight(weightedAUCRight);
        p.setWeightedAUCLeft(weightedAUCLeft);
        p.setWeightedAUCEnsemble(weightedAUCEnsemble);

        long vertibayesTime = System.currentTimeMillis();
        ExpectationMaximizationResponse res = vertiBayesComparison();
        p.addVertibayesTime(System.currentTimeMillis() - vertibayesTime);
        p.addVertibayesPerformance(res.getSvdgAuc());

        return p;
    }

    public Performance automaticSplit() throws Exception {
        Map<String, Double> aucs = new HashMap<>();
        double weightedAUCEnsemble = 0;
        double weightedAUCLeft = 0;
        double weightedAUCRight = 0;
        double weightedAUCCentral = 0;

        Performance p = new Performance();
        long time = 0;
        for (int i = 0; i < ROUNDS; i++) {
            splitSource();
            long start = System.currentTimeMillis();
            EnsembleResponse e = trainModel();
            long duration = System.currentTimeMillis() - start;
            time += duration;
            if (duration > p.getMaxTime()) {
                p.setMaxTime(duration);
            }
            if (p.getMinTime() == 0 || duration < p.getMinTime()) {
                p.setMinTime(duration);
            }

            long vertibayesTime = System.currentTimeMillis();
            ExpectationMaximizationResponse res = vertiBayesComparison();
            p.addVertibayesTime(System.currentTimeMillis() - vertibayesTime);
            p.addVertibayesPerformance(res.getSvdgAuc());

            weightedAUCEnsemble += e.getWeightedAUC();
            EnsembleResponse left = validateAgainstLocal(LEFT_LOCAL);
            weightedAUCLeft += left.getWeightedAUC();
            p.addLeftAuc(left.getAucs());
            EnsembleResponse right = validateAgainstLocal(RIGHT_LOCAL);
            weightedAUCRight += right.getWeightedAUC();
            p.addRightAuc(right.getAucs());

            for (String key : e.getAucs().keySet()) {
                if (aucs.containsKey(key)) {
                    aucs.put(key, aucs.get(key) + e.getAucs().get(key));
                } else {
                    aucs.put(key, e.getAucs().get(key));
                }
            }
        }
        for (String key : aucs.keySet()) {
            aucs.put(key, aucs.get(key) / ROUNDS);
        }
        p.normalize(ROUNDS);
        p.setEnsembleAuc(aucs);

        EnsembleResponse central = validateAgainstLocal(SOURCE);
        weightedAUCCentral = central.getWeightedAUC();
        p.setCentralAuc(central.getAucs());
        p.setAverageTime(time / ROUNDS);


        weightedAUCLeft /= ROUNDS;
        weightedAUCRight /= ROUNDS;
        weightedAUCEnsemble /= ROUNDS;

        p.setWeightedAUCCentral(weightedAUCCentral);
        p.setWeightedAUCRight(weightedAUCRight);
        p.setWeightedAUCLeft(weightedAUCLeft);
        p.setWeightedAUCEnsemble(weightedAUCEnsemble);

        return p;
    }

    public Performance populationSplit() throws Exception {
        Map<String, Double> aucs = new HashMap<>();
        double weightedAUCEnsemble = 0;
        double weightedAUCLeft = 0;
        double weightedAUCRight = 0;
        double weightedAUCCentral = 0;

        Performance p = new Performance();
        long time = 0;
        for (int i = 0; i < ROUNDS; i++) {
            splitPopulation();
            long start = System.currentTimeMillis();
            EnsembleResponse e = trainModelHybrid(false);
            long duration = System.currentTimeMillis() - start;
            time += duration;
            if (duration > p.getMaxTime()) {
                p.setMaxTime(duration);
            }
            if (p.getMinTime() == 0 || duration < p.getMinTime()) {
                p.setMinTime(duration);
            }

            long vertibayesTime = System.currentTimeMillis();
            ExpectationMaximizationResponse res = vertiBayesComparison();
            p.addVertibayesTime(System.currentTimeMillis() - vertibayesTime);
            p.addVertibayesPerformance(res.getSvdgAuc());

            weightedAUCEnsemble += e.getWeightedAUC();
            EnsembleResponse left = validateAgainstLocal(LEFT_LOCAL);
            weightedAUCLeft += left.getWeightedAUC();
            p.addLeftAuc(left.getAucs());
            EnsembleResponse right = validateAgainstLocal(RIGHT_LOCAL);
            weightedAUCRight += right.getWeightedAUC();
            p.addRightAuc(right.getAucs());

            for (String key : e.getAucs().keySet()) {
                if (aucs.containsKey(key)) {
                    aucs.put(key, aucs.get(key) + e.getAucs().get(key));
                } else {
                    aucs.put(key, e.getAucs().get(key));
                }
            }
        }
        for (String key : aucs.keySet()) {
            aucs.put(key, aucs.get(key) / ROUNDS);
        }
        p.normalize(ROUNDS);
        p.setEnsembleAuc(aucs);

        EnsembleResponse central = validateAgainstLocal(SOURCE);
        weightedAUCCentral = central.getWeightedAUC();
        p.setCentralAuc(central.getAucs());
        p.setAverageTime(time / ROUNDS);


        weightedAUCLeft /= ROUNDS;
        weightedAUCRight /= ROUNDS;
        weightedAUCEnsemble /= ROUNDS;

        p.setWeightedAUCCentral(weightedAUCCentral);
        p.setWeightedAUCRight(weightedAUCRight);
        p.setWeightedAUCLeft(weightedAUCLeft);
        p.setWeightedAUCEnsemble(weightedAUCEnsemble);

        return p;
    }

    private ExpectationMaximizationResponse vertiBayesComparison() throws Exception {
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
        req.setFolds(FOLDS);

        return central.expectationMaximization(req);
    }

    public EnsembleResponse validateAgainstLocal(String source) throws Exception {
        Performance p = new Performance();
        EnsembleServer station1 = new EnsembleServer(source, "1");
        EnsembleEndpoint endpoint1 = new EnsembleEndpoint(station1);
        EnsembleServer secret = new EnsembleServer("4", Arrays.asList(endpoint1));

        ServerEndpoint secretEnd = new ServerEndpoint(secret);

        List<ServerEndpoint> all = new ArrayList<>();
        all.add(endpoint1);
        all.add(secretEnd);
        secret.setEndpoints(all);
        station1.setEndpoints(all);

        EnsembleCentralServer central = new EnsembleCentralServer();
        central.initEndpoints(Arrays.asList(endpoint1), secretEnd);

        CreateEnsembleRequest req = new CreateEnsembleRequest();
        req.setTarget(TARGET);
        req.setFolds(FOLDS);

        EnsembleResponse response = central.createEnsemble(req);
        return response;
    }

    private EnsembleResponse trainModel() throws Exception {
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
        req.setFolds(FOLDS);

        EnsembleResponse response = central.createEnsemble(req);
        return response;
    }

    private EnsembleResponse trainModelHybrid(boolean hybrid) throws Exception {
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
        req.setFolds(FOLDS);
        req.setHybrid(hybrid);

        EnsembleResponse response = central.createEnsemble(req);
        return response;
    }

    private void splitSourceManually(Set<String> leftManual, Set<String> rightManual)
            throws IOException, InvalidDataFormatException {
        Data data = Parser.parseData(SOURCE, 0);

        List<List<Attribute>> left = new ArrayList<>();
        List<List<Attribute>> right = new ArrayList<>();


        for (int i = 0; i < data.getData().size(); i++) {
            if (i == data.getIdColumn()) {
                left.add(0, data.getData().get(i));
                right.add(0, data.getData().get(i));
            } else if (leftManual.contains(data.getData().get(i).get(0).getAttributeName())) {
                left.add(data.getData().get(i));
            } else if (rightManual.contains(data.getData().get(i).get(0).getAttributeName())) {
                right.add(data.getData().get(i));
            }
        }
        assertEquals(left.size() + right.size(), data.getData().size() + 1);

        dataToArff(new Data(0, -1, left), LEFT);
        dataToArff(new Data(0, -1, right), RIGHT);


        createLocal(left, right);
    }

    private void splitSource() throws IOException, InvalidDataFormatException {
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

        createLocal(left, right);
    }

    private void splitPopulation() throws IOException, InvalidDataFormatException {
        Data data = Parser.parseData(SOURCE, 0);

        List<List<Attribute>> left = new ArrayList<>();
        List<List<Attribute>> right = new ArrayList<>();

        Random r = new Random();


        left = new ArrayList<>();
        right = new ArrayList<>();

        for (int i = 0; i < data.getData().size(); i++) {
            if (i == data.getIdColumn()) {
                left.add(0, data.getData().get(i));
                right.add(0, data.getData().get(i));
            } else {
                left.add(data.getData().get(i));
                right.add(data.getData().get(i));
            }
        }


        List<Attribute> leftPresent = new ArrayList<>();
        List<Attribute> rightPresent = new ArrayList<>();
        boolean done = false;
        while (!done) {
            int leftC = 0;
            int rightC = 0;
            leftPresent = new ArrayList<>();
            rightPresent = new ArrayList<>();
            for (int i = 0; i < data.getNumberOfIndividuals(); i++) {
                Attribute present = new Attribute(Attribute.AttributeType.bool, "true", "locallyPresent");
                Attribute absent = new Attribute(Attribute.AttributeType.bool, "false", "locallyPresent");
                if (r.nextDouble() < 0.5) {
                    leftC++;
                    leftPresent.add(present);
                    rightPresent.add(absent);
                } else {
                    rightC++;
                    leftPresent.add(absent);
                    rightPresent.add(present);
                }
            }
            done = leftC >= 50 && rightC >= 50;
        }

        left.add(leftPresent);
        right.add(rightPresent);

        dataToArff(new Data(0, -1, left), LEFT);
        dataToArff(new Data(0, -1, right), RIGHT);

        createLocalPopulation(left, right);
    }

    private void createLocal(List<List<Attribute>> left, List<List<Attribute>> right) {
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

        dataToArff(new Data(0, -1, left), LEFT_LOCAL);
        dataToArff(new Data(0, -1, right), RIGHT_LOCAL);
    }

    private void createLocalPopulation(List<List<Attribute>> left, List<List<Attribute>> right) {
        int index = -1;

        for (int i = 0; i < left.size(); i++) {
            if (left.get(i).get(0).getAttributeName().equals("locallyPresent")) {
                index = i;
                break;
            }
        }
        left.remove(index);
        index = -1;
        for (int i = 0; i < right.size(); i++) {
            if (right.get(i).get(0).getAttributeName().equals("locallyPresent")) {
                index = i;
                break;
            }
        }
        right.remove(index);

        dataToArff(new Data(0, -1, left), LEFT_LOCAL);
        dataToArff(new Data(0, -1, right), RIGHT_LOCAL);
    }
}
