package com.myfailuredetector.components;

import java.io.File;

import com.myfailuredetector.repository.HealthyHostFileRepository;

public class FetchAndStoreHealthyRecordsDameon implements Runnable
{
    // We can also use a template with today's date as suffix
    private static final String S3_BUCKET_NAME = "my-failure-detector-app-healthy-records";
    private static final String HEALTHY_HOST_FILE_NAME = "healthy_hosts";
    private static final String LOCAL_HEALTHY_HOST_FILE_PATH_TEMPLATE = "/tmp/%s/%s";

    private final HealthyHostFileRepository healthyHostFileRepository;

    public FetchAndStoreHealthyRecordsDameon( final HealthyHostFileRepository healthyHostFileRepository )
    {
        this.healthyHostFileRepository = healthyHostFileRepository;
    }

    /*
    The bulk of the work will be done by a scheduled Lambda. The Lambda function will read all HEALTHY records after every interval and write to an S3 file.
    This class will only download that file to local disk
     */
    public void fetchHealthyRecords() {
        final String localFileName = String.format( LOCAL_HEALTHY_HOST_FILE_PATH_TEMPLATE, S3_BUCKET_NAME, HEALTHY_HOST_FILE_NAME );
        System.out.println( String.format( "Will fetch healthy hosts to file %s", localFileName ));
        this.healthyHostFileRepository.fetchHealthyHostsFileToLocalFile( S3_BUCKET_NAME, HEALTHY_HOST_FILE_NAME, new File( localFileName ) );
    }

    @Override
    public void run()
    {
        this.fetchHealthyRecords();
    }
}
