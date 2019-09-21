package com.gzch.lsplat.work.mode.event;

import com.gzch.lsplat.work.ErrorCode;
import com.gzch.lsplat.work.mode.LoginRecord;

import java.util.List;

/**
 * Created by lw on 2018/1/4.
 */

public class LoginHistoryEvent {

    private int execResult = ErrorCode.SUCCESS;

    private int cmd;

    private List<LoginRecord> loginRecordList;

    public int getExecResult() {
        return execResult;
    }

    public void setExecResult(int execResult) {
        this.execResult = execResult;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public List<LoginRecord> getLoginRecordList() {
        return loginRecordList;
    }

    public void setLoginRecordList(List<LoginRecord> loginRecordList) {
        this.loginRecordList = loginRecordList;
    }
}
