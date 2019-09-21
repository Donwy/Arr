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

public class Motdata implements Serializable {
    private List<AlarmTime> time;

    public List<AlarmTime> getTime() {
        return time;
    }

    public void setTime(List<AlarmTime> time) {
        this.time = time;
    }

    public static Motdata parse(String str) {
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

    public static Motdata parse(JSONObject json) {
        if (json == null) return null;
        Motdata motdata = new Motdata();
        JSONArray mTime = json.optJSONArray("time");
        List<AlarmTime> alarmTimeList = new ArrayList();
        if (mTime != null) {
            for (int i = 0; i < mTime.length(); i++) {
                try {
                    alarmTimeList.add(AlarmTime.parse(mTime.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        motdata.setTime(alarmTimeList);
        return motdata;
    }

    @Override
    public String toString() {
        return "Motdata{" +
                "time=" + time +
                '}';
    }

    public static class AlarmTime implements Serializable {
        private String motst;
        private String motet;

        public String getMotst() {
            return motst;
        }

        public void setMotst(String motst) {
            String[] times = motst.split(":");
            String hour = times[0];
            String minute = times[1];
            if (hour.length() == 1) {
                hour = "0" + hour;
            }
            if (minute.length() == 1) {
                minute = "0" + minute;
            }
            this.motst = hour + ":" + minute;
        }

        public String getMotet() {
            return motet;
        }

        public void setMotet(String motet) {
            String[] times = motet.split(":");
            String hour = times[0];
            String minute = times[1];
            if (hour.length() == 1) {
                hour = "0" + hour;
            }
            if (minute.length() == 1) {
                minute = "0" + minute;
            }
            this.motet = hour + ":" + minute;
        }

        public AlarmTime parse(String str) {
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

        public static AlarmTime parse(JSONObject json) {
            if (json == null) return null;
            AlarmTime alarmTime = new AlarmTime();
            alarmTime.setMotst(json.optString("motst"));
            alarmTime.setMotet(json.optString("motet"));
            return alarmTime;
        }

        @Override
        public String toString() {
            return "AlarmTime{" +
                    "motst='" + motst + '\'' +
                    ", motet='" + motet + '\'' +
                    '}';
        }
    }
}
