package com.florian.bayesianensemble.webservice;

import com.florian.bayesianensemble.webservice.domain.CollectNodesRequest;
import com.florian.bayesianensemble.webservice.domain.CreateEnsembleRequest;
import com.florian.bayesianensemble.webservice.domain.EnsembleResponse;
import com.florian.bayesianensemble.webservice.domain.internal.*;
import com.florian.nscalarproduct.encryption.Paillier;
import com.florian.nscalarproduct.encryption.PublicPaillierKey;
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
import java.util.stream.IntStream;

import static com.florian.bayesianensemble.util.Util.findNode;
import static com.florian.vertibayes.webservice.mapping.WebNodeMapper.mapWebNodeFromNode;
import static com.florian.vertibayes.webservice.mapping.WebNodeMapper.mapWebNodeToNode;

@RestController
public class EnsembleCentralServer extends VertiBayesCentralServer {
    private static final int PRECISION = 5;
    private static final int TEN = 10;

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

        List<double[]> probablites = new ArrayList<>();
        for (int i = 0; i < req.getFolds(); i++) {
            initFold(folds, i);

            Map<String, String> bayesNets = performEnsembleOpenMarkov(req);
            initValidationFold(folds, i);

            classify(probablites, req, bayesNets);
        }

        //Train final model
        activateAll(folds);
        Map<String, String> bayesNets = performEnsembleOpenMarkov(req);

        Map<String, Double> aucs = calculateAUC(probablites, req.getTarget(), bayesNets);

        //create response
        EnsembleResponse response = createEnsembleResponse(aucs, bayesNets, req.getTarget());
        return response;
    }

    private EnsembleResponse noFoldEnsemble(CreateEnsembleRequest req, int[] folds) throws Exception {
        Map<String, String> bayesNets = performEnsembleOpenMarkov(req);
        activateAll(folds);
        List<double[]> probablites = new ArrayList<>();
        classify(probablites, req, bayesNets);
        Map<String, Double> aucs = calculateAUC(probablites, req.getTarget(), bayesNets);

        //create response
        EnsembleResponse response = createEnsembleResponse(aucs, bayesNets, req.getTarget());
        return response;
    }

    private Map<String, String> performEnsembleOpenMarkov(CreateEnsembleRequest req) throws Exception {
        Node target = getTargetNode(req.getTarget());
        Map<String, String> bayesNets = new HashMap<>();

        Map<String, List<Node>> structures = generateStructures(req);

        structures.keySet().parallelStream().forEach(x -> {
            try {
                bayesNets.put(x, trainStructure(x, structures, target, req.isHybrid()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return bayesNets;
    }

    private String trainStructure(String key, Map<String, List<Node>> structures, Node target, boolean isHybrid)
            throws Exception {
        for (ServerEndpoint e : getEndpoints()) {
            if (e.getServerId().equals(key)) {
                setUseLocalData(isHybrid, (EnsembleEndpoint) e);
            }
        }
        WebBayesNetwork n = new WebBayesNetwork();
        n.setNodes(mapWebNodeFromNode(structures.get(key)));
        n.setOpenMarkovResponse(true);
        n.setTarget(target.getName());
        ExpectationMaximizationOpenMarkovResponse res =
                (ExpectationMaximizationOpenMarkovResponse) expectationMaximization(
                        n);
        return res.getOpenMarkov();
    }

    private Map<String, List<Node>> generateStructures(CreateEnsembleRequest req) {
        Node target = getTargetNode(req.getTarget());
        Map<String, List<Node>> networks = new HashMap<>();

        getEndpoints().stream().forEach(x -> networks.put(x.getServerId(), generateStructure(req, target, x)));

        return networks;
    }

    private List<Node> generateStructure(CreateEnsembleRequest req, Node target, ServerEndpoint e) {
        if (req.getNetworks() != null && req.getNetworks().containsKey(e.getServerId())) {
            List<Node> network = mapWebNodeToNode(req.getNetworks().get(e.getServerId()));
            return network;
        } else {
            List<Node> network = getLocalNodes((EnsembleEndpoint) e, target);
            setBins(network, req);
            setUseLocalData(req.isHybrid(), (EnsembleEndpoint) e);
            network = learnStructure(network, req.getMinPercentage());
            return network;
        }
    }

    private void setBins(List<Node> network, CreateEnsembleRequest req) {
        if (req.getBinned() != null) {
            for (WebNode n : req.getBinned()) {
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

    private EnsembleResponse createEnsembleResponse(Map<String, Double> aucs, Map<String, String> bayesNets,
                                                    String target)
            throws Exception {
        //create weighted AUC
        double weightedAUC = 0;

        Paillier p = new Paillier();
        p.generateKeyPair();
        for (String key : aucs.keySet()) {
            WeightedAUCReq r = new WeightedAUCReq();
            r.setAttributeName(target);
            r.setAttributeValue(key);
            r.setAuc(aucs.get(key));
            r.setKey(p.getPublicKey());
            r.setPrecision(PRECISION);
            for (ServerEndpoint e : getEndpoints()) {
                weightedAUC += p.decrypt(((EnsembleEndpoint) e).getWeightedAUC(r)).doubleValue() / Math.pow(TEN,
                                                                                                            PRECISION);
            }
        }
        weightedAUC /= getEndpoints().get(0).getPopulation();

        EnsembleResponse response = new EnsembleResponse();
        response.setAucs(aucs);
        response.setNetworks(bayesNets.values().parallelStream().collect(Collectors.toList()));
        response.setWeightedAUC(weightedAUC);
        return response;
    }

    public Map<String, Double> calculateAUC(List<double[]> probabilities, String target, Map<String, String> bayesNets)
            throws Exception {
        Map<String, Double> aucs = new HashedMap();
        Map<String, Integer> count = new HashedMap();
        for (ServerEndpoint e : getEndpoints()) {
            ValidateRequest validate = new ValidateRequest();
            validate.setTarget(target);
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

    private List<double[]> classify(List<double[]> probabilities, CreateEnsembleRequest req,
                                    Map<String, String> bayesNets)
            throws Exception {
        ClassifyRequest classify = new ClassifyRequest();
        String encryptionName = String.valueOf(System.currentTimeMillis());
        classify.setTarget(req.getTarget());
        classify.setNetworks(bayesNets);
        classify.setKey(getPublicPaillierKey(encryptionName));
        classify.setPrecision(PRECISION);

        sumProbabilities(classify, probabilities, encryptionName);

        return probabilities;
    }

    private PublicPaillierKey getPublicPaillierKey(String encryptionName) {
        EnsembleEndpoint encryptionEndpoint = (EnsembleEndpoint) getEndpoints().get(getEndpoints().size() - 1);
        PublicPaillierKey key = encryptionEndpoint.generatePaillierKey(encryptionName);
        return key;
    }

    private void sumProbabilities(ClassifyRequest req, List<double[]> probabilities, String encryptionName)
            throws Exception {
        List<List<Probability>> res = new ArrayList<>();
        for (ServerEndpoint e : getEndpoints()) {
            res.add(((EnsembleEndpoint) e).classify(req).getProbabilities());
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
                        for (int j = 0; j < p.get(i).getEncryptedProbability().length; j++) {
                            summed[i].getProbability()[j] = p.get(i)
                                    .getProbability()[j] + summed[i].getProbability()[j];
                        }
                    }

                    if (summed[i].getEncryptedProbability() == null) {
                        summed[i].setEncryptedProbability(p.get(i).getEncryptedProbability());
                    } else {
                        for (int j = 0; j < p.get(i).getEncryptedProbability().length; j++) {
                            summed[i].getEncryptedProbability()[j] = p.get(i)
                                    .getEncryptedProbability()[j].multiply(summed[i].getEncryptedProbability()[j]);
                        }
                    }
                    count[i]++;
                }
            }
        }

        IntStream.range(0, summed.length).parallel().forEach(i -> decrypt(summed[i], encryptionName, count[i]));
        for (int i = 0; i < summed.length; i++) {
            if (i >= probabilities.size()) {
                //put in a dummy
                probabilities.add(new double[0]);
            }
            if (summed[i].isActive()) {
                probabilities.set(i, summed[i].getProbability());
            }
        }
    }

    private void decrypt(Probability p, String encryptionName, double count) {
        if (p.isActive()) {
            DecryptionRequest decrypt = new DecryptionRequest();
            double[] d = new double[p.getEncryptedProbability().length];
            for (int j = 0; j < d.length; j++) {
                decrypt.setValue(p.getEncryptedProbability()[j]);
                decrypt.setName(encryptionName);
                d[j] = ((EnsembleEndpoint) getEndpoints().get(getEndpoints().size() - 1)).decrypt(decrypt)
                        .doubleValue() / Math.pow(TEN, PRECISION);
            }

            for (int j = 0; j < p.getEncryptedProbability().length; j++) {
                p.getProbability()[j] = d[j] / count;
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

