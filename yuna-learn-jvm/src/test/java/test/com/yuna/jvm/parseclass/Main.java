package test.com.yuna.jvm.parseclass;

import java.io.File;
import java.io.FileInputStream;

import com.yuna.jvm.parseclass.basic.info.MethodInfo;
import com.yuna.jvm.parseclass.util.Bytes;
import com.yuna.jvm.parseclass.basic.ClassFile;
import com.yuna.jvm.parseclass.basic.ConstantPool;
import com.yuna.jvm.parseclass.basic.info.BasicInfo;
import com.yuna.jvm.parseclass.basic.info.FieldInfo;

public class Main {

    public static void main(String[] args) throws Exception {
        File file = new File("C:/Users/Administrator/Desktop/Main.class");
        FileInputStream is = new FileInputStream(file);

        ClassFile classFile = new ClassFile();
        classFile.logicData = Bytes.readU4(is);
        classFile.minorVersion = Bytes.readU2(is);
        classFile.majorVersion = Bytes.readU2(is);
        classFile.constantPool = ConstantPool.readConstantPool(is);// constant pool
        classFile.accessFlags = Bytes.readU2(is);// access flags

        classFile.className = classFile.constantPool.getClassUtf8(Bytes.readU2(is));// u2 class index
        classFile.superClassName = classFile.constantPool.getClassUtf8(Bytes.readU2(is));// u2 super class index
        classFile.interfaceCount = Bytes.readU2(is);// u2 interface count
        if (classFile.interfaceCount > 0) {// u2*count interfaces
            String[] interfaceNames = new String[classFile.interfaceCount];
            for (int i = 0; i < classFile.interfaceCount; i++) {
                interfaceNames[i] = classFile.constantPool.getClassUtf8(Bytes.readU2(is));
            }
            classFile.interfaceNames = interfaceNames;
        }
        classFile.fieldCount = Bytes.readU2(is);// u2 field count
        if (classFile.fieldCount > 0) {// fields
            FieldInfo[] fields = new FieldInfo[classFile.fieldCount];
            for (int i = 0; i < classFile.fieldCount; i++) {
                fields[i] = (FieldInfo) BasicInfo.getInstance(classFile.constantPool, is, FieldInfo.class);
            }
            classFile.fields = fields;
        }
        classFile.methodCount = Bytes.readU2(is);// u2 method count
        if (classFile.methodCount > 0) {// methods
            MethodInfo[] methods = new MethodInfo[classFile.methodCount];
            for (int i = 0; i < classFile.methodCount; i++) {
                methods[i] = (MethodInfo) BasicInfo.getInstance(classFile.constantPool, is, MethodInfo.class);
            }
            classFile.methods = methods;
        }
        classFile.display();
    }
}
