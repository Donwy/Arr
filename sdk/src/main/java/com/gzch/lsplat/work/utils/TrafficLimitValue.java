package com.gzch.lsplat.work.utils;

import android.text.TextUtils;

import com.example.sdk.R;
import com.gzch.lsplat.work.action.DBAction;
import com.longse.lsapc.lsacore.BitdogInterface;
import com.longse.lsapc.lsacore.interf.ThreadWrapper;
import com.longse.lsapc.lsacore.sapi.sharedPreferences.StringCache;

/**
 * Created by lw on 2018/11/14.
 */

public class TrafficLimitValue {

    public static final int array = R.array.flow_values;

    private final String[] trafficValue;

    private static final String CACHE_KEY = "key_traffic_limit_value";

    private static final long[] MAX_FLOWS = {1024 * 1024 * 50,1024 * 1024 * 100,
                                                  1024 * 1024 * 200,1024 * 1024 * 500};

    private static TrafficLimitValue instance;

    private TrafficLimitValue(){
        trafficValue = BitdogInterface.getInstance().getApplicationContext().getResources().getStringArray(array);
    }

    public final static TrafficLimitValue getInstance() {
        synchronized (TrafficLimitValue.class){
            if (instance == null){
                instance = new TrafficLimitValue();
            }
            return instance;
        }
    }

    public int save(String value){
        if (TextUtils.isEmpty(value)){return -1;}
        for (int i = 0; i < trafficValue.length; i++){
            if (value.equals(trafficValue[i])){
                saveValue(value);
                return i;
            }
        }
        return -1;
    }

    public int save(int pos){
        if (pos >= 0 && pos < trafficValue.length){
            saveValue(trafficValue[pos]);
            return pos;
        }
        return -1;
    }

    public int get(){
        String value = getValue();
        if (TextUtils.isEmpty(value)){return 0;}
        for (int i = 0; i < trafficValue.length; i++){
            if (value.equals(trafficValue[i])){
                return i;
            }
        }
        return 0;
    }

    public long getMax(){
        return MAX_FLOWS[get()];
    }

    private void saveValue(String value){
        if (TextUtils.isEmpty(value)){return;}
        StringCache.getInstance().addCache(CACHE_KEY,value);
        BitdogInterface.getInstance().post(new Runnable() {
            @Override
            public void run() {
                DBAction.getInstance().updateFlowMaxSize();
            }
        }, ThreadWrapper.THREAD_IO);
    }

    private String getValue(){
        return StringCache.getInstance().queryCache(CACHE_KEY,trafficValue[0]);
    }
}
