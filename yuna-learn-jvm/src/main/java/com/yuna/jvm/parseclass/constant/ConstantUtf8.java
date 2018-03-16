package com.yuna.jvm.parseclass.constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * 1. UTF-8编码的字符串
 * 
 * u1	tag			1
 * u2	length		1
 * u1	bytes		length
 */
public class ConstantUtf8 extends BasicConstantInfo {
    public static final int TYPE = 1;
    public static final String NAME = "Utf8";

    public int length;
    public byte[] bytes;
    public String value;

    @Override
    public void read(InputStream is) {
        length = Bytes.readU2(is);
        bytes = new byte[length];
        try {
            is.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            value = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "ConstantUtf8 [value=" + value + "]";
    }
}
