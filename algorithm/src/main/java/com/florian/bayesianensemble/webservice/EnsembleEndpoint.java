package com.florian.bayesianensemble.webservice;

import com.florian.bayesianensemble.webservice.domain.CollectNodesRequest;
import com.florian.bayesianensemble.webservice.domain.internal.InternalNetwork;
import com.florian.nscalarproduct.webservice.Server;
import com.florian.vertibayes.webservice.VertiBayesEndpoint;

public class EnsembleEndpoint extends VertiBayesEndpoint {

    public EnsembleEndpoint(Server server) {
        super(server);
    }

    public EnsembleEndpoint(String url) {
        super(url);
    }

    public InternalNetwork getNodes(CollectNodesRequest request) {
        if (testing) {
            return ((EnsembleServer) (server)).getNodes(request);
        } else {
            return REST_TEMPLATE.postForEntity(serverUrl + "/getNodes", request, InternalNetwork.class)
                    .getBody();
        }
    }

    public InternalNetwork getAllNodes() {
        if (testing) {
            return ((EnsembleServer) (server)).getAllNodes();
        } else {
            return REST_TEMPLATE.getForEntity(serverUrl + "/getAllNodes", InternalNetwork.class)
                    .getBody();
        }
    }
}
