package com.yuna.jvm.parseclass.basic.attr;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * u2   attribute_name_index
 * u4   attribute_length
 * u1   info                    array[attribute_length]
 */
public class AttributeInfo extends BasicAttributeInfo {

    @Override
    public void read(InputStream is) {
        attributeNameIndex = Bytes.readU2(is);
        attributeLength = Bytes.readU4(is);
        if (attributeLength > 0) {
            info = new short[(int) attributeLength];
            for (int i = 0; i < attributeLength; i++) {
                info[i] = Bytes.readU1(is);
            }
        }
    }
    
    @Override
    public void display() {
        
    }
}
