package com.gzch.lsplat.work.action;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.TrafficStats;
import android.text.TextUtils;

import com.gzch.lsplat.work.WorkContext;
import com.gzch.lsplat.work.cmd.BitdogCmd;
import com.gzch.lsplat.work.db.SQLiteHelper;
import com.gzch.lsplat.work.file.FileManager;
import com.gzch.lsplat.work.mode.ChannelInfoEntity;
import com.gzch.lsplat.work.mode.DemoInfo;
import com.gzch.lsplat.work.mode.EqupInfo;
import com.gzch.lsplat.work.mode.FaceAttrInfo;
import com.gzch.lsplat.work.mode.Flow;
import com.gzch.lsplat.work.mode.Group;
import com.gzch.lsplat.work.mode.ImageInfo;
import com.gzch.lsplat.work.mode.LoginRecord;
import com.gzch.lsplat.work.mode.SerialLoginRecord;
import com.gzch.lsplat.work.mode.UserInfo;
import com.gzch.lsplat.work.mode.event.UpdateRAMDataEvent;
import com.gzch.lsplat.work.utils.NetUtils;
import com.gzch.lsplat.work.utils.TrafficLimitValue;
import com.longse.lsapc.lsacore.BitdogInterface;
import com.longse.lsapc.lsacore.sapi.db.DBHelper;
import com.longse.lsapc.lsacore.sapi.file.FileHelper;
import com.longse.lsapc.lsacore.sapi.log.KLog;
import com.longse.lsapc.lsacore.sapi.sharedPreferences.StringCache;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lw on 2018/1/4.
 */

public class DBAction {

    private Object lock = new Object();

    private static final DBAction ourInstance = new DBAction();

    public static DBAction getInstance() {
        return ourInstance;
    }

    private DBAction() {
        deleteOldFlowData();
    }

    /**
     * 获取登录历史记录
     */
    public List<LoginRecord> getLoginHistory(final int needPassord){
        String sql = "select * from " + SQLiteHelper.LOGIN_RECORD_TABLE_NAME + " order by id desc ";
        return DBHelper.getInstance().query(sql, null, new DBHelper.QueryListener<LoginRecord>() {
            @Override
            public List<LoginRecord> queryOk(Cursor c) {
                List<LoginRecord> loginRecordList = new ArrayList<>();
                if (c != null && c.getCount() > 0){
                    LoginRecord loginRecord = null;
                    int i = 0;
                    while (c.moveToNext()){
                        loginRecord = new LoginRecord();
                        loginRecord.setAccount(c.getString(c.getColumnIndex("account")));
                        if ((i == 0 && (needPassord == 0)) || needPassord == 2){
                            i++;
                            loginRecord.setPassword(c.getString(c.getColumnIndex("password")));
                            if (needPassord == 2 && TextUtils.isEmpty(loginRecord.getPassword())){
                                continue;
                            }
                        } else {
                            loginRecord.setPassword("");
                        }
                        loginRecord.setToken(c.getString(c.getColumnIndex("token")));
                        loginRecord.setToken2(c.getString(c.getColumnIndex("token2")));
                        loginRecord.setUserId(c.getString(c.getColumnIndex("userid")));
                        loginRecord.setTime(c.getString(c.getColumnIndex("time")));
                        loginRecord.setLoginType(c.getString(c.getColumnIndex("login_type")));
                        loginRecordList.add(loginRecord);
                    }
                }
                return loginRecordList;
            }

            @Override
            public void error() {
            }
        });
    }

    /**
     * 保存登录历史记录
     * @param loginRecord
     */
    public void insertLoginRecord(final LoginRecord loginRecord){
        if (loginRecord == null)return;
        deleteLoginRecord(loginRecord);
        saveLoginRecord(loginRecord);
    }

    /**
     * 保存登录记录
     * @param loginRecord
     */
    private void saveLoginRecord(LoginRecord loginRecord){
        if (loginRecord == null)return;
        ContentValues contentValues = new ContentValues();
        contentValues.put("account",loginRecord.getAccount());
        contentValues.put("password",loginRecord.getPassword());
        contentValues.put("token",loginRecord.getToken());
        contentValues.put("token2",loginRecord.getToken2());
        contentValues.put("userid",loginRecord.getUserId());
        contentValues.put("time",loginRecord.getTime());
        contentValues.put("login_type",loginRecord.getLoginType());
        DBHelper.getInstance().insert(SQLiteHelper.LOGIN_RECORD_TABLE_NAME,contentValues);
    }

    /**
     * 删除某个账号的登录记录
     * @param loginRecord
     */
    public void deleteLoginRecord(LoginRecord loginRecord){
        if (loginRecord == null)return;
        String where = "";
        String args = "";
        if (LoginRecord.ACCOUNT.equals(loginRecord.getLoginType())){
            where = "account = ? ";
            args = loginRecord.getAccount();
        } else {
            where = "userid = ? ";
            args = loginRecord.getUserId();
        }
        DBHelper.getInstance().delete(SQLiteHelper.LOGIN_RECORD_TABLE_NAME,where,new String[]{args});
    }

    public void deleteLoginPassword(){
        ContentValues values = new ContentValues();
        values.put("password","");
        DBHelper.getInstance().update(SQLiteHelper.LOGIN_RECORD_TABLE_NAME,values,null,null);
    }

    /**
     * 保存直连设备 或 列表缓存
     * @param devices
     * @param userId
     */
    public void insertDeviceList(List<EqupInfo> devices, String userId){
        if (devices == null || TextUtils.isEmpty(userId))return;
        List<EqupInfo> equpInfoList = new ArrayList<>();
        equpInfoList.addAll(devices);
        for (EqupInfo equpInfo : equpInfoList){
            insertDevice(equpInfo,userId);
        }
    }

    public void clearPlayCache(){
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        String sql = "select * from " + SQLiteHelper.PLAY_CACHE_TABLE_NAME + " where userid = ? order by id ASC ";
        String[] args = new String[]{userId};
        List<EqupInfo> equpInfos = new ArrayList<>();
        try {
            equpInfos = DBHelper.getInstance().query(sql, args, new DBHelper.QueryListener<EqupInfo>() {
                @Override
                public List<EqupInfo> queryOk(Cursor c) {
                    return getDeviceFromCursor(c,false);
                }

                @Override
                public void error() {
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            equpInfos = new ArrayList<>();
            SQLiteHelper.reCreatePlayCacheTable();
        }
        if (equpInfos == null){
            equpInfos = new ArrayList<>();
        }
        String deviceId = null;
        boolean hasDev = false;
        for (EqupInfo equpInfo : equpInfos){
            hasDev = false;
            deviceId = equpInfo.getEqupId();
            if (TextUtils.isEmpty(deviceId)){
                continue;
            }
            if (equpInfo.isDirect() || equpInfo.isLocaleSaveDevice() || equpInfo.checkIsSNLocaleDevice()){
                continue;
            }
            deletePlayCache(equpInfo);
            EventBus.getDefault().post(UpdateRAMDataEvent.deviceDelete(deviceId));
            clearDeviceRamCache();
            deleteDevice(equpInfo,userId);
            deleteFavoritesPlayerDevice(deviceId);
        }
    }

    /**
     *
     * @param devices
     * @param userId
     */
    public void clearCache(List<EqupInfo> devices, String userId){
        if (devices == null || TextUtils.isEmpty(userId))return;
        clearPlayCache(EqupInfo.PRE_VIEW_MODE,devices);
        clearPlayCache(EqupInfo.PLAY_BACK_MODE,devices);
        clearPlayCache(EqupInfo.VR_MODE,devices);
    }

    private void clearPlayCache(int playMode, List<EqupInfo> devices){
        if (devices == null)return;
        if (devices.size() == 0){
            clearPlayCache();
            return;
        }
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        String sql = "";
        String[] args = null;
        switch (playMode){
            case EqupInfo.PRE_VIEW_MODE:
                sql = "select * from " + SQLiteHelper.PLAY_CACHE_TABLE_NAME + " where userid = ? and playMode = ? " +
                        " UNION " + " select * from " + SQLiteHelper.PLAY_CACHE_TABLE_NAME + " where playMode = ?  order by id ASC ";
                args = new String[]{userId,EqupInfo.PRE_VIEW_MODE + "",EqupInfo.DIRECT_MODE + ""};
                break;
            case EqupInfo.PLAY_BACK_MODE:
                sql = "select * from " + SQLiteHelper.PLAY_CACHE_TABLE_NAME + " where userid = ? and playMode = ? order by id ASC ";
                args = new String[]{userId,EqupInfo.PLAY_BACK_MODE + ""};
                break;
            case EqupInfo.VR_MODE:
                sql = "select * from " + SQLiteHelper.PLAY_CACHE_TABLE_NAME + " where userid = ? and playMode = ? order by id ASC ";
                args = new String[]{userId,EqupInfo.VR_MODE + ""};
                break;
//            case EqupInfo.DIRECT_MODE:
//                break;
        }
        List<EqupInfo> equpInfos = new ArrayList<>();
        try {
            equpInfos = DBHelper.getInstance().query(sql, args, new DBHelper.QueryListener<EqupInfo>() {
                @Override
                public List<EqupInfo> queryOk(Cursor c) {
                    return getDeviceFromCursor(c,false);
                }

                @Override
                public void error() {
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            equpInfos = new ArrayList<>();
            SQLiteHelper.reCreatePlayCacheTable();
        }
        if (equpInfos == null){
            equpInfos = new ArrayList<>();
        }
        String deviceId = null;
        boolean hasDev = false;
        for (EqupInfo equpInfo : equpInfos){
            hasDev = false;
            deviceId = equpInfo.getEqupId();
            if (TextUtils.isEmpty(deviceId)){
                continue;
            }
            if (equpInfo.isDirect() || equpInfo.isLocaleSaveDevice() || equpInfo.checkIsSNLocaleDevice()){
                continue;
            }
            for (EqupInfo dev : devices){
                if (deviceId.equals(dev.getEqupId())){
                    hasDev = true;
                    break;
                }
            }
            if (!hasDev){
                deletePlayCache(equpInfo);
                EventBus.getDefault().post(UpdateRAMDataEvent.deviceDelete(deviceId));
                clearDeviceRamCache();
                deleteDevice(equpInfo,userId);
                deleteFavoritesPlayerDevice(deviceId);
            }
        }
    }

    private void clearDeviceRamCache(){
        int[] cmds = new int[]{BitdogCmd.REQUEST_DEVICE_MANAGER_DEVICE_LIST_CMD,
                BitdogCmd.REQUEST_VR_PLAY_DEVICE_LIST_CMD,
                BitdogCmd.REQUEST_NO_VR_PLAY_DEVICE_LIST_CMD};
        for (int cmd : cmds){
            BitdogInterface.getInstance().delData(WorkContext.GROUP_DEVICE_DATA_KEY + cmd);
        }
    }

    /**
     * 保存单个设备信息,可以是缓存 和 直连设备
     * @param device
     * @param userId
     */
    public void insertDevice(EqupInfo device, String userId){
        if (device == null)return;
        if (!device.isDirect()){
            if (TextUtils.isEmpty(userId))return;
        }
        deleteDevice(device,userId);
        ContentValues values = new ContentValues();
        values.put("userid",userId);
        values.put("equpId",device.getEqupId());
        values.put("ifOnLine",device.getIfOnLine());
        values.put("ifBind",device.getIfBind());
        values.put("equoModle",device.getEquoModle());
        values.put("version",device.getVersion());
        values.put("checkStr",device.getCheckStr());
        values.put("deviceName",device.getDeviceName());
        values.put("deviceType",device.getDeviceType());
        values.put("sysVersion",device.getSysVersion());
        values.put("deviceDetatilType",device.getDeviceDetatilType());
        values.put("cateId",device.getCateId());
        values.put("useCloudStorage",device.getUseCloudStorage());
        values.put("canUseCloudStorage",device.getCanUseCloudStorage());
        values.put("cateName",device.getCateName());
        values.put("deviceConnectServer",device.getDeviceConnectServer());
        values.put("localPwd",device.getLocalPwd());
        values.put("localUser",device.getLocalUser());
        values.put("order_num",device.getOrder_num());
        values.put("localeDeviceIp",device.getLocaleDeviceIp());
        values.put("localeDevicePort",device.getLocaleDevicePort());
        values.put("isDirect",(device.isDirect()) ? "1" : "0");
        values.put("PrivateServer",device.getPrivateServer());
        values.put("mode",device.getAbsMode());
        values.put("deviceStreams",device.getDeviceStreams());
        values.put("port",device.getPort());
        values.put("remotePlay",(device.isRemotePlay()) ? "1" : "0");
        values.put("h264_plus",device.getH264_plus());
        values.put("h265_plus",device.getH265_plus());
        values.put("can_use_cloud_storage",device.getCan_use_cloud_storage());
        values.put("use_cloud_storage",device.getUse_cloud_storage());
        values.put("replay_data_rate",device.getReplay_data_rate());
        values.put("replay_video_type",device.getReplay_video_type());
        values.put("channel_name",device.getChannelName());
        values.put("isSNLocaleDevice",device.getIsSNLocaleDevice());
        if (device.getSharePermision() != null){
            try {
                JSONArray array = new JSONArray();
                JSONObject jsonObject = null;
                for (Map.Entry<Integer, String> m : device.getSharePermision().entrySet()){
                    if (m != null){
                        jsonObject = new JSONObject();
                        jsonObject.put("channel",m.getKey());
                        jsonObject.put("share_permssion",m.getValue());
                        array.put(jsonObject);
                    }
                }
                values.put("share_permision",array.toString());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        DBHelper.getInstance().insert(SQLiteHelper.DEVICE_INFO_TABLE_NAME,values);
        if (!device.isDirect()){
            insertDeviceChannel(device,userId);
        }
    }

    private void insertDeviceChannel(EqupInfo equpInfo, String userid){
        if (equpInfo == null)return;
        if (equpInfo.isDirect())return;
        if (userid == null){
            userid = "";
        }
        DBHelper.getInstance().delete(SQLiteHelper.DEVICE_INFO_CHANNEL_TABLE_NAME,
                " device_id = ? ",new String[]{equpInfo.getEqupId()});
        List<ChannelInfoEntity> channelInfoEntities = equpInfo.getInfoEntitys();
        if (channelInfoEntities != null){
            ContentValues values = null;
            for (ChannelInfoEntity channelInfoEntity : channelInfoEntities){
                values = new ContentValues();
                values.put("device_id",equpInfo.getEqupId());
                values.put("dc_id",channelInfoEntity.getDc_id());
                values.put("userid",userid);
                values.put("channel_name",channelInfoEntity.getChannel_name());
                values.put("channel",channelInfoEntity.getChannel());
                values.put("order_num",channelInfoEntity.getOrder_num());
                values.put("alarm_open",channelInfoEntity.getAlarm_open() ? 1 : 0);
                values.put("replay_data_rate",channelInfoEntity.getReplay_data_rate());
                values.put("replay_video_type",channelInfoEntity.getReplay_video_type());
                DBHelper.getInstance().insert(SQLiteHelper.DEVICE_INFO_CHANNEL_TABLE_NAME,values);
            }
        }
    }

    private List<ChannelInfoEntity> queryDeviceChannels(String device_id , String userId){
        String sql = "select * from " + SQLiteHelper.DEVICE_INFO_CHANNEL_TABLE_NAME + " where userid = ? and device_id = ? order by id desc";
        List<ChannelInfoEntity> channelInfoEntities = DBHelper.getInstance().query(sql, new String[]{userId,device_id}, new DBHelper.QueryListener<ChannelInfoEntity>() {
            @Override
            public List<ChannelInfoEntity> queryOk(Cursor c) {
                List<ChannelInfoEntity> channelInfoEntityList = new ArrayList<>();
                if (c != null){
                    ChannelInfoEntity channelInfoEntity = null;
                    while (c.moveToNext()){
                        channelInfoEntity = new ChannelInfoEntity();
                        channelInfoEntity.setOrder_num(c.getInt(c.getColumnIndex("order_num")));
                        channelInfoEntity.setChannel(c.getInt(c.getColumnIndex("channel")));
                        channelInfoEntity.setChannel_name(c.getString(c.getColumnIndex("channel_name")));
                        channelInfoEntity.setDevice_id(c.getString(c.getColumnIndex("device_id")));
                        channelInfoEntity.setAlarm_open((c.getInt(c.getColumnIndex("alarm_open")) == 1));
                        channelInfoEntity.setDc_id(c.getInt(c.getColumnIndex("dc_id")));
                        channelInfoEntity.setReplay_data_rate(c.getInt(c.getColumnIndex("replay_data_rate")));
                        channelInfoEntity.setReplay_video_type(c.getInt(c.getColumnIndex("replay_video_type")));
                        channelInfoEntityList.add(channelInfoEntity);
                    }
                }
                return channelInfoEntityList;
            }

            @Override
            public void error() {
            }
        });
        return channelInfoEntities;
    }

    /**
     * 删除某台设备 包括缓存 和 直连设备
     * @param device
     * @param userId 登录用户userId
     */
    public void deleteDevice(EqupInfo device, String userId){
        if (device == null)return;
        if (device.isDirect()){
            if (TextUtils.isEmpty(device.getDeviceConnectServer()))return;
            String where = " deviceConnectServer = ? ";
            DBHelper.getInstance().delete(SQLiteHelper.DEVICE_INFO_TABLE_NAME,where,new String[]{device.getDeviceConnectServer()});
        } else {
            if (TextUtils.isEmpty(userId))return;
            String deviceId = device.getEqupId();
            if (TextUtils.isEmpty(deviceId))return;
            String where = " userid = ? and equpId = ? and isDirect = ? ";
            DBHelper.getInstance().delete(SQLiteHelper.DEVICE_INFO_TABLE_NAME,where,new String[]{userId,deviceId,"0"});
            DBHelper.getInstance().delete(SQLiteHelper.DEVICE_INFO_CHANNEL_TABLE_NAME,
                    " device_id = ? ",new String[]{deviceId});
        }
    }

    /**
     * 删除直连设备
     * @param device
     * @param userId
     */
    public void deleteDevice(String device, String userId){
        if (TextUtils.isEmpty(device))return;
        String where = " deviceConnectServer = ? ";
        DBHelper.getInstance().delete(SQLiteHelper.DEVICE_INFO_TABLE_NAME,where,new String[]{device});
    }

    /**
     * 修改直连设备 账号密码
     * @param device
     * @param userId
     * @param oldUrl
     */
    public void updateDeviceDirect(EqupInfo device, String userId, String oldUrl){
        if (device == null)return;
        if (device.isDirect()){
            if (TextUtils.isEmpty(device.getDeviceConnectServer()))return;
            String where = " deviceConnectServer = ? ";
            ContentValues values = new ContentValues();
            values.put("userid",userId);
            values.put("equpId",device.getEqupId());
            values.put("ifOnLine",device.getIfOnLine());
            values.put("ifBind",device.getIfBind());
            values.put("equoModle",device.getEquoModle());
            values.put("version",device.getVersion());
            values.put("checkStr",device.getCheckStr());
            values.put("deviceName",device.getDeviceName());
            values.put("deviceType",device.getDeviceType());
            values.put("sysVersion",device.getSysVersion());
            values.put("deviceDetatilType",device.getDeviceDetatilType());
            values.put("cateId",device.getCateId());
            values.put("useCloudStorage",device.getUseCloudStorage());
            values.put("canUseCloudStorage",device.getCanUseCloudStorage());
            values.put("cateName",device.getCateName());
            values.put("deviceConnectServer",device.getDeviceConnectServer());
            values.put("localPwd",device.getLocalPwd());
            values.put("localUser",device.getLocalUser());
            values.put("order_num",device.getOrder_num());
            values.put("localeDeviceIp",device.getLocaleDeviceIp());
            values.put("localeDevicePort",device.getLocaleDevicePort());
            values.put("isDirect",(device.isDirect()) ? "1" : "0");
            values.put("PrivateServer",device.getPrivateServer());
            values.put("mode",device.getAbsMode());
            values.put("deviceStreams",device.getDeviceStreams());
            values.put("port",device.getPort());
            values.put("remotePlay",(device.isRemotePlay()) ? "1" : "0");
            values.put("h264_plus",device.getH264_plus());
            values.put("h265_plus",device.getH265_plus());
            values.put("can_use_cloud_storage",device.getCan_use_cloud_storage());
            values.put("use_cloud_storage",device.getUse_cloud_storage());
            values.put("replay_data_rate",device.getReplay_data_rate());
            values.put("replay_video_type",device.getReplay_video_type());
            values.put("channel_name",device.getChannelName());
            values.put("isSNLocaleDevice",device.getIsSNLocaleDevice());
            if (device.getSharePermision() != null){
                try {
                    JSONArray array = new JSONArray();
                    JSONObject jsonObject = null;
                    for (Map.Entry<Integer, String> m : device.getSharePermision().entrySet()){
                        if (m != null){
                            jsonObject = new JSONObject();
                            jsonObject.put("channel",m.getKey());
                            jsonObject.put("share_permssion",m.getValue());
                            array.put(jsonObject);
                        }
                    }
                    values.put("share_permision",array.toString());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            DBHelper.getInstance().update(SQLiteHelper.DEVICE_INFO_TABLE_NAME,values,where,new String[]{oldUrl});
        }
    }

    /**
     * 删除播放缓存
     * @param device
     * @param userId
     */
    public void deletePlayCacheDevice(EqupInfo device, String userId){
        if (device == null)return;
        if (device.isDirect()){
            if (TextUtils.isEmpty(device.getDeviceConnectServer()))return;
            String where = " deviceConnectServer = ? ";
            DBHelper.getInstance().delete(SQLiteHelper.PLAY_CACHE_TABLE_NAME,where,new String[]{device.getDeviceConnectServer()});
        } else {
            if (TextUtils.isEmpty(userId))return;
            String deviceId = device.getEqupId();
            if (TextUtils.isEmpty(deviceId))return;
            String where = " userid = ? and equpId = ? and isDirect = ? ";
            DBHelper.getInstance().delete(SQLiteHelper.PLAY_CACHE_TABLE_NAME,where,new String[]{userId,deviceId,"0"});
        }
    }

    /**
     * 更新缓存中 设备名
     * @param userId
     * @param deviceId
     * @param deviceName
     */
    public void updateDeviceName(String userId, String deviceId, String deviceName){
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(deviceName))return;
        ContentValues values = new ContentValues();
        values.put("deviceName",deviceName);
        String where = " userid = ? and equpId = ? and isDirect = ? ";
        DBHelper.getInstance().update(SQLiteHelper.DEVICE_INFO_TABLE_NAME,values,where,new String[]{userId,deviceId,"0"});
    }

    /**
     * 查询该用户下的所有设备 包括缓存 和 直连
     * @param userId 用户Id
     * @return
     */
    public List<EqupInfo> queryDevice(String userId){
        if (TextUtils.isEmpty(userId))return null;
        String sql = "select * from " + SQLiteHelper.DEVICE_INFO_TABLE_NAME + " where userid = ? order by id desc";
        List<EqupInfo> equpInfos = DBHelper.getInstance().query(sql, new String[]{userId}, new DBHelper.QueryListener<EqupInfo>() {
            @Override
            public List<EqupInfo> queryOk(Cursor c) {
                return getDeviceFromCursor(c,true);
            }

            @Override
            public void error() {
            }
        });
        List<EqupInfo> direct = queryDirectDevice();
        if (direct != null){
            equpInfos.addAll(direct);
        }
        return equpInfos;
    }

    /**
     * 删除所有的缓存设备和通道，不会删除直连设备
     */
    public void deleteAllCacheDevice(String userId){
        if (userId == null)return;
        String where = " userid = ? and isDirect != ? ";
        DBHelper.getInstance().delete(SQLiteHelper.DEVICE_INFO_TABLE_NAME,where,new String[]{userId,"1"});
        DBHelper.getInstance().delete(SQLiteHelper.DEVICE_INFO_CHANNEL_TABLE_NAME," userid = ? ",new String[]{userId});
        KLog.getInstance().d("delete all device cache.").print();
    }

    /**
     * 查找直连设备
     * @return
     */
    public List<EqupInfo> queryDirectDevice(){
        String sql = "select * from " + SQLiteHelper.DEVICE_INFO_TABLE_NAME + " where isDirect = ? order by id desc";
        List<EqupInfo> equpInfos = DBHelper.getInstance().query(sql, new String[]{"1"}, new DBHelper.QueryListener<EqupInfo>() {
            @Override
            public List<EqupInfo> queryOk(Cursor c) {
                return getDeviceFromCursor(c,true);
            }

            @Override
            public void error() {
            }
        });
        if (equpInfos != null){
            for (EqupInfo equpInfo : equpInfos){
                equpInfo.setIfOnLine("1");
                equpInfo.setStreamNum(1,1,0);
                if (equpInfo.getMode() > 1){
                    ChannelInfoEntity channelInfoEntity = null;
                    List<ChannelInfoEntity> channelInfoEntities = new ArrayList<>();
                    for (int i = 1; i <= equpInfo.getMode() + 1; i++){
                        channelInfoEntity = new ChannelInfoEntity();
                        channelInfoEntity.setChannel_name(i + "");
                        channelInfoEntity.setChannel(i);
                        channelInfoEntity.setOrder_num(i);
                        channelInfoEntities.add(channelInfoEntity);
                    }
                    equpInfo.setInfoEntitys(channelInfoEntities);
                }
            }
        }
        return equpInfos;
    }

    public boolean checkDirectDevice(String ip, String port){
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port))return false;
        String sql = "select * from " + SQLiteHelper.DEVICE_INFO_TABLE_NAME + " where isDirect = ? and equpId = ? order by id desc";
        List<EqupInfo> equpInfos = DBHelper.getInstance().query(sql, new String[]{"1",ip + port}, new DBHelper.QueryListener<EqupInfo>() {
            @Override
            public List<EqupInfo> queryOk(Cursor c) {
                return getDeviceFromCursor(c,false);
            }

            @Override
            public void error() {
            }
        });
        if (equpInfos != null){
            return equpInfos.size() > 0;
        }
        return false;
    }

    /**
     * 查询VR设备
     * @param userId
     * @return
     */
    public List<EqupInfo> queryVRDevice(String userId){
        if (TextUtils.isEmpty(userId))return null;
        String sql = "select * from " + SQLiteHelper.DEVICE_INFO_TABLE_NAME + " where userid = ? and deviceType = ? order by id desc";
        List<EqupInfo> equpInfos = DBHelper.getInstance().query(sql, new String[]{userId,"IPC_5"}, new DBHelper.QueryListener<EqupInfo>() {
            @Override
            public List<EqupInfo> queryOk(Cursor c) {
                return getDeviceFromCursor(c,true);
            }

            @Override
            public void error() {
            }
        });
        return equpInfos;
    }

    /**
     * 查询不包含VR的设备
     * @param userId
     * @return
     */
    public List<EqupInfo> queryNoVRDevice(String userId){
        if (TextUtils.isEmpty(userId))return null;
        String sql = "select * from " + SQLiteHelper.DEVICE_INFO_TABLE_NAME + " where userid = ? and deviceType is not ? order by id desc";
        List<EqupInfo> equpInfos = DBHelper.getInstance().query(sql, new String[]{userId,"IPC_5"}, new DBHelper.QueryListener<EqupInfo>() {
            @Override
            public List<EqupInfo> queryOk(Cursor c) {
                return getDeviceFromCursor(c,true);
            }

            @Override
            public void error() {
            }
        });
        List<EqupInfo> direct = queryDirectDevice();
        if (direct != null){
            equpInfos.addAll(direct);
        }
        return equpInfos;
    }

    /**
     * 查询不包含VR和直连的设备
     * @param userId
     * @return
     */
    public List<EqupInfo> queryNoVRNoDirectDevice(String userId){
        if (TextUtils.isEmpty(userId))return null;
        String sql = "select * from " + SQLiteHelper.DEVICE_INFO_TABLE_NAME + " where userid = ? and isDirect = ? and deviceType not ? order by id desc";
        List<EqupInfo> equpInfos = DBHelper.getInstance().query(sql, new String[]{userId,"1","IPC_5"}, new DBHelper.QueryListener<EqupInfo>() {
            @Override
            public List<EqupInfo> queryOk(Cursor c) {
                return getDeviceFromCursor(c,true);
            }

            @Override
            public void error() {
            }
        });
        return equpInfos;
    }

    private List<EqupInfo> getDeviceFromCursor(Cursor c, boolean needChannel){
        if (c == null)return null;
        List<EqupInfo> devices = new ArrayList<>();
        EqupInfo device = null;
        while (c.moveToNext()){
            device = new EqupInfo();
            device.setEqupId(c.getString(c.getColumnIndex("equpId")));
            device.setIfOnLine(c.getString(c.getColumnIndex("ifOnLine")));
            device.setIfBind(c.getString(c.getColumnIndex("ifBind")));
            device.setEquoModle(c.getString(c.getColumnIndex("equoModle")));
            device.setVersion(c.getString(c.getColumnIndex("version")));
            device.setCheckStr(c.getString(c.getColumnIndex("checkStr")));
            device.setDeviceName(c.getString(c.getColumnIndex("deviceName")));
            device.setDeviceType(c.getString(c.getColumnIndex("deviceType")));
            device.setSysVersion(c.getString(c.getColumnIndex("sysVersion")));
            device.setDeviceDetatilType(c.getString(c.getColumnIndex("deviceDetatilType")));
            device.setCateId(c.getString(c.getColumnIndex("cateId")));
            device.setUseCloudStorage(c.getString(c.getColumnIndex("useCloudStorage")));
            device.setCanUseCloudStorage(c.getString(c.getColumnIndex("canUseCloudStorage")));
            device.setCateName(c.getString(c.getColumnIndex("cateName")));
            device.setDeviceConnectServer(c.getString(c.getColumnIndex("deviceConnectServer")));
            device.setLocalPwd(c.getString(c.getColumnIndex("localPwd")));
            device.setLocalUser(c.getString(c.getColumnIndex("localUser")));
            device.setOrder_num(c.getString(c.getColumnIndex("order_num")));
            device.setLocaleDeviceIp(c.getString(c.getColumnIndex("localeDeviceIp")));
            String direct = c.getString(c.getColumnIndex("isDirect"));
            device.setDirect("1".equals(direct));
            String remote = c.getString(c.getColumnIndex("remotePlay"));
            device.setRemotePlay("1".equals(remote));
            device.setPrivateServer(c.getString(c.getColumnIndex("PrivateServer")));
            device.setH264_plus(c.getString(c.getColumnIndex("h264_plus")));
            device.setH265_plus(c.getString(c.getColumnIndex("h265_plus")));
            device.setCan_use_cloud_storage(c.getString(c.getColumnIndex("can_use_cloud_storage")));
            device.setCan_use_cloud_storage(c.getString(c.getColumnIndex("use_cloud_storage")));
            device.setReplay_data_rate(c.getInt(c.getColumnIndex("replay_data_rate")));
            device.setReplay_video_type(c.getInt(c.getColumnIndex("replay_video_type")));
            device.setChannelName(c.getString(c.getColumnIndex("channel_name")));

            device.setLocaleDevicePort(c.getInt(c.getColumnIndex("localeDevicePort")));
            device.setMode(c.getInt(c.getColumnIndex("mode")));
            device.setDeviceStreams(Integer.valueOf(c.getString(c.getColumnIndex("deviceStreams"))));
            device.setPort(c.getInt(c.getColumnIndex("port")));
            try {
                device.setIsSNLocaleDevice(c.getInt(c.getColumnIndex("isSNLocaleDevice")));
            } catch (Exception e){
                e.printStackTrace();
                device.setIsSNLocaleDevice(0);
            }

            int index = c.getColumnIndex("playMode");
            if (index > 0){
                String m = c.getString(index);
                try {
                    KLog.getInstance().d("getDeviceFromCursor playmode = %s",m).print();
                    int mode = Integer.valueOf(m);
                    device.setPlayMode(mode);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (needChannel && !device.isDirect()){
                String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
                List<ChannelInfoEntity> channelInfoEntities = queryDeviceChannels(device.getEqupId(),userId);
                device.setInfoEntitys(channelInfoEntities);
            }
            if (device.isDirect()){
                device.setStreamNum(1,1,0);
            }

            String share_per = c.getString(c.getColumnIndex("share_permision"));
            if (!TextUtils.isEmpty(share_per)){
                try {
                    JSONArray array = new JSONArray(share_per);
                    Map<Integer, String> stringMap = new HashMap<>();
                    JSONObject jsonObject = null;
                    for (int i = 0; i < array.length(); i++){
                        jsonObject = array.optJSONObject(i);
                        if (jsonObject != null){
                            if (jsonObject.has("channel") && jsonObject.has("share_permssion")){
                                stringMap.put(jsonObject.optInt("channel"),jsonObject.optString("share_permssion"));
                            }
                        }
                    }
                    device.setSharePermision(stringMap);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            devices.add(device);
        }
        return devices;
    }

    /***
     * 插入分组列表
     * @param groups
     * @param userId
     */
    public void insertGroups(List<Group> groups, String userId){
        if (groups == null || TextUtils.isEmpty(userId))return;
        List<Group> groupList = new ArrayList<>();
        groupList.addAll(groups);
        for (Group g : groupList){
            insertGroup(g,userId);
        }
    }

    private void insertGroup(Group group, String userId){
        if (group == null || TextUtils.isEmpty(userId))return;
        deleteGroup(group,userId);
        ContentValues values = new ContentValues();
        values.put("userid",userId);
        values.put("cate_id",group.getGroupId());
        values.put("cate_name",group.getGroupName());
        values.put("order_num",group.getGroupPosition());
        DBHelper.getInstance().insert(SQLiteHelper.GROUP_TABLE_NAME,values);
    }

    public void deleteGroup(Group group, String userId){
        if (group == null || TextUtils.isEmpty(userId))return;
        String where = " userid = ? and cate_id = ? ";
        DBHelper.getInstance().delete(SQLiteHelper.GROUP_TABLE_NAME,where,
                new String[]{userId,group.getGroupId()});
    }

    public void updateGroup(String groupId, String userId, Group newGroup){
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(userId) || newGroup == null)return;
        ContentValues values = new ContentValues();
        if (!TextUtils.isEmpty(newGroup.getGroupName())){
            values.put("cate_name",newGroup.getGroupName());
        }
        String where = " userid = ? and cate_id = ? ";
        DBHelper.getInstance().update(SQLiteHelper.GROUP_TABLE_NAME,values,where,new String[]{userId,groupId});
    }

    public void deleteAllGroup(){
        DBHelper.getInstance().delete(SQLiteHelper.GROUP_TABLE_NAME,"",null);
        KLog.getInstance().d("delete all group cache.").print();
    }

    /**
     * 查找分组列表
     * @param userId
     * @return
     */
    public List<Group> queryGroup(String userId){
        if (TextUtils.isEmpty(userId))return null;
        String sql = "select * from " + SQLiteHelper.GROUP_TABLE_NAME + " where userid = ? order by id desc";
        List<Group> groups = DBHelper.getInstance().query(sql, new String[]{userId}, new DBHelper.QueryListener<Group>() {
            @Override
            public List<Group> queryOk(Cursor c) {
                List<Group> groupList = new ArrayList<>();
                if (c != null){
                    Group group = null;
                    while (c.moveToNext()){
                        group = new Group();
                        group.setGroupId(c.getString(c.getColumnIndex("cate_id")));
                        group.setGroupName(c.getString(c.getColumnIndex("cate_name")));
                        group.setGroupPosition(c.getString(c.getColumnIndex("order_num")));
                        groupList.add(group);
                    }
                }
                return groupList;
            }

            @Override
            public void error() {
            }
        });
        return groups;
    }

    /**
     * 插入新的序列号登录记录
     * @param serialLoginRecord
     */
    public void insertSerialLoginRecord(SerialLoginRecord serialLoginRecord){
        if (serialLoginRecord != null && !TextUtils.isEmpty(serialLoginRecord.getDeviceId())){
            delSerialLoginRecord(serialLoginRecord.getDeviceId());
            ContentValues values = new ContentValues();
            values.put("account",serialLoginRecord.getAccount());
            values.put("password",serialLoginRecord.getPassword());
            values.put("device_id",serialLoginRecord.getDeviceId());
            DBHelper.getInstance().insert(SQLiteHelper.SERIAL_LOGIN_HISTORY_TABLE_NAME,values);
        }
    }

    /**
     * 删除一个序列号登录记录
     * @param deviceId
     */
    public void delSerialLoginRecord(String deviceId){
        if (TextUtils.isEmpty(deviceId))return;
        String where = " device_id = ? ";
        DBHelper.getInstance().delete(SQLiteHelper.SERIAL_LOGIN_HISTORY_TABLE_NAME,
                                       where,
                                       new String[]{deviceId});
    }

    /**
     * 查找所有的序列号登录记录
     * @return
     */
    public List<SerialLoginRecord> querySerialLoginRecord(){
        String sql = "select * from " + SQLiteHelper.SERIAL_LOGIN_HISTORY_TABLE_NAME + " order by id desc";
        List<SerialLoginRecord> records = DBHelper.getInstance().query(sql, null, new DBHelper.QueryListener<SerialLoginRecord>() {
            @Override
            public List<SerialLoginRecord> queryOk(Cursor c) {
                if (c != null){
                    List<SerialLoginRecord> recordList = new ArrayList<>();
                    SerialLoginRecord serialLoginRecord = null;
                    while (c.moveToNext()){
                        serialLoginRecord = new SerialLoginRecord();
                        serialLoginRecord.setAccount(c.getString(c.getColumnIndex("account")));
                        serialLoginRecord.setPassword(c.getString(c.getColumnIndex("password")));
                        serialLoginRecord.setDeviceId(c.getString(c.getColumnIndex("device_id")));
                        recordList.add(serialLoginRecord);
                    }
                    return recordList;
                }
                return null;
            }

            @Override
            public void error() {
            }
        });
        return records;
    }

    /**
     * 新增DEMO设备
     * @param demoInfo
     */
    public void insertDemoDevice(DemoInfo demoInfo){
        if (demoInfo != null){
            deleteDemoDevice(demoInfo.getId());
            ContentValues values = new ContentValues();
            values.put("demo_id",demoInfo.getId());
            values.put("device_name",demoInfo.getDevice_name());
            values.put("num",demoInfo.getNum());
            values.put("deviceType",demoInfo.getDeviceType());
            values.put("is_vr",demoInfo.isVr() ? 1 : 2);
            DBHelper.getInstance().insert(SQLiteHelper.DEMO_LIST_BACKUP_TABLE_NAME,values);
        }
    }

    /**
     * 删除一个DEMO设备
     * @param demoId
     */
    public void deleteDemoDevice(String demoId){
        if (TextUtils.isEmpty(demoId))return;
        String where = " demo_id = ? ";
        DBHelper.getInstance().delete(SQLiteHelper.DEMO_LIST_BACKUP_TABLE_NAME,
                where,
                new String[]{demoId});
    }

    /**
     * 删除所有的DEMO设备
     */
    public void deleteAllDemoDevice(){
//        DBHelper.getInstance().delete(SQLiteHelper.DEMO_LIST_BACKUP_TABLE_NAME,
//                "",
//                null);
        List<DemoInfo> demoInfos = queryDemoDevice();
        if (demoInfos != null){
            for (DemoInfo demoInfo : demoInfos){
                deleteDemoDevice(demoInfo.getId());
            }
        }
    }

    /**
     * 查询所有演示设备
     * @return
     */
    public List<DemoInfo> queryDemoDevice(){
        String sql = "select * from " + SQLiteHelper.DEMO_LIST_BACKUP_TABLE_NAME + " order by id ASC";
        List<DemoInfo> demoInfos = DBHelper.getInstance().query(sql, null, new DBHelper.QueryListener<DemoInfo>() {
            @Override
            public List<DemoInfo> queryOk(Cursor c) {
                List<DemoInfo> demoInfoList = new ArrayList<>();
                if (c != null){
                    DemoInfo demoInfo = null;
                    while (c.moveToNext()){
                        demoInfo = new DemoInfo();
                        demoInfo.setId(c.getString(c.getColumnIndex("demo_id")));
                        demoInfo.setDevice_name(c.getString(c.getColumnIndex("device_name")));
                        demoInfo.setNum(c.getString(c.getColumnIndex("num")));
                        demoInfo.setDeviceType(c.getString(c.getColumnIndex("deviceType")));
                        demoInfo.setVr(c.getInt(c.getColumnIndex("is_vr")) == 1);
                        demoInfoList.add(demoInfo);
                    }
                }
                return demoInfoList;
            }

            @Override
            public void error() {

            }
        });
        return demoInfos;
    }

    /**
     * 缓存用户信息
     * 第三方登录暂不需要缓存用户信息
     * @param account
     * @param userInfo
     */
    public void insertUserInfo(String account, UserInfo userInfo){
        if (TextUtils.isEmpty(account) || userInfo == null)return;
        deleteUserInfo(account);
        ContentValues values = new ContentValues();
        values.put("account",account);
        values.put("phoneNumber",userInfo.getPhoneNumber());
        values.put("email",userInfo.getEmail());
        values.put("ifBind",userInfo.getIfBind());
        values.put("realName",userInfo.getRealName());
        values.put("subName",userInfo.getSubName());
        values.put("userIconPath",userInfo.getUserIconPath());
        values.put("userId",userInfo.getUserId());
        DBHelper.getInstance().insert(SQLiteHelper.USER_INFO_TABLE_NAME,values);
    }

    /**
     * 删除缓存的用户信息
     * @param account
     */
    public void deleteUserInfo(String account){
        if (TextUtils.isEmpty(account))return;
        String where = " account = ? ";
        DBHelper.getInstance().delete(SQLiteHelper.USER_INFO_TABLE_NAME,
                where,
                new String[]{account});
    }

    /**
     * 查询缓存的用户信息
     * @param account
     * @return
     */
    public UserInfo queryUserInfo(final String account){
        if (account == null){
            return null;
        }
        String sql = "select * from " + SQLiteHelper.USER_INFO_TABLE_NAME + " where account = ? order by id desc";
        List<UserInfo> userInfo = DBHelper.getInstance().query(sql, new String[]{account}, new DBHelper.QueryListener<UserInfo>() {
            @Override
            public List<UserInfo> queryOk(Cursor c) {
                if (c != null){
                    List<UserInfo> userInfoList = new ArrayList<>();
                    UserInfo info = null;
                    while (c.moveToNext()){
                        info = new UserInfo();
                        info.setEmail(c.getString(c.getColumnIndex("email")));
                        info.setPhoneNumber(c.getString(c.getColumnIndex("phoneNumber")));
                        info.setIfBind(c.getString(c.getColumnIndex("ifBind")));
                        info.setRealName(c.getString(c.getColumnIndex("realName")));
                        info.setSubName(c.getString(c.getColumnIndex("subName")));
                        info.setUserIconPath(c.getString(c.getColumnIndex("userIconPath")));
                        info.setUserId(c.getString(c.getColumnIndex("userId")));
                        userInfoList.add(info);
                    }
                    return userInfoList;
                }
                return null;
            }

            @Override
            public void error() {

            }
        });
        if (userInfo != null && userInfo.size() > 0){
            return userInfo.get(0);
        }
        return null;
    }

    /**
     * 插入截图或录像
     * @param imageInfo
     */
    public synchronized void insertDeviceMedia(ImageInfo imageInfo){
        if (imageInfo == null)return;
        ContentValues values = new ContentValues();
        values.put("path",imageInfo.getPath());
        values.put("name",imageInfo.getdName());
        values.put("fname",imageInfo.getFileName());
        values.put("time",imageInfo.getTime() + "");
        values.put("device_id",imageInfo.getdId());
        values.put("device_channel",imageInfo.getChannel() + "");
        values.put("playMode",imageInfo.getPlayMode());
        values.put("type",imageInfo.getTag());
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        values.put("userId",userId);
        KLog.getInstance().d("insertDeviceMedia values name = %s",values).print();
        DBHelper.getInstance().insert(SQLiteHelper.MEDIA_DEVICE_TABLE_NAME,values);
    }

    /**
     * 删除截图或录像
     * @param path
     * @param fileName
     */
    public void deleteDeviceMedia(String path, String fileName){
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(fileName))return;
        String where = " path = ? and fname = ? ";
        DBHelper.getInstance().delete(SQLiteHelper.MEDIA_DEVICE_TABLE_NAME,
                where,
                new String[]{path,fileName});
    }

    /**
     * 查询截图和录像
     * @return
     */
    public List<ImageInfo> queryDeviceMedia(){
        String sql = "select * from " + SQLiteHelper.MEDIA_DEVICE_TABLE_NAME + " order by id desc";
        List<ImageInfo> imageInfos = DBHelper.getInstance().query(sql, null, new DBHelper.QueryListener<ImageInfo>() {
            @Override
            public List<ImageInfo> queryOk(Cursor c) {
                if (c != null){
                    List<ImageInfo> medias = new ArrayList<>();
                    ImageInfo info = null;
                    File file = null;
                    while (c.moveToNext()){
                        info = new ImageInfo();
                        info.setPath(c.getString(c.getColumnIndex("path")));
                        info.setdName(c.getString(c.getColumnIndex("name")));
                        info.setFileName(c.getString(c.getColumnIndex("fname")));
                        info.setTag(c.getInt(c.getColumnIndex("type")));

                        if (TextUtils.isEmpty(info.getPath()) || TextUtils.isEmpty(info.getdName()) || TextUtils.isEmpty(info.getFileName())){
                            continue;
                        }

                        if (!TextUtils.isEmpty(info.getFileName())){
                            file = new File(info.getPath(),info.getFileName());
                            if (file == null || !file.exists()){
                                if (info.getTag() == ImageInfo.IMG){
                                    deleteDeviceMedia(info.getPath(),info.getFileName());
                                    KLog.getInstance().d("DBAction queryDeviceMedia insert imginfo = %s",info).print();
                                    continue;
                                }
                            }
                        } else {
                            KLog.getInstance().d("DBAction queryDeviceMedia insert imginfo = %s",info).print();
                            continue;
                        }

                        info.setdId(c.getString(c.getColumnIndex("device_id")));
                        info.setPlayMode(c.getInt(c.getColumnIndex("playMode")));

                        long time = 0L;
                        String t = c.getString(c.getColumnIndex("time"));
                        if (!TextUtils.isEmpty(t)){
                            try {
                                time = Long.valueOf(t);
                            } catch (Exception e){
                                e.printStackTrace();
                                time = 0L;
                            }
                        }
                        info.setTime(time);

                        if (info.getTag() == ImageInfo.VIDEO){
                            if (info.getPath().contains("/p2p/h264/")){
                                //老FreeIP中的数据
                                final String root = FileHelper.getPhoneRootPath();
                                File img = new File(root + "p2p/image/",info.getFileName().replace("h264", "jpeg"));
                                if (img == null || !img.exists()){
                                    deleteDeviceMedia(info.getPath(),info.getFileName());
                                    KLog.getInstance().d("DBAction queryDeviceMedia insert imginfo = %s",info).print();
                                    continue;
                                }
                                File mp4File = new File(root + "p2p/video/",info.getFileName().replace("h264", "mp4"));
                                if (mp4File == null || !mp4File.exists()){
                                    deleteDeviceMedia(info.getPath(),info.getFileName());
                                    KLog.getInstance().d("DBAction queryDeviceMedia insert imginfo = %s",info).print();
                                    continue;
                                }
                                info.setImgPath(img.getAbsolutePath());
                                info.setH264Path(file.getAbsolutePath());
                                info.setMp4Path(mp4File.getAbsolutePath());
                            } else {
                                File img = new File(FileManager.getMediaVideoImg(),info.getFileName().replace("h264", "jpeg"));
                                if (img == null || !img.exists()){
                                    deleteDeviceMedia(info.getPath(),info.getFileName());
                                    KLog.getInstance().d("DBAction queryDeviceMedia insert imginfo = %s",info).print();
                                    continue;
                                }
                                String mp4 = FileManager.getMediaVideoDir(FileManager.PREVIEW_DIR);
                                switch (info.getPlayMode()){
                                    case EqupInfo.PRE_VIEW_MODE:
                                    case EqupInfo.PRE_VIEW_MODE | EqupInfo.LOCALE_MODE:
                                    case EqupInfo.DIRECT_MODE:
                                        mp4 = FileManager.getMediaVideoDir(FileManager.PREVIEW_DIR);
                                        break;
                                    case EqupInfo.PLAY_BACK_MODE:
                                    case EqupInfo.PLAY_BACK_MODE | EqupInfo.LOCALE_MODE:
                                        mp4 = FileManager.getMediaVideoDir(FileManager.PLAYBACK_DIR);
                                        break;
                                    case EqupInfo.VR_MODE:
                                    case EqupInfo.VR_MODE | EqupInfo.LOCALE_MODE:
                                        mp4 = FileManager.getMediaVideoDir(FileManager.VR_DIR);
                                        break;
                                }
                                File mp4File = new File(mp4,info.getFileName().replace("h264", "mp4"));
                                if (mp4File == null || !mp4File.exists()){
                                    deleteDeviceMedia(info.getPath(),info.getFileName());
                                    KLog.getInstance().d("DBAction queryDeviceMedia insert imginfo = %s",info).print();
                                    continue;
                                }
                                info.setImgPath(img.getAbsolutePath());
                                info.setH264Path(file.getAbsolutePath());
                                info.setMp4Path(mp4File.getAbsolutePath());
                            }
                        } else {
                            info.setImgPath(file.getAbsolutePath());
                        }

                        info.setChannel(c.getString(c.getColumnIndex("device_channel")));
                        info.setCurrUser(c.getString(c.getColumnIndex("userId")));
                        KLog.getInstance().d("DBAction queryDeviceMedia query imginfo = %s",info).print();
                        medias.add(info);
                    }
                    return medias;
                }
                return null;
            }

            @Override
            public void error() {
            }
        });
        return imageInfos;
    }

    public List<EqupInfo> queryPlayCache(int playMode){
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        String sql = "";
        String[] args = null;
        switch (playMode){
            case EqupInfo.PRE_VIEW_MODE:
                sql = "select * from " + SQLiteHelper.PLAY_CACHE_TABLE_NAME + " where userid = ? and playMode = ? " +
                " UNION " + " select * from " + SQLiteHelper.PLAY_CACHE_TABLE_NAME + " where playMode = ?  order by id ASC ";
                args = new String[]{userId,EqupInfo.PRE_VIEW_MODE + "",EqupInfo.DIRECT_MODE + ""};
                break;
            case EqupInfo.PLAY_BACK_MODE:
                sql = "select * from " + SQLiteHelper.PLAY_CACHE_TABLE_NAME + " where userid = ? and playMode = ? order by id ASC ";
                args = new String[]{userId,EqupInfo.PLAY_BACK_MODE + ""};
                break;
            case EqupInfo.VR_MODE:
                sql = "select * from " + SQLiteHelper.PLAY_CACHE_TABLE_NAME + " where userid = ? and playMode = ? order by id ASC ";
                args = new String[]{userId,EqupInfo.VR_MODE + ""};
                break;
//            case EqupInfo.DIRECT_MODE:
//                break;
        }
        List<EqupInfo> equpInfos = new ArrayList<>();
        try {
            equpInfos = DBHelper.getInstance().query(sql, args, new DBHelper.QueryListener<EqupInfo>() {
                @Override
                public List<EqupInfo> queryOk(Cursor c) {
                    return getDeviceFromCursor(c,false);
                }

                @Override
                public void error() {
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            SQLiteHelper.reCreatePlayCacheTable();
            equpInfos = new ArrayList<>();
        }
        if (equpInfos == null){
            equpInfos = new ArrayList<>();
        }
        return equpInfos;
    }

    public void insertPlayCache(List<EqupInfo> devices){
        if (devices == null || devices.size() == 0)return;
        for (EqupInfo equpInfo : devices){
            deletePlayCache(equpInfo);
        }
        for (EqupInfo equpInfo : devices){
            insertPlayCache(equpInfo);
        }
    }

    private void deletePlayCache(EqupInfo device){
        if (device == null)return;
        if (device.isDirect()){
            String where = " isDirect = ? ";
            DBHelper.getInstance().delete(SQLiteHelper.PLAY_CACHE_TABLE_NAME,where,new String[]{"1"});
            String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
            String where2 = " userid = ? and playMode = ? ";
            DBHelper.getInstance().delete(SQLiteHelper.PLAY_CACHE_TABLE_NAME,where2,new String[]{userId,EqupInfo.PRE_VIEW_MODE + ""});
        } else {
            String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
            String where = " userid = ? and playMode = ? ";
            DBHelper.getInstance().delete(SQLiteHelper.PLAY_CACHE_TABLE_NAME,where,new String[]{userId,device.getPlayMode() + ""});
            if (device.getPlayMode() == EqupInfo.PRE_VIEW_MODE){
                String where2 = " isDirect = ? ";
                DBHelper.getInstance().delete(SQLiteHelper.PLAY_CACHE_TABLE_NAME,where2,new String[]{"1"});
            }
        }
    }

    public void updatePlayCacheDirect(EqupInfo equpInfo,boolean insert){
        if (TextUtils.isEmpty(equpInfo.getDeviceConnectServer()) || !equpInfo.isDirect())return;
        String where = " deviceConnectServer = ? ";
        DBHelper.getInstance().delete(SQLiteHelper.PLAY_CACHE_TABLE_NAME,where,new String[]{equpInfo.getDeviceConnectServer()});
        if (insert){
            insertPlayCache(equpInfo);
        }
    }

    public void updatePlayCacheDirectAccountPassword(EqupInfo device, String userId, String oldUrl){
        if (device == null)return;
        if (device.isDirect()){
            if (TextUtils.isEmpty(device.getDeviceConnectServer()))return;
            String where = " deviceConnectServer = ? ";
            ContentValues values = new ContentValues();
            values.put("userid",userId);
            values.put("equpId",device.getEqupId());
            values.put("ifOnLine",device.getIfOnLine());
            values.put("ifBind",device.getIfBind());
            values.put("equoModle",device.getEquoModle());
            values.put("version",device.getVersion());
            values.put("checkStr",device.getCheckStr());
            values.put("deviceName",device.getDeviceName());
            values.put("deviceType",device.getDeviceType());
            values.put("sysVersion",device.getSysVersion());
            values.put("deviceDetatilType",device.getDeviceDetatilType());
            values.put("cateId",device.getCateId());
            values.put("useCloudStorage",device.getUseCloudStorage());
            values.put("canUseCloudStorage",device.getCanUseCloudStorage());
            values.put("cateName",device.getCateName());
            values.put("deviceConnectServer",device.getDeviceConnectServer());
            values.put("localPwd",device.getLocalPwd());
            values.put("localUser",device.getLocalUser());
            values.put("order_num",device.getOrder_num());
            values.put("localeDeviceIp",device.getLocaleDeviceIp());
            values.put("localeDevicePort",device.getLocaleDevicePort());
            values.put("isDirect",(device.isDirect()) ? "1" : "0");
            values.put("PrivateServer",device.getPrivateServer());
            values.put("mode",device.getAbsMode());
            values.put("deviceStreams",device.getDeviceStreams());
            values.put("port",device.getPort());
            values.put("remotePlay",(device.isRemotePlay()) ? "1" : "0");
            values.put("h264_plus",device.getH264_plus());
            values.put("h265_plus",device.getH265_plus());
            values.put("can_use_cloud_storage",device.getCan_use_cloud_storage());
            values.put("use_cloud_storage",device.getUse_cloud_storage());
            values.put("replay_data_rate",device.getReplay_data_rate());
            values.put("replay_video_type",device.getReplay_video_type());
            values.put("channel_name",device.getChannelName());
            values.put("isSNLocaleDevice",device.getIsSNLocaleDevice());
            if (device.getSharePermision() != null){
                try {
                    JSONArray array = new JSONArray();
                    JSONObject jsonObject = null;
                    for (Map.Entry<Integer, String> m : device.getSharePermision().entrySet()){
                        if (m != null){
                            jsonObject = new JSONObject();
                            jsonObject.put("channel",m.getKey());
                            jsonObject.put("share_permssion",m.getValue());
                            array.put(jsonObject);
                        }
                    }
                    values.put("share_permision",array.toString());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            DBHelper.getInstance().update(SQLiteHelper.PLAY_CACHE_TABLE_NAME,values,where,new String[]{oldUrl});
        }
    }

    private void insertPlayCache(EqupInfo device){
        if (device == null)return;
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        if ((device.getPlayMode() & EqupInfo.LOCALE_MODE) != 0)return;//局域网播放不保存
        ContentValues values = new ContentValues();
        values.put("userid",userId);
        values.put("equpId",device.getEqupId());
        values.put("ifOnLine",device.getIfOnLine());
        values.put("ifBind",device.getIfBind());
        values.put("equoModle",device.getEquoModle());
        values.put("version",device.getVersion());
        values.put("checkStr",device.getCheckStr());
        values.put("deviceName",device.getDeviceName());
        values.put("deviceType",device.getDeviceType());
        values.put("sysVersion",device.getSysVersion());
        values.put("deviceDetatilType",device.getDeviceDetatilType());
        values.put("cateId",device.getCateId());
        values.put("useCloudStorage",device.getUseCloudStorage());
        values.put("canUseCloudStorage",device.getCanUseCloudStorage());
        values.put("cateName",device.getCateName());
        values.put("deviceConnectServer",device.getDeviceConnectServer());
        values.put("localPwd",device.getLocalPwd());
        values.put("localUser",device.getLocalUser());
        values.put("order_num",device.getOrder_num());
        values.put("localeDeviceIp",device.getLocaleDeviceIp());
        values.put("localeDevicePort",device.getLocaleDevicePort());
        values.put("isDirect",(device.isDirect()) ? "1" : "0");
        values.put("PrivateServer",device.getPrivateServer());
        values.put("mode",device.isIPC() ? 1 : device.getAbsMode());
        values.put("deviceStreams",device.getDeviceStreams());
        values.put("port",device.getPort());
        values.put("remotePlay",(device.isRemotePlay()) ? "1" : "0");
        values.put("h264_plus",device.getH264_plus());
        values.put("h265_plus",device.getH265_plus());
        values.put("can_use_cloud_storage",device.getCan_use_cloud_storage());
        values.put("use_cloud_storage",device.getUse_cloud_storage());
        values.put("replay_data_rate",device.getReplay_data_rate());
        values.put("replay_video_type",device.getReplay_video_type());
        values.put("channel_name",device.getChannelName());
        values.put("playMode",device.getPlayMode() + "");
        values.put("isSNLocaleDevice",device.getIsSNLocaleDevice());
        if (device.getSharePermision() != null){
            try {
                JSONArray array = new JSONArray();
                JSONObject jsonObject = null;
                for (Map.Entry<Integer, String> m : device.getSharePermision().entrySet()){
                    if (m != null){
                        jsonObject = new JSONObject();
                        jsonObject.put("channel",m.getKey());
                        jsonObject.put("share_permssion",m.getValue());
                        array.put(jsonObject);
                    }
                }
                values.put("share_permision",array.toString());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        DBHelper.getInstance().insert(SQLiteHelper.PLAY_CACHE_TABLE_NAME,values);
    }

    public void insertLocaleDevice(String deviceId, String account, String password){
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(account))return;
        if (password == null){
            password = "";
        }
        deleteLocaleDevice(deviceId);
        ContentValues values = new ContentValues();
        values.put("device_id",deviceId);
        values.put("account",account);
        values.put("password",password);
        DBHelper.getInstance().insert(SQLiteHelper.LOCALE_DEVICE_TABLE_NAME,values);
    }

    private void deleteLocaleDevice(String deviceId){
        if (TextUtils.isEmpty(deviceId))return;
        String where = " device_id = ? ";
        DBHelper.getInstance().delete(SQLiteHelper.LOCALE_DEVICE_TABLE_NAME,where,new String[]{deviceId});
    }

    public String queryLocaleDeviceAccount(String deviceId){
        if (TextUtils.isEmpty(deviceId))return "";
        String sql = "select * from " + SQLiteHelper.LOCALE_DEVICE_TABLE_NAME + " where device_id = ? order by id desc";
        List<String> account = DBHelper.getInstance().query(sql, new String[]{deviceId}, new DBHelper.QueryListener<String>() {
            @Override
            public List<String> queryOk(Cursor c) {
                if (c != null){
                    List<String> accounts = new ArrayList<>();
                    String account = "";
                    while (c.moveToNext()){
                        account = c.getString(c.getColumnIndex("account"));
                        accounts.add(account);
                    }
                    return accounts;
                }
                return null;
            }

            @Override
            public void error() {
            }
        });
        if (account == null){
            return "";
        } else {
            if (account.size() > 0){
                return account.get(0);
            } else {
                return "";
            }
        }
    }

    /**
     * 序列号播放保存到本地
     * 暂时取消
     * @param device
     */
    public void insertSNPlayerDevice(EqupInfo device){
        if (device == null)return;
        BitdogInterface.getInstance().exec(BitdogCmd.DELETE_SN_PLAY_CACHE_CMD, String.format("{\"device_id\":\"%s\"}",device.getEqupId()), BitdogInterface.SYNC_HANDLE);
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        if ((device.getPlayMode() & EqupInfo.LOCALE_MODE) != 0)return;//局域网播放不保存
        ContentValues values = new ContentValues();
        values.put("userid",userId);
        values.put("equpId",device.getEqupId());
        values.put("ifOnLine",device.getIfOnLine());
        values.put("ifBind",device.getIfBind());
        values.put("equoModle",device.getEquoModle());
        values.put("version",device.getVersion());
        values.put("checkStr",device.getCheckStr());
        values.put("deviceName",device.getDeviceName());
        values.put("deviceType",device.getDeviceType());
        values.put("sysVersion",device.getSysVersion());
        values.put("deviceDetatilType",device.getDeviceDetatilType());
        values.put("cateId",device.getCateId());
        values.put("useCloudStorage",device.getUseCloudStorage());
        values.put("canUseCloudStorage",device.getCanUseCloudStorage());
        values.put("cateName",device.getCateName());
        values.put("deviceConnectServer",device.getDeviceConnectServer());
        values.put("localPwd",device.getLocalPwd());
        values.put("localUser",device.getLocalUser());
        values.put("order_num",device.getOrder_num());
        values.put("localeDeviceIp",device.getLocaleDeviceIp());
        values.put("localeDevicePort",device.getLocaleDevicePort());
        values.put("isDirect","0");
        values.put("PrivateServer",device.getPrivateServer());
        values.put("mode",device.getAbsMode());
        values.put("deviceStreams",device.getDeviceStreams());
        values.put("port",device.getPort());
        values.put("remotePlay","0");
        values.put("h264_plus",device.getH264_plus());
        values.put("h265_plus",device.getH265_plus());
        values.put("can_use_cloud_storage",device.getCan_use_cloud_storage());
        values.put("use_cloud_storage",device.getUse_cloud_storage());
        values.put("replay_data_rate",device.getReplay_data_rate());
        values.put("replay_video_type",device.getReplay_video_type());
        values.put("channel_name",device.getChannelName());
        values.put("playMode",device.getPlayMode() + "");
        values.put("isSNLocaleDevice",1);
        if (device.getSharePermision() != null){
            try {
                JSONArray array = new JSONArray();
                JSONObject jsonObject = null;
                for (Map.Entry<Integer, String> m : device.getSharePermision().entrySet()){
                    if (m != null){
                        jsonObject = new JSONObject();
                        jsonObject.put("channel",m.getKey());
                        jsonObject.put("share_permssion",m.getValue());
                        array.put(jsonObject);
                    }
                }
                values.put("share_permision",array.toString());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        DBHelper.getInstance().insert(SQLiteHelper.SN_LOGIN_CACHE_TABLE_NAME,values);
    }

    public void deleteSNPlayerDevice(String deviceId){
        if (TextUtils.isEmpty(deviceId))return;
        String where = " equpId = ? ";
        DBHelper.getInstance().delete(SQLiteHelper.SN_LOGIN_CACHE_TABLE_NAME,where,new String[]{deviceId});
    }

    public List<EqupInfo> querySNPlayerDevice(){
        String sql = "select * from " + SQLiteHelper.SN_LOGIN_CACHE_TABLE_NAME + " order by id desc";
        List<EqupInfo> devices = DBHelper.getInstance().query(sql, null, new DBHelper.QueryListener<EqupInfo>() {
            @Override
            public List<EqupInfo> queryOk(Cursor c) {
                if (c != null){
                    List<EqupInfo> devices = new ArrayList<>();
                    EqupInfo device = null;
                    while (c.moveToNext()){
                        device = new EqupInfo();
                        device.setEqupId(c.getString(c.getColumnIndex("equpId")));
                        device.setIfOnLine(c.getString(c.getColumnIndex("ifOnLine")));
                        device.setIfBind(c.getString(c.getColumnIndex("ifBind")));
                        device.setEquoModle(c.getString(c.getColumnIndex("equoModle")));
                        device.setVersion(c.getString(c.getColumnIndex("version")));
                        device.setCheckStr(c.getString(c.getColumnIndex("checkStr")));
                        device.setDeviceName(c.getString(c.getColumnIndex("deviceName")));
                        device.setDeviceType(c.getString(c.getColumnIndex("deviceType")));
                        device.setSysVersion(c.getString(c.getColumnIndex("sysVersion")));
                        device.setDeviceDetatilType(c.getString(c.getColumnIndex("deviceDetatilType")));
                        device.setCateId(c.getString(c.getColumnIndex("cateId")));
                        device.setUseCloudStorage(c.getString(c.getColumnIndex("useCloudStorage")));
                        device.setCanUseCloudStorage(c.getString(c.getColumnIndex("canUseCloudStorage")));
                        device.setCateName(c.getString(c.getColumnIndex("cateName")));
                        device.setDeviceConnectServer(c.getString(c.getColumnIndex("deviceConnectServer")));
                        device.setLocalPwd(c.getString(c.getColumnIndex("localPwd")));
                        device.setLocalUser(c.getString(c.getColumnIndex("localUser")));
                        device.setOrder_num(c.getString(c.getColumnIndex("order_num")));
                        device.setLocaleDeviceIp(c.getString(c.getColumnIndex("localeDeviceIp")));
                        String direct = c.getString(c.getColumnIndex("isDirect"));
                        device.setDirect("1".equals(direct));
                        String remote = c.getString(c.getColumnIndex("remotePlay"));
                        device.setRemotePlay("1".equals(remote));
                        device.setPrivateServer(c.getString(c.getColumnIndex("PrivateServer")));
                        device.setH264_plus(c.getString(c.getColumnIndex("h264_plus")));
                        device.setH265_plus(c.getString(c.getColumnIndex("h265_plus")));
                        device.setCan_use_cloud_storage(c.getString(c.getColumnIndex("can_use_cloud_storage")));
                        device.setCan_use_cloud_storage(c.getString(c.getColumnIndex("use_cloud_storage")));
                        device.setLocaleDevicePort(c.getInt(c.getColumnIndex("localeDevicePort")));
                        device.setMode(c.getInt(c.getColumnIndex("mode")));
                        device.setDeviceStreams(Integer.valueOf(c.getString(c.getColumnIndex("deviceStreams"))));
                        device.setPort(c.getInt(c.getColumnIndex("port")));
                        device.setLocaleSaveDevice(true);
                        device.setIsSNLocaleDevice(1);
                        int index = c.getColumnIndex("playMode");
                        if (index > 0){
                            String m = c.getString(index);
                            try {
                                int mode = Integer.valueOf(m);
                                device.setPlayMode(mode);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        //生成通道
                        if (device != null && !device.isIPC()) {
                            int num = device.getChannelSum();
                            List<ChannelInfoEntity> channelInfoEntities = new ArrayList<>();
                            ChannelInfoEntity channelInfoEntity = null;
                            for (int i = 1; i <= num; i++) {
                                channelInfoEntity = new ChannelInfoEntity();
                                channelInfoEntity.setDevice_id(device.getEqupId());
                                channelInfoEntity.setChannel_name("CH" + i);
                                channelInfoEntity.setChannel(i);
                                channelInfoEntity.setOrder_num(i);
                                channelInfoEntities.add(channelInfoEntity);
                            }
                            device.setInfoEntitys(channelInfoEntities);
                        }

                        devices.add(device);
                    }
                    return devices;
                }
                return null;
            }

            @Override
            public void error() {
            }
        });
        return devices;
    }

    /**
     * 收藏某个播放窗口的设备(只收藏单个通道或设备)
     * @param device
     */
    public void insertFavoritesPlayerDevice(EqupInfo device){
        if (device == null)return;
        deleteFavoritesPlayerDevice(device.getEqupId(),device.getAbsMode());
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        if ((device.getPlayMode() & EqupInfo.LOCALE_MODE) != 0)return;//局域网播放不保存
        if (device.checkIsSNLocaleDevice())return;//本地序列号设备不收藏
        if (!device.isIPC()){
            String query = "select * from " + SQLiteHelper.FAVORITES_CACHE_TABLE_NAME + " where userid = ? and equpId = ? order by id desc";
            List<EqupInfo> devices = DBHelper.getInstance().query(query, new String[]{userId,device.getEqupId()}, new DBHelper.QueryListener<EqupInfo>() {
                @Override
                public List<EqupInfo> queryOk(Cursor c) {
                    if (c != null){
                        return getDeviceFromCursor(c,false);
                    }
                    return null;
                }

                @Override
                public void error() {
                }
            });
            if (devices != null && devices.size() > 0){
                insertFavoritesDeviceChannel(device);
                return;
            }
        }
        ContentValues values = new ContentValues();
        values.put("userid",userId);
        values.put("equpId",device.getEqupId());
        values.put("ifOnLine",device.getIfOnLine());
        values.put("ifBind",device.getIfBind());
        values.put("equoModle",device.getEquoModle());
        values.put("version",device.getVersion());
        values.put("checkStr",device.getCheckStr());
        values.put("deviceName",device.getDeviceName());
        values.put("deviceType",device.getDeviceType());
        values.put("sysVersion",device.getSysVersion());
        values.put("deviceDetatilType",device.getDeviceDetatilType());
        values.put("cateId",device.getCateId());
        values.put("useCloudStorage",device.getUseCloudStorage());
        values.put("canUseCloudStorage",device.getCanUseCloudStorage());
        values.put("cateName",device.getCateName());
        values.put("deviceConnectServer",device.getDeviceConnectServer());
        values.put("localPwd",device.getLocalPwd());
        values.put("localUser",device.getLocalUser());
        values.put("order_num",device.getOrder_num());
        values.put("localeDeviceIp",device.getLocaleDeviceIp());
        values.put("localeDevicePort",device.getLocaleDevicePort());
        values.put("isDirect","0");
        values.put("PrivateServer",device.getPrivateServer());
        values.put("mode",device.getAbsMode());
        values.put("deviceStreams",device.getDeviceStreams());
        values.put("port",device.getPort());
        values.put("remotePlay","0");
        values.put("h264_plus",device.getH264_plus());
        values.put("h265_plus",device.getH265_plus());
        values.put("can_use_cloud_storage",device.getCan_use_cloud_storage());
        values.put("use_cloud_storage",device.getUse_cloud_storage());
        values.put("replay_data_rate",device.getReplay_data_rate());
        values.put("replay_video_type",device.getReplay_video_type());
        values.put("channel_name",device.getChannelName());
        values.put("playMode",device.getPlayMode() + "");
        values.put("isSNLocaleDevice",device.getIsSNLocaleDevice());
        if (device.getSharePermision() != null){
            try {
                JSONArray array = new JSONArray();
                JSONObject jsonObject = null;
                for (Map.Entry<Integer, String> m : device.getSharePermision().entrySet()){
                    if (m != null){
                        jsonObject = new JSONObject();
                        jsonObject.put("channel",m.getKey());
                        jsonObject.put("share_permssion",m.getValue());
                        array.put(jsonObject);
                    }
                }
                values.put("share_permision",array.toString());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        DBHelper.getInstance().insert(SQLiteHelper.FAVORITES_CACHE_TABLE_NAME,values);
        insertFavoritesDeviceChannel(device);
    }

    private void insertFavoritesDeviceChannel(EqupInfo equpInfo){
        if (equpInfo == null)return;
        if (equpInfo.isDirect())return;
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        if (userId == null){
            userId = "";
        }
        DBHelper.getInstance().delete(SQLiteHelper.FAVORITES_DEVICE_CHANNEL_TABLE_NAME,
                " userid = ? and device_id = ? and channel = ? ",new String[]{userId,equpInfo.getEqupId(),equpInfo.getAbsMode() + ""});
        ContentValues values = new ContentValues();
        values.put("device_id",equpInfo.getEqupId());
        values.put("dc_id","");
        values.put("userid",userId);
        values.put("channel_name",equpInfo.getChannelName());
        values.put("channel",equpInfo.getAbsMode());
        values.put("order_num",equpInfo.getAbsMode());
        values.put("alarm_open",0);
        values.put("replay_data_rate",equpInfo.getReplay_data_rate());
        values.put("replay_video_type",equpInfo.getReplay_video_type());
        DBHelper.getInstance().insert(SQLiteHelper.FAVORITES_DEVICE_CHANNEL_TABLE_NAME,values);
    }

    private List<ChannelInfoEntity> queryFavoritesDeviceChannels(String device_id){
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        String sql = "select * from " + SQLiteHelper.FAVORITES_DEVICE_CHANNEL_TABLE_NAME + " where userid = ? and device_id = ? order by id desc";
        List<ChannelInfoEntity> channelInfoEntities = DBHelper.getInstance().query(sql, new String[]{userId,device_id}, new DBHelper.QueryListener<ChannelInfoEntity>() {
            @Override
            public List<ChannelInfoEntity> queryOk(Cursor c) {
                List<ChannelInfoEntity> channelInfoEntityList = new ArrayList<>();
                if (c != null){
                    ChannelInfoEntity channelInfoEntity = null;
                    while (c.moveToNext()){
                        channelInfoEntity = new ChannelInfoEntity();
                        channelInfoEntity.setOrder_num(c.getInt(c.getColumnIndex("order_num")));
                        channelInfoEntity.setChannel(c.getInt(c.getColumnIndex("channel")));
                        channelInfoEntity.setChannel_name(c.getString(c.getColumnIndex("channel_name")));
                        channelInfoEntity.setDevice_id(c.getString(c.getColumnIndex("device_id")));
                        channelInfoEntity.setAlarm_open((c.getInt(c.getColumnIndex("alarm_open")) == 1));
                        channelInfoEntity.setDc_id(c.getInt(c.getColumnIndex("dc_id")));
                        channelInfoEntity.setReplay_data_rate(c.getInt(c.getColumnIndex("replay_data_rate")));
                        channelInfoEntity.setReplay_video_type(c.getInt(c.getColumnIndex("replay_video_type")));
                        channelInfoEntityList.add(channelInfoEntity);
                    }
                }
                return channelInfoEntityList;
            }

            @Override
            public void error() {
            }
        });
        return channelInfoEntities;
    }

    public void deleteFavoritesPlayerDevice(String deviceId, int channel){
        if (TextUtils.isEmpty(deviceId))return;
        List<ChannelInfoEntity> channelInfoEntities = queryFavoritesDeviceChannels(deviceId);
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        if (channelInfoEntities == null || channelInfoEntities.size() == 0
                || (channelInfoEntities.size() == 1 && channelInfoEntities.get(0) != null && channelInfoEntities.get(0).getChannel() == channel)){
            String where = " userid = ? and equpId = ? and mode = ? ";
            if (channelInfoEntities != null && channelInfoEntities.size() == 1) {
                String whereChannel = " userid = ? and device_id = ? and channel = ? ";
                DBHelper.getInstance().delete(SQLiteHelper.FAVORITES_DEVICE_CHANNEL_TABLE_NAME,whereChannel,new String[]{userId,deviceId,channel + ""});
            }
            DBHelper.getInstance().delete(SQLiteHelper.FAVORITES_CACHE_TABLE_NAME,where,new String[]{userId,deviceId,channel + ""});
        } else {
            String where = " userid = ? and device_id = ? and channel = ? ";
            DBHelper.getInstance().delete(SQLiteHelper.FAVORITES_DEVICE_CHANNEL_TABLE_NAME,where,new String[]{userId,deviceId,channel + ""});
        }
    }

    public void updateFavoritesPlayerDevice(String deviceId, int channel, String name){
        if (TextUtils.isEmpty(deviceId))return;
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        List<ChannelInfoEntity> channelInfoEntities = queryFavoritesDeviceChannels(deviceId);
        if (channelInfoEntities == null || channelInfoEntities.size() == 0){
            String where = " userid = ? and equpId = ? and mode = ? ";
            ContentValues values = new ContentValues();
            values.put("deviceName",name);
            DBHelper.getInstance().update(SQLiteHelper.FAVORITES_CACHE_TABLE_NAME,values,where,new String[]{userId,deviceId,channel + ""});
        } else {
            String where = " userid = ? and device_id = ? and channel = ? ";
            ContentValues values = new ContentValues();
            values.put("channel_name",name);
            DBHelper.getInstance().update(SQLiteHelper.FAVORITES_DEVICE_CHANNEL_TABLE_NAME,values,where,new String[]{userId,deviceId,channel + ""});
        }
    }

    public void deleteFavoritesPlayerDevice(String deviceId){
        if (TextUtils.isEmpty(deviceId))return;
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        String where = " userid = ? and equpId = ? ";
        DBHelper.getInstance().delete(SQLiteHelper.FAVORITES_CACHE_TABLE_NAME,where,new String[]{userId,deviceId});
        String whereChannel = " userid = ? and device_id = ? ";
        DBHelper.getInstance().delete(SQLiteHelper.FAVORITES_DEVICE_CHANNEL_TABLE_NAME,whereChannel,new String[]{userId,deviceId});
    }

    public List<EqupInfo> queryFavoritesPlayerDevice(){
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        String sql = "select * from " + SQLiteHelper.FAVORITES_CACHE_TABLE_NAME + " where userid = ? order by id desc";
        List<EqupInfo> devices = DBHelper.getInstance().query(sql, new String[]{userId}, new DBHelper.QueryListener<EqupInfo>() {
            @Override
            public List<EqupInfo> queryOk(Cursor c) {
                if (c != null){
                    return getDeviceFromCursor(c,false);
                }
                return null;
            }

            @Override
            public void error() {
            }
        });
        if (devices != null){
            List<ChannelInfoEntity> channelInfoEntities = null;
            Iterator<EqupInfo> equpInfoIterator = devices.iterator();
            EqupInfo equpInfo = null;
            while (equpInfoIterator.hasNext()){
                equpInfo = equpInfoIterator.next();
                if (equpInfo != null){
                    equpInfo.setLocaleSaveDevice(true);
                    if (!equpInfo.isIPC()){
                        channelInfoEntities = queryFavoritesDeviceChannels(equpInfo.getEqupId());
                        if (channelInfoEntities != null && channelInfoEntities.size() > 0){
                            equpInfo.setInfoEntitys(channelInfoEntities);
                        } else {
                            equpInfoIterator.remove();
                        }
                    }
                }
            }
        }
        return devices;
    }

    public boolean isFavorites(String deviceId, int channel){
        if (TextUtils.isEmpty(deviceId))return false;
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        String sql = "select * from " + SQLiteHelper.FAVORITES_CACHE_TABLE_NAME + " where userid = ? and equpId = ? order by id desc";
        List<EqupInfo> devices = DBHelper.getInstance().query(sql, new String[]{userId,deviceId}, new DBHelper.QueryListener<EqupInfo>() {
            @Override
            public List<EqupInfo> queryOk(Cursor c) {
                if (c != null){
                    return getDeviceFromCursor(c,false);
                }
                return null;
            }

            @Override
            public void error() {
            }
        });
        if (devices == null || devices.size() == 0){
            return false;
        } else {
            EqupInfo equpInfo = devices.get(0);
            if (equpInfo != null){
                if (equpInfo.isIPC()){
                    return true;
                } else {
                    List<ChannelInfoEntity> channelInfoEntities = queryFavoritesDeviceChannels(deviceId);
                    if (channelInfoEntities != null){
                        for (ChannelInfoEntity channelInfoEntity : channelInfoEntities){
                            if (channelInfoEntity != null && channelInfoEntity.getChannel() == channel){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void updateReplayStreamVideoTypeCache(String deviceId, int channel, int data_rate, int video_type){
        if (TextUtils.isEmpty(deviceId) || channel == -1)return;
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        String whereDevice = " userid = ? and equpId = ? and mode = ? ";
        String whereChannel = " userid = ? and device_id = ? and channel = ? ";

        ContentValues values = new ContentValues();
        if (data_rate != -1){
            values.put("replay_data_rate",data_rate);
        }
        if (video_type != -1){
            values.put("replay_video_type",video_type);
        }
        DBHelper.getInstance().update(SQLiteHelper.DEVICE_INFO_TABLE_NAME,values,whereDevice,new String[]{userId,deviceId,channel + ""});
        DBHelper.getInstance().update(SQLiteHelper.PLAY_CACHE_TABLE_NAME,values,whereDevice,new String[]{userId,deviceId,channel + ""});
        if (channel == 1){
            DBHelper.getInstance().update(SQLiteHelper.PLAY_CACHE_TABLE_NAME,values,whereDevice,new String[]{userId,deviceId,"0"});
        }
        DBHelper.getInstance().update(SQLiteHelper.SN_LOGIN_CACHE_TABLE_NAME,values,whereDevice,new String[]{userId,deviceId,channel + ""});
        DBHelper.getInstance().update(SQLiteHelper.FAVORITES_CACHE_TABLE_NAME,values,whereDevice,new String[]{userId,deviceId,channel + ""});

        DBHelper.getInstance().update(SQLiteHelper.DEVICE_INFO_CHANNEL_TABLE_NAME,values,whereChannel,new String[]{userId,deviceId,channel + ""});
        DBHelper.getInstance().update(SQLiteHelper.FAVORITES_DEVICE_CHANNEL_TABLE_NAME,values,whereChannel,new String[]{userId,deviceId,channel + ""});
    }

    public void updateDeviceUserPasswordCache(String deviceId, String user, String password){
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(user))return;
        if (password == null){
            password = "";
        }
        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
        String whereDevice = " userid = ? and equpId = ? ";

        ContentValues values = new ContentValues();
        values.put("localUser",user);
        values.put("localPwd",password);
        DBHelper.getInstance().update(SQLiteHelper.DEVICE_INFO_TABLE_NAME,values,whereDevice,new String[]{userId,deviceId});
        DBHelper.getInstance().update(SQLiteHelper.PLAY_CACHE_TABLE_NAME,values,whereDevice,new String[]{userId,deviceId});
        DBHelper.getInstance().update(SQLiteHelper.SN_LOGIN_CACHE_TABLE_NAME,values,whereDevice,new String[]{userId,deviceId});
        DBHelper.getInstance().update(SQLiteHelper.FAVORITES_CACHE_TABLE_NAME,values,whereDevice,new String[]{userId,deviceId});
    }

    /**
     * "UidRxBytes integer ," +    //当前软件接收的字节数
     "UidTxBytes integer ," +    //当前软件发送的字节数
     "wifi_RxBytes integer ," +     //WiFi接收的字节数
     "wifi_TxBytes integer ," +     //WiFi发送的字节数
     "mobile_RxBytes integer ," +   //手机流量接收的字节数
     "mobile_TxBytes integer ," +   //手机流量发送的字节数
     "time_date integer ," +         //当前记录的时间(当天零点的时间戳)
     "time integer)";                 //最后一次记录的时间戳
     */
    /**
     * 统计流量
     * @param netType 网络类型
     */
    public synchronized Flow updateFlowData(@NetUtils.NetType int netType) throws Exception {
        String sql = "select * from " + SQLiteHelper.FLOW_COMPUTE + " where time_date = ? order by id desc";
        long timeDate = getDayTime();
        List<Flow> flows = DBHelper.getInstance().query(sql, new String[]{timeDate + ""}, new DBHelper.QueryListener<Flow>() {
            @Override
            public List<Flow> queryOk(Cursor c) {
                List<Flow> flowList = new ArrayList<>();
                if (c != null){
                    Flow flow = null;
                    while (c.moveToNext()){
                        flow = new Flow();
                        flow.setUidRxBytes(c.getLong(c.getColumnIndex("UidRxBytes")));
                        flow.setUidTxBytes(c.getLong(c.getColumnIndex("UidTxBytes")));
                        flow.setWifi_RxBytes(c.getLong(c.getColumnIndex("wifi_RxBytes")));
                        flow.setWifi_TxBytes(c.getLong(c.getColumnIndex("wifi_TxBytes")));
                        flow.setMobile_RxBytes(c.getLong(c.getColumnIndex("mobile_RxBytes")));
                        flow.setMobile_TxBytes(c.getLong(c.getColumnIndex("mobile_TxBytes")));
                        flow.setMax_Bytes(c.getLong(c.getColumnIndex("max_Bytes")));
                        flow.setTips_size(c.getInt(c.getColumnIndex("tips_size")));
                        flow.setTime_date(c.getLong(c.getColumnIndex("time_date")));
                        flow.setTime(c.getLong(c.getColumnIndex("time")));
                        flowList.add(flow);
                    }
                }
                return flowList;
            }

            @Override
            public void error() {
            }
        });

        final int uid = BitdogInterface.getInstance().getApplicationContext().getApplicationInfo().uid;
        long uidRxBytes = TrafficStats.getUidRxBytes(uid);
        long uidTxBytes = TrafficStats.getUidTxBytes(uid);
        if (uidRxBytes == -1 || uidTxBytes == -1){
            uidRxBytes = getRxBytesManual(uid);
            uidTxBytes = getRxBytesManual(uid);
        }
        Flow lastFlow = null;
        ContentValues values = new ContentValues();
        if (flows == null || flows.size() == 0){
            values.put("UidRxBytes",uidRxBytes);
            values.put("UidTxBytes",uidTxBytes);
            values.put("wifi_RxBytes",0L);
            values.put("wifi_TxBytes",0L);
            values.put("mobile_RxBytes",0L);
            values.put("mobile_TxBytes",0L);
            values.put("max_Bytes", TrafficLimitValue.getInstance().getMax());//初始值 50M
            values.put("tips_size",0);
        } else {
            lastFlow = flows.get(0);
//            KLog.getInstance().d("SystemEventService DBAction updateFlowData flow = %s",lastFlow).print();
            long lastUidRxBytes = lastFlow.getUidRxBytes();
            long lastUidTxBytes = lastFlow.getUidTxBytes();

            values.put("UidRxBytes",uidRxBytes);
            values.put("UidTxBytes",uidTxBytes);

            long rxBytes = uidRxBytes - lastUidRxBytes;
            long plusRxBytes = 0L;
            if (rxBytes < 0L){//重新开机后得数据
                rxBytes = uidRxBytes + lastUidRxBytes;
                values.put("UidRxBytes",rxBytes);
                plusRxBytes = uidRxBytes;
            } else {
                plusRxBytes = rxBytes;
            }
            long txBytes = uidTxBytes - lastUidTxBytes;
            long plusTxBytes = 0L;
            if (txBytes < 0L){
                txBytes = uidTxBytes + + lastUidTxBytes;
                values.put("UidTxBytes",txBytes);
                plusTxBytes = uidTxBytes;
            } else {
                plusTxBytes = txBytes;
            }
            if (((netType & NetUtils.NETWORK_MOBILE) != 0)){
                values.put("mobile_RxBytes",lastFlow.getMobile_RxBytes() + plusRxBytes);
                values.put("mobile_TxBytes",lastFlow.getMobile_TxBytes() + plusTxBytes);
                if (lastFlow != null){
                    lastFlow.setMobile_RxBytes(lastFlow.getMobile_RxBytes() + plusRxBytes);
                    lastFlow.setMobile_TxBytes(lastFlow.getMobile_TxBytes() + plusTxBytes);
                }
            } else {
                values.put("wifi_RxBytes",lastFlow.getWifi_RxBytes() + plusRxBytes);
                values.put("wifi_TxBytes",lastFlow.getWifi_TxBytes() + plusTxBytes);
            }
        }
        values.put("time_date",timeDate);
        values.put("time", System.currentTimeMillis());
        if (flows == null || flows.size() == 0){
            DBHelper.getInstance().insert(SQLiteHelper.FLOW_COMPUTE,values);
        } else {
            synchronized (lock){
                DBHelper.getInstance().update(SQLiteHelper.FLOW_COMPUTE,values," time_date = ? ",new String[]{String.valueOf(timeDate)});
            }
        }
        return lastFlow;
    }

    public void updateFlowMaxSize(boolean clickStatus){
        String sql = "select * from " + SQLiteHelper.FLOW_COMPUTE + " where time_date = ? order by id desc";
        long timeDate = getDayTime();
        List<Flow> flows = DBHelper.getInstance().query(sql, new String[]{timeDate + ""}, new DBHelper.QueryListener<Flow>() {
            @Override
            public List<Flow> queryOk(Cursor c) {
                List<Flow> flowList = new ArrayList<>();
                if (c != null){
                    Flow flow = null;
                    while (c.moveToNext()){
                        flow = new Flow();
                        flow.setUidRxBytes(c.getLong(c.getColumnIndex("UidRxBytes")));
                        flow.setUidTxBytes(c.getLong(c.getColumnIndex("UidTxBytes")));
                        flow.setWifi_RxBytes(c.getLong(c.getColumnIndex("wifi_RxBytes")));
                        flow.setWifi_TxBytes(c.getLong(c.getColumnIndex("wifi_TxBytes")));
                        flow.setMobile_RxBytes(c.getLong(c.getColumnIndex("mobile_RxBytes")));
                        flow.setMobile_TxBytes(c.getLong(c.getColumnIndex("mobile_TxBytes")));
                        flow.setMax_Bytes(c.getLong(c.getColumnIndex("max_Bytes")));
                        flow.setTips_size(c.getInt(c.getColumnIndex("tips_size")));
                        flow.setTime_date(c.getLong(c.getColumnIndex("time_date")));
                        flow.setTime(c.getLong(c.getColumnIndex("time")));
                        flowList.add(flow);
                    }
                }
                return flowList;
            }

            @Override
            public void error() {
            }
        });
        if (flows == null || flows.size() == 0)return;
        Flow flow = flows.get(0);
        ContentValues values = new ContentValues();
        values.put("max_Bytes",flow.getMax_Bytes() + TrafficLimitValue.getInstance().getMax());
        int tips_size = 0;
        if (clickStatus){
            tips_size = 0;
        } else {
            tips_size = flow.getTips_size() + 1;
        }
        values.put("tips_size",tips_size);
        synchronized (lock){
            DBHelper.getInstance().update(SQLiteHelper.FLOW_COMPUTE,values," time_date = ? ",new String[]{String.valueOf(timeDate)});
        }
    }

    public void updateFlowMaxSize(){
        final long max_value = TrafficLimitValue.getInstance().getMax();
        String sql = "select * from " + SQLiteHelper.FLOW_COMPUTE + " where time_date = ? order by id desc";
        long timeDate = getDayTime();
        List<Flow> flows = DBHelper.getInstance().query(sql, new String[]{timeDate + ""}, new DBHelper.QueryListener<Flow>() {
            @Override
            public List<Flow> queryOk(Cursor c) {
                List<Flow> flowList = new ArrayList<>();
                if (c != null){
                    Flow flow = null;
                    while (c.moveToNext()){
                        flow = new Flow();
                        flow.setUidRxBytes(c.getLong(c.getColumnIndex("UidRxBytes")));
                        flow.setUidTxBytes(c.getLong(c.getColumnIndex("UidTxBytes")));
                        flow.setWifi_RxBytes(c.getLong(c.getColumnIndex("wifi_RxBytes")));
                        flow.setWifi_TxBytes(c.getLong(c.getColumnIndex("wifi_TxBytes")));
                        flow.setMobile_RxBytes(c.getLong(c.getColumnIndex("mobile_RxBytes")));
                        flow.setMobile_TxBytes(c.getLong(c.getColumnIndex("mobile_TxBytes")));
                        flow.setMax_Bytes(c.getLong(c.getColumnIndex("max_Bytes")));
                        flow.setTips_size(c.getInt(c.getColumnIndex("tips_size")));
                        flow.setTime_date(c.getLong(c.getColumnIndex("time_date")));
                        flow.setTime(c.getLong(c.getColumnIndex("time")));
                        flowList.add(flow);
                    }
                }
                return flowList;
            }

            @Override
            public void error() {
            }
        });
        if (flows == null || flows.size() == 0)return;
        Flow flow = flows.get(0);
        long mobileFlow = flow.getMobile_RxBytes() + flow.getMobile_TxBytes();
        long max = flow.getMax_Bytes();
        long max_end = 0L;
        if (max == max_value)return;
        if (max < max_value){
            max_end = max_value;
        }  else {
            max_end = mobileFlow + max_value;
        }
        ContentValues values = new ContentValues();
        values.put("max_Bytes",max_end);
        synchronized (lock){
            DBHelper.getInstance().update(SQLiteHelper.FLOW_COMPUTE,values," time_date = ? ",new String[]{String.valueOf(timeDate)});
        }
    }

    public void deleteOldFlowData(){
        final long timeDate = getDayTime();
        DBHelper.getInstance().delete(SQLiteHelper.FLOW_COMPUTE," time_date != ? " ,new String[]{String.valueOf(timeDate)});
    }


    /**
     * 插入人脸特征信息
     * @param attrInfo
     */
    public void insertFaceAttr(FaceAttrInfo attrInfo){
        if (attrInfo == null) return;
        ContentValues values = new ContentValues();
        values.put("type",attrInfo.getType());
        values.put("capturePicName",attrInfo.getCapturePicName());
        values.put("compareLibPicName",attrInfo.getCompareLibPicName());
        values.put("compareCapPicName",attrInfo.getCompareCapPicName());
        values.put("name",attrInfo.getName());
        values.put("age",attrInfo.getAge());
        values.put("sex",attrInfo.getSex());
        values.put("eyeocc",attrInfo.getEyeocc());
        values.put("mouththocc",attrInfo.getMouththocc());
        values.put("headwear",attrInfo.getHeadwear());
        values.put("noseocc",attrInfo.getNoseocc());
        values.put("beard",attrInfo.getBeard());
        values.put("similarity",attrInfo.getSimilarity());
        DBHelper.getInstance().insert(SQLiteHelper.AI_FACE_ATTR_TABLE_NAME,values);
    }

    /**
     * 删除单个人脸属性数据
     * @param attrInfo
     */
    public void deleteFaceAttrInfo(FaceAttrInfo attrInfo){
        if (attrInfo == null) return;
        String where = " capturePicName = ? and compareLibPicName = ? and compareCapPicName = ?";
        DBHelper.getInstance().delete(SQLiteHelper.AI_FACE_ATTR_TABLE_NAME,where,
                new String[]{attrInfo.getCapturePicName(), attrInfo.getCompareLibPicName(), attrInfo.getCompareCapPicName()});
    }

    /**
     * 查询所有人脸属性表
     * @return
     */
    public List<FaceAttrInfo> queryFaceAttrInfo(String type){
        String sql = "select * from " + SQLiteHelper.AI_FACE_ATTR_TABLE_NAME + " where type = ? order by id desc";
        List<FaceAttrInfo> faceAttrInfos = DBHelper.getInstance().query(sql, new String[]{type}, new DBHelper.QueryListener<FaceAttrInfo>() {
            @Override
            public List<FaceAttrInfo> queryOk(Cursor c) {
                return getFaceAttrInfo(c);
            }

            @Override
            public void error() {
            }
        });
        return faceAttrInfos;
    }

    /**
     * 查询 id 最小的人脸属性
     * @return
     */
    public List<FaceAttrInfo> queryMinIdFaceInfo(){
        String sql = "select * from " + SQLiteHelper.AI_FACE_ATTR_TABLE_NAME + " order by id asc limit 1";
        List<FaceAttrInfo> faceAttrInfos = DBHelper.getInstance().query(sql, new String[]{}, new DBHelper.QueryListener<FaceAttrInfo>() {
            @Override
            public List<FaceAttrInfo> queryOk(Cursor c) {
                return getFaceAttrInfo(c);
            }

            @Override
            public void error() {
            }
        });
        return faceAttrInfos;
    }

    private List<FaceAttrInfo> getFaceAttrInfo(Cursor c){
        if (c == null)return null;
        List<FaceAttrInfo> attrInfos = new ArrayList<>();
        FaceAttrInfo attrInfo = null;
        while (c.moveToNext()){
            attrInfo = new FaceAttrInfo();
            attrInfo.setType(c.getString(c.getColumnIndex("type")));
            attrInfo.setCapturePicName(c.getString(c.getColumnIndex("capturePicName")));
            attrInfo.setCompareLibPicName(c.getString(c.getColumnIndex("compareLibPicName")));
            attrInfo.setCompareCapPicName(c.getString(c.getColumnIndex("compareCapPicName")));
            attrInfo.setName(c.getString(c.getColumnIndex("name")));
            attrInfo.setAge(c.getInt(c.getColumnIndex("age")));
            attrInfo.setSex(c.getInt(c.getColumnIndex("sex")));
            attrInfo.setEyeocc(c.getInt(c.getColumnIndex("eyeocc")));
            attrInfo.setMouththocc(c.getInt(c.getColumnIndex("mouththocc")));
            attrInfo.setHeadwear(c.getInt(c.getColumnIndex("headwear")));
            attrInfo.setNoseocc(c.getInt(c.getColumnIndex("noseocc")));
            attrInfo.setBeard(c.getInt(c.getColumnIndex("beard")));
            attrInfo.setSimilarity(c.getInt(c.getColumnIndex("similarity")));
            attrInfos.add(attrInfo);
        }
        return attrInfos;
    }

    /**
     * 通过uid查询文件夹中的数据
     * 接收到的数据量
     * @param localUid
     * @return
     */
    private long getRxBytesManual(int localUid) {
        File dir = new File("/proc/uid_stat/");
        String[] children = dir.list();
        StringBuffer stringBuffer = new StringBuffer();
        if (children == null)return 0L;
        for (int i = 0; i < children.length; i++) {
            stringBuffer.append(children[i]);
            stringBuffer.append("   ");
        }
        if (!Arrays.asList(children).contains(String.valueOf(localUid))) {
            return 0L;
        }
        File uidFileDir = new File("/proc/uid_stat/" + localUid);
        File uidActualFileReceived = new File(uidFileDir, "tcp_rcv");
        String textReceived = "0";
        try {
            BufferedReader brReceived = new BufferedReader(new FileReader(uidActualFileReceived));
            String receivedLine;
            if ((receivedLine = brReceived.readLine()) != null) {
                textReceived = receivedLine;
            }
            return Long.valueOf(textReceived).longValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 通过uid查询文件夹中的数据
     * 发送的数据量
     * @param localUid
     * @return
     */
    private long getTxBytesManual(int localUid) {
        File dir = new File("/proc/uid_stat/");
        String[] children = dir.list();
        if (children == null)return 0L;
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < children.length; i++) {
            stringBuffer.append(children[i]);
            stringBuffer.append("   ");
        }
        if (!Arrays.asList(children).contains(String.valueOf(localUid))) {
            return 0L;
        }
        File uidFileDir = new File("/proc/uid_stat/" + localUid);
        File uidActualFileSent = new File(uidFileDir, "tcp_snd");
        String textSent = "0";
        try {
            BufferedReader brSent = new BufferedReader(new FileReader(uidActualFileSent));
            String sentLine;
            if ((sentLine = brSent.readLine()) != null) {
                textSent = sentLine;
            }
            return Long.valueOf(textSent).longValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 当天零点的时间戳
     * @return
     */
    private long getDayTime(){
        long time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        return calendar.getTimeInMillis() / 1000;
    }

}
