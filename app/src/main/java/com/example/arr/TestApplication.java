package com.example.arr;


import android.app.Application;

import com.example.sdk.BitvisionSdk;

/**
 * @author Donvy_y
 * @date 2019/9/20
 */
public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BitvisionSdk.init(this);
    }
}
