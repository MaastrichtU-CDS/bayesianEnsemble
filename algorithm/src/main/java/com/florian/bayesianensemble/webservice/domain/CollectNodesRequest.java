package com.florian.bayesianensemble.webservice.domain;

import java.util.ArrayList;
import java.util.List;

public class CollectNodesRequest {
    List<String> names = new ArrayList<>();

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }
}
