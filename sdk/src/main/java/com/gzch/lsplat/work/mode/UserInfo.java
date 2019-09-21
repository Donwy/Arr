package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lw on 2017/12/23.
 */

/**
 * 用户信息
 */
public class UserInfo {

    /**
     * 发送请求的命令
     */
    private int cmd;

    /**
     * 请求结果码
     */
    private int execResultCode;

    /**
     * 错误说明
     */
    private Object obj;

    private String phoneNumber;

    private String email;

    private String ifBind;

    private String realName;

    private String subName;

    private String userIconPath;

    private String userId;

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getExecResultCode() {
        return execResultCode;
    }

    public void setExecResultCode(int execResultCode) {
        this.execResultCode = execResultCode;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIfBind() {
        return ifBind;
    }

    public void setIfBind(String ifBind) {
        this.ifBind = ifBind;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getUserIconPath() {
        return userIconPath;
    }

    public void setUserIconPath(String userIconPath) {
        this.userIconPath = userIconPath;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static UserInfo parse(String json){
        if (TextUtils.isEmpty(json))return null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject != null){
                return parse(jsonObject);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static UserInfo parse(JSONObject jsonObject){
        UserInfo info = new UserInfo();
        if (jsonObject != null) {
            info.setEmail(jsonObject.optString("email_addr"));
            info.setIfBind(jsonObject.optString("email_check"));
            info.setPhoneNumber(jsonObject
                    .optString("phone_number"));
            info.setRealName(jsonObject.optString("real_name"));
            info.setSubName(jsonObject.optString("nickname"));
            info.setUserIconPath(jsonObject.optString("user_img"));
            info.setUserId(jsonObject.optString("user_id"));
        }

        return info;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "cmd=" + cmd +
                ", execResultCode=" + execResultCode +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", ifBind='" + ifBind + '\'' +
                ", realName='" + realName + '\'' +
                ", subName='" + subName + '\'' +
                ", userIconPath='" + userIconPath + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
