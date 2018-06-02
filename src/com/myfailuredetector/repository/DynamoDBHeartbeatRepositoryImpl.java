package com.myfailuredetector.repository;

import com.myfailuredetector.data.Heartbeat;

public class DynamoDBHeartbeatRepositoryImpl<T> implements HeartbeatRepository<Heartbeat>
{
    @Override
    public void store( final Heartbeat data )
    {
        throw new UnsupportedOperationException( "Implement me" );
    }
}
