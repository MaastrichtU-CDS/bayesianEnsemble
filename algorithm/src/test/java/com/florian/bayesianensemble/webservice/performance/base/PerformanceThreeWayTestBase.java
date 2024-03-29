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
import com.florian.vertibayes.webservice.domain.CreateNetworkRequest;
import com.florian.vertibayes.webservice.domain.external.ExpectationMaximizationResponse;
import com.florian.vertibayes.webservice.domain.external.WebBayesNetwork;

import java.io.IOException;
import java.util.*;

import static com.florian.bayesianensemble.webservice.performance.base.Util.dataToArff;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PerformanceThreeWayTestBase {
    private final String SOURCE;
    private final String TARGET;
    private final int ROUNDS;
    private final int FOLDS;
    private static final String LEFT = "resources/Experiments/left.arff";
    private static final String LEFT_LOCAL = "resources/Experiments/leftlocal.arff";
    private static final String RIGHT = "resources/Experiments/right.arff";
    private static final String RIGHT_LOCAL = "resources/Experiments/rightlocal.arff";
    private static final String CENTER = "resources/Experiments/center.arff";
    private static final String CENTER_LOCAL = "resources/Experiments/centerlocal.arff";


    public PerformanceThreeWayTestBase(String source, String target, int rounds, int folds) {
        this.SOURCE = source;
        this.TARGET = target;
        this.ROUNDS = rounds;
        this.FOLDS = folds;
    }

    public Performance manualSplit(Set<String> leftManual, Set<String> rightManual, Set<String> centerManual)
            throws Exception {
        Map<String, Double> aucs = new HashMap<>();
        double weightedAUCEnsemble = 0;
        double weightedAUCLeft = 0;
        double weightedAUCRight = 0;
        double weightedAUCCenter = 0;

        Performance p = new Performance();
        long time = 0;
        splitSourceManually(leftManual, rightManual, centerManual);

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
        EnsembleResponse center = validateAgainstLocal(CENTER_LOCAL);
        weightedAUCCenter += center.getWeightedAUC();
        p.addCenterAuc(center.getAucs());

        for (String key : e.getAucs().keySet()) {
            if (aucs.containsKey(key)) {
                aucs.put(key, aucs.get(key) + e.getAucs().get(key));
            } else {
                aucs.put(key, e.getAucs().get(key));
            }
        }

        p.setEnsembleAuc(aucs);

        EnsembleResponse central = validateAgainstLocal(SOURCE);
        double weightedAUCCentral = central.getWeightedAUC();
        p.setWeightedAUCCentral(weightedAUCCentral);

        p.setAverageTime(time);

        p.setWeightedAUCRight(weightedAUCRight);
        p.setWeightedAUCCenter(weightedAUCCenter);
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
        double weightedAUCCenter = 0;

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

            weightedAUCEnsemble += e.getWeightedAUC();
            EnsembleResponse left = validateAgainstLocal(LEFT_LOCAL);
            weightedAUCLeft += left.getWeightedAUC();
            p.addLeftAuc(left.getAucs());
            EnsembleResponse right = validateAgainstLocal(RIGHT_LOCAL);
            weightedAUCRight += right.getWeightedAUC();
            p.addRightAuc(right.getAucs());
            EnsembleResponse center = validateAgainstLocal(CENTER_LOCAL);
            weightedAUCCenter += center.getWeightedAUC();
            p.addCenterAuc(center.getAucs());

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
        weightedAUCCenter /= ROUNDS;
        weightedAUCEnsemble /= ROUNDS;

        p.setWeightedAUCCentral(weightedAUCCentral);
        p.setWeightedAUCRight(weightedAUCRight);
        p.setWeightedAUCLeft(weightedAUCLeft);
        p.setWeightedAUCEnsemble(weightedAUCEnsemble);
        p.setWeightedAUCCenter(weightedAUCCenter);

        long start = System.currentTimeMillis();
        ExpectationMaximizationResponse res = vertiBayesComparison();
        p.setVertibayesTime(System.currentTimeMillis() - start);
        p.setVertibayesPerformance(res.getSvdgAuc());
        return p;
    }

    public Performance hybridSplit(boolean hybridModel) throws Exception {
        Map<String, Double> aucs = new HashMap<>();
        double weightedAUCEnsemble = 0;
        double weightedAUCLeft = 0;
        double weightedAUCRight = 0;
        double weightedAUCCentral = 0;
        double weightedAUCCenter = 0;

        Performance p = new Performance();
        long time = 0;
        for (int i = 0; i < ROUNDS; i++) {
            splitHybrid();
            long start = System.currentTimeMillis();
            EnsembleResponse e = trainModelHybrid(hybridModel);
            long duration = System.currentTimeMillis() - start;
            time += duration;
            if (duration > p.getMaxTime()) {
                p.setMaxTime(duration);
            }
            if (p.getMinTime() == 0 || duration < p.getMinTime()) {
                p.setMinTime(duration);
            }

            weightedAUCEnsemble += e.getWeightedAUC();
            EnsembleResponse left = validateAgainstLocal(LEFT_LOCAL);
            weightedAUCLeft += left.getWeightedAUC();
            p.addLeftAuc(left.getAucs());
            EnsembleResponse right = validateAgainstLocal(RIGHT_LOCAL);
            weightedAUCRight += right.getWeightedAUC();
            p.addRightAuc(right.getAucs());
            EnsembleResponse center = validateAgainstLocal(CENTER_LOCAL);
            weightedAUCCenter += center.getWeightedAUC();
            p.addCenterAuc(center.getAucs());

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
        weightedAUCCenter /= ROUNDS;
        weightedAUCEnsemble /= ROUNDS;

        p.setWeightedAUCCentral(weightedAUCCentral);
        p.setWeightedAUCRight(weightedAUCRight);
        p.setWeightedAUCLeft(weightedAUCLeft);
        p.setWeightedAUCEnsemble(weightedAUCEnsemble);
        p.setWeightedAUCCenter(weightedAUCCenter);

        long start = System.currentTimeMillis();
        ExpectationMaximizationResponse res = vertiBayesComparison();
        p.setVertibayesTime(System.currentTimeMillis() - start);
        p.setVertibayesPerformance(res.getSvdgAuc());
        return p;
    }

    public Performance populationSplit() throws Exception {
        Map<String, Double> aucs = new HashMap<>();
        double weightedAUCEnsemble = 0;
        double weightedAUCLeft = 0;
        double weightedAUCRight = 0;
        double weightedAUCCentral = 0;
        double weightedAUCCenter = 0;

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

            weightedAUCEnsemble += e.getWeightedAUC();
            EnsembleResponse left = validateLocalAgainstGlobal(LEFT_LOCAL);
            weightedAUCLeft += left.getWeightedAUC();
            p.addLeftAuc(left.getAucs());
            EnsembleResponse right = validateLocalAgainstGlobal(RIGHT_LOCAL);
            weightedAUCRight += right.getWeightedAUC();
            p.addRightAuc(right.getAucs());
            EnsembleResponse center = validateLocalAgainstGlobal(CENTER_LOCAL);
            weightedAUCCenter += center.getWeightedAUC();
            p.addCenterAuc(center.getAucs());

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
        weightedAUCCenter /= ROUNDS;
        weightedAUCEnsemble /= ROUNDS;

        p.setWeightedAUCCentral(weightedAUCCentral);
        p.setWeightedAUCRight(weightedAUCRight);
        p.setWeightedAUCLeft(weightedAUCLeft);
        p.setWeightedAUCEnsemble(weightedAUCEnsemble);
        p.setWeightedAUCCenter(weightedAUCCenter);

        long start = System.currentTimeMillis();
        ExpectationMaximizationResponse res = vertiBayesComparison();
        p.setVertibayesTime(System.currentTimeMillis() - start);
        p.setVertibayesPerformance(res.getSvdgAuc());
        return p;
    }

    public Performance populationBiassedSplit(String label, double bias) throws Exception {
        Map<String, Double> aucs = new HashMap<>();
        double weightedAUCEnsemble = 0;
        double weightedAUCLeft = 0;
        double weightedAUCRight = 0;
        double weightedAUCCentral = 0;
        double weightedAUCCenter = 0;

        Performance p = new Performance();
        long time = 0;
        for (int i = 0; i < ROUNDS; i++) {
            splitPopulationBias(label, bias);
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

            weightedAUCEnsemble += e.getWeightedAUC();
            EnsembleResponse left = validateLocalAgainstGlobal(LEFT_LOCAL);
            weightedAUCLeft += left.getWeightedAUC();
            p.addLeftAuc(left.getAucs());
            EnsembleResponse right = validateLocalAgainstGlobal(RIGHT_LOCAL);
            weightedAUCRight += right.getWeightedAUC();
            p.addRightAuc(right.getAucs());
            EnsembleResponse center = validateLocalAgainstGlobal(CENTER_LOCAL);
            weightedAUCCenter += center.getWeightedAUC();
            p.addCenterAuc(center.getAucs());

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
        weightedAUCCenter /= ROUNDS;
        weightedAUCEnsemble /= ROUNDS;

        p.setWeightedAUCCentral(weightedAUCCentral);
        p.setWeightedAUCRight(weightedAUCRight);
        p.setWeightedAUCLeft(weightedAUCLeft);
        p.setWeightedAUCEnsemble(weightedAUCEnsemble);
        p.setWeightedAUCCenter(weightedAUCCenter);

        long start = System.currentTimeMillis();
        ExpectationMaximizationResponse res = vertiBayesComparison();
        p.setVertibayesTime(System.currentTimeMillis() - start);
        p.setVertibayesPerformance(res.getSvdgAuc());
        return p;
    }

    private ExpectationMaximizationResponse vertiBayesComparison() throws Exception {
        EnsembleServer station1 = new EnsembleServer(LEFT, "1");
        EnsembleServer station2 = new EnsembleServer(RIGHT, "2");
        EnsembleServer station3 = new EnsembleServer(CENTER, "3");
        EnsembleEndpoint endpoint1 = new EnsembleEndpoint(station1);
        EnsembleEndpoint endpoint2 = new EnsembleEndpoint(station2);
        EnsembleEndpoint endpoint3 = new EnsembleEndpoint(station3);
        EnsembleServer secret = new EnsembleServer("4", Arrays.asList(endpoint1, endpoint2, endpoint3));

        ServerEndpoint secretEnd = new ServerEndpoint(secret);

        List<ServerEndpoint> all = new ArrayList<>();
        all.add(endpoint1);
        all.add(endpoint2);
        all.add(endpoint3);
        all.add(secretEnd);
        secret.setEndpoints(all);
        station1.setEndpoints(all);
        station2.setEndpoints(all);
        station3.setEndpoints(all);

        EnsembleCentralServer central = new EnsembleCentralServer();
        central.initEndpoints(Arrays.asList(endpoint1, endpoint2, endpoint3), secretEnd);

        CreateNetworkRequest r = new CreateNetworkRequest();
        r.setMinPercentage(0.1);

        WebBayesNetwork req = new WebBayesNetwork();
        req.setNodes(central.buildNetwork(r).getNodes());
        req.setTarget(TARGET);
        req.setFolds(FOLDS);

        return central.expectationMaximization(req);
    }

    public EnsembleResponse validateLocalAgainstGlobal(String source) throws Exception {
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

        EnsembleServer station2 = new EnsembleServer(SOURCE, "1");
        EnsembleEndpoint endpoint2 = new EnsembleEndpoint(station2);
        EnsembleServer secret2 = new EnsembleServer("4", Arrays.asList(endpoint2));

        ServerEndpoint secretEnd2 = new ServerEndpoint(secret2);


        EnsembleCentralServer central2 = new EnsembleCentralServer();
        central2.initEndpoints(Arrays.asList(endpoint2), secretEnd2);
        EnsembleResponse response2 = central2.validateEnsemble(response.getNetworks().get(0), TARGET);
        response.setAucs(response2.getAucs());
        response.setWeightedAUC(response2.getWeightedAUC());

        return response;
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
        EnsembleServer station3 = new EnsembleServer(CENTER, "3");
        EnsembleEndpoint endpoint1 = new EnsembleEndpoint(station1);
        EnsembleEndpoint endpoint2 = new EnsembleEndpoint(station2);
        EnsembleEndpoint endpoint3 = new EnsembleEndpoint(station3);
        EnsembleServer secret = new EnsembleServer("4", Arrays.asList(endpoint1, endpoint2, endpoint3));

        ServerEndpoint secretEnd = new ServerEndpoint(secret);

        List<ServerEndpoint> all = new ArrayList<>();
        all.add(endpoint1);
        all.add(endpoint2);
        all.add(endpoint3);
        all.add(secretEnd);
        secret.setEndpoints(all);
        station1.setEndpoints(all);
        station2.setEndpoints(all);
        station3.setEndpoints(all);

        EnsembleCentralServer central = new EnsembleCentralServer();
        central.initEndpoints(Arrays.asList(endpoint1, endpoint2, endpoint3), secretEnd);

        CreateEnsembleRequest req = new CreateEnsembleRequest();
        req.setTarget(TARGET);
        req.setFolds(FOLDS);

        EnsembleResponse response = central.createEnsemble(req);
        return response;
    }

    private EnsembleResponse trainModelHybrid(boolean hybrid) throws Exception {
        EnsembleServer station1 = new EnsembleServer(LEFT, "1");
        EnsembleServer station2 = new EnsembleServer(RIGHT, "2");
        EnsembleServer station3 = new EnsembleServer(CENTER, "3");
        EnsembleEndpoint endpoint1 = new EnsembleEndpoint(station1);
        EnsembleEndpoint endpoint2 = new EnsembleEndpoint(station2);
        EnsembleEndpoint endpoint3 = new EnsembleEndpoint(station3);
        EnsembleServer secret = new EnsembleServer("4", Arrays.asList(endpoint1, endpoint2, endpoint3));

        ServerEndpoint secretEnd = new ServerEndpoint(secret);

        List<ServerEndpoint> all = new ArrayList<>();
        all.add(endpoint1);
        all.add(endpoint2);
        all.add(endpoint3);
        all.add(secretEnd);
        secret.setEndpoints(all);
        station1.setEndpoints(all);
        station2.setEndpoints(all);
        station3.setEndpoints(all);

        EnsembleCentralServer central = new EnsembleCentralServer();
        central.initEndpoints(Arrays.asList(endpoint1, endpoint2, endpoint3), secretEnd);

        CreateEnsembleRequest req = new CreateEnsembleRequest();
        req.setTarget(TARGET);
        req.setFolds(FOLDS);
        req.setHybrid(hybrid);

        EnsembleResponse response = central.createEnsemble(req);
        return response;
    }


    private void splitSourceManually(Set<String> leftManual, Set<String> rightManual, Set<String> centerManual)
            throws IOException, InvalidDataFormatException {
        Data data = Parser.parseData(SOURCE, 0);

        List<List<Attribute>> left = new ArrayList<>();
        List<List<Attribute>> right = new ArrayList<>();
        List<List<Attribute>> center = new ArrayList<>();


        for (int i = 0; i < data.getData().size(); i++) {
            if (i == data.getIdColumn()) {
                left.add(0, data.getData().get(i));
                right.add(0, data.getData().get(i));
                center.add(0, data.getData().get(i));
            } else if (leftManual.contains(data.getData().get(i).get(0).getAttributeName())) {
                left.add(data.getData().get(i));
            } else if (rightManual.contains(data.getData().get(i).get(0).getAttributeName())) {
                right.add(data.getData().get(i));
            } else if (centerManual.contains(data.getData().get(i).get(0).getAttributeName())) {
                center.add(data.getData().get(i));
            }
        }
        assertEquals(left.size() + right.size() + center.size(), data.getData().size() + 2);

        dataToArff(new Data(0, -1, left), LEFT);
        dataToArff(new Data(0, -1, right), RIGHT);
        dataToArff(new Data(0, -1, center), CENTER);


        createLocal(left, right, center);
    }

    private void splitSource() throws IOException, InvalidDataFormatException {
        Data data = Parser.parseData(SOURCE, 0);

        List<List<Attribute>> left = new ArrayList<>();
        List<List<Attribute>> right = new ArrayList<>();
        List<List<Attribute>> center = new ArrayList<>();

        Random r = new Random();
        boolean valid = false;
        while (!valid) {
            left = new ArrayList<>();
            right = new ArrayList<>();
            center = new ArrayList<>();
            for (int i = 0; i < data.getData().size(); i++) {
                if (i == data.getIdColumn()) {
                    left.add(0, data.getData().get(i));
                    right.add(0, data.getData().get(i));
                } else if (r.nextDouble() <= 0.33) {
                    left.add(data.getData().get(i));
                } else if (r.nextDouble() <= 0.66) {
                    right.add(data.getData().get(i));
                } else {
                    center.add(data.getData().get(i));
                }
            }
            if (left.size() > 2 && right.size() > 2 && center.size() > 2) {
                //check if both slits have at least 2 attribute + ID.
                valid = true;
            }
        }
        dataToArff(new Data(0, -1, left), LEFT);
        dataToArff(new Data(0, -1, right), RIGHT);
        dataToArff(new Data(0, -1, center), CENTER);

        createLocal(left, right, center);
    }

    private void splitPopulation() throws IOException, InvalidDataFormatException {
        Data data = Parser.parseData(SOURCE, 0);

        List<List<Attribute>> left = new ArrayList<>();
        List<List<Attribute>> right = new ArrayList<>();
        List<List<Attribute>> center = new ArrayList<>();

        Random r = new Random();

        left = new ArrayList<>();
        right = new ArrayList<>();
        center = new ArrayList<>();
        for (int i = 0; i < data.getData().size(); i++) {
            if (i == data.getIdColumn()) {
                left.add(0, data.getData().get(i));
                right.add(0, data.getData().get(i));
                center.add(0, data.getData().get(i));
            } else {
                left.add(data.getData().get(i));
                right.add(data.getData().get(i));
                center.add(data.getData().get(i));
            }
        }
        List<Attribute> leftPresent = new ArrayList<>();
        List<Attribute> middlePresent = new ArrayList<>();
        List<Attribute> rightPresent = new ArrayList<>();
        boolean done = false;
        while (!done) {
            int leftC = 0;
            int rightC = 0;
            int centerC = 0;
            leftPresent = new ArrayList<>();
            middlePresent = new ArrayList<>();
            rightPresent = new ArrayList<>();

            for (int i = 0; i < data.getNumberOfIndividuals(); i++) {
                Attribute present = new Attribute(Attribute.AttributeType.bool, "true", "locallyPresent");
                Attribute absent = new Attribute(Attribute.AttributeType.bool, "false", "locallyPresent");
                double ran = r.nextDouble();
                if (ran < 0.33) {
                    leftC++;
                    leftPresent.add(present);
                    rightPresent.add(absent);
                    middlePresent.add(absent);
                } else if (ran < 0.66) {
                    rightC++;
                    leftPresent.add(absent);
                    rightPresent.add(present);
                    middlePresent.add(absent);
                } else {
                    centerC++;
                    leftPresent.add(absent);
                    rightPresent.add(absent);
                    middlePresent.add(present);
                }
            }
            done = leftC >= 50 && rightC >= 50 && centerC >= 50;
        }

        left.add(leftPresent);
        right.add(rightPresent);
        center.add(middlePresent);

        dataToArff(new Data(0, -1, left), LEFT);
        dataToArff(new Data(0, -1, right), RIGHT);
        dataToArff(new Data(0, -1, center), CENTER);

        createLocalPopulation(left, right, center);
    }

    private void splitPopulationBias(String label, double bias) throws IOException, InvalidDataFormatException {
        Data data = Parser.parseData(SOURCE, 0);

        List<List<Attribute>> left = new ArrayList<>();
        List<List<Attribute>> right = new ArrayList<>();
        List<List<Attribute>> center = new ArrayList<>();

        Random r = new Random();

        left = new ArrayList<>();
        right = new ArrayList<>();
        center = new ArrayList<>();
        for (int i = 0; i < data.getData().size(); i++) {
            if (i == data.getIdColumn()) {
                left.add(0, data.getData().get(i));
                right.add(0, data.getData().get(i));
                center.add(0, data.getData().get(i));
            } else {
                left.add(data.getData().get(i));
                right.add(data.getData().get(i));
                center.add(data.getData().get(i));
            }
        }
        List<Attribute> leftPresent = new ArrayList<>();
        List<Attribute> middlePresent = new ArrayList<>();
        List<Attribute> rightPresent = new ArrayList<>();
        boolean done = false;

        Set<String> labels = data.getUniqueValues(data.getAttributeValues(label));

        //pick a random label

        String selected = labels.stream().skip(new Random().nextInt(labels.size() - 1)).findFirst().orElse(null);


        while (!done) {
            int leftC = 0;
            int rightC = 0;
            int centerC = 0;
            leftPresent = new ArrayList<>();
            middlePresent = new ArrayList<>();
            rightPresent = new ArrayList<>();

            for (int i = 0; i < data.getNumberOfIndividuals(); i++) {
                Attribute present = new Attribute(Attribute.AttributeType.bool, "true", "locallyPresent");
                Attribute absent = new Attribute(Attribute.AttributeType.bool, "false", "locallyPresent");
                double ran = r.nextDouble();
                if (!data.getAttributeValues(label).get(i).getValue().equals(selected)) {
                    //label is not equal to the one we want to dominate on our site
                    if (ran < 1 - bias) {
                        leftC++;
                        leftPresent.add(present);
                        rightPresent.add(absent);
                        middlePresent.add(absent);
                    } else if (ran < (1 - (1 - bias)) / 2) {
                        rightC++;
                        leftPresent.add(absent);
                        rightPresent.add(present);
                        middlePresent.add(absent);
                    } else {
                        centerC++;
                        leftPresent.add(absent);
                        rightPresent.add(absent);
                        middlePresent.add(present);
                    }
                } else {
                    //label is the one we want to bias
                    if (ran < bias) {
                        leftC++;
                        leftPresent.add(present);
                        rightPresent.add(absent);
                        middlePresent.add(absent);
                    } else if (ran < 1 - ((1 - bias) / 2)) {
                        rightC++;
                        leftPresent.add(absent);
                        rightPresent.add(present);
                        middlePresent.add(absent);
                    } else {
                        centerC++;
                        leftPresent.add(absent);
                        rightPresent.add(absent);
                        middlePresent.add(present);
                    }
                }
            }
            done = leftC >= 50 && rightC >= 50 && centerC >= 50;
        }

        left.add(leftPresent);
        right.add(rightPresent);
        center.add(middlePresent);

        dataToArff(new Data(0, -1, left), LEFT);
        dataToArff(new Data(0, -1, right), RIGHT);
        dataToArff(new Data(0, -1, center), CENTER);

        createLocalPopulation(left, right, center);
    }

    private void createLocal(List<List<Attribute>> left, List<List<Attribute>> right, List<List<Attribute>> center) {
        int leftTarget = -1;
        int rightTarget = -1;
        int centerTarget = -1;
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
        for (int i = 0; i < center.size(); i++) {
            if (center.get(i).get(0).getAttributeName().equals(TARGET)) {
                centerTarget = i;
            }
        }
        if (leftTarget < 0) {
            if (rightTarget < 0) {
                left.add(center.get(centerTarget));
            } else {
                left.add(right.get(rightTarget));
            }
        }
        if (rightTarget < 0) {
            if (leftTarget < 0) {
                right.add(center.get(centerTarget));
            } else {
                right.add(left.get(leftTarget));
            }
        }
        if (centerTarget < 0) {
            if (rightTarget < 0) {
                center.add(left.get(leftTarget));
            } else {
                center.add(right.get(rightTarget));
            }
        }

        dataToArff(new Data(0, -1, left), LEFT_LOCAL);
        dataToArff(new Data(0, -1, right), RIGHT_LOCAL);
        dataToArff(new Data(0, -1, center), CENTER_LOCAL);
    }

    private void createLocalPopulation(List<List<Attribute>> left, List<List<Attribute>> right,
                                       List<List<Attribute>> center) {
        int index = -1;

        left = removeNotPresent(left);
        right = removeNotPresent(right);
        center = removeNotPresent(center);

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
        index = -1;
        for (int i = 0; i < center.size(); i++) {
            if (center.get(i).get(0).getAttributeName().equals("locallyPresent")) {
                index = i;
                break;
            }
        }
        center.remove(index);

        dataToArff(new Data(0, -1, left), LEFT_LOCAL);
        dataToArff(new Data(0, -1, right), RIGHT_LOCAL);
        dataToArff(new Data(0, -1, center), CENTER_LOCAL);
    }

    private void splitHybrid() throws IOException, InvalidDataFormatException {
        Data data = Parser.parseData(SOURCE, 0);

        List<List<Attribute>> left = new ArrayList<>();
        List<List<Attribute>> right = new ArrayList<>();
        List<List<Attribute>> center = new ArrayList<>();

        Random r = new Random();
        boolean valid = false;
        while (!valid) {
            left = new ArrayList<>();
            right = new ArrayList<>();
            center = new ArrayList<>();
            for (int i = 0; i < data.getData().size(); i++) {
                if (i == data.getIdColumn()) {
                    left.add(0, data.getData().get(i));
                    right.add(0, data.getData().get(i));
                    center.add(0, data.getData().get(i));
                } else if (r.nextDouble() <= 0.5) {
                    left.add(data.getData().get(i));
                } else {
                    right.add(data.getData().get(i));
                    center.add(data.getData().get(i));
                }
            }
            if (left.size() > 2 && right.size() > 2 && center.size() > 2) {
                //check if both slits have at least 2 attribute + ID.
                valid = true;
            }
        }

        List<Attribute> middlePresent = new ArrayList<>();
        List<Attribute> rightPresent = new ArrayList<>();
        boolean done = false;
        while (!done) {
            int rightC = 0;
            int centerC = 0;
            middlePresent = new ArrayList<>();
            rightPresent = new ArrayList<>();

            for (int i = 0; i < data.getNumberOfIndividuals(); i++) {
                Attribute present = new Attribute(Attribute.AttributeType.bool, "true", "locallyPresent");
                Attribute absent = new Attribute(Attribute.AttributeType.bool, "false", "locallyPresent");
                double ran = r.nextDouble();
                if (ran < 0.5) {
                    rightC++;
                    rightPresent.add(present);
                    middlePresent.add(absent);
                } else {
                    centerC++;
                    rightPresent.add(absent);
                    middlePresent.add(present);
                }
            }
            done = rightC >= 50 && centerC >= 50;
        }

        right.add(rightPresent);
        center.add(middlePresent);
        dataToArff(new Data(0, -1, left), LEFT);
        dataToArff(new Data(0, -1, right), RIGHT);
        dataToArff(new Data(0, -1, center), CENTER);

        createLocalHybrid(left, right, center);
    }

    private void createLocalHybrid(List<List<Attribute>> left, List<List<Attribute>> right,
                                   List<List<Attribute>> center) {
        int leftTarget = -1;
        int rightTarget = -1;
        int centerTarget = -1;
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
        for (int i = 0; i < center.size(); i++) {
            if (center.get(i).get(0).getAttributeName().equals(TARGET)) {
                centerTarget = i;
            }
        }
        if (leftTarget < 0) {
            if (rightTarget < 0) {
                left.add(center.get(centerTarget));
            } else {
                left.add(right.get(rightTarget));
            }
        }
        if (rightTarget < 0) {
            if (leftTarget < 0) {
                right.add(center.get(centerTarget));
            } else {
                right.add(left.get(leftTarget));
            }
        }
        if (centerTarget < 0) {
            if (rightTarget < 0) {
                center.add(left.get(leftTarget));
            } else {
                center.add(right.get(rightTarget));
            }
        }

        int index = -1;

        for (int i = 0; i < left.size(); i++) {
            if (left.get(i).get(0).getAttributeName().equals("locallyPresent")) {
                index = i;
                break;
            }
        }
        if (index > -1) {
            left.remove(index);
        }
        index = -1;
        for (int i = 0; i < right.size(); i++) {
            if (right.get(i).get(0).getAttributeName().equals("locallyPresent")) {
                index = i;
                break;
            }
        }
        if (index > -1) {
            right.remove(index);
        }
        index = -1;
        for (int i = 0; i < center.size(); i++) {
            if (center.get(i).get(0).getAttributeName().equals("locallyPresent")) {
                index = i;
                break;
            }
        }
        if (index > -1) {
            center.remove(index);
        }

        dataToArff(new Data(0, -1, left), LEFT_LOCAL);
        dataToArff(new Data(0, -1, right), RIGHT_LOCAL);
        dataToArff(new Data(0, -1, center), CENTER_LOCAL);
    }

    private List<List<Attribute>> removeNotPresent(List<List<Attribute>> data) {
        int index = 0;

        List<List<Attribute>> copy = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            copy.add(new ArrayList<>());
        }

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).get(0).getAttributeName().equals("locallyPresent")) {
                index = i;
                break;
            }
        }
        int count = 0;
        for (int i = 0; i < data.get(0).size(); i++) {
            boolean present = Boolean.valueOf(data.get(index).get(i).getValue());
            if (present) {
                count++;
                for (int j = 0; j < data.size(); j++) {
                    copy.get(j).add(data.get(j).get(i));
                }
            }

        }
        return data;
    }
}
