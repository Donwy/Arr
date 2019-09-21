package com.gzch.lsplat.work.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.TextUtils;

import com.gzch.lsplat.work.WorkContext;
import com.gzch.lsplat.work.action.DBAction;
import com.gzch.lsplat.work.cmd.BitdogCmd;
import com.gzch.lsplat.work.mode.DirectInfo;
import com.gzch.lsplat.work.mode.ImageInfo;
import com.gzch.lsplat.work.mode.LoginRecord;
import com.longse.lsapc.lsacore.BitdogInterface;
import com.longse.lsapc.lsacore.interf.ThreadWrapper;
import com.longse.lsapc.lsacore.sapi.file.FileHelper;
import com.longse.lsapc.lsacore.sapi.log.KLog;
import com.longse.lsapc.lsacore.sapi.sharedPreferences.StringCache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lw on 2018/6/28.
 * 启动后马上执行数据迁移，迁移完成才进入主界面
 */
public class FreeipOldDataMigration {

    private static FreeipOldDataMigration instance;
    private FreeipOldDataMigration(){
        dbHelper = new FreeIpSQLiteDBHelper(BitdogInterface.getInstance().getApplicationContext());
    }

    public static FreeipOldDataMigration getInstance(){
        synchronized (FreeipOldDataMigration.class){
            if (instance == null){
                instance = new FreeipOldDataMigration();
            }
            return instance;
        }
    }

    private FreeIpSQLiteDBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private static int DBBaseSum = 0;

    private synchronized SQLiteDatabase openDB(){
        if (dbHelper == null){
            return null;
        }
        if(DBBaseSum == 0){
            sqLiteDatabase = dbHelper.getReadableDatabase();
        }
        DBBaseSum++;
        return sqLiteDatabase;
    }

    private synchronized void closeDB(){
        if(DBBaseSum == 1 && null != sqLiteDatabase){
            sqLiteDatabase.close();
        }
        DBBaseSum--;
    }

    private synchronized void insert(String table, ContentValues values){
        if (TextUtils.isEmpty(table) || values == null || values.size() == 0){
            return;
        }
        openDB();
        if (sqLiteDatabase != null){
            sqLiteDatabase.insert(table,null,values);
        }
        closeDB();
    }

    private synchronized void delete(String table, String whereClause, String[] whereArgs){
        if (TextUtils.isEmpty(table) || TextUtils.isEmpty(whereClause) || whereArgs == null || whereArgs.length == 0){
            return;
        }
        openDB();
        if (sqLiteDatabase != null){
            sqLiteDatabase.delete(table,whereClause,whereArgs);
        }
        closeDB();
    }

    /**
     * 把老FreeIP中用户生成的数据迁移到新数据库
     * 迁移一次，删除一次，保证不重复不遗漏
     */
    public void start(final Runnable runnable){
        if (WorkContext.CONTEXT_APP == WorkContext.BITDOG_APP){
            if (runnable != null){
                runnable.run();
            }
            return;
        }
        BitdogInterface.getInstance().post(new Runnable() {
            @Override
            public void run() {
                String day = StringCache.getInstance().queryCache("itemchoose","0");
                if (!"0".equals(day)){
                    StringCache.getInstance().addCache("itemPosition", day);
                    StringCache.getInstance().addCache("itemchoose", "0");
                }
                loginAccount();
                direct();
                media();
                if (runnable != null){
                    runnable.run();
                }
            }
        }, ThreadWrapper.THREAD_IO);
    }

//    private static final String SAVEREFRESHTOKEN = "wechat_token";//wechat_token
//    private static final String SAVEFBTOKEN = "facebook_token";//fb_token
//    private static final String SAVETWITTERTOKEN = "twitter_token";//twitter_token
//    private static final String SAVETWITTERSECRET = "twitter_token_secret";//twitter_token_secret
//    private static final String SAVEGOOGLETOKEN = "google_token";//google_token
    private static final String SAVEJPUSHSWITCH = "jpushswitch";          //是否开启消息推送提醒
    private static final String JPUSH_STATUS_KEY = "JPush_status_key";

    private static final String OPEN = "open";
    private static final String CLOSE = "close";
    private void loginAccount(){
//        final String queryAccountSql = " select * from user_Account order by id ASC ";
//        openDB();
//        if (sqLiteDatabase != null){
//            Cursor c = sqLiteDatabase.rawQuery(queryAccountSql,null);
//            if (c != null){
//                List<LoginRecord> loginRecordList = new ArrayList<>();
//                LoginRecord loginRecord = null;
//                while (c.moveToNext()){
//                    loginRecord = new LoginRecord();
//                    String account = c.getString(c.getColumnIndex("account"));
//                    String password = c.getString(c.getColumnIndex("password"));
//                    loginRecord.setAccount(account);
//                    loginRecord.setPassword(password);
//                    loginRecord.setLoginType(LoginRecord.ACCOUNT);
//                    loginRecordList.add(loginRecord);
//                }
//                if (!c.isClosed()){
//                    c.close();
//                }
//                if (loginRecordList != null && loginRecordList.size() > 0){
//                    for (LoginRecord record : loginRecordList){
//                        delete("user_Account"," account = ? ",new String[]{record.getAccount()});
//                        DBAction.getInstance().insertLoginRecord(record);
//                    }
//                }
//                closeDB();
//                return;
//            }
//        }
//        closeDB();

        String userName = StringCache.getInstance().setSharedPreferencesName("smarthomestringcache", Context.MODE_PRIVATE).queryCache("user_name", "");
        String password = StringCache.getInstance().setSharedPreferencesName("smarthomestringcache", Context.MODE_PRIVATE).queryCache("password", "");
        //第三方登录重新验证
//        String wechatToken = StringCache.getInstance().setSharedPreferencesName("smarthomestringcache",Context.MODE_PRIVATE).queryCache(SAVEREFRESHTOKEN, "");
//        String facebookToken = StringCache.getInstance().setSharedPreferencesName("smarthomestringcache",Context.MODE_PRIVATE).queryCache(SAVEFBTOKEN, "");
//        String twitterToken = StringCache.getInstance().setSharedPreferencesName("smarthomestringcache",Context.MODE_PRIVATE).queryCache(SAVETWITTERTOKEN, "");
//        String twitterTokenSecret = StringCache.getInstance().setSharedPreferencesName("smarthomestringcache",Context.MODE_PRIVATE).queryCache(SAVETWITTERSECRET, "");
//        String googleToken = StringCache.getInstance().setSharedPreferencesName("smarthomestringcache",Context.MODE_PRIVATE).queryCache(SAVEGOOGLETOKEN, "");
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)){
            LoginRecord loginRecord = new LoginRecord();
            loginRecord.setLoginType(LoginRecord.ACCOUNT);
            loginRecord.setAccount(userName);
            loginRecord.setPassword(password);
            DBAction.getInstance().insertLoginRecord(loginRecord);
        }
        if ("1".equals(StringCache.getInstance().setSharedPreferencesName("smarthomestringcache", Context.MODE_PRIVATE).queryCache(SAVEJPUSHSWITCH, "1"))){
            StringCache.getInstance().addCache(JPUSH_STATUS_KEY,OPEN);
        } else {
            StringCache.getInstance().addCache(JPUSH_STATUS_KEY,CLOSE);
        }
    }

    private void direct(){
        final String querySql = " select * from device ";
        openDB();
        if (sqLiteDatabase != null){
            Cursor c = sqLiteDatabase.rawQuery(querySql,null);
            if (c != null){
                List<DirectInfo> directInfoList = new ArrayList<>();
                DirectInfo directInfo = null;
                while (c.moveToNext()){
                    directInfo = new DirectInfo();
                    int index = c.getColumnIndex("deviceId");
                    if (index > 0){
                        directInfo.setDeviceId(c.getString(index));
                    }
                    directInfo.setChannelNumber(c.getInt(c.getColumnIndex("deviceChannels")));
                    directInfo.setUser(c.getString(c.getColumnIndex("userName")));
                    directInfo.setPassword(c.getString(c.getColumnIndex("userPwd")));
                    directInfo.setDeviceName(c.getString(c.getColumnIndex("deviceName")));
                    directInfo.setIp(c.getString(c.getColumnIndex("deviceIp")));
                    directInfo.setPort(c.getInt(c.getColumnIndex("devicePort")) + "");
                    directInfo.setDeviceType(c.getString(c.getColumnIndex("deviceType")));
                    directInfoList.add(directInfo);
                }
                if (!c.isClosed()){
                    c.close();
                }
                if (directInfoList != null && directInfoList.size() > 0){
                    for (DirectInfo record : directInfoList){
                        delete("device"," deviceIp = ? and devicePort = ? ",new String[]{record.getIp(),record.getPort()});
                        BitdogInterface.getInstance().exec(BitdogCmd.INSERT_DIRECT_DEVICE_CMD,
                                String.format("{\"deviceId\":\"%s\",\"deviceType\":\"%s\",\"deviceName\":\"%s\",\"ip\":\"%s\",\"port\":\"%s\"," +
                                                "\"user\":\"%s\",\"password\":\"%s\",\"channelNumber\":\"%s\"}",
                                        record.getDeviceId(),record.getDeviceType(), record.getDeviceName(), record.getIp(), record.getPort(), record.getUser(), record.getPassword(), record.getChannelNumber())
                                , BitdogInterface.ASYNC_HANDLE);
                    }
                }
                closeDB();
                return;
            }
        }
        closeDB();
    }

    private static final String FILEPATH = "/p2p/image/";  //存储截图
    private static final String VIDEOPATH = "/p2p/video/";//录像文件转码后存储
    private static final String VIDEOSTREAM = "/p2p/h264/";//录像原始码流存储
    private void media(){
        final String querySql = " select * from image ";
        openDB();
        if (sqLiteDatabase != null){
            Cursor c = sqLiteDatabase.rawQuery(querySql,null);
            if (c != null){
                final List<ImageInfo> imageInfoList = new ArrayList<>();
                ImageInfo imageInfo = null;
                while (c.moveToNext()){
                    imageInfo = new ImageInfo();
                    imageInfo.setChannel("-1");
                    String tag = c.getString(c.getColumnIndex("tag"));
                    String time = c.getString(c.getColumnIndex("time"));
                    imageInfo.setFreeipName(time);
                    if ("img".equals(tag)){
                        imageInfo.setTag(ImageInfo.IMG);
                        String path = Environment.getExternalStorageDirectory()
                                .getPath() + FILEPATH + time + ".jpeg";
                        File img = new File(path);
                        if (img.exists()){
                            imageInfo.setTime(img.lastModified());
                            imageInfo.setPath(Environment.getExternalStorageDirectory()
                                    .getPath() + FILEPATH);
                            imageInfo.setFileName(time + ".jpeg");
                        }
                    } else {
                        imageInfo.setTag(ImageInfo.VIDEO);
                        final String root = FileHelper.getPhoneRootPath();
                        File img = new File(root + "p2p/image/",time + ".jpeg");
                        if (img == null || !img.exists()){
                            continue;
                        }
                        File mp4File = new File(root + "p2p/video/",time + ".mp4");
                        if (mp4File == null || !mp4File.exists()){
                            continue;
                        }
                        imageInfo.setTime(img.lastModified());
                        imageInfo.setPath(Environment.getExternalStorageDirectory()
                                .getPath() + VIDEOSTREAM);
                        imageInfo.setFileName(time + ".h264");
                    }
                    imageInfo.setCurrUser("");
                    imageInfo.setdId(c.getString(c.getColumnIndex("dId")));
                    imageInfo.setdName(c.getString(c.getColumnIndex("dName")));
                    imageInfoList.add(imageInfo);
                }
                if (!c.isClosed()){
                    c.close();
                }
                KLog.getInstance().d("freeip old data image info list %s",imageInfoList).print();
                if (imageInfoList != null && imageInfoList.size() > 0){
                    for (ImageInfo record : imageInfoList){
                        delete("image"," dId = ? and time = ? ",new String[]{record.getdId(),record.getFreeipName()});
                        DBAction.getInstance().insertDeviceMedia(record);
                    }
                }
                closeDB();
                return;
            }
        }
        closeDB();
    }

    class FreeIpSQLiteDBHelper extends SQLiteOpenHelper {

        private static final String DBNAME = "rtsp.db";
        private static final int DBVERSION = 7;
        public static final String TABLENAME = "device";
        public static final String MEDIA = "image";
        public static final String USERACCOUNT = "user_Account";

        public static final String SNTABLE = "sntable";
        public static final String CACHE_DEVICE= "cache_device";

        public static final String PREVIEWTABLE = "preview";

        public FreeIpSQLiteDBHelper(Context context) {
            super(context, DBNAME, null, DBVERSION);
        }

        private static final String sql = "create table if not exists " + TABLENAME + " (_id varchar(50),deviceName varchar(40),deviceType varchar(10),deviceIp varchar(40),devicePort integer,userName varchar(30),userPwd varchar(30),deviceChannels integer,deviceId text)";
        private static final String sql2 = "create table if not exists " + MEDIA + " (tag varchar(20),currUser varchar(20),dId varchar(20),time varchar(30),dName varchar(30))";
        private static final String sql3 = "create table if not exists " + SNTABLE + " (sn varchar(20),deviceUser varchar(50),devicePwd varchar(50))";
        private static final String sql4 = "create table if not exists "+ USERACCOUNT +"(id integer primary key " + "autoincrement not null,account text not null,password text not null,account_spare text)";

        private static final String CACHE_DEVICE_TABLE = "create table if not exists " + CACHE_DEVICE +
                "(id integer primary key autoincrement not null," +
                "userid text not null," + //userid指定唯一用户
                "position integer ," +
                "device_id text ," +
                "player_config text ," +
                "cache_spare text)";

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(sql);
            db.execSQL(sql2);
            db.execSQL(sql3);
            db.execSQL(sql4);
            db.execSQL(CACHE_DEVICE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            switch (oldVersion){
                case 0:
                case 1:
                case 2:
                case 3:
                    String sql4 = "create table if not exists " + PREVIEWTABLE + " (sn varchar(20),playerId integer,deviceStreams integer,mode integer,deviceName varchar(40),deviceModle varchar(40))";
                    db.execSQL(sql4);
                case 4:
                    db.execSQL(CACHE_DEVICE_TABLE);
                case 5:
                    db.execSQL(" DROP TABLE " + CACHE_DEVICE);
                    db.execSQL(CACHE_DEVICE_TABLE);
                case 6:
                    db.execSQL(" ALTER TABLE " + TABLENAME + " ADD COLUMN deviceId text ");
            }
        }
    }

}
