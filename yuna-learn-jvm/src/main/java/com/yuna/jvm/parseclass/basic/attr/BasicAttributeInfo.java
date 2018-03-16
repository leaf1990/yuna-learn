package com.yuna.jvm.parseclass.basic.attr;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.yuna.jvm.parseclass.basic.ConstantPool;
import com.yuna.jvm.parseclass.basic.IDisplayable;
import com.yuna.jvm.parseclass.basic.IReadable;
import com.yuna.jvm.parseclass.util.Bytes;

/**
 * u2   attribute_name_index
 * u4   attribute_length
 * u1   info                    array[attribute_length]
 */
public abstract class BasicAttributeInfo implements IReadable, IDisplayable {
    public int attributeNameIndex;
    public long attributeLength;
    public short[] info;

    public String type;// 标识类型
    public ConstantPool pool;// 常量池

    public static Map<String, Class<? extends BasicAttributeInfo>> ATTR_INFO_MAP = new HashMap<>();
    static {
        ATTR_INFO_MAP.put(CodeAttrInfo.TYPE, CodeAttrInfo.class);
        ATTR_INFO_MAP.put(ExceptionsAttrInfo.TYPE, ExceptionsAttrInfo.class);
        ATTR_INFO_MAP.put(LineNumberTableAttrInfo.TYPE, LineNumberTableAttrInfo.class);
        ATTR_INFO_MAP.put(LocalVariableTableAttrInfo.TYPE, LocalVariableTableAttrInfo.class);
        ATTR_INFO_MAP.put(SourceFileAttrInfo.TYPE, SourceFileAttrInfo.class);
        ATTR_INFO_MAP.put(ConstantValueAttrInfo.TYPE, ConstantValueAttrInfo.class);
        ATTR_INFO_MAP.put(InnerClassesAttrInfo.TYPE, InnerClassesAttrInfo.class);

        ATTR_INFO_MAP.put("Deprecated", AttributeInfo.class);
        ATTR_INFO_MAP.put("Synthetic", AttributeInfo.class);

        ATTR_INFO_MAP.put(SignatureAttrInfo.TYPE, SignatureAttrInfo.class);
    }

    public static BasicAttributeInfo getInstance(ConstantPool pool, InputStream is) {
        int nameIndex = Bytes.readU2(is);
        String type = pool.getUtf8(nameIndex);
        //        System.out.println(type);
        try {
            BasicAttributeInfo info = ATTR_INFO_MAP.get(type).newInstance();
            info.type = type;
            info.pool = pool;
            info.attributeNameIndex = nameIndex;
            info.attributeLength = Bytes.readU4(is);
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
