package com.gzch.lsplat.work.mode;

/**
 * Created by lw on 2018/9/6.
 */

public class LoginAccountInfo {

    private String account = "";
    private String password = "";
    private String iconUrl = "";
    private String userName = "";
    private String userId = "";

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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "LoginAccountInfo{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
