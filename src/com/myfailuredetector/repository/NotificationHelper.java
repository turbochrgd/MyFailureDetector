package com.myfailuredetector.repository;

import java.util.Set;

public interface NotificationHelper<T>
{
    void sendNotifications( Set<T> records );
}
