package com.longse.lsapc.lsacore.sapi.log.formatter;

/**
 * Created by lw on 2017/7/6.
 */

public class ThreadFormatter implements Formatter<Thread> {

    @Override
    public String formatter(Thread data) {
        if (data == null)return EMPTY;
        return "Thread_name = " + data.getName();
    }
}
