package com.gzch.lsplat.work.mode.event;

import com.gzch.lsplat.work.mode.OrderItem;

import java.util.List;

/**
 * Created by LY on 2018/3/5.
 */

public class OrderItemEvent {
    /**
     * web请求 结果码 code值
     */
    private int resultCode;

    /**
     * 请求错误提示
     */
    private String errMsg;

    /**
     * 请求的命令
     */
    private int cmd;

    /**
     * 到期时间
     */
    private String mTimeInfo;

    /**
     * 0  免费未领取; 免费已经被领取。
     */
    private String mIsActive;

    private List<OrderItem> orderItems;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(String timeInfo, String isActive, List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        this.mTimeInfo = timeInfo;
        this.mIsActive = isActive;

    }

    public String getmTimeInfo() {
        return mTimeInfo;
    }

    public String getmIsActive() {
        return mIsActive;
    }

    @Override
    public String toString() {
        return "OrderItemEvent{" +
                "resultCode=" + resultCode +
                ", errMsg='" + errMsg + '\'' +
                ", cmd=" + cmd +
                ", mTimeInfo='" + mTimeInfo + '\'' +
                ", mIsActive='" + mIsActive + '\'' +
                ", orderItems=" + orderItems +
                '}';
    }
}
