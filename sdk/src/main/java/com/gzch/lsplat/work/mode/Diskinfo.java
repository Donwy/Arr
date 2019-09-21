package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by dk on 2018/5/18.
 */

public class Diskinfo implements Serializable {
    private int DiskPort;
    private int DiskStat;
    private double DiskTotalCap;
    private double DiskAvlCap;

    public double getDiskTotalCap() {
        return DiskTotalCap;
    }

    public void setDiskTotalCap(double diskTotalCap) {
        DiskTotalCap = diskTotalCap;
    }

    public double getDiskAvlCap() {
        return DiskAvlCap;
    }

    public void setDiskAvlCap(double diskAvlCap) {
        DiskAvlCap = diskAvlCap;
    }

    public int getDiskPort() {
        return DiskPort;
    }

    public void setDiskPort(int diskPort) {
        DiskPort = diskPort;
    }

    public int getDiskStat() {
        return DiskStat;
    }

    public void setDiskStat(int diskStat) {
        DiskStat = diskStat;
    }


    public static Diskinfo parse(String str) {
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

    public static Diskinfo parse(JSONObject json) {
        if (json == null) return null;
        Diskinfo diskinfo = new Diskinfo();
        diskinfo.setDiskPort(json.optInt("DiskPort"));
        diskinfo.setDiskStat(json.optInt("DiskStat"));
        diskinfo.setDiskTotalCap(json.optDouble("DiskTotalCap"));
        diskinfo.setDiskAvlCap(json.optDouble("DiskAvlCap"));
        return diskinfo;
    }

    @Override
    public String toString() {
        return "Diskinfo{" +
                "DiskPort=" + DiskPort +
                ", DiskStat=" + DiskStat +
                ", DiskTotalCap=" + DiskTotalCap +
                ", DiskAvlCap=" + DiskAvlCap +
                '}';
    }
}
