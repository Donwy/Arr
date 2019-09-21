package com.gzch.lsplat.work.mode.event;

import com.gzch.lsplat.work.mode.SerialLoginRecord;

import java.util.List;

/**
 * Created by lw on 2018/2/1.
 */

public class SerialLoginRecordEvent {
    /**
     * web请求 结果码 code值
     */
    private int resultCode;

    /**
     * 请求的命令
     */
    private int cmd;

    private List<SerialLoginRecord> records;

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

    public List<SerialLoginRecord> getRecords() {
        return records;
    }

    public void setRecords(List<SerialLoginRecord> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "SerialLoginRecordEvent{" +
                "resultCode=" + resultCode +
                ", cmd=" + cmd +
                ", records=" + records +
                '}';
    }
}
