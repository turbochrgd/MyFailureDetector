package com.myfailuredetector.repository;

import java.util.Date;
import java.util.Set;

import com.myfailuredetector.data.Heartbeat;
import com.myfailuredetector.data.HostState;

public interface HeartbeatRepository<T>
{

    void store(T data);
    Set<Heartbeat> getOlderRecords( Date beforeTime);

    void storeAll( Set<T> records );
}
