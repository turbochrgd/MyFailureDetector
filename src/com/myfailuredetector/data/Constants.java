package com.myfailuredetector.data;

import java.time.Duration;

public class Constants
{
    public static final Duration DYNAMODB_LOCK_TTL = Duration.ofSeconds( 60 );
    public static final Duration HEARTBEAT_INTERVAL = Duration.ofSeconds( 10 );
    public static final Duration HOST_CHECK_IN_TOLERANCE = HEARTBEAT_INTERVAL.plus( Duration.ofSeconds( 5 ));
}
