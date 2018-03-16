package com.yuna.jvm.parseclass.basic.attr;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * u2   attribute_name_index
 * u4   attribute_length
 * u2   line_number_table_length
 * line_number_info 
 */
public class LineNumberTableAttrInfo extends BasicAttributeInfo {
    public static final String TYPE = "LineNumberTable";
    public int lineNumberTableLength;
    public LineNumberInfo[] lineNumberTable;

    @Override
    public void read(InputStream is) {
        lineNumberTableLength = Bytes.readU2(is);
        if (lineNumberTableLength > 0) {
            lineNumberTable = new LineNumberInfo[lineNumberTableLength];
            for (int i = 0; i < lineNumberTableLength; i++) {
                LineNumberInfo info = new LineNumberInfo();
                info.read(is);
                lineNumberTable[i] = info;
            }
        }
    }

    @Override
    public void display() {

    }
}
