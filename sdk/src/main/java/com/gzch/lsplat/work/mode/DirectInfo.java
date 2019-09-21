package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import com.gzch.lsplat.work.R;
import com.longse.lsapc.lsacore.BitdogInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lw on 2018/1/9.
 */

public class DirectInfo implements Serializable {

    public static final String IPC = "IPC";
    public static final String FISH = "FISH";
    public static final String DVR = "DVR";
    public static final String XVR = "XVR";
    public static final String NVR = "NVR";
    public static final String HVR = "HVR";
    public static final String SN = "SN";

    private String deviceId;
    private String deviceType;
    private String deviceName;
    private String ip;
    private String port;
    private String user;
    private String password;
    private int channelNumber;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }

    public EqupInfo createEqupInfo(){
        EqupInfo equpInfo = new EqupInfo();
        equpInfo.setDirect(true);
//        equpInfo.setEqupId(ip + port);
        equpInfo.setEqupId(deviceId);
        equpInfo.setDeviceDetatilType(deviceType);
        equpInfo.setDeviceType(deviceType);
        equpInfo.setOrder_num("0");
        equpInfo.setCateId("-1");
        equpInfo.setDeviceName(BitdogInterface.getInstance().getApplicationContext().getString(R.string.direct_devices));
        equpInfo.setLocalUser(user);
        equpInfo.setLocalPwd(password);
        equpInfo.setDeviceName(deviceName);
        equpInfo.setPrivateServer(ip);
        equpInfo.setLocaleDeviceIp(ip);
        try {
            int p = Integer.valueOf(port);
            equpInfo.setPort(p);
            equpInfo.setLocaleDevicePort(p);
        } catch (Exception e){
            e.printStackTrace();
        }
        equpInfo.setMode(channelNumber);
        String url = "";
        if (TextUtils.isEmpty(user)){
            url = String.format("rtsp://%s:%s/",ip,port);
        } else if(TextUtils.isEmpty(password)){
            url = String.format("rtsp://%s@%s:%s/",user,ip,port);
        } else {
            url = String.format("rtsp://%s:%s@%s:%s/",user,password,ip,port);
        }
        equpInfo.setDeviceConnectServer(url);
        if (channelNumber > 1){
            equpInfo.setEquoModle("2100");
        } else {
            equpInfo.setEquoModle("2000");
        }
        ChannelInfoEntity channelInfoEntity = null;
        List<ChannelInfoEntity> channelInfoEntities = new ArrayList<>();
        for (int i = 0; i < channelNumber; i++){
            channelInfoEntity = new ChannelInfoEntity();
            channelInfoEntity.setChannel(i);
            channelInfoEntity.setOrder_num(i);
            channelInfoEntities.add(channelInfoEntity);
        }
        equpInfo.setInfoEntitys(channelInfoEntities);
        return equpInfo;
    }

    public static DirectInfo parse(String str){
        if (!TextUtils.isEmpty(str)){
            try {
                JSONObject jsonObject = new JSONObject(str);
                if (jsonObject != null){
                    return parse(jsonObject);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static DirectInfo parse(JSONObject jsonObject){
        if (jsonObject == null)return null;
        DirectInfo directInfo = new DirectInfo();
        directInfo.setDeviceId(jsonObject.optString("deviceId"));
        directInfo.setDeviceType(jsonObject.optString("deviceType"));
        directInfo.setDeviceName(jsonObject.optString("deviceName"));
        directInfo.setIp(jsonObject.optString("ip"));
        directInfo.setPort(jsonObject.optString("port"));
        directInfo.setUser(jsonObject.optString("user"));
        directInfo.setPassword(jsonObject.optString("password"));
        try {
            String n = jsonObject.optString("channelNumber");
            if (!TextUtils.isEmpty(n)){
                int x = Integer.valueOf(n);
                directInfo.setChannelNumber(x);
                return directInfo;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        directInfo.setChannelNumber(1);
        return directInfo;
    }

    @Override
    public String toString() {
        return "DirectInfo{" +
                "deviceType='" + deviceType + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", channelNumber=" + channelNumber +
                '}';
    }
}
