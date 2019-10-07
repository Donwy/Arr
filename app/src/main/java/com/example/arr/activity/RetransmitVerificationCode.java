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

public class RetransmitVerificationCode extends AppCompatActivity implements View.OnClickListener{

    private EditText mEditText;
    private TextView mSubmit;
    private TextView mSubmit1;
    private TextView mResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_retransmitverificationcode);
        initView();
    }

    private void initView() {
        mEditText = findViewById(R.id.account);
        mResult = findViewById(R.id.show_result);
        mSubmit = findViewById(R.id.submit);
        mSubmit1 = findViewById(R.id.submit1);
        mSubmit.setOnClickListener(this);
        mSubmit1.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                showResult();
                break;
            case R.id.submit1:
                showResult1();
                break;
        }

    }

    private void showResult1() {
        String account = mEditText.getText().toString().trim();
        BitvisionSdk.retransmitVerificationCode(account);
    }

    private void showResult() {
        String account = mEditText.getText().toString().trim();
        BitvisionSdk.registeredCode(account);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getResult(Result result) {
        mResult.setText("retransmitVerificationCode >>> \n" + result.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
