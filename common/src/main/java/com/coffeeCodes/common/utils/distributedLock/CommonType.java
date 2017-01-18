package com.coffeeCodes.common.utils.distributedLock;

/**
 * Created by ChangSheng on 2017/1/18 14:05.
 */
public class CommonType {

    public static int WAITLOCKERS = 2;//当前服务器等待锁的线程数量，如果超过或等于此值，当前线程直接返回，不再等待锁
    public static String REDISKEY="coffee:dislock:";//redis下key前缀
    public static int LOCKEXPIRETIME = 5;//锁的过期时间，单位秒，默认5秒过期
}