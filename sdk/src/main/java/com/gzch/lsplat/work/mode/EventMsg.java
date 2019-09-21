package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by ly on 2018/3/5.
 */

public class EventMsg implements Serializable {

    public final static long serialVersionUID = 3L;

    private String am_id = ""; // int         报警的主键
    private String device_id = ""; // int      设备序列号
    private String pic = ""; // string         报警图片地址
    private String create_time = "" ; // int    报警时间戳（单位秒）
    private String device_name = ""; // string    设备备注名
    private String device_model = ""; // string    设备类型
    private String type = "";  //string   报警类型 2、红外  3、烟感   2、3以外是移动侦测

    //时间标题时间
    private String titleTime;

    /**
     * 排序类型
     */
    public static final int EVENT_MSG_DATE_TITLE = 976;//时间标题
    public static final int EVENT_MSG_CONTENT = 117;//消息内容
    private int sortType = EVENT_MSG_CONTENT;//消息类型

    public static EventMsg parse(String str){
        if (TextUtils.isEmpty(str))return null;
        try {
            JSONObject json = new JSONObject(str);
            return parse(json);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static EventMsg parse(JSONObject json){
        if (json == null)return null;
        EventMsg msg = new EventMsg();
        msg.setAm_id(json.optString("am_id"));
        msg.setDevice_id(json.optString("device_id"));
        msg.setPic(json.optString("pic"));
        msg.setCreate_time(json.optString("create_time") + "000");
        msg.setDevice_name(json.optString("device_name"));
        msg.setDevice_model(json.optString("device_model"));
        msg.setType(json.optString("type"));
        return msg;
    }

    public String getTitleTime() {
        return titleTime;
    }

    public void setTitleTime(String titleTime) {
        this.titleTime = titleTime;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    /**
     * 返回该条消息当天最大的时间戳
     * @return
     */
    public String getDateTimeString(){
        if (TextUtils.isEmpty(create_time))return "";
        try {
            long time = Long.valueOf(create_time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
//            calendar.set(Calendar.MILLISECOND,999);
            return String.valueOf((calendar.getTimeInMillis() / 1000));
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public String getAm_id() {
        return am_id;
    }

    public void setAm_id(String am_id) {
        this.am_id = am_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getComparaTime(){
        if (sortType == EVENT_MSG_DATE_TITLE){
            return titleTime;
        }
        return create_time;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "EventMsg{" +
                "am_id='" + am_id + '\'' +
                ", device_id='" + device_id + '\'' +
                ", pic='" + pic + '\'' +
                ", create_time='" + create_time + '\'' +
                ", device_name='" + device_name + '\'' +
                ", device_model='" + device_model + '\'' +
                ", type='" + type + '\'' +
                ", sortType=" + sortType +
                '}';
    }
}
