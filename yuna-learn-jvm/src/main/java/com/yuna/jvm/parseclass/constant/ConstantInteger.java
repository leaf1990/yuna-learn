package com.yuna.jvm.parseclass.constant;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * 3. 整型字面量
 * 
 * u1	tag 
 * u4	bytes
 */
public class ConstantInteger extends BasicConstantInfo {
    public static final int TYPE = 3;

    public long value;

    @Override
    public void read(InputStream is) {
        value = Bytes.readU4(is);
    }
}
