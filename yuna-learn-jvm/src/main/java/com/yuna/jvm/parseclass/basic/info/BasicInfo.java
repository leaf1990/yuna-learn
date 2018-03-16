package com.yuna.jvm.parseclass.basic.info;

import java.io.InputStream;

import com.yuna.jvm.parseclass.basic.ConstantPool;
import com.yuna.jvm.parseclass.basic.IDisplayable;
import com.yuna.jvm.parseclass.util.Bytes;
import com.yuna.jvm.parseclass.basic.IReadable;
import com.yuna.jvm.parseclass.basic.attr.BasicAttributeInfo;

public abstract class BasicInfo implements IReadable, IDisplayable {
    public int accessFlag;
    public int nameIndex;
    public int descriptorIndex;
    public int attributesCount;
    public BasicAttributeInfo[] attributes;

    public ConstantPool pool;

    @Override
    public void read(InputStream is) {
        accessFlag = Bytes.readU2(is);
        nameIndex = Bytes.readU2(is);
        descriptorIndex = Bytes.readU2(is);
        attributesCount = Bytes.readU2(is);
        if (attributesCount > 0) {
            //            System.out.println("BaseInfo.attributesCount:" + attributesCount);
            attributes = new BasicAttributeInfo[attributesCount];
            for (int i = 0; i < attributesCount; i++) {
                attributes[i] = BasicAttributeInfo.getInstance(pool, is);
            }
        }
    }

    public static BasicInfo getInstance(ConstantPool pool, InputStream is, Class<? extends BasicInfo> clazz) {
        try {
            BasicInfo info = clazz.newInstance();
            info.pool = pool;
            info.read(is);
            return info;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
