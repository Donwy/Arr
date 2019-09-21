package com.longse.lsapc.lsacore.sapi.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.text.TextUtils;

import com.longse.lsapc.lsacore.interf.ErrorCode;
import com.longse.lsapc.lsacore.sapi.log.KLog;

import java.util.List;

import static android.net.wifi.WifiManager.WIFI_STATE_DISABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_DISABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_UNKNOWN;

public class WiFiHelper {

  private static final String TAG = "WiFiHelper";

  private Context ctx;
  private static final String formatStr = "\"%s\"";

  private static final int NO_NET = 0;// 无密码
  private static final int WEP_NET = 1;
  private static final int WAP_NET = 2;

  private NetWorkListener netWorkListener;//wifi网络变化监听

  private Object lock = new Object();

  private static final int LOCK_TIME_OUT = 20 * 1000;

  private boolean NetWorkListenerWifiConnected = false;
  private boolean NetWorkListenerWifiEnable = false;

  public WiFiHelper(Context ctx){
    init(ctx);
  }

  class NetWorkListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) { //监听wifi的打开与关闭
        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
        KLog.getInstance().d(TAG + "##conn##NetWorkListener wifi open or close status = %d",wifiState).print();
        switch (wifiState) {
          case WIFI_STATE_UNKNOWN :
          case WIFI_STATE_DISABLED :
          case WIFI_STATE_DISABLING :
            NetWorkListenerWifiEnable = false;
          case WIFI_STATE_ENABLED :
            NetWorkListenerWifiEnable = true;
            expanLock();
            break;
          case WIFI_STATE_ENABLING :
            break;
        }
      } else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())){
        Parcelable parcelableExtra = intent
                .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (null != parcelableExtra) {
          NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
          NetworkInfo.State state = networkInfo.getState();
          boolean isConnected = state == NetworkInfo.State.CONNECTED;
          KLog.getInstance().d(TAG + "##conn##NetWorkListener wifi connect status = %s",isConnected).print();
          if (isConnected) {
            NetWorkListenerWifiConnected = true;
            expanLock();
          }
        }
      }
    }
  }

  private void expanLock(){
    synchronized (lock){
      lock.notify();
    }
  }

  private void init(Context ctx) {

    if(ctx == null){
      throw new NullPointerException("WiFiManager init Context is null.");
    }
    this.ctx = ctx.getApplicationContext();
    netWorkListener = new NetWorkListener();
  }

  private void register(){
    if (ctx == null)return;
    IntentFilter filter = new IntentFilter();
    filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    ctx.registerReceiver(netWorkListener,filter);
  }

  private void unregister(){
    if(ctx != null && netWorkListener != null){
      ctx.unregisterReceiver(netWorkListener);
    }
  }

  /**
   * 连接指定的WiFi
   * @param account WiFi账号
   * @param word WiFi密码
   * @param clearPassword 是否清除以前的连接密码
   *                      如果该值为false，若以前连接上了该WiFi，
   *                      那么不管当前传入的WiFi密码是否正确，都能连接
   * @return 是否连接成功
   */
  public int conn(String account, String word, boolean clearPassword) {
    if (TextUtils.isEmpty(account)) {
      return ErrorCode.PARAMS_ERROR;
    }
    register();
    final String password;
    if (word == null){
      password = "";
    } else {
      password = word;
    }
    WifiManager mWifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
    KLog.getInstance().d(TAG + "##conn wifi start open status = %d",mWifiManager.getWifiState()).print();
    switch (mWifiManager.getWifiState()){
      case WIFI_STATE_DISABLED :
      case WIFI_STATE_DISABLING :
      case WIFI_STATE_UNKNOWN :
        unregister();
        return ErrorCode.WIFI_STATUS_UNKNOWN;
      case WIFI_STATE_ENABLED :
        break;
      case WIFI_STATE_ENABLING :
        KLog.getInstance().d(TAG + "##conn waiting wifi open start").print();
        boolean wifiConnect = wifiEnableingWait();//等待wifi打开
        KLog.getInstance().d(TAG + "##conn waiting wifi open stop").print();
        if(!wifiConnect || mWifiManager.getWifiState() != WIFI_STATE_ENABLED){
          KLog.getInstance().d(TAG + "##conn waiting wifi open ,wifi is DISABLE").print();
          unregister();
          return ErrorCode.WIFI_DISABLE_ERROR;
        }
        break;
    }
    //先判断附近是否有该wifi
    List<ScanResult> list = mWifiManager.getScanResults();
    boolean isContinue = false;
    for (ScanResult s : list) {
      if (s.SSID.equals(account)) {
        KLog.getInstance().d(TAG + "##conn has wifi ssid = " + s.SSID + "##acount = "
                + account).print();
        isContinue = true;
        break;
      }
    }
    if (!isContinue) {
      KLog.getInstance().d(TAG + "##conn there is no '%s' wifi",account).print();
      unregister();
      return ErrorCode.NO_FIND_WIFI_SSID_ERROR; //没找到这个wifi，请用户检查是否开启路由
    }
    //是否已经连上了wifi
    WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
    if(wifiInfo != null){//已经连上了一个wifi，判断是否是需要的这个wifi
      String ssid = wifiInfo.getSSID();
      KLog.getInstance().d(TAG + "##conn now connected wifi ssid = %s,account = %s",ssid,account).print();
      if(!clearPassword && ("\"" + account + "\"").equals(ssid)){
        KLog.getInstance().d(TAG + "##conn now connected '%s' ok",ssid).print();
        unregister();
        return ErrorCode.SUCCESS;//连接成功
      } else {
        int netWorkId = wifiInfo.getNetworkId();
        if(netWorkId != -1){
          mWifiManager.disconnect();
          mWifiManager.disableNetwork(netWorkId);
          if (clearPassword){
            mWifiManager.removeNetwork(netWorkId);
          }
          KLog.getInstance().d(TAG + "##conn disconnect now connected netWorkId = %d",netWorkId).print();
        }
      }
    }
    if (!clearPassword){
      //以前是否连过该wifi，尝试直接连接
      List<WifiConfiguration> confs = mWifiManager.getConfiguredNetworks();
      int netWorkid = -1;
      for (WifiConfiguration conf : confs) {
        if (("\"" + account.trim() + "\"").equals(conf.SSID)) {
          KLog.getInstance().d(TAG + "##conn has the wifi ssid = %s , try connected netWorkId",conf.SSID).print();
          netWorkid = conf.networkId;
          mWifiManager.enableNetwork(conf.networkId, true);
          mWifiManager.reconnect();
          break;
        }
      }

      if(netWorkid != -1) {
        boolean wifiConn = wifiConnectOk();//等待wifi连接成功
        WifiInfo wifiInfoTest = mWifiManager.getConnectionInfo();
        if(wifiInfoTest != null){
          String ssid = wifiInfoTest.getSSID();
          if(wifiConn && ("\"" + account + "\"").equals(ssid)){
            KLog.getInstance().d(TAG + "##conn has the wifi , try connected '%s' ok",ssid).print();
            unregister();
            return ErrorCode.SUCCESS;
          }else {
            KLog.getInstance().d(TAG + "##conn has the wifi , try connected netWorkId error , maybe the password is changed").print();
            mWifiManager.disconnect(); //连接失败；可能用户修改了密码,删除该网络；后面使用传入的密码继续尝试连接
            mWifiManager.disableNetwork(netWorkid);
            mWifiManager.removeNetwork(netWorkid);
          }
        }
      }
      if(mWifiManager.getWifiState() != WIFI_STATE_ENABLED){
        KLog.getInstance().d(TAG + "##conn connecting wifi enable false").print();
        unregister();
        return ErrorCode.WIFI_DISABLE_ERROR;
      }
    }

    int wifitype = getEncryptionType(account);
    KLog.getInstance().d(TAG + "##conn this wifi wifitype = " + wifitype + "##account = " + account).print();
    boolean wifiConnectResult = false;
    if (wifitype == WEP_NET) {
      wifiConnectResult = ConnectToNetworkWEP(account, password);
    } else if (wifitype == WAP_NET) {
      wifiConnectResult = ConnectToNetworkWPA(account, password);
    } else {
      wifiConnectResult = ConnectToNetworkOPEN(account);
    }
    if(!wifiConnectResult){
      KLog.getInstance().d(TAG + "##conn account and password connect wifi error").print();
      unregister();
      return ErrorCode.WIFI_CONNECT_FAIL;
    }
    KLog.getInstance().d(TAG + "##conn account and password connecting wifi ,waiting result").print();
    boolean wifiConn = wifiConnectOk();//等待wifi连接成功
    WifiInfo wifiInfoTest = mWifiManager.getConnectionInfo();
    if(wifiInfoTest != null){
      String ssid = wifiInfoTest.getSSID();
      if(wifiConn && ("\"" + account + "\"").equals(ssid)){
        KLog.getInstance().d(TAG + "##conn account and password connecting wifi ,connect '%s'ok",ssid).print();
        unregister();
        return ErrorCode.SUCCESS;
      }
    }
    if(mWifiManager.getWifiState() != WIFI_STATE_ENABLED){
      KLog.getInstance().d(TAG + "##conn connecting wifi enable false").print();
      unregister();
      return ErrorCode.WIFI_DISABLE_ERROR;
    }
    KLog.getInstance().d(TAG + "##conn account and password connecting wifi ,connect fail").print();
    unregister();
    return ErrorCode.WIFI_CONNECT_FAIL;
  }

  /**
   * wifi是否连接
   * @return
   */
  private boolean wifiConnectOk(){
    NetWorkListenerWifiConnected = false;
    try{
      long start = System.currentTimeMillis ();
      long waitTime = LOCK_TIME_OUT;
      while (wifiIsEnable()) {
        synchronized (lock) {
          lock.wait(waitTime);
        }
        if (!NetWorkListenerWifiConnected) {
          long now = System.currentTimeMillis ();
          long timeSoFar = now - start;
          if (timeSoFar >= LOCK_TIME_OUT){
            return false;
          }else{
            waitTime = LOCK_TIME_OUT - timeSoFar;
          }
        }else{
          if(NetWorkListenerWifiEnable){
            return true;
          }
        }
      }
    }catch (InterruptedException e){
      e.printStackTrace();
    }
    return false;
  }

  /**
   * 等待wifi打开
   */
  private boolean wifiEnableingWait(){
    try{
      long start = System.currentTimeMillis ();
      long waitTime = LOCK_TIME_OUT;
      for (;;) {
        synchronized (lock) {
          lock.wait(waitTime);
        }
        if (!wifiIsEnable()) {
          long now = System.currentTimeMillis ();
          long timeSoFar = now - start;
          if (timeSoFar >= LOCK_TIME_OUT){
            return false;
          }else{
            waitTime = LOCK_TIME_OUT - timeSoFar;
          }
        }else{
          return true;
        }
      }
    }catch (InterruptedException e){
      e.printStackTrace();
    }
    return false;
  }

  private boolean wifiIsEnable(){
    WifiManager mWifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
    return mWifiManager.getWifiState() == WIFI_STATE_ENABLED;
  }

  private int getEncryptionType(String ssid) {
    int encryptType = NO_NET;
    WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
    List<ScanResult> networkList = wifi.getScanResults();
    if (networkList != null) {
      for (ScanResult network : networkList) {
        if (ssid.equals(network.SSID)) {
          String Capabilities = network.capabilities;
          if (Capabilities.contains("WPA2")) {
            encryptType = WAP_NET;
          } else if (Capabilities.contains("WPA")) {
            encryptType = WAP_NET;
          } else if (Capabilities.contains("WEP")) {
            encryptType = WEP_NET;
          } else {
            encryptType = NO_NET;
          }
          break;
        }
      }

    }
    return encryptType;
  }

  private boolean ConnectToNetworkOPEN(String networkSSID) {
    try {
      WifiConfiguration conf = new WifiConfiguration();
      conf.SSID = "\"" + networkSSID + "\"";
      conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

      WifiManager wifiManager =
              (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
      int networkId = wifiManager.addNetwork(conf);

      if (networkId == -1) {
        networkId = wifiManager.addNetwork(conf);
      }

      if(networkId != -1){
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
      }
      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  private boolean ConnectToNetworkWEP(String networkSSID, String password) {
    try {
      WifiConfiguration conf = new WifiConfiguration();
      conf.SSID = "\"" + networkSSID + "\"";
      conf.wepKeys[0] = "\"" + password + "\"";
      conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
      conf.allowedGroupCiphers.set(WifiConfiguration.AuthAlgorithm.OPEN);
      conf.allowedGroupCiphers.set(WifiConfiguration.AuthAlgorithm.SHARED);

      WifiManager wifiManager =
          (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
      int networkId = wifiManager.addNetwork(conf);

      if (networkId == -1) {
        // 16进制密码尝试去掉引号
        conf.wepKeys[0] = password;
        networkId = wifiManager.addNetwork(conf);
      }

      if(networkId != -1){
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
      }
      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  private boolean ConnectToNetworkWPA(String networkSSID, String password) {
    try {
      WifiConfiguration conf = new WifiConfiguration();
      conf.SSID = "\"" + networkSSID + "\"";
      conf.preSharedKey = "\"" + password + "\"";
      conf.status = WifiConfiguration.Status.ENABLED;
      conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
      conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
      conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
      conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
      conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

      WifiManager wifiManager =
          (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
      int networkId = wifiManager.addNetwork(conf);

      if (networkId == -1) {
        conf.wepKeys[0] = password;
        networkId = wifiManager.addNetwork(conf);
      }

      if(networkId != -1){
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
      }
      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

}
