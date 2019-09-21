package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dk on 2017/9/22.
 */

public class DeviceNativeInfo implements Serializable {

    public static final long serialVersionUID = 2L;
    private static JSONObject jsonObject;
    private String serialNum; //9860228094181,
    private String devType; //”IPC”,
    private String firmVer; //””test_001”,
    private String devSdkVer; //”1.0.2.15”,
    private String mac; //”00:00:4d:52:8f:d0”,
    private int tfCardStat; //0,
    private double tfCardAvlCap; //8,
    private double tfCardTotalCap; //16,
    private String devName; //”MyIPC”,
    private String devUserName; //”admin”,
    private String devPassWord; //”admin”,
    private int wlanMode; //1,	//0: ap; 1: sta
    private String apWifiInfo; //”{"ssid":"LongseIPC","password":"12345678","encodeType":"WPA2-PSK"}”,
    private String staWifiInfo; //”{"ssid":"TP-Link","password":"12345678","encodeType":"WPA2-PSK"}”,
    private String ip; //”192.168.1.102”,
    private boolean configSound; //true,
    private String promptLang; //”eng”,
    private int sceneMode; //1,		//0: day; 1: night; 2: auto
    private int streamMode; //2,	//0: main; 1: first sub; 2: second sub; 3: auto
    private long timeStamp; //1489481330154,
    private String timeZone; //28800,
    private boolean motDetOn; //true,
    private List<Boolean> motNVRDetOn;
    private String scheduleAlarm;//":"[8]”,			//json数组的字符串
    private boolean motDetRecord; //false,
    private String scheduleRecord;//":"[8,30,0]”,	//json数组的字符串
    private boolean tfCardFormat; //true,
    private int devVolume; //20,
    private String screenRotation; //1,		//0: 0°; 1: 90°; 2: 180°;3: 270°
    private boolean devStatusLamp; //true,
    private boolean systemUpdateRemind; //true,
    private boolean onlineRemind; //true,
    private boolean offlineRemind; //true,
    private boolean userConnRemind; //true
    private boolean pushImage; //是否推送图片

    private List<Integer> pushTimes;
    private List<Integer> faceModes;
    private List<String> areaAlarm;

    private int currentPushTime = 0;
    private int currentFaceMode = 0;

    private boolean supportMotDetOn = false;//是否支持移动侦测开关
    private int supportPushImage = -1;//是否支持移动侦测开关  0 不包含间隔时间； 1 包含间隔时间


    private boolean issmart = false;          //是否是新设备
    private List<Diskinfo> diskinfo;
    private List<Motdata> motdata;
    private List<Smartdata> smartdata;
    private boolean restart = false;          //收到设备重启的标志
    //编码参数
    private List<DeviceStream> stream;      //码流信息
    private StreamEnable enable;
    private String type;
    private int code;

    private boolean hasDeviceUpdate = false;
    private String currentVersion;
    private String newVersion;
    private int checkStatus = -1;                //0 当前为最新版本； 1 存在可升级版本； 2 正在升级中

    private boolean restart_allow = true;
    private boolean encode_allow = true;
    private boolean factory_allow = true;
    private boolean motion_allow = true;
    private boolean formatDisk_allow = true;

    public static DeviceNativeInfo parse(String str) {
        if (TextUtils.isEmpty(str)) return null;
        try {
            JSONObject jsonObject = new JSONObject(str);
            if (jsonObject == null) return null;
            return parse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param json
     * @return
     */
    public static DeviceNativeInfo parse(JSONObject json) {
        if (json == null) return null;
        jsonObject = json;
        DeviceNativeInfo nativeInfo = new DeviceNativeInfo();
        nativeInfo.setSerialNum(json.optString("serialNum"));
        nativeInfo.setDevType(json.optString("devType"));
        nativeInfo.setFirmVer(json.optString("firmVer"));
        nativeInfo.setDevSdkVer(json.optString("devSdkVer"));
        nativeInfo.setMac(json.optString("mac"));
        nativeInfo.setTfCardStat(json.optInt("tfCardStat"));
        nativeInfo.setTfCardAvlCap(json.optDouble("tfCardAvlCap"));
        nativeInfo.setTfCardTotalCap(json.optDouble("tfCardTotalCap"));
        nativeInfo.setDevName(json.optString("devName"));
        nativeInfo.setDevUserName(json.optString("devUserName"));
        nativeInfo.setDevPassWord(json.optString("devPassWord"));
        nativeInfo.setWlanMode(json.optInt("wlanMode"));
        nativeInfo.setApWifiInfo(json.optString("apWifiInfo"));
        nativeInfo.setStaWifiInfo(json.optString("staWifiInfo"));
        nativeInfo.setIp(json.optString("ip"));
        nativeInfo.setConfigSound(json.optBoolean("configSound"));
        nativeInfo.setPromptLang(json.optString("promptLang"));
        nativeInfo.setSceneMode(json.optInt("sceneMode"));
        nativeInfo.setStreamMode(json.optInt("streamMode"));
        nativeInfo.setTimeStamp(json.optLong("timeStamp"));
        nativeInfo.setTimeZone(json.optString("timeZone"));

        if (json.has("device_update")) {
            nativeInfo.setHasDeviceUpdate(true);
            try {
                JSONObject updateObject = json.getJSONObject("device_update");
                nativeInfo.setCurrentVersion(updateObject.optString("current_version"));
                nativeInfo.setNewVersion(updateObject.optString("new_version"));
                nativeInfo.setCheckStatus(updateObject.optInt("check_status"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            nativeInfo.setHasDeviceUpdate(false);
        }


        nativeInfo.setSupportMotDetOn(json.has("motDetOn"));
        if (!json.optString("devType").contains("IPC")) {
            JSONArray mNVRArray = json.optJSONArray("motDetOn");
            List<Boolean> nvrDetOn = new ArrayList();
            if (mNVRArray != null) {
                for (int i = 0; i < mNVRArray.length(); i++) {
                    try {
                        nvrDetOn.add((Boolean) mNVRArray.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                nativeInfo.setMotNVRDetOn(nvrDetOn);
            } else {
                nativeInfo.setMotDetOn(json.optBoolean("motDetOn"));
            }

        } else {
            nativeInfo.setMotDetOn(json.optBoolean("motDetOn"));
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = json.getJSONObject("motDetSens");
            if (jsonObject.has("push_times")) {
                nativeInfo.setSupportPushImage(1);
                List<Integer> pushTime = new ArrayList<>();
                JSONArray pushArray = jsonObject.optJSONArray("push_times");
                if (pushArray != null) {
                    for (int i = 0; i < pushArray.length(); i++) {
                        pushTime.add(pushArray.optInt(i));
                    }
                }
                nativeInfo.setPushTimes(pushTime);
                nativeInfo.setCurrentPushTime(pushTime.get(jsonObject.optInt("push_time_subscript")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject == null) {
            if (json.has("push_image")) {
                nativeInfo.setSupportPushImage(0);
                nativeInfo.setPushImage(json.optBoolean("push_image"));
            }
        }

        nativeInfo.setScheduleAlarm(json.optString("scheduleAlarm"));
        nativeInfo.setMotDetRecord(json.optBoolean("motDetRecord"));
        nativeInfo.setScheduleRecord(json.optString("scheduleRecord"));
        nativeInfo.setTfCardFormat(json.optBoolean("tfCardFormat"));
        nativeInfo.setDevVolume(json.optInt("devVolume"));
        nativeInfo.setScreenRotation(json.optString("screenRotation"));
        nativeInfo.setDevStatusLamp(json.optBoolean("devStatusLamp"));
        nativeInfo.setSystemUpdateRemind(json.optBoolean("systemUpdateRemind"));
        nativeInfo.setOnlineRemind(json.optBoolean("onlineRemind"));
        nativeInfo.setOfflineRemind(json.optBoolean("offlineRemind"));
        nativeInfo.setUserConnRemind(json.optBoolean("userConnRemind"));
        nativeInfo.setIssmart(json.optBoolean("issmart"));

        List<Diskinfo> info = new ArrayList<>();
        if (!json.optString("devType").contains("IPC")) {
            JSONArray mdiskinfoArray = json.optJSONArray("diskinfo");
            if (mdiskinfoArray != null) {
                for (int i = 0; i < mdiskinfoArray.length(); i++) {
                    try {
                        info.add(Diskinfo.parse(mdiskinfoArray.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Diskinfo diskinfo = new Diskinfo();
            diskinfo.setDiskAvlCap(nativeInfo.getTfCardAvlCap() * 1024);
            diskinfo.setDiskTotalCap(nativeInfo.getTfCardTotalCap() * 1024);
            diskinfo.setDiskStat(nativeInfo.getTfCardStat());
            diskinfo.setDiskPort(0);
            info.add(diskinfo);
        }
        nativeInfo.setDiskinfo(info);

        JSONArray motdataArray = json.optJSONArray("motdata");
        List<Motdata> motdata = new ArrayList<>();
        if (motdataArray != null) {
            for (int i = 0; i < motdataArray.length(); i++) {
                try {
                    motdata.add(Motdata.parse(motdataArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        nativeInfo.setMotdata(motdata);

        JSONArray smartdataArray = json.optJSONArray("smartdata");
        List<Smartdata> smartdata = new ArrayList<>();
        if (smartdataArray != null) {
            for (int i = 0; i < smartdataArray.length(); i++) {
                try {
                    smartdata.add(Smartdata.parse(smartdataArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        nativeInfo.setSmartdata(smartdata);
        nativeInfo.setReStart(json.optBoolean("restart"));

        JSONArray streamArray = json.optJSONArray("stream");
        List<DeviceStream> streams = new ArrayList<>();
        if (streamArray != null) {
            for (int i = 0; i < streamArray.length(); i++) {
                try {
                    streams.add(DeviceStream.parse(streamArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        nativeInfo.setStream(streams);
        try {
            if (json.has("enable")) {
                nativeInfo.setEnable(StreamEnable.parse(json.getJSONObject("enable")));
            } else {
                nativeInfo.setEnable(StreamEnable.defaultStreamEnable());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        nativeInfo.setType(json.optString("type"));
        nativeInfo.setCode(json.optInt("code"));

        /**
         * "restart_allow"    ：重启
         "encode_allow"    ：编码参数设置
         "factory_allow"    ：恢复出厂
         "motion_allow"    ：移动侦测设置（包括开关和时间段）
         "formatDisk_allow"  or tfCardFormat_allow  (ipc)  ：硬盘格式化
         */
        if (json.has("restart_allow")) {
            nativeInfo.setRestart_allow(json.optBoolean("restart_allow"));
        }
        if (json.has("encode_allow")) {
            nativeInfo.setEncode_allow(json.optBoolean("encode_allow"));
        }
        if (json.has("factory_allow")) {
            nativeInfo.setFactory_allow(json.optBoolean("factory_allow"));
        }
        if (json.has("motion_allow")) {
            nativeInfo.setMotion_allow(json.optBoolean("motion_allow"));
        }
        if (json.has("formatDisk_allow")) {
            nativeInfo.setFormatDisk_allow(json.optBoolean("formatDisk_allow"));
        }
        if (json.has("tfCardFormat_allow")) {
            nativeInfo.setFormatDisk_allow(json.optBoolean("tfCardFormat_allow"));
        }

        if (json.has("face_mode")) {
            JSONObject jsonObject1 = json.optJSONObject("face_mode");
            if (jsonObject1 != null) {
                nativeInfo.setCurrentFaceMode(jsonObject1.optInt("current_mode"));
                JSONArray jsonArray = jsonObject1.optJSONArray("support_mode");
                List<Integer> face = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    face.add(jsonArray.optInt(i));
                }
                nativeInfo.setFaceModes(face);
            }
        }

        if (json.has("alarm_area")) {

            JSONArray areaArray = json.optJSONArray("alarm_area");
            if (areaArray != null) {
                List<String> areaAlarm = new ArrayList<>();
                StringBuilder data;
                for (int i = 0; i < areaArray.length(); i++) {
                    data = new StringBuilder(Integer.toBinaryString(areaArray.optInt(i)));
                    if (data.length() > 22) {
                        data = new StringBuilder(data.substring(data.length() - 22));
                    }
                    if (data.length() < 22) {
                        while (data.length() < 22) {
                            data.insert(0, "0");
                        }
                    }
                    areaAlarm.add(data.toString());
                }
                nativeInfo.setAreaAlarm(areaAlarm);
            }
        }


        return nativeInfo;
    }

    public boolean isAllow() {
        return (restart_allow && encode_allow && factory_allow && motion_allow && formatDisk_allow);
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

    public boolean isSupportMotDetOn() {
        return supportMotDetOn;
    }

    public void setSupportMotDetOn(boolean supportMotDetOn) {
        this.supportMotDetOn = supportMotDetOn;
    }

    public List<Integer> getPushTimes() {
        return pushTimes;
    }

    public void setPushTimes(List<Integer> pushTimes) {
        this.pushTimes = pushTimes;
    }

    public int getCurrentPushTime() {
        return currentPushTime;
    }

    public void setCurrentPushTime(int currentPushTime) {
        this.currentPushTime = currentPushTime;
    }

    public List<Boolean> getMotNVRDetOn() {
        return motNVRDetOn;
    }

    public void setMotNVRDetOn(List<Boolean> motNVRDetOn) {
        this.motNVRDetOn = motNVRDetOn;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    public String getFirmVer() {
        return firmVer;
    }

    public void setFirmVer(String firmVer) {
        this.firmVer = firmVer;
    }

    public String getDevSdkVer() {
        return devSdkVer;
    }

    public void setDevSdkVer(String devSdkVer) {
        this.devSdkVer = devSdkVer;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getTfCardStat() {
        return tfCardStat;
    }

    public void setTfCardStat(int tfCardStat) {
        this.tfCardStat = tfCardStat;
    }

    public double getTfCardAvlCap() {
        return tfCardAvlCap;
    }

    public void setTfCardAvlCap(double tfCardAvlCap) {
        this.tfCardAvlCap = (double) Math.round(tfCardAvlCap * 100) / 100;
    }

    public double getTfCardTotalCap() {
        return tfCardTotalCap;
    }

    public void setTfCardTotalCap(double tfCardTotalCap) {
        this.tfCardTotalCap = (double) Math.round(tfCardTotalCap * 100) / 100;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getDevUserName() {
        return devUserName;
    }

    public void setDevUserName(String devUserName) {
        this.devUserName = devUserName;
    }

    public String getDevPassWord() {
        return devPassWord;
    }

    public void setDevPassWord(String devPassWord) {
        this.devPassWord = devPassWord;
    }

    public int getWlanMode() {
        return wlanMode;
    }

    public void setWlanMode(int wlanMode) {
        this.wlanMode = wlanMode;
    }

    public String getApWifiInfo() {
        return apWifiInfo;
    }

    public void setApWifiInfo(String apWifiInfo) {
        this.apWifiInfo = apWifiInfo;
    }

    public String getStaWifiInfo() {
        return staWifiInfo;
    }

    public void setStaWifiInfo(String staWifiInfo) {
        this.staWifiInfo = staWifiInfo;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isConfigSound() {
        return configSound;
    }

    public void setConfigSound(boolean configSound) {
        this.configSound = configSound;
    }

    public String getPromptLang() {
        return promptLang;
    }

    public void setPromptLang(String promptLang) {
        this.promptLang = promptLang;
    }

    public int getSceneMode() {
        return sceneMode;
    }

    public void setSceneMode(int sceneMode) {
        this.sceneMode = sceneMode;
    }

    public int getStreamMode() {
        return streamMode;
    }

    public void setStreamMode(int streamMode) {
        this.streamMode = streamMode;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isMotDetOn() {
        return motDetOn;
    }

    public void setMotDetOn(boolean motDetOn) {
        this.motDetOn = motDetOn;
    }

    public String getScheduleAlarm() {
        return scheduleAlarm;
    }

    public void setScheduleAlarm(String scheduleAlarm) {
        this.scheduleAlarm = scheduleAlarm;
    }

    public boolean isMotDetRecord() {
        return motDetRecord;
    }

    public void setMotDetRecord(boolean motDetRecord) {
        this.motDetRecord = motDetRecord;
    }

    public String getScheduleRecord() {
        return scheduleRecord;
    }

    public void setScheduleRecord(String scheduleRecord) {
        this.scheduleRecord = scheduleRecord;
    }

    public boolean isTfCardFormat() {
        return tfCardFormat;
    }

    public void setTfCardFormat(boolean tfCardFormat) {
        this.tfCardFormat = tfCardFormat;
    }

    public int getDevVolume() {
        return devVolume;
    }

    public void setDevVolume(int devVolume) {
        this.devVolume = devVolume;
    }

    public String getScreenRotation() {
        return screenRotation;
    }

    public void setScreenRotation(String screenRotation) {
        this.screenRotation = screenRotation;
    }

    public boolean isDevStatusLamp() {
        return devStatusLamp;
    }

    public void setDevStatusLamp(boolean devStatusLamp) {
        this.devStatusLamp = devStatusLamp;
    }

    public boolean isSystemUpdateRemind() {
        return systemUpdateRemind;
    }

    public void setSystemUpdateRemind(boolean systemUpdateRemind) {
        this.systemUpdateRemind = systemUpdateRemind;
    }

    public boolean isOnlineRemind() {
        return onlineRemind;
    }

    public void setOnlineRemind(boolean onlineRemind) {
        this.onlineRemind = onlineRemind;
    }

    public boolean isOfflineRemind() {
        return offlineRemind;
    }

    public void setOfflineRemind(boolean offlineRemind) {
        this.offlineRemind = offlineRemind;
    }

    public boolean isUserConnRemind() {
        return userConnRemind;
    }

    public void setUserConnRemind(boolean userConnRemind) {
        this.userConnRemind = userConnRemind;
    }

    public boolean isIssmart() {
        return issmart;
    }

    public void setIssmart(boolean issmart) {
        this.issmart = issmart;
    }

    public List<Diskinfo> getDiskinfo() {
        return diskinfo;
    }

    public void setDiskinfo(List<Diskinfo> diskinfo) {
        this.diskinfo = diskinfo;
    }

    public List<Motdata> getMotdata() {
        return motdata;
    }

    public void setMotdata(List<Motdata> motdata) {
        this.motdata = motdata;
    }

    public List<Smartdata> getSmartdata() {
        return smartdata;
    }

    public void setSmartdata(List<Smartdata> smartdata) {
        this.smartdata = smartdata;
    }

    public boolean isReStart() {
        return restart;
    }

    public void setReStart(boolean restart) {
        this.restart = restart;
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

    public boolean isPushImage() {
        return pushImage;
    }

    public void setPushImage(boolean pushImage) {
        this.pushImage = pushImage;
    }

    public int isSupportPushImage() {
        return supportPushImage;
    }

    public void setSupportPushImage(int supportPushImage) {
        this.supportPushImage = supportPushImage;
    }

    public boolean isHasDeviceUpdate() {
        return hasDeviceUpdate;
    }

    public void setHasDeviceUpdate(boolean hasDeviceUpdate) {
        this.hasDeviceUpdate = hasDeviceUpdate;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public List<String> getAreaAlarm() {
        return areaAlarm;
    }

    public void setAreaAlarm(List<String> areaAlarm) {
        this.areaAlarm = areaAlarm;
    }

    public List<Integer> getFaceModes() {
        return faceModes;
    }

    public void setFaceModes(List<Integer> faceModes) {
        this.faceModes = faceModes;
    }

    public int getCurrentFaceMode() {
        return currentFaceMode;
    }

    public void setCurrentFaceMode(int currentFaceMode) {
        this.currentFaceMode = currentFaceMode;
    }

    public DeviceNativeInfo parseParam(String str) {
        if (jsonObject == null) return null;
        try {
            JSONObject object = new JSONObject(str);
            if (object == null) return null;
            String key1;
//            String key2;
            Iterator<String> iterator1 = jsonObject.keys();
//            Iterator<String> iterator2 = object.keys();
            while (iterator1.hasNext()) {
                key1 = iterator1.next();
                if (object.has(key1) && !"type".equals(key1) && !"code".equals(key1)) {
                    jsonObject.put(key1, object.opt(key1));
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parse(jsonObject);
    }

    @Override
    public String toString() {
        return "DeviceNativeInfo{" +
                "serialNum='" + serialNum + '\'' +
                ", devType='" + devType + '\'' +
                ", firmVer='" + firmVer + '\'' +
                ", devSdkVer='" + devSdkVer + '\'' +
                ", mac='" + mac + '\'' +
                ", tfCardStat=" + tfCardStat +
                ", tfCardAvlCap=" + tfCardAvlCap +
                ", tfCardTotalCap=" + tfCardTotalCap +
                ", devName='" + devName + '\'' +
                ", devUserName='" + devUserName + '\'' +
                ", devPassWord='" + devPassWord + '\'' +
                ", wlanMode=" + wlanMode +
                ", apWifiInfo='" + apWifiInfo + '\'' +
                ", staWifiInfo='" + staWifiInfo + '\'' +
                ", ip='" + ip + '\'' +
                ", configSound=" + configSound +
                ", promptLang='" + promptLang + '\'' +
                ", sceneMode=" + sceneMode +
                ", streamMode=" + streamMode +
                ", timeStamp=" + timeStamp +
                ", timeZone='" + timeZone + '\'' +
                ", motDetOn=" + motDetOn +
                ", motNVRDetOn=" + motNVRDetOn +
                ", scheduleAlarm='" + scheduleAlarm + '\'' +
                ", motDetRecord=" + motDetRecord +
                ", scheduleRecord='" + scheduleRecord + '\'' +
                ", tfCardFormat=" + tfCardFormat +
                ", devVolume=" + devVolume +
                ", screenRotation='" + screenRotation + '\'' +
                ", devStatusLamp=" + devStatusLamp +
                ", systemUpdateRemind=" + systemUpdateRemind +
                ", onlineRemind=" + onlineRemind +
                ", offlineRemind=" + offlineRemind +
                ", userConnRemind=" + userConnRemind +
                ", pushImage=" + pushImage +
                ", pushTimes=" + pushTimes +
                ", faceModes=" + faceModes +
                ", areaAlarm=" + areaAlarm +
                ", currentPushTime=" + currentPushTime +
                ", currentFaceMode=" + currentFaceMode +
                ", supportMotDetOn=" + supportMotDetOn +
                ", supportPushImage=" + supportPushImage +
                ", issmart=" + issmart +
                ", diskinfo=" + diskinfo +
                ", motdata=" + motdata +
                ", smartdata=" + smartdata +
                ", restart=" + restart +
                ", stream=" + stream +
                ", enable=" + enable +
                ", type='" + type + '\'' +
                ", code=" + code +
                ", hasDeviceUpdate=" + hasDeviceUpdate +
                ", currentVersion='" + currentVersion + '\'' +
                ", newVersion='" + newVersion + '\'' +
                ", checkStatus=" + checkStatus +
                ", restart_allow=" + restart_allow +
                ", encode_allow=" + encode_allow +
                ", factory_allow=" + factory_allow +
                ", motion_allow=" + motion_allow +
                ", formatDisk_allow=" + formatDisk_allow +
                '}';
    }
}
