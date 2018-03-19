package com.yuna.netty.rocketmq.protocol;

/**
 * Created by yuna430 on 2018/3/19 0019.
 */
public enum SerializeType {
    JSON((byte) 0),
    ROCKETMQ((byte) 1);
    private byte code;

    SerializeType(byte code) {
        this.code = code;
    }

    public static SerializeType valueOf(byte code) {
        for (SerializeType serializeType : SerializeType.values()) {
            if (serializeType.getCode() == code) {
                return serializeType;
            }
        }
        return null;
    }

    public byte getCode() {
        return code;
    }
}
