package com.myfailuredetector.data;

import java.util.Date;

public class Heartbeat
{
    private final String hostUUID;
    private final Date timestamp;
    private final HostState hostState;
    private final String serviceName;

    public Heartbeat( final String hostUUID, final Date timestamp, final HostState hostState, final String serviceName )
    {
        this.hostUUID = hostUUID;
        this.timestamp = timestamp;
        this.hostState = hostState;
        this.serviceName = serviceName;
    }

    public String getHostUUID()
    {
        return hostUUID;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public HostState getHostState()
    {
        return hostState;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    @Override
    public String toString()
    {
        return "Heartbeat{" +
               "hostUUID='" + hostUUID + '\'' +
               ", timestamp=" + timestamp +
               ", hostState=" + hostState +
               ", serviceName='" + serviceName + '\'' +
               '}';
    }
}
