from __future__ import print_function

import os
from datetime import datetime
from urllib2 import urlopen
import boto3
import json
import decimal
from boto3.dynamodb.conditions import Key, Attr

TOLERANCE = int(os.environ['tolerance'])

def lambda_handler(event, context):
    print('Running lambda at {}...'.format(event['time']))
    dynamodb = boto3.resource('dynamodb', region_name='us-east-1')

    table = dynamodb.Table('host_states')
    currentTime = int(datetime.now().strftime("%s"))

    response = table.scan(
        FilterExpression=Key('timestamp').lt(currentTime - TOLERANCE)
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
