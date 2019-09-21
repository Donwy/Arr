package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import com.gzch.lsplat.work.WorkContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 订单列表
 * Created by LY on 2017/9/26.
 */

public class CouldTypeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String pfeId;
    private String pfcId;
    private String pftId;
    private String price;
    private String duration;
    private String canBeGift;
    private String lifeTime;  //保存时间
    private String currency; //货币类型
    private String title;    //套餐类型
    private String pfspId;    //套餐

    public static CouldTypeBean parse(String str){
        if(TextUtils.isEmpty(str)) return null;
        try {
            JSONObject json = new JSONObject(str);
            if(json != null){
                return parse(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CouldTypeBean parse(JSONObject json){
        if(json == null) return null;
        CouldTypeBean item = new CouldTypeBean();
        item.setPfeId(json.optString("pfe_id"));
        item.setPfcId(json.optString("pfc_id"));
        item.setPftId(json.optString("pft_id"));
        item.setPrice(json.optString("price"));
        item.setDuration(json.optString("duration"));
        item.setCanBeGift(json.optString("can_be_gift"));
        item.setCurrency(json.optString("currency"));
        if (WorkContext.CONTEXT_APP == WorkContext.BITDOG_APP) {
            item.setTitle(json.optString("sales_package_title"));
            item.setPfspId(json.optString("pfsp_id"));
            item.setLifeTime(json.optString("life_time"));
        } else {
            item.setTitle(json.optString("title"));
        }


        return item;
    }

    public String getPfspId() {
        return pfspId;
    }

    public void setPfspId(String pfspId) {
        this.pfspId = pfspId;
    }

    public String getPfeId() {
        return pfeId;
    }

    public void setPfeId(String pfeId) {
        this.pfeId = pfeId;
    }

    public String getCanBeGift() {
        return canBeGift;
    }

    public void setCanBeGift(String canBeGift) {
        this.canBeGift = canBeGift;
    }

    public String getPfcId() {
        return pfcId;
    }

    public void setPfcId(String pfcId) {
        this.pfcId = pfcId;
    }

    public String getPftId() {
        return pftId;
    }

    public void setPftId(String pftId) {
        this.pftId = pftId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(String lifeTime) {
        this.lifeTime = lifeTime;
    }

    @Override
    public String toString() {
        return "CouldTypeBean{" +
                "pfeId='" + pfeId + '\'' +
                ", pfcId='" + pfcId + '\'' +
                ", pftId='" + pftId + '\'' +
                ", price='" + price + '\'' +
                ", duration='" + duration + '\'' +
                ", canBeGift='" + canBeGift + '\'' +
                ", lifeTime='" + lifeTime + '\'' +
                ", currency='" + currency + '\'' +
                ", title='" + title + '\'' +
                ", pfspId='" + pfspId + '\'' +
                '}';
    }

    public String toJson(CouldTypeBean couldTypeBean) {
        JSONObject object = new JSONObject();

        try {
            object.put("pfe_id", couldTypeBean.getPfeId());
            object.put("pfc_id", couldTypeBean.getPfcId());
            object.put("pft_id", couldTypeBean.getPftId());
            object.put("price", couldTypeBean.getPrice());
            object.put("life_time", couldTypeBean.getLifeTime());
            object.put("duration", couldTypeBean.getDuration());
            object.put("can_be_gift", couldTypeBean.getCanBeGift());
            object.put("currency", couldTypeBean.getCurrency());
            object.put("sales_package_title", couldTypeBean.getTitle());
            object.put("pfsp_id", couldTypeBean.getPfspId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
