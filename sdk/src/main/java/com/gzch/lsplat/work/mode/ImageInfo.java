package com.gzch.lsplat.work.mode;

import java.io.Serializable;

public class ImageInfo implements Serializable {

    private static final long serialVersionUID = 2L;

    /**
     * "path text ," +
     "name text ," +
     "time text ," +
     "device_id text ," +
     "device_channel text ," +
     "playMode integer ," + //类型 实时预览 ， 回放  ，VR 等,对应 EqupInfo#playMode
     "type integer ," +  // 1 : img , 2 video
     "userId text)"
     */

    public static final int IMG = 1;
    public static final int VIDEO = 2;

    private int tag;  //type

    private int playMode;

    private String currUser;//userId

    private String dId;//device_id

    private String dName;//device_name

    private String fileName;//file_name

    private int playerId;

    private String path;

    private long time;

    private String channel;

    private int x;

    private int y;

    private int width;

    private int height;

    private boolean shouldInsertDB = false;//是否需要保存数据库

    private String freeipName;//转移freeip数据时添加的临时参数

    private String imgPath;

    private String h264Path;//视频文件时才有该参数

    private String mp4Path;//视频文件时才有该参数

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getCurrUser() {
        return currUser;
    }

    public void setCurrUser(String currUser) {
        this.currUser = currUser;
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isShouldInsertDB() {
        return shouldInsertDB;
    }

    public void setShouldInsertDB(boolean shouldInsertDB) {
        this.shouldInsertDB = shouldInsertDB;
    }

    public String getFreeipName() {
        return freeipName;
    }

    public void setFreeipName(String freeipName) {
        this.freeipName = freeipName;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getH264Path() {
        return h264Path;
    }

    public void setH264Path(String h264Path) {
        this.h264Path = h264Path;
    }

    public String getMp4Path() {
        return mp4Path;
    }

    public void setMp4Path(String mp4Path) {
        this.mp4Path = mp4Path;
    }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "tag=" + tag +
                ", playMode=" + playMode +
                ", currUser='" + currUser + '\'' +
                ", dId='" + dId + '\'' +
                ", dName='" + dName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", playerId=" + playerId +
                ", path='" + path + '\'' +
                ", time=" + time +
                ", channel='" + channel + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", shouldInsertDB=" + shouldInsertDB +
                ", freeipName='" + freeipName + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", h264Path='" + h264Path + '\'' +
                ", mp4Path='" + mp4Path + '\'' +
                '}';
    }
}
