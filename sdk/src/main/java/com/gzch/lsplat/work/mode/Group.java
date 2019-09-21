package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lw on 2017/12/26.
 */

public class Group implements Comparable<Group>, Serializable {

    /**
     * 分组Id
     */
    private String groupId;

    /**
     * 分组名
     */
    private String groupName;

    /**
     * 分组排序位置
     */
    private String groupPosition;

    private boolean isLine = false;

    private List<EqupInfo> devices;

    public boolean isLine() {
        return isLine;
    }

    public void setLine(boolean line) {
        isLine = line;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupPosition() {
        return groupPosition;
    }

    public void setGroupPosition(String groupPosition) {
        this.groupPosition = groupPosition;
    }

    public List<EqupInfo> getDevices() {
        return devices;
    }

    public void setDevices(List<EqupInfo> devices) {
        this.devices = devices;
    }

    public static Group parse(String str){
        if (!TextUtils.isEmpty(str)){
            try {
                JSONObject jsonObject = new JSONObject(str);
                return parse(jsonObject);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Group parse(JSONObject jsonObject){
        if (jsonObject == null)return null;
        Group group = new Group();
        group.setGroupId(jsonObject.optString("cate_id"));
        group.setGroupName(jsonObject.optString("cate_name"));
        group.setGroupPosition(jsonObject.optString("order_num"));
        return group;
    }

    @Override
    public int compareTo(@NonNull Group o) {
        if (o == null || this == o) return 0;
        String position1 = o.getGroupPosition();
        String position2 = getGroupPosition();
        if (TextUtils.isEmpty(position1) || TextUtils.isEmpty(position2)) {
            return 0;
        }
        try {
            int p1 = Integer.valueOf(position1);
            int p2 = Integer.valueOf(position2);
            if (p1 < p2) {
                return 1;
            } else if (p1 > p2) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Group{" +
//                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
//                ", groupPosition='" + groupPosition + '\'' +
                ", devices=" + devices +
                '}';
    }
}
