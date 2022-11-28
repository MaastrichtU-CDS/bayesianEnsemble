import vantage6.client

IMAGE = 'harbor.carrier-mu.src.surf-hosted.nl/carrier/bayesian_ensemble'
NAME = 'bayesianensemble from client'


class BayesianEnsembleClient:

    def __init__(self, client: vantage6.client.Client):
        """

        :param client: Vantage6 client
        """
        self.client = client

    def bayesianEnsemble(self, collaboration, commodity_node, nodes,  target, networks, binned, minpercentage, hybrid, folds):
        return self.client.task.create(collaboration=collaboration,
                                       organizations=[commodity_node],
                                       name=NAME, image=IMAGE, description=NAME,
                                       input={'method': 'bayesianEnsemble', 'master': True,
                                              'args': [nodes, target, networks, binned, minpercentage, hybrid, folds]})
