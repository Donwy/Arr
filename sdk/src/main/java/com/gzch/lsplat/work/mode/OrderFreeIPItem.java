package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单列表
 * Created by LY on 2018/9/13.
 */

public class OrderFreeIPItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String salesPackageTitle;
    private String duration;//天数
    private List<CouldTypeBean> couldTypeUSDList;
    private List<CouldTypeBean> couldTypeCNYList;

    public static OrderFreeIPItem parse(String str){
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

    public static OrderFreeIPItem parse(JSONObject json){
        if(json == null) return null;
        OrderFreeIPItem item = new OrderFreeIPItem();
        List<CouldTypeBean> CNYlist = new ArrayList<>();
        List<CouldTypeBean> USDlist = new ArrayList<>();
        item.setSalesPackageTitle(json.optString("sales_package_title"));
        item.setDuration(json.optString("duration"));
        for (int i = 0; i < json.optJSONArray("CNY").length(); i++) {
            CouldTypeBean couldTypebean;
            try {
                couldTypebean = CouldTypeBean.parse(json.optJSONArray("CNY").get(i).toString());
                CNYlist.add(couldTypebean);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < json.optJSONArray("USD").length(); i++) {
            CouldTypeBean couldTypebean;
            try {
                couldTypebean = CouldTypeBean.parse(json.optJSONArray("USD").get(i).toString());
                USDlist.add(couldTypebean);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        item.setCouldTypeCNYList(CNYlist);
        item.setCouldTypeUSDList(USDlist);
        return item;
    }

    public String getSalesPackageTitle() {
        return salesPackageTitle;
    }

    public void setSalesPackageTitle(String salesPackageTitle) {
        this.salesPackageTitle = salesPackageTitle;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<CouldTypeBean> getCouldTypeUSDList() {
        return couldTypeUSDList;
    }

    public void setCouldTypeUSDList(List<CouldTypeBean> couldTypeUSDList) {
        this.couldTypeUSDList = couldTypeUSDList;
    }

    public List<CouldTypeBean> getCouldTypeCNYList() {
        return couldTypeCNYList;
    }

    public void setCouldTypeCNYList(List<CouldTypeBean> couldTypeCNYList) {
        this.couldTypeCNYList = couldTypeCNYList;
    }

    @Override
    public String toString() {
        return "OrderFreeIPItem{" +
                "salesPackageTitle='" + salesPackageTitle + '\'' +
                ", duration='" + duration + '\'' +
                ", couldTypeUSDList=" + couldTypeUSDList +
                ", couldTypeCNYList=" + couldTypeCNYList +
                '}';
    }
}
