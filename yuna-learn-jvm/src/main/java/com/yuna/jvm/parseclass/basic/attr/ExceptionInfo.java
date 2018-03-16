package com.yuna.jvm.parseclass.basic.attr;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * u2   start_pc
 * u2   end_pc
 * u2   hanlder_pc
 * u2   catch_type
 */
public class ExceptionInfo {
    public int startPc;
    public int endPc;
    public int hanlderPc;
    public int catchType;

    public void read(InputStream is) {
        startPc = Bytes.readU2(is);
        endPc = Bytes.readU2(is);
        hanlderPc = Bytes.readU2(is);
        catchType = Bytes.readU2(is);
    }
}
