package test.com.yuna.zookeeper.leader;

import com.yuna.zookeeper.leader.AbstractProcessor;

import java.util.concurrent.TimeUnit;

/**
 * Created by yeyayun on 2018/3/14 0014.
 */
public class PromotionProcessor extends AbstractProcessor {
    @Override
    public void process() {
        logger.info("PromotionProcessor start");
        getExecutorService().schedule(() -> process(), 1000, TimeUnit.MILLISECONDS);
    }
}
