package com.florian.bayesianensemble.webservice;

import com.florian.bayesianensemble.webservice.domain.CreateEnsembleRequest;
import com.florian.bayesianensemble.webservice.domain.EnsembleResponse;
import com.florian.nscalarproduct.webservice.ServerEndpoint;
import com.florian.vertibayes.webservice.domain.external.WebNode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        List<List<WebNode>> networks = response.getNetworks();

        assertEquals(networks.size(), 2);

        List<WebNode> network1 = networks.get(0);
        List<WebNode> network2 = networks.get(1);

        //assert that the only node that is present in both is the target node
        for (WebNode n : network1) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network2, n));
            } else {
                assertFalse(checkNodeIsPresent(network2, n));
            }
        }

        for (WebNode n : network2) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network1, n));
            } else {
                assertFalse(checkNodeIsPresent(network1, n));
            }
        }

        //check expected network
        //network 1: x1 -> x2
        //network 2: x3 -> x1
        //probabilities are not

        assertEquals(network1.get(0).getName(), "x1");
        assertEquals(network1.get(0).getParents().size(), 0);

        assertEquals(network1.get(1).getName(), "x2");
        assertEquals(network1.get(1).getParents().size(), 1);
        assertEquals(network1.get(1).getParents().get(0), "x1");

        assertEquals(network2.get(0).getName(), "x3");
        assertEquals(network2.get(0).getParents().size(), 0);

        assertEquals(network2.get(1).getName(), "x1");
        assertEquals(network2.get(1).getParents().size(), 1);
        assertEquals(network2.get(1).getParents().get(0), "x3");

        //assert the AUCS are correctly calculate for this small dataset with no internal logic
        assertEquals(response.getAucs().get("1"), 0.52, 0.01);
        assertEquals(response.getAucs().get("0"), 0.52, 0.01);

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
        req.setFolds(10);
        EnsembleResponse response = central.createEnsemble(req);
        List<List<WebNode>> networks = response.getNetworks();

        assertEquals(networks.size(), 2);

        List<WebNode> network1 = networks.get(0);
        List<WebNode> network2 = networks.get(1);

        //assert that the only node that is present in both is the target node
        for (WebNode n : network1) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network2, n));
            } else {
                assertFalse(checkNodeIsPresent(network2, n));
            }
        }

        for (WebNode n : network2) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network1, n));
            } else {
                assertFalse(checkNodeIsPresent(network1, n));
            }
        }

        //check expected network
        //network 1: x1 -> x2
        //network 2: x3 -> x1
        //probabilities are not

        assertEquals(network1.get(0).getName(), "x1");
        assertEquals(network1.get(0).getParents().size(), 0);

        assertEquals(network1.get(1).getName(), "x2");
        assertEquals(network1.get(1).getParents().size(), 1);
        assertEquals(network1.get(1).getParents().get(0), "x1");

        assertEquals(network2.get(0).getName(), "x3");
        assertEquals(network2.get(0).getParents().size(), 0);

        assertEquals(network2.get(1).getName(), "x1");
        assertEquals(network2.get(1).getParents().size(), 1);
        assertEquals(network2.get(1).getParents().get(0), "x3");

        //assert the AUCS are correctly calculate for this small dataset with no internal logic
        assertEquals(response.getAucs().get("1"), 0.52, 0.01);
        assertEquals(response.getAucs().get("0"), 0.52, 0.01);

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
        List<List<WebNode>> networks = response.getNetworks();

        assertEquals(networks.size(), 3);

        List<WebNode> network1 = networks.get(0);
        List<WebNode> network2 = networks.get(1);
        List<WebNode> network3 = networks.get(2);

        //check network 1 & 2 have the same nodes, but only share the target with network 3


        for (WebNode n : network1) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network3, n));
            } else {
                assertFalse(checkNodeIsPresent(network3, n));
            }
            assertTrue(checkNodeIsPresent(network2, n));
        }

        for (WebNode n : network2) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network3, n));
            } else {
                assertFalse(checkNodeIsPresent(network3, n));
            }
            assertTrue(checkNodeIsPresent(network1, n));
        }

        //check expected network
        //network 1: x1 -> x2
        //network 2: x3 -> x1
        //probabilities are not

        assertEquals(network1.get(0).getName(), "x1");
        assertEquals(network1.get(0).getParents().size(), 0);

        assertEquals(network1.get(1).getName(), "x2");
        assertEquals(network1.get(1).getParents().size(), 1);
        assertEquals(network1.get(1).getParents().get(0), "x1");

        assertEquals(network2.get(0).getName(), "x1");
        assertEquals(network2.get(0).getParents().size(), 0);

        assertEquals(network2.get(1).getName(), "x2");
        assertEquals(network2.get(1).getParents().size(), 1);
        assertEquals(network2.get(1).getParents().get(0), "x1");

        assertEquals(network3.get(0).getName(), "x3");
        assertEquals(network3.get(0).getParents().size(), 0);

        assertEquals(network3.get(1).getName(), "x1");
        assertEquals(network3.get(1).getParents().size(), 1);
        assertEquals(network3.get(1).getParents().get(0), "x3");

        //check probabilities are different for the two networks that are hybridly split
        assertEquals(network1.get(0).getProbabilities().get(0).getP(), 0.40, 0.01);
        assertEquals(network2.get(0).getProbabilities().get(0).getP(), 0.60, 0.01);


        //assert the AUCS are correctly calculate for this small dataset with little to no internal logic
        assertEquals(response.getAucs().get("1"), 0.52, 0.01);
        assertEquals(response.getAucs().get("0"), 0.52, 0.01);

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
        req.setFolds(10);
        EnsembleResponse response = central.createEnsemble(req);
        List<List<WebNode>> networks = response.getNetworks();

        assertEquals(networks.size(), 3);

        List<WebNode> network1 = networks.get(0);
        List<WebNode> network2 = networks.get(1);
        List<WebNode> network3 = networks.get(2);

        //check network 1 & 2 have the same nodes, but only share the target with network 3


        for (WebNode n : network1) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network3, n));
            } else {
                assertFalse(checkNodeIsPresent(network3, n));
            }
            assertTrue(checkNodeIsPresent(network2, n));
        }

        for (WebNode n : network2) {
            if (n.getName().equals(target)) {
                assertTrue(checkNodeIsPresent(network3, n));
            } else {
                assertFalse(checkNodeIsPresent(network3, n));
            }
            assertTrue(checkNodeIsPresent(network1, n));
        }

        //check expected network
        //network 1: x1 -> x2
        //network 2: x3 -> x1
        //probabilities are not

        assertEquals(network1.get(0).getName(), "x1");
        assertEquals(network1.get(0).getParents().size(), 0);

        assertEquals(network1.get(1).getName(), "x2");
        assertEquals(network1.get(1).getParents().size(), 1);
        assertEquals(network1.get(1).getParents().get(0), "x1");

        assertEquals(network2.get(0).getName(), "x1");
        assertEquals(network2.get(0).getParents().size(), 0);

        assertEquals(network2.get(1).getName(), "x2");
        assertEquals(network2.get(1).getParents().size(), 1);
        assertEquals(network2.get(1).getParents().get(0), "x1");

        assertEquals(network3.get(0).getName(), "x3");
        assertEquals(network3.get(0).getParents().size(), 0);

        assertEquals(network3.get(1).getName(), "x1");
        assertEquals(network3.get(1).getParents().size(), 1);
        assertEquals(network3.get(1).getParents().get(0), "x3");

        //check probabilities are different for the two networks that are hybridly split
        assertEquals(network1.get(0).getProbabilities().get(0).getP(), 0.40, 0.02);
        assertEquals(network2.get(0).getProbabilities().get(0).getP(), 0.60, 0.02);


        //assert the AUCS are correctly calculate for this small dataset with little to no internal logic
        assertEquals(response.getAucs().get("1"), 0.52, 0.01);
        assertEquals(response.getAucs().get("0"), 0.52, 0.01);

    }

    private boolean checkNodeIsPresent(List<WebNode> nodes, WebNode n) {
        for (WebNode node : nodes) {
            if (n.getName().equals(node.getName())) {
                return true;
            }
        }
        return false;
    }
}