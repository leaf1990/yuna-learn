package com.yuna.jvm.parseclass.basic.attr;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * u2   attribute_name_index
 * u4   attribute_length
 * u2   number_of_exceptions
 * u2   exception_index_table   CONSTANT_Class_info array
 */
public class ExceptionsAttrInfo extends BasicAttributeInfo {
    public static final String TYPE = "Exceptions";
    public int numberOfExceptions;
    public int[] exceptionIndexTable;

    @Override
    public void read(InputStream is) {
        numberOfExceptions = Bytes.readU2(is);
        if (numberOfExceptions > 0) {
            exceptionIndexTable = new int[numberOfExceptions];
            for (int i = 0; i < numberOfExceptions; i++) {
                exceptionIndexTable[i] = Bytes.readU2(is);
            }
        }
    }

    @Override
    public void display() {

    }
}
