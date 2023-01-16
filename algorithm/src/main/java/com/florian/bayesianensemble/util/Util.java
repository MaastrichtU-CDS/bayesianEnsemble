package com.florian.bayesianensemble.util;

import com.florian.vertibayes.bayes.Node;

import java.util.List;

public final class Util {

    private Util() {
    }

    public static Node findNode(String name, List<Node> nodes) {
        for (Node n : nodes) {
            if (n.getName().equals(name)) {
                return n;
            }
        }
        return null;
    }
}
