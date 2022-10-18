package com.florian.bayesianensemble.webservice;

import com.florian.bayesianensemble.webservice.domain.CollectNodesRequest;
import com.florian.bayesianensemble.webservice.domain.CreateEnsembleRequest;
import com.florian.bayesianensemble.webservice.domain.EnsembleResponse;
import com.florian.nscalarproduct.webservice.ServerEndpoint;
import com.florian.vertibayes.bayes.Network;
import com.florian.vertibayes.bayes.Node;
import com.florian.vertibayes.webservice.VertiBayesCentralServer;
import com.florian.vertibayes.webservice.domain.external.ExpectationMaximizationResponse;
import com.florian.vertibayes.webservice.domain.external.WebBayesNetwork;
import com.florian.vertibayes.webservice.mapping.WebNodeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnsembleCentralServer extends VertiBayesCentralServer {

    public EnsembleResponse createEnsemble(CreateEnsembleRequest req) throws Exception {
        Node target = getTargetNode(req.getTarget());

        List<ExpectationMaximizationResponse> networks = new ArrayList<>();
        for (ServerEndpoint e : getEndpoints()) {
            List<Node> network = getLocalNodes((EnsembleEndpoint) e, target);
            learnStructure(network);
            WebBayesNetwork n = new WebBayesNetwork();
            n.setNodes(WebNodeMapper.mapWebNodeFromNode(network));
            n.setOpenMarkovResponse(true);
            n.setTarget(target.getName());
            networks.add(expectationMaximization(n));
        }
        EnsembleResponse response = new EnsembleResponse();
        response.setNetworks(networks);
        return response;
    }

    private List<Node> getLocalNodes(EnsembleEndpoint e, Node target) {
        List<Node> nodes = e.getAllNodes().getNodes();
        boolean contained = false;
        for (Node n : nodes) {
            if (n.getName().equals(target.getName())) {
                contained = true;
                break;
            }
        }
        if (!contained) {
            nodes.add(target);
        }
        return nodes;
    }

    private Node getTargetNode(String target) {
        CollectNodesRequest targetReq = new CollectNodesRequest();
        targetReq.getNames().add(target);
        List<Node> targetList = getEndpoints().stream().map(x -> ((EnsembleEndpoint) x).getNodes(targetReq))
                .collect(Collectors.toList()).get(0).getNodes();
        return targetList.get(0);
    }

    private List<Node> learnStructure(List<Node> nodes) {
        Network n = new Network(getEndpoints(), getSecretEndpoint(), this, getEndpoints().get(0).getPopulation());
        n.setNodes(nodes);
        n.createNetwork();
        return n.getNodes();
    }
}
