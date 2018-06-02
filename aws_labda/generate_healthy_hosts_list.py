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
BUCKET_NAME = os.environ['healthy_records_bucket_name']
FILE_NAME = "healthy_hosts.txt"

def lambda_handler(event, context):
    dynamodb = boto3.resource('dynamodb', region_name=REGION)
    s3 = boto3.client('s3', region_name=REGION)

    table = dynamodb.Table('host_states')
    currentTime = int(datetime.now().strftime("%s"))

    print("Querying for hosts that checked in after " + str(currentTime - TOLERANCE) + " and are HEALTHY")

    response = table.scan(
        FilterExpression=Key('timestamp').gt(currentTime - TOLERANCE) & Attr('hostState').eq('HEALTHY')
    )

    healthy_hosts = []
    for x in response["Items"]:
        healthy_hosts.append( x["serviceName"] + ":" + x["hostUUID"])

    healthy_hosts = list(set(healthy_hosts))
    print(healthy_hosts)

    os.chdir('/tmp')
    with open(FILE_NAME, 'w') as fileHandle:
        fileHandle.write(", ".join(hostUUID for hostUUID in healthy_hosts))

    with open(FILE_NAME, 'r') as fileHandle:
        s3.upload_fileobj(fileHandle, BUCKET_NAME, "healthy_hosts")

    os.remove("/tmp/" + FILE_NAME)

    return healthy_hosts
