package test.com.yuna.jvm.parseclass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ParseJavaClassFile {
    public static void main(String[] args) throws Exception {
        File classFile = new File("C:/Users/Administrator/Desktop/Main.class");
        FileInputStream inputStream = new FileInputStream(classFile);
        System.out.println("magic data: " + readU4(inputStream));
        System.out.println("minor version: " + readU2(inputStream));
        System.out.println("major version: " + readU2(inputStream));
        int poolSize = readU2(inputStream);
        System.out.println("pool size:" + poolSize);
        for (int i = 1; i < poolSize; i++) {
            System.out.println("pool type:" + readU1(inputStream));
        }
    }

    /**
     * 读取一个字节
     *
     * @return
     */
    public static short readU1(InputStream inputStream) {
        byte[] bytes = new byte[1];
        try {
            inputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        short value = (short) (bytes[0] & 0xf0);
        return value;
    }

    /**
     * 读取二个字节
     *
     * @return
     */
    public static int readU2(InputStream inputStream) {
        byte[] bytes = new byte[2];
        try {
            inputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int num = 0;
        for (int i = 0; i < bytes.length; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }

    /**
     * 读取四个字节
     *
     * @return
     */
    public static long readU4(InputStream inputStream) {
        byte[] bytes = new byte[4];
        try {
            inputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long num = 0;
        for (int i = 0; i < bytes.length; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }
}
