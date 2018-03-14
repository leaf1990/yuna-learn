package com.yuna.zookeeper.leader;

import com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by yeyayun on 2018/3/14 0014.
 */
public class TaskLanucher {
    private static final Logger logger = LoggerFactory.getLogger(TaskLanucher.class);
    private static final int BASE_SLEEP_TIME_MS = 1000;
    private static final int MAX_RETRIES = 3;
    private String zkServers;
    private int workPoolSize = 2;
    private Map<String, AbstractProcessor> processorMap = Maps.newHashMap();
    private CuratorFramework client;
    private TaskLeaderSelector taskLeaderSelector;
    private volatile boolean running = false;

    public void start() throws Exception {
        if (!running) {
            client = CuratorFrameworkFactory.newClient(zkServers, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
            client.start();
            taskLeaderSelector = new TaskLeaderSelector(client, workPoolSize, processorMap);
            try {
                taskLeaderSelector.start();
            } catch (Exception e) {
                logger.error("TaskLeaderSelector start error!!");
                this.stop();
                throw e;
            }
        } else {
            throw new Exception("TaskLanucher already started!!!");
        }
        logger.info("TaskLanucher started!!!");
    }

    public void stop() {
        CloseableUtils.closeQuietly(client);
        CloseableUtils.closeQuietly(taskLeaderSelector);
        running = false;
        logger.info("TaskLanucher stoped!!!");
    }

    public String getZkServers() {
        return zkServers;
    }

    public TaskLanucher setZkServers(String zkServers) {
        this.zkServers = zkServers;
        return this;
    }

    public int getWorkPoolSize() {
        return workPoolSize;
    }

    public TaskLanucher setWorkPoolSize(int workPoolSize) {
        this.workPoolSize = workPoolSize;
        return this;
    }

    public Map<String, AbstractProcessor> getProcessorMap() {
        return processorMap;
    }

    public TaskLanucher setProcessorMap(Map<String, AbstractProcessor> processorMap) {
        this.processorMap = processorMap;
        return this;
    }
}
