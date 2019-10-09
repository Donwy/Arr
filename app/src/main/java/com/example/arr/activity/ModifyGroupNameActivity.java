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

public class ModifyGroupNameActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ModifyGroupNameActivity";
    private EditText mEditText;
    private EditText mEditText1;
    private TextView mSubmit;
    private TextView mResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_modifygroupname);
        initView();
    }

    private void initView() {
        mEditText = findViewById(R.id.groupName);
        mEditText1 = findViewById(R.id.groupId);

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
        String groupName = mEditText.getText().toString().trim();
        String groupId = mEditText1.getText().toString().trim();

//        BitvisionSdk.modifyGroupName("1111","51935");
        BitvisionSdk.modifyGroupName(groupName,groupId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getResult(Result result) {
        mResult.setText("modifyGroupName >>> \n" + result.toString());
        Log.d(TAG, "getResult: "+ result.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
