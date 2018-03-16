package test.com.yuna.redis.base;

import com.yuna.redis.base.RedisTool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPool;

/**
 * Created by yeyayun on 2018/3/16 0016.
 */
public class RedisTookTest {
    private final static String OK = "OK";
    private JedisPool jedisPool;
    private RedisTool redisTool;

    @Before
    public void init() {
        jedisPool = new JedisPool("127.0.0.1", 6379);
        redisTool = new RedisTool(jedisPool);
    }

    @Test
    public void test_stringGetSet() {
        String key = "test01";
        String value = "test01";
        Assert.assertEquals(OK, redisTool.set(key, value));
        Assert.assertEquals(value, redisTool.get(key));
        Assert.assertEquals(1l, (long) redisTool.del(key));
    }
}
