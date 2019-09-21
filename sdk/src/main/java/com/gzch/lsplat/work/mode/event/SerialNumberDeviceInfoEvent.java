package com.gzch.lsplat.work.mode.event;

import com.gzch.lsplat.work.mode.EqupInfo;

/**
 * Created by lw on 2018/1/6.
 */

public class SerialNumberDeviceInfoEvent {

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

    private EqupInfo deviceInfo;

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

    public EqupInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(EqupInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    @Override
    public String toString() {
        return "SerialNumberDeviceInfoEvent{" +
                "resultCode=" + resultCode +
                ", errMsg='" + errMsg + '\'' +
                ", cmd=" + cmd +
                ", deviceInfo=" + deviceInfo +
                '}';
    }
}
