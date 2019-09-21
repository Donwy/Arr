package com.longse.lsapc.lsacore.sapi.file;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.longse.lsapc.lsacore.BitdogInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lw on 2017/12/4.
 */

public class FileHelper {

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * 获取存储卡根目录
     * @return
     */
    public static String getPhoneRootPath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 创建文件夹
     */
    public static void createSDCardDir(String path){
        if (TextUtils.isEmpty(path) || !isExternalStorageWritable())return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Context ctx = BitdogInterface.getInstance().getApplicationContext();
            if (ctx != null){
                if (ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    return;
                }
            } else {
                return;
            }
        }
        File f = new File(path);
        if (f != null && f.isDirectory()){
            f.mkdirs();
        }
    }

    /**
     * 计算某个目录或文件的大小
     * @param f 需要计算大小的文件
     * @return
     */
    public static long computeDirSize(File f){
        long result = 0;
        if(f != null && f.exists()){
            if (f.isDirectory()){
                File[] files = f.listFiles();
                for(File fs : files){
                    if(fs.isFile()){
                        result += fs.length();
                    }else{
                        result += computeDirSize(fs);
                    }
                }
            } else if (f.isFile()){
                return f.length();
            }
        }
        return result;
    }

    /**
     * 删除一个文件或目录
     * @param f : 需要删除的文件或目录
     */
    public static void deleteDir(File f){
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (File file : files) {
                deleteDir(file);
            }
        } else {
            f.delete();
        }
    }

    /**
     * 一个文件复制到一个目录
     * @param f ：需要复制的文件
     * @param toFile ： 目的地目录
     */
    public static void copyFile(File f, File toFile) {
        if (f == null || toFile == null || !f.exists()) return;
        if (f.isDirectory() || toFile.isFile()) return;
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(f);
            if (!toFile.exists()) {
                toFile.mkdirs();
            }
            File outFile = new File(toFile, f.getName());
            if (outFile.exists()) {
                outFile.delete();
            }
            outFile.createNewFile();
            out = new FileOutputStream(outFile);
            byte[] buf = new byte[2048];
            int read;
            while ((read = in.read(buf)) > 0) {
                out.write(buf, 0, read);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
