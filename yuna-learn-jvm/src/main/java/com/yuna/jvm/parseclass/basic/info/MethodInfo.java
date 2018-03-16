package com.yuna.jvm.parseclass.basic.info;

import com.yuna.jvm.parseclass.basic.attr.BasicAttributeInfo;

/**
 *  u2	access_flags
 *  u2	name_index
 *  u2	descriptor_index
 *  u2	attributes_count
 *  attribute_info	attributes
 */
public class MethodInfo extends BasicInfo {
    @Override
    public void display() {
        System.out.println(pool.getUtf8(nameIndex));
        System.out.println(pool.getUtf8(descriptorIndex));
        for (BasicAttributeInfo info : attributes) {
            System.out.println(info.type);
        }
    }
}
