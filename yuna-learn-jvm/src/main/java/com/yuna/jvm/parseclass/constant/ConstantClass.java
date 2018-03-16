package com.yuna.jvm.parseclass.constant;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * 7. 类或接口的符号引用
 * 
 * u1	tag				1 
 * u2	utf8_index		1
 */
public class ConstantClass extends BasicConstantInfo {
    public static final int TYPE = 7;
    public static final String NAME = "Class";

    public int nameIndex;

    @Override
    public void read(InputStream is) {
        nameIndex = Bytes.readU2(is);
    }
}
