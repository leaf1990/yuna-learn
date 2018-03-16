package com.yuna.jvm.parseclass.constant;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * 6. 双精度浮点型字面量
 * 
 * u1	tag 
 * u8	bytes
 */
public class ConstantDouble extends BasicConstantInfo {
    public static final int TYPE = 6;
    public long highValue;
    public long lowValue;

    @Override
    public void read(InputStream is) {
        highValue = Bytes.readU4(is);
        lowValue = Bytes.readU4(is);
    }
}
