package com.gzch.lsplat.work.mode.event;

import com.gzch.lsplat.work.mode.DemoInfo;

import java.util.List;

/**
 * Created by lw on 2018/1/8.
 */

public class DemoDeviceList {

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

    private List<DemoInfo> demoInfos;

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

    public List<DemoInfo> getDemoInfos() {
        return demoInfos;
    }

    public void setDemoInfos(List<DemoInfo> demoInfos) {
        this.demoInfos = demoInfos;
    }

    @Override
    public String toString() {
        return "DemoDeviceList{" +
                "resultCode=" + resultCode +
                ", errMsg='" + errMsg + '\'' +
                ", cmd=" + cmd +
                ", demoInfos=" + demoInfos +
                '}';
    }
}
