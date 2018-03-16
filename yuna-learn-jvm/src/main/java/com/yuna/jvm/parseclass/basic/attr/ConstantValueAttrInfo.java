package com.yuna.jvm.parseclass.basic.attr;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * u2   attribute_name_index
 * u4   attribute_length
 * u2   constantvalue_index     ==> CONSTANT_(Long|Float|Double|Integer|String)_info
 */
public class ConstantValueAttrInfo extends BasicAttributeInfo {
    public static final String TYPE = "ConstantValue";
    public int constantvalueIndex;

    @Override
    public void read(InputStream is) {
        constantvalueIndex = Bytes.readU2(is);
    }

    @Override
    public void display() {

    }
}
