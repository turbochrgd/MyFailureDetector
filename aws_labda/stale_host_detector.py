from __future__ import print_function

import os
from datetime import datetime
from urllib2 import urlopen
import boto3
import json
import decimal
from boto3.dynamodb.conditions import Key, Attr

TOLERANCE = int(os.environ['tolerance'])
REGION = os.environ['region']

def lambda_handler(event, context):
    print('Running lambda')
    dynamodb = boto3.resource('dynamodb', region_name=REGION)

    table = dynamodb.Table('host_states')
    currentTime = int(datetime.now().strftime("%s"))

    lookbackTime = 1000 * (currentTime - TOLERANCE)
    print("Looking for hosts with timestamp less than " + str(lookbackTime))
    response = table.scan(
        FilterExpression=Key('timestamp').lt(lookbackTime)
    )

    stale_hosts = []
    for x in response["Items"]:
        stale_hosts.append(x)
        table.update_item(
            Key={
                'timestamp': x['timestamp'],
                'hostUUID': x['hostUUID']
            },
            UpdateExpression='SET hostState = :val1',
            ExpressionAttributeValues={
                ':val1': 'FAILED'
            }
        )
    return stale_hosts
