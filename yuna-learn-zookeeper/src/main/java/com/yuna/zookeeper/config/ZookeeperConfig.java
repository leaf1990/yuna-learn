package com.yuna.zookeeper.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by yeyayun on 2018/3/14 0014.
 */
public class ZookeeperConfig {
    public final static String ADDRESS = "127.0.0.1:2181";
    public final static String ZK_TASK_LEADER_PATH = "/yuna/zookeeper/leader";


    public static final int BASE_SLEEP_TIME_MS = 1000;
    public static final int MAX_RETRIES = 3;
    public static final CuratorFramework CLIENT = CuratorFrameworkFactory.newClient(ZookeeperConfig.ADDRESS, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));

    static {
        CLIENT.start();
    }
}
