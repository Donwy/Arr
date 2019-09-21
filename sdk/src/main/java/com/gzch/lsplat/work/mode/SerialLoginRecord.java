package com.gzch.lsplat.work.mode;

/**
 * Created by lw on 2018/2/1.
 */

public class SerialLoginRecord {

    //设备账号
    private String account;

    //设备密码
    private String password;

    //设备序列号
    private String deviceId;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "SerialLoginRecord{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
