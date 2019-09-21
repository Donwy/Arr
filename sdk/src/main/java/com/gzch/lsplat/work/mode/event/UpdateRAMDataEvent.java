package com.gzch.lsplat.work.mode.event;

/**
 * Created by lw on 2018/8/7.
 */

import android.text.TextUtils;

import com.gzch.lsplat.work.mode.EqupInfo;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

/**
 * 修改已存在的播放数据
 */
public class UpdateRAMDataEvent {

    /**
     * 设备被删除
     * 需要从播放列表中删除
     */
    private static final int DEVICE_DELETE = 176;

    /**
     * 修改了某个通道回放设置
     */
    private static final int PLAYBACK_PARAM_CHANGED_REPLAY_SETTING = 848;

    /**
     * 修改了设备密码
     */
    private static final int DEVICE_PASSWORD_CHANGED = 819;

    /**
     * 直连设备删除
     */
    private static final int DIRECT_DEVICE_DELETE = 757;

    /**
     * 直连修改密码
     */
    private static final int DIRECT_DEVICE_UPDATE_PASSWORD = 758;

    private int updateType;

    /**
     * 修改的数据
     */
    private String changedParams;

    public boolean needNotify(){
        return updateType != DEVICE_PASSWORD_CHANGED;
    }

    /**
     * 设备被解绑删除了
     * @param deviceId : 序列号
     * @return
     */
    public static UpdateRAMDataEvent deviceDelete(String deviceId){
        if (TextUtils.isEmpty(deviceId))return null;
        UpdateRAMDataEvent updateRAMDataEvent = new UpdateRAMDataEvent();
        updateRAMDataEvent.updateType = DEVICE_DELETE;
        updateRAMDataEvent.changedParams = "{\"device_id\":\"" + deviceId + "\"}";
        return updateRAMDataEvent;
    }

    /**
     * 修改了某个通道回放码流类型
     * @return
     */
    public static UpdateRAMDataEvent playbackChangedReplaySetting(String params){
        if (TextUtils.isEmpty(params))return null;
        UpdateRAMDataEvent updateRAMDataEvent = new UpdateRAMDataEvent();
        updateRAMDataEvent.updateType = PLAYBACK_PARAM_CHANGED_REPLAY_SETTING;
        updateRAMDataEvent.changedParams = params;
        return updateRAMDataEvent;
    }

    /**
     * 修改设备密码
     * @param params
     * @return
     */
    public static UpdateRAMDataEvent devicePasswordChanged(String params){
        if (TextUtils.isEmpty(params))return null;
        UpdateRAMDataEvent updateRAMDataEvent = new UpdateRAMDataEvent();
        updateRAMDataEvent.updateType = DEVICE_PASSWORD_CHANGED;
        updateRAMDataEvent.changedParams = params;
        return updateRAMDataEvent;
    }

    public static UpdateRAMDataEvent directDeviceDelete(String params){
        if (TextUtils.isEmpty(params))return null;
        UpdateRAMDataEvent updateRAMDataEvent = new UpdateRAMDataEvent();
        updateRAMDataEvent.updateType = DIRECT_DEVICE_DELETE;
        updateRAMDataEvent.changedParams = params;
        return updateRAMDataEvent;
    }

    public static UpdateRAMDataEvent directDeviceUpdatePassword(String params){
        if (TextUtils.isEmpty(params))return null;
        UpdateRAMDataEvent updateRAMDataEvent = new UpdateRAMDataEvent();
        updateRAMDataEvent.updateType = DIRECT_DEVICE_UPDATE_PASSWORD;
        updateRAMDataEvent.changedParams = params;
        return updateRAMDataEvent;
    }

    public static EqupInfo changeEqupInfo(EqupInfo device, UpdateRAMDataEvent params){
        if (device == null)return null;
        if (params == null || TextUtils.isEmpty(params.changedParams))return device;
        try {
            JSONObject jsonObject = new JSONObject(params.changedParams);
            if (jsonObject == null)return device;
            String deviceId = jsonObject.optString("device_id");
            int channel_id = jsonObject.optInt("channel_id",-1);
            int data_rate = jsonObject.optInt("data_rate",-1);
            int video_type = jsonObject.optInt("video_type",-1);
            String local_user = jsonObject.optString("local_user","");
            String local_pwd = jsonObject.optString("local_pwd","");
            String direct_url = jsonObject.optString("direct_url","");
            String new_direct_url = jsonObject.optString("new_direct_url","");
            if (TextUtils.isEmpty(deviceId) && TextUtils.isEmpty(direct_url))return null;

            if (params.updateType == DIRECT_DEVICE_DELETE){
                if (direct_url.equals(device.getDeviceConnectServer())){
                    return null;
                }
            }

            if (params.updateType == DIRECT_DEVICE_UPDATE_PASSWORD){
                if (direct_url.equals(device.getDeviceConnectServer())){
                    if (!TextUtils.isEmpty(local_user) && !TextUtils.isEmpty(local_pwd)){
                        if (!TextUtils.isEmpty(new_direct_url)){
                            device.setDeviceConnectServer(new_direct_url);
                        }
                        device.setLocalUser(local_user);
                        device.setLocalPwd(local_pwd);
                    }
                    return null;
                }
            }

            if (!deviceId.equals(device.getEqupId()))return device;
            if (channel_id != -1){
                if (channel_id != device.getAbsMode()){
                    return device;
                }
            }
            switch (params.updateType){
                case DEVICE_DELETE:
                    return null;
                case PLAYBACK_PARAM_CHANGED_REPLAY_SETTING:
                    if (data_rate != -1){
                        device.setReplay_data_rate(data_rate);
                    }
                    if (video_type != -1){
                        device.setReplay_video_type(video_type);
                    }
                    break;
                case DEVICE_PASSWORD_CHANGED:
                    if (!TextUtils.isEmpty(local_user) && !TextUtils.isEmpty(local_pwd)){
                        device.setLocalUser(local_user);
                        device.setLocalPwd(local_pwd);
                    }
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
            return device;
        }
        return device;
    }

    public static List<EqupInfo> changeEqupInfo(List<EqupInfo> devices, UpdateRAMDataEvent params){
        if (devices == null)return null;
        if (params == null || TextUtils.isEmpty(params.changedParams))return devices;
        try {
            JSONObject jsonObject = new JSONObject(params.changedParams);
            if (jsonObject == null)return devices;
            String deviceId = jsonObject.optString("device_id");
            int channel_id = -1;
            if (params.toString().contains("channel")) {
                channel_id = jsonObject.optInt("channel",-1);
            } else {
                channel_id = jsonObject.optInt("channel_id",-1);
            }
            int data_rate = jsonObject.optInt("data_rate",-1);
            int video_type = jsonObject.optInt("video_type",-1);
            String local_user = jsonObject.optString("local_user","");
            String local_pwd = jsonObject.optString("local_pwd","");
            String direct_url = jsonObject.optString("direct_url","");
            String new_direct_url = jsonObject.optString("new_direct_url","");
            if (TextUtils.isEmpty(deviceId) && TextUtils.isEmpty(direct_url))return devices;
            Iterator<EqupInfo> infoIterator = devices.iterator();
            EqupInfo device = null;
            while (infoIterator.hasNext()){
                device = infoIterator.next();
                if (params.updateType == DIRECT_DEVICE_DELETE){
                    if (direct_url.equals(device.getDeviceConnectServer())){
                        devices.remove(device);
                        continue;
                    }
                }
                if (params.updateType == DIRECT_DEVICE_UPDATE_PASSWORD){
                    if (direct_url.equals(device.getDeviceConnectServer())){
                        if (!TextUtils.isEmpty(local_user) && !TextUtils.isEmpty(local_pwd)){
                            if (!TextUtils.isEmpty(new_direct_url)){
                                device.setDeviceConnectServer(new_direct_url);
                            }
                            device.setLocalUser(local_user);
                            device.setLocalPwd(local_pwd);
                            continue;
                        }
                    }
                }
                if (!deviceId.equals(device.getEqupId())){
                    continue;
                }
                if (channel_id != -1){
                    if (channel_id != device.getAbsMode()){
                        continue;
                    }
                }
                switch (params.updateType){
                    case DEVICE_DELETE:
                        devices.remove(device);
                        break;
                    case PLAYBACK_PARAM_CHANGED_REPLAY_SETTING:
                        if (data_rate != -1){
                            device.setReplay_data_rate(data_rate);
                        }
                        if (video_type != -1){
                            device.setReplay_video_type(video_type);
                        }
                        break;
                    case DEVICE_PASSWORD_CHANGED:
                        if (!TextUtils.isEmpty(local_user) && !TextUtils.isEmpty(local_pwd)){
                            device.setLocalUser(local_user);
                            device.setLocalPwd(local_pwd);
                        }
                        break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            return devices;
        }
        return devices;
    }

    @Override
    public String toString() {
        return "UpdateRAMDataEvent{" +
                "updateType=" + updateType +
                ", changedParams='" + changedParams + '\'' +
                '}';
    }
}
