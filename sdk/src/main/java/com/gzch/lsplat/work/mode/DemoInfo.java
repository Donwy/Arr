package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lw on 2018/1/8.
 */

public class DemoInfo {

    private String id;
    private String device_name;
    private String num;
    private boolean isVr;
    private String deviceType;

    public String getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public boolean isVr() {
        return isVr;
    }
    public void setVr(boolean isVr) {
        this.isVr = isVr;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDevice_name() {
        return device_name;
    }
    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }
    public String getNum() {
        return num;
    }
    public void setNum(String num) {
        this.num = num;
    }
    public static List<DemoInfo> parse(String str){
        if(!TextUtils.isEmpty(str)){
            try {
                JSONObject json = new JSONObject(str);
                if(json != null){
                    return parse(json);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<DemoInfo> parse(JSONObject json){
        if(json == null)return null;
        List<DemoInfo> demos = new ArrayList<DemoInfo>();
        try{
            JSONArray array = json.optJSONArray("data");
            for(int i=0;i<array.length();i++){
                JSONObject ob = array.optJSONObject(i);
                DemoInfo demo = new DemoInfo();
                demo.setId(ob.optString("id"));
                demo.setDevice_name(ob.optString("device_name"));
                demo.setNum(ob.optString("num"));
                demo.setDeviceType(ob.optString("type"));
                if(ob.optInt("is_vr") == 1){
                    demo.setVr(true);
                }else {
                    demo.setVr(false);
                }
                demos.add(demo);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return demos;
    }
}
