package com.yuna.jvm.parseclass.basic.attr;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;

/**
 * u2   attribute_name_index
 * u4   attribute_length
 * u2   number_of_classes
 * inner_classe_info    inner_classes 
 */
public class InnerClassesAttrInfo extends BasicAttributeInfo {
    public static final String TYPE = "InnerClasses";
    public int numberOfClasses;
    public InnerClassesInfo[] innerClasses;

    @Override
    public void read(InputStream is) {
        numberOfClasses = Bytes.readU2(is);
        if (numberOfClasses > 0) {
            innerClasses = new InnerClassesInfo[numberOfClasses];
            for (int i = 0; i < numberOfClasses; i++) {
                InnerClassesInfo info = new InnerClassesInfo();
                info.read(is);
                innerClasses[i] = info;
            }
        }
    }

    @Override
    public void display() {

    }
}
