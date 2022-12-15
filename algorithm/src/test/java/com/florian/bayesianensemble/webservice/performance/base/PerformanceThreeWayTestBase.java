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

        p.setAverageTime(time);

        p.setWeightedAUCRight(weightedAUCRight);
        p.setWeightedAUCCenter(weightedAUCCenter);
        p.setWeightedAUCLeft(weightedAUCLeft);
        p.setWeightedAUCEnsemble(weightedAUCEnsemble);
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
                //check if both slits have at least 1 attribute + ID.
                valid = true;
            }
        }
        dataToArff(new Data(0, -1, left), LEFT);
        dataToArff(new Data(0, -1, right), RIGHT);
        dataToArff(new Data(0, -1, center), CENTER);

        createLocal(left, right, center);
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
}
