package com.myfailuredetector.components;

import java.time.Duration;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import com.myfailuredetector.data.Constants;
import com.myfailuredetector.data.Heartbeat;
import com.myfailuredetector.data.HostState;
import com.myfailuredetector.data.LockObject;
import com.myfailuredetector.repository.HeartbeatRepository;
import com.myfailuredetector.repository.LockRepository;
import com.myfailuredetector.repository.NotificationHelper;

public class LeaderDaemon
{

    private final LockRepository<LockObject> lockRepository;
    private final HeartbeatRepository<Heartbeat> heartbeatRepository;
    private final NotificationHelper<Heartbeat> notificationHelper;
    private final String hostUUID;

    public LeaderDaemon( final LockRepository<LockObject> lockRepository, final HeartbeatRepository<Heartbeat> heartbeatRepository,
                         final NotificationHelper<Heartbeat> notificationHelper, final String hostUUID )
    {
        this.lockRepository = lockRepository;
        this.heartbeatRepository = heartbeatRepository;
        this.notificationHelper = notificationHelper;
        this.hostUUID = hostUUID;
    }

    public void run() {
        final Date executionStartTimestamp = new Date();
        if (tryToBecomeLeader( executionStartTimestamp )) {
            // Query heartbeats with RANGE_KEY <= NOW - TOLERANCE
            Date now = new Date(  );
            final Date cutOffTimestamp = new Date( now.getTime() - Constants.HOST_CHECK_IN_TOLERANCE.toMillis() );
            final Set<Heartbeat> olderRecords = heartbeatRepository.getOlderRecords( cutOffTimestamp );
            final Set<Heartbeat> fallenRecords = olderRecords.stream().map( heartbeat -> heartbeat.setHostState( HostState.FAILED ) ).collect( Collectors.toSet() );
            this.heartbeatRepository.storeAll( fallenRecords );

            /*
             The following can also be accomplished by tapping into DDB streams and running a AWS Lambda.
              */
            this.notificationHelper.sendNotifications( fallenRecords );
        }
    }

    private boolean tryToBecomeLeader( final Date lockedAt ) {
        // Try to grab lock from dynamo db
        LockObject lockObject = new LockObject( hostUUID, lockedAt );
        return lockRepository.lock( lockObject, Duration.ofSeconds( 2 ) );
    }
}
