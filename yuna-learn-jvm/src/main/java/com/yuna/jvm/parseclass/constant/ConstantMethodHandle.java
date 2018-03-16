package com.yuna.jvm.parseclass.constant;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * 15. 表示方法句柄
 * 
 * u1	tag
 * u1	reference_kind(1~9)
 * u2	reference_index
 */
public class ConstantMethodHandle extends BasicConstantInfo {
    public static final int TYPE = 15;

    public short referenceKind;
    public int referenceIndex;

    @Override
    public void read(InputStream is) {
        referenceKind = Bytes.readU1(is);
        referenceIndex = Bytes.readU2(is);
    }
}
