package com.example.arr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arr.R;
import com.example.sdk.BitvisionSdk;
import com.gzch.lsplat.work.mode.event.SerialNumberDeviceInfoEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class GetDeviceInfoWithSnActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEditText;
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
//        BitvisionSdk.getDeviceInfoWithSN("1000000000460");
        BitvisionSdk.getDeviceInfoWithSN(serial);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getResult(SerialNumberDeviceInfoEvent result) {
        mResult.setText("getDeviceInfoWithSN >>> \n" +  result.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
