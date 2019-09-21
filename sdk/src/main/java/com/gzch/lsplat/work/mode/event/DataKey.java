package com.gzch.lsplat.work.mode.event;

/**
 * Created by lw on 2017/12/26.
 */

public class DataKey {

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
     * 数据存放在HashMap中的Key值
     */
    private String KEY;

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

    public String getKEY() {
        return KEY;
    }

    public void setKEY(String KEY) {
        this.KEY = KEY;
    }

    @Override
    public String toString() {
        return "DataKey{" +
                "resultCode=" + resultCode +
                ", errMsg='" + errMsg + '\'' +
                ", cmd=" + cmd +
                ", KEY='" + KEY + '\'' +
                '}';
    }
}
