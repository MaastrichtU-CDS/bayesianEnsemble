import time
from typing import List

import requests
from vantage6.common import info
from vantage6.algorithm.tools.decorators import algorithm_client

from com.florian.bayesianensemble import urlcollector
from com.florian.bayesianensemble.wrapper import secondary

WAIT = 10
RETRY = 20
IMAGE = 'harbor.carrier-mu.src.surf-hosted.nl/carrier/bayesian_ensemble'

SLEEP = 10
WAIT_CONTAINER_STARTUP = 10
NODE_TIMEOUT = 360
MAX_RETRIES = NODE_TIMEOUT // SLEEP


def parse_addresses(adresses):
    parsed = []
    for adress in adresses:
        parsed.append(parse_adress(adress))

    return parsed

def parse_adress(adress):
    return f'http://{adress["ip"]}:{adress["port"]}'

@algorithm_client
def bayesianEnsemble(client, nodes, target, networks, binned, minpercentage, hybrid, folds, trainStructure, *args, **kwargs):
        """
    
        :param client:
        :param exclude_orgs:
        :param nodes, organizations who own the data
        :param commoditynode: organization id of commodity node
        :return: bayesian network in the form of a bif-file, such as pgmpy expects.
        """
        info('Initializing nodes')
        for node in nodes:
            _initEndpoints(client, [node])

        # TODO: init commodity server on a different server?
        info('initializing commodity server')
        commodity_node_task = secondary.init_local()

        adresses = _get_algorithm_addresses(client, len(nodes))

        # assuming the last taks before tasks[0] controls the commodity server
        # Assumption is basically that noone got in between the starting of this master-task and its subtasks
        # ToDo make this more stable in case of multiple users
        global_commodity_address = client.vpn.get_own_address()

        # Assuming commodity server is on same machine
        commodity_address = _http_url('localhost', 8888)
        info(f'Commodity address: {commodity_address}')

        # wait a moment for Spring to start
        info('Waiting for spring to start...')
        _wait()

        info('Sharing addresses & setting ids')
        _setId(commodity_address, "0");

        adresses = parse_addresses(adresses)

        id = 1

        for adress in adresses:
            _setId(adress, str(id));
            id += 1
            others = adresses.copy()
            others.remove(adress)
            # for some reason global_commodity_address is a list of length 1, so pick first one
            others.append(parse_adress(global_commodity_address[0]))
            urlcollector.put_endpoints(adress, others)

        _initCentralServer(commodity_address, adresses)

        response = _trainEnsemble(commodity_address, target, networks, binned, minpercentage, hybrid, folds, trainStructure)

        info('Commiting murder')
        for adress in adresses:
            _killSpring(adress)

        return response

def _trainEnsemble(targetUrl, target, networks, binned, minpercentage, hybrid, folds, trainStructure):
    r = requests.post(targetUrl + "/createEnsemble", json={
        "target": target,
        "networks": networks,
        "binned": binned,
        "hybrid":hybrid,
        "minPercentage":minpercentage,
        "folds":folds,
        "trainStructure":trainStructure
    })
    return r.json()


def _initCentralServer(central: str, others: List[str]):
    r = requests.post(central + "/initCentralServer", json={
        "secretServer": central,
        "servers": others
    })

    if not r.ok:
        raise Exception("Could not initialize central server")

def _killSpring(server: str):
    try:
        r = requests.put(server + "/kill")
    except Exception as e:
        # We expect an error here
        info(e)
        pass

def _setId(ip: str, id:str):
    r = requests.post(ip + "/setID",params={"id":id})


def _initEndpoints(client, organizations):
    # start the various java endpoints for n2n
    return client.task.create(
        name="vertiBayesSpring",
        input_={'method': 'init'},
        organizations=organizations
    )


def _wait():
    time.sleep(WAIT)

def _get_algorithm_addresses(client, expected_amount: int):
    retries = 0

    # Wait for nodes to get ready
    while True:
        addresses = client.vpn.get_child_addresses()

        info(f"Addresses: {addresses}")

        if len(addresses) >= expected_amount:
            break

        if retries >= MAX_RETRIES:
            raise Exception(
                f"Could not connect to all {expected_amount} datanodes. There are "
                f"only {len(addresses)} nodes available"
            )
        time.sleep(SLEEP)
        retries += 1

    return addresses


def _addresses_complete(addresses):
    info("waiting for adresses to be complete")
    if len(addresses) == 0:
        return False
    for a in addresses:
        if not a['port']:
            return False
    return True


def _http_url(address: str, port: int):
    return f'http://{address}:{port}'
