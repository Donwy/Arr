package com.gzch.lsplat.work.mode.event;

import java.util.List;

/**
 * Created by lw on 2018/1/31.
 */

public class WifiSettingResultEvent {
    /**
     * web请求 结果码 code值
     */
    private int resultCode;

    /**
     * 请求的命令
     */
    private int cmd;

    /**
     * WiFi配网后 得到的设备序列号列表
     */
    private List<String> deviceIds;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    @Override
    public String toString() {
        return "WifiSettingResultEvent{" +
                "resultCode=" + resultCode +
                ", cmd=" + cmd +
                ", deviceIds=" + deviceIds +
                '}';
    }
}
