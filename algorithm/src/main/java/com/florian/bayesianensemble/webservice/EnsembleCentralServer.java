package com.florian.bayesianensemble.webservice;

import com.florian.bayesianensemble.webservice.domain.CollectNodesRequest;
import com.florian.bayesianensemble.webservice.domain.CreateEnsembleRequest;
import com.florian.bayesianensemble.webservice.domain.EnsembleResponse;
import com.florian.bayesianensemble.webservice.domain.internal.ClassifyRequest;
import com.florian.bayesianensemble.webservice.domain.internal.InternalNetwork;
import com.florian.bayesianensemble.webservice.domain.internal.Probability;
import com.florian.bayesianensemble.webservice.domain.internal.ValidateRequest;
import com.florian.nscalarproduct.webservice.ServerEndpoint;
import com.florian.vertibayes.bayes.Network;
import com.florian.vertibayes.bayes.Node;
import com.florian.vertibayes.webservice.VertiBayesCentralServer;
import com.florian.vertibayes.webservice.VertiBayesEndpoint;
import com.florian.vertibayes.webservice.domain.CreateNetworkRequest;
import com.florian.vertibayes.webservice.domain.external.ExpectationMaximizationOpenMarkovResponse;
import com.florian.vertibayes.webservice.domain.external.WebBayesNetwork;
import com.florian.vertibayes.webservice.domain.external.WebNode;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.florian.bayesianensemble.util.Util.findNode;
import static com.florian.vertibayes.webservice.mapping.WebNodeMapper.mapWebNodeFromNode;

@RestController
public class EnsembleCentralServer extends VertiBayesCentralServer {
    @PostMapping ("createEnsemble")
    public EnsembleResponse createEnsemble(@RequestBody CreateEnsembleRequest req) throws Exception {
        initEndpoints();
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

            Map<String, String> bayesNets = performEnsembleOpenMarkov(req);
            initValidationFold(folds, i);
            Map<String, Double> foldAucs = validateEnsembleOpenMarkov(req, bayesNets);
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
        Map<String, String> bayesNets = performEnsembleOpenMarkov(req);

        //create response
        EnsembleResponse response = createEnsembleResponse(aucs, bayesNets);
        return response;
    }

    private EnsembleResponse noFoldEnsemble(CreateEnsembleRequest req, int[] folds) throws Exception {
        Map<String, String> bayesNets = performEnsembleOpenMarkov(req);
        activateAll(folds);

        Map<String, Double> aucs = validateEnsembleOpenMarkov(req, bayesNets);

        //create response
        EnsembleResponse response = createEnsembleResponse(aucs, bayesNets);
        return response;
    }

    private Map<String, String> performEnsembleOpenMarkov(CreateEnsembleRequest req) throws Exception {
        Node target = getTargetNode(req.getTarget());
        Map<String, String> bayesNets = new HashMap<>();

        for (ServerEndpoint e : getEndpoints()) {
            List<Node> network = getLocalNodes((EnsembleEndpoint) e, target);
            setBins(network, req);
            setUseLocalData(req.isHybrid(), (EnsembleEndpoint) e);
            network = learnStructure(network, req.getMinPercentage());
            WebBayesNetwork n = new WebBayesNetwork();
            n.setNodes(mapWebNodeFromNode(network));
            n.setOpenMarkovResponse(true);
            n.setTarget(target.getName());
            ExpectationMaximizationOpenMarkovResponse res =
                    (ExpectationMaximizationOpenMarkovResponse) expectationMaximization(
                            n);
            bayesNets.put(e.getServerId(), res.getOpenMarkov());
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

        List<InternalNetwork> targetLists = getEndpoints().parallelStream()
                .map(x -> ((EnsembleEndpoint) x).getNodes(targetReq))
                .collect(Collectors.toList());
        for (InternalNetwork l : targetLists) {
            if (l.getNodes().size() > 0) {
                return l.getNodes().get(0);
            }
        }
        return null;
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

    private EnsembleResponse createEnsembleResponse(Map<String, Double> aucs, Map<String, String> bayesNets)
            throws Exception {
        EnsembleResponse response = new EnsembleResponse();
        response.setAucs(aucs);
        response.setNetworks(bayesNets.values().stream().collect(Collectors.toList()));
        return response;
    }

    private Map<String, Double> validateEnsembleOpenMarkov(CreateEnsembleRequest req, Map<String, String> bayesNets)
            throws Exception {
        ClassifyRequest classify = new ClassifyRequest();
        classify.setTarget(req.getTarget());
        classify.setNetworks(bayesNets);
        Map<String, Integer> count = new HashedMap();
        Map<String, Double> aucs = new HashedMap();

        for (ServerEndpoint e : getEndpoints()) {
            List<double[]> probabilities = new ArrayList<>();
            sumProbabilities(classify, probabilities);
            ValidateRequest validate = new ValidateRequest();
            validate.setTarget(req.getTarget());
            validate.setNetworks(bayesNets);
            validate.setProbabilities(probabilities);
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

    private void sumProbabilities(ClassifyRequest req, List<double[]> probabilities) throws Exception {
        List<List<Probability>> res = new ArrayList<>();
        for (ServerEndpoint e : getEndpoints()) {
            if (e instanceof EnsembleEndpoint) {
                res.add(((EnsembleEndpoint) e).classify(req).getProbabilities());
            }
        }
        Probability[] summed = new Probability[res.get(0).size()];
        double[] count = new double[res.get(0).size()];
        for (List<Probability> p : res) {
            for (int i = 0; i < p.size(); i++) {
                if (summed[i] == null) {
                    summed[i] = new Probability();
                }
                if (p.get(i).isActive()) {
                    summed[i].setActive(true);
                }
                if (p.get(i).isLocallyPresent()) {
                    summed[i].setLocallyPresent(true);
                    if (summed[i].getProbability() == null) {
                        summed[i].setProbability(p.get(i).getProbability());
                    } else {
                        for (int j = 0; j < p.get(i).getProbability().length; j++) {
                            summed[i].getProbability()[j] = p.get(i)
                                    .getProbability()[j] + summed[i].getProbability()[j];
                        }
                    }
                    count[i]++;
                }
            }
        }
        for (int i = 0; i < summed.length; i++) {
            probabilities.add(summed[i].getProbability());
            if (summed[i].isActive()) {
                for (int j = 0; j < summed[i].getProbability().length; j++) {
                    summed[i].getProbability()[j] = summed[i].getProbability()[j] / count[i];
                }
            }
        }
    }

    private void initEndpoints() {
        if (getEndpoints().size() == 0) {
            setEndpoints(new ArrayList<>());
            for (String s : servers) {
                getEndpoints().add(new EnsembleEndpoint(s));
            }
        }
        if (getSecretEndpoint() == null) {
            setSecretEndpoint(new ServerEndpoint(secretServer));
        }
        getEndpoints().stream().forEach(x -> x.initEndpoints());
        getSecretEndpoint().initEndpoints();
        getEndpoints().stream().forEach(x -> ((VertiBayesEndpoint) x).initMaximumLikelyhoodData(new ArrayList<>()));
    }

}

