package com.yuna.redis.util;

import java.util.List;
import java.util.Map;

/**
 * Created by yeyayun on 2018/3/16 0016.
 */
public final class ConvertUtil {
    /**
     * Map -> Key + value Array
     *
     * @param map
     * @return
     */
    public static String[] mapToArray(Map<String, String> map) {
        String[] array = new String[map.size() << 2];
        int index = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            array[index++] = entry.getKey();
            array[index++] = entry.getValue();
        }
        return array;
    }

    /**
     * List -> Array
     *
     * @param list
     * @return
     */
    public static String[] listToArray(List<String> list) {
        return list.toArray(new String[list.size()]);
    }
}
