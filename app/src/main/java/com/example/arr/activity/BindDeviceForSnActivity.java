package com.example.arr.activity;

import android.os.Bundle;
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

    private EditText mEditText;
    private EditText mEditText1;
    private EditText mEditText2;
    private EditText mEditText3;
    private TextView mSubmit;
    private TextView mResult;
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
        String deviceId = mEditText.getText().toString().trim();
        String localUser = mEditText1.getText().toString().trim();
        String pwd = mEditText2.getText().toString().trim();
        String verify = mEditText3.getText().toString().trim();
//        BitvisionSdk.bindDeviceForSN("1000000000460","2851133868@qq.com","longse","ABCDEF");
        BitvisionSdk.bindDeviceForSN(deviceId,localUser,pwd,verify);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getResult(Result result) {
        mResult.setText("bindDeviceForSN >>> \n" + result.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
