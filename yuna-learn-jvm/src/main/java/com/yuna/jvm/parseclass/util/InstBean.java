package com.yuna.jvm.parseclass.util;

import java.util.List;

public class InstBean {
    public int code;
    public String inst;
    public List<String> params;

    @Override
    public String toString() {
        return "[code=" + code + ", inst=" + inst + ", params=" + params + "]";
    }
}
