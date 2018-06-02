from __future__ import print_function

import os
from datetime import datetime
from urllib2 import urlopen
import boto3
import json
import decimal
from boto3.dynamodb.conditions import Key, Attr

# 'timestamp': 1234567890,
# 'hostUUID': localhost
# 'hostState': {FAILED, HEALTHY}

REGION = os.environ['region']

def lambda_handler(event, context):
    print('Running lambda at')
    dynamodb = boto3.resource('dynamodb', region_name=REGION)

    table = dynamodb.Table('host_states')
    currentTime = int(datetime.now().strftime("%s"))
    hostName = "host-" + str(currentTime)
    response = table.put_item(
       Item={
            'timestamp': currentTime,
            'hostUUID': hostName,
            'hostState': 'HEALTHY'
        }
    )

    return hostName
