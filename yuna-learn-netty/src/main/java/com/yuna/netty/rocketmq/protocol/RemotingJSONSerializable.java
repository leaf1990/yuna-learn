package com.yuna.netty.rocketmq.protocol;

import com.alibaba.fastjson.JSON;

import java.nio.charset.Charset;

/**
 * Created by yuna430 on 2018/3/19 0019.
 */
public abstract class RemotingJSONSerializable {
    private final static Charset UTF8 = Charset.forName("UTF-8");

    public static byte[] encode(final Object obj) {
        final String json = toJson(obj);
        System.out.println(json);
        if (json != null) {
            return json.getBytes(UTF8);
        }
        return null;
    }

    public static String toJson(final Object obj) {
        return toJson(obj, false);
    }

    public static String toJson(final Object obj, final boolean prettyFormat) {
        return JSON.toJSONString(obj, prettyFormat);
    }

    public static <T> T decode(final byte[] data, final Class<T> clazz) {
        final String json = new String(data, UTF8);
        System.out.println(json);
        return fromJson(json, clazz);
    }

    public static <T> T fromJson(final String json, final Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public byte[] encode() {
        return encode(this);
    }

    public String toJson() {
        return toJson(this);
    }

    public String toJson(final boolean prettyFormat) {
        return toJson(this, prettyFormat);
    }
}
