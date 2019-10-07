package com.example.arr.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arr.R;
import com.example.sdk.BitvisionSdk;
import com.longse.lsapc.lsacore.mode.Result;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author Donvy_y
 * @date 2019/9/30
 */
public class ModifyHeadActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mSubmit;
    private TextView mResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_modifyhead);
        initView();
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

    private void showResult() {
        try{
            InputStream inputStream = getResources().getAssets().open("img_head.jpg");
            Bitmap image = BitmapFactory.decodeStream(inputStream);
            String imgPath =  bitmapToString(image);
            inputStream.close();

            BitvisionSdk.modifyHead(imgPath);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String bitmapToString(Bitmap bitmap) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
        byte[]bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getResult(Result result) {
        mResult.setText("modifyHead>>> \n" + result.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
