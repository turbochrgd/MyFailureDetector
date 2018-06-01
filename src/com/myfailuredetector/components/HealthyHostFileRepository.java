package com.myfailuredetector.components;

import java.io.File;

public interface HealthyHostFileRepository
{
    void fetchHealthyHostsFileToLocalFile( String bucketName, final String fileName, File file );
}
