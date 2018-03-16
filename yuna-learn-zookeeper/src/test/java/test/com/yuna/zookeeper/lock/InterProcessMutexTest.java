package test.com.yuna.zookeeper.lock;

import com.yuna.zookeeper.config.ZookeeperConfig;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yeyayun on 2018/3/15 0015.
 */
public class InterProcessMutexTest {

    public static void main(String[] args) {
        new Thread(() -> new InterProcessMutexBean("bean1").use(), "yuna-thread-01").start();
        new Thread(() -> new InterProcessMutexBean("bean2").use(), "yuna-thread-02").start();
        BlockingQueue query = new ArrayBlockingQueue(1);
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class InterProcessMutexBean {
    private InterProcessMutex lock;
    private String name;

    public InterProcessMutexBean(String name) {
        this.name = name;
        lock = new InterProcessMutex(ZookeeperConfig.CLIENT, "/yuna/zookeeper/lock");
    }

    public void use() {
        try {
            lock.acquire();
            System.out.println(name + " in use");
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
