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
TOPIC_ARN = os.environ['topic_arn']
DELETE_ENABLED = True

def lambda_handler(event, context):
    print('Running lambda at')
    dynamodb = boto3.resource('dynamodb', region_name=REGION)
    sns = boto3.client('sns', region_name=REGION)

    table = dynamodb.Table('host_states')
    currentTime = int(datetime.now().strftime("%s"))

    response = table.scan(
        FilterExpression=Key('timestamp').lt(currentTime - TOLERANCE) & Attr('hostState').eq('FAILED')
    )

    cleanedup_hosts = []
    for x in response["Items"]:
        cleanedup_hosts.append(x)
        if (DELETE_ENABLED):
            table.delete_item(
                Key={
                    'timestamp': x['timestamp'],
                    'hostUUID': x['hostUUID']
                }
            )
        response = sns.publish(
            TopicArn=TOPIC_ARN,
            Message=str(x),
            Subject='FailedHost ' + x['hostUUID'],
            MessageStructure='string'
        )
    return cleanedup_hosts
