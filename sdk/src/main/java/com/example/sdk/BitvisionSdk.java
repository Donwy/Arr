package com.example.sdk;

import android.content.Context;

import com.gzch.lsplat.work.WebDeviceDetailWork;
import com.gzch.lsplat.work.WebDeviceWork;
import com.gzch.lsplat.work.Work;
import com.gzch.lsplat.work.cmd.BitdogCmd;
import com.longse.lsapc.lsacore.BitdogInterface;
import com.longse.lsapc.lsacore.mode.Result;

import org.json.JSONObject;


/**
 * @author Donvy_y
 * @date 2019/9/20
 */
public class BitvisionSdk {

    public static void init(Context ctx){
        BitdogInterface.getInstance().registeredWorker(Work.class);
        BitdogInterface.getInstance().registeredWorker(WebDeviceWork.class);
        BitdogInterface.getInstance().registeredWorker(WebDeviceDetailWork.class);
        BitdogInterface.getInstance().init(ctx.getApplicationContext(),null);
    }

    /**
     * Get Area Server Address (获取区域服务器地址)
     * @param isAsync  ASYNC 异步：1, SYNC 同步：2
     */
    public static void getAreaServerAddress(int isAsync) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.GET_LOCALE_IP,"",isAsync);
    }

    /**
     * Registered code (注册验证码)
     * @param account Registered Account (注册账号)
     */
    public static void registeredCode(String account) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.SEND_REGISTER_EMAIL_CMD,
                String.format("{\"user_name\":\"%s\"}", account), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Registered users (用户注册)
     * @param account  Registered Account (注册账号)
     * @param password      Registered Password (注册密码)
     * @param code     Registered Verification Code (注册验证码)
     */
    public static void registeredUsers(String account, String password, String code) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.REGISTER_CMD,
                String.format("{\"username\":\"%s\",\"password\":\"%s\",\"code\":\"%s\"}", account, password, code),
                BitdogInterface.ASYNC_HANDLE);

    }

    /**
     * Retransmit Verification Code (重发验证码)
     * @param email  Email  (邮箱)
     */
    public static void retransmitVerificationCode(String email) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.SEND_EMAIL_CMD,
                String.format("{\"username\":\"%s\"}", email), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * User Login (用户登录)
     * @param account  User Account (用户账户)
     * @param password  User Password (用户密码)
     */
    public static void userLogin(String account, String password) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.LOGIN_CMD,
                String.format("{\"account\":\"%s\",\"password\":\"%s\",\"local_ip\":\"%s\",\"save\":%d}", account, password, "", Integer.valueOf(0)),
                BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Forget Password (找回密码)
     * @param email  Email  (邮箱)
     * @param password  Reset Password (重设密码)
     * @param code  Verification Code (验证码)
     */
    public static void forgetPassword(String email, String password, String code) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.RESET_PASSWORD_CMD,
                String.format("{\"user_name\":\"%s\",\"password\":\"%s\",\"code\":\"%s\"}", email, password, code), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Check User Account (校验找回密码的账号)
     * @param email Email (邮箱)
     */
    public static void checkUserAccount(String email) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.SEND_EMAIL_CMD,
                String.format("{\"username\":\"%s\"}", email), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Get User Information (获取用户信息)
     */
    public static void getUserInformation() {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.REQUEST_USER_INFO_CMD, "", BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Modify User Nickname (修改昵称)
     * @param nickname Reset User Nickname (重设昵称)
     */
    public static void modifyUserNickname(String nickname) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.SET_NICK_NAME_CMD,
                String.format("{\"nick_name\":\"%s\"}", nickname), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Modify Head (修改头像)
     * @param imgPath   Path Of The Resetting Image (重设图片的路径)
     */
    public static void modifyHead(String imgPath) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.UPLOAD_ICON_CMD,
                String.format("{\"file_path\":\"%s\"}", imgPath), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Get Device Group (获取设备分组)
     */
    public static void getDeviceGroup() {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.REQUEST_DEVICE_GROUP_CMD,
                "", BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Delete Group (删除分组)
     * @param  groupId The Id Of Selected Group (被选中组的Id)
     */
    public static void deleteGroup(String groupId) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.DELETE_GROUP_CMD,
                String.format("{\"cate_id\":\"%s\"}", groupId), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Modify Group Name (修改分组名)
     * @param groupName  The New Name Of Selected Group （被选中组的新名字）
     * @param groupId The Id Of Selected Group (被选中组的Id)
     */
    public static void modifyGroupName(String groupName, String groupId) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.UPDATE_GROUP_NAME_CMD,
                String.format("{\"cate_name\":\"%s\",\"cate_id\":\"%s\"}", groupName, groupId),
                BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Add Device Group (新增设备分组)
     * @param groupName The Name Of New Group (新增组名）
     */
    public static void addDeviceGroup(String groupName) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.CREATE_GROUP_CMD,
                String.format("{\"cat_name\":\"%s\"}", groupName), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Get The Bound Device List (获取绑定设备列表)
     */
    public static void getTheBoundDeviceList() {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.REQUEST_DEVICE_LIST_CMD, "", BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Delete Device (删除设备)
     * @param deviceId Device Id(设备Id)
     */
    public static void deleteDevice(String deviceId) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.UNBIND_DEVICE_CMD,
                String.format("{\"device_id\":\"%s\"}", deviceId), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Modify Device Name (修改设备名)
     * @param deviceId  Device Id (设备Id）
     * @param deviceName  New Device Name (新的设备名)
     */
    public static void modifyDeviceName(String deviceId, String deviceName) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.SET_DEVICE_NAME_CMD,
                String.format("{\"device_id\":\"%s\",\"device_name\":\"%s\"}", deviceId, deviceName), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Bind Device For SN (绑定设备)
     * @param device_id   The Serial Number Of Device(设备的序列号)
     * @param local_user User Account (用户账号)
     * @param local_psw User Password (用户密码)
     * @param verify The Verify Code Of Device (设备的验证码)
     */
    public static void bindDeviceForSN(String device_id, String local_user, String local_psw, String verify) {
        JSONObject params = new JSONObject();
        try {
            params.put("device_id", device_id);
            params.put("local_user", local_user);
            params.put("local_psw", local_psw);
            params.put("verify", verify);
        } catch (Exception e){
            e.printStackTrace();
        }
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.BIND_DEVICE_CMD,
                params.toString(), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Get Device Info With SN (通过序列号获取设备信息)
     * @param serial  The Serial Number Of Device(设备的序列号)
     */
    public static void getDeviceInfoWithSN(String serial) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.GET_DEVICE_INFO_BY_SERIAL_NUMBER_CMD,
                String.format("{\"device_id\":\"%s\"}", serial), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * Determining whether the device requires authentication code(判断设备是否需要验证码)
     * @param serial
     */
    public static void checkTheSerial(String serial) {
        Result result = BitdogInterface.getInstance().exec(BitdogCmd.CHECK_DEVICE_CODE_CMD,
                String.format("{\"device_id\":\"%s\"}", serial), BitdogInterface.ASYNC_HANDLE);
    }
}
