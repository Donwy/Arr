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

public class DeviceStream implements Serializable {
    private List<String> resolution;
    private int now_resolution;
    private int quality;
    private FrameRate frame_rate;

    public static DeviceStream parse(String str) {
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

    public static DeviceStream parse(JSONObject json) {
        if (json == null) return null;
        DeviceStream deviceStream=new DeviceStream();
        JSONArray jsonArray = json.optJSONArray("resolution");
        List<String> strings = new ArrayList();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    strings.add((String) (jsonArray.get(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        deviceStream.setResolution(strings);
        deviceStream.setNow_resolution(json.optInt("now_resolution"));
        if (json.has("quality")){
            deviceStream.setQuality(json.optInt("quality"));
        }
        try {
            deviceStream.setFrame_rate(FrameRate.parse(json.getJSONObject("frame_rate")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return deviceStream;
    }

    public List<String> getResolution() {
        return resolution;
    }

    public void setResolution(List<String> resolution) {
        this.resolution = resolution;
    }

    public int getNow_resolution() {
        return now_resolution;
    }

    public void setNow_resolution(int now_resolution) {
        this.now_resolution = now_resolution;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public FrameRate getFrame_rate() {
        return frame_rate;
    }

    public void setFrame_rate(FrameRate frame_rate) {
        this.frame_rate = frame_rate;
    }

    @Override
    public String toString() {
        return "DeviceStream{" +
                "resolution=" + resolution +
                ", now_resolution=" + now_resolution +
                ", quality=" + quality +
                ", frame_rate=" + frame_rate +
                '}';
    }
}
