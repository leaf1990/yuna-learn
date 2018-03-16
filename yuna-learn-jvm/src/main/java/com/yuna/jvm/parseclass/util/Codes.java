package com.yuna.jvm.parseclass.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Codes {

    public static Map<Integer, InstBean> INST_MAP = new HashMap<>();

    public static InstBean getInst(short code) {
        return INST_MAP.get((int) code);
    }

    static {
        init();
    }

    private static void init() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File("ins.ini")));
            String line = null, first = null;
            int index = 1;
            while ((line = reader.readLine()) != null) {
                if (index++ % 2 == 1) {
                    first = line;
                    continue;
                }
                String[] strs = line.split(" ");
                InstBean bean = new InstBean();
                bean.code = Integer.parseInt(first.substring(2), 16);
                bean.inst = strs[0];
                if (strs.length > 1) {
                    List<String> params = new ArrayList<String>();
                    bean.params = params;
                    for (int i = 1; i < strs.length; i++) {
                        params.add(strs[i]);
                    }
                }
                INST_MAP.put(bean.code, bean);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //    public static void main(String[] args) throws Exception {
    //        //        System.out.println(Integer.parseInt("aa", 16));
    //        BufferedReader reader = new BufferedReader(new FileReader(new File("ins.ini")));
    //        String line = null;
    //        List<InstBean> instList = new ArrayList<InstBean>();
    //        int index = 1;
    //        String first = null;
    //        while ((line = reader.readLine()) != null) {
    //            System.out.println(line);
    //            if (index++ % 2 == 1) {
    //                first = line;
    //                continue;
    //            }
    //            String[] strs = line.split(" ");
    //            InstBean bean = new InstBean();
    //            bean.code = Integer.parseInt(first.substring(2), 16);
    //            bean.inst = strs[0];
    //            if (strs.length > 1) {
    //                List<String> params = new ArrayList<String>();
    //                bean.params = params;
    //                for (int i = 1; i < strs.length; i++) {
    //                    params.add(strs[i]);
    //                }
    //            }
    //            instList.add(bean);
    //        }
    //        System.out.println(instList);
    //    }
}
