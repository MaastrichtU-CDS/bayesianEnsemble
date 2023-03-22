import time
from typing import List

import requests
from vantage6.common import info

from com.florian.bayesianensemble import urlcollector
from com.florian.bayesianensemble.wrapper import secondary

WAIT = 10
RETRY = 20
IMAGE = 'harbor.carrier-mu.src.surf-hosted.nl/carrier/bayesian_ensemble'


def bayesianEnsemble(client, data, nodes, target, networks, binned, minpercentage, hybrid, folds, trainStructure, *args, **kwargs):
        """
    
        :param client:
        :param exclude_orgs:
        :param nodes, organizations who own the data
        :param commoditynode: organization id of commodity node
        :return: bayesian network in the form of a bif-file, such as pgmpy expects.
        """
        tasks = []
        info('Initializing nodes')
        info('Dit is de nieuwste image')
        i = 0
        print(nodes)
        names = []
        for node in nodes:
            info('Node ' + str(i))
            tasks.append(_initEndpoints(client, [node]))
            names.append(node)
            i = i+1

        # TODO: init commodity server on a different server?
        info('initializing commodity server')
        commodity_node_task = secondary.init_local()

        adresses = []

        for task in tasks:
            adresses.append(_await_addresses(client, task["id"])[0])


        #assuming the last taks before tasks[0] controls the commodity server
        #Assumption is basically that noone got in between the starting of this master-task and its subtasks
        #ToDo make this more stable in case of multiple users
        global_commodity_address = _await_addresses(client, tasks[0]["id"]-1)[0]

        # Assuming commodity server is on same machine
        commodity_address = _http_url('localhost', 8888)
        info(f'Commodity address: {commodity_address}')

        # wait a moment for Spring to start
        info('Waiting for spring to start...')
        _wait()

        info('Sharing addresses & setting ids')
        _setId(commodity_address, "0");
        id = 0
        for adress in adresses:
            info('adress')
            info(adress)
            _setId(adress, str(nodes[id]));
            id+=1
            others = adresses.copy()
            others.remove(adress)
            others.append(global_commodity_address)
            urlcollector.put_endpoints(adress, others)

        info('were here now')
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
    info(id)
    info(ip)
    r = requests.post(ip + "/setID?id="+id)

def _initEndpoints(client, organizations):
    # start the various java endpoints for n2n
    return client.create_new_task(
        input_={'method': 'init'},
        organization_ids=organizations
    )


def _wait():
    time.sleep(WAIT)

def _await_addresses(client, task_id, n_nodes=1):
    addresses = client.get_algorithm_addresses(task_id=task_id)

    c = 0
    while not _addresses_complete(addresses):
        if c >= RETRY:
            raise Exception('Retried too many times')

        info(f'Polling results for port numbers attempt {c}...')
        addresses = client.get_algorithm_addresses(task_id=task_id)
        c += 1
        time.sleep(WAIT)
    info("Found adresses")
    return [_http_url(address['ip'], address['port']) for address in addresses]


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
