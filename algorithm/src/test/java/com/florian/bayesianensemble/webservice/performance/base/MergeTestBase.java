package com.florian.bayesianensemble.webservice.performance.base;

import com.florian.bayesianensemble.webservice.EnsembleCentralServer;
import com.florian.bayesianensemble.webservice.EnsembleEndpoint;
import com.florian.bayesianensemble.webservice.EnsembleServer;
import com.florian.bayesianensemble.webservice.domain.CreateEnsembleRequest;
import com.florian.bayesianensemble.webservice.domain.EnsembleResponse;
import com.florian.bayesianensemble.webservice.performance.tests.SmallDiabetesTest;
import com.florian.nscalarproduct.webservice.ServerEndpoint;
import com.florian.vertibayes.webservice.BayesServer;
import com.florian.vertibayes.webservice.VertiBayesCentralServer;
import com.florian.vertibayes.webservice.VertiBayesEndpoint;
import com.florian.vertibayes.webservice.domain.CreateNetworkRequest;
import com.florian.vertibayes.webservice.domain.external.ExpectationMaximizationResponse;
import com.florian.vertibayes.webservice.domain.external.WebBayesNetwork;
import com.florian.vertibayes.webservice.domain.external.WebNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.florian.vertibayes.weka.BifMapper.fromOpenMarkovBif;
import static com.florian.vertibayes.weka.BifMapper.toOpenMarkovBif;

public class MergeTestBase {

    final String LEFT = "resources/Experiments/left.arff";
    final String RIGHT = "resources/Experiments/right.arff";

    public void compare() throws Exception {
        SmallDiabetesTest.testPerformanceAutomatic();
        EnsembleResponse e = trainEnsemble("Outcome");
        System.out.println(e.getWeightedAUC());
        List<List<WebNode>> networks = new ArrayList<>();
        for (String n : e.getNetworks()) {
            networks.add(fromOpenMarkovBif(n));
        }

        List<WebNode> network = new ArrayList<>();
        for (List<WebNode> n : networks) {
            for (WebNode wb : n) {
                if (find(network, wb.getName()) != null) {
                    continue;
                }
                WebNode copy = new WebNode();
                copy.setName(wb.getName());
                copy.setType(wb.getType());
                if (wb.getBins() != null && wb.getBins().size() > 0) {
                    copy.setBins(wb.getBins());
                }
                network.add(copy);
            }
        }

        //copy all links:
        for (List<WebNode> n : networks) {
            for (WebNode wb : n) {
                WebNode copy = find(network, wb.getName());
                copy.getParents().addAll(wb.getParents());
            }
        }

        ExpectationMaximizationResponse r = vertiBayesComparison("Outcome", network);
        System.out.println(r.getScvAuc());

        ExpectationMaximizationResponse r2 = vertiBayesComparison("Outcome");
        System.out.println(r2.getScvAuc());


        System.out.println(toOpenMarkovBif(r.getNodes()));
        System.out.println();
        System.out.println(toOpenMarkovBif(r2.getNodes()));
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

    private ExpectationMaximizationResponse vertiBayesComparison(String TARGET, List<WebNode> nodes) throws Exception {
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


        WebBayesNetwork req = new WebBayesNetwork();
        req.setNodes(nodes);
        req.setTarget(TARGET);
        req.setFolds(1);
        req.setTrainStructure(false);

        return central.expectationMaximization(req);
    }

    private EnsembleResponse trainEnsemble(String target) throws Exception {
        EnsembleServer station1 = new EnsembleServer(LEFT,
                                                     "1");
        EnsembleServer station2 = new EnsembleServer(RIGHT,
                                                     "2");
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
        req.setTarget(target);
        return central.createEnsemble(req);
    }

    private WebNode find(List<WebNode> network, String name) {
        for (WebNode n : network) {
            if (n.getName().equals(name)) {
                return n;
            }
        }
        return null;
    }
}
