package com.example.arr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//import android.support.v4.app.ActivityCompat;
//import com.gzch.lsplat.bitdog.base.Constant;
//import com.yalantis.ucrop.UCrop;
//import com.yalantis.ucrop.UCropActivity;

/**
 * Created by dk on 2018/1/13.
 */

public class MediaUtils {
    public static final int GET_IMAGE_BY_CAMERA = 5001;
    public static final int GET_IMAGE_FROM_PHONE = 5002;
    public static final int CROP_IMAGE = 5003;
    public static Uri imageUriFromCamera;
    public static Uri cropImageUri;
    private static Uri destinationUri;
//    private static UCrop.Options options;

    // 使用系统当前日期加以调整作为照片的名称
    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpeg";
    }

    public static void openLocalImage(final Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, GET_IMAGE_FROM_PHONE);
        Constant.isPhotoOrCamera = true;
    }

    public static void openLocalImage(final Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        fragment.startActivityForResult(intent, GET_IMAGE_FROM_PHONE);
        Constant.isPhotoOrCamera = true;
    }

    public static void takePhoto(File tempFile, Fragment fragment) {
        Context context=fragment.getContext();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = getImgUri(context, tempFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        fragment.startActivityForResult(intent, GET_IMAGE_BY_CAMERA);
        Constant.isPhotoOrCamera = true;
    }
    public static void takePhoto(File tempFile, Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = getImgUri(activity, tempFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, GET_IMAGE_BY_CAMERA);
        Constant.isPhotoOrCamera = true;
    }

    public static Uri getImgUri(Context context, File tempFile) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                uri = FileProvider.getUriForFile(context, "com.gzch.lsplat.bitdog.fileProvider", tempFile);
            } catch (Exception e) {
                e.printStackTrace();
                uri = null;
            }
        } else {
            uri = Uri.fromFile(tempFile);
        }
        return uri;
    }


    public static void cropImage(Fragment fragment, Uri srcUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(srcUri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 13);
        intent.putExtra("aspectY", 11);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 130);
        intent.putExtra("outputY", 110);
        intent.putExtra("return-data", true);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        //裁剪权限
        List<ResolveInfo> resInfoList = fragment.getContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            fragment.getContext().grantUriPermission(packageName, srcUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        fragment.startActivityForResult(intent, CROP_IMAGE);
    }
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor == null){
                return contentUri.toString();
            }
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //播放视频
    public static void playVideo(Context mContext, String videoPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileProvider", new File(videoPath));
            intent.setDataAndType(contentUri, "video/*");
        } else {
            Uri uri = Uri.fromFile(new File(videoPath));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "video/*");
        }
        mContext.startActivity(intent);
        Constant.isPhotoOrCamera = true;
    }

    public static void cropImage(Fragment fragment, Uri srcUri, Uri saveUri) {
        Intent intent = getCropIntent(srcUri,saveUri);
        fragment.startActivityForResult(intent, CROP_IMAGE);
    }
    public static void cropImage(Activity activity, Uri srcUri, Uri saveUri) {
        Intent intent = getCropIntent(srcUri,saveUri);
        activity.startActivityForResult(intent, CROP_IMAGE);
    }
    /**
     * https://blog.csdn.net/flying_FFL/article/details/54670451
     * @param srcUri
     * @param saveUri
     * @return
     */
    private static Intent getCropIntent(Uri srcUri, Uri saveUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(srcUri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 13);
        intent.putExtra("aspectY", 11);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 130);
        intent.putExtra("outputY", 110);
//        intent.putExtra("return-data", true);

        //uritempFile为Uri类变量，实例化uritempFile，转化为uri方式解决问题
        intent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        //裁剪权限
//        List<ResolveInfo> resInfoList = fragment.getContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//        for (ResolveInfo resolveInfo : resInfoList) {
//            String packageName = resolveInfo.activityInfo.packageName;
//            fragment.getContext().grantUriPermission(packageName, srcUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        }
        return intent;
    }
//    public static void initUCrop(Uri uri, File tempDir, Fragment fragment) {
//        //Uri destinationUri = RxPhotoTool.createImagePathUri(this);
//        setUCrop(tempDir);
//        UCrop.of(uri, destinationUri)
//                .withAspectRatio(1, 1)
//                .withMaxResultSize(1000, 1000)
//                .withOptions(options)
//                .start(getContext(),fragment);
//        Constant.isPhotoOrCamera = true;
//    }
//
//    public static void initUCrop(Uri uri, File tempDir, Activity activity) {
//        setUCrop(tempDir);
//        UCrop.of(uri, destinationUri)
//                .withAspectRatio(1, 1)
//                .withMaxResultSize(1000, 1000)
//                .withOptions(options)
//                .start(activity);
//        Constant.isPhotoOrCamera = true;
//    }
//
//    private static void setUCrop(File tempDir) {
//        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
//        long time = System.currentTimeMillis();
//        String imageName = timeFormatter.format(new Date(time));
//
//        destinationUri = Uri.fromFile(new File(tempDir, imageName + ".jpeg"));
//
//        options = new UCrop.Options();
//        //设置裁剪图片可操作的手势
//        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
//        //设置隐藏底部容器，默认显示
//        //options.setHideBottomControls(true);
//        //设置toolbar颜色
//        options.setToolbarColor(ActivityCompat.getColor(getContext(), R.color.title_red));
//        //设置状态栏颜色
//        options.setStatusBarColor(ActivityCompat.getColor(getContext(), R.color.title_red));
//
//        //开始设置
//        //设置最大缩放比例
//        options.setMaxScaleMultiplier(5);
//        //设置图片在切换比例时的动画
//        options.setImageToCropBoundsAnimDuration(666);
//        //设置裁剪窗口是否为椭圆
//        //options.setOvalDimmedLayer(true);
//        //设置是否展示矩形裁剪框
//        // options.setShowCropFrame(false);
//        //设置裁剪框横竖线的宽度
//        //options.setCropGridStrokeWidth(20);
//        //设置裁剪框横竖线的颜色
//        //options.setCropGridColor(Color.GREEN);
//        //设置竖线的数量
//        //options.setCropGridColumnCount(2);
//        //设置横线的数量
//        //options.setCropGridRowCount(1);
//
//    }





}
