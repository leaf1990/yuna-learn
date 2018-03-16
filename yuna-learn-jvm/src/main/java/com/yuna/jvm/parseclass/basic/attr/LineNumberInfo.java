package com.yuna.jvm.parseclass.basic.attr;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * u2   start_pc
 * u2   line_number 
 */
public class LineNumberInfo {
    public int startPc;
    public int lineNumber;

    public void read(InputStream is) {
        startPc = Bytes.readU2(is);
        lineNumber = Bytes.readU2(is);
    }
}
