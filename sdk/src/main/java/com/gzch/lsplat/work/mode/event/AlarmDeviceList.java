package com.gzch.lsplat.work.mode.event;

import com.gzch.lsplat.work.mode.AlarmDeviceInfo;

import java.util.List;

/**
 * Created by LY on 2018/1/31.
 */

public class AlarmDeviceList {

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

    private List<AlarmDeviceInfo> alarmDeviceLists;

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

    public List<AlarmDeviceInfo> getAlarmDeviceLists() {
        return alarmDeviceLists;
    }

    public void setAlarmDeviceLists(List<AlarmDeviceInfo> alarmDeviceLists) {
        this.alarmDeviceLists = alarmDeviceLists;
    }

    @Override
    public String toString() {
        return "AlarmDeviceList{" +
                "resultCode=" + resultCode +
                ", errMsg='" + errMsg + '\'' +
                ", cmd=" + cmd +
                ", alarmDeviceLists=" + alarmDeviceLists +
                '}';
    }
}
