package com.florian.bayesianensemble.webservice;

import com.florian.bayesianensemble.webservice.domain.CreateEnsembleRequest;
import com.florian.bayesianensemble.webservice.domain.EnsembleResponse;
import com.florian.nscalarproduct.data.Attribute;
import com.florian.nscalarproduct.webservice.ServerEndpoint;
import com.florian.vertibayes.bayes.Bin;
import com.florian.vertibayes.webservice.domain.external.WebNode;
import org.junit.jupiter.api.Test;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.florian.bayesianensemble.openmarkov.OpenMarkovClassifier.loadModel;
import static org.junit.jupiter.api.Assertions.*;

public class EnsembleCentralServerTest {

    @Test
    public void testCreateEnsemble() throws Exception {
        EnsembleServer station1 = new EnsembleServer("resources/Experiments/k2/smallK2Example_firsthalf.csv",
                                                     "1");
        EnsembleServer station2 = new EnsembleServer("resources/Experiments/k2/smallK2Example_secondhalf.csv",
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
        String target = "x1";
        req.setTarget(target);
        EnsembleResponse response = central.createEnsemble(req);
        List<ProbNet> networks = new ArrayList<>();
        for (String net : response.getNetworks()) {
            networks.add(loadModel(net));
        }

        assertEquals(networks.size(), 2);

        ProbNet network1 = networks.get(0);
        ProbNet network2 = networks.get(1);
        List<Link<Node>> links_1 = network1.getLinks();
        List<Link<Node>> links_2 = network2.getLinks();

        //assert that the only node that is present in both is the target node
        for (Variable n : network1.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network2.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network2.getVariables(), n));
            }
        }

        for (Variable n : network2.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network1.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network1.getVariables(), n));
            }
        }
        //check expected network
        //network 1: x1 -> x2
        //network 2: x3 -> x1
        //probabilities are not explicitly checked


        assertEquals(links_1.size(), 1);
        assertEquals(links_1.get(0).getNode1().getName(), "x1");
        assertEquals(links_1.get(0).getNode1().getParents().size(), 0);
        assertEquals(links_1.get(0).getNode2().getName(), "x2");
        assertEquals(links_1.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_1.get(0).getNode2().getParents().get(0).getName(), "x1");


        assertEquals(links_2.size(), 1);
        assertEquals(links_2.get(0).getNode2().getName(), "x1");
        assertEquals(links_2.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_2.get(0).getNode2().getParents().get(0).getName(), "x3");
        assertEquals(links_2.get(0).getNode1().getName(), "x3");
        assertEquals(links_2.get(0).getNode1().getParents().size(), 0);


        assertEquals(response.getAucs().get("1"), 0.78, 0.01);
        assertEquals(response.getAucs().get("0"), 0.78, 0.01);

    }

    @Test
    public void testCreateEnsembleIris() throws Exception {
        EnsembleServer station1 = new EnsembleServer("resources/Experiments/iris/left.arff",
                                                     "1");
        EnsembleServer station2 = new EnsembleServer("resources/Experiments/iris/right.arff",
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
        String target = "label";
        req.setTarget(target);
        EnsembleResponse response = central.createEnsemble(req);
        List<ProbNet> networks = new ArrayList<>();
        for (String net : response.getNetworks()) {
            networks.add(loadModel(net));
        }

        assertEquals(networks.size(), 2);

        ProbNet network1 = networks.get(0);
        ProbNet network2 = networks.get(1);
        List<Link<Node>> links_1 = network1.getLinks();
        List<Link<Node>> links_2 = network2.getLinks();

        //assert that the only node that is present in both is the target node
        for (Variable n : network1.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network2.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network2.getVariables(), n));
            }
        }

        for (Variable n : network2.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network1.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network1.getVariables(), n));
            }
        }
        //check expected network
        //network 1: x1 -> x2
        //network 2: x3 -> x1
        //probabilities are not explicitly checked


        assertEquals(links_1.size(), 2);
        assertEquals(links_1.get(0).getNode1().getName(), "sepalwidth");
        assertEquals(links_1.get(0).getNode1().getParents().size(), 0);
        assertEquals(links_1.get(0).getNode2().getName(), "petalwidth");
        assertEquals(links_1.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_1.get(0).getNode2().getParents().get(0).getName(), "sepalwidth");
        assertEquals(links_1.get(1).getNode1().getName(), "petalwidth");
        assertEquals(links_1.get(1).getNode1().getParents().size(), 1);
        assertEquals(links_1.get(1).getNode1().getParents().get(0).getName(), "sepalwidth");
        assertEquals(links_1.get(1).getNode2().getName(), "label");
        assertEquals(links_1.get(1).getNode2().getParents().size(), 1);
        assertEquals(links_1.get(1).getNode2().getParents().get(0).getName(), "petalwidth");

        assertEquals(links_2.size(), 2);
        assertEquals(links_2.get(0).getNode1().getName(), "sepallength");
        assertEquals(links_2.get(0).getNode1().getParents().size(), 0);
        assertEquals(links_2.get(0).getNode2().getName(), "petallength");
        assertEquals(links_2.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_2.get(0).getNode2().getParents().get(0).getName(), "sepallength");

        assertEquals(links_2.get(1).getNode1().getName(), "petallength");
        assertEquals(links_2.get(1).getNode1().getParents().size(), 1);
        assertEquals(links_2.get(1).getNode1().getParents().get(0).getName(), "sepallength");
        assertEquals(links_2.get(1).getNode2().getName(), "label");
        assertEquals(links_2.get(1).getNode2().getParents().size(), 1);
        assertEquals(links_2.get(1).getNode2().getParents().get(0).getName(), "petallength");

        assertEquals(response.getAucs().get("Iris-setosa"), 0.95, 0.05);
        assertEquals(response.getAucs().get("Iris-versicolor"), 0.89, 0.05);
        assertEquals(response.getAucs().get("Iris-virginica"), 0.99, 0.05);
        assertEquals(response.getWeightedAUC(), 0.93, 0.05);
    }

    @Test
    public void testCreateEnsemblePredefinedStructure() throws Exception {
        EnsembleServer station1 = new EnsembleServer("resources/Experiments/k2/smallK2Example_firsthalf.csv",
                                                     "1");
        EnsembleServer station2 = new EnsembleServer("resources/Experiments/k2/smallK2Example_secondhalf.csv",
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
        String target = "x1";
        req.setTarget(target);

        EnsembleResponse response = central.createEnsemble(req);

        req.setNetworks(new HashMap<>());
        req.getNetworks().put("2", createX3Structured());
        EnsembleResponse responsePrestructured = central.createEnsemble(req);

        List<ProbNet> networks = new ArrayList<>();
        for (String net : response.getNetworks()) {
            networks.add(loadModel(net));
        }

        List<ProbNet> networksPrestuctured = new ArrayList<>();
        for (String net : responsePrestructured.getNetworks()) {
            networksPrestuctured.add(loadModel(net));
        }

        assertEquals(networks.size(), 2);
        assertEquals(networksPrestuctured.size(), 2);

        ProbNet network2 = networks.get(1);
        List<Link<Node>> links_2 = network2.getLinks();

        ProbNet network2Prestructured = networksPrestuctured.get(1);
        List<Link<Node>> links_2Prestructured = network2Prestructured.getLinks();

        assertEquals(links_2.size(), 1);
        assertEquals(links_2.get(0).getNode2().getName(), "x1");
        assertEquals(links_2.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_2.get(0).getNode2().getParents().get(0).getName(), "x3");
        assertEquals(links_2.get(0).getNode1().getName(), "x3");
        assertEquals(links_2.get(0).getNode1().getParents().size(), 0);

        //check that the prestructured network looks different, namely the parent-child relation is switched around

        assertEquals(links_2Prestructured.size(), 1);
        assertEquals(links_2Prestructured.get(0).getNode2().getName(), "x3");
        assertEquals(links_2Prestructured.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_2Prestructured.get(0).getNode2().getParents().get(0).getName(), "x1");
        assertEquals(links_2Prestructured.get(0).getNode1().getName(), "x1");
        assertEquals(links_2Prestructured.get(0).getNode1().getParents().size(), 0);


        assertEquals(response.getAucs().get("1"), 0.78, 0.01);
        assertEquals(response.getAucs().get("0"), 0.78, 0.01);
        assertEquals(response.getWeightedAUC(), 0.78, 0.01);

    }

    @Test
    public void testCreateEnsembleOpenMarkov() throws Exception {
        EnsembleServer station1 = new EnsembleServer("resources/Experiments/k2/smallK2Example_firsthalf.csv",
                                                     "1");
        EnsembleServer station2 = new EnsembleServer("resources/Experiments/k2/smallK2Example_secondhalf.csv",
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
        String target = "x1";
        req.setTarget(target);
        EnsembleResponse response = central.createEnsemble(req);

        List<ProbNet> networks = new ArrayList<>();
        for (String net : response.getNetworks()) {
            networks.add(loadModel(net));
        }

        assertEquals(networks.size(), 2);

        ProbNet network1 = networks.get(0);
        ProbNet network2 = networks.get(1);
        List<Link<Node>> links_1 = network1.getLinks();
        List<Link<Node>> links_2 = network2.getLinks();

        //assert that the only node that is present in both is the target node
        for (Variable n : network1.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network2.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network2.getVariables(), n));
            }
        }

        for (Variable n : network2.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network1.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network1.getVariables(), n));
            }
        }
        //check expected network
        //network 1: x1 -> x2
        //network 2: x3 -> x1
        //probabilities are not explicitly checked


        assertEquals(links_1.size(), 1);
        assertEquals(links_1.get(0).getNode1().getName(), "x1");
        assertEquals(links_1.get(0).getNode1().getParents().size(), 0);
        assertEquals(links_1.get(0).getNode2().getName(), "x2");
        assertEquals(links_1.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_1.get(0).getNode2().getParents().get(0).getName(), "x1");


        assertEquals(links_2.size(), 1);
        assertEquals(links_2.get(0).getNode2().getName(), "x1");
        assertEquals(links_2.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_2.get(0).getNode2().getParents().get(0).getName(), "x3");
        assertEquals(links_2.get(0).getNode1().getName(), "x3");
        assertEquals(links_2.get(0).getNode1().getParents().size(), 0);


        assertEquals(response.getAucs().get("1"), 0.78, 0.01);
        assertEquals(response.getAucs().get("0"), 0.78, 0.01);
        assertEquals(response.getWeightedAUC(), 0.78, 0.01);

    }

    @Test
    public void testCreateEnsembleBigData() throws Exception {
        EnsembleServer station1 = new EnsembleServer("resources/Experiments/k2/bigK2Example_firsthalf.csv",
                                                     "1");
        EnsembleServer station2 = new EnsembleServer("resources/Experiments/k2/bigK2Example_secondhalf.csv",
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
        String target = "x3";
        req.setTarget(target);
        req.setFolds(10);
        EnsembleResponse response = central.createEnsemble(req);

        List<ProbNet> networks = new ArrayList<>();
        for (String net : response.getNetworks()) {
            networks.add(loadModel(net));
        }

        assertEquals(networks.size(), 2);

        ProbNet network1 = networks.get(0);
        ProbNet network2 = networks.get(1);
        List<Link<Node>> links_1 = network1.getLinks();
        List<Link<Node>> links_2 = network2.getLinks();

        //assert that the only node that is present in both is the target node
        for (Variable n : network1.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network2.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network2.getVariables(), n));
            }
        }

        for (Variable n : network2.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network1.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network1.getVariables(), n));
            }
        }
        //check expected network
        //network 1: x2 -> x3, x1
        //network 2: x3
        //probabilities are not explicitly checked


        assertEquals(links_1.size(), 1);
        assertEquals(links_1.get(0).getNode1().getName(), "x2");
        assertEquals(links_1.get(0).getNode1().getParents().size(), 0);
        assertEquals(links_1.get(0).getNode2().getName(), "x3");
        assertEquals(links_1.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_1.get(0).getNode2().getParents().get(0).getName(), "x2");


        assertEquals(links_2.size(), 0);

        //this is the only model to correctly identify the data is random. Probably due to the fact that the others
        // have only 10 individuals
        assertEquals(response.getAucs().get("1"), 0.5, 0.01);
        assertEquals(response.getAucs().get("0"), 0.5, 0.01);
        assertEquals(response.getWeightedAUC(), 0.5, 0.01);

    }

    @Test
    public void testCreateEnsembleManualBinned() throws Exception {
        EnsembleServer station1 = new EnsembleServer("resources/Experiments/k2/smallK2Example_firsthalf.csv",
                                                     "1");
        EnsembleServer station2 = new EnsembleServer("resources/Experiments/k2/smallK2Example_secondhalf.csv",
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
        String target = "x1";
        req.setTarget(target);
        req.setBinned(createK2Nodes());

        EnsembleResponse response = central.createEnsemble(req);
        List<ProbNet> networks = new ArrayList<>();
        for (String net : response.getNetworks()) {
            networks.add(loadModel(net));
        }

        assertEquals(networks.size(), 2);

        ProbNet network1 = networks.get(0);
        ProbNet network2 = networks.get(1);
        List<Link<Node>> links_1 = network1.getLinks();
        List<Link<Node>> links_2 = network2.getLinks();

        //assert that the only node that is present in both is the target node
        for (Variable n : network1.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network2.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network2.getVariables(), n));
            }
        }

        for (Variable n : network2.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network1.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network1.getVariables(), n));
            }
        }
        //check expected network
        //network 1: x1 -> x2
        //network 2: x3 -> x1
        //probabilities are not explicitly checked


        assertEquals(links_1.size(), 1);
        assertEquals(links_1.get(0).getNode1().getName(), "x1");
        assertEquals(links_1.get(0).getNode1().getParents().size(), 0);
        assertEquals(links_1.get(0).getNode2().getName(), "x2");
        assertEquals(links_1.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_1.get(0).getNode2().getParents().get(0).getName(), "x1");


        assertEquals(links_2.size(), 1);
        assertEquals(links_2.get(0).getNode2().getName(), "x1");
        assertEquals(links_2.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_2.get(0).getNode2().getParents().get(0).getName(), "x3");
        assertEquals(links_2.get(0).getNode1().getName(), "x3");
        assertEquals(links_2.get(0).getNode1().getParents().size(), 0);

        assertEquals(response.getAucs().get("1"), 0.78, 0.01);
        assertEquals(response.getAucs().get("0"), 0.78, 0.01);
        assertEquals(response.getWeightedAUC(), 0.78, 0.01);

    }

    @Test
    public void testCreateEnsembleKfold() throws Exception {
        EnsembleServer station1 = new EnsembleServer("resources/Experiments/k2/smallK2Example_firsthalf.csv",
                                                     "1");
        EnsembleServer station2 = new EnsembleServer("resources/Experiments/k2/smallK2Example_secondhalf.csv",
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
        String target = "x1";
        req.setTarget(target);
        req.setFolds(2);
        EnsembleResponse response = central.createEnsemble(req);
        List<ProbNet> networks = new ArrayList<>();
        for (String net : response.getNetworks()) {
            networks.add(loadModel(net));
        }

        assertEquals(networks.size(), 2);

        ProbNet network1 = networks.get(0);
        ProbNet network2 = networks.get(1);
        List<Link<Node>> links_1 = network1.getLinks();
        List<Link<Node>> links_2 = network2.getLinks();

        //assert that the only node that is present in both is the target node
        for (Variable n : network1.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network2.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network2.getVariables(), n));
            }
        }

        for (Variable n : network2.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network1.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network1.getVariables(), n));
            }
        }
        //check expected network
        //network 1: x1 -> x2
        //network 2: x3 -> x1
        //probabilities are not explicitly checked


        assertEquals(links_1.size(), 1);
        assertEquals(links_1.get(0).getNode1().getName(), "x1");
        assertEquals(links_1.get(0).getNode1().getParents().size(), 0);
        assertEquals(links_1.get(0).getNode2().getName(), "x2");
        assertEquals(links_1.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_1.get(0).getNode2().getParents().get(0).getName(), "x1");


        assertEquals(links_2.size(), 1);
        assertEquals(links_2.get(0).getNode2().getName(), "x1");
        assertEquals(links_2.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_2.get(0).getNode2().getParents().get(0).getName(), "x3");
        assertEquals(links_2.get(0).getNode1().getName(), "x3");
        assertEquals(links_2.get(0).getNode1().getParents().size(), 0);

        //The AUC can differ a lot due to the fact that we're doing k-fold on 10 instances.
        // so not checking that one

    }

    @Test
    public void testCreateEnsembleHybridSplit() throws Exception {
        EnsembleServer station1 = new EnsembleServer(
                "resources/Experiments/hybridsplit/smallK2Example_firsthalf_hybrid1.csv",
                "1");
        EnsembleServer station2 = new EnsembleServer(
                "resources/Experiments/hybridsplit/smallK2Example_firsthalf_hybrid2.csv",
                "2");
        EnsembleServer station3 = new EnsembleServer("resources/Experiments/hybridsplit/smallK2Example_secondhalf.csv",
                                                     "3");
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
        String target = "x1";
        req.setTarget(target);
        req.setHybrid(true);
        EnsembleResponse response = central.createEnsemble(req);
        List<String> networks = response.getNetworks();

        assertEquals(networks.size(), 3);

        ProbNet network1 = loadModel(networks.get(0));
        ProbNet network2 = loadModel(networks.get(1));
        ProbNet network3 = loadModel(networks.get(2));

        //check network 1 & 2 have the same nodes, but only share the target with network 3

        for (Variable n : network1.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network3.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network3.getVariables(), n));
            }
            assertTrue(checkNodeIsPresent(network2.getVariables(), n));
        }

        for (Variable n : network2.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network3.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network3.getVariables(), n));
            }
            assertTrue(checkNodeIsPresent(network1.getVariables(), n));
        }

        //check expected network
        //network 1: x1 -> x2
        //network 2: x3 -> x1
        //probabilities are not
        List<Link<Node>> links_1 = network1.getLinks();
        List<Link<Node>> links_2 = network2.getLinks();
        List<Link<Node>> links_3 = network3.getLinks();

        assertEquals(links_1.size(), 1);
        assertEquals(links_1.get(0).getNode1().getName(), "x1");
        assertEquals(links_1.get(0).getNode1().getParents().size(), 0);
        assertEquals(links_1.get(0).getNode2().getName(), "x2");
        assertEquals(links_1.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_1.get(0).getNode2().getParents().get(0).getName(), "x1");

        assertEquals(links_2.size(), 1);
        assertEquals(links_2.get(0).getNode1().getName(), "x1");
        assertEquals(links_2.get(0).getNode1().getParents().size(), 0);
        assertEquals(links_2.get(0).getNode2().getName(), "x2");
        assertEquals(links_2.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_2.get(0).getNode2().getParents().get(0).getName(), "x1");


        assertEquals(links_3.size(), 1);
        assertEquals(links_3.get(0).getNode2().getName(), "x1");
        assertEquals(links_3.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_3.get(0).getNode2().getParents().get(0).getName(), "x3");
        assertEquals(links_3.get(0).getNode1().getName(), "x3");
        assertEquals(links_3.get(0).getNode1().getParents().size(), 0);

        //check probabilities are different for the two networks that are hybridly split
        assertEquals(network1.getPotentials().get(0).getVariables().get(0).getName(), "x1");
        assertEquals(network2.getPotentials().get(0).getVariables().get(0).getName(), "x1");
        assertEquals(network1.getPotentials().get(0).getCPT().values[0], 0.40, 0.02);
        assertEquals(network2.getPotentials().get(0).getCPT().values[0], 0.60, 0.02);

        //assert the AUCS are correctly calculate for this small dataset with little to no internal logic
        assertEquals(response.getAucs().get("1"), 0.85, 0.025);
        assertEquals(response.getAucs().get("0"), 0.85, 0.025);
        assertEquals(response.getWeightedAUC(), 0.86, 0.025);
    }

    @Test
    public void testCreateEnsembleHybridSplitKFold() throws Exception {
        EnsembleServer station1 = new EnsembleServer(
                "resources/Experiments/hybridsplit/smallK2Example_firsthalf_hybrid1.csv",
                "1");
        EnsembleServer station2 = new EnsembleServer(
                "resources/Experiments/hybridsplit/smallK2Example_firsthalf_hybrid2.csv",
                "2");
        EnsembleServer station3 = new EnsembleServer("resources/Experiments/hybridsplit/smallK2Example_secondhalf.csv",
                                                     "3");
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
        String target = "x1";
        req.setTarget(target);
        req.setHybrid(true);
        req.setFolds(2);
        EnsembleResponse response = central.createEnsemble(req);
        List<String> networks = response.getNetworks();

        assertEquals(networks.size(), 3);

        ProbNet network1 = loadModel(networks.get(0));
        ProbNet network2 = loadModel(networks.get(1));
        ProbNet network3 = loadModel(networks.get(2));

        //check network 1 & 2 have the same nodes, but only share the target with network 3

        for (Variable n : network1.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network3.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network3.getVariables(), n));
            }
            assertTrue(checkNodeIsPresent(network2.getVariables(), n));
        }

        for (Variable n : network2.getVariables()) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network3.getVariables(), n));
            } else {
                assertFalse(checkNodeIsPresent(network3.getVariables(), n));
            }
            assertTrue(checkNodeIsPresent(network1.getVariables(), n));
        }

        //check expected network
        //network 1: x1 -> x2
        //network 2: x3 -> x1
        //probabilities are not
        List<Link<Node>> links_1 = network1.getLinks();
        List<Link<Node>> links_2 = network2.getLinks();
        List<Link<Node>> links_3 = network3.getLinks();

        assertEquals(links_1.size(), 1);
        assertEquals(links_1.get(0).getNode1().getName(), "x1");
        assertEquals(links_1.get(0).getNode1().getParents().size(), 0);
        assertEquals(links_1.get(0).getNode2().getName(), "x2");
        assertEquals(links_1.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_1.get(0).getNode2().getParents().get(0).getName(), "x1");

        assertEquals(links_2.size(), 1);
        assertEquals(links_2.get(0).getNode1().getName(), "x1");
        assertEquals(links_2.get(0).getNode1().getParents().size(), 0);
        assertEquals(links_2.get(0).getNode2().getName(), "x2");
        assertEquals(links_2.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_2.get(0).getNode2().getParents().get(0).getName(), "x1");


        assertEquals(links_3.size(), 1);
        assertEquals(links_3.get(0).getNode2().getName(), "x1");
        assertEquals(links_3.get(0).getNode2().getParents().size(), 1);
        assertEquals(links_3.get(0).getNode2().getParents().get(0).getName(), "x3");
        assertEquals(links_3.get(0).getNode1().getName(), "x3");
        assertEquals(links_3.get(0).getNode1().getParents().size(), 0);

        //check probabilities are different for the two networks that are hybridly split
        assertEquals(network1.getPotentials().get(0).getVariables().get(0).getName(), "x1");
        assertEquals(network2.getPotentials().get(0).getVariables().get(0).getName(), "x1");
        assertEquals(network1.getPotentials().get(0).getCPT().values[0], 0.40, 0.02);
        assertEquals(network2.getPotentials().get(0).getCPT().values[0], 0.60, 0.02);


        //The AUC can differ a lot due to the fact that we're doing k-fold on 10 instances.
        //so not checking that one


    }

    private boolean checkNodeIsPresent(List<Variable> variables, Variable v) {
        for (Variable variable : variables) {
            if (variable.getName().equals(v.getName())) {
                return true;
            }
        }
        return false;
    }

    public static List<WebNode> createK2Nodes() {
        List<WebNode> nodes = new ArrayList<>();
        WebNode x1 = new WebNode();
        x1.setType(Attribute.AttributeType.numeric);
        x1.setName("x1");
        WebNode x2 = new WebNode();
        x2.setType(Attribute.AttributeType.numeric);
        x2.setName("x2");
        WebNode x3 = new WebNode();
        x3.setType(Attribute.AttributeType.string);
        x3.setName("x3");

        nodes.add(x1);
        nodes.add(x2);
        nodes.add(x3);

        Bin one = new Bin();
        one.setUpperLimit("1.5");
        one.setLowerLimit("0.5");

        Bin zero = new Bin();
        zero.setUpperLimit("0.5");
        zero.setLowerLimit("-0.5");

        for (WebNode node : nodes) {
            if (!node.getName().equals("x3")) {
                node.getBins().add(zero);
                node.getBins().add(one);
            }
        }

        return nodes;

    }

    public static List<WebNode> createX3Structured() {
        List<WebNode> nodes = new ArrayList<>();
        WebNode x1 = new WebNode();
        x1.setType(Attribute.AttributeType.string);
        x1.setName("x1");
        WebNode x3 = new WebNode();
        x3.setType(Attribute.AttributeType.string);
        x3.setName("x3");

        nodes.add(x1);
        nodes.add(x3);

        x3.getParents().add("x1");

        return nodes;

    }
}