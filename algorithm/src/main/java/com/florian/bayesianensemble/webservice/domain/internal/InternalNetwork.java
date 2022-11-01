package com.florian.bayesianensemble.webservice.domain.internal;

import com.florian.vertibayes.bayes.Node;

import java.util.ArrayList;
import java.util.List;

public class InternalNetwork {
    private List<Node> nodes = new ArrayList<>();

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
