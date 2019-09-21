package com.longse.lsapc.lsacore.mode;

/**
 * Created by lw on 2017/12/9.
 */

public interface HttpResult<T> {

    /**
     * 请求结果代码
     * 0 成功， 其他失败
     * @return
     */
    int resultCode();

    /**
     * 成功时获取请求结果
     * 失败时，可用于返回错误说明,利于调试
     * @return
     */
    T getResult();

}
