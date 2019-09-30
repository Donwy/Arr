package com.example.arr;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sdk.BitvisionSdk;

/**
 * @author Donvy_y
 * @date 2019/9/30
 */
public class RegisteredUsers extends AppCompatActivity implements View.OnClickListener {

    private EditText mAccount;
    private EditText mInputPsw;
    private EditText mInputCode;
    private TextView mCaptcha;
    private TextView mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeredusers);
        initView();
    }

    private void initView() {
        mAccount = findViewById(R.id.account);
        mInputPsw = findViewById(R.id.input_psw);
        mInputCode = findViewById(R.id.input_code);
        mCaptcha = findViewById(R.id.captcha);
        mRegister = findViewById(R.id.register);

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
        String psw = mInputPsw.getText().toString().trim();
        String code = mInputCode.getText().toString().trim();
        BitvisionSdk.registeredUsers(account, psw, code);
    }
}

