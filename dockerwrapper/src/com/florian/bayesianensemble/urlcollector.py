import requests
from typing import Any, Dict, Tuple

from requests.adapters import HTTPAdapter
from urllib3 import Retry

RETRY = 10

def _get_address_from_result(result: Dict[str, Any]) -> Tuple[str, int]:
    address = result['ip']
    port = result['port']

    return address, port


def _set_endpoints(target, endpoints):
    target_ip, target_port = _get_address_from_result(target)
    others = []
    targetUrl = "http://" + target_ip + ":" + target_port
    for e in endpoints:
        other_ip, other_port = _get_address_from_result(e)
        others.append("http://" + other_ip + ":" + other_port)

    return put_endpoints(others, targetUrl)


def put_endpoints(targetUrl, others):
    payload = {"servers": others}
    r = requests.post(targetUrl + "/setEndpoints", json=payload, timeout=10)



def _kill_endpoint(target):
    target_ip, target_port = _get_address_from_result(target)
    targetUrl = "http://" + target_ip + ":" + target_port
    r = requests.put(targetUrl + "/kill")


def _get_connection_session():
    """
    Create connection adapter with sensible retry strategy
    :return:
    """
    retry_strategy = Retry(
        total=RETRY
    )
    adapter = HTTPAdapter(max_retries=retry_strategy)
    http = requests.Session()
    http.mount("https://", adapter)
    http.mount("http://", adapter)

    return http
