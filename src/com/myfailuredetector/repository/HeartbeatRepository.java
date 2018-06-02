package com.myfailuredetector.repository;

public interface HeartbeatRepository<T>
{
    void store(T data);
}
