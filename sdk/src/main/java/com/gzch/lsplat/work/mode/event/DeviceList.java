package com.gzch.lsplat.work.mode.event;

import com.gzch.lsplat.work.mode.EqupInfo;

import java.util.List;

/**
 * Created by lw on 2017/12/26.
 */

public class DeviceList {

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

    private List<EqupInfo> devices;

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

    public List<EqupInfo> getDevices() {
        return devices;
    }

    public void setDevices(List<EqupInfo> devices) {
        this.devices = devices;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return "DeviceList{" +
                "resultCode=" + resultCode +
                ", errMsg='" + errMsg + '\'' +
                ", devices=" + devices +
                '}';
    }
}
