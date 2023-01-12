package com.florian.bayesianensemble.webservice;

import com.florian.bayesianensemble.webservice.domain.CollectNodesRequest;
import com.florian.bayesianensemble.webservice.domain.internal.*;
import com.florian.nscalarproduct.encryption.PublicPaillierKey;
import com.florian.nscalarproduct.webservice.Server;
import com.florian.vertibayes.bayes.Node;
import com.florian.vertibayes.webservice.VertiBayesEndpoint;
import com.florian.vertibayes.webservice.mapping.WebNodeMapper;

import java.math.BigInteger;
import java.util.List;

public class EnsembleEndpoint extends VertiBayesEndpoint {

    public EnsembleEndpoint(Server server) {
        super(server);
    }

    public EnsembleEndpoint(String url) {
        super(url);
    }

    public PublicPaillierKey generatePaillierKey(String name) {
        if (testing) {
            return ((EnsembleServer) (server)).generatePaillierKey(name);
        } else {
            return REST_TEMPLATE.getForEntity(serverUrl + "/generatePaillierKey?name=" + name, PublicPaillierKey.class)
                    .getBody();
        }
    }

    public BigInteger decrypt(DecryptionRequest req) {
        if (testing) {
            return ((EnsembleServer) (server)).decrypt(req);
        } else {
            return REST_TEMPLATE.postForEntity(serverUrl + "/decrypt", req, BigInteger.class)
                    .getBody();
        }
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

    public BigInteger getWeightedAUC(WeightedAUCReq req) throws Exception {
        if (testing) {
            return ((EnsembleServer) (server)).getWeightedAUC(req);
        } else {
            return REST_TEMPLATE.postForEntity(serverUrl + "/getWeightedAUC", req, BigInteger.class)
                    .getBody();
        }
    }

    public ValidateResponse validate(ValidateRequest req) throws Exception {
        if (testing) {
            return ((EnsembleServer) (server)).validate(req);
        } else {
            return REST_TEMPLATE.postForEntity(serverUrl + "/validate", req, ValidateResponse.class)
                    .getBody();
        }
    }

    public ClassificationResponse classify(ClassifyRequest req) throws Exception {
        if (testing) {
            return ((EnsembleServer) (server)).classify(req);
        } else {
            return REST_TEMPLATE.postForEntity(serverUrl + "/classify", req, ClassificationResponse.class)
                    .getBody();
        }
    }

    public boolean isFullyLocal(List<Node> structure) throws Exception {
        CheckFullyLocalRequest req = new CheckFullyLocalRequest();
        req.setNodes(WebNodeMapper.mapWebNodeFromNode(structure));

        if (testing) {
            return ((EnsembleServer) (server)).isFullyLocal(req);
        } else {
            return REST_TEMPLATE.postForEntity(serverUrl + "/isFullyLocal", req, boolean.class)
                    .getBody();
        }
    }
}
