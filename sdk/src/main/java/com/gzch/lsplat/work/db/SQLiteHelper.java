package com.gzch.lsplat.work.db;

import android.content.Context;

import com.longse.lsapc.lsacore.sapi.db.DBHelper;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;


/**
 * Created by lw on 2017/12/7.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    //生成的数据库文件名
    private static final String DB_NAME = "bitdog_sql.db";

    //当前数据库版本号，每次修改或升级需要更新该值
    private static final int VERSION = 7;

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        //不可忽略的 进行so库加载
        SQLiteDatabase.loadLibs(context);
        EncryptionSql.getInstences().encrypt(context, DB_NAME, "123456");
    }

    /**
     * 设备列表
     */
    public static final String DEVICE_INFO_TABLE_NAME = "device_info";

    private static final String DEVICE_INFO = "create table if not exists " + DEVICE_INFO_TABLE_NAME +
            "(id integer primary key autoincrement not null," +
            "userid text not null," + //userid指定唯一用户
            "equpId text not null," +
            "ifOnLine text ," +
            "ifBind text ," +
            "equoModle text ," +
            "version text ," +
            "checkStr text ," +
            "deviceName text ," +
            "deviceType text ," +
            "sysVersion text ," +
            "deviceDetatilType text ," +
            "cateId text ," +
            "useCloudStorage text ," +
            "canUseCloudStorage text ," +
            "cateName text ," +
            "deviceConnectServer text ," +
            "localPwd text ," +
            "localUser text ," +
//            "channelList text ," +
            "order_num text ," +
            "localeDeviceIp text ," +
            "localeDevicePort integer ," +
            "isDirect text ," +   //0 : false,  1 : true
            "PrivateServer text ," +
            "mode integer ," +
            "deviceStreams integer ," +
            "port integer ," +
            "remotePlay text ," +   //0 : false,  1 : true
            "h264_plus text ," +
            "h265_plus text ," +
            "can_use_cloud_storage text ," +
            "use_cloud_storage text ," +
            "replay_data_rate integer ," + //回放的码流选择 0:子码流  1:主码流
            "replay_video_type integer ," + //设备回放的录像 1:移动侦测录像 2:普通录像 3:定时录像 4:全部录像
            "channel_name text ," + //播放时显示
            "device_spare text , " +
            "share_permision text , " +
            "isSNLocaleDevice integer )"; // 1 : 本地保存的序列号设备

    /**
     * 设备通道
     */
    public static final String DEVICE_INFO_CHANNEL_TABLE_NAME = "device_info_channel";
    private static final String DEVICE_INFO_CHANNEL = "create table if not exists " + DEVICE_INFO_CHANNEL_TABLE_NAME +
            "(id integer primary key autoincrement not null," +
            "device_id text not null," +
            "userid text ," +
            "dc_id text ," +
            "channel_name text ," +
            "channel integer ," +
            "order_num integer ," +
            "alarm_open integer ," +//0 : false,  1 : true
            "replay_data_rate integer ," + //回放的码流选择 0:子码流  1:主码流
            "replay_video_type integer ," + //设备回放的录像 1:移动侦测录像 2:普通录像 3:定时录像 4:全部录像
            "channel_spare text)";

    /**
     * 设备分组信息
     */
    public static final String GROUP_TABLE_NAME = "group_device";
    private static final String GROUP_TABLE = "create table if not exists " + GROUP_TABLE_NAME +
            "(id integer primary key autoincrement not null," +
            "userid text not null," + //userid指定唯一用户
            "cate_id text ," +
            "cate_name text ," +
            "order_num text ," +
            "group_spare text)";


    /**
     * 登录历史记录
     */
    public static final String LOGIN_RECORD_TABLE_NAME = "login_history";

    /**
     * 登录用户历史记录
     */
    private static final String LOGIN_RECORD = "create table if not exists " + LOGIN_RECORD_TABLE_NAME +
            "(id integer primary key autoincrement not null," +
            "account text ," +
            "password text ," +
            "token text ," +//第三方登录使用
            "userid text ," +//第三方登录使用去重，账号密码登录可为""
            "time text ," +//登录时间戳
            "login_type text ," + //账号密码登录或微信，Facebook等第三方登录
            /**
             * Twitter 需要两个参数 token 为 accessToken ；token2 为 tokenSecret
             * 其他登录不需要token2
             */
            "token2 text " +//第三方登录使用
            ")";

    /**
     * 序列号登录历史记录表
     */
    public static final String SERIAL_LOGIN_HISTORY_TABLE_NAME = "serial_login_history";

    /**
     * 序列号登录历史记录
     */
    private static final String SERIAL_LOGIN_RECORD = "create table if not exists " + SERIAL_LOGIN_HISTORY_TABLE_NAME +
            "(id integer primary key autoincrement not null," +
            "account text ," +
            "password text ," +
            "device_id text)";

    /**
     * 演示列表
     */
    public static final String DEMO_LIST_BACKUP_TABLE_NAME = "demo_list_backup";

    private static final String DEMO_LIST_BACKUP = "create table if not exists " + DEMO_LIST_BACKUP_TABLE_NAME +
            "(id integer primary key autoincrement not null," +
            "demo_id text ," +
            "device_name text ," +
            "num text ," +
            "deviceType text ," +
            "is_vr integer)"; // is_vr 1 true or false

    /**
     * 用户信息表名
     * 第三方登录暂不需要缓存用户信息
     */
    public static final String USER_INFO_TABLE_NAME = "user_info_table";

    private static final String USER_INFO_TABLE = "create table if not exists " + USER_INFO_TABLE_NAME +
            "(id integer primary key autoincrement not null," +
            "account text ," +
            "phoneNumber text ," +
            "email text ," +
            "ifBind text ," +
            "realName text ," +
            "subName text ," +
            "userIconPath text ," +
            "userId text)";

    /**
     * 设备截图和录像
     */
    public static final String MEDIA_DEVICE_TABLE_NAME = "media_device_table";

    private static final String MEDIA_DEVICE_TABLE = "create table if not exists " + MEDIA_DEVICE_TABLE_NAME +
            "(id integer primary key autoincrement not null," +
            "path text ," +
            "name text ," +
            "fname text ," +
            "time text ," +
            "device_id text ," +
            "device_channel text ," +
            "playMode integer ," + //类型 实时预览 ， 回放  ，VR 等,对应 EqupInfo#playMode
            "type integer ," +  // 1 : img , 2 video
            "userId text)";

    /**
     * 播放缓存
     */
    public static final String PLAY_CACHE_TABLE_NAME = "device_play_cache";

    private static final String PLAY_CACHE = "create table if not exists " + PLAY_CACHE_TABLE_NAME +
            "(id integer primary key autoincrement not null," +
            "userid text not null," + //userid指定唯一用户
            "equpId text not null," +
            "playMode text ," +
            "ifOnLine text ," +
            "ifBind text ," +
            "equoModle text ," +
            "version text ," +
            "checkStr text ," +
            "deviceName text ," +
            "deviceType text ," +
            "sysVersion text ," +
            "deviceDetatilType text ," +
            "cateId text ," +
            "useCloudStorage text ," +
            "canUseCloudStorage text ," +
            "cateName text ," +
            "deviceConnectServer text ," +
            "localPwd text ," +
            "localUser text ," +
            "order_num text ," +
            "localeDeviceIp text ," +
            "localeDevicePort integer ," +
            "isDirect text ," +   //0 : false,  1 : true
            "PrivateServer text ," +
            "mode integer ," +
            "deviceStreams integer ," +
            "port integer ," +
            "remotePlay text ," +   //0 : false,  1 : true
            "h264_plus text ," +
            "h265_plus text ," +
            "can_use_cloud_storage text ," +
            "use_cloud_storage text ," +
            "replay_data_rate integer ," + //回放的码流选择 0:子码流  1:主码流
            "replay_video_type integer ," + //设备回放的录像 1:移动侦测录像 2:普通录像 3:定时录像 4:全部录像
            "channel_name text ," + //播放时显示
            "device_spare text ," +
            "share_permision text , " +
            "isSNLocaleDevice integer )"; // 1 : 本地保存的序列号设备

    /**
     * 局域网设备
     */
    public static final String LOCALE_DEVICE_TABLE_NAME = "locale_device_table";

    private static final String LOCALE_DEVICE_TABLE = "create table if not exists " + LOCALE_DEVICE_TABLE_NAME +
            "(id integer primary key autoincrement not null," +
            "device_id text ," +
            "account text ," +
            "password text)";

    public static void reCreatePlayCache(){
        try {
            String deleteSql = " DROP TABLE " + PLAY_CACHE_TABLE_NAME;
            DBHelper.getInstance().execSQL(deleteSql);
            DBHelper.getInstance().execSQL(PLAY_CACHE);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void reCreatePlayCacheTable(){
        try {
            DBHelper.getInstance().execSQL(PLAY_CACHE);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * SN播放缓存
     */
    public static final String SN_LOGIN_CACHE_TABLE_NAME = "snlogin_device_play_cache";

    private static final String SN_LOGIN_CACHE = "create table if not exists " + SN_LOGIN_CACHE_TABLE_NAME +
            "(id integer primary key autoincrement not null," +
            "userid text not null," + //userid指定唯一用户
            "equpId text not null," +
            "playMode text ," +
            "ifOnLine text ," +
            "ifBind text ," +
            "equoModle text ," +
            "version text ," +
            "checkStr text ," +
            "deviceName text ," +
            "deviceType text ," +
            "sysVersion text ," +
            "deviceDetatilType text ," +
            "cateId text ," +
            "useCloudStorage text ," +
            "canUseCloudStorage text ," +
            "cateName text ," +
            "deviceConnectServer text ," +
            "localPwd text ," +
            "localUser text ," +
            "order_num text ," +
            "localeDeviceIp text ," +
            "localeDevicePort integer ," +
            "isDirect text ," +   //0 : false,  1 : true
            "PrivateServer text ," +
            "mode integer ," +
            "deviceStreams integer ," +
            "port integer ," +
            "remotePlay text ," +   //0 : false,  1 : true
            "h264_plus text ," +
            "h265_plus text ," +
            "can_use_cloud_storage text ," +
            "use_cloud_storage text ," +
            "replay_data_rate integer ," + //回放的码流选择 0:子码流  1:主码流
            "replay_video_type integer ," + //设备回放的录像 1:移动侦测录像 2:普通录像 3:定时录像 4:全部录像
            "channel_name text ," + //播放时显示
            "device_spare text ," +
            "share_permision text , " +
            "isSNLocaleDevice integer )"; // 1 : 本地保存的序列号设备

    /**
     * 收藏设备
     */
    public static final String FAVORITES_CACHE_TABLE_NAME = "favorites_device_play_cache";

    private static final String FAVORITES_CACHE = "create table if not exists " + FAVORITES_CACHE_TABLE_NAME +
            "(id integer primary key autoincrement not null," +
            "userid text not null," + //userid指定唯一用户
            "equpId text not null," +
            "playMode text ," +
            "ifOnLine text ," +
            "ifBind text ," +
            "equoModle text ," +
            "version text ," +
            "checkStr text ," +
            "deviceName text ," +
            "deviceType text ," +
            "sysVersion text ," +
            "deviceDetatilType text ," +
            "cateId text ," +
            "useCloudStorage text ," +
            "canUseCloudStorage text ," +
            "cateName text ," +
            "deviceConnectServer text ," +
            "localPwd text ," +
            "localUser text ," +
            "order_num text ," +
            "localeDeviceIp text ," +
            "localeDevicePort integer ," +
            "isDirect text ," +   //0 : false,  1 : true
            "PrivateServer text ," +
            "mode integer ," +
            "deviceStreams integer ," +
            "port integer ," +
            "remotePlay text ," +   //0 : false,  1 : true
            "h264_plus text ," +
            "h265_plus text ," +
            "can_use_cloud_storage text ," +
            "use_cloud_storage text ," +
            "replay_data_rate integer ," + //回放的码流选择 0:子码流  1:主码流
            "replay_video_type integer ," + //设备回放的录像 1:移动侦测录像 2:普通录像 3:定时录像 4:全部录像
            "channel_name text ," + //播放时显示
            "device_spare text ," +
            "share_permision text , " +
            "isSNLocaleDevice integer )"; // 1 : 本地保存的序列号设备

    /**
     * 收藏的设备通道
     */
    public static final String FAVORITES_DEVICE_CHANNEL_TABLE_NAME = "favorites_device_info_channel";
    private static final String FAVORITES_DEVICE_CHANNEL = "create table if not exists " + FAVORITES_DEVICE_CHANNEL_TABLE_NAME +
            "(id integer primary key autoincrement not null," +
            "device_id text not null," +
            "userid text ," +
            "dc_id text ," +
            "channel_name text ," +
            "channel integer ," +
            "order_num integer ," +
            "alarm_open integer ," +//0 : false,  1 : true
            "replay_data_rate integer ," + //回放的码流选择 0:子码流  1:主码流
            "replay_video_type integer ," + //设备回放的录像 1:移动侦测录像 2:普通录像 3:定时录像 4:全部录像
            "channel_spare text)";

    /**
     * 流量统计
     */
    public static final String FLOW_COMPUTE = "flow_compute";

    private static final String FLOW_COMPUTE_TABLE = "create table if not exists " + FLOW_COMPUTE +
            "(id integer primary key autoincrement not null," +
            "UidRxBytes integer ," +    //当前软件接收的字节数
            "UidTxBytes integer ," +    //当前软件发送的字节数
            "wifi_RxBytes integer ," +     //WiFi接收的字节数
            "wifi_TxBytes integer ," +     //WiFi发送的字节数
            "mobile_RxBytes integer ," +   //手机流量接收的字节数
            "mobile_TxBytes integer ," +   //手机流量发送的字节数
            "max_Bytes integer ," +   //需要提示的临界值
            "tips_size integer ," +   //提示次数，3次之后不提示（每次点击取消后+1，点击确认后清零）
            "time_date integer ," +         //当前记录的时间(当天零点的时间戳)
            "time integer)";                 //最后一次记录的时间戳

    /**
     * ai 人脸信息
     */
    public static final String AI_FACE_ATTR_TABLE_NAME = "ai_face_attr_table";

    private static final String AI_FACE_ATTR_TABLE_TABLE = "create table if not exists " + AI_FACE_ATTR_TABLE_NAME +
            "(id integer primary key autoincrement not null," +
            "type text," +
            "capturePicName text ," +
            "compareLibPicName text ," +
            "compareCapPicName text ," +
            "name text ," +
            "age integer ," +
            "sex integer ," +
            "eyeocc integer ," +
            "mouththocc integer ," +
            "headwear integer ," +
            "noseocc integer ," +
            "beard integer ," +
            "similarity integer)";


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if (sqLiteDatabase != null){
            sqLiteDatabase.execSQL(LOGIN_RECORD);
            sqLiteDatabase.execSQL(DEVICE_INFO);
            sqLiteDatabase.execSQL(DEVICE_INFO_CHANNEL);
            sqLiteDatabase.execSQL(GROUP_TABLE);
            sqLiteDatabase.execSQL(SERIAL_LOGIN_RECORD);
            sqLiteDatabase.execSQL(DEMO_LIST_BACKUP);
            sqLiteDatabase.execSQL(USER_INFO_TABLE);
            sqLiteDatabase.execSQL(MEDIA_DEVICE_TABLE);
            sqLiteDatabase.execSQL(PLAY_CACHE);
            sqLiteDatabase.execSQL(LOCALE_DEVICE_TABLE);
            sqLiteDatabase.execSQL(SN_LOGIN_CACHE);
            sqLiteDatabase.execSQL(FAVORITES_CACHE);
            sqLiteDatabase.execSQL(FLOW_COMPUTE_TABLE);
            sqLiteDatabase.execSQL(FAVORITES_DEVICE_CHANNEL);
            sqLiteDatabase.execSQL(AI_FACE_ATTR_TABLE_TABLE);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (sqLiteDatabase == null)return;
        switch (oldVersion){
            case 1:
            case 2:
                sqLiteDatabase.execSQL(FLOW_COMPUTE_TABLE);
            case 3:
                String add_token2sql = "ALTER TABLE " + LOGIN_RECORD_TABLE_NAME + " ADD COLUMN token2 text";
                sqLiteDatabase.execSQL(add_token2sql);
            case 4:
                String addSNLocale = "ALTER TABLE " + DEVICE_INFO_TABLE_NAME + " ADD COLUMN isSNLocaleDevice integer";
                String addSNLocale2 = "ALTER TABLE " + PLAY_CACHE_TABLE_NAME + " ADD COLUMN isSNLocaleDevice integer";
                String addSNLocale3 = "ALTER TABLE " + SN_LOGIN_CACHE_TABLE_NAME + " ADD COLUMN isSNLocaleDevice integer";
                String addSNLocale4 = "ALTER TABLE " + FAVORITES_CACHE_TABLE_NAME + " ADD COLUMN isSNLocaleDevice integer";
                sqLiteDatabase.execSQL(addSNLocale);
                sqLiteDatabase.execSQL(addSNLocale2);
                sqLiteDatabase.execSQL(addSNLocale3);
                sqLiteDatabase.execSQL(addSNLocale4);
            case 5:
                String addSNLocale5 = "ALTER TABLE " + DEVICE_INFO_TABLE_NAME + " ADD COLUMN share_permision text";
                String addSNLocale52 = "ALTER TABLE " + PLAY_CACHE_TABLE_NAME + " ADD COLUMN share_permision text";
                String addSNLocale53 = "ALTER TABLE " + SN_LOGIN_CACHE_TABLE_NAME + " ADD COLUMN share_permision text";
                String addSNLocale54 = "ALTER TABLE " + FAVORITES_CACHE_TABLE_NAME + " ADD COLUMN share_permision text";
                sqLiteDatabase.execSQL(addSNLocale5);
                sqLiteDatabase.execSQL(addSNLocale52);
                sqLiteDatabase.execSQL(addSNLocale53);
                sqLiteDatabase.execSQL(addSNLocale54);
            case 6:
                sqLiteDatabase.execSQL(AI_FACE_ATTR_TABLE_TABLE);
        }
    }
}
