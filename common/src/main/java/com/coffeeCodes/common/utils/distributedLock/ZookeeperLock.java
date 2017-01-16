package com.coffeeCodes.common.utils.distributedLock;

import com.coffeeCodes.common.exceptions.CoffeeCodesException;
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by ChangSheng on 2016/11/7 14:43.
 */
@Service
@Slf4j
public class ZookeeperLock {

    @Value("${zookeeper.url}")
    private String zkServerUrl;

    @Value("${zookeeper.lockTime}")
    private long lockTime;

    private CuratorFramework client = null;

    @PostConstruct
    public void init() {
        try {
            connect();
        } catch (Exception e) {
            log.error("zookeeper connect  error：" , e);
        }
    }

    public static void main(String[] args){
        ZookeeperLock zookeeperLock = new ZookeeperLock();
        zookeeperLock.init();
        InterProcessMutex interProcessMutex = zookeeperLock.createInterProcessMutex("/test");
        zookeeperLock.acquire(interProcessMutex,3l);
        zookeeperLock.release(interProcessMutex,"/test");
    }

    /**
     * 初始化连接
     */
    private void connect() {

        client = CuratorFrameworkFactory.newClient(zkServerUrl, new ExponentialBackoffRetry(100, 3));
        client.start();
    }

    /**
     *
     * @param path  锁定路径
     */
    public InterProcessMutex createInterProcessMutex(String path){
        return new InterProcessMutex(client, path);
    }

    /**
     * 抢zk锁，重载方法，默认使用配置文件的锁定时间
     * @param lock      InterProcessMutex
     */
    public  void acquire(InterProcessMutex lock) {
        long beginTime = new Date().getTime();
        acquire(lock, lockTime);
        long endTime = new Date().getTime();
        if((endTime - beginTime) > 3000){
            log.error("ZookeeperLock timeOut : time {} " , (endTime - beginTime));
        }
    }


    /**
     * 抢zk锁，重载方法，可以自定义锁定时间
     * @param lock 单位(秒)
     * @param lockTime 锁定时间
     */
    public void acquire(InterProcessMutex lock, long lockTime) {
        try {
            if(!lock.acquire(lockTime, TimeUnit.SECONDS)){
                throw new CoffeeCodesException("抢zk锁失败");
            }
        } catch (Exception e) {
            throw new CoffeeCodesException("抢zk锁时发生异常");
        }
    }

    /**
     * 释放zk锁
     * @param lock  InterProcessMutex
     */
    public synchronized  void  release(InterProcessMutex lock, String patch) {
        try {
            if(lock != null){
                lock.release();
                // 尝试删除锁节点
                client.delete().forPath(patch);
            }
        }catch (KeeperException e) {
            //忽略zk锁子节点存在时删除节点失败
            if (!Objects.equal(e.code().name(), KeeperException.Code.NOTEMPTY.name())) {
                log.error("zookeeper lock KeeperException: {}",Throwables.getStackTraceAsString(e));
            }
        } catch (Exception e) {
            log.error("zookeeper lock release fail:{}", Throwables.getStackTraceAsString(e));
        }

    }
}
