package com.florian.bayesianensemble.webservice;

import com.florian.bayesianensemble.webservice.domain.CollectNodesRequest;
import com.florian.bayesianensemble.webservice.domain.internal.DecryptionRequest;
import com.florian.nscalarproduct.data.Attribute;
import com.florian.nscalarproduct.encryption.PublicPaillierKey;
import com.florian.vertibayes.bayes.Node;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnsembleServerTest {

    @Test
    public void testGetTargetNode() {
        EnsembleServer server = new EnsembleServer("resources/Experiments/k2/smallK2Example_firsthalf.csv",
                                                   "1");
        CollectNodesRequest req = new CollectNodesRequest();
        String target = "x1";
        String nonsense = "nonsense";
        req.getNames().add(target);
        req.getNames().add(nonsense);
        List<Node> nodes = server.getNodes(req).getNodes();
        assertEquals(nodes.size(), 1);
        assertEquals(nodes.get(0).getName(), target);
        assertEquals(nodes.get(0).getType(), Attribute.AttributeType.string);
    }

    @Test
    public void testEncryption() {
        EnsembleServer server = new EnsembleServer("resources/Experiments/k2/smallK2Example_firsthalf.csv",
                                                   "1");
        PublicPaillierKey key = server.generatePaillierKey("test");
        double original = 0.1;
        int PRECISION = 5;
        BigInteger value = BigInteger.valueOf((long) (original * Math.pow(10, PRECISION)));
        BigInteger encrypted = key.encrypt(value);
        DecryptionRequest req = new DecryptionRequest();
        req.setValue(encrypted);
        req.setName("test");
        assertEquals(server.decrypt(req).doubleValue() / Math.pow(10, PRECISION), original);

    }
}