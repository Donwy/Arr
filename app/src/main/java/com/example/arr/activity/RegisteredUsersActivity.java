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


/**
 * @author Donvy_y
 * @date 2019/9/30
 */
public class RegisteredUsersActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisteredUsersActivity";
    private EditText mAccount;
    private EditText mInputPsw;
    private EditText mInputCode;
    private TextView mCaptcha;
    private TextView mRegister;
    private TextView mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_registeredusers);
        initView();
    }

    private void initView() {
        mAccount = findViewById(R.id.account);
        mInputPsw = findViewById(R.id.input_psw);
        mInputCode = findViewById(R.id.input_code);
        mCaptcha = findViewById(R.id.captcha);
        mRegister = findViewById(R.id.register);
        mResult = findViewById(R.id.show_result);

        mAccount.setOnClickListener(this);
        mInputPsw.setOnClickListener(this);
        mInputCode.setOnClickListener(this);
        mCaptcha.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.captcha:
                getCaptcha();
                break;
            case R.id.register:
                submit();
                break;
        }

    }

    private void getCaptcha() {
        String account = mAccount.getText().toString().trim();
        BitvisionSdk.registeredCode(account);
    }

    private void submit() {
        String account = mAccount.getText().toString().trim();
        String password = mInputPsw.getText().toString().trim();
        String code = mInputCode.getText().toString().trim();
        BitvisionSdk.registeredUsers(account, password, code);
    }

    @Subscribe
    public void getResult(Result result) {

        mResult.setText("RegisteredUser >>> \n" + result.toString());
        Log.d(TAG, "getResult: "+ result.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

