package com.yuna.jvm.parseclass.basic.attr;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * u2   attribute_name_index
 * u4   attribute_length
 * u2   sourcefile_index
 */
public class SourceFileAttrInfo extends BasicAttributeInfo {
    public static final String TYPE = "SourceFile";
    public int sourcefileIndex;

    @Override
    public void read(InputStream is) {
        sourcefileIndex = Bytes.readU2(is);
    }

    @Override
    public void display() {

    }
}
