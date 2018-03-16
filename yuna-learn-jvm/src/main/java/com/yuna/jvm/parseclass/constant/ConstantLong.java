package com.yuna.jvm.parseclass.constant;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * 5. 长整型字面量
 * 
 * u1	tag 
 * u8	bytes
 */
public class ConstantLong extends BasicConstantInfo {
    public static final int TYPE = 5;

    public long highValue;
    public long lowValue;

    @Override
    public void read(InputStream is) {
        highValue = Bytes.readU4(is);
        lowValue = Bytes.readU4(is);
    }
}
