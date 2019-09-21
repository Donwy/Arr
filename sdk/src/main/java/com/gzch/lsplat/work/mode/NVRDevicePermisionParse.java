package com.gzch.lsplat.work.mode;

import java.io.Serializable;

/**
 * Created by lw on 2019/5/23.
 */

public class NVRDevicePermisionParse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 0 - 10 位为本地权限，APP不使用
     */
    private static final int START_VALID_PERMISION = 10;

    /**
     * 预览
     */
    public static final int SUPPORT_LIVE_VIEW = START_VALID_PERMISION + 1;

    /**
     * PTZ操作
     */
    public static final int SUPPORT_PTZ = SUPPORT_LIVE_VIEW + 1;

    /**
     * 回放
     */
    public static final int SUPPORT_PLAYBACK = SUPPORT_PTZ + 1;

    /**
     * 摄像机录像设置
     */
    public static final int SUPPORT_RECORD_SETTING = SUPPORT_PLAYBACK + 1;

    /**
     * 摄像机编码设置
     */
    public static final int SUPPORT_CODE_SETTING = SUPPORT_RECORD_SETTING + 1;

    /**
     * 摄像机OSD设置
     */
    public static final int SUPPORT_OSD_SETTING = SUPPORT_CODE_SETTING + 1;

    /**
     * 摄像机图像设置
     */
    public static final int SUPPORT_VIDEO_SETTING = SUPPORT_OSD_SETTING + 1;

    /**
     * 摄像机移动侦测设置
     */
    public static final int SUPPORT_MOTION_DETECTION_SETTING = SUPPORT_VIDEO_SETTING + 1;

    /**
     * 摄像机 文件备份
     */
    public static final int SUPPORT_FILE_BACKUP = SUPPORT_MOTION_DETECTION_SETTING + 1;

    /**
     * 隐私遮挡
     */
    public static final int SUPPORT_PRIVACY_MASK = SUPPORT_FILE_BACKUP + 1;

    /**
     * 视频丢失
     */
    private static final int SUPPORT_VIDEO_LOSS = SUPPORT_PRIVACY_MASK + 8;

    /**
     * 磁盘管理
     */
    public static final int SUPPORT_DISK = 37;

    /**
     * 通道设置
     */
    private static final int SUPPORT_CAMERA_SETUP = SUPPORT_DISK + 1;

    /**
     * 通用设置
     */
    private static final int SUPPORT_GENERAL_SETUP = SUPPORT_CAMERA_SETUP + 1;

    /**
     * 网络设置
     */
    private static final int SUPPORT_NETWORK_SETUP = SUPPORT_GENERAL_SETUP + 1;

    /**
     * 显示设置
     */
    private static final int SUPPORT_DISPLAY_SETUP = SUPPORT_NETWORK_SETUP + 1;

    /**
     * 异常设置
     */
    private static final int SUPPORT_EXCEPTION_SETUP = SUPPORT_DISPLAY_SETUP + 1;

    /**
     * 用户设置
     */
    private static final int SUPPORT_USER_SETUP = SUPPORT_EXCEPTION_SETUP + 1;

    /**
     * 系统设置
     */
    private static final int SUPPORT_SYS_SETUP = SUPPORT_USER_SETUP + 1;

    /**
     * 日志搜索
     */
    private static final int SUPPORT_LOG_SEARCH = SUPPORT_SYS_SETUP + 1;

    /**
     * 手动升级
     */
    private static final int SUPPORT_MANUAL_UPDATE = SUPPORT_LOG_SEARCH + 1;

    /**
     * 在线升级
     */
    public static final int SUPPORT_ONLINE_UPDATE = SUPPORT_MANUAL_UPDATE + 1;



    /**
     * 自动维护
     */
    public static final int SUPPORT_AUTO_MAINTAIN = SUPPORT_ONLINE_UPDATE + 1;

    /**
     * 恢复默认设置
     */
    public static final int SUPPORT_RESTORE_DEFAULT = SUPPORT_AUTO_MAINTAIN + 1;

    /**
     * 关机重启
     */
    public static final int SUPPORT_SHUTDOWN_REBOOT = SUPPORT_RESTORE_DEFAULT + 1;

    /**
     * 通道配置
     */
    private static final int SUPPORT_CHANNEL_CONFIG = SUPPORT_SHUTDOWN_REBOOT + 1;

    /**
     * 远程报警
     */
    private static final int SUPPORT_ALARM = SUPPORT_CHANNEL_CONFIG + 1;

    /**
     * 远程升级前端
     */
    public static final int SUPPORT_UPGRADECAMERA = SUPPORT_ALARM + 1;

    /**
     * 本地升级前端 APP不使用
     * 智能报警
     */
    private static final int SUPPORT_ALARM_SETUP = SUPPORT_UPGRADECAMERA + 2;


}
