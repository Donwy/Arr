package com.longse.lsapc.lsacore.sapi.sharedPreferences;

import android.content.Context;

import java.util.concurrent.TimeUnit;

public interface ICache<T,K> {
  void init(Context ctx);
  boolean addCache(T obj, K key);
  boolean addCache(T obj, K key, long time, TimeUnit timeUnit);
  boolean delCache(K key);
  boolean changeCache(K key, T newValue);
  boolean changeCache(K key, T newValue, long time, TimeUnit timeUnit);
  T queryCache(K key, K defValue);
}
