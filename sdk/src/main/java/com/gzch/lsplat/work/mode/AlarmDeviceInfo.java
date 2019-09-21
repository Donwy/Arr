package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by LY on 2018/1/31.
 */

public class AlarmDeviceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String device_id;
    private String pic;
    private String last_time;
    private String device_name;
    private String limit_days;//最快一次过期的报警时间

    public AlarmDeviceInfo() {
    }

    public AlarmDeviceInfo(String device_id, String pic, String last_time, String device_name, String limit_days) {
        this.device_id = device_id;
        this.pic = pic;
        this.last_time = last_time;
        this.device_name = device_name;
        this.limit_days = limit_days;
    }

    public static AlarmDeviceInfo parse(String str){
        if (TextUtils.isEmpty(str))return null;
        try {
            JSONObject json = new JSONObject(str);
            return parse(json);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static AlarmDeviceInfo parse(JSONObject json){
        if (json == null)return null;
        AlarmDeviceInfo deviceList = new AlarmDeviceInfo();
        deviceList.setDevice_id(json.optString("device_id"));
        deviceList.setPic(json.optString("pic"));
        deviceList.setLast_time(json.optString("last_time"));
        deviceList.setDevice_name(json.optString("device_name"));
        deviceList.setLimit_days(json.optString("limit_days"));
        return deviceList;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getLimit_days() {
        return limit_days;
    }

    public void setLimit_days(String limit_days) {
        this.limit_days = limit_days;
    }

    @Override
    public String toString() {
        return "AlarmDeviceInfo{" +
                "device_id='" + device_id + '\'' +
                ", pic='" + pic + '\'' +
                ", last_time='" + last_time + '\'' +
                ", device_name='" + device_name + '\'' +
                ", limit_days='" + limit_days + '\'' +
                '}';
    }

}
