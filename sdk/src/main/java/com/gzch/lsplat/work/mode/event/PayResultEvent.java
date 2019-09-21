package com.gzch.lsplat.work.mode.event;

/**
 * Created by LY on 2018/4/3.
 */

public class PayResultEvent {
    /**
     * web请求 结果码 code值
     */
    private int resultCode;

    /**
     * 请求错误提示
     */
    private String errMsg;

    /**
     * 请求的命令
     */
    private int cmd;

    /**
     * 到期时间
     */
    private String mTimeInfo;

    private String mUseCloud;

    public String getmTimeInfo() {
        return mTimeInfo;
    }

    public void setmTimeInfo(String mTimeInfo) {
        this.mTimeInfo = mTimeInfo;
    }

    public String getmUseCloud() {
        return mUseCloud;
    }

    public void setmUseCloud(String mUseCloud) {
        this.mUseCloud = mUseCloud;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public int getCmd() {
        return cmd;
    }


    public void setCmd(int cmd) {
        this.cmd = cmd;
    }


    @Override
    public String toString() {
        return "PayResultEvent{" +
                "resultCode=" + resultCode +
                ", errMsg='" + errMsg + '\'' +
                ", cmd=" + cmd +
                ", mTimeInfo='" + mTimeInfo + '\'' +
                ", mUseCloud='" + mUseCloud + '\'' +
                '}';
    }
}
