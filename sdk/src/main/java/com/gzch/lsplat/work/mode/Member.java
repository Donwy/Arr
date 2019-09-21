package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lw on 2018/1/5.
 */

public class Member {
    /**
     * BindDeviceUserInfoEntity：[
     user_id : int           用户ID
     email_addr : string     用户邮箱
     real_name : string      用户真实姓名
     nickname : string       用户昵称
     user_name : string      用户登录名
     user_img : string       用户头像
     dr_id : string          绑定关系ID
     ]
     */
    public String userId;
    public String emailAddr;
    public String realName;
    public String nickName;
    public String userName;
    public String userImg;
    public String drId;

    public String getDrId() {
        return drId;
    }

    public void setDrId(String drId) {
        this.drId = drId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailAddr() {
        return emailAddr;
    }

    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public static Member parse(String str) {
        if (TextUtils.isEmpty(str)) return null;
        try {
            JSONObject json = new JSONObject(str);
            if (json != null) {
                return parse(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Member parse(JSONObject json) {
        if (json == null) return null;
        /**
         * BindDeviceUserInfoEntity：[
         user_id : int           用户ID
         email_addr : string     用户邮箱
         real_name : string      用户真实姓名
         nickname : string       用户昵称
         user_name : string      用户登录名
         user_img : string       用户头像
         dr_id : string          绑定关系ID
         ]
         */
        Member item = new Member();
        item.setUserId(json.optString("user_id"));
        item.setEmailAddr(json.optString("email_addr"));
        item.setRealName(json.optString("real_name"));
        item.setNickName(json.optString("nickname"));
        item.setUserName(json.optString("user_name"));
        item.setUserImg(json.optString("user_img"));
        item.setDrId(json.optString("dr_id"));
        return item;
    }

    @Override
    public String toString() {
        return "MemberBean{" +
                "userId='" + userId + '\'' +
                ", emailAddr='" + emailAddr + '\'' +
                ", realName='" + realName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", userName='" + userName + '\'' +
                ", userImg='" + userImg + '\'' +
                ", drId='" + drId + '\'' +
                '}';
    }
}
