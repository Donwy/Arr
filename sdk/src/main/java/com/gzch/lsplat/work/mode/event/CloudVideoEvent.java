package com.gzch.lsplat.work.mode.event;

import com.gzch.lsplat.work.mode.CloudVideoBean;

import java.util.List;

/**
 * Created by LY on 2018/3/1.
 */

public class CloudVideoEvent {
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

    private List<CloudVideoBean> cloudVideoBeans;

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

    public List<CloudVideoBean> getCloudVideoBeans() {
        return cloudVideoBeans;
    }

    public void setCloudVideoBeans(List<CloudVideoBean> cloudVideoBeans) {
        this.cloudVideoBeans = cloudVideoBeans;
    }

    @Override
    public String toString() {
        return "CloudVideoEvent{" +
                "resultCode=" + resultCode +
                ", errMsg='" + errMsg + '\'' +
                ", cmd=" + cmd +
                ", cloudVideoBeans=" + cloudVideoBeans +
                '}';
    }
}
