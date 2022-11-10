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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.florian.bayesianensemble.openmarkov.OpenMarkovClassifier.loadModel;
import static com.florian.bayesianensemble.util.Util.createArrf;

@RestController
public class EnsembleServer extends BayesServer {
    private static final String ARFF = "individuals.arff";

    public EnsembleServer() {
    }

    public EnsembleServer(String id, List<ServerEndpoint> endpoints) {
        this.serverId = id;
        this.setEndpoints(endpoints);
    }

    public EnsembleServer(String path, String id) {
        super(path, id);
    }

    @PostMapping ("getNodes")
    public InternalNetwork getNodes(@RequestBody CollectNodesRequest req) {
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
    public ValidateResponse validate(@RequestBody ValidateRequest req) throws Exception {
        if (isLocallyPresent(req.getTarget())) {
            ValidateResponse res = new ValidateResponse();
            res.setAucs(calculateAUCOpenMarkov(req.getProbabilities(), req.getTarget(),
                                               req.getNetworks().get(this.serverId)));
            return res;
        } else {
            return new ValidateResponse();
        }
    }

    @PostMapping ("classify")
    public ClassificationResponse classify(@RequestBody ClassifyRequest req) throws Exception {
        Instances data = makeInstances(req.getTarget());
        List<Probability> probabilities = new ArrayList<>();
        ProbNet network = null;
        if (req.getNetworks() != null) {
            network = loadModel(req.getNetworks().get(this.serverId));
        }
        for (int i = 0; i < data.numInstances(); i++) {
            Probability p = new Probability();
            probabilities.add(p);
            if (recordIsActive(i)) {
                p.setActive(true);
            }
            if (recordIsLocallyPresent(i) && recordIsActive(i)) {
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

    private double calculateAUC(List<double[]> probabilities, List<String> labels, int classLabel, String target) {

        Set uniqueProbs = new HashSet();
        for (int i = 0; i < probabilities.size(); i++) {
            if (probabilities.get(i) == null) {
                continue;
            } else {
                uniqueProbs.add(probabilities.get(i)[classLabel]);
            }
        }
        //make sure 0 and 1 are included.
        uniqueProbs.add(0.0);
        uniqueProbs.add(1.0);
        List<Double> sorted = (List<Double>) uniqueProbs.stream().sorted().collect(Collectors.toList());
        Collections.reverse(sorted);
        double auc = 0;
        double oldTpr = 0;
        double oldFpr = 0;

        for (Double treshold : sorted) {
            double tp = 0;
            double fp = 0;
            double tn = 0;
            double fn = 0;
            for (int i = 0; i < probabilities.size(); i++) {
                if (probabilities.get(i) == null) {
                    continue;
                } else {
                    String trueLabel = getData().getAttributeValues(target).get(i).getValue();
                    if (probabilities.get(i)[classLabel] >= treshold) {
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
            }
            double tpr = tp + fn == 0 ? 0 : tp / (tp + fn);
            double fpr = fp + tn == 0 ? 0 : fp / (fp + tn);
            auc += (fpr - oldFpr) * (tpr - oldTpr) / 2 + ((fpr - oldFpr) * oldTpr);
            oldFpr = fpr;
            oldTpr = tpr;
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
