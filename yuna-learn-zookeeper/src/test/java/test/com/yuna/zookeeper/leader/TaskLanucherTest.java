package test.com.yuna.zookeeper.leader;

import com.google.common.collect.Maps;
import com.yuna.zookeeper.config.ZookeeperConfig;
import com.yuna.zookeeper.leader.AbstractProcessor;
import com.yuna.zookeeper.leader.TaskLanucher;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yeyayun on 2018/3/14 0014.
 */
public class TaskLanucherTest {

    @Test
    public void test_start() throws Exception {
        TaskLanucher lanucher = new TaskLanucher();
        lanucher.setZkServers(ZookeeperConfig.ADDRESS);
        lanucher.setProcessorMap(processorMap());
        lanucher.setZkPath(ZookeeperConfig.ZK_TASK_LEADER_PATH);
        lanucher.start();

        synchronized (this) {
            this.wait();
        }
    }

    private Map<String, AbstractProcessor> processorMap() {
        Map<String, AbstractProcessor> processorMap = Maps.newHashMap();
        processorMap.put("aaa", new AbstractProcessor() {
            @Override
            public void process() {
                logger.info("aaa-start");
                getExecutorService().schedule(() -> process(), 1000, TimeUnit.MILLISECONDS);
            }
        });
        processorMap.put("bbb", new AbstractProcessor() {
            @Override
            public void process() {
                logger.info("bbb-start");
                getExecutorService().schedule(() -> process(), 1000, TimeUnit.MILLISECONDS);
            }
        });
        processorMap.put("PromotionProcessor", new PromotionProcessor());
        return processorMap;
    }
}
