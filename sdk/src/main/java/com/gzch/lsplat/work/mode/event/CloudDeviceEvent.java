package com.gzch.lsplat.work.mode.event;

import com.gzch.lsplat.work.mode.CloudDeviceBean;

import java.util.List;

/**
 * Created by LY on 2018/3/1.
 */

public class CloudDeviceEvent {
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

    private List<CloudDeviceBean> cloudDeviceBeans;

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

    public List<CloudDeviceBean> getCloudDeviceBeans() {
        return cloudDeviceBeans;
    }

    public void setCloudDevcieBeans(List<CloudDeviceBean> cloudDeviceBeans) {
        this.cloudDeviceBeans = cloudDeviceBeans;
    }

    @Override
    public String toString() {
        return "CloudDeviceEvent{" +
                "resultCode=" + resultCode +
                ", errMsg='" + errMsg + '\'' +
                ", cmd=" + cmd +
                ", cloudDeviceBeans=" + cloudDeviceBeans +
                '}';
    }
}
