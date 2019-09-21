package com.gzch.lsplat.work.mode;

import java.io.Serializable;

/**
 * Created by lw on 2019/3/19.
 */

/**
 * 设备本地端权限
 * typedef enum SDK_MENU_ID {
 LOCAL_LIVE_VIEW = 0,  //本地预览，包括音频
 LOCAL_PTZ_CONTROL,    //本地PTZ设置
 LOCAL_PLAYBACK,     //本地回放设置
 LOCAL_RECORD,      //本地录像设置
 LOCAL_ENCODE,      //本地编码参数
 LOCAL_OSD_SETUP,     //本地OSD设置
 LOCAL_VIDEO_SETUP,    //本地图像设置
 LOCAL_MOTION_DETECTION,  //本地移动侦测
 LOCAL_FILE_BACKUP,     //本地文件备份
 LOCAL_PRIVACY_MASK,     //本地隐私遮挡
 LOCAL_VIDEO_LOSS,       //本地视频丢失  10

 REMOTE_LIVE_VIEW,    //远程预览，包括音频
 REMOTE_PTZ_CONTROL,   //远程PTZ设置
 REMOTE_PLAYBACK,    //远程回放设置
 REMOTE_RECORD,      //远程录像设置
 REMOTE_ENCODE,      //远程编码参数
 REMOTE_OSD_SETUP,    //远程OSD设置
 REMOTE_VIDEO_SETUP,   //远程图像设置
 REMOTE_MOTION_DETECTION,//远程移动侦测
 REMOTE_FILE_BACKUP,   //远程文件备份
 REMOTE_PRIVACY_MASK,    //远程隐私遮挡
 REMOTE_VIDEO_LOSS,      //远程视频丢失  21

 LOCAL_POLL_CONTROL,   //本地轮巡设置
 LOCAL_DISK,       //本地磁盘管理
 LOCAL_CAMERA_SETUP,    //本地通道设置
 LOCAL_GENERAL_SETUP,  //本地通用设置
 LOCAL_NETWORK_SETUP,  //本地网络设置
 LOCAL_DISPLAY_SETUP,  //本地显示设置
 LOCAL_EXCEPTION_SETUP,  //本地异常设置
 LOCAL_USER_SETUP,    //本地用户设置
 LOCAL_SYS_SETUP,    //本地系统设置
 LOCAL_LOG_SEARCH,    //本地日志搜索
 LOCAL_MANUAL_UPDATE,  //本地手动升级
 LOCAL_ONLINE_UPDATE,  //本地在线升级
 LOCAL_AUTO_MAINTAIN,  //本地自动维护
 LOCAL_RESTORE_DEFAULT,  //本地恢复默认
 LOCAL_SHUTDOWN_REBOOT,   //本地关机重启
 LOCAL_CHANNEL_CONFIG,   //本地通道配置
 LOCAL_ALARM,            //本地报警      38

 REMOTE_POLL_CONTROL,   //远程轮巡设置
 REMOTE_DISK,       //远程磁盘管理
 REMOTE_CAMERA_SETUP,  //远程通道设置
 REMOTE_GENERAL_SETUP,  //远程通用设置
 REMOTE_NETWORK_SETUP,  //远程网络设置
 REMOTE_DISPLAY_SETUP,  //远程显示设置
 REMOTE_EXCEPTION_SETUP,  //远程异常设置
 REMOTE_USER_SETUP,    //远程用户设置
 REMOTE_SYS_SETUP,    //远程系统设置
 REMOTE_LOG_SEARCH,    //远程日志搜索
 REMOTE_MANUAL_UPDATE,  //远程手动升级
 REMOTE_ONLINE_UPDATE,  //远程在线升级
 REMOTE_AUTO_MAINTAIN,  //远程自动维护
 REMOTE_RESTORE_DEFAULT,  //远程恢复默认
 REMOTE_SHUTDOWN_REBOOT,  //远程关机重启
 REMOTE_CHANNEL_CONFIG,  //远程通道配置
 REMOTE_ALARM,           //远程报警      55
 REMOTE_UPGRADECAMERA=60,       //远程升级前端
 LOCAL_UPGRADECAMERA=61,      //本地升级前端
 REMOTE_ALARM_SETUP=62,    //远程智能报警
 LOCAL_ALARM_SETUP=63,    //本地智能报警   63
 }SDK_MENU_ID_E;
 */
public class DevicePermisionParse implements Serializable {

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
    private static final int SUPPORT_VIDEO_LOSS = SUPPORT_PRIVACY_MASK + 1;

    /**
     * 22 - 38 为本地权限 APP不使用
     * 轮巡设置
     */
    private static final int SUPPORT_POLL_CONTROL = SUPPORT_VIDEO_LOSS + 18;

    /**
     * 磁盘管理
     */
    public static final int SUPPORT_DISK = SUPPORT_POLL_CONTROL + 1;

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

    /**
     * 没有权限
     */
    public static final String NO_PERMISION = "000000000000000000000000000000000000000000000000000000000000000";

    private String permisionStr;

    public DevicePermisionParse(String permisionStr){
        if (permisionStr == null){
            permisionStr = NO_PERMISION;
        } else {
            int s = NO_PERMISION.length() - permisionStr.length();
            if (s != 0){
                if (s > 0){
                    for (int i = 0; i < s; i++){
                        permisionStr += "1";/* 如果设备不上传权限，默认有该权限*/
                    }
                } else {
                    permisionStr = permisionStr.substring(0,NO_PERMISION.length() - 1);
                }
            }
        }
        this.permisionStr = permisionStr;
//        KLog.getInstance().d("DevicePermisionParse permisionStr = %s" , permisionStr).print();
    }

    public boolean isSupport(int index,boolean isNVR){
//        if (isNVR){
//            index = getNVRIndex(index);
//        }
//        if (index >=0 && index < permisionStr.length()){
////            KLog.getInstance().d("isSupport permisionStr = %s , index = %d , charat = %s",permisionStr,index,permisionStr.charAt(index) + "").ws(5).print();
//            return permisionStr.charAt(index) == '1';
//        }
        return true;/* 如果权限长度不符合要求，默认有该权限 */
    }

    public String getPermisionStr() {
        return permisionStr;
    }

    private int getNVRIndex(int index){
        int result = index;
        switch (index){
            case SUPPORT_DISK:
                result = NVRDevicePermisionParse.SUPPORT_DISK;
                break;
            case SUPPORT_ONLINE_UPDATE:
                result = NVRDevicePermisionParse.SUPPORT_ONLINE_UPDATE;
                break;
            case SUPPORT_RESTORE_DEFAULT:
                result = NVRDevicePermisionParse.SUPPORT_RESTORE_DEFAULT;
                break;
            case SUPPORT_SHUTDOWN_REBOOT:
                result = NVRDevicePermisionParse.SUPPORT_SHUTDOWN_REBOOT;
                break;
        }
        return result - 1;
    }

    @Override
    public String toString() {
        return "DevicePermisionParse{" +
                "permisionStr='" + permisionStr + '\'' +
                '}';
    }
}
