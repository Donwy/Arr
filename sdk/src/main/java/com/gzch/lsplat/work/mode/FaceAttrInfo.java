package com.gzch.lsplat.work.mode;

import org.json.JSONObject;

import java.io.Serializable;

public class FaceAttrInfo implements Serializable {

    private static final long serialVersionUID = 20190903L;
    // 允许文件夹最大存储空间
    public static final long MAX_STORAGE = 1024 * 1024 * 1024;

    public static final String FACE_CAPTURE_ATTR = "face_capture_attr";
    public static final String FACE_COMPARE_RESULT = "face_compare_result";

    /**
     * 抓拍信息
     * {"type":"face_capture_attr","capture_pic_name":"1000000000004_0_1567473668712_cap_capture.jpg",
     * "device_id":"1000000000004","channel_id":0,"age":32,"sex":1,"eyeocc":1,"mouthocc":1,"headwear":5,"noseocc":1,"beard":1}
     */

    /**
     * 比对信息
     * {"type":"face_compare_result","compare_lib_pic_name":"1000000000004_0_1567473673533_com_lib.jpg",
     * "compare_cap_pic_name":"1000000000004_0_1567473673533_com_capture.jpg",
     * "device_id":"1000000000004","channel_id":0,"name":"dabuliediandavid","similarity":80}
     */

    private String type;
    private String capturePicName;
    private String compareLibPicName;
    private String compareCapPicName;
    private String name;
    private int age;
    private int sex;
    // 眼睛戴东西 [无遮挡,普通眼镜,太阳镜,其他]
    private int eyeocc;
    // 嘴上戴东西,[无遮挡,面具,口罩,其他]
    private int mouththocc;
    // 头上戴东西,[帽子,少量头发,短发,长发,其他]
    private int headwear;
    // 鼻子遮挡,[无遮挡,面具,口罩,其他]
    private int noseocc;
    // 胡子的状态,[没胡子,嘴唇上面的胡子,络腮胡,其他]
    private int beard;
    // 相似度
    private int similarity;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCapturePicName() {
        return capturePicName;
    }

    public void setCapturePicName(String capturePicName) {
        this.capturePicName = capturePicName;
    }

    public String getCompareLibPicName() {
        return compareLibPicName;
    }

    public void setCompareLibPicName(String compareLibPicName) {
        this.compareLibPicName = compareLibPicName;
    }

    public String getCompareCapPicName() {
        return compareCapPicName;
    }

    public void setCompareCapPicName(String compareCapPicName) {
        this.compareCapPicName = compareCapPicName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getEyeocc() {
        return eyeocc;
    }

    public void setEyeocc(int eyeocc) {
        this.eyeocc = eyeocc;
    }

    public int getMouththocc() {
        return mouththocc;
    }

    public void setMouththocc(int mouththocc) {
        this.mouththocc = mouththocc;
    }

    public int getHeadwear() {
        return headwear;
    }

    public void setHeadwear(int headwear) {
        this.headwear = headwear;
    }

    public int getNoseocc() {
        return noseocc;
    }

    public void setNoseocc(int noseocc) {
        this.noseocc = noseocc;
    }

    public int getBeard() {
        return beard;
    }

    public void setBeard(int beard) {
        this.beard = beard;
    }

    public int getSimilarity() {
        return similarity;
    }

    public void setSimilarity(int similarity) {
        this.similarity = similarity;
    }

    public static FaceAttrInfo prease(JSONObject jsonObject) {
        if (jsonObject == null) return null;
        FaceAttrInfo faceAttrInfo = new FaceAttrInfo();
        faceAttrInfo.setType(jsonObject.optString("type"));
        faceAttrInfo.setCapturePicName(jsonObject.optString("capture_pic_name"));
        faceAttrInfo.setCompareLibPicName(jsonObject.optString("compare_lib_pic_name"));
        faceAttrInfo.setCompareCapPicName(jsonObject.optString("compare_cap_pic_name"));
        faceAttrInfo.setName(jsonObject.optString("name"));
        faceAttrInfo.setAge(jsonObject.optInt("age"));
        faceAttrInfo.setSex(jsonObject.optInt("sex"));
        faceAttrInfo.setEyeocc(jsonObject.optInt("eyeocc"));
        faceAttrInfo.setMouththocc(jsonObject.optInt("mouthocc"));
        faceAttrInfo.setHeadwear(jsonObject.optInt("headwear"));
        faceAttrInfo.setNoseocc(jsonObject.optInt("noseocc"));
        faceAttrInfo.setBeard(jsonObject.optInt("beard"));
        faceAttrInfo.setSimilarity(jsonObject.optInt("similarity"));
        return faceAttrInfo;
    }

    @Override
    public String toString() {
        return "FaceAttrInfo{" +
                "type='" + type + '\'' +
                ", capturePicName='" + capturePicName + '\'' +
                ", compareLibPicName='" + compareLibPicName + '\'' +
                ", compareCapPicName='" + compareCapPicName + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", eyeocc=" + eyeocc +
                ", mouththocc=" + mouththocc +
                ", headwear=" + headwear +
                ", noseocc=" + noseocc +
                ", beard=" + beard +
                ", similarity=" + similarity +
                '}';
    }
}
