# MyFailureDetector
An example of a system to detect healthy host in a clique and get healthy host records with millisecond latencies.

Use case:

Suppose you have a bunch microservices which run in a VPC. Ofcourse, since this is 2018, you run in the cloud. For example: you use AWS. Your service run in a VPC and reach out to public internet to read/write to DynamoDB, S3 and other publicly available services.
However, to reach out to your internal micro-services (i.e. the ones running in your awesome VPC), there is no need to reach out to the public internet. So, how do you know which machine/host to reach out to for a certain service?


Using DNS is an option, but it is static and comes with added latencies for DNS queries. Also, you would have to maintain the updated list of hosts behind the DNS in case your hosts are replaced/terminated etc.
Since we would need a system to maintain the list of hosts behind DNS, do we even need the DNS? 

Well, the answer is NO. The following example describes, using DynamoDB, S3, AWS Lambda and SNS, a system to maintain a record of healthy hosts in your fleet, such that all your healthy hosts know about all the other healthy hosts in the fleet.  

In comes distributed failure detection and host management. In this system, we assume that AWS is highly available.

![alt text](https://raw.githubusercontent.com/turbochrgd/MyFailureDetector/master/system-design/MyFailureDetector.png)


<h2>Components:</h2>

1. MyFailureDetector : Runs on each of your machine in the VPC. Spawns and manages worker threads: HeartbeatDaemon and FetchAndStoreHealthyRecordsDaemon.
2. HeartbeatDaemon : Heartbeats into a DynamoDB table. If healthy i.e. running, it will create a new record in DynamoDB. Hash key is the update timestamp and range key is IP_ADDRESS. Will be run by a scheduler every HB (heartbeat) seconds.
3. FetchAndStoreHealthyRecordsDaemon : Reads the latest healthy records file from S3.  Store returned records on non-volatile local disk.
4. GetHealthyHost API (Not pictured) : Will read the FetchAndStoreHealthyRecords file to maintain a round robin (or health quotient based formula) to return the "best" healthy record to use. Used by clients or users.
5. StaleHostDetector Lambda : Lambda to FAIL all old records. Query DynamoDB clique with HASH_KEY < TIMESTAMP - TOLERANCE and FAIL then records. 
6. GenerateHealthyHostList Lambda : Lambda to generate the latest healthy hosts for your fleet. Reads the DynamoDB table to query records with HASH_KEY > now - HB - delta. Publishes the record list to S3.
7. CleanupFailedHost Lambda : Removes all FAILED hosts from the DynamoDB table. Keeps the table size small (equivalent to your fleet). Publish SNS notification for cleanup and audit.

<h2>Vocabulary used:</h2>

<strong>TIMESTAMP</strong> = the current timestamp in UTC

<strong>HB</strong> = the configurable heart beat or check-in interval in seconds

<strong>TOLERANCE</strong> = interval in seconds after within which the host should check-in with the clique ( > HB)

<strong>HEALTHY</strong> = Host state if the host was able to reach the clique

<strong>FAILED</strong> = Host state when a host has been removed from the clique


Further systems:
1. Tools/system to ingest the SNS notifications fro cleaned up hosts. Systems and monitoring to replace those hosts.
2. System to create a full loop of host health from the calling service i.e. when a host detects that one of the (other) hosts in the file is not well.  
3. The services running on the machine to let MyFailureDetector know that they are unhealthy.
