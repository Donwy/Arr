package com.gzch.lsplat.work.file;

import com.gzch.lsplat.work.WorkContext;
import com.longse.lsapc.lsacore.sapi.file.FileHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lw on 2017/12/12.
 */

public class FileManager {

    /**
     * APP 根目录
     */
    private static final String root;

    static {
        if (WorkContext.CONTEXT_APP == WorkContext.BITDOG_APP) {
            root = "BitVision" + File.separator;
        } else {
            root = "FreeIP" + File.separator;
        }
    }

    /**
     * 缓存文件目录
     */
    private static final String cache = root + "cache" + File.separator;

    /**
     * 设备头像,用户头像等目录
     */
    private static final String icon = root + "icon" + File.separator;

    /**
     * 媒体文件目录，保存相册需要的各种截图，录像等
     */
    private static final String media = root + "media" + File.separator;
    //手动截图
    private static final String mediaImg = media + "img" + File.separator;
    private static final String mediaPrewImg = mediaImg + "prewImg" + File.separator;
    private static final String mediaPlaybackImg = mediaImg + "playbackImg" + File.separator;
    private static final String mediaVrImg = mediaImg + "vrImg" + File.separator;
    private static final String aiFaceImg = mediaImg + "aiFaceImg" + File.separator;

    //录像
    private static final String mediaVideo = media + "video" + File.separator;
    //MP4首帧图片 缩略图
    private static final String mediaVideoImg = mediaVideo + "img" + File.separator;
    //mp4
    private static final String mediaMp4 = mediaVideo + "mp4" + File.separator;
    private static final String mediaPrewVideo = mediaMp4 + "prewVideo" + File.separator;
    private static final String mediaPlaybackVideo = mediaMp4 + "playbackVideo" + File.separator;
    private static final String mediaVrVideo = mediaMp4 + "vrVideo" + File.separator;
    private static final String mediaCloudVideo = mediaMp4 + "cloudVideo" + File.separator;
    //h264
    private static final String mediaH264 = mediaVideo + "h264" + File.separator;
    private static final String mediaPrewH264 = mediaH264 + "prewH264" + File.separator;
    private static final String mediaPlaybackH264 = mediaH264 + "playbackH264" + File.separator;
    private static final String mediaVrH264 = mediaH264 + "vrH264" + File.separator;

    /**
     * 下载文件存放目录
     */
    private static final String download = root + "download" + File.separator;

    /**
     * 实时预览文件夹
     */
    public static final int PREVIEW_DIR = 1;

    /**
     * 回放文件夹
     */
    public static final int PLAYBACK_DIR = 2;

    /**
     * VR文件夹
     */
    public static final int VR_DIR = 3;
    /**
     * 云录像文件夹
     */
    public static final int CLOUD_DIR = 4;

    /**
     * 人脸识别文件夹
     */
    public static final int AI_FACE_DIR = 5;

    /**
     * 下载文件存放目录
     *
     * @return
     */
    public static String getDownloadDir() {
        String path = FileHelper.getPhoneRootPath() + download;
        FileHelper.createSDCardDir(path);
        return path;
    }

    /**
     * 缓存文件目录
     *
     * @return
     */
    public static String getCacheFileDir() {
        String path = FileHelper.getPhoneRootPath() + cache;
        FileHelper.createSDCardDir(path);
        return path;
    }

    /**
     * 设备头像,用户头像等文件
     *
     * @return
     */
    public static String getIconFileDir() {
        String path = FileHelper.getPhoneRootPath() + icon;
        FileHelper.createSDCardDir(path);
        return path;
    }

    /**
     * 手动截图文件
     *
     * @param dir
     * @return
     */
    public static String getMediaImgDir(int dir) {
        String parent = mediaImg;
        switch (dir) {
            case PREVIEW_DIR:
                parent = mediaPrewImg;
                break;
            case PLAYBACK_DIR:
                parent = mediaPlaybackImg;
                break;
            case VR_DIR:
                parent = mediaVrImg;
                break;
            case AI_FACE_DIR:
                parent = aiFaceImg;
                break;
        }
        String path = FileHelper.getPhoneRootPath() + parent;
        FileHelper.createSDCardDir(path);
        return path;
    }

    public static String getMediaVideoImg() {
        String path = FileHelper.getPhoneRootPath() + mediaVideoImg;
        FileHelper.createSDCardDir(path);
        return path;
    }

    /**
     * 录像mp4文件
     *
     * @param dir
     * @return
     */
    public static String getMediaVideoDir(int dir) {
        String parent = mediaVideo;
        switch (dir) {
            case PREVIEW_DIR:
                parent = mediaPrewVideo;
                break;
            case PLAYBACK_DIR:
                parent = mediaPlaybackVideo;
                break;
            case VR_DIR:
                parent = mediaVrVideo;
                break;
            case CLOUD_DIR:
                parent = mediaCloudVideo;
                break;
        }
        String path = FileHelper.getPhoneRootPath() + parent;
        FileHelper.createSDCardDir(path);
        return path;

    }

    /**
     * 录像H264文件
     *
     * @param dir
     * @return
     */
    public static String getMediaH264Dir(int dir) {
        String parent = mediaH264;
        switch (dir) {
            case PREVIEW_DIR:
                parent = mediaPrewH264;
                break;
            case PLAYBACK_DIR:
                parent = mediaPlaybackH264;
                break;
            case VR_DIR:
                parent = mediaVrH264;
                break;
        }
        String path = FileHelper.getPhoneRootPath() + parent;
        FileHelper.createSDCardDir(path);
        return path;
    }

    /**
     * 创建所有的目录
     */
    public static void initDir() {
        getCacheFileDir();
        getIconFileDir();
        getMediaVideoImg();
        getDownloadDir();
        for (int i = 1; i <= 3; i++) {
            getMediaH264Dir(i);
            getMediaImgDir(i);
            getMediaVideoDir(i);
        }
    }

    /**
     * 校对文件夹下的文件是否存在
     *
     * @param strPath
     * @return
     */
    public static boolean checkLinkedFiles(String strPath, String filename) {
        File dir = new File(strPath);
        File[] file = dir.listFiles();
        if (file != null) {
            for (File f : file) {
                if (!f.isDirectory()) {
                    if (f.getName().equals(filename)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<File> getLinkedFiles(String strPath) {
        List<File> videoFiles = new ArrayList<>();
        File dir = new File(strPath);
        File[] file = dir.listFiles();
        if (file == null) {
            return videoFiles;
        }
        for (File f : file) {
            if (!f.isDirectory()) {
                if (f.getName().endsWith(".mp4")) {
                    videoFiles.add(f);
                }
            }
        }
        return videoFiles;
    }

}
