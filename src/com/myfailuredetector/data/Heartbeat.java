package com.myfailuredetector.data;

import java.util.Date;

public class Heartbeat
{
    private final String hostUUID;
    private final Date timestamp;
    private final HostState hostState;

    public Heartbeat( final String hostUUID, final Date timestamp, final HostState hostState )
    {
        this.hostUUID = hostUUID;
        this.timestamp = timestamp;
        this.hostState = hostState;
    }

    public Heartbeat( final String hostUUID, final Date timestamp, final String hostState )
    {
        this(hostUUID, timestamp, HostState.valueOf( hostState ));
    }

    public Heartbeat setHostState( final HostState hostState )
    {
        return new Heartbeat( this.hostUUID, this.timestamp, this.hostState );
    }
}
