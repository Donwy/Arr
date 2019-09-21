package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LY on 2017/10/10.
 */

public class CloudDeviceBean implements Serializable {

    public String device_id;       //设备ID
    public String end_time;        //设备订单到期时间
    public String device_name;     //设备备注名
    public String device_model;    //设备类型
    public List<CloudChannelBean> cloudChannelList; //开通的通道

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }

    public List<CloudChannelBean> getCloudChannelList() {
        return cloudChannelList;
    }

    public void setCloudChannelList(List<CloudChannelBean> cloudChannelList) {
        this.cloudChannelList = cloudChannelList;
    }

    public static CloudDeviceBean parse(String str){
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

    public static CloudDeviceBean parse(JSONObject json){
        if(json == null) return null;
        CloudDeviceBean item = new CloudDeviceBean();
        item.setDevice_id(json.optString("device_id"));
        item.setEnd_time(json.optString("end_time"));
        item.setDevice_name(json.optString("device_name"));
        item.setDevice_model(json.optString("device_model"));
        if (json.has("channelList")) {
            try {

                JSONArray channelArray = json.getJSONArray("channelList");
                List<CloudChannelBean> list = new ArrayList<>();
                for (int i = 0; i < channelArray.length(); i++) {
                    CloudChannelBean cloudChannelItem = new CloudChannelBean();
                    cloudChannelItem.setDeviceId(channelArray.optJSONObject(i).optString("device_id"));
                    cloudChannelItem.setEndTime(channelArray.optJSONObject(i).optString("end_time"));
                    cloudChannelItem.setChannel(channelArray.optJSONObject(i).optString("channel"));
                    cloudChannelItem.setChannelName(channelArray.optJSONObject(i).optString("channel_name"));
                    list.add(cloudChannelItem);
                }
                item.setCloudChannelList(list);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return item;
    }

    @Override
    public String toString() {
        return "CloudDeviceBean{" +
                "device_id='" + device_id + '\'' +
                ", end_time='" + end_time + '\'' +
                ", device_name='" + device_name + '\'' +
                ", device_model='" + device_model + '\'' +
                ", cloudChannelList=" + cloudChannelList +
                '}';
    }

    public static class CloudChannelBean implements Serializable {
        private String deviceId;
        private String endTime;
        private String channel;
        private String channelName;

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        @Override
        public String toString() {
            return "CloudChannelBean{" +
                    "deviceId='" + deviceId + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", channel='" + channel + '\'' +
                    ", channelName='" + channelName + '\'' +
                    '}';
        }
    }
}
