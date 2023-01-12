package com.florian.bayesianensemble.webservice.domain.internal;

import com.florian.vertibayes.webservice.domain.external.WebNode;

import java.util.ArrayList;
import java.util.List;

public class CheckFullyLocalRequest {
    private List<WebNode> nodes = new ArrayList<>();

    public List<WebNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<WebNode> nodes) {
        this.nodes = nodes;
    }
}
