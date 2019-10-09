package com.example.arr.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arr.R;
import com.example.sdk.BitvisionSdk;
import com.longse.lsapc.lsacore.mode.Result;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BindDeviceForSnActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "BindDeviceForSnActivity";
    private EditText mEditText;
    private EditText mEditText1;
    private EditText mEditText2;
    private EditText mEditText3;
    private TextView mSubmit;
    private TextView mResult;
    private TextView mCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_binddeviceforsn);
        initView();
    }

    private void initView() {
        mEditText = findViewById(R.id.deviceId);
        mEditText1 = findViewById(R.id.account);
        mEditText2 = findViewById(R.id.pwd);
        mEditText3 = findViewById(R.id.verify);
        mResult = findViewById(R.id.show_result);
        mSubmit = findViewById(R.id.submit);
        mCheck = findViewById(R.id.check);
        mSubmit.setOnClickListener(this);
        mCheck.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                showResult();
                break;
            case R.id.check:
                checkTheSerial();
                break;
        }

    }

    private void checkTheSerial() {
        String serial = mEditText.getText().toString().trim();
        BitvisionSdk.checkTheSerial(serial);
    }


    private void showResult() {
        String device_id = mEditText.getText().toString().trim();
        String local_user = mEditText1.getText().toString().trim();
        String local_psw = mEditText2.getText().toString().trim();
        String verify = mEditText3.getText().toString().trim();
        BitvisionSdk.bindDeviceForSN(device_id, local_user, local_psw, verify);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getResult(Result result) {
        mResult.setText("bindDeviceForSN >>> \n" + result.toString());
        Log.d(TAG, "getResult: "+ result.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
