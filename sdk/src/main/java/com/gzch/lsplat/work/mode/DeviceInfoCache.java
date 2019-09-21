package com.gzch.lsplat.work.mode;

import org.json.JSONObject;

import java.lang.ref.SoftReference;

/**
 * Created by lw on 2019/7/8.
 */

public class DeviceInfoCache {

    /**
     * 获取到的设备信息
     */
    private SoftReference<JSONObject> data;

    /**
     * 信息获取时间
     */
    private long time;

    public JSONObject getData() {
        if (data != null){
            return data.get();
        }
        return null;
    }

    public void setData(JSONObject device) {
        if (device != null){
            data = new SoftReference<JSONObject>(device);
        }
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    /**
     * 设备信息 有效期 5 分钟，超时后重新请求
     * @return
     */
    public boolean checkEqupInfo(){
        if (getData() == null)return false;
        if ((System.currentTimeMillis() - time) > 5 * 60 * 1000L){
            return false;
        }
        return true;
    }
}
