package com.yuna.jvm.parseclass.constant;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * 10. 类中方法符号引用
 * 
 * u1	tag
 * u2	class_index
 * u2	nameAndType_index
 */
public class ConstantMethodRef extends BasicConstantInfo {
    public static final int TYPE = 10;
    public static final String NAME = "Methodref";

    public int classIndex;
    public int nameAndTypeIndex;

    @Override
    public void read(InputStream is) {
        classIndex = Bytes.readU2(is);
        nameAndTypeIndex = Bytes.readU2(is);
    }
}
