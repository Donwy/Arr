package com.example.sdk;

import android.util.Log;
import com.gzch.lsplat.work.WebDeviceDetailWork;
import com.gzch.lsplat.work.WebDeviceWork;
import com.gzch.lsplat.work.Work;
import com.gzch.lsplat.work.api.HttpAPI;
import com.gzch.lsplat.work.cmd.BitdogCmd;
import com.gzch.lsplat.work.mode.EqupInfo;
import com.gzch.lsplat.work.mode.UserInfo;
import com.longse.lsapc.lsacore.BitdogInterface;
import com.longse.lsapc.lsacore.mode.HttpResult;
import com.longse.lsapc.lsacore.mode.Result;
import com.longse.lsapc.lsacore.sapi.http.HttpHelper;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;
import static android.content.ContentValues.TAG;



/**
 * @author Donvy_y
 * @date 2019/9/20
 */
public class BitvisionSdk {


    public static void init(){
        BitdogInterface.getInstance().registeredWorker(Work.class);
        BitdogInterface.getInstance().registeredWorker(WebDeviceWork.class);
        BitdogInterface.getInstance().registeredWorker(WebDeviceDetailWork.class);
    }

    /**
     * Get Area Server Address (获取区域服务器地址)
     */
    public static void getAreaServerAddress() {
        Result r = BitdogInterface.getInstance().exec(BitdogCmd.GET_LOCALE_IP,"",BitdogInterface.SYNC_HANDLE);
    }


    /**
     * Register users (用户注册)
     * @param account  Register Account (注册账号)
     * @param psw      Register Password (注册密码)
     * @param code     Register Verification Code (注册验证码)
     */
    public static void registerUsers(String account, String psw, String code) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.REGISTER_CMD,
                String.format("{\"username\":\"%s\",\"password\":\"%s\",\"code\":\"%s\"}", account, psw, code),
                BitdogInterface.ASYNC_HANDLE);
        Log.d(TAG, "registerUsers: " + result);
    }

    /**
     * Retransmit Verification Code (重发验证码)
     * @param str
     */
    public static void retransmitVerificationCode(String str) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.SEND_REGISTER_EMAIL_CMD,
                String.format("{\"user_name\":\"%s\"}", str), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * User Login (用户登录)
     * @param account 
     * @param psw 
     */
    public static void userLogin(String account, String psw, String keepPsw) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.LOGIN_CMD,
                String.format("{\"account\":\"%s\",\"password\":\"%s\",\"local_ip\":\"%s\",\"save\":%d}", account, psw, "", Integer.valueOf(keepPsw)),
                BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Forget Password (找回密码)
     * @param code
     * @param email
     * @param psw 
     */
    public static void forgetPassword(String code, String email, String psw) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.RESET_PASSWORD_CMD,
                String.format("{\"user_name\":\"%s\",\"password\":\"%s\",\"code\":\"%s\"}", email, psw, code), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Check User Account (校验找回密码的账号)
     * @param email
     */
    public static void checkUserAccount(String email) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.SEND_EMAIL_CMD,
                String.format("{\"username\":\"%s\"}", email), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Get User Information (获取用户信息)
     * @param userInfo 
     */
    public static void getUserInformation(UserInfo userInfo) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.JPUSH_AUTO_START_CMD, userInfo.getUserId(), BitdogInterface.SYNC_HANDLE);
    }

    /**
     * Modify User Nickname (修改昵称)
     * @param detail
     */
    public static void modifyUserNickname(String detail) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.SET_NICK_NAME_CMD,
                String.format("{\"nick_name\":\"%s\"}", detail), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Modify Head (修改头像)
     * @param imgPath
     */
    public static void modifyHead(String imgPath) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.UPLOAD_ICON_CMD,
                String.format("{\"file_path\":\"%s\"}", imgPath), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Get Device Group (获取设备分组)
     * @param euqiId
     * @param cateId
     */
    public static void getDeviceGroup(String euqiId, String cateId) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.CHANGE_DEVICE_GROUP_CMD,
                String.format("{\"device_id\":\"%s\",\"cate_id\":\"%s\"}", euqiId, cateId), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Delete Group (删除分组)
     * @param cateId
     */
    public static void deleteGroup(String cateId) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.DELETE_GROUP_CMD,
                String.format("{\"cate_id\":\"%s\"}", cateId), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Modify Group Name (修改分组名)
     * @param name
     * @param groupId
     */
    public static void modifyGroupName(String name, String groupId) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.UPDATE_GROUP_NAME_CMD,
                String.format("{\"cate_name\":\"%s\",\"cate_id\":\"%s\"}", name, groupId),
                BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Add Device Group (新增设备分组)
     * @param name
     */
    public static void addDeviceGroup(String name) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.CREATE_GROUP_CMD,
                String.format("{\"cat_name\":\"%s\"}", name),
                BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Get The Bound Device List (获取绑定设备列表)
     */
    public static void getTheBoundDeviceList() {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.REQUEST_DEVICE_LIST_CMD, "", BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Delete Device (删除设备)
     * @param info
     */
    public static void deleteDevice(EqupInfo info) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.UNBIND_DEVICE_CMD,
                String.format("{\"device_id\":\"%s\"}", info.getEqupId()), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Modify Device Name (修改设备名)
     * @param info
     * @param name
     */
    public static void modifyDeviceName(EqupInfo info, String name) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.SET_DEVICE_NAME_CMD,
                String.format("{\"device_id\":\"%s\",\"device_name\":\"%s\"}", info.getEqupId(), name), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Bind Device For SN (绑定设备)
     * @param device_id
     * @param local_user
     * @param local_pwd
     * @param verify
     */
    public static void bindDeviceForSN(String device_id, String local_user,String local_pwd,String verify) {
        //{"device_id":"","local_user":"","local_pwd":"","verify":""}
        JSONObject params = new JSONObject();
        try {
            params.put("device_id", device_id);
            params.put("local_user", local_user);
            params.put("local_pwd", local_pwd);
            params.put("verify", verify);
        } catch (Exception e){
            e.printStackTrace();
        }
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.BIND_DEVICE_CMD,
                params.toString(), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Get Device Info With SN (通过序列号获取设备信息)
     * @param serial
     * @param devName
     * @param account
     * @param password
     */
    public static void getDeviceInfoWithSN(String serial, String devName, String account, String password) {
        JSONObject params = new JSONObject();
        try {
            params.put("device_id", serial);
            params.put("dev_name", devName);
            params.put("account", account);
            params.put("password", password);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.GET_DEVICE_INFO_BY_SERIAL_NUMBER_CMD,
                params.toString(), BitdogInterface.ASYNC_HANDLE);
    }
}
