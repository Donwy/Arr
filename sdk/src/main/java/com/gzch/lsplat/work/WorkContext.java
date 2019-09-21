package com.gzch.lsplat.work;

import com.longse.lsapc.lsacore.BitdogInterface;

/**
 * Created by lw on 2018/1/9.
 */

public final class WorkContext {

    /**
     * 登录用户userId
     */
    public static final String USER_ID_KEY = "login_user_id";

    /**
     * 设备列表内存数据 + cmd
     */
    public static final String GROUP_DEVICE_DATA_KEY = "group_device_data_work";
    /**
     * 事件消息内存数据 + cmd
     */
    public static final String EVENT_MSG_DATA_KEY = "event_msg_data_work";

    /**
     * 账号密码登录时的账号
     * 第三方登录时，该值为""
     */
    public volatile static String ACCOUNT = "";

    /**
     * 第一次请求到设备列表时删除上次的缓存
     */
    public static boolean firstStart2DelCacheDevice = true;
    /**
     * 第一次请求到分组列表时删除上次的缓存
     */
    public static boolean firstStart2DelCacheDGroup = true;

    public static final int FREEIP_APP = 1;

    public static final int BITDOG_APP = 2;

    public static final int CONTEXT_APP;

    static {
        final String packageName = BitdogInterface.getInstance().getApplicationContext().getPackageName();
        if ("com.gzch.lsplat.bitdog".equals(packageName)){
            CONTEXT_APP = BITDOG_APP;
        } else if ("com.xc.hdscreen".equals(packageName)){
            CONTEXT_APP = FREEIP_APP;
        } else {
            CONTEXT_APP = BITDOG_APP;
        }
    }
}
