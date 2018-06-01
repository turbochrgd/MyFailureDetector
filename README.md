# MyFailureDetector
An example of a system to detect healthy host in a clique and get healthy host records with millisecond latencies

![alt text](https://raw.githubusercontent.com/turbochrgd/MyFailureDetector/master/system-design/MyFailureDetector.png)


<h2>Components:</h2>

1. MyFailureDetector : The application itself that heartbeats. Spawns and manages worker threads.
2. HeartbeatDaemon : Heartbeats into the DynamoDB clique (table). If healthy, will create a new record in DynamoDB. Hash key is IP_ADDRESS and range key is the update timestamp. Will be run by a scheduler every HB (heartbeat) seconds.
3. FetchAndStoreHealthyRecordsDaemon : Query DyanmoDB clique with RANGE_KEY >= TIMESTAMP - HB and STATE = HEALTHY. Store returned records on non-volatile local disk.
4. GetHealthyHost API (Not pictured) : Will read the FetchAndStoreHealthyRecords file to maintain a round robin (or health quotient based formula) to return the "best" healthy record to use. Used by clients or users. Will implement this in an upcoming repository.
5. LeaderDaemon : Keeps the DyanmoDB clique clean by "FAIL"ing all old records. Query DynamoDB clique with RANGE_KEY < TIMESTAMP - TOLERANCE and FAIL then records. Publish SNS notification for cleanup and audit.
6. CleanupAndAuditDaemon : Reads the "FAIL"ed SNS notifications via SQS and emits logs to CloudWatch

<h2>Vocabulary used:</h2>

<strong>Clique</strong> = A set of hosts which were known to be healthy recently

<strong>TIMESTAMP</strong> = the current timestamp in UTC

<strong>HB</strong> = the configurable heart beat or check-in interval in seconds

<strong>TOLERANCE</strong> = interval in seconds after within which the host should check-in with the clique ( > HB)

<strong>HEALTHY</strong> = Host state if the host was able to reach the clique

<strong>FALLEN</strong> = Host state which did not check in with the clique for a certain TOLERANCE interval

<strong>FAILED</strong> = Host state when a host has been removed from the clique


<h2>Component workings:</h2>

<strong>HeartbeatDaemon :</strong> Simply write itself in the DynamoDB table. Creates a new record each time with HASH_KEY = IP_ADDRESS and RANGE_KEY = TIMESTAMP. If the DyanmoDB table has a record that is quite old, we will know that host was unable to reach the clique and is possibly unhealthy.

<strong>LeaderDaemon :</strong> Heart beating only writes will records are latest after a certain timestamp. We need to remove all the older records (records with RANGE_KEY < TIMESTAMP + some delta i.e. TOLERANCE) to make sure we remove FALLEN hosts and we don't consume high RCUs during queries for healthy records. Also, older records don't serve any purpose. We need to elect a leader who will clean up the older records. For this purpose, we will use a locking table in DynamoDB. Attaining a lock record in this table which elect a leader. The leader will clean up older records. 

<strong>CleanupAndAuditDaemon :</strong> LeaderDaemon will send FAILED notifications to SNS. The cleanup daemon will create an audit trail and logging in CloudWatch by consuming these notifications.

<strong>FetchAndStoreHealthyRecordsDaemon :</strong> Fetch all the healthy hosts which have checked in to the clique within a certain TOLERANCE. For sake of this design, we will make a thread on the local machine. This job can more efficiently and cheaply run by AWS Lambda.

<strong>GetHealthyHost API :</strong> A restful API to return the "best" healthy host in the clique. This API is used on the localhost only. Implicitly, we cannot reach out to another host to fetch details about which host to communicate to. In real world applications, the User application will reach out to this API on localhost and fetch the healthy hosts in its clique.
