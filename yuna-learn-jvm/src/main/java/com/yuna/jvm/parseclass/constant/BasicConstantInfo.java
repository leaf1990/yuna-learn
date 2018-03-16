package com.yuna.jvm.parseclass.constant;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.yuna.jvm.parseclass.basic.IReadable;

public abstract class BasicConstantInfo implements IReadable {
    public static Map<Integer, Class<? extends BasicConstantInfo>> CONST_INFO_MAP = new HashMap<>();

    public short tag;

    static {
        CONST_INFO_MAP.put(ConstantClass.TYPE, ConstantClass.class);
        CONST_INFO_MAP.put(ConstantDouble.TYPE, ConstantDouble.class);
        CONST_INFO_MAP.put(ConstantFieldRef.TYPE, ConstantFieldRef.class);
        CONST_INFO_MAP.put(ConstantFloat.TYPE, ConstantFloat.class);
        CONST_INFO_MAP.put(ConstantInteger.TYPE, ConstantInteger.class);
        CONST_INFO_MAP.put(ConstantInterfaceMethodRef.TYPE, ConstantInterfaceMethodRef.class);
        CONST_INFO_MAP.put(ConstantInvokeDynamic.TYPE, ConstantInvokeDynamic.class);
        CONST_INFO_MAP.put(ConstantLong.TYPE, ConstantLong.class);
        CONST_INFO_MAP.put(ConstantMethodHandle.TYPE, ConstantMethodHandle.class);
        CONST_INFO_MAP.put(ConstantMethodRef.TYPE, ConstantMethodRef.class);
        CONST_INFO_MAP.put(ConstantMethodType.TYPE, ConstantMethodType.class);
        CONST_INFO_MAP.put(ConstantNameAndType.TYPE, ConstantNameAndType.class);
        CONST_INFO_MAP.put(ConstantString.TYPE, ConstantString.class);
        CONST_INFO_MAP.put(ConstantUtf8.TYPE, ConstantUtf8.class);
    }

    public static BasicConstantInfo getInstance(short tag, InputStream is) {
        try {
            BasicConstantInfo info = CONST_INFO_MAP.get((int) tag).newInstance();
            info.read(is);
            info.tag = tag;
            return info;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
