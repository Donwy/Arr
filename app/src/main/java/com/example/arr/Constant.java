package com.example.arr;

/**
 * Created by dk on 2018/1/3.
 */

public final class Constant {
    public static final String ISLOGIN = "islogin"; //存储是否登录
    public static final String SAVEUSERINFO = "userInfo";  //用户信息
    public static final String EXIT_LOGIN_STATUS = "exit_login_status";  //APP上一次是否是登录状态
    public static final String EVENT_REFRESH_LANGUAGE = "event_refresh_language";  //APP更新语言
    public static final String ACTION_CALENDAR = "select_time_playback";
    public static final String ACTION_CALENDAR_SN = "select_time_sn_playback";
    public static final String ORIENTATION_LANDSCAPE = "orientation_landscape";
    public static final String ORIENTATION_PORTRAIT = "orientation_portrait";
    public static final String LAST_START_TIME = "last_start_time";
    public static final String SAVE_PSW = "save_psw";//0 或者不传 保存密码，否则不保存(save = 1); 默认保存密码
    public static final String SAVE_SN_PSW = "save_sn_psw";//0 或者不传 保存密码，否则不保存(save = 1); 默认保存密码


    /***************持久变量***************************/
    public static int notificationSize = -1;
    public final static int CACHE_DAYS = 1;
    public static int FRAGMENT_TAB = -1;

    public static boolean isActive = false; //全局变量，判断是否是前台进程
    public static boolean isPhotoOrCamera = false; //全局变量，判断是否是进入相机或相册
    public static final String GESTURE_LOCK = "gesture_lock"; //存储手势密码的开始状态；
    public static final String GESTURE_LOCK_PWD = "gesture_lock_passward"; //存储手势密码；

    /**
     * 流量提醒开关
     */
    public static final String FLOW_TIPS_KEY = "flow_tips_key";
    public static final String FLOW_TIPS_OPEN = "open_flow_tips";
    public static final String FLOW_TIPS_CLOSE = "close_flow_tips";
}
