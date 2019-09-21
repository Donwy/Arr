package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.longse.lsapc.lsacore.sapi.log.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by lw on 2017/6/8.
 */

public class ChannelInfoEntity implements Comparable<ChannelInfoEntity>, Serializable {

    private static final long serialVersionUID = 3L;

    private int dc_id;
    private String device_id;
    private String cate_id;
    private String channel_name;
    private int channel;
    private int order_num;
    private boolean alarm_open;
    private int replay_data_rate;  //回放的码流选择 0:子码流  1:主码流
    private int replay_video_type; //设备回放的录像 1:移动侦测录像 2:普通录像 3:定时录像 4:全部录像

    /**
     * 数据来源, 自动创建的，数据库缓存的，网络请求的
     */
    public static final int CREATE_AUTO = 1;
    public static final int DB_CACHE = 2;
    public static final int INTENT_REQUEST = 3;
    private int date_link;

    /**
     * 数据获得的时间
     */
    private long data_link_time;

    private DevicePermisionParse permision;
    private DevicePermisionParse localePermision;
    private DevicePermisionParse remotePermision;

    /**
     * 分享权限
     */
    private String sharePermision;

    public String getSharePermision() {
        return sharePermision;
    }

    public void setSharePermision(String sharePermision) {
        this.sharePermision = sharePermision;
    }

    public DevicePermisionParse getPermision() {
        return permision;
    }

    public void setPermision(DevicePermisionParse permision) {
        this.permision = permision;
    }

    public DevicePermisionParse getLocalePermision() {
        return localePermision;
    }

    public void setLocalePermision(DevicePermisionParse localePermision) {
        this.localePermision = localePermision;
    }

    public DevicePermisionParse getRemotePermision() {
        return remotePermision;
    }

    public void setRemotePermision(DevicePermisionParse remotePermision) {
        this.remotePermision = remotePermision;
    }

    public boolean shouldNetRequest(){
        if (date_link == CREATE_AUTO || date_link == DB_CACHE){
            return true;
        }
        long now = System.currentTimeMillis();
        if (now > data_link_time){
            //超过30分钟，需要刷新数据
            return (now - data_link_time) > 1 * 30 * 60 * 1000;
        }
        return false;
    }

    public int getDate_link() {
        return date_link;
    }

    public void setDate_link(int date_link) {
        this.date_link = date_link;
    }

    public long getData_link_time() {
        return data_link_time;
    }

    public void setData_link_time(long data_link_time) {
        this.data_link_time = data_link_time;
    }

    public static ChannelInfoEntity parseLocaleSharedData(JSONObject object){
        return parse(object);
    }

    public static ChannelInfoEntity parse(String str){
        if(!TextUtils.isEmpty(str)){
            try{
                JSONObject jsonObject = new JSONObject(str);
                if(jsonObject != null){
                    return parse(jsonObject);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * dc_id : number          关系主键
     device_id : number      设备ID
     channel_name : string   通道备注名
     channel : number        通道号
     order_num : number      排序位置
     */
    public static ChannelInfoEntity parse(JSONObject jsonObject){
        if(jsonObject == null)return null;
        ChannelInfoEntity infoEntity = new ChannelInfoEntity();
        infoEntity.setDc_id(jsonObject.optInt("dc_id"));
        infoEntity.setDevice_id(jsonObject.optString("device_id"));
        infoEntity.setCate_id(jsonObject.optString("cate_id"));
        infoEntity.setChannel_name(jsonObject.optString("channel_name"));
        infoEntity.setChannel(jsonObject.optInt("channel"));
        infoEntity.setOrder_num(jsonObject.optInt("order_num"));
        infoEntity.setReplay_data_rate(jsonObject.optInt("replay_data_rate"));
        infoEntity.setReplay_video_type(jsonObject.optInt("replay_video_type"));
        infoEntity.setDate_link(ChannelInfoEntity.INTENT_REQUEST);
        infoEntity.setData_link_time(System.currentTimeMillis());

        infoEntity.setPermision(new DevicePermisionParse(jsonObject.optString("permision")));
        infoEntity.setLocalePermision(new DevicePermisionParse(jsonObject.optString("local_permision")));
        infoEntity.setRemotePermision(new DevicePermisionParse(jsonObject.optString("remote_permision")));
        infoEntity.setSharePermision(jsonObject.optString("share_permision"));
        KLog.getInstance().d("ChannelInfoEntity parse obj = %s",infoEntity).print();
        return infoEntity;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try{
            json.put("dc_id",dc_id);
            json.put("device_id",device_id);
            json.put("channel_name",channel_name);
            json.put("channel",channel);
            json.put("order_num",order_num);
            json.put("replay_data_rate",replay_data_rate);
            json.put("replay_video_type",replay_video_type);
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    public int getDc_id() {
        return dc_id;
    }

    public void setDc_id(int dc_id) {
        this.dc_id = dc_id;
    }

    public String getCate_id() {
        return cate_id;
    }

    public void setCate_id(String cate_id) {
        this.cate_id = cate_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getOrder_num() {
        return order_num;
    }

    public void setOrder_num(int order_num) {
        this.order_num = order_num;
    }

    public boolean getAlarm_open() {
        return alarm_open;
    }

    public void setAlarm_open(boolean alarm_open) {
        this.alarm_open = alarm_open;
    }

    public int getReplay_data_rate() {
        return replay_data_rate;
    }

    public void setReplay_data_rate(int replay_data_rate) {
        this.replay_data_rate = replay_data_rate;
    }

    public int getReplay_video_type() {
        return replay_video_type;
    }

    public void setReplay_video_type(int replay_video_type) {
        this.replay_video_type = replay_video_type;
    }

    @Override
    public int compareTo(@NonNull ChannelInfoEntity o) {
        if (o == null || this == o) return 0;
        int p1 = o.getOrder_num();
        int p2 = getOrder_num();
        if (p1 < p2) {
            return 1;
        } else if (p1 > p2) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "ChannelInfoEntity{" +
//                "dc_id=" + dc_id +
                ", device_id='" + device_id + '\'' +
                ", cate_id='" + cate_id + '\'' +
//                ", channel_name='" + channel_name + '\'' +
                ", channel=" + channel +
//                ", order_num=" + order_num +
//                ", alarm_open=" + alarm_open +
                ", replay_data_rate=" + replay_data_rate +
                ", replay_video_type=" + replay_video_type +
//                ", date_link=" + date_link +
//                ", data_link_time=" + data_link_time +
//                ", permision=" + permision +
//                ", localePermision=" + localePermision +
                ", remotePermision=" + remotePermision +
//                ", sharePermision='" + sharePermision + '\'' +
                '}';
    }
}
