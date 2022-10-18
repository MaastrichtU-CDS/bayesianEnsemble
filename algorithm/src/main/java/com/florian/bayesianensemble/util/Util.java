package com.florian.bayesianensemble.util;

import com.florian.vertibayes.bayes.Node;
import com.florian.vertibayes.webservice.domain.external.WebNode;

import java.util.List;

public class Util {

    public static Node findNode(String name, List<Node> nodes) {
        for (Node n : nodes) {
            if (n.getName().equals(name)) {
                return n;
            }
        }
        return null;
    }

    public static WebNode findWebNode(String name, List<WebNode> nodes) {
        for (WebNode n : nodes) {
            if (n.getName().equals(name)) {
                return n;
            }
        }
        return null;
    }
}
