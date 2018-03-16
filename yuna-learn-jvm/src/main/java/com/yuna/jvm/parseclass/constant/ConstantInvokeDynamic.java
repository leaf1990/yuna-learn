package com.yuna.jvm.parseclass.constant;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * 18. 表示一个动态方法调用点
 * 
 * u1	tag
 * u2	bootstrap_method_attr_index
 * u2	nameAndType_index
 */
public class ConstantInvokeDynamic extends BasicConstantInfo {
    public static final int TYPE = 18;

    public int bootstrapMethodAttrIndex;
    public int nameAndTypeIndex;

    @Override
    public void read(InputStream is) {
        bootstrapMethodAttrIndex = Bytes.readU2(is);
        nameAndTypeIndex = Bytes.readU2(is);
    }
}
