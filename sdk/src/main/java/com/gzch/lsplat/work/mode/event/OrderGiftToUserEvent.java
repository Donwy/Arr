package com.gzch.lsplat.work.mode.event;

/**
 * Created by LY on 2018/3/5.
 */

public class OrderGiftToUserEvent {
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

    /**
     * 显示vip标记1显示， 0隐藏
     */
    private String mUseCloud;


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

    public String getmUseCloud() {
        return mUseCloud;
    }

    public void setmUseCloud(String mUseCloud) {
        this.mUseCloud = mUseCloud;
    }

    public String getmTimeInfo() {
        return mTimeInfo;
    }

    public void setmTimeInfo(String mTimeInfo) {
        this.mTimeInfo = mTimeInfo;
    }

    public void setOrderGiftToUserEvent(String timeInfo, String useCloud) {
        this.mTimeInfo = timeInfo;
        this.mUseCloud = useCloud;
    }

    @Override
    public String toString() {
        return "OrderGiftToUserEvent{" +
                "resultCode=" + resultCode +
                ", errMsg='" + errMsg + '\'' +
                ", cmd=" + cmd +
                ", mTimeInfo='" + mTimeInfo + '\'' +
                ", mIsActive='" + mUseCloud + '\'' +
                '}';
    }
}
