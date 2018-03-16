package com.yuna.jvm.parseclass.constant;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * 12. 字段或方法的部分符号引用
 * 
 * u1	tag
 * u2	utf8_index(方法名)
 * u2	utf8_index(描述)
 */
public class ConstantNameAndType extends BasicConstantInfo {
    public static final int TYPE = 12;
    public static final String NAME = "NameAndType";

    public int nameIndex;
    public int descIndex;

    @Override
    public void read(InputStream is) {
        nameIndex = Bytes.readU2(is);
        descIndex = Bytes.readU2(is);
    }
}
