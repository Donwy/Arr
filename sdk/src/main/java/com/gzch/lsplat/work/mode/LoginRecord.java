package com.gzch.lsplat.work.mode;

/**
 * Created by lw on 2018/1/4.
 */

public class LoginRecord {

    private String account = "";
    private String password = "";
    private String token = "";

    /**
     * Twitter 需要两个参数 token 为 accessToken ；token2 为 tokenSecret
     * 其他登录不需要token2
     */
    private String token2 = "";
    private String userId = "";//第三方登录使用去重，账号密码登录可为""
    private String time = "";//时间戳

    private String loginType;
    /**
     * loginType只能是下面这些值
     */
    public static final String ACCOUNT = "account_login";//账号登录
    public static final String WECHAT = "wechat_login";//第三方
    public static final String FACEBOOK = "facebook_login";//第三方
    public static final String TWITTER = "twitter_login";//第三方
    public static final String GOOGLE = "google_login";//第三方
    public static final String LINE = "Line_login";//第三方

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken2() {
        return token2;
    }

    public void setToken2(String token2) {
        this.token2 = token2;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    @Override
    public String toString() {
        return "LoginRecord{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", token2='" + token2 + '\'' +
                ", userId='" + userId + '\'' +
                ", time='" + time + '\'' +
                ", loginType='" + loginType + '\'' +
                '}';
    }
}
