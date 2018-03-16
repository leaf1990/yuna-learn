package com.yuna.jvm.parseclass.basic.attr;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * u2   attribute_name_index
 * u4   attribute_length
 * u2   signature_index     ==> CONSTANT_Utf8_info
 */
public class SignatureAttrInfo extends BasicAttributeInfo {
    public static final String TYPE = "Signature";
    public int signatureIndex;

    @Override
    public void read(InputStream is) {
        signatureIndex = Bytes.readU2(is);
    }

    @Override
    public void display() {

    }
}
