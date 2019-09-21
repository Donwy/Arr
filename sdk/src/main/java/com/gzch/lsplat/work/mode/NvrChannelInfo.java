package com.gzch.lsplat.work.mode;


import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dk on 2018/5/21.
 */

public class NvrChannelInfo {
    private boolean motswitch;
    private List<Motdata> motdata;
    private boolean smartswitch;
    private List<Smartdata> smartdata;

    //编码参数
    private String cmd_type;                //类型（暂时判断通道）
    private int code_type=-1;               //编码类型1：H264  2：H265  7：H264+  8：H265+
    private List<DeviceStream> stream;      //码流信息
    private StreamEnable enable;
    private int channelLinkStatus;          //1正常 0不正常
    private int channel_type;               ///通道类型  0为模拟设备  1为IPC（IPC不可以调画质）
    private String device_id;
    private String channel_name;
    private String channel_id;
    private String type;
    private int code;

    //控制设备音量
    private boolean hasIPCAudioCfg = false;
    private boolean ipcAudioEnable = false;
    private int maxVolume = -1;
    private int nowVolume = -1;

    private boolean hasOnliveUpdate = false;    //判断是否支持远程升级通道版本，包括重启，恢复出厂设置
    private int checkStatus = -1;               //判断是否可升级通道版本  CheckStatus=1,有更新固件,其他没有版本需要更新
    private String curVersion;                  //当前版本
    private String onlineVersion;               //最新的版本号。
    private String versionLog;                  //版本升级的内容。
    private int downUpdatefileStatus;       //1时 表示在下载升级固件时正常, 为0表示没有在下载固件, 其他任何值表示下载出现异常
    private int progressBar;

    private boolean hasPirControl = false;
    private boolean lightOpen = false;
    private boolean lightAlarm = false;
    private boolean leftEnable = false;
    private boolean rightEnable = false;
    private int lightMaxDuring = 1;
    private int lightNowDuring = 1;
    private int nowSensitity = 1;


    private boolean mChannelRestart = false;    //判断通道是否重启。


    private boolean restart_allow = true;
    private boolean encode_allow = true;
    private boolean factory_allow = true;
    private boolean motion_allow = true;
    private boolean formatDisk_allow = true;

    public boolean isAllow(){
        return (restart_allow && encode_allow && factory_allow && motion_allow && formatDisk_allow);
    }


    public NvrChannelInfo() {
    }

    public boolean isRestart_allow() {
        return restart_allow;
    }

    public void setRestart_allow(boolean restart_allow) {
        this.restart_allow = restart_allow;
    }

    public boolean isEncode_allow() {
        return encode_allow;
    }

    public void setEncode_allow(boolean encode_allow) {
        this.encode_allow = encode_allow;
    }

    public boolean isFactory_allow() {
        return factory_allow;
    }

    public void setFactory_allow(boolean factory_allow) {
        this.factory_allow = factory_allow;
    }

    public boolean isMotion_allow() {
        return motion_allow;
    }

    public void setMotion_allow(boolean motion_allow) {
        this.motion_allow = motion_allow;
    }

    public boolean isFormatDisk_allow() {
        return formatDisk_allow;
    }

    public void setFormatDisk_allow(boolean formatDisk_allow) {
        this.formatDisk_allow = formatDisk_allow;
    }

    public boolean isMotswitch() {
        return motswitch;
    }

    public void setMotswitch(boolean motswitch) {
        this.motswitch = motswitch;
    }

    public List<Motdata> getMotdata() {
        return motdata;
    }

    public void setMotdata(List<Motdata> motdata) {
        this.motdata = motdata;
    }

    public boolean isSmartswitch() {
        return smartswitch;
    }

    public void setSmartswitch(boolean smartswitch) {
        this.smartswitch = smartswitch;
    }

    public List<Smartdata> getSmartdata() {
        return smartdata;
    }

    public void setSmartdata(List<Smartdata> smartdata) {
        this.smartdata = smartdata;
    }

    public String getCmd_type() {
        return cmd_type;
    }

    public void setCmd_type(String cmd_type) {
        this.cmd_type = cmd_type;
    }

    public int getCode_type() {
        return code_type;
    }

    public void setCode_type(int code_type) {
        this.code_type = code_type;
    }

    public List<DeviceStream> getStream() {
        return stream;
    }

    public void setStream(List<DeviceStream> stream) {
        this.stream = stream;
    }

    public StreamEnable getEnable() {
        return enable;
    }

    public void setEnable(StreamEnable enable) {
        this.enable = enable;
    }

    public int getChannelLinkStatus() {
        return channelLinkStatus;
    }

    public void setChannelLinkStatus(int channelLinkStatus) {
        this.channelLinkStatus = channelLinkStatus;
    }

    public int getChannel_type() {
        return channel_type;
    }

    public void setChannel_type(int channel_type) {
        this.channel_type = channel_type;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public boolean isHasIPCAudioCfg() {
        return hasIPCAudioCfg;
    }

    public void setHasIPCAudioCfg(boolean hasIPCAudioCfg) {
        this.hasIPCAudioCfg = hasIPCAudioCfg;
    }

    public boolean isIpcAudioEnable() {
        return ipcAudioEnable;
    }

    public void setIpcAudioEnable(boolean ipcAudioEnable) {
        this.ipcAudioEnable = ipcAudioEnable;
    }

    public int getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(int maxVolume) {
        this.maxVolume = maxVolume;
    }

    public int getNowVolume() {
        return nowVolume;
    }

    public void setNowVolume(int nowVolume) {
        this.nowVolume = nowVolume;
    }

    public boolean isHasOnliveUpdate() {
        return hasOnliveUpdate;
    }

    public void setHasOnliveUpdate(boolean hasOnliveUpdate) {
        this.hasOnliveUpdate = hasOnliveUpdate;
    }

    public String getCurVersion() {
        return curVersion;
    }

    public void setCurVersion(String curVersion) {
        this.curVersion = curVersion;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getOnlineVersion() {
        return onlineVersion;
    }

    public void setOnlineVersion(String onlineVersion) {
        this.onlineVersion = onlineVersion;
    }

    public String getVersionLog() {
        return versionLog;
    }

    public void setVersionLog(String versionLog) {
        this.versionLog = versionLog;
    }

    public int getDownUpdatefileStatus() {
        return downUpdatefileStatus;
    }

    public void setDownUpdatefileStatus(int downUpdatefileStatus) {
        this.downUpdatefileStatus = downUpdatefileStatus;
    }

    public int getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(int progressBar) {
        this.progressBar = progressBar;
    }

    public boolean ismChannelRestart() {
        return mChannelRestart;
    }

    public void setmChannelRestart(boolean mChannelRestart) {
        this.mChannelRestart = mChannelRestart;
    }

    public boolean isHasPirControl() {
        return hasPirControl;
    }

    public void setHasPirControl(boolean hasPirControl) {
        this.hasPirControl = hasPirControl;
    }

    public boolean isLightOpen() {
        return lightOpen;
    }

    public void setLightOpen(boolean lightOpen) {
        this.lightOpen = lightOpen;
    }

    public boolean isLightAlarm() {
        return lightAlarm;
    }

    public void setLightAlarm(boolean lightAlarm) {
        this.lightAlarm = lightAlarm;
    }

    public boolean isLeftEnable() {
        return leftEnable;
    }

    public void setLeftEnable(boolean leftEnable) {
        this.leftEnable = leftEnable;
    }

    public boolean isRightEnable() {
        return rightEnable;
    }

    public void setRightEnable(boolean rightEnable) {
        this.rightEnable = rightEnable;
    }

    public int getLightMaxDuring() {
        return lightMaxDuring;
    }

    public void setLightMaxDuring(int lightMaxDuring) {
        this.lightMaxDuring = lightMaxDuring;
    }

    public int getLightNowDuring() {
        return lightNowDuring;
    }

    public void setLightNowDuring(int lightNowDuring) {
        this.lightNowDuring = lightNowDuring;
    }

    public int getNowSensitity() {
        return nowSensitity;
    }

    public void setNowSensitity(int nowSensitity) {
        this.nowSensitity = nowSensitity;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public static NvrChannelInfo parse(String str){
        if(TextUtils.isEmpty(str)) return null;

        JSONObject json = null;
        try {
            json = new JSONObject(str);
            if(json != null){
                return parse(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static NvrChannelInfo parse(JSONObject json){
        if(json == null) return null;
        NvrChannelInfo item = new NvrChannelInfo();
        /**
         * "restart_allow"    ：重启
         "encode_allow"    ：编码参数设置
         "factory_allow"    ：恢复出厂
         "motion_allow"    ：移动侦测设置（包括开关和时间段）
         "formatDisk_allow"  or tfCardFormat_allow  (ipc)  ：硬盘格式化
         */
        if (json.has("restart_allow")){
            item.setRestart_allow(json.optBoolean("restart_allow"));
        }
        if (json.has("encode_allow")){
            item.setEncode_allow(json.optBoolean("encode_allow"));
        }
        if (json.has("factory_allow")){
            item.setFactory_allow(json.optBoolean("factory_allow"));
        }
        if (json.has("motion_allow")){
            item.setMotion_allow(json.optBoolean("motion_allow"));
        }
        if (json.has("formatDisk_allow")){
            item.setFormatDisk_allow(json.optBoolean("formatDisk_allow"));
        }
        if (json.has("tfCardFormat_allow")){
            item.setFormatDisk_allow(json.optBoolean("tfCardFormat_allow"));
        }
        item.setMotswitch(json.optBoolean("motswitch"));
        JSONArray mMotdataArray =  json.optJSONArray("motdata");
        List<Motdata> motdata = new ArrayList();
        if (mMotdataArray!=null){
            for (int i = 0; i < mMotdataArray.length(); i++) {
                try {
                    motdata.add(Motdata.parse(mMotdataArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        item.setMotdata(motdata);
        item.setSmartswitch(json.optBoolean("smartswitch"));
        JSONArray mSmartdataArray =  json.optJSONArray("smartdata");
        List<Smartdata> smartdata = new ArrayList();
        if (mSmartdataArray!=null){
            for (int i = 0; i < mSmartdataArray.length(); i++) {
                try {
                    smartdata.add(Smartdata.parse(mSmartdataArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        item.setSmartdata(smartdata);
        item.setCmd_type(json.optString("cmd_type"));
        item.setCode_type(json.optInt("code_type"));
        item.setChannelLinkStatus(json.optInt("channelLinkStatus"));
        item.setChannel_type(json.optInt("channel_type"));
        item.setDevice_id(json.optString("device_id"));
        item.setChannel_id(json.optString("channel_id"));

        if (json.has("channel_name")) {
            item.setChannel_name(json.optString("channel_name"));
        }
        item.setType(json.optString("type"));
        item.setCode(json.optInt("code"));

        JSONArray streamArray=json.optJSONArray("stream");
        List<DeviceStream> streams=new ArrayList<>();
        if (streamArray!=null){
            for (int i = 0; i < streamArray.length(); i++) {
                try {
                    streams.add(DeviceStream.parse(streamArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        item.setStream(streams);
        try {
            item.setEnable(StreamEnable.parse(json.getJSONObject("enable")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (json.has("IPC_AudioCfg")) {
            item.setHasIPCAudioCfg(true);
            JSONObject audioObject = json.optJSONObject("IPC_AudioCfg");
            item.setIpcAudioEnable(audioObject.optBoolean("IPC_AudioEnable"));
            item.setMaxVolume(audioObject.optInt("MaxVolume"));
            item.setNowVolume(audioObject.optInt("NowVolume"));
        } else {
            item.setHasIPCAudioCfg(false);
        }

        if (json.has("LSTV_PIR_CFG")) {
            item.setHasPirControl(true);
            JSONObject audioObject = json.optJSONObject("LSTV_PIR_CFG");
            item.setLightOpen(audioObject.optBoolean("light_open"));
            item.setLightAlarm(audioObject.optBoolean("light_alarm"));
            item.setLeftEnable(audioObject.optBoolean("left_enable"));
            item.setRightEnable(audioObject.optBoolean("right_enable"));
            item.setLightMaxDuring(audioObject.optInt("light_max_during"));
            item.setLightNowDuring(audioObject.optInt("light_now_during"));
            item.setNowSensitity(audioObject.optInt("now_sensitity"));
        } else {
            item.setHasPirControl(false);
        }

        try {
            item.setmChannelRestart(json.getBoolean("channel_restart"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (json.has("OnliveUpdate")) {
            item.setHasOnliveUpdate(true);
            JSONObject upDateObject = json.optJSONObject("OnliveUpdate");
            item.setCheckStatus(upDateObject.optInt("CheckStatus"));
            item.setOnlineVersion(upDateObject.optString("OnlineVersion"));
            item.setCurVersion(upDateObject.optString("CurVersion"));
            item.setVersionLog(upDateObject.optString("VersionLog"));
            item.setDownUpdatefileStatus(upDateObject.optInt("DownUpdatefileStatus"));
            item.setProgressBar(upDateObject.optInt("ProgressBar"));
        } else {
            item.setHasOnliveUpdate(false);
        }

        return item;
    }

    @Override
    public String toString() {
        return "NvrChannelInfo{" +
                "motswitch=" + motswitch +
                ", motdata=" + motdata +
                ", smartswitch=" + smartswitch +
                ", smartdata=" + smartdata +
                ", cmd_type='" + cmd_type + '\'' +
                ", code_type=" + code_type +
                ", stream=" + stream +
                ", enable=" + enable +
                ", channelLinkStatus=" + channelLinkStatus +
                ", channel_type=" + channel_type +
                ", device_id='" + device_id + '\'' +
                ", channel_name='" + channel_name + '\'' +
                ", channel_id='" + channel_id + '\'' +
                ", type='" + type + '\'' +
                ", code=" + code +
                ", hasIPCAudioCfg=" + hasIPCAudioCfg +
                ", ipcAudioEnable=" + ipcAudioEnable +
                ", maxVolume=" + maxVolume +
                ", nowVolume=" + nowVolume +
                ", hasOnliveUpdate=" + hasOnliveUpdate +
                ", checkStatus=" + checkStatus +
                ", curVersion='" + curVersion + '\'' +
                ", onlineVersion='" + onlineVersion + '\'' +
                ", versionLog='" + versionLog + '\'' +
                ", downUpdatefileStatus=" + downUpdatefileStatus +
                ", progressBar=" + progressBar +
                ", hasPirControl=" + hasPirControl +
                ", lightOpen=" + lightOpen +
                ", lightAlarm=" + lightAlarm +
                ", leftEnable=" + leftEnable +
                ", rightEnable=" + rightEnable +
                ", lightMaxDuring=" + lightMaxDuring +
                ", lightNowDuring=" + lightNowDuring +
                ", nowSensitity=" + nowSensitity +
                ", mChannelRestart=" + mChannelRestart +
                '}';
    }
}
