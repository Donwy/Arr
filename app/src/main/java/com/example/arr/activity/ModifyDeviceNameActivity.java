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

public class ModifyDeviceNameActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEditText;
    private EditText mEditText1;

    private TextView mSubmit;
    private TextView mResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_modifydevicename);
        initView();
    }

    private void initView() {
        mEditText = findViewById(R.id.equipId);
        mEditText1 = findViewById(R.id.name);
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
        String equipId= mEditText.getText().toString().trim();
        String name = mEditText1.getText().toString().trim();
//        BitvisionSdk.modifyDeviceName("1000000000460","460");
        BitvisionSdk.modifyDeviceName(equipId,name);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getResult(Result result) {
        mResult.setText("modifyDeviceName >>> \n" + result.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
