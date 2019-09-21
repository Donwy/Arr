package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by dk on 2018/6/25.
 */

public class FrameRate implements Serializable {
    private int totle_rate;
    private int now_rate;

    public static FrameRate parse(String str){
        if (TextUtils.isEmpty(str))return null;
        JSONObject jsonObject=null;
        try {
            jsonObject=new JSONObject(str);
            if (jsonObject!=null){
                return parse(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FrameRate parse(JSONObject json) {
        if (json==null)return null;
        FrameRate frameRate=new FrameRate();
        frameRate.setTotle_rate(json.optInt("totle_rate"));
        frameRate.setNow_rate(json.optInt("now_rate"));
        return frameRate;
    }

    public int getTotle_rate() {
        return totle_rate;
    }

    public void setTotle_rate(int totle_rate) {
        this.totle_rate = totle_rate;
    }

    public int getNow_rate() {
        return now_rate;
    }

    public void setNow_rate(int now_rate) {
        this.now_rate = now_rate;
    }

    @Override
    public String toString() {
        return "FrameRate{" +
                "totle_rate=" + totle_rate +
                ", now_rate=" + now_rate +
                '}';
    }
}
