package com.longse.lsapc.lsacore.sapi.log.formatter;

/**
 * Created by lw on 2017/7/5.
 */

/**
 * 格式化显示
 * @param <T>
 */
public interface Formatter<T> {
    String EMPTY = " * null";
    String formatter(T data);
}
