package com.gzch.lsplat.work.mode.event;

import android.text.TextUtils;

import com.gzch.lsplat.work.mode.Member;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * {"code":0,"msg":"\u6210\u529f","data":{"guessList":[],"channelInfo":{"channels_were_bought":["1"]},"firmware":{"version":"0.0.0.0","url":""}}
 * Created by lw on 2018/1/5.
 */

public class MemberList {

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

    private List<Member> members;
    private ChannelInfo channelInfo;

    private String deviceVersion;
    private String deviceDownloadUrl;

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

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }

    public void setChannelInfo(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getDeviceDownloadUrl() {
        return deviceDownloadUrl;
    }

    public void setDeviceDownloadUrl(String deviceDownloadUrl) {
        this.deviceDownloadUrl = deviceDownloadUrl;
    }

    @Override
    public String toString() {
        return "MemberList{" +
                "resultCode=" + resultCode +
                ", errMsg='" + errMsg + '\'' +
                ", cmd=" + cmd +
                ", members=" + members +
                ", channelInfo=" + channelInfo +
                ", deviceVersion='" + deviceVersion + '\'' +
                ", deviceDownloadUrl='" + deviceDownloadUrl + '\'' +
                '}';
    }

    public static class ChannelInfo {
        List<Integer> channels_were_bought;

        public List<Integer> getChannels_were_bought() {
            return channels_were_bought;
        }

        public void setChannels_were_bought(List<Integer> channels_were_bought) {
            this.channels_were_bought = channels_were_bought;
        }

        public  ChannelInfo parse(String str) {
            if (TextUtils.isEmpty(str)) return null;

            JSONObject json = null;
            try {
                json = new JSONObject(str);
                if (json != null) {
                    return parse(json);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static ChannelInfo parse(JSONObject json) {
            if (json == null) return null;
            ChannelInfo channelInfo = new ChannelInfo();
            JSONArray jsonArray = json.optJSONArray("channels_were_bought");
            List<Integer> integers = new ArrayList();
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        integers.add(jsonArray.getInt(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            channelInfo.setChannels_were_bought(integers);
            return channelInfo;
        }

        @Override
        public String toString() {
            return "ChannelInfo{" +
                    "channels_were_bought=" + channels_were_bought +
                    '}';
        }
    }
}
