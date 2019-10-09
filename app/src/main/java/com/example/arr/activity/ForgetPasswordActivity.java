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

/**
 * @author Donvy_y
 * @date 2019/9/30
 */
public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ForgetPasswordActivity";
    private EditText mAccount;
    private EditText mPsw;
    private EditText mEditText2;
    private TextView mSubmit;
    private TextView mResult;
    private TextView mCaptcha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_forgetpassword);
        initView();
    }

    private void initView() {
        mAccount = findViewById(R.id.account);
        mPsw = findViewById(R.id.input_psw);
        mEditText2 = findViewById(R.id.input_code);
        mResult = findViewById(R.id.show_result);
        mSubmit = findViewById(R.id.submit);
        mCaptcha = findViewById(R.id.captcha);
        mSubmit.setOnClickListener(this);
        mCaptcha.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                showResult();
                break;
            case R.id.captcha:
                getCaptcha();
                break;
        }

    }

    private void getCaptcha() {
        String account = mAccount.getText().toString().trim();
        BitvisionSdk.retransmitVerificationCode(account);
    }

    private void showResult() {
        String email = mAccount.getText().toString().trim();
        String password = mPsw.getText().toString().trim();
        String code = mEditText2.getText().toString().trim();
        BitvisionSdk.forgetPassword(email,password, code);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getResult(Result result) {
        mResult.setText("checkUserAccount >>> \n" + result.toString());
        Log.d(TAG, "getResult: "+ result.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
