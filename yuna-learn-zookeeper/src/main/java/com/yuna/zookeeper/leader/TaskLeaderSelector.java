package com.yuna.zookeeper.leader;

import com.yuna.common.thread.ThreadFactoryImpl;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.CancelLeadershipException;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 通过zookeeper进行 Leader 选举，保证在分布式场景下只有一个节点运行
 * <p>
 * Created by yeyayun on 2018/3/14 0014.
 */
public class TaskLeaderSelector extends LeaderSelectorListenerAdapter implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(TaskLeaderSelector.class);
    private static final String LEADER_PATH = "/yuna/zookeeper/leader";

    private final LeaderSelector leaderSelector;
    private final int workPoolSize;
    private final ScheduledExecutorService executorService;
    private final Map<String, AbstractProcessor> processorMap;
    private volatile boolean isLeader = false;

    public TaskLeaderSelector(CuratorFramework client, int workPoolSize, Map<String, AbstractProcessor> processorMap) {
        leaderSelector = new LeaderSelector(client, LEADER_PATH, this);
        leaderSelector.autoRequeue();
        this.workPoolSize = workPoolSize;
        this.processorMap = processorMap;
        this.executorService = Executors.newScheduledThreadPool(this.workPoolSize, new ThreadFactoryImpl("LeaderTask-"));
    }

    public void start() {
        leaderSelector.start();
    }

    /**
     * Called when your instance has been granted leadership. This method
     * should not return until you wish to release leadership
     *
     * @param client the client
     * @throws Exception any errors
     */
    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        logger.info("I'm leader now!!!");
        isLeader = true;
        try {
            for (Map.Entry<String, AbstractProcessor> entry : processorMap.entrySet()) {
                entry.getValue().init(executorService);
                executorService.submit(() -> entry.getValue().process());
            }
            while (isLeader) {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            logger.error("Appear one error, will relinquish leader!", e);
            throw e;
        } finally {
            logger.info("I'm relinquish leader now!!!");
            isLeader = false;
        }
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        try {
            super.stateChanged(client, newState);
        } catch (CancelLeadershipException e) {
            isLeader = false;
        }
    }

    @Override
    public void close() throws IOException {
        leaderSelector.close();
        isLeader = false;
    }
}
