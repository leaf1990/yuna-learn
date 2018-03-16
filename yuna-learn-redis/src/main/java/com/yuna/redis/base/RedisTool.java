package com.yuna.redis.base;

import com.yuna.redis.util.ConvertUtil;
import com.yuna.redis.util.JavassistJedisProxyFactory;
import com.yuna.redis.util.JedisCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;

/**
 * Reids操作封装，以便以后统一监控埋点
 * <p>
 * Created by yeyayun on 2018/3/16 0016.
 */
public class RedisTool {
    protected final static Logger logger = LoggerFactory.getLogger(RedisTool.class);

    private JedisPool jedisPool;

    public RedisTool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public <T> T execute(JedisCallback<T> jedisCallback) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        logger.debug("invoke method:" + methodName);
        Jedis jedis = null;
        try {
//             jedis = jedisPool.getResource();
            jedis = getJedis();
            return jedisCallback.execute(jedis);
        } catch (Exception e) {
            throw e;
        } finally {
            jedis.close();
        }
    }

    public Jedis getJedis() {
        try {
            JavassistJedisProxyFactory factory = new JavassistJedisProxyFactory();
            factory.setTarget(jedisPool.getResource());
            return (Jedis) factory.getProxy();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /////////////////////////////////////////
    //Common
    /////////////////////////////////////////

    /**
     * String - set
     *
     * @param key
     * @return
     */
    public Long del(String key) {
        return execute((jedis) -> jedis.del(key));
    }
    /////////////////////////////////////////
    //String get set mget mset
    /////////////////////////////////////////

    /**
     * String - get
     *
     * @param key
     * @return
     */
    public String get(String key) {
        return execute((jedis) -> jedis.get(key));
    }

    /**
     * String - set
     *
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value) {
        return execute((jedis) -> jedis.set(key, value));
    }

    /**
     * String - mget
     *
     * @param keys
     * @return
     */
    public List<String> mget(List<String> keys) {
        return execute((jedis) -> jedis.mget(ConvertUtil.listToArray(keys)));
    }

    /**
     * String - mset
     *
     * @param keyValues
     * @return
     */
    public String mset(Map<String, String> keyValues) {
        return execute(jedis -> jedis.mset(ConvertUtil.mapToArray(keyValues)));
    }

    /////////////////////////////////////////
    //Hash
    /////////////////////////////////////////

    /////////////////////////////////////////
    //List
    /////////////////////////////////////////

    /////////////////////////////////////////
    //Set
    /////////////////////////////////////////

    /////////////////////////////////////////
    //ZSet
    /////////////////////////////////////////

    public RedisTool setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        return this;
    }
}
