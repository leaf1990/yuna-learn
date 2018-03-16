package com.yuna.jvm.parseclass.constant;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * 8. 字符串类型字面量
 * 
 * u1	tag
 * u2	utf8_index
 */
public class ConstantString extends BasicConstantInfo {
    public static final int TYPE = 8;
    public static final String NAME = "String";

    public int valueIndex;

    @Override
    public void read(InputStream is) {
        valueIndex = Bytes.readU2(is);
    }

}
