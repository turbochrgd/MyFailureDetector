package com.myfailuredetector.repository;

import java.io.File;

public interface HealthyHostFileRepository
{
    void fetchHealthyHostsFileToLocalFile( String bucketName, final String fileName, File file );
}
