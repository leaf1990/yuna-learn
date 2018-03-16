package com.yuna.jvm.parseclass.util;

/**
 * 类，字段，方法访问标志
 */
public class AccessFlag {
    // class access flag
    public static final int ACC_PUBLIC = 0x0001;
    public static final int ACC_FINAL = 0x0010;
    public static final int ACC_SUPER = 0x0020;
    public static final int ACC_INTERFACE = 0x0200;
    public static final int ACC_ABSTRACT = 0x0400;
    public static final int ACC_SYNTHETIC = 0x1000;
    public static final int ACC_ANNOTATION = 0x2000;
    public static final int ACC_ENUM = 0x4000;

    //	field access flag
    //	public static final int ACC_PUBLIC = 0x0001;
    //	public static final int ACC_PRIVATE = 0x0002;
    //	public static final int ACC_PROTECTED = 0x0004;
    //	public static final int ACC_STATIC = 0x0008;
    //	public static final int ACC_FINAL = 0x0010;
    public static final int ACC_VOLATILE = 0x0040;
    public static final int ACC_TRANSIENT = 0x0080;
    //	public static final int ACC_SYNTHETIC = 0x1000;
    //	public static final int ACC_ENUM = 0x4000;

    //	method access flag
    //	public static final int ACC_PUBLIC = 0x0001;
    public static final int ACC_PRIVATE = 0x0002;
    public static final int ACC_PROTECTED = 0x0004;
    public static final int ACC_STATIC = 0x0008;
    //	public static final int ACC_FINAL = 0x0010;
    public static final int ACC_SYNCHRONIZED = 0x0020;
    public static final int ACC_BRIDGE = 0x0040;
    public static final int ACC_VARARGS = 0x0080;
    public static final int ACC_NATIVE = 0x0100;
    //	public static final int ACC_ABSTRACT = 0x0400;
    public static final int ACC_STRICTFP = 0x0800;
    //	public static final int ACC_SYNTHETIC = 0x1000;

    // inner class access flag
    //    public static final int ACC_PUBLIC = 0x0001;
    //    public static final int ACC_PRIVATE = 0x0002;
    //    public static final int ACC_PROTECTED = 0x0004;
    //    public static final int ACC_STATIC = 0x0008;
    //    public static final int ACC_FINAL = 0x0010;
    public static final int ACC_INNER_CLASS_INTERFACE = 0x0020;
    //    public static final int ACC_ABSTRACT = 0x0400;
    //    public static final int ACC_SYNTHETIC = 0x1000;
    //    public static final int ACC_ANNOTATION = 0x2000;
    //    public static final int ACC_ENUM = 0x4000;

    public static String innerClassAccess(int flag) {
        StringBuffer buffer = new StringBuffer();
        if ((flag & ACC_PUBLIC) != 0) {
            buffer.append("ACC_PUBLIC ");
        }
        if ((flag & ACC_PRIVATE) != 0) {
            buffer.append("ACC_PRIVATE ");
        }
        if ((flag & ACC_PROTECTED) != 0) {
            buffer.append("ACC_PROTECTED ");
        }
        if ((flag & ACC_STATIC) != 0) {
            buffer.append("ACC_STATIC ");
        }
        if ((flag & ACC_FINAL) != 0) {
            buffer.append("ACC_FINAL ");
        }
        if ((flag & ACC_INNER_CLASS_INTERFACE) != 0) {
            buffer.append("ACC_INNER_CLASS_INTERFACE ");
        }
        if ((flag & ACC_ABSTRACT) != 0) {
            buffer.append("ACC_ABSTRACT ");
        }
        if ((flag & ACC_SYNTHETIC) != 0) {
            buffer.append("ACC_SYNTHETIC ");
        }
        if ((flag & ACC_ANNOTATION) != 0) {
            buffer.append("ACC_ANNOTATION ");
        }
        if ((flag & ACC_ENUM) != 0) {
            buffer.append("ACC_ENUM ");
        }
        return buffer.toString();
    }

    public static String methodAccess(int flag) {
        StringBuffer buffer = new StringBuffer();
        if ((flag & ACC_PUBLIC) != 0) {
            buffer.append("ACC_PUBLIC ");
        }
        if ((flag & ACC_PRIVATE) != 0) {
            buffer.append("ACC_PRIVATE ");
        }
        if ((flag & ACC_PROTECTED) != 0) {
            buffer.append("ACC_PROTECTED ");
        }
        if ((flag & ACC_STATIC) != 0) {
            buffer.append("ACC_STATIC ");
        }
        if ((flag & ACC_FINAL) != 0) {
            buffer.append("ACC_FINAL ");
        }
        if ((flag & ACC_SYNCHRONIZED) != 0) {
            buffer.append("ACC_SYNCHRONIZED ");
        }
        if ((flag & ACC_BRIDGE) != 0) {
            buffer.append("ACC_BRIDGE ");
        }
        if ((flag & ACC_VARARGS) != 0) {
            buffer.append("ACC_VARARGS ");
        }
        if ((flag & ACC_NATIVE) != 0) {
            buffer.append("ACC_NATIVE ");
        }
        if ((flag & ACC_ABSTRACT) != 0) {
            buffer.append("ACC_ABSTRACT ");
        }
        if ((flag & ACC_STRICTFP) != 0) {
            buffer.append("ACC_STRICTFP ");
        }
        if ((flag & ACC_SYNTHETIC) != 0) {
            buffer.append("ACC_SYNTHETIC ");
        }
        return buffer.toString();
    }

    public static String fieldAccess(int flag) {
        StringBuffer buffer = new StringBuffer();
        if ((flag & ACC_PUBLIC) != 0) {
            buffer.append("ACC_PUBLIC ");
        }
        if ((flag & ACC_PRIVATE) != 0) {
            buffer.append("ACC_PRIVATE ");
        }
        if ((flag & ACC_PROTECTED) != 0) {
            buffer.append("ACC_PROTECTED ");
        }
        if ((flag & ACC_STATIC) != 0) {
            buffer.append("ACC_STATIC ");
        }
        if ((flag & ACC_FINAL) != 0) {
            buffer.append("ACC_FINAL ");
        }
        if ((flag & ACC_VOLATILE) != 0) {
            buffer.append("ACC_VOLATILE ");
        }
        if ((flag & ACC_TRANSIENT) != 0) {
            buffer.append("ACC_TRANSIENT ");
        }
        if ((flag & ACC_SYNTHETIC) != 0) {
            buffer.append("ACC_SYNTHETIC ");
        }
        if ((flag & ACC_ENUM) != 0) {
            buffer.append("ACC_ENUM ");
        }
        return buffer.toString();
    }

    public static String classAccess(int flag) {
        StringBuffer buffer = new StringBuffer();
        if ((flag & ACC_PUBLIC) != 0) {
            buffer.append("ACC_PUBLIC ");
        }
        if ((flag & ACC_FINAL) != 0) {
            buffer.append("ACC_FINAL ");
        }
        if ((flag & ACC_SUPER) != 0) {
            buffer.append("ACC_SUPER ");
        }
        if ((flag & ACC_INTERFACE) != 0) {
            buffer.append("ACC_INTERFACE ");
        }
        if ((flag & ACC_ABSTRACT) != 0) {
            buffer.append("ACC_ABSTRACT ");
        }
        if ((flag & ACC_SYNTHETIC) != 0) {
            buffer.append("ACC_SYNTHETIC ");
        }
        if ((flag & ACC_ANNOTATION) != 0) {
            buffer.append("ACC_ANNOTATION ");
        }
        if ((flag & ACC_ENUM) != 0) {
            buffer.append("ACC_ENUM ");
        }
        return buffer.toString();
    }
}
