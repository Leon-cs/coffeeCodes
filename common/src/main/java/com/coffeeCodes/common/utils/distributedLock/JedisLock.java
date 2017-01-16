package com.coffeeCodes.common.utils.distributedLock;

import redis.clients.jedis.Jedis;

import java.util.UUID;

public class JedisLock {

    private static final Lock NO_LOCK = new Lock(new UUID(0L, 0L), 0L);
    
    private static final int ONE_SECOND = 1000;

    private static final int DEFAULT_EXPIRY_TIME_MILLIS = Integer.getInteger("com.coffeeCodes.redis.lock.expiry.millis", 60 * ONE_SECOND);
    private static final int DEFAULT_ACQUIRE_TIMEOUT_MILLIS = Integer.getInteger("com.coffeeCodes.redis.lock.acquire.millis", 10 * ONE_SECOND);
    private static final int DEFAULT_ACQUIRE_RESOLUTION_MILLIS = Integer.getInteger("com.coffeeCodes.redis.lock.acquire.resolution.millis", 100);

    private final Jedis jedis;

    private final String lockKeyPath;

    private final int lockExpiryInMillis;
    private final int acquireTimeoutInMillis;
    private final UUID lockUUID;

    private Lock lock = null;

    /**
     * 内部类
     * uuid:单例
     * expiry time:过期时间
     *
     */
    protected static class Lock {
        private UUID uuid;
        private long expiryTime;

        protected Lock(UUID uuid, long expiryTimeInMillis) {
            this.uuid = uuid;
            this.expiryTime = expiryTimeInMillis;
        }
        
        protected static Lock fromString(String text) {
            try {
                String[] parts = text.split(":");
                UUID theUUID = UUID.fromString(parts[0]);
                long theTime = Long.parseLong(parts[1]);
                return new Lock(theUUID, theTime);
            } catch (Exception any) {
                return NO_LOCK;
            }
        }
        
        public UUID getUUID() {
            return uuid;
        }

        public long getExpiryTime() {
            return expiryTime;
        }
        
        @Override
        public String toString() {
            return uuid.toString()+":"+expiryTime;  
        }

        boolean isExpired() {
            return getExpiryTime() < System.currentTimeMillis();
        }

        boolean isExpiredOrMine(UUID otherUUID) {
            return this.isExpired() || this.getUUID().equals(otherUUID);
        }
    }
    
    
    /**
     * 使用 默认的获取锁超时时间与默认的锁过期时间
     */
    public JedisLock(Jedis jedis, String lockKey) {
        this(jedis, lockKey, DEFAULT_ACQUIRE_TIMEOUT_MILLIS, DEFAULT_EXPIRY_TIME_MILLIS);
    }

    /**
     * 使用 默认的锁过期时间
     */
    public JedisLock(Jedis jedis, String lockKey, int acquireTimeoutMillis) {
        this(jedis, lockKey, acquireTimeoutMillis, DEFAULT_EXPIRY_TIME_MILLIS);
    }

    /**
     * 构造方法
     */
    public JedisLock(Jedis jedis, String lockKey, int acquireTimeoutMillis, int expiryTimeMillis) {
        this(jedis, lockKey, acquireTimeoutMillis, expiryTimeMillis, UUID.randomUUID());
    }

    /**
     * uuid为锁的唯一标识
     */
    public JedisLock(Jedis jedis, String lockKey, int acquireTimeoutMillis, int expiryTimeMillis, UUID uuid) {
        this.jedis = jedis;
        this.lockKeyPath = lockKey;
        this.acquireTimeoutInMillis = acquireTimeoutMillis;
        this.lockExpiryInMillis = expiryTimeMillis+1;
        this.lockUUID = uuid;
    }

    public UUID getLockUUID() {
        return lockUUID;
    }

    /**
     * @return lock key path
     */
    public String getLockKeyPath() {
        return lockKeyPath;
    }

    /**
     * 获取锁
     */
    public synchronized boolean acquire() throws InterruptedException {
        return acquire(jedis);
    }

    /**
     * 获取锁
     */
    protected synchronized boolean acquire(Jedis jedis) throws InterruptedException {
        int timeout = acquireTimeoutInMillis;
        while (timeout >= 0) {
            final Lock newLock = asLock(System.currentTimeMillis() + lockExpiryInMillis);
            if (jedis.setnx(lockKeyPath, newLock.toString()) == 1) {
                //jedis.expire(lockKeyPath, expireTime);
                this.lock = newLock;
                return true;
            }

            final String currentValueStr = jedis.get(lockKeyPath);
            final Lock currentLock = Lock.fromString(currentValueStr);
            if (currentLock.isExpiredOrMine(lockUUID)) {
                String oldValueStr = jedis.getSet(lockKeyPath, newLock.toString());
                if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                    this.lock = newLock;
                    return true;
                }
            }

            timeout -= DEFAULT_ACQUIRE_RESOLUTION_MILLIS;
            Thread.sleep(DEFAULT_ACQUIRE_RESOLUTION_MILLIS);
        }

        return false;
    }

    /**
     * Renew lock.
     * 
     * @return true if lock is acquired, false otherwise
     * @throws InterruptedException
     *             in case of thread interruption
     */
    public boolean renew() throws InterruptedException {
        final Lock lock = Lock.fromString(jedis.get(lockKeyPath));
        return lock.isExpiredOrMine(lockUUID) && acquire(jedis);
    }

    /**
     * 释放锁
     */
    public synchronized void release() {
        release(jedis);
    }

    /**
     * 释放锁
     */
    protected synchronized void release(Jedis jedis) {
        if (isLocked()) {
            jedis.del(lockKeyPath);
            this.lock = null;
        }
    }

    /**
     * Check if owns the lock
     * @return  true if lock owned
     */
    public synchronized boolean isLocked() {
        return this.lock != null;
    }
    
    /**
     * Returns the expiry time of this lock
     * @return  the expiry time in millis (or null if not locked)
     */
    public synchronized long getLockExpiryTimeInMillis() {
        return this.lock.getExpiryTime();
    }
    
    
    private Lock asLock(long expires) {
        return new Lock(lockUUID, expires);
    }


}
