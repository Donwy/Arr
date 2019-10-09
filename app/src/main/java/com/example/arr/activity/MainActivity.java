package com.example.arr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.arr.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private RelativeLayout getServerIpRel;
    private RelativeLayout registeredUsersRel;
    private RelativeLayout retransmitVerificationCodeRel;
    private RelativeLayout userLoginRel;
    private RelativeLayout forgetPasswordRel;
    private RelativeLayout checkUserAccountRel;
    private RelativeLayout userInfoRel;
    private RelativeLayout modifyUserNicknameRel;
    private RelativeLayout modifyHeadRel;
    private RelativeLayout deviceGroupRel;
    private RelativeLayout deleteGroupRel;
    private RelativeLayout modifyGroupNameRel;
    private RelativeLayout addDeviceGroupRel;
    private RelativeLayout theBoundDeviceListRel;
    private RelativeLayout deleteDeviceRel;
    private RelativeLayout modifyDeviceNameRel;
    private RelativeLayout bindDeviceForSNRel;
    private RelativeLayout DeviceInfoWithSNRel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


    }

    public void initView() {
        getServerIpRel = findViewById(R.id.getServerIp_rel);
        registeredUsersRel = findViewById(R.id.registeredUsers_rel);
//        retransmitVerificationCodeRel = findViewById(R.id.retransmitVerificationCode_rel);
        userLoginRel = findViewById(R.id.userLogin_rel);
        forgetPasswordRel = findViewById(R.id.forgetPassword_rel);
        checkUserAccountRel = findViewById(R.id.checkUserAccount_rel);

        userInfoRel = findViewById(R.id.userInfo_rel);
        modifyUserNicknameRel = findViewById(R.id.modifyUserNickname_rel);
        modifyHeadRel = findViewById(R.id.modifyHead_rel);
        deviceGroupRel = findViewById(R.id.deviceGroup_rel);
        deleteGroupRel = findViewById(R.id.deleteGroup_rel);
        modifyGroupNameRel = findViewById(R.id.modifyGroupName_rel);

        addDeviceGroupRel = findViewById(R.id.addDeviceGroup_rel);
        theBoundDeviceListRel = findViewById(R.id.theBoundDeviceList_rel);
        deleteDeviceRel = findViewById(R.id.deleteDevice_rel);
        modifyDeviceNameRel = findViewById(R.id.modifyDeviceName_rel);
        bindDeviceForSNRel = findViewById(R.id.bindDeviceForSN_rel);
        DeviceInfoWithSNRel = findViewById(R.id.DeviceInfoWithSN_rel);


        getServerIpRel.setOnClickListener(this);
        registeredUsersRel.setOnClickListener(this);
//        retransmitVerificationCodeRel.setOnClickListener(this);
        userLoginRel.setOnClickListener(this);
        forgetPasswordRel.setOnClickListener(this);
        checkUserAccountRel.setOnClickListener(this);

        userInfoRel.setOnClickListener(this);
        modifyUserNicknameRel.setOnClickListener(this);
        modifyHeadRel.setOnClickListener(this);
        deviceGroupRel.setOnClickListener(this);
        deleteGroupRel.setOnClickListener(this);
        modifyGroupNameRel.setOnClickListener(this);

        addDeviceGroupRel.setOnClickListener(this);
        theBoundDeviceListRel.setOnClickListener(this);
        deleteDeviceRel.setOnClickListener(this);
        modifyDeviceNameRel.setOnClickListener(this);
        bindDeviceForSNRel.setOnClickListener(this);
        DeviceInfoWithSNRel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.getServerIp_rel:
                getServerIp();
                break;
            case R.id.registeredUsers_rel:
                getRegisteredUsers();
                break;
//            case R.id.retransmitVerificationCode_rel:
//                getRetransmitVerificationCode();
//                break;
            case R.id.userLogin_rel:
                getUserLogin();
                break;
            case R.id.forgetPassword_rel:
                getForgetPassword();
                break;
            case R.id.checkUserAccount_rel:
                getCheckUserAccount();
                break;
            case R.id.userInfo_rel:
                getUserInfo();
                break;
            case R.id.modifyUserNickname_rel:
                getModifyUserNickname();
                break;
            case R.id.modifyHead_rel:
                getModifyHead();
                break;
            case R.id.deviceGroup_rel:
                getDeviceGroup();
                break;
            case R.id.deleteGroup_rel:
                getDeleteGroup();
                break;
            case R.id.modifyGroupName_rel:
                getModifyGroupName();
                break;
            case R.id.addDeviceGroup_rel:
                getAddDeviceGroup();
                break;
            case R.id.theBoundDeviceList_rel:
                getBoundDeviceList();
                break;
            case R.id.deleteDevice_rel:
                getDeleteDevice();
                break;
            case R.id.modifyDeviceName_rel:
                getModifyDeviceName();
                break;
            case R.id.bindDeviceForSN_rel:
                getBindDeviceForSN();
                break;
            case R.id.DeviceInfoWithSN_rel:
                getDeviceInfoWithSN();
                break;
            default:
                break;
        }
    }

    //Get Device Info With SN (通过序列号获取设备信息--1)
    private void getDeviceInfoWithSN() {
        Intent intent = new Intent(MainActivity.this, GetDeviceInfoWithSnActivity.class);
        startActivity(intent);
    }

    //Bind Device For SN (绑定设备--1)
    private void getBindDeviceForSN() {
        Intent intent = new Intent(MainActivity.this, BindDeviceForSnActivity.class);
        startActivity(intent);
    }


    //Modify Device Name (修改设备名--1)
    private void getModifyDeviceName() {

        Intent intent = new Intent(MainActivity.this, ModifyDeviceNameActivity.class);
        startActivity(intent);
    }

    //Delete Device (删除设备--1)
    private void getDeleteDevice() {

        Intent intent = new Intent(MainActivity.this, DeleteDeviceActivity.class);
        startActivity(intent);
    }

    //Get The Bound Device List (获取绑定设备列表--1)
    private void getBoundDeviceList() {

        Intent intent = new Intent(MainActivity.this, GetTheBoundDeviceListActivity.class);
        startActivity(intent);
    }

    //Add Device Group (新增设备分组--1)
    private void getAddDeviceGroup() {

        Intent intent = new Intent(MainActivity.this, AddDeviceGroupActivity.class);
        startActivity(intent);
    }

    //Modify Group Name (修改分组名字--1)
    private void getModifyGroupName() {

        Intent intent = new Intent(MainActivity.this, ModifyGroupNameActivity.class);
        startActivity(intent);
    }

    //Delete Group (删除分组--1)
    private void getDeleteGroup() {

        Intent intent = new Intent(MainActivity.this, DeleteGroupActivity.class);
        startActivity(intent);
    }

    //Get Device Group (获取设备分组--1)
    private void getDeviceGroup() {
        Intent intent = new Intent(MainActivity.this, GetDeviceGroupActivity.class);
        startActivity(intent);
    }

    //Modify Head (修改头像--0)
    private void getModifyHead() {
        Intent intent = new Intent(MainActivity.this, ModifyHeadActivity.class);
        startActivity(intent);
    }


    //Modify User Nickname (修改昵称--1)
    private void getModifyUserNickname() {

        Intent intent = new Intent(MainActivity.this, ModifyUserNicknameActivity.class);
        startActivity(intent);
    }

//    Get User Information (获取用户信息--1)
    private void getUserInfo() {
        Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
        startActivity(intent);
    }

    //Check User Account (校验找回密码的账号--1)
    private void getCheckUserAccount() {
        Intent intent = new Intent(MainActivity.this, CheckUserAccountActivity.class);
        startActivity(intent);
    }

    //Forget Password (找回密码--1)
    private void getForgetPassword() {
        Intent intent = new Intent(MainActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);
    }

    //User Login (用户登录--1)
    private void getUserLogin() {
        Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
        startActivity(intent);
    }

    //Retransmit Verification Code (重发验证码--1)
//    private void getRetransmitVerificationCode() {
//        Intent intent = new Intent(MainActivity.this, RetransmitVerificationCode.class);
//        startActivity(intent);
//    }

    //Registered users (用户注册)
    private void getRegisteredUsers() {

        Intent intent = new Intent(MainActivity.this, RegisteredUsersActivity.class);
        startActivity(intent);
    }

    //Get Area Server Address (获取区域服务器地址--1)
    private void getServerIp() {
        Intent intent = new Intent(MainActivity.this, GetServerIpActivity.class);
        startActivity(intent);
    }
}
