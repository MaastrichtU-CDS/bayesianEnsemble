package com.florian.bayesianensemble.openmarkov;

import org.openmarkov.core.exception.*;
import org.openmarkov.core.model.network.*;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.variableElimination.tasks.VEPropagation;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class OpenMarkovClassifier {

    private OpenMarkovClassifier() {
    }

    public static ProbNet loadModel(String model) {
        try {
            // Open the file containing the network
            InputStream input = new ByteArrayInputStream(model.getBytes());
            // Load the Bayesian network
            PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
            ProbNet network = pgmxReader.loadProbNet("Network", input);
            return network;

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<Variable, TablePotential> classify(Map<String, String> evidence, String target,
                                                             ProbNet network)
            throws NodeNotFoundException, IncompatibleEvidenceException, InvalidStateException,
                   NotEvaluableNetworkException, UnexpectedInferenceException {

        VEPropagation vePropagation;
        EvidenceCase postResolutionEvidence = new EvidenceCase();
        EvidenceCase preResolutionEvidence = null;

        List<Variable> variablesOfInterest = new ArrayList<>();

        variablesOfInterest.add(network.getVariable(target));


        for (String key : evidence.keySet()) {
            Variable v = network.getVariable(key);
            if (v.getVariableType() == VariableType.FINITE_STATES) {
                try {
                    int index = v.getStateIndex(v.getState(evidence.get(key)));
                    Finding f = new Finding(v, index);
                    postResolutionEvidence.addFinding(f);
                } catch (Exception e) {
                    System.out.println(e);
                }

            } else if (v.getVariableType() == VariableType.DISCRETIZED) {
                try {
                    Finding f = new Finding(v, Double.valueOf(evidence.get(key)));
                    postResolutionEvidence.addFinding(f);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }


        vePropagation = new VEPropagation(network);
        vePropagation.setVariablesOfInterest(variablesOfInterest);
        vePropagation.setPreResolutionEvidence(preResolutionEvidence);
        vePropagation.setPostResolutionEvidence(postResolutionEvidence);
        HashMap<Variable, TablePotential> posteriorVales = vePropagation.getPosteriorValues();

        return posteriorVales;
    }
}
