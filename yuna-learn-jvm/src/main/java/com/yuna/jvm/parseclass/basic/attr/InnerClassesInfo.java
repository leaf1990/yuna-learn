package com.yuna.jvm.parseclass.basic.attr;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * u2   inner_class_info_index
 * u2   outer_class_info_index
 * u2   inner_name_index
 * u2   inner_class_access_flags
 */
public class InnerClassesInfo {
    public int innerClassInfoIndex;
    public int outerClassInfoIndex;
    public int innerNameIndex;
    public int innerClassAccessFlags;

    public void read(InputStream is) {
        innerClassInfoIndex = Bytes.readU2(is);
        outerClassInfoIndex = Bytes.readU2(is);
        innerNameIndex = Bytes.readU2(is);
        innerClassAccessFlags = Bytes.readU2(is);
    }
}
