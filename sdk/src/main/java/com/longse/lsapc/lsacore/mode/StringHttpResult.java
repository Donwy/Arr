package com.longse.lsapc.lsacore.mode;

import com.longse.lsapc.lsacore.interf.ErrorCode;

/**
 * Created by lw on 2017/12/9.
 */

public class StringHttpResult implements HttpResult<String> {

    /**
     * 错误码
     */
    private int resultCode = ErrorCode.RESPONSE_ERROR;

    /**
     * 返回结果
     */
    private String response = "";

    public void setResultCode(int resultCode){
        this.resultCode = resultCode;
    }

    public void setResponse(String response){
        this.response = response;
    }

    @Override
    public int resultCode() {
        return resultCode;
    }

    @Override
    public String getResult() {
        return response;
    }

    @Override
    public String toString() {
        return "StringHttpResult{" +
                "resultCode=" + resultCode +
                ", response='" + response + '\'' +
                '}';
    }
}
