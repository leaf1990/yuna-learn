package com.yuna.jvm.parseclass.constant;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * 16. 标识方法类型
 * 
 * u1	tag
 * u2	descriptor_index
 */
public class ConstantMethodType extends BasicConstantInfo {
    public static final int TYPE = 16;
    public static final String NAME = "MethodType";

    public int descIndex;

    @Override
    public void read(InputStream is) {
        descIndex = Bytes.readU2(is);
    }
}
