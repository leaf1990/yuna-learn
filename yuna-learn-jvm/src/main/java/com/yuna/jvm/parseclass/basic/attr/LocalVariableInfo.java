package com.yuna.jvm.parseclass.basic.attr;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * u2   start_pc
 * u2   length
 * u2   name_index
 * u2   descriptor_index
 * u2   index 
 */
public class LocalVariableInfo {
    public int startPc;
    public int length;
    public int nameIndex;
    public int descriptorIndex;
    public int index;

    public void read(InputStream is) {
        startPc = Bytes.readU2(is);
        length = Bytes.readU2(is);
        nameIndex = Bytes.readU2(is);
        descriptorIndex = Bytes.readU2(is);
        index = Bytes.readU2(is);
    }
}
