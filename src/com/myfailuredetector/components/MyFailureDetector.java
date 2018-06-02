package com.myfailuredetector.components;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.amazonaws.regions.Regions;
import com.myfailuredetector.data.Constants;
import com.myfailuredetector.repository.DynamoDBHeartbeatRepositoryImpl;
import com.myfailuredetector.repository.HealthyHostFileRepository;
import com.myfailuredetector.repository.HeartbeatRepository;
import com.myfailuredetector.repository.S3HealthyHostsFileRepositoryImpl;

public class MyFailureDetector
{

    private final ScheduledExecutorService scheduler;
    private final HeartbeatDaemon heartbeatDaemon;
    private final FetchAndStoreHealthyRecordsDameon fetchAndStoreHealthyRecordsDameon;

    public MyFailureDetector( final HeartbeatDaemon heartbeatDaemon, final FetchAndStoreHealthyRecordsDameon fetchAndStoreHealthyRecordsDameon )
    {
        this.scheduler = Executors.newScheduledThreadPool( 1 );
        this.heartbeatDaemon = heartbeatDaemon;
        this.fetchAndStoreHealthyRecordsDameon = fetchAndStoreHealthyRecordsDameon;
    }

    public void run() {
        this.scheduler.scheduleAtFixedRate( this.heartbeatDaemon, 5, Constants.HEARTBEAT_INTERVAL.getSeconds(), TimeUnit.SECONDS);
        this.scheduler.scheduleAtFixedRate( this.fetchAndStoreHealthyRecordsDameon, 5, Constants.HEALTHY_HOST_FILE_UPDATE_INTERVAL.getSeconds(), TimeUnit.SECONDS);
    }


    public static void main( String[] args )
    {
        HeartbeatRepository heartbeatRepository = new DynamoDBHeartbeatRepositoryImpl( Regions.US_EAST_1 );
        HealthyHostFileRepository healthyHostFileRepository = new S3HealthyHostsFileRepositoryImpl();
        final String[] hostUUIDs = {"localhost-1", "localhost-2", "localhost-3", "localhost-4", "localhost-5", "localhost-6"};
        for (String hostUUID : hostUUIDs)
        {
            HeartbeatDaemon heartbeatDaemon = new HeartbeatDaemon(hostUUID, heartbeatRepository);
            FetchAndStoreHealthyRecordsDameon fetchAndStoreHealthyRecordsDameon = new FetchAndStoreHealthyRecordsDameon( healthyHostFileRepository );
            MyFailureDetector myFailureDetector = new MyFailureDetector( heartbeatDaemon, fetchAndStoreHealthyRecordsDameon );
            myFailureDetector.run();
        }

    }
}
