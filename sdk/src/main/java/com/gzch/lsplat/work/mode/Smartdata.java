package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dk on 2018/5/21.
 */

public class Smartdata implements Serializable {
    private List<SmartTime> time;

    public List<SmartTime> getTime() {
        return time;
    }

    public void setTime(List<SmartTime> time) {
        this.time = time;
    }
    public static Smartdata parse(String str) {
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

    public static Smartdata parse(JSONObject json) {
        if (json == null) return null;
        Smartdata smartdata = new Smartdata();
        JSONArray mTime = json.optJSONArray("time");
        List<SmartTime> smartTimeList = new ArrayList();
        if (mTime != null) {
            for (int i = 0; i < mTime.length(); i++) {
                try {
                    smartTimeList.add(SmartTime.parse(mTime.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        smartdata.setTime(smartTimeList);
        return smartdata;
    }

    @Override
    public String toString() {
        return "Smartdata{" +
                "time=" + time +
                '}';
    }

    public static class SmartTime implements Serializable {
        private String smartst;
        private String smartet;

        public String getSmartst() {
            return smartst;
        }

        public void setSmartst(String smartst) {
            String[] times = smartst.split(":");
            String hour = times[0];
            String minute = times[1];
            if (hour.length() == 1) {
                hour = "0" + hour;
            }
            if (minute.length() == 1) {
                minute = "0" + minute;
            }
            this.smartst = hour + ":" + minute;
        }

        public String getSmartet() {
            return smartet;
        }

        public void setSmartet(String smartet) {
            String[] times = smartet.split(":");
            String hour = times[0];
            String minute = times[1];
            if (hour.length() == 1) {
                hour = "0" + hour;
            }
            if (minute.length() == 1) {
                minute = "0" + minute;
            }
            this.smartet = hour + ":" + minute;
        }
        public SmartTime parse(String str) {
            if (TextUtils.isEmpty(str)) return null;

            JSONObject json = null;
            try {
                json = new JSONObject(str);
                return parse(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static SmartTime parse(JSONObject json) {
            if (json == null) return null;
            SmartTime smartTime = new SmartTime();
            smartTime.setSmartst(json.optString("smartst"));
            smartTime.setSmartet(json.optString("smartet"));
            return smartTime;
        }

        @Override
        public String toString() {
            return "SmartTime{" +
                    "smartst='" + smartst + '\'' +
                    ", smartet='" + smartet + '\'' +
                    '}';
        }
    }
}
