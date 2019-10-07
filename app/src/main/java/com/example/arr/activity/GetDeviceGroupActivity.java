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

/**
 * @author Donvy_y
 * @date 2019/9/30
 */
public class GetDeviceGroupActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEditText;
    private EditText mEditText1;
    private TextView mSubmit;
    private TextView mResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_getdevicegroup);
        initView();
    }

    private void initView() {
        mEditText = findViewById(R.id.equiId);
        mEditText1 = findViewById(R.id.cateId);
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
        String euqiId= mEditText.getText().toString().trim();
        String cateId = mEditText1.getText().toString().trim();
        BitvisionSdk.getDeviceGroup(euqiId,cateId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getResult(Result result) {
        mResult.setText("getDeviceGroup >>> \n" + result.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
