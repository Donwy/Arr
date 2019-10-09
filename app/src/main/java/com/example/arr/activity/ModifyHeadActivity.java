package com.example.arr.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arr.MediaUtils;
import com.example.arr.R;
import com.example.sdk.BitvisionSdk;
import com.gzch.lsplat.work.file.FileManager;
import com.longse.lsapc.lsacore.mode.Result;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Donvy_y
 * @date 2019/9/30
 */
public class ModifyHeadActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ModifyHeadActivity";
    private TextView mSubmit;
    private TextView mResult;

    private File tempDir = null;
    private File tempFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_modifyhead);
        initView();
        tempDir = new File(FileManager.getIconFileDir());
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        tempFile = new File(tempDir, MediaUtils.getPhotoFileName());
    }

    private void initView() {
        mResult = findViewById(R.id.show_result);
        mSubmit = findViewById(R.id.submit);
        mSubmit.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                showResult();
                break;
        }
    }

    private File bitmapToFile(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;

    }
    private void showResult() {
        try{
            InputStream inputStream = getResources().getAssets().open("img_head.jpg");
            Bitmap image = BitmapFactory.decodeStream(inputStream);
            String imgPath = bitmapToFile(image).getPath();
            Log.d(TAG, "showResult: 123" + imgPath);
            BitvisionSdk.modifyHead(imgPath);
            inputStream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getResult(Result result) {
        mResult.setText("modifyHead>>> \n" + result.toString());
        Log.d(TAG, "getResult: "+ result.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
