package com.florian.bayesianensemble.webservice;

import com.florian.bayesianensemble.webservice.domain.CollectNodesRequest;
import com.florian.bayesianensemble.webservice.domain.CreateEnsembleRequest;
import com.florian.bayesianensemble.webservice.domain.EnsembleResponse;
import com.florian.bayesianensemble.webservice.domain.internal.ValidateRequest;
import com.florian.nscalarproduct.webservice.ServerEndpoint;
import com.florian.vertibayes.bayes.Network;
import com.florian.vertibayes.bayes.Node;
import com.florian.vertibayes.webservice.VertiBayesCentralServer;
import com.florian.vertibayes.webservice.VertiBayesEndpoint;
import com.florian.vertibayes.webservice.domain.CreateNetworkRequest;
import com.florian.vertibayes.webservice.domain.external.ExpectationMaximizationWekaResponse;
import com.florian.vertibayes.webservice.domain.external.WebBayesNetwork;
import com.florian.vertibayes.webservice.domain.external.WebNode;
import org.apache.commons.collections.map.HashedMap;
import weka.classifiers.bayes.BayesNet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.florian.bayesianensemble.util.Util.findNode;
import static com.florian.vertibayes.webservice.mapping.WebNodeMapper.mapWebNodeFromNode;
import static com.florian.vertibayes.weka.BifMapper.fromWekaBif;

public class EnsembleCentralServer extends VertiBayesCentralServer {

    public EnsembleResponse createEnsemble(CreateEnsembleRequest req) throws Exception {
        int[] folds = createFolds(req.getFolds());
        if (req.getFolds() > 1) {
            return kfoldEnsemble(req, folds);
        } else {
            return noFoldEnsemble(req, folds);
        }
    }

    private EnsembleResponse kfoldEnsemble(CreateEnsembleRequest req, int[] folds) throws Exception {

        Map<String, Double> aucs = new HashMap<>();

        for (int i = 0; i < req.getFolds(); i++) {
            initFold(folds, i);

            Map<String, BayesNet> bayesNets = performEnsemble(req);
            initValidationFold(folds, i);
            Map<String, Double> foldAucs = validateEnsemble(req, bayesNets);
            for (String key : foldAucs.keySet()) {
                if (aucs.get(key) == null) {
                    aucs.put(key, foldAucs.get(key));
                } else {
                    aucs.put(key, aucs.get(key) + foldAucs.get(key));
                }
            }
        }

        //average AUC
        for (String key : aucs.keySet()) {
            aucs.put(key, aucs.get(key) / req.getFolds());
        }

        //Train final model
        activateAll(folds);
        Map<String, BayesNet> bayesNets = performEnsemble(req);

        //create response
        EnsembleResponse response = createEnsembleEsponse(aucs, bayesNets);
        return response;
    }

    private EnsembleResponse noFoldEnsemble(CreateEnsembleRequest req, int[] folds) throws Exception {
        Map<String, BayesNet> bayesNets = performEnsemble(req);
        activateAll(folds);

        Map<String, Double> aucs = validateEnsemble(req, bayesNets);

        //create response
        EnsembleResponse response = createEnsembleEsponse(aucs, bayesNets);
        return response;
    }

    private Map<String, BayesNet> performEnsemble(CreateEnsembleRequest req) throws Exception {
        Node target = getTargetNode(req.getTarget());
        Map<String, BayesNet> bayesNets = new HashMap<>();


        for (ServerEndpoint e : getEndpoints()) {
            List<Node> network = getLocalNodes((EnsembleEndpoint) e, target);
            setBins(network, req);
            setUseLocalData(req.isHybrid(), (EnsembleEndpoint) e);
            network = learnStructure(network, req.getMinPercentage());
            WebBayesNetwork n = new WebBayesNetwork();
            n.setNodes(mapWebNodeFromNode(network));
            n.setWekaResponse(true);
            n.setTarget(target.getName());
            ExpectationMaximizationWekaResponse res = (ExpectationMaximizationWekaResponse) expectationMaximization(n);
            bayesNets.put(e.getServerId(), res.getWeka());
        }

        return bayesNets;
    }

    private void setBins(List<Node> network, CreateEnsembleRequest req) {
        if (req.getNetworks() != null) {
            for (WebNode n : req.getNetworks()) {
                Node node = findNode(n.getName(), network);
                if (node != null) {
                    node.setBins(n.getBins());
                }
            }
        }
    }

    private void setUseLocalData(boolean hybrid, EnsembleEndpoint e) {
        //make sure every other endpoint is set to use full data:
        for (ServerEndpoint end : getEndpoints()) {
            ((VertiBayesEndpoint) end).setUseLocalOnly(false);
        }
        //check if purely local data should be used for the local endpoint
        if (hybrid) {
            e.setUseLocalOnly(true);
        }
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

    private List<Node> learnStructure(List<Node> nodes, int minpercentage) {
        Network n = new Network(getEndpoints(), getSecretEndpoint(), this, getEndpoints().get(0).getPopulation());
        n.setNodes(nodes);
        CreateNetworkRequest req = new CreateNetworkRequest();
        req.setNodes(mapWebNodeFromNode(nodes));
        req.setMinPercentage(minpercentage);
        n.createNetwork(req);
        return n.getNodes();
    }

    private EnsembleResponse createEnsembleEsponse(Map<String, Double> aucs, Map<String, BayesNet> bayesNets)
            throws Exception {
        EnsembleResponse response = new EnsembleResponse();
        response.setAucs(aucs);
        List<List<WebNode>> nets = new ArrayList<>();
        for (BayesNet net : bayesNets.values()) {
            nets.add(fromWekaBif(net.graph()));
        }
        response.setNetworks(nets);
        return response;
    }

    private Map<String, Double> validateEnsemble(CreateEnsembleRequest req, Map<String, BayesNet> bayesNets)
            throws Exception {
        ValidateRequest validate = new ValidateRequest();
        validate.setNetworks(bayesNets);
        validate.setTarget(req.getTarget());
        Map<String, Integer> count = new HashedMap();
        Map<String, Double> aucs = new HashedMap();

        for (ServerEndpoint e : getEndpoints()) {
            Map<String, Double> foldAUC = ((EnsembleEndpoint) e).validate(validate).getAucs();
            for (String key : foldAUC.keySet()) {
                if (aucs.get(key) == null) {
                    count.put(key, 1);
                    aucs.put(key, foldAUC.get(key));
                } else {
                    aucs.put(key, foldAUC.get(key) + aucs.get(key));
                    count.put(key, count.get(key) + 1);
                }
            }
        }
        for (String key : aucs.keySet()) {
            // average in case of a hybrid split
            aucs.put(key, aucs.get(key) / count.get(key));
        }
        return aucs;
    }
}

