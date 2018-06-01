package com.myfailuredetector.repository;

import java.time.Duration;

public interface LockRepository<T>
{

    boolean lock( T t, Duration revalidationTime );

}
