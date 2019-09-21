package com.gzch.lsplat.work.mode.event;

import com.gzch.lsplat.work.mode.LocalDevice;

import java.util.List;

/**
 * Created by lw on 2018/1/31.
 */

public class SearchDeviceResultEvent {
    /**
     * web请求 结果码 code值
     */
    private int resultCode;

    /**
     * 请求的命令
     */
    private int cmd;

    private List<LocalDevice> localDevices;

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

    public List<LocalDevice> getLocalDevices() {
        return localDevices;
    }

    public void setLocalDevices(List<LocalDevice> localDevices) {
        this.localDevices = localDevices;
    }

    @Override
    public String toString() {
        return "SearchDeviceResultEvent{" +
                "resultCode=" + resultCode +
                ", cmd=" + cmd +
                ", localDevices=" + localDevices +
                '}';
    }
}
