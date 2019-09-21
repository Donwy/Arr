package com.longse.lsapc.lsacore.sapi.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.longse.lsapc.lsacore.sapi.log.KLog;


import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by lw on 2017/11/30.
 */

public class DBHelper {

    private static final String TAG = "DBHelper";

    private static final DBHelper ourInstance = new DBHelper();

    private net.sqlcipher.database.SQLiteDatabase sqLiteDatabase;

    private SQLiteOpenHelper sqLiteOpenHelper;

    private static int DBBaseSum = 0;

    public static DBHelper getInstance() {
        return ourInstance;
    }

    private DBHelper() {
    }

    /**
     * 推荐在Application中调用该方法
     * @param sqLiteOpenHelper
     */
    public void init(SQLiteOpenHelper sqLiteOpenHelper){
        if (sqLiteOpenHelper != null){
            this.sqLiteOpenHelper = sqLiteOpenHelper;
        }
    }

    /**
     * 是否已初始化
     * @return
     */
    private boolean checkInit(){
        return sqLiteOpenHelper != null;
    }

    private synchronized SQLiteDatabase openDB(){
        if (sqLiteOpenHelper == null){
            KLog.getInstance().e("OpenDB sqLiteOpenHelper is null.").tag(TAG).print();
            return null;
        }
        if(DBBaseSum == 0){
            sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase("123456");
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

    /**
     * 插入数据
     * 推荐使用该方法
     * @param table ： 需要插入数据的表名
     * @param values ： 键值对
     */
    public synchronized void insert(String table, ContentValues values){
        if (!checkInit()){
            KLog.getInstance().e("no init.").tag(TAG).print();
            return;
        }
        if (TextUtils.isEmpty(table) || values == null || values.size() == 0){
            KLog.getInstance().e("insert param tableName is empty or ContentValues is null or ContentValues size is 0.").tag(TAG).print();
            return;
        }
        openDB();
        if (sqLiteDatabase != null){
            sqLiteDatabase.insert(table,null,values);
            KLog.getInstance().d("insert insert ok ,table = %s",table).tag(TAG).print();
        }
//        closeDB();
    }

    /**
     * 更新数据
     * 推荐使用方法
     * @param table : 操作的表名
     * @param values ： 需要修改的键值
     * @param whereClause ： 定位具体位置的查询语句 (相当于SQL语句中 where 后面的内容)
     * @param whereArgs ： whereClause 中的占位符所对应的具体值
     */
    public synchronized void update(String table, ContentValues values, String whereClause, String[] whereArgs){
        if (!checkInit()){
            KLog.getInstance().e("no init.").tag(TAG).print();
            return;
        }
        if (TextUtils.isEmpty(table) || values == null || values.size() == 0){
            KLog.getInstance().e("update param is empty or null or 0.").tag(TAG).print();
            return;
        }
        openDB();
        if (sqLiteDatabase != null){
            sqLiteDatabase.update(table,values,whereClause,whereArgs);
        }
//        closeDB();
    }

    /**
     * 删除数据
     * 推荐使用方法
     * @param table : 操作的表名
     * @param whereClause : 定位具体位置的查询语句 (相当于SQL语句中 where 后面的内容)
     * @param whereArgs : whereClause 中的占位符所对应的具体值
     */
    public synchronized void delete(String table, String whereClause, String[] whereArgs){
        if (!checkInit()){
            KLog.getInstance().e("no init.").tag(TAG).print();
            return;
        }
        if (TextUtils.isEmpty(table) || TextUtils.isEmpty(whereClause) || whereArgs == null || whereArgs.length == 0){
            KLog.getInstance().e("delete param is empty or null or 0.").tag(TAG).print();
            return;
        }
        openDB();
        if (sqLiteDatabase != null){
            sqLiteDatabase.delete(table,whereClause,whereArgs);
            KLog.getInstance().d("delete delete ok ,table = %s",table).tag(TAG).print();
        }
//        closeDB();
    }

    /**
     * 直接执行SQL语句
     * 增删改 操作可调该方法执行
     * sql示例: 插入数据 ： insert into table ( k1 , k2 ) values ( v1 , v2 )
     *          更新数据 ： update table set k1 = v1 , k2 = v2 where k3 = v3 and k4 = v4
     *          删除语句 ： delete from table where k1 = v1 and k2 = v2
     * @param sql
     */
    public synchronized void execSQL(String sql){
        if (!checkInit()){
            KLog.getInstance().e("no init.").tag(TAG).print();
            return;
        }
        KLog.getInstance().d("execSQL ,param sql = %s",sql).tag(TAG).print();
        if (TextUtils.isEmpty(sql)){
            KLog.getInstance().e("execSQL param sql is empty.").tag(TAG).print();
            return;
        }
        openDB();
        if (sqLiteDatabase != null){
            sqLiteDatabase.execSQL(sql);
        }
//        closeDB();
    }

    /**
     * 查询数据
     * @param sql : 查询语句
     *            查询语句示例 ： select * from table where k1 = v1 and k2 = v2 order by id desc limit 100 offset 0
     *            视具体情况增删示例内容
     * @param selectionArgs : 查询语句内部占位符所对应的具体值 (如果所有内容都已经写在SQL语句中，该值可为null)
     * @param queryListener : 查询结果回调
     */
    public synchronized  <T> List<T> query(String sql, String[] selectionArgs, QueryListener<T> queryListener){
        if (!checkInit()){
            KLog.getInstance().e("no init.").tag(TAG).print();
            if (queryListener != null){
                queryListener.error();
            }
            return null;
        }
        if (TextUtils.isEmpty(sql)){
            KLog.getInstance().e("query param sql is empty .").tag(TAG).print();
            if (queryListener != null){
                queryListener.error();
            }
            return null;
        }
        openDB();
        if (sqLiteDatabase != null){
            Cursor c = sqLiteDatabase.rawQuery(sql,selectionArgs);
            try {
                if (c != null && queryListener != null){
                    List<T> list = queryListener.queryOk(c);
                    if (!c.isClosed()){
                        c.close();
                    }
//                    closeDB();
                    return list;
                }
            } finally {
                if (!c.isClosed()){
                    c.close();
                }
            }
        }
//        closeDB();
        if (queryListener != null){
            queryListener.error();
        }
        return null;
    }

    /**
     * 查询数据库回调接口
     */
    public interface QueryListener<T >{
        /**
         * 注意：不要在该回调方法内开线程使用Cursor
         * @param c
         */
        List<T> queryOk(Cursor c);
        void error();
    }

}
