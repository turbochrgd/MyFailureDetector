package com.myfailuredetector.data;

import java.time.Duration;

public class Constants
{
    public static final Duration HEARTBEAT_INTERVAL = Duration.ofSeconds( 10 );
    public static final Duration HOST_CHECK_IN_TOLERANCE = HEARTBEAT_INTERVAL.plus( Duration.ofSeconds( 5 ));
    public static final Duration HEALTHY_HOST_FILE_UPDATE_INTERVAL = Duration.ofSeconds( 30 );
    public static final String SERVICE_NAME = "MyHighlyAvailableService";
    public static final String HEARTBEAT_TABLE = "host_states";
}
