package com.yuna.redis.util;

import redis.clients.jedis.Jedis;

/**
 * @param <T>
 */
public interface JedisCallback<T> {
    T execute(Jedis jedis);
}
