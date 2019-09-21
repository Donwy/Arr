package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dk on 2018/6/25.
 */

public class StreamEnable implements Serializable {

    public static final long serialVersionUID = 1L;

    private List<String> exist_code_type; //支持编码类型 H264,H265
    private int now_code_type;            //当前的编码类型角标
    private boolean issupport;            //是否支持
    private boolean isopen;               //是否开启

    public static StreamEnable defaultStreamEnable(){
        StreamEnable streamEnable = new StreamEnable();
        streamEnable.exist_code_type = new ArrayList<>();
        streamEnable.issupport = false;
        streamEnable.isopen = false;
        return streamEnable;
    }

    public static StreamEnable parse(String str) {
        if (TextUtils.isEmpty(str)) return defaultStreamEnable();

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

    public static StreamEnable parse(JSONObject json) {
        if (json == null) return defaultStreamEnable();
        StreamEnable streamEnable = new StreamEnable();
        JSONArray jsonArray = json.optJSONArray("exist_code_type");
        List<String> strings = new ArrayList();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    strings.add(jsonArray.optString(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        streamEnable.setExist_code_type(strings);
        streamEnable.setNow_code_type(json.optInt("now_code_type"));
        streamEnable.setIssupport(json.optBoolean("issupport"));
        streamEnable.setIsopen(json.optBoolean("isopen"));
        return streamEnable;
    }

    public List<String> getExist_code_type() {
        return exist_code_type;
    }

    public void setExist_code_type(List<String> exist_code_type) {
        this.exist_code_type = exist_code_type;
    }

    public int getNow_code_type() {
        return now_code_type;
    }

    public void setNow_code_type(int now_code_type) {
        this.now_code_type = now_code_type;
    }

    public boolean isIssupport() {
        return issupport;
    }

    public void setIssupport(boolean issupport) {
        this.issupport = issupport;
    }

    public boolean isIsopen() {
        return isopen;
    }

    public void setIsopen(boolean isopen) {
        this.isopen = isopen;
    }

    @Override
    public String toString() {
        return "StreamEnable{" +
                "exist_code_type=" + exist_code_type +
                ", now_code_type=" + now_code_type +
                ", issupport=" + issupport +
                ", isopen=" + isopen +
                '}';
    }
}
