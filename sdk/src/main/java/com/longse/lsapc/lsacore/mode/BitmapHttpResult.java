package com.longse.lsapc.lsacore.mode;

import android.graphics.Bitmap;

import com.longse.lsapc.lsacore.interf.ErrorCode;

/**
 * Created by lw on 2017/12/28.
 */

public class BitmapHttpResult implements HttpResult<Bitmap> {

    /**
     * 错误码
     */
    private int resultCode = ErrorCode.RESPONSE_ERROR;

    /**
     * 返回结果
     */
    private Bitmap response = null;

    public BitmapHttpResult setResultCode(int resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    public void setResponse(Bitmap response) {
        this.response = response;
    }

    @Override
    public int resultCode() {
        return resultCode;
    }

    @Override
    public Bitmap getResult() {
        return response;
    }
}
