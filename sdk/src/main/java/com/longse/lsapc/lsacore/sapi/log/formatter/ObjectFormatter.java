package com.longse.lsapc.lsacore.sapi.log.formatter;

/**
 * Created by lw on 2017/7/5.
 */

public class ObjectFormatter implements Formatter {
    @Override
    public String formatter(Object data) {
        if (data == null)return EMPTY;
        return data.getClass().getSimpleName() + " = " + data;
    }
}
