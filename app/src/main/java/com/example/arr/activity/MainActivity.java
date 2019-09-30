package com.example.arr.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arr.R;
import com.example.sdk.BitvisionSdk;
import com.gzch.lsplat.work.mode.UserInfo;
import com.gzch.lsplat.work.mode.event.DeviceList;
import com.gzch.lsplat.work.mode.event.GroupList;
import com.gzch.lsplat.work.mode.event.LoginHistoryEvent;
import com.gzch.lsplat.work.mode.event.SerialNumberDeviceInfoEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


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

    private String info = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        initView();


    }

    public void initView() {
        getServerIpRel = findViewById(R.id.getServerIp_rel);
        registeredUsersRel = findViewById(R.id.registeredUsers_rel);
        retransmitVerificationCodeRel = findViewById(R.id.retransmitVerificationCode_rel);
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
        retransmitVerificationCodeRel.setOnClickListener(this);
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
            case R.id.retransmitVerificationCode_rel:
                getRetransmitVerificationCode();
                break;
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
        try{
            info = "通过序列号获取设备信息";
            BitvisionSdk.getDeviceInfoWithSN("1000000000460");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Bind Device For SN (绑定设备--1)
    private void getBindDeviceForSN() {
        try{
            info = "绑定设备";
            BitvisionSdk.bindDeviceForSN("1000000000460","2851133868@qq.com","longse","ABCDEF");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Modify Device Name (修改设备名--1)
    private void getModifyDeviceName() {
        try{
            info = "修改设备名";
            BitvisionSdk.modifyDeviceName("1000000000460","460");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Delete Device (删除设备--1)
    private void getDeleteDevice() {
        try{
            info = "删除设备";
            BitvisionSdk.deleteDevice("1000000000460");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Get The Bound Device List (获取绑定设备列表--1)
    private void getBoundDeviceList() {
        try{
            info = "获取绑定设备列表";
            BitvisionSdk.getTheBoundDeviceList();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Add Device Group (新增设备分组--1)
    private void getAddDeviceGroup() {
        try{
            info = "新增设备分组";
            BitvisionSdk.addDeviceGroup("123456");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Modify Group Name (修改分组名字--1)
    private void getModifyGroupName() {
        try{
            info = "修改分组名";
            BitvisionSdk.modifyGroupName("1111","51935");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Delete Group (删除分组--1)
    private void getDeleteGroup() {
        try{
            info = "删除分组";
            BitvisionSdk.deleteGroup("51935");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Get Device Group (获取设备分组--1)
    private void getDeviceGroup() {
        try{
            info = "获取设备分组";
            BitvisionSdk.getDeviceGroup();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Modify Head (修改头像--0)
    private void getModifyHead() {
        info = "修改头像";
        try{
            InputStream inputStream = getResources().getAssets().open("img_head.jpg");
            Bitmap image = BitmapFactory.decodeStream(inputStream);
            String imgPath =  bitmapToString(image);
            inputStream.close();

            BitvisionSdk.modifyHead(imgPath);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String bitmapToString(Bitmap bitmap) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
        byte[]bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    //Modify User Nickname (修改昵称--1)
    private void getModifyUserNickname() {
        try{
            info = "修改昵称";
            BitvisionSdk.modifyUserNickname("longse");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Get User Information (获取用户信息--1)
    private void getUserInfo() {
        try{
            info = "获取用户信息";
            BitvisionSdk.getUserInformation();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Check User Account (校验找回密码的账号--1)
    private void getCheckUserAccount() {
        try{
            info = "校验找回密码的账号";
            BitvisionSdk.checkUserAccount("572419921@qq.com");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Forget Password (找回密码--1)
    private void getForgetPassword() {
        try{
            info = "找回密码";
            BitvisionSdk.forgetPassword("7942", "572419921@qq.com", "b12345678");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //User Login (用户登录--1)
    private void getUserLogin() {
        try{
            info = "用户登录";
            BitvisionSdk.userLogin("2851133868@qq.com","longse2019");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Retransmit Verification Code (重发验证码--1)
    private void getRetransmitVerificationCode() {
//        try{
//            info = "重发验证码";
//            BitvisionSdk.retransmitVerificationCode("d572419921@163.com");
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    //Registered users (用户注册)
    private void getRegisteredUsers() {
        info = "Registered users";
        Intent intent = new Intent(MainActivity.this, RegisteredUsersActivity.class);
        startActivity(intent);
    }

    //Get Area Server Address (获取区域服务器地址--1)
    private void getServerIp() {
        try{
            info = "获取区域服务器地址";
            BitvisionSdk.getAreaServerAddress(1);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

  

//    @Subscribe
//    public void getResult(Result result) {
//        Log.d(TAG, "getResult: "+ info+" >>>> " + result);
//
//    }

    @Subscribe
    public void getResult(SerialNumberDeviceInfoEvent result) {
        Log.d(TAG, "getResult: "+ info+" >>>> " + result);
    }

    @Subscribe
    public void getResult(LoginHistoryEvent result) {
        Log.d(TAG, "getResult: "+ info+" >>>> " + result);
    }

    @Subscribe
    public void getResult(UserInfo result) {
        Log.d(TAG, "getResult: "+ info+" >>>> " + result);
    }

    @Subscribe
    public void getResult(GroupList result) {
        Log.d(TAG, "getResult: "+ info+" >>>> " + result);
    }

    @Subscribe
    public void getResult(DeviceList result) {
        Log.d(TAG, "getResult: "+ info+" >>>> " + result);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
