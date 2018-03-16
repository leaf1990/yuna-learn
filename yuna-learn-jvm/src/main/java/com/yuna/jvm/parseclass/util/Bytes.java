package com.yuna.jvm.parseclass.util;

import java.io.IOException;
import java.io.InputStream;

public class Bytes {

    public static short readU1(InputStream is) {
        byte[] bytes = new byte[1];
        try {
            is.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        short ret = (short) (bytes[0] & 0xFF);
        return ret;
    }

    public static int readU2(InputStream is) {
        byte[] bytes = new byte[2];
        try {
            is.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int ret = ((int) (bytes[0] & 0xFF) << 8) + (bytes[1] & 0xFF);
        return ret;
    }

    public static long readU4(InputStream is) {
        byte[] bytes = new byte[4];
        try {
            is.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long ret = ((long) (bytes[0] & 0xFF) << 24) + ((bytes[1] & 0xFF) << 16) + ((bytes[2] & 0xFF) << 8) + (bytes[3] & 0xFF);
        return ret;
    }
}
