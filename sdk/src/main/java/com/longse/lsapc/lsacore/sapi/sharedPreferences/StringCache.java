package com.longse.lsapc.lsacore.sapi.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 使用SharedPreferences缓存字符串； 跨进程操作时不要使用
 * 
 * @author liuwei
 * 
 */
public class StringCache implements ICache<String, String> {
  private static StringCache instance;

  private StringCache() {}

  public static final StringCache getInstance() {
    synchronized (StringCache.class) {
      if (instance == null) {
        instance = new StringCache();
      }
      if(ctx == null)return instance;
      return instance.getDefaultCache();//default;
    }
  }

  private static Context ctx;

  private SharedPreferences sp;

  private SharedPreferences.Editor ed;

  private final static String DEFAULT_CACHE_NAME = "default_name";
  
  private final static int DEFAULT_MODE = Context.MODE_PRIVATE;

  private String cacheName = DEFAULT_CACHE_NAME;
  
  private int mode = DEFAULT_MODE;

  private final static String jsonValueKey = "cacheValueKey";// key代表的值: 需要保存的数据
  private final static String jsonTimeKey = "cacheTimeKey"; // key代表的值: 时间毫秒数 ; 小于0表示无时间限制

  @Override
  public void init(Context ctx) {
    StringCache.ctx = ctx;
  }

  /**
   * Context 为空时不可用
   * 调用时先测试
   * @return
   */
  public boolean checkEnable(){
    return ctx != null;
  }

  public StringCache getDefaultCache() {
    if (ctx == null) return null;
    if(!cacheName.equals(DEFAULT_CACHE_NAME)){
      cacheName = DEFAULT_CACHE_NAME;
      mode = DEFAULT_MODE;
      sp = ctx.getSharedPreferences(cacheName, mode);
      ed = sp.edit();      
    }else{
      mode = DEFAULT_MODE;
      if(sp == null){
        sp = ctx.getSharedPreferences(cacheName, mode);
        ed = sp.edit();
      }
    }
    if (this.queryCache("has_encrypted", "0").equals("0")) {
      handleTransition();
    }
    return this;
  }

  public StringCache setSharedPreferencesName(String name, int mode) {
    if (ctx == null) return null;
    if (TextUtils.isEmpty(name)) {
      return getDefaultCache();
    }
//    cacheName = name;
//    this.mode = mode;
    sp = ctx.getSharedPreferences(name, mode);
    ed = sp.edit();
    return this;
  }

  /**
   * encrypt function
   * @return cipherText base64
   */
  private String encryptPreference(String plainText){
    return EncryptUtil.getInstance(ctx).encrypt(plainText);
  }

  /**
   * decrypt function
   * @return plainText
   */
  private String decryptPreference(String cipherText){
    return EncryptUtil.getInstance(ctx).decrypt(cipherText);
  }

  private boolean putValue(String key, String value) {
    if (ctx == null) return false;
    if (ed == null) {
      sp = ctx.getSharedPreferences(cacheName, mode);
      ed = sp.edit();
    }
    if (ed != null) {
      ed.putString(encryptPreference(key), encryptPreference(value));
      ed.apply();
      return true;
    }
    return false;
  }

  /**
   * 无时间限制缓存
   */
  @Override
  public boolean addCache(String key, String value) {
    if (TextUtils.isEmpty(key)) return false;
    return putValue(key, createString(value));
  }

  /**
   * 有时间限制； 过期失效 时间最低支持到毫秒,再小会丢失精度
   */
  @Override
  public boolean addCache(String key, String value, long time, TimeUnit timeUnit) {
    if (TextUtils.isEmpty(key)) return false;
    long t = TimeUnit.MILLISECONDS.convert(time, timeUnit) + System.currentTimeMillis();
    return putValue(key, createString(value, t));
  }

  @Override
  public boolean delCache(String key) {
    if (TextUtils.isEmpty(key)) return false;
    return putValue(key, "");
  }

  @Override
  public boolean changeCache(String key, String newValue) {
    return addCache(key,newValue);
  }

  @Override
  public boolean changeCache(String key, String newValue, long time, TimeUnit timeUnit) {
    return addCache(key,newValue,time, timeUnit);
  }

  @Override
  public String queryCache(String key, String defValue) {
    if (TextUtils.isEmpty(key)) return defValue;
    if (sp != null) {
      String data = sp.getString(encryptPreference(key), null);
      data = (data == null ? defValue : decryptPreference(data));
      return checkValue(data);
    }
    return defValue;
  }

  private String createString(String value) {
    return createString(value, -1);
  }

  private String createString(String value, long time) {
    if (time >= 0 && time < System.currentTimeMillis()) return "";// 时间已过期
    try {
      JSONObject json = new JSONObject();
      json.put(jsonValueKey, value);
      if (time > 0) {
        json.put(jsonTimeKey, time);
      } else {
        json.put(jsonTimeKey, -1L);
      }
      if (json != null) {
        return json.toString();
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return "";
  }

  private String checkValue(String data) {
    if (TextUtils.isEmpty(data)) return data;
    try {
      JSONObject json = new JSONObject(data);
      if (json != null) {
        long time = json.optLong(jsonTimeKey);
        if (time <= 0L) {
          return json.optString(jsonValueKey);
        }
        if (time > 0L && time > System.currentTimeMillis()) {
          return json.optString(jsonValueKey);
        } else {
          return "";
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return data;
  }

  /**
   * 处理加密过度
   */
  public void handleTransition() {
    Map<String, ?> oldMap = sp.getAll();
    Map<String, String> newMap = new HashMap<>();
    for (Map.Entry<String, ?> entry : oldMap.entrySet()){
      Log.i(StringCache.class.getName(), "key:"+entry.getKey()+", value:"+ entry.getValue());
      newMap.put(encryptPreference(entry.getKey()), encryptPreference(entry.getValue().toString()));
    }
    ed.clear().commit();
    for (Map.Entry<String, String> entry : newMap.entrySet()){
      ed.putString(entry.getKey(), entry.getValue());
    }
    ed.commit();
    this.addCache("has_encrypted", "1");
  }
}
