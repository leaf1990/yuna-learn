package com.yuna.jvm.parseclass.basic.attr;

import java.io.InputStream;

import com.yuna.jvm.parseclass.util.Bytes;
import com.yuna.jvm.parseclass.util.Codes;
import com.yuna.jvm.parseclass.util.InstBean;

/**
 *  u2  attribute_name_index
 *  u4  attribute_length
 *  u2  max_stack
 *  u2  max_locals
 *  u4  code_length
 *  u1  code                    array[code_length] 
 *  u2  exception_table_length
 *  exception_info
 *  u2  attributes_count
 *  attribute_info
 */
public class CodeAttrInfo extends BasicAttributeInfo {
    public static final String TYPE = "Code";
    public int maxStack;
    public int maxLocals;
    public long codeLength;
    public short[] code;
    public int exceptionTableLength;
    public ExceptionInfo[] exceptionTable;
    public int attributesCount;
    public BasicAttributeInfo[] attributes;

    @Override
    public void display() {
        System.out.println("    Code:");
        System.out.println("      statck=" + maxStack + ", locals=" + maxLocals + ", args_size=");
        int len = 0, codeLen = code.length;
        while (len < codeLen) {
            short scode = code[len++];
            InstBean instBean = Codes.getInst(scode);
            System.out.printf("    %4d: %-20s", len - 1, instBean.inst);
            if (instBean.params != null) {
                for (int i = 0; i < instBean.params.size(); i++) {
                    len++;
                }
            }
            System.out.println();
        }
    }

    @Override
    public void read(InputStream is) {
        maxStack = Bytes.readU2(is);
        maxLocals = Bytes.readU2(is);
        codeLength = Bytes.readU4(is);
        //        System.out.println("CodeAttributeInfo.maxStack:" + maxStack);
        //        System.out.println("CodeAttributeInfo.maxLocals:" + maxLocals);
        //        System.out.println("CodeAttributeInfo.codeLength:" + (int) codeLength);
        if (codeLength > 0) {
            code = new short[(int) codeLength];
            for (int i = 0; i < (int) codeLength; i++) {
                code[i] = Bytes.readU1(is);
                //                System.out.println("CodeAttributeInfo.code[" + i + "]:" + Integer.toHexString(code[i]));
            }
            display();
        }
        exceptionTableLength = Bytes.readU2(is);
        //        System.out.println("CodeAttributeInfo.exceptionTableLength:" + exceptionTableLength);
        if (exceptionTableLength > 0) {
            exceptionTable = new ExceptionInfo[exceptionTableLength];
            for (int i = 0; i < exceptionTableLength; i++) {
                ExceptionInfo info = new ExceptionInfo();
                info.read(is);
                exceptionTable[i] = info;
            }
        }
        attributesCount = Bytes.readU2(is);
        //        System.out.println("CodeAttributeInfo.attributesCount:" + attributesCount);
        if (attributesCount > 0) {
            attributes = new BasicAttributeInfo[attributesCount];
            for (int i = 0; i < attributesCount; i++) {
                attributes[i] = getInstance(pool, is);
            }
        }
    }
}
