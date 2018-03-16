package com.yuna.jvm.parseclass.basic;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.yuna.jvm.parseclass.util.Bytes;
import com.yuna.jvm.parseclass.constant.ConstantClass;
import com.yuna.jvm.parseclass.constant.ConstantFieldRef;
import com.yuna.jvm.parseclass.constant.BasicConstantInfo;
import com.yuna.jvm.parseclass.constant.ConstantInterfaceMethodRef;
import com.yuna.jvm.parseclass.constant.ConstantMethodRef;
import com.yuna.jvm.parseclass.constant.ConstantNameAndType;
import com.yuna.jvm.parseclass.constant.ConstantString;
import com.yuna.jvm.parseclass.constant.ConstantUtf8;

public class ConstantPool {
    public List<BasicConstantInfo> constantList;

    public static ConstantPool readConstantPool(InputStream is) {
        ConstantPool pool = new ConstantPool();

        int poolSize = Bytes.readU2(is);
        for (int i = 1; i < poolSize; i++) {
            pool.addPoolConstant(BasicConstantInfo.getInstance(Bytes.readU1(is), is));
        }

        return pool;
    }

    public void addPoolConstant(BasicConstantInfo info) {
        if (constantList == null) {
            constantList = new ArrayList<>();
            constantList.add(null);// 默认第一个留空
        }
        constantList.add(info);
    }

    /**
    Constant pool:
    #1 = Methodref          #11.#29        // java/lang/Object."<init>":()V
    #2 = Class              #30            // org/springframework/context/support/ClassPathXmlApplicationContext
    #3 = String             #31            // applicationContext.xml
    #4 = Methodref          #2.#32         // org/springframework/context/support/ClassPathXmlApplicationContext."<init>":(Ljava/lang/String;)V
    #5 = Fieldref           #33.#34        // java/lang/System.out:Ljava/io/PrintStream;
    #6 = Methodref          #35.#36        // java/io/PrintStream.println:(Ljava/lang/Object;)V
    #7 = Class              #37            // com/tgou/cassandra/dao/MessageDao
    #8 = InterfaceMethodref #38.#39        // org/springframework/context/ApplicationContext.getBean:(Ljava/lang/Class;)Ljava/lang/Object;
    #9 = Methodref          #7.#40         // com/tgou/cassandra/dao/MessageDao.test:()V
    #10 = Class              #41            // com/tgou/cassandra/cql/Main
    #11 = Class              #42            // java/lang/Object
    #12 = Utf8               <init>
    #13 = Utf8               ()V
    #14 = Utf8               Code
    #15 = Utf8               LineNumberTable
    #16 = Utf8               LocalVariableTable
    #17 = Utf8               this
    #18 = Utf8               Lcom/tgou/cassandra/cql/Main;
    #19 = Utf8               main
    #20 = Utf8               ([Ljava/lang/String;)V
    #21 = Utf8               args
    #22 = Utf8               [Ljava/lang/String;
    #23 = Utf8               content
    #24 = Utf8               Lorg/springframework/context/ApplicationContext;
    #25 = Utf8               dao
    #26 = Utf8               Lcom/tgou/cassandra/dao/MessageDao;
    #27 = Utf8               SourceFile
    #28 = Utf8               Main.java
    #29 = NameAndType        #12:#13        // "<init>":()V
    #30 = Utf8               org/springframework/context/support/ClassPathXmlApplicationContext
    #31 = Utf8               applicationContext.xml
    #32 = NameAndType        #12:#43        // "<init>":(Ljava/lang/String;)V
    #33 = Class              #44            // java/lang/System
    #34 = NameAndType        #45:#46        // out:Ljava/io/PrintStream;
    #35 = Class              #47            // java/io/PrintStream
    #36 = NameAndType        #48:#49        // println:(Ljava/lang/Object;)V
    #37 = Utf8               com/tgou/cassandra/dao/MessageDao
    #38 = Class              #50            // org/springframework/context/ApplicationContext
    #39 = NameAndType        #51:#52        // getBean:(Ljava/lang/Class;)Ljava/lang/Object;
    #40 = NameAndType        #53:#13        // test:()V
    #41 = Utf8               com/tgou/cassandra/cql/Main
    #42 = Utf8               java/lang/Object
    #43 = Utf8               (Ljava/lang/String;)V
    #44 = Utf8               java/lang/System
    #45 = Utf8               out
    #46 = Utf8               Ljava/io/PrintStream;
    #47 = Utf8               java/io/PrintStream
    #48 = Utf8               println
    #49 = Utf8               (Ljava/lang/Object;)V
    #50 = Utf8               org/springframework/context/ApplicationContext
    #51 = Utf8               getBean
    #52 = Utf8               (Ljava/lang/Class;)Ljava/lang/Object;
    #53 = Utf8               test
     */
    private final static String POOL_INDEX_FORMAT = "%-20s %-20s";

    public void display() {
        System.out.println("Constant pool:");
        for (int i = 1; i < constantList.size(); i++) {
            System.out.printf("%5s = ", "#" + i);
            BasicConstantInfo infoBean = constantList.get(i);
            int tag = infoBean.tag;
            if (tag == ConstantMethodRef.TYPE) {
                ConstantMethodRef info = (ConstantMethodRef) infoBean;
                System.out.printf(POOL_INDEX_FORMAT, info.NAME, "#" + info.classIndex + ".#" + info.nameAndTypeIndex);
                System.out.print("// " + getClassUtf8(info.classIndex) + "." + getNameAndTypeUtf8(info.nameAndTypeIndex));
            } // 
            else if (tag == ConstantClass.TYPE) {
                ConstantClass info = (ConstantClass) infoBean;
                System.out.printf(POOL_INDEX_FORMAT, info.NAME, "#" + info.nameIndex);
                System.out.print("// " + getUtf8(info.nameIndex));
            } // 
            else if (tag == ConstantFieldRef.TYPE) {
                ConstantFieldRef info = (ConstantFieldRef) infoBean;
                System.out.printf(POOL_INDEX_FORMAT, info.NAME, "#" + info.classIndex + ".#" + info.nameAndTypeIndex);
                System.out.print("// " + getClassUtf8(info.classIndex) + "." + getNameAndTypeUtf8(info.nameAndTypeIndex));
            } // 
            else if (tag == ConstantNameAndType.TYPE) {
                ConstantNameAndType info = (ConstantNameAndType) infoBean;
                System.out.printf(POOL_INDEX_FORMAT, info.NAME, "#" + info.nameIndex + ".#" + info.descIndex);
                System.out.print("// " + getNameAndTypeUtf8(info));
            } //
            else if (tag == ConstantString.TYPE) {
                ConstantString info = (ConstantString) infoBean;
                System.out.printf(POOL_INDEX_FORMAT, info.NAME, "#" + info.valueIndex);
                System.out.print("// " + getUtf8(info.valueIndex));
            } // 
            else if (tag == ConstantInterfaceMethodRef.TYPE) {
                ConstantInterfaceMethodRef info = (ConstantInterfaceMethodRef) infoBean;
                System.out.printf(POOL_INDEX_FORMAT, info.NAME, "#" + info.classIndex + ".#" + info.nameAndTypeIndex);
                System.out.print("// " + getClassUtf8(info.classIndex) + "." + getNameAndTypeUtf8(info.nameAndTypeIndex));
            } // 
            else if (tag == ConstantUtf8.TYPE) {
                ConstantUtf8 info = (ConstantUtf8) infoBean;
                System.out.printf(POOL_INDEX_FORMAT, info.NAME, info.value);
            }
            System.out.println();
        }
    }

    public String getNameAndTypeUtf8(int index) {
        ConstantNameAndType nameAndTypeInfo = (ConstantNameAndType) constantList.get(index);
        return getUtf8(nameAndTypeInfo.nameIndex) + ":" + getUtf8(nameAndTypeInfo.descIndex);
    }

    public String getNameAndTypeUtf8(ConstantNameAndType nameAndTypeInfo) {
        return getUtf8(nameAndTypeInfo.nameIndex) + ":" + getUtf8(nameAndTypeInfo.descIndex);
    }

    public String getClassUtf8(int index) {
        ConstantClass valueInfo = (ConstantClass) constantList.get(index);
        return getUtf8(valueInfo.nameIndex);
    }

    public String getUtf8(int index) {
        ConstantUtf8 valueInfo = (ConstantUtf8) constantList.get(index);
        return valueInfo.value;
    }
}
