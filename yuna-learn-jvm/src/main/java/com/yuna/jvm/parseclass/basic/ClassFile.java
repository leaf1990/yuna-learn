package com.yuna.jvm.parseclass.basic;

import com.yuna.jvm.parseclass.basic.info.MethodInfo;
import com.yuna.jvm.parseclass.util.AccessFlag;
import com.yuna.jvm.parseclass.basic.info.FieldInfo;

public class ClassFile {
    public long logicData;
    public int minorVersion;
    public int majorVersion;
    public int accessFlags;
    public ConstantPool constantPool;

    public String className;
    public String superClassName;
    public int interfaceCount;
    public String[] interfaceNames;

    public int fieldCount;
    public int methodCount;
    public FieldInfo[] fields;
    public MethodInfo[] methods;

    public void display() {
        System.out.println(" magic data: " + logicData);
        System.out.println(" minor version: " + minorVersion);
        System.out.println(" major version: " + majorVersion);

        System.out.println(" flags: " + AccessFlag.classAccess(accessFlags));
        constantPool.display();
        for (MethodInfo info : methods) {
            info.display();
        }
    }
}
