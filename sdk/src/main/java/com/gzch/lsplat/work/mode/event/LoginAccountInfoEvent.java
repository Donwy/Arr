package com.gzch.lsplat.work.mode.event;

import com.gzch.lsplat.work.mode.LoginAccountInfo;

import java.util.List;

/**
 * Created by lw on 2018/9/6.
 */

public class LoginAccountInfoEvent {

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

    private List<LoginAccountInfo> loginAccountInfos;

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

    public List<LoginAccountInfo> getLoginAccountInfos() {
        return loginAccountInfos;
    }

    public void setLoginAccountInfos(List<LoginAccountInfo> loginAccountInfos) {
        this.loginAccountInfos = loginAccountInfos;
    }

    @Override
    public String toString() {
        return "LoginAccountInfoEvent{" +
                "resultCode=" + resultCode +
                ", errMsg='" + errMsg + '\'' +
                ", cmd=" + cmd +
                ", loginAccountInfos=" + loginAccountInfos +
                '}';
    }
}
