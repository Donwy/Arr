package com.gzch.lsplat.work.mode;

/**
 * Created by lw on 2018/6/8.
 */

public class Flow {
    /**
     * "UidRxBytes integer ," +    //当前软件接收的字节数
     "UidTxBytes integer ," +    //当前软件发送的字节数
     "wifi_RxBytes integer ," +     //WiFi接收的字节数
     "wifi_TxBytes integer ," +     //WiFi发送的字节数
     "mobile_RxBytes integer ," +   //手机流量接收的字节数
     "mobile_TxBytes integer ," +   //手机流量发送的字节数
     "max_Bytes integer ," +   //需要提示的临界值
     "tips_size integer ," +   //提示次数，3次之后不提示（每次点击取消后+1，点击确认后清零）
     "time_date integer ," +         //当前记录的时间(当天零点的时间戳)
     "time integer)";                 //最后一次记录的时间戳
     */
    private long uidRxBytes;
    private long uidTxBytes;
    private long wifi_RxBytes;
    private long wifi_TxBytes;
    private long mobile_RxBytes;
    private long mobile_TxBytes;
    private long time_date;
    private long time;
    private long max_Bytes;
    private int tips_size;

    public long getUidRxBytes() {
        return uidRxBytes;
    }

    public void setUidRxBytes(long uidRxBytes) {
        this.uidRxBytes = uidRxBytes;
    }

    public long getUidTxBytes() {
        return uidTxBytes;
    }

    public void setUidTxBytes(long uidTxBytes) {
        this.uidTxBytes = uidTxBytes;
    }

    public long getWifi_RxBytes() {
        return wifi_RxBytes;
    }

    public void setWifi_RxBytes(long wifi_RxBytes) {
        this.wifi_RxBytes = wifi_RxBytes;
    }

    public long getWifi_TxBytes() {
        return wifi_TxBytes;
    }

    public void setWifi_TxBytes(long wifi_TxBytes) {
        this.wifi_TxBytes = wifi_TxBytes;
    }

    public long getMobile_RxBytes() {
        return mobile_RxBytes;
    }

    public void setMobile_RxBytes(long mobile_RxBytes) {
        this.mobile_RxBytes = mobile_RxBytes;
    }

    public long getMobile_TxBytes() {
        return mobile_TxBytes;
    }

    public void setMobile_TxBytes(long mobile_TxBytes) {
        this.mobile_TxBytes = mobile_TxBytes;
    }

    public long getTime_date() {
        return time_date;
    }

    public void setTime_date(long time_date) {
        this.time_date = time_date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getMax_Bytes() {
        return max_Bytes;
    }

    public void setMax_Bytes(long max_Bytes) {
        this.max_Bytes = max_Bytes;
    }

    public int getTips_size() {
        return tips_size;
    }

    public void setTips_size(int tips_size) {
        this.tips_size = tips_size;
    }

    @Override
    public String toString() {
        return "Flow{" +
                "uidRxBytes=" + uidRxBytes +
                ", uidTxBytes=" + uidTxBytes +
                ", wifi_RxBytes=" + wifi_RxBytes +
                ", wifi_TxBytes=" + wifi_TxBytes +
                ", mobile_RxBytes=" + mobile_RxBytes +
                ", mobile_TxBytes=" + mobile_TxBytes +
                ", time_date=" + time_date +
                ", time=" + time +
                ", max_Bytes=" + max_Bytes +
                ", tips_size=" + tips_size +
                '}';
    }
}
