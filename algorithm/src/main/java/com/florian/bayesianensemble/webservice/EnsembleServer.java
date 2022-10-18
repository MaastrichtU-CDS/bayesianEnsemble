package com.florian.bayesianensemble.webservice;

import com.florian.bayesianensemble.webservice.domain.CollectNodesRequest;
import com.florian.bayesianensemble.webservice.domain.internal.InternalNetwork;
import com.florian.nscalarproduct.data.Attribute;
import com.florian.nscalarproduct.webservice.ServerEndpoint;
import com.florian.vertibayes.bayes.Node;
import com.florian.vertibayes.webservice.BayesServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EnsembleServer extends BayesServer {

    public EnsembleServer(String id, List<ServerEndpoint> endpoints) {
        this.serverId = id;
        this.setEndpoints(endpoints);
    }

    public EnsembleServer(String path, String id) {
        super(path, id);
    }

    @PostMapping ("getNodes")
    public InternalNetwork getNodes(CollectNodesRequest req) {
        List<Node> nodes = new ArrayList<>();
        for (String name : req.getNames()) {
            Integer relevantColumn = getData().getAttributeCollumn(name);
            if (relevantColumn != null) {
                Attribute target = getData().getData().get(relevantColumn).get(0);
                nodes.add(new Node(target.getAttributeName(), (Set) this.getUniqueValues(target.getAttributeName()),
                                   target.getType()));
            }
        }
        InternalNetwork network = new InternalNetwork();
        network.setNodes(nodes);
        return network;
    }

    @GetMapping ("getAllNodes")
    public InternalNetwork getAllNodes() {
        List<Node> nodes = new ArrayList<>();
        List<List<Attribute>> data = getData().getData();
        for (int i = 0; i < data.size(); i++) {
            if (i != getData().getIdColumn() && i != getData().getLocalPresenceColumn()) {
                Attribute target = getData().getData().get(i).get(0);
                nodes.add(new Node(target.getAttributeName(), (Set) this.getUniqueValues(target.getAttributeName()),
                                   target.getType()));
            }
        }
        InternalNetwork network = new InternalNetwork();
        network.setNodes(nodes);
        return network;
    }
}
