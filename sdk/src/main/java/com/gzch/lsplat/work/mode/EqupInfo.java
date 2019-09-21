package com.gzch.lsplat.work.mode;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.gzch.lsplat.work.WorkContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lw on 2017/12/25.
 */

public class EqupInfo implements Comparable<EqupInfo>, Serializable {

    private static final long serialVersionUID = 3L;

    /**
     * 播放模式
     */
    public static final int PRE_VIEW_MODE = 1 << 0;
    public static final int PLAY_BACK_MODE = 1 << 1;
    public static final int VR_MODE = 1 << 2;
    public static final int DIRECT_MODE = 1 << 3;
    public static final int DEMO_VR_MODE = 1 << 4;
    public static final int DEMO_COMMON_MODE = 1 << 5;
    public static final int SN_MODE = 1 << 6;
    public static final int SING_SCREEN_MODE = 1 << 7;

    public static final int LOCALE_MODE = 1 << 8;//局域网播放

//    public static final int S_SCREEN_MODE = PLAY_BACK_MODE | SN_MODE;

    private String equpId; // 设备ID
    private String ifOnLine;// 是否在线
    private String ifBind;// 是否已被绑定
    private String equoModle;
    private String version;
    private String checkStr;
    private String deviceName;// 设备名
    private String deviceType;
    private String sysVersion;// 设备版本
    private String deviceDetatilType = ""; // 从设备端获取到的设备
    private String cateId = "";// 分组ID
    private String useCloudStorage = "";  // int  是否使用云存储 1：使用  0：没有
    private String canUseCloudStorage;  //是否能够使用云存储 1：可以  0：不可以

    private String cateName = "";
    private String deviceConnectServer;//播放地址，直连url完整地址

    private String localPwd = "";//设备密码
    private String localUser = "";//设备账号
    private List<ChannelInfoEntity> infoEntitys;//各通道信息
    private String order_num;//组内位置，用于排序

    private boolean isLocalePlayer = false;//本地播放
    private String localeDeviceIp = "";//局域网
    private int localeDevicePort = 0;
    private boolean isDirect = false;
    private String PrivateServer = "";
    private int mode = 0;
    private int deviceStreams = 0;
    private int port = 0;

    private boolean remotePlay = false;//云存储录像

    private String h264_plus;
    private String h265_plus;
    private String can_use_cloud_storage;
    private String use_cloud_storage;

    private long startTime;//回放开始时间
    private long endTime;//回放结束时间
    private long currentBuffStream = 0; // 回放当前缓冲时间

    //序列号 ， 收藏 的设备
    private boolean localeSaveDevice = false;

    private String channelName = "";//播放时显示

    /**
     * "replay_data_rate integer ," + //回放的码流选择 1:子码流  0:主码流
     * "replay_video_type integer ," + //设备回放的录像 1:移动侦测录像 2:普通录像 4:全部录像
     * "channel_name text ," + //播放时显示
     */
    private int replay_data_rate = 1;
    //此值围着兼容IPC设备，如果是IPC普通录像也传4
    private int replay_video_type = 0;

    /**
     * 播放模式
     */
    private int playMode = PRE_VIEW_MODE;

    /**
     * 实时预览时请求到的码流信息
     */
    private int fluency = 0;
    private int bd = 0;
    private int hd = 0;

    private int stream = -1;

    /**
     * 设备绑定关系id
     */
    private String dr_id;

    private List<String> cate_group;

    /**
     * 切换码流时的临时变量
     */
    private boolean isConnectStream = false;

    public boolean isConnectStream() {
        return isConnectStream;
    }

    public void setConnectStream(boolean connectStream) {
        isConnectStream = connectStream;
    }

    private DevicePermisionParse permision;
    private DevicePermisionParse localePermision;
    private DevicePermisionParse remotePermision;

    /**
     * 分享通道 和 权限
     * Integer: 通道
     * String: 权限
     */
    private Map<Integer, String> sharePermision;

    private DeviceNativeInfo deviceNativeInfo;

    private List<DevicePermisionParse> channelPermisionParses;

    /**
     * 本地保存的序列号设备
     */
    private int isSNLocaleDevice = 0;

    public int getIsSNLocaleDevice() {
        return isSNLocaleDevice;
    }

    public void setIsSNLocaleDevice(int isSNLocaleDevice) {
        this.isSNLocaleDevice = isSNLocaleDevice;
    }

    public boolean checkIsSNLocaleDevice(){
        return isSNLocaleDevice == 1;
    }

    /**
     * 选中时 对应通道的id
     */
    private int dc_id;

    public int getDc_id() {
        return dc_id;
    }

    public void setDc_id(int dc_id) {
        this.dc_id = dc_id;
    }

    public List<String> getCate_group() {
        return cate_group;
    }

    public void setCate_group(List<String> cate_group) {
        this.cate_group = cate_group;
    }

    /**
     * 上一次请求时间
     */
    private volatile long last_data_time;

    public long getLast_data_time() {
        return last_data_time;
    }

    public void setLast_data_time(long last_data_time) {
        this.last_data_time = last_data_time;
    }

    public List<DevicePermisionParse> getChannelPermisionParses() {
        return channelPermisionParses;
    }

    private boolean requestChannel = false;

    public boolean isRequestChannel() {
        return requestChannel;
    }

    public void setChannelPermisionParses(List<DevicePermisionParse> channelPermisionParses) {
        this.channelPermisionParses = channelPermisionParses;
    }

    public void setSharePermision(Map<Integer, String> sharePermision) {
        this.sharePermision = sharePermision;
    }

    public DevicePermisionParse getPermision() {
        if (permision == null) {
            return new DevicePermisionParse("");
        }
        return permision;
    }

    public void setPermision(DevicePermisionParse permision) {
        this.permision = permision;
    }

    public DevicePermisionParse getLocalePermision() {
        if (localePermision == null) {
            return new DevicePermisionParse("");
        }
        return localePermision;
    }

    public void setLocalePermision(DevicePermisionParse localePermision) {
        this.localePermision = localePermision;
    }

    public DevicePermisionParse getRemotePermision() {
        if (remotePermision == null) {
            return new DevicePermisionParse("");
        }
        return remotePermision;
    }

    public void setRemotePermision(DevicePermisionParse remotePermision) {
        this.remotePermision = remotePermision;
    }

    public Map<Integer, String> getSharePermision() {
        return sharePermision;
    }

    public DeviceNativeInfo getDeviceNativeInfo() {
        return deviceNativeInfo;
    }

    public void setDeviceNativeInfo(DeviceNativeInfo deviceNativeInfo) {
        this.deviceNativeInfo = deviceNativeInfo;
    }

    /**
     * 根据 播放 playMode 测试权限
     *
     * @return
     */
    public boolean checkPlayChannelSupportPermission() {
        if (isLocalePlayer()) return true;
        if ((playMode & EqupInfo.PRE_VIEW_MODE) != 0
                || (playMode & EqupInfo.VR_MODE) != 0) {
            return checkChannelSupportPermission(mode, DevicePermisionParse.SUPPORT_LIVE_VIEW);
        }
        if ((playMode & EqupInfo.PLAY_BACK_MODE) != 0) {
            return checkChannelSupportPermission(mode, DevicePermisionParse.SUPPORT_PLAYBACK);
        }
        return true;
    }

    public boolean checkChannelSupportPermission(int mode, int permissionIndex) {
        if (isLocalePlayer()) return true;
        if (!isIPC()) {
            if ("1".equals(getCateId())) {
                boolean result = false;
                Map<Integer, String> map = getSharePermision();
                if (map == null || TextUtils.isEmpty(map.get(mode))) {
                    return true;
                }
//                KLog.getInstance().d("checkChannelSupportPermission getSharePermision = %s , mode = %d , permissionIndex = %d",map,mode,permissionIndex).print();
                if (map != null) {
                    if (map.get(mode) != null) {
                        switch (permissionIndex) {
                            case DevicePermisionParse.SUPPORT_LIVE_VIEW:
                                if (map.get(mode).contains("1")) {
                                    result = true;
                                }
                                break;
                            case DevicePermisionParse.SUPPORT_PLAYBACK:
                                if (map.get(mode).contains("2")) {
                                    result = true;
                                }
                                break;
                            default:
                                if (map.get(mode).contains("3")) {
                                    result = true;
                                }
                                break;
                        }
                    }
                }
                return result;
            }

//            if (permissionIndex > 38){
//                if (getRemotePermision() != null){
//                    if (isNVR()){
//                        return getRemotePermision().isSupport(permissionIndex,isNVR());
//                    }
//                    if (getRemotePermision().isSupport(permissionIndex,isNVR())){
//                        return true;
//                    }
//                }
//            }
//
//            if (getInfoEntitys() != null){
//                for (ChannelInfoEntity channelInfoEntity : getInfoEntitys()){
//                    if (channelInfoEntity != null && channelInfoEntity.getChannel() == mode){
////                        KLog.getInstance().d("ChannelInfoEntity isSupport mode = %d , channelInfoEntity = %s , size = %d",mode,channelInfoEntity,getInfoEntitys().size()).print();
//                        if (channelInfoEntity.getRemotePermision() != null){
//                            return channelInfoEntity.getRemotePermision().isSupport(permissionIndex,isNVR());
//                        }
//                    }
//                }
//            }
        } else {
            if ("1".equals(getCateId())) {
                boolean result = false;
                Map<Integer, String> map = getSharePermision();
                if (map == null || TextUtils.isEmpty(map.get(1))) {
                    return true;
                }
                if (map != null && map.get(1) != null) {
                    switch (permissionIndex) {
                        case DevicePermisionParse.SUPPORT_LIVE_VIEW:
                            if (map.get(1).contains("1")) {
                                result = true;
                            }
                            break;
                        case DevicePermisionParse.SUPPORT_PLAYBACK:
                            if (map.get(1).contains("2")) {
                                result = true;
                            }
                            break;
                        default:
                            if (map.get(1).contains("3")) {
                                result = true;
                            }
                            break;
                    }
                }
                return result;
            }
//            if (getPermision() != null){
//                return getPermision().isSupport(permissionIndex,false);
//            }
        }
        return true;
    }

    public void setStreamNum(int hd, int bd, int fl) {
        this.fluency = fl;
        this.bd = bd;
        this.hd = hd;
        int result = 1;
        if (hd == 1 && bd == 0 && fl == 0) { // 单码流
            result = 0;
        } else if (hd == 1 && bd == 1 && fl == 0) {
            result = 1;
        } else if (hd == 1 && bd == 1 && fl == 1) {
            result = 2;
        } else {
            result = 1; // 获取码流失败 默认子码流
        }
        if (!isConnectStream) {
            setStream(result);
        }
    }

    public String getDr_id() {
        return dr_id;
    }

    public void setDr_id(String dr_id) {
        this.dr_id = dr_id;
    }

    public int getReplay_data_rate() {
        if (WorkContext.CONTEXT_APP == WorkContext.BITDOG_APP) {
            if (replay_data_rate != 0 && replay_data_rate != 1) {
                return 1;
            }
            return replay_data_rate;
        } else {
            return getStream();
        }
    }

    public void setReplay_data_rate(int replay_data_rate) {
        this.replay_data_rate = replay_data_rate;
    }

    public void setReplay_data_rate(int replay_data_rate, int channel) {
        if (infoEntitys != null) {
            for (ChannelInfoEntity channelInfoEntity : infoEntitys) {
                if (channelInfoEntity.getChannel() == channel) {
                    channelInfoEntity.setReplay_data_rate(replay_data_rate);
                }
            }
        }
    }

    public int getReplay_video_type() {
        if (WorkContext.CONTEXT_APP == WorkContext.BITDOG_APP) {
            if (replay_video_type != 1 && replay_video_type != 2 && replay_video_type != 3 && replay_video_type != 4) {
                return 4;
            }
            if (isIPC()) {
                if (replay_video_type == 2) {
                    this.replay_video_type = 4;
                }
            }
            if (replay_video_type == 3) {
                this.replay_video_type = 4;
            }
            return replay_video_type;
        }
        return 4;
    }

    public void setReplay_video_type(int replay_video_type) {
        if (isIPC()) {
            if (replay_video_type != 1 && replay_video_type != 4) {
                this.replay_video_type = 4;
            }
        }
        if (replay_video_type == 3) {
            this.replay_video_type = 4;
        }
        this.replay_video_type = replay_video_type;
    }

    public void setReplay_video_type(int replay_video_type, int channel) {
        if (infoEntitys != null) {
            if (isIPC()) {
                if (replay_video_type != 1 && replay_video_type != 4) {
                    replay_video_type = 4;
                }
            }
            if (replay_video_type == 3) {
                this.replay_video_type = 4;
            } else {
                this.replay_video_type = replay_video_type;
            }
            for (ChannelInfoEntity channelInfoEntity : infoEntitys) {
                if (channelInfoEntity.getChannel() == channel) {
                    channelInfoEntity.setReplay_video_type(replay_video_type);
                }
            }
        }
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public boolean isLocaleSaveDevice() {
        return localeSaveDevice;
    }

    public void setLocaleSaveDevice(boolean localeSaveDevice) {
        this.localeSaveDevice = localeSaveDevice;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getCurrentBuffStream() {
        return currentBuffStream;
    }

    public void setCurrentBuffStream(long currentBuffStream) {
        this.currentBuffStream = currentBuffStream;
    }

    public int getStream() {
//        if (playMode == VR_MODE){
//            return 0;
//        }
        return stream;
    }

    public void setStream(int stream) {
        this.stream = stream;
    }

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }

    public String getH264_plus() {
        return h264_plus;
    }

    public void setH264_plus(String h264_plus) {
        this.h264_plus = h264_plus;
    }

    public String getH265_plus() {
        return h265_plus;
    }

    public void setH265_plus(String h265_plus) {
        this.h265_plus = h265_plus;
    }

    public String getCan_use_cloud_storage() {
        return can_use_cloud_storage;
    }

    public void setCan_use_cloud_storage(String can_use_cloud_storage) {
        this.can_use_cloud_storage = can_use_cloud_storage;
    }

    public String getUse_cloud_storage() {
        return use_cloud_storage;
    }

    public void setUse_cloud_storage(String use_cloud_storage) {
        this.use_cloud_storage = use_cloud_storage;
    }

    public boolean isRemotePlay() {
        return remotePlay;
    }

    public void setRemotePlay(boolean remotePlay) {
        this.remotePlay = remotePlay;
    }

    public String getCanUseCloudStorage() {
        return canUseCloudStorage;
    }

    public void setCanUseCloudStorage(String canUseCloudStorage) {
        this.canUseCloudStorage = canUseCloudStorage;
    }

    public String getPrivateServer() {
        return PrivateServer;
    }

    public void setPrivateServer(String privateServer) {
        PrivateServer = privateServer;
    }

    public int getMode() {
        if (mode == 0) return 0;
        return mode - 1;
    }

    public int getAbsMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getDeviceStreams() {
        return deviceStreams;
    }

    public void setDeviceStreams(int deviceStreams) {
        this.deviceStreams = deviceStreams;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isDirect() {
        return isDirect;
    }

    public void setDirect(boolean direct) {
        isDirect = direct;
    }

    public boolean isLocalePlayer() {
        return isLocalePlayer;
    }

    public void setLocalePlayer(boolean localePlayer) {
        isLocalePlayer = localePlayer;
    }

    public String getLocaleDeviceIp() {
        return localeDeviceIp;
    }

    public void setLocaleDeviceIp(String localeDeviceIp) {
        this.localeDeviceIp = localeDeviceIp;
    }

    public int getLocaleDevicePort() {
        return localeDevicePort;
    }

    public void setLocaleDevicePort(int localeDevicePort) {
        this.localeDevicePort = localeDevicePort;
    }

    /**
     * 是否在线
     *
     * @return
     */
    public boolean isOnLine() {
        return "1".equals(ifOnLine);
    }

    public boolean isIPC() {
        if (!TextUtils.isEmpty(deviceDetatilType)) {
            return deviceDetatilType.contains("IPC");
        } else {
            String t = getEquoModleStr();
            if (!TextUtils.isEmpty(t)) {
                return t.contains("IPC");
            }
        }
        return true;
    }

    public boolean isNVR() {
        if (!TextUtils.isEmpty(deviceDetatilType)) {
            return deviceDetatilType.contains("NVR");
        } else {
            String t = getEquoModleStr();
            if (!TextUtils.isEmpty(t)) {
                return t.contains("NVR");
            }
        }
        return false;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("device_id", equpId);
            json.put("device_status", ifOnLine);
            json.put("bind_status", ifBind);
            json.put("device_model", deviceDetatilType);
            json.put("use_cloud_storage", useCloudStorage);
            json.put("can_use_cloud_storage", canUseCloudStorage);
            json.put("device_sdkver", version);
            json.put("device_verify", checkStr);
            json.put("device_name", deviceName);
            json.put("device_type", deviceType);
            json.put("device_firmware_ver", sysVersion);
            json.put("server_ip", deviceConnectServer);
            json.put("cate_id", cateId);
            json.put("local_pwd", localPwd);
            json.put("local_user", localUser);
            json.put("order_num", order_num);
            json.put("isDirect", isDirect);
            json.put("dr_id ", dr_id);
            if (infoEntitys != null) {
                JSONArray array = new JSONArray();
                for (ChannelInfoEntity c : infoEntitys) {
                    array.put(c.toJson());
                }
                json.put("channelList", array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static EqupInfo parseLocaleSharedData(JSONObject object) {
        return parse(object);
    }

    public String getChannelOrderName(int channel) {
        if (infoEntitys == null) return "CH " + channel;
        for (ChannelInfoEntity channelInfoEntity : infoEntitys) {
            if (channelInfoEntity.getChannel() == channel) {
                String name = channelInfoEntity.getChannel_name();
                if (TextUtils.isEmpty(name) || String.valueOf(channel).equals(name)) {
                    return "CH " + channel;
                } else {
                    return name;
                }
            }
        }
        return "CH " + channel;
    }

    public void setChannelOrderName(int channel, String channelName) {
        for (ChannelInfoEntity channelInfoEntity : infoEntitys) {
            if (channelInfoEntity.getChannel() == channel) {
                channelInfoEntity.setChannel_name(channelName);
            }
        }
    }

    public String getChannelOrderNamePlay(int channel) {
        if (infoEntitys == null) return "CH " + channel;
        for (ChannelInfoEntity channelInfoEntity : infoEntitys) {
            if (channelInfoEntity.getChannel() == channel) {
                String name = channelInfoEntity.getChannel_name();
                if (TextUtils.isEmpty(name) || String.valueOf(channel).equals(name)) {
                    return "CH" + channel;
                } else {
                    return name;
                }
            }
        }
        return "CH" + channel;
    }

    public String getOrder_num() {
        return order_num;
    }

    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }

    public List<ChannelInfoEntity> getInfoEntitys() {
        return infoEntitys;
    }

    public void setInfoEntitys(List<ChannelInfoEntity> infoEntitys) {
        if (infoEntitys != null) {
            if (getCate_group() != null && getCate_group().size() > 1) {
                List<ChannelInfoEntity> channelInfoEntityList = new ArrayList<>();
                for (ChannelInfoEntity channelInfoEntity : infoEntitys) {
                    if (channelInfoEntity.getCate_id() != null && channelInfoEntity.getCate_id().equals(cateId)) {
                        channelInfoEntityList.add(channelInfoEntity);
                    }
                }
                this.infoEntitys = channelInfoEntityList;
                return;
            }
        }
        this.infoEntitys = infoEntitys;
    }

    public String getLocalPwd() {
        return localPwd;
    }

    public void setLocalPwd(String localPwd) {
        this.localPwd = localPwd;
    }

    public String getLocalUser() {
        return localUser;
    }

    public void setLocalUser(String localUser) {
        this.localUser = localUser;
    }

    public String getDeviceDetatilType() {
        return deviceDetatilType;
    }

    public String getDeviceDetatilTypeName() {
        if (isIPC()) {
            return "IPC";
        }
        return deviceDetatilType;
    }

    public void setDeviceDetatilType(String deviceDetatilType) {
        this.deviceDetatilType = deviceDetatilType;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getDeviceConnectServer() {
        return deviceConnectServer;
    }

    public void setDeviceConnectServer(String deviceConnectServer) {
        this.deviceConnectServer = deviceConnectServer;
    }

    public String getSysVersion() {
        return sysVersion;
    }

    public void setSysVersion(String sysVersion) {
        this.sysVersion = sysVersion;
    }

    public String getEqupId() {
        if (equpId == null) {
            equpId = "";
        }
        return equpId;
    }

    public void setEqupId(String equpId) {
        this.equpId = equpId;
    }

    public String getIfOnLine() {
        return ifOnLine;
    }

    public void setIfOnLine(String ifOnLine) {
        this.ifOnLine = ifOnLine;
    }

    public String getIfBind() {
        return ifBind;
    }

    public void setIfBind(String ifBind) {
        this.ifBind = ifBind;
    }

    public String getUseCloudStorage() {
        return useCloudStorage;
    }

    public void setUseCloudStorage(String useCloudStorage) {
        this.useCloudStorage = useCloudStorage;
    }

    public String getEquoModle() {
        return equoModle;
    }

    public void setEquoModle(String equoModle) {
        this.equoModle = equoModle;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCheckStr() {
        return checkStr;
    }

    public void setCheckStr(String checkStr) {
        this.checkStr = checkStr;
    }

    public String getDeviceName() {
        if (TextUtils.isEmpty(deviceName)) {
            return equpId;
        }
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getCateId() {
        return cateId;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    public static EqupInfo parse(String str) {
        if (TextUtils.isEmpty(str))
            return null;
        try {
            JSONObject array = new JSONObject(str);
            if (array != null) {
                return parse(array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static EqupInfo parse(JSONObject object) {
        if (object == null)
            return null;
        EqupInfo info = new EqupInfo();
        info.setEqupId(object.optString("device_id"));
        info.setIfOnLine(object.optString("device_status"));
//		System.out.println("setIfOnLine..........." + info.getIfOnLine());
        info.setIfBind(object.optString("bind_status"));
        info.setEquoModle(SynscDeviceModle(object.optString("device_model")));
        info.setDeviceDetatilType(object.optString("device_model"));
        info.setVersion(object.optString("device_sdkver"));
        info.setCheckStr(object.optString("device_verify"));
        info.setDeviceName(object.optString("device_name"));
        info.setDeviceType(object.optString("device_type"));
        info.setSysVersion(object.optString("device_firmware_ver"));
        info.setDeviceConnectServer(object.optString("server_ip"));
        info.setCateId(object.optString("cate_id")); // ����cateId
        info.setLocalPwd(object.optString("local_pwd"));
        info.setLocalUser(object.optString("local_user"));
        info.setOrder_num(object.optString("order_num"));
        if (object.has("isDirect")) {
            info.setDirect(object.optBoolean("isDirect"));
        }
        info.setUseCloudStorage(object.optString("use_cloud_storage"));
        info.setCanUseCloudStorage(object.optString("can_use_cloud_storage"));
        info.setH264_plus(object.optString("h264_plus"));
        info.setH265_plus(object.optString("h265_plus"));
        info.setDr_id(object.optString("dr_id"));
        info.setPermision(new DevicePermisionParse(object.optString("permision")));
        info.setLocalePermision(new DevicePermisionParse(object.optString("local_permision")));
        info.setRemotePermision(new DevicePermisionParse(object.optString("remote_permision")));

        JSONArray cateArray = object.optJSONArray("cate_group");
        if (cateArray != null) {
            List<String> cateList = new ArrayList<>();
            String cate = "";
            for (int i = 0; i < cateArray.length(); i++) {
                cate = cateArray.optString(i);
                cateList.add(cate);
                if (i == 0) {
                    info.setCateId(cate);
                }
            }
            info.setCate_group(cateList);
        }

        if ("1".equals(info.getCateId())) {
            JSONArray shareChannels = object.optJSONArray("share_channels");
            if (shareChannels != null && shareChannels.length() > 0) {
                Map<Integer, String> stringMap = new HashMap<>();
                JSONObject jsonObject = null;
                String channel_share_permission = null;
                for (int i = 0; i < shareChannels.length(); i++) {
                    jsonObject = shareChannels.optJSONObject(i);
                    if (jsonObject != null) {
                        int channel = jsonObject.optInt("channel");
                        channel_share_permission = jsonObject.optString("share_permision");
                        if (TextUtils.isEmpty(channel_share_permission)) {
                            channel_share_permission = jsonObject.optString("share_permssion");
                        }
                        stringMap.put(channel, channel_share_permission);
                    }
                }
                info.setSharePermision(stringMap);
            } else {
                info.setSharePermision(null);
            }
        }

        JSONArray channelPermissionArray = object.optJSONArray("channel_permision");
        if (channelPermissionArray != null) {
            List<DevicePermisionParse> permisionParses = new ArrayList<>();
            for (int i = 0; i < channelPermissionArray.length(); i++) {
                permisionParses.add(new DevicePermisionParse(channelPermissionArray.optString(i)));
            }
            info.setChannelPermisionParses(permisionParses);
        }

        List<ChannelInfoEntity> list = new ArrayList<>();
        ChannelInfoEntity channelInfoEntityPermission = null;
        if (object.has("channelList")) {
            info.requestChannel = false;
            JSONArray array = object.optJSONArray("channelList");
            if (array != null) {
                Map<Integer, String> stringMapShare = new HashMap<>();
                for (int i = 0; i < array.length(); i++) {
                    try {
                        channelInfoEntityPermission = ChannelInfoEntity.parse(array.getJSONObject(i));
                        if (channelInfoEntityPermission != null) {
                            if (channelInfoEntityPermission.getChannel() > 0 && info.getChannelPermisionParses() != null
                                    && channelInfoEntityPermission.getChannel() <= info.getChannelPermisionParses().size()) {
                                channelInfoEntityPermission.setRemotePermision(info.getChannelPermisionParses().get(channelInfoEntityPermission.getChannel() - 1));
                            }
                        }
//                        channelInfoEntityPermission.setCate_id(info.getCateId());
                        list.add(channelInfoEntityPermission);
                        if ("1".equals(info.getCateId())) {
                            stringMapShare.put(channelInfoEntityPermission.getChannel(), channelInfoEntityPermission.getSharePermision());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (info.getSharePermision() == null || info.getSharePermision().size() == 0) {
                    if (stringMapShare.size() > 0) {
                        info.setSharePermision(stringMapShare);
                    }
                }
            }
        } else {
            info.requestChannel = false;
            //生成通道列表
            List<ChannelInfoEntity> channelInfoEntities = createChannelInfoEntity(info);
            if (channelInfoEntities != null) {
                list.addAll(channelInfoEntities);
            }
        }

        if (info.isIPC()) {
            if (list != null && list.size() == 1) {
                ChannelInfoEntity channelInfoEntity = list.get(0);
                if (channelInfoEntity != null) {
                    info.setReplay_data_rate(channelInfoEntity.getReplay_data_rate());
                    info.setReplay_video_type(channelInfoEntity.getReplay_video_type());
                    info.setDc_id(channelInfoEntity.getDc_id());
                    info.setChannelName(channelInfoEntity.getChannel_name());
                }
            }
        } else {
//            info.setInfoEntitys(list);
        }
//        info.setInfoEntitys(list);
        info.infoEntitys = list;
//        KLog.getInstance().e("Equpinfo parse = %s", info).print();
        return info;
    }

    private static List<ChannelInfoEntity> createChannelInfoEntity(EqupInfo info) {
        if (info == null) return null;
        List<ChannelInfoEntity> channelInfoEntityList = new ArrayList<>();
        ChannelInfoEntity channelInfoEntity = null;
        if (info.isIPC()) {
            channelInfoEntity = new ChannelInfoEntity();
            channelInfoEntity.setCate_id(info.getCateId());
            channelInfoEntity.setDevice_id(info.getEqupId());
            channelInfoEntity.setChannel(0);
            channelInfoEntity.setChannel_name("CH1");
            channelInfoEntity.setOrder_num(0);
            channelInfoEntity.setReplay_video_type(0);
            channelInfoEntity.setReplay_data_rate(1);
            channelInfoEntity.setDate_link(ChannelInfoEntity.CREATE_AUTO);
            channelInfoEntity.setData_link_time(System.currentTimeMillis());
            channelInfoEntity.setPermision(new DevicePermisionParse(DevicePermisionParse.NO_PERMISION));
            channelInfoEntity.setLocalePermision(new DevicePermisionParse(DevicePermisionParse.NO_PERMISION));
            channelInfoEntity.setRemotePermision(new DevicePermisionParse(DevicePermisionParse.NO_PERMISION));
            channelInfoEntityList.add(channelInfoEntity);
        } else {
            if ("1".equals(info.getCateId())) {
                int x = 0;
                for (Map.Entry<Integer, String> entry : info.getSharePermision().entrySet()) {
                    channelInfoEntity = new ChannelInfoEntity();
                    channelInfoEntity.setDevice_id(info.getEqupId());
                    channelInfoEntity.setChannel(entry.getKey() + 1);
                    channelInfoEntity.setChannel_name("CH" + (entry.getKey() + 1));
                    channelInfoEntity.setOrder_num(entry.getKey());
                    channelInfoEntity.setReplay_video_type(0);
                    channelInfoEntity.setReplay_data_rate(1);
                    channelInfoEntity.setDate_link(ChannelInfoEntity.CREATE_AUTO);
                    channelInfoEntity.setData_link_time(System.currentTimeMillis());
                    channelInfoEntity.setPermision(new DevicePermisionParse(DevicePermisionParse.NO_PERMISION));
                    channelInfoEntity.setLocalePermision(new DevicePermisionParse(DevicePermisionParse.NO_PERMISION));
                    channelInfoEntity.setRemotePermision(new DevicePermisionParse(DevicePermisionParse.NO_PERMISION));

                    if (x >= 0 && info.getChannelPermisionParses() != null
                            && x < info.getChannelPermisionParses().size()) {
                        channelInfoEntity.setRemotePermision(info.getChannelPermisionParses().get(x));
                    }
                    x++;
                    channelInfoEntity.setCate_id(info.getCateId());
                    channelInfoEntityList.add(channelInfoEntity);
                }
            } else {
                int channelLength = info.getChannelSum();
                if (channelLength > 0) {
                    for (int i = 0; i < channelLength; i++) {
                        channelInfoEntity = new ChannelInfoEntity();
                        channelInfoEntity.setDevice_id(info.getEqupId());
                        channelInfoEntity.setChannel(i + 1);
                        channelInfoEntity.setChannel_name("CH" + (i + 1));
                        channelInfoEntity.setOrder_num(i);
                        channelInfoEntity.setReplay_video_type(0);
                        channelInfoEntity.setReplay_data_rate(1);
                        channelInfoEntity.setDate_link(ChannelInfoEntity.CREATE_AUTO);
                        channelInfoEntity.setData_link_time(System.currentTimeMillis());
                        channelInfoEntity.setPermision(new DevicePermisionParse(DevicePermisionParse.NO_PERMISION));
                        channelInfoEntity.setLocalePermision(new DevicePermisionParse(DevicePermisionParse.NO_PERMISION));
                        channelInfoEntity.setRemotePermision(new DevicePermisionParse(DevicePermisionParse.NO_PERMISION));

                        if (channelInfoEntity.getChannel() > 0 && info.getChannelPermisionParses() != null
                                && channelInfoEntity.getChannel() <= info.getChannelPermisionParses().size()) {
                            channelInfoEntity.setRemotePermision(info.getChannelPermisionParses().get(channelInfoEntity.getChannel() - 1));
                        }
                        channelInfoEntity.setCate_id(info.getCateId());
                        channelInfoEntityList.add(channelInfoEntity);
                    }
                }
            }
        }
        return channelInfoEntityList;
    }

    /**
     * 返回结果做了修改 为了兼容之前的UI层 在此做数据转换
     *
     * @param deviceModle
     * @return
     */
    public static String SynscDeviceModle(String deviceModle) {
        String modle = "";
        if (deviceModle == null)
            return "1";
        if (deviceModle.startsWith("IPC")) {
            modle = "1000";
        } else if (deviceModle.contains("_")) {
            if (deviceModle.split("_")[0].equals("DVR")) {
                if (Integer.parseInt(deviceModle.split("_")[1]) == 4) {
                    modle = "2010";
                } else if (Integer.parseInt(deviceModle.split("_")[1]) == 8) {
                    modle = "2110";
                } else if (Integer.parseInt(deviceModle.split("_")[1]) == 16) {
                    modle = "2210";
                } else if (Integer.parseInt(deviceModle.split("_")[1]) == 25) {
                    modle = "2310";
                } else if (Integer.parseInt(deviceModle.split("_")[1]) == 32) {
                    modle = "2410";
                } else { // 无法识别的路数 默认为4
                    modle = "2010";
                }
            } else { // 其他统称为NVR
                if (Integer.parseInt(deviceModle.split("_")[1]) == 4) {
                    modle = "4010";
                } else if (Integer.parseInt(deviceModle.split("_")[1]) == 8) {
                    modle = "4110";
                } else if (Integer.parseInt(deviceModle.split("_")[1]) == 16) {
                    modle = "4210";
                } else if (Integer.parseInt(deviceModle.split("_")[1]) == 25) {
                    modle = "4310";
                } else if (Integer.parseInt(deviceModle.split("_")[1]) == 32) {
                    modle = "4410";
                } else if (Integer.parseInt(deviceModle.split("_")[1]) == 9) {
                    modle = "4610";
                } else if (Integer.parseInt(deviceModle.split("_")[1]) == 36) {
                    modle = "4710";
                } else if (Integer.parseInt(deviceModle.split("_")[1]) == 64) {
                    modle = "4810";
                } else { // 无法识别的路数 默认为4
                    modle = "4010";
                }
            }
        } else if (deviceModle.equals("")) {
            modle = "1";
        }

        return modle;
    }

    public int getChannelSum() {
        int sum = createTotalChannel(getEquoModle());

        if (infoEntitys != null) {
            if (sum != infoEntitys.size()) {
                return infoEntitys.size();
            }
        }
        return sum;
    }

    public String getEquoModleStr() {
        String result = "IPC";
        try {
            if (getEquoModle().equals("") || getEquoModle() == null) {
                return "IPC";
            }
            int type = Integer.parseInt(getEquoModle());
            if (type < 2000) {// IPC
                result = "IPC";
            } else if (type > 2000 && type < 2101) {// DVR��ͨ��
                result = "DVR-4";
            } else if (type > 2100 && type < 2201) {// DVR8ͨ��
                result = "DVR-8";
            } else if (type > 2200 && type < 2301) {// DVR16ͨ��
                result = "DVR-16";
            } else if (type > 2300 && type < 2401) {// DVR24ͨ��
                result = "DVR-25";
            } else if (type > 2400 && type < 2501) {// DVR32ͨ��
                result = "DVR-32";
            } else if (type > 4000 && type < 4101) {// NVR4ͨ��
                result = "NVR-4";
            } else if (type > 4100 && type < 4201) {// NVR8ͨ��
                result = "NVR-8";
            } else if (type > 4200 && type < 4301) {// NVR16ͨ��
                result = "NVR-16";
            } else if (type > 4300 && type < 4401) {// NVR24ͨ��
                result = "NVR-25";
            } else if (type > 4400 && type < 4501) {// NVR32ͨ��
                result = "NVR-32";
            } else if (type > 4500 && type < 4601) { // ����nvr-25
                result = "NVR-25";
            } else if (type > 4600 && type < 4701) { // ����nvr-9
                result = "NVR-9";
            } else if (type > 4700 && type < 4801) {
                result = "NVR-36";
            } else if (type > 4800 && type < 4901) {
                result = "NVR-64";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private int createTotalChannel(String deviceModle) {
        int modle = 0;
        try {
            modle = Integer.parseInt(deviceModle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int chann = -1;
        if ((modle > 2000 && modle < 2101) || (modle > 4000 && modle < 4101)) {
            chann = 4;
        } else if ((modle > 2100 && modle < 2201) || (modle > 4100 && modle < 4201)) {
            chann = 8;
        } else if ((modle > 2200 && modle < 2301) || (modle > 4200 && modle < 4301)) {
            chann = 16;
        } else if ((modle > 2300 && modle < 2401)) {
            chann = 24;
        } else if ((modle > 2400 && modle < 2501) || (modle > 4400 && modle < 4501)) {
            chann = 32;
        } else if ((modle > 4500 && modle < 4601) || (modle > 4300 && modle < 4401)) {
            chann = 25;
        } else if ((modle > 4600 && modle < 4701)) {
            chann = 9;
        } else if ((modle > 4700 && modle < 4801)) {
            chann = 36;
        } else if ((modle > 4800 && modle < 4901)) {
            chann = 64;
        } else {
            chann = 4;
        }

        return chann;
    }

    @Override
    public int compareTo(@NonNull EqupInfo o) {
        if (o == null || this == o) return 0;
        String position1 = o.getOrder_num();
        String position2 = getOrder_num();
        if (TextUtils.isEmpty(position1) || TextUtils.isEmpty(position2)) {
            return 0;
        }
        try {
            int p1 = Integer.valueOf(position1);
            int p2 = Integer.valueOf(position2);
            if (p1 < p2) {
                return 1;
            } else if (p1 > p2) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public DirectInfo createDirectInfo() {
        if (isDirect() && !TextUtils.isEmpty(deviceConnectServer)) {
            DirectInfo directInfo = new DirectInfo();
            String[] d = deviceConnectServer.substring(7).split(":");
            if (d != null && d.length == 3) {
                directInfo.setUser(d[0]);
                String[] p = d[1].split("@");
                if (p != null && p.length == 2) {
                    directInfo.setPassword(p[0]);
                    directInfo.setIp(p[1]);
                }
                directInfo.setPort(d[2].split("/")[0]);
            }
            directInfo.setDeviceType(deviceType);
            if (infoEntitys != null) {
                directInfo.setChannelNumber(infoEntitys.size());
            }
            directInfo.setDeviceName(deviceName);
            directInfo.setDeviceId(equpId);
            return directInfo;
        }
        return null;
    }

    /**
     * 清除不在该分组下的通道
     */
    public void clearChannel() {
        if (getInfoEntitys() != null) {
            List<ChannelInfoEntity> channelInfoEntityList = new ArrayList<>();
            for (ChannelInfoEntity channelInfoEntity : getInfoEntitys()) {
                if (channelInfoEntity != null) {
                    if (cateId != null) {
                        if (cateId.equals(channelInfoEntity.getCate_id())) {
                            channelInfoEntityList.add(channelInfoEntity);
                        }
                    }
                }
            }
            this.infoEntitys = channelInfoEntityList;
        }
    }

    @Override
    public EqupInfo clone() {
        EqupInfo equpInfo = new EqupInfo();
        equpInfo.setEqupId(equpId);
        equpInfo.setIfBind(ifBind);
        equpInfo.setIfOnLine(ifOnLine);
        equpInfo.setEquoModle(equoModle);
        equpInfo.setVersion(version);
        equpInfo.setCheckStr(checkStr);
        equpInfo.setDeviceName(deviceName);
        equpInfo.setDeviceType(deviceType);
        equpInfo.setSysVersion(sysVersion);
        equpInfo.setDeviceDetatilType(deviceDetatilType);
        equpInfo.setCateId(cateId);
        equpInfo.setUseCloudStorage(useCloudStorage);
        equpInfo.setCanUseCloudStorage(canUseCloudStorage);
        equpInfo.setCateName(cateName);
        equpInfo.setDeviceConnectServer(deviceConnectServer);
        equpInfo.setLocalPwd(localPwd);
        equpInfo.setLocalUser(localUser);
        List<ChannelInfoEntity> channelInfoEntities = new ArrayList<>();
        if (infoEntitys != null) {
            channelInfoEntities.addAll(infoEntitys);
        }
        equpInfo.infoEntitys = channelInfoEntities;
        equpInfo.setOrder_num(order_num);
        equpInfo.setLocalePlayer(isLocalePlayer);
        equpInfo.setLocaleDeviceIp(localeDeviceIp);
        equpInfo.setLocaleDevicePort(localeDevicePort);
        equpInfo.setDirect(isDirect);
        equpInfo.setPrivateServer(getPrivateServer());
        equpInfo.setMode(mode);
        equpInfo.setDeviceStreams(deviceStreams);
        if (isDirect()) {
            equpInfo.setStreamNum(1, 1, 0);
        }
        equpInfo.setPort(port);
        equpInfo.setRemotePlay(remotePlay);
        equpInfo.setH264_plus(h264_plus);
        equpInfo.setH265_plus(h265_plus);
        equpInfo.setCan_use_cloud_storage(can_use_cloud_storage);
        equpInfo.setUse_cloud_storage(use_cloud_storage);
        equpInfo.setLocaleSaveDevice(localeSaveDevice);
        equpInfo.setReplay_data_rate(replay_data_rate);
        equpInfo.setReplay_video_type(replay_video_type);
        equpInfo.setChannelName(channelName);
        equpInfo.setDr_id(dr_id);

        equpInfo.setChannelPermisionParses(channelPermisionParses);
        equpInfo.setSharePermision(sharePermision);
        equpInfo.setRemotePermision(remotePermision);
        equpInfo.setLocalePermision(localePermision);
        equpInfo.setPermision(permision);
        equpInfo.setCate_group(cate_group);
        equpInfo.setIsSNLocaleDevice(isSNLocaleDevice);
        return equpInfo;
    }

    @Override
    public String toString() {
        return "EqupInfo{" +
                "equpId='" + equpId + '\'' +
                ", ifOnLine='" + ifOnLine + '\'' +
                ", ifBind='" + ifBind + '\'' +
                ", equoModle='" + equoModle + '\'' +
                ", version='" + version + '\'' +
                ", checkStr='" + checkStr + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", sysVersion='" + sysVersion + '\'' +
                ", deviceDetatilType='" + deviceDetatilType + '\'' +
                ", cateId='" + cateId + '\'' +
                ", useCloudStorage='" + useCloudStorage + '\'' +
                ", canUseCloudStorage='" + canUseCloudStorage + '\'' +
                ", cateName='" + cateName + '\'' +
                ", deviceConnectServer='" + deviceConnectServer + '\'' +
                ", localPwd='" + localPwd + '\'' +
                ", localUser='" + localUser + '\'' +
                ", infoEntitys=" + infoEntitys +
                ", order_num='" + order_num + '\'' +
                ", isLocalePlayer=" + isLocalePlayer +
                ", localeDeviceIp='" + localeDeviceIp + '\'' +
                ", localeDevicePort=" + localeDevicePort +
                ", isDirect=" + isDirect +
                ", PrivateServer='" + PrivateServer + '\'' +
                ", mode=" + mode +
                ", deviceStreams=" + deviceStreams +
                ", port=" + port +
                ", remotePlay=" + remotePlay +
                ", h264_plus='" + h264_plus + '\'' +
                ", h265_plus='" + h265_plus + '\'' +
                ", can_use_cloud_storage='" + can_use_cloud_storage + '\'' +
                ", use_cloud_storage='" + use_cloud_storage + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", currentBuffStream=" + currentBuffStream +
                ", localeSaveDevice=" + localeSaveDevice +
                ", channelName='" + channelName + '\'' +
                ", replay_data_rate=" + replay_data_rate +
                ", replay_video_type=" + replay_video_type +
                ", playMode=" + playMode +
                ", fluency=" + fluency +
                ", bd=" + bd +
                ", hd=" + hd +
                ", stream=" + stream +
                ", dr_id='" + dr_id + '\'' +
                ", cate_group=" + cate_group +
                ", isConnectStream=" + isConnectStream +
                ", permision=" + permision +
                ", localePermision=" + localePermision +
                ", remotePermision=" + remotePermision +
                ", sharePermision=" + sharePermision +
                ", deviceNativeInfo=" + deviceNativeInfo +
                ", channelPermisionParses=" + channelPermisionParses +
                ", dc_id=" + dc_id +
                ", last_data_time=" + last_data_time +
                ", requestChannel=" + requestChannel +
                '}';
    }
}
