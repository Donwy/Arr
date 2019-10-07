package com.example.arr.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.arr.R;
import com.example.sdk.BitvisionSdk;
import com.gzch.lsplat.work.mode.event.SerialNumberDeviceInfoEvent;
import com.longse.lsapc.lsacore.mode.Result;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class GetDeviceInfoWithSnActivity extends AppCompatActivity implements View.OnClickListener{

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
        setContentView(R.layout.activity_getdeviceinfowithsn);
        initView();
    }

    private void initView() {
        mEditText = findViewById(R.id.serial);
        mEditText1 = findViewById(R.id.devName);
        mEditText2 = findViewById(R.id.account);
        mEditText3 = findViewById(R.id.password);
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
        String serial = mEditText.getText().toString().trim();
        String devName = mEditText1.getText().toString().trim();
        String account = mEditText2.getText().toString().trim();
        String password = mEditText3.getText().toString().trim();
//        BitvisionSdk.getDeviceInfoWithSN("1000000000460","2851133868@qq.com","longse","ABCDEF");
        BitvisionSdk.getDeviceInfoWithSN(serial,devName,account,password);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getResult(SerialNumberDeviceInfoEvent result) {
        mResult.setText("getDeviceInfoWithSN >>> \n" + result.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
