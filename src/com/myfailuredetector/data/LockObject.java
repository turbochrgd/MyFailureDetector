package com.myfailuredetector.data;

import java.time.Duration;
import java.util.Date;

public class LockObject
{
    private final String lockIdentifier = "MyFailureDetector";
    private final String lockHolderUUID;
    private final Date lockedAt;
    private final Duration ttl = Constants.DYNAMODB_LOCK_TTL;

    public LockObject( final String lockHolderUUID, final Date lockedAt )
    {
        this.lockHolderUUID = lockHolderUUID;
        this.lockedAt = lockedAt;
    }
}
