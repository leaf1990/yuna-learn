package com.yuna.jvm.parseclass.basic.attr;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * u2   attribute_name_index
 * u4   attribute_length
 * u2   local_variable_table_length
 * local_variable_table 
 */
public class LocalVariableTableAttrInfo extends BasicAttributeInfo {
    public static final String TYPE = "LocalVariableTable";
    public int localVariableTableLength;
    public LocalVariableInfo[] localVariableTable;

    @Override
    public void read(InputStream is) {
        localVariableTableLength = Bytes.readU2(is);
        if (localVariableTableLength > 0) {
            localVariableTable = new LocalVariableInfo[localVariableTableLength];
            for (int i = 0; i < localVariableTableLength; i++) {
                LocalVariableInfo info = new LocalVariableInfo();
                info.read(is);
                localVariableTable[i] = info;
            }
        }
    }

    @Override
    public void display() {

    }
}
