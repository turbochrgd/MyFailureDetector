package com.myfailuredetector.components;

import java.util.Date;

import com.myfailuredetector.data.Heartbeat;
import com.myfailuredetector.data.HostState;
import com.myfailuredetector.repository.HeartbeatRepository;

public class HeartbeatDaemon
{
    private final String hostUUID;
    private final HeartbeatRepository<Heartbeat> heartbeatRepository;

    public HeartbeatDaemon( final String hostUUID, final HeartbeatRepository heartbeatRepository )
    {
        this.hostUUID = hostUUID;
        this.heartbeatRepository = heartbeatRepository;
    }

    public void heartbeat() {
        Heartbeat heartbeat = new Heartbeat( hostUUID, new Date(), HostState.HEALTHY );
        heartbeatRepository.store( heartbeat );
    }
}
