package com.florian.bayesianensemble.webservice;

import com.florian.bayesianensemble.openmarkov.OpenMarkovClassifier;
import com.florian.bayesianensemble.webservice.domain.CollectNodesRequest;
import com.florian.bayesianensemble.webservice.domain.internal.*;
import com.florian.nscalarproduct.data.Attribute;
import com.florian.nscalarproduct.data.Data;
import com.florian.nscalarproduct.webservice.ServerEndpoint;
import com.florian.vertibayes.bayes.Node;
import com.florian.vertibayes.webservice.BayesServer;
import org.apache.commons.collections.map.HashedMap;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.florian.bayesianensemble.openmarkov.OpenMarkovClassifier.loadModel;
import static com.florian.bayesianensemble.util.Util.createArrf;

public class EnsembleServer extends BayesServer {
    private static final String ARFF = "individuals.arff";

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

    @PostMapping ("validate")
    public ValidateResponse validate(ValidateRequest req) throws Exception {
        if (isLocallyPresent(req.getTarget())) {
            List<double[]> probabilities = new ArrayList<>();

            sumProbabilities(req, probabilities);
            ValidateResponse res = new ValidateResponse();
            res.setAucs(calculateAUCOpenMarkov(probabilities, req.getTarget(),
                                               req.getNetworks().get(this.serverId)));
            return res;
        } else {
            return new ValidateResponse();
        }
    }

    private void sumProbabilities(ValidateRequest req, List<double[]> probabilities) throws Exception {
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
                if (p.get(i).isLocallyPresent()) {
                    if (summed[i] == null) {
                        summed[i] = new Probability();
                    }
                    summed[i].setProbability(p.get(i).getProbability());
                    count[i]++;
                }
            }
        }
        for (int i = 0; i < summed.length; i++) {
            probabilities.add(summed[i].getProbability());
            for (int j = 0; j < summed[i].getProbability().length; j++) {
                probabilities.get(i)[j] = probabilities.get(i)[j] / count[i];
            }
        }
    }

    @PostMapping ("classify")
    public ClassificationResponse classify(ValidateRequest req) throws Exception {
        Instances data = makeInstances(req.getTarget());
        List<Probability> probabilities = new ArrayList<>();
        ProbNet network = null;
        if (req.getNetworks() != null) {
            network = loadModel(req.getNetworks().get(this.serverId));
        }
        for (int i = 0; i < data.numInstances(); i++) {
            Probability p = new Probability();
            probabilities.add(p);
            if (recordIsLocallyPresent(i)) {
                p.setLocallyPresent(true);
                Map<String, String> evidence = createIndividual(getData(), i, req.getTarget());
                HashMap<Variable, TablePotential> posteriorValues = OpenMarkovClassifier.classify(evidence,
                                                                                                  req.getTarget(),
                                                                                                  network);
                for (Variable key : posteriorValues.keySet()) {
                    String name = key.getName();
                    TablePotential potential = posteriorValues.get(key);
                    for (int k = 0; k < potential.values.length; k++) {
                        if (name.equals(req.getTarget())) {
                            p.setProbability(potential.getValues());
                        }
                    }
                }
            }
        }
        ClassificationResponse res = new ClassificationResponse();
        res.setProbabilities(probabilities);
        return res;
    }

    private Map<String, String> createIndividual(Data data, int i, String target) {
        List<List<Attribute>> d = data.getData();
        Map<String, String> evidence = new HashMap<>();
        for (int j = 0; j < data.getData().size(); j++) {
            Attribute a = d.get(j).get(i);
            if (data.getIdColumn() == j || data.getLocalPresenceColumn() == j || a.isUnknown()
                    || a.getAttributeName().equals("locallyPresent") || a.getAttributeName().equals(target)) {
                continue;
            }
            evidence.put(a.getAttributeName(), a.getValue());
        }
        return evidence;
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

    private boolean isLocallyPresent(String target) {
        return getData().getAttributeCollumn(target) != null;
    }

    private Instances makeInstances(String target) throws IOException {
        createArrf(getData(), target, ARFF);
        Instances data = new Instances(new BufferedReader(new FileReader(ARFF)));
        for (int i = 0; i < data.numAttributes(); i++) {
            if (data.attribute(i).name().equals(target)) {
                data.setClassIndex(i);
                break;
            }
        }
        return data;
    }

    private Map<String, Double> calculateAUCOpenMarkov(List<double[]> probabilities, String target, String net) {
        List<String> labels = collectLabelsOpenMarkov(target, loadModel(net));
        Map<String, Double> aucs = new HashedMap();

        for (int i = 0; i < labels.size(); i++) {
            aucs.put(labels.get(i), calculateAUC(probabilities, labels, i, target));
        }
        return aucs;
    }

    private Map<String, Double> calculateAUC(List<double[]> probabilities, String target, BayesNet net) {
        double auc = 0;
        int node = 0;
        for (int i = 0; i < net.getNrOfNodes(); i++) {
            if (net.getNodeName(i).equals(target)) {
                node = i;
            }
        }
        List<String> labels = collectLabels(net, node);
        Map<String, Double> aucs = new HashedMap();

        for (int i = 0; i < labels.size(); i++) {
            aucs.put(labels.get(i), calculateAUC(probabilities, labels, i, target));
        }
        return aucs;
    }

    private double calculateAUC(List<double[]> probabilities, List<String> labels, int classLabel, String target) {

        Set uniqueProbs = new HashSet();
        for (int i = 0; i < probabilities.size(); i++) {
            uniqueProbs.add(probabilities.get(i)[classLabel]);
        }
        List<Double> sorted = (List<Double>) uniqueProbs.stream().sorted().collect(Collectors.toList());
        double auc = 0;
        double oldTp = 0;
        double oldFpr = 0;

        for (Double treshold : sorted) {
            double tp = 0;
            double fp = 0;
            double tn = 0;
            double fn = 0;
            for (int i = 0; i < probabilities.size(); i++) {
                String trueLabel = getData().getAttributeValues(target).get(i).getValue();
                if (probabilities.get(i)[classLabel] <= treshold) {
                    //probability is high enough to be assigned this class label
                    if (trueLabel.equals(labels.get(classLabel))) {
                        tp++;
                    } else {
                        fp++;
                    }
                } else {
                    //probability is too low to be assigned this class label
                    if (trueLabel.equals(labels.get(classLabel))) {
                        fn++;
                    } else {
                        tn++;
                    }
                }
            }
            double tpr = tp / (tp + fn);
            double fpr = fp / (fp + tn);
            auc += tpr * (fpr - oldFpr);
            oldFpr = fpr;
        }
        return auc;
    }

    private List<String> collectLabels(BayesNet net, int node) {
        List<String> labels = new ArrayList<>();
        boolean done = false;
        int i = 0;
        while (!done) {
            try {
                labels.add(net.getNodeValue(node, i));
                i++;
            } catch (IndexOutOfBoundsException e) {
                //ran out of bounds
                done = true;
            }
        }
        return labels;
    }

    private List<String> collectLabelsOpenMarkov(String target, ProbNet net) {
        Variable node = null;
        try {
            node = net.getVariable(target);
            return Arrays.asList(node.getStates()).stream().map(x -> x.getName()).collect(Collectors.toList());
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
