package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by LY on 2017/10/10.
 */

public class CloudVideoBean implements Serializable {

    private static final long serialVersionUID = 3L;

    public String vl_id;       //视频主键
    public String device_id;   //设备ID
    public String video_url;//视频地址
    public String type;     //视频编码类型
    public String create_time; //视频时间

    public String getVl_id() {
        return vl_id;
    }

    public void setVl_id(String vl_id) {
        this.vl_id = vl_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public static CloudVideoBean parse(String str){
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

    public static CloudVideoBean parse(JSONObject json){
        if(json == null) return null;
        CloudVideoBean item = new CloudVideoBean();
        item.setVl_id(json.optString("vl_id"));
        item.setDevice_id(json.optString("device_id"));
        item.setVideo_url(json.optString("video_url"));
        item.setType(json.optString("type"));
        item.setCreate_time(json.optString("create_time"));
        return item;
    }


    @Override
    public String toString() {
        return "CloudVideoBean{" +
                "vl_id=" + vl_id +
                ", device_id=" + device_id +
                ", video_url='" + video_url + '\'' +
                ", type='" + type + '\'' +
                ", create_time=" + create_time +
                '}';
    }
}
