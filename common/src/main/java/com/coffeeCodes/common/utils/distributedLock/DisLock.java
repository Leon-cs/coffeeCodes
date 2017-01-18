package com.coffeeCodes.common.utils.distributedLock;

import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

/**
 * Created by ChangSheng on 2017/1/18 14:06.
 */
public interface DisLock{

    boolean tryLock(Jedis jedis);
    boolean tryLock(long time, TimeUnit unit, Jedis jedis) throws InterruptedException;
    void unlock(Jedis jedis);
}