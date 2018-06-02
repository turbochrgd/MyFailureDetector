package com.myfailuredetector.repository;

import java.io.File;

public class S3HealthyHostsFileRepositoryImpl implements HealthyHostFileRepository
{
    @Override
    public void fetchHealthyHostsFileToLocalFile( final String bucketName, final String fileName, final File file )
    {
        throw new UnsupportedOperationException( "Implement me" );
    }
}
