package com.gzch.lsplat.work;

import android.text.TextUtils;

import com.gzch.lsplat.work.action.AccountAction;
import com.gzch.lsplat.work.action.DBAction;
import com.gzch.lsplat.work.api.HttpAPI;
import com.gzch.lsplat.work.cmd.BitdogCmd;
import com.gzch.lsplat.work.file.FileManager;
import com.gzch.lsplat.work.mode.EqupInfo;
import com.gzch.lsplat.work.mode.FaceAttrInfo;
import com.gzch.lsplat.work.mode.ImageInfo;
import com.gzch.lsplat.work.mode.LoginAccountInfo;
import com.gzch.lsplat.work.mode.LoginRecord;
import com.gzch.lsplat.work.mode.UserInfo;
import com.gzch.lsplat.work.mode.event.DataKey;
import com.gzch.lsplat.work.mode.event.LoginAccountInfoEvent;
import com.gzch.lsplat.work.mode.event.LoginHistoryEvent;
import com.gzch.lsplat.work.mode.event.UpdateRAMDataEvent;
import com.gzch.lsplat.work.net.HttpRequest;
import com.gzch.lsplat.work.utils.MD5Util;
import com.longse.lsapc.lsacore.BitdogInterface;
import com.longse.lsapc.lsacore.interf.LSCoreInterface;
import com.longse.lsapc.lsacore.mode.HttpResult;
import com.longse.lsapc.lsacore.mode.Result;
import com.longse.lsapc.lsacore.sapi.http.HttpHelper;
import com.longse.lsapc.lsacore.sapi.sharedPreferences.StringCache;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lw on 2017/12/7.
 */

/**
 * 需要ApplicationContext时可直接请求BitdogInterface.getInstance().getApplicationContext()
 */
public class Work implements LSCoreInterface.Worker {

    private AccountAction accountAction = new AccountAction();

    private HttpRequest httpRequest = new HttpRequest();

    /**
     * 做一些初始化的工作
     * 创建时会自动调用
     */
    @Override
    public void init() {
    }

    /**
     * 该Worker可以处理的命令
     * 上层发送命令后会根据该方法判断是否处理
     *
     * @param cmd
     * @return
     */
    @Override
    public boolean isMyCmd(int cmd) {
        if (cmd == BitdogCmd.LOGIN_CMD || cmd == BitdogCmd.REQUEST_USER_INFO_CMD || cmd == BitdogCmd.SHOW_VERIFICATION_CMD) {
            return true;
        }
        if (cmd >= BitdogCmd.REGISTER_CMD && cmd <= BitdogCmd.SET_PASSWORD_CMD) {
            return true;
        }
        if (cmd == BitdogCmd.GET_LOGIN_HISTORY_CMD || cmd == BitdogCmd.UPLOAD_ICON_CMD) {
            return true;
        }
        if (cmd == BitdogCmd.COMMIT_SUGGEST_CMD) {
            return true;
        }
        if (cmd >= BitdogCmd.QUERY_MEDIA_FILE_CMD && cmd <= BitdogCmd.DELETE_LOGIN_RECORD) {
            return true;
        }
        if (cmd == BitdogCmd.UPDATE_FAVORITES_DEVICE_CMD || cmd == BitdogCmd.DELETE_FAVORITES_DEVICE_CMD) {
            return true;
        }
        if (cmd == BitdogCmd.LOGIN_RECORD_DETAIL_CMD || cmd == BitdogCmd.SWITCH_LOGIN_CMD || cmd == BitdogCmd.DELETE_ALL_PASSWORD) {
            return true;
        }
        if (cmd == BitdogCmd.DELETE_SN_PLAY_CACHE_CMD || cmd == BitdogCmd.REQUEST_AGREEMENT_CMD) {
            return true;
        }

        if (cmd == BitdogCmd.QUERY_CAPTURE_FILE_CMD || cmd == BitdogCmd.QUERY_COMPARED_FILE_CMD || cmd == BitdogCmd.DELETE_AI_FACE_ATTR_CMD) {
            return true;
        }
        return cmd == BitdogCmd.GET_MESSAGE_PUSH_PLATFORM_CMD || cmd == BitdogCmd.SET_MESSAGE_PUSH_PLATFORM_CMD;
    }

    /**
     * 处理命令
     *
     * @param cmd
     * @param jsonParams
     * @param handleType 同步或异步,同步调用结果直接返回,异步调用执行结果通过消息返回
     * @return
     */
    @Override
    public Result worked(int cmd, String jsonParams, int handleType) {
        switch (cmd) {
            case BitdogCmd.DELETE_SN_PLAY_CACHE_CMD:
                if (TextUtils.isEmpty(jsonParams)) {
                    if (handleType == BitdogInterface.ASYNC_HANDLE) {
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                    }
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                } else {
                    String devId = "";
                    String devLocale = "";
                    try {
                        JSONObject jsonObject = new JSONObject(jsonParams);
                        devId = jsonObject.optString("device_id");
                        devLocale = jsonObject.optString("locale");
                        if (TextUtils.isEmpty(devLocale)) {
                            DBAction.getInstance().deleteSNPlayerDevice(devId);
                        }
                        EqupInfo equpInfo = new EqupInfo();
                        equpInfo.setDirect(false);
                        equpInfo.setEqupId(devId);
                        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY, "");
                        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(devId)) {
                            EventBus.getDefault().post(UpdateRAMDataEvent.deviceDelete(devId));
                            clearDeviceRamCache();
                            DBAction.getInstance().deleteDevice(equpInfo, userId);
                            DBAction.getInstance().deletePlayCacheDevice(equpInfo, userId);
                            DBAction.getInstance().deleteFavoritesPlayerDevice(devId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (handleType == BitdogInterface.ASYNC_HANDLE) {
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS, cmd));
                    }
                    return Result.getFastResult(ErrorCode.SUCCESS, cmd);
                }
            case BitdogCmd.REQUEST_AGREEMENT_CMD:
                if (TextUtils.isEmpty(jsonParams)) {
                    if (handleType == BitdogInterface.ASYNC_HANDLE) {
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                    }
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                } else {
                    int agree_type = -1;
                    try {
                        JSONObject jsonObject = new JSONObject(jsonParams);
                        agree_type = jsonObject.optInt("agree_type", -1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        agree_type = -1;
                    }
                    if (agree_type == -1) {
                        if (handleType == BitdogInterface.ASYNC_HANDLE) {
                            EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                        }
                        return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                    }
                    String url = "";

                    String requestUrl = "";
                    if (agree_type == 0) {
                        requestUrl = WorkContext.CONTEXT_APP == WorkContext.BITDOG_APP ? HttpAPI.cloudServiceBD : HttpAPI.cloudService;
                    } else if (agree_type == 1) {
                        requestUrl = WorkContext.CONTEXT_APP == WorkContext.BITDOG_APP ? HttpAPI.privacyPolicyBD : HttpAPI.privacyPolicy;
                    } else {
                        requestUrl = WorkContext.CONTEXT_APP == WorkContext.BITDOG_APP ? HttpAPI.serviceAgreementBD : HttpAPI.serviceAgreement;
                    }

                    Map<String, Object> sendEmail = new HashMap<>();
                    Result result = httpRequest.commonRequest(sendEmail, jsonParams, requestUrl,
                            cmd, BitdogInterface.SYNC_HANDLE, false);
                    if (result != null) {
                        if (result.getExecResult() == ErrorCode.SUCCESS) {
                            if (result.getObj() != null && result.getObj() instanceof JSONObject) {
                                JSONObject jsonObject = ((JSONObject) result.getObj());
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data != null) {
                                    url = data.optString("url");
                                }
                            }
                        } else {
                            if (handleType == BitdogInterface.ASYNC_HANDLE) {
                                EventBus.getDefault().post(result);
                            }
                            return result;
                        }
                    }
                    if (!TextUtils.isEmpty(url)) {
                        if (handleType == BitdogInterface.ASYNC_HANDLE) {
                            EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS, cmd).setObj(url));
                        }
                        return Result.getFastResult(ErrorCode.SUCCESS, cmd).setObj(url);
                    }
                }
                break;
            case BitdogCmd.DELETE_ALL_PASSWORD:
                DBAction.getInstance().deleteLoginPassword();
                Result deleteResult = Result.getFastResult(ErrorCode.SUCCESS, cmd);
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(deleteResult);
                }
                return deleteResult;
            case BitdogCmd.LOGIN_CMD://账号登录
                StringCache.getInstance().addCache(WorkContext.USER_ID_KEY, "");
                if (handleType == BitdogInterface.SYNC_HANDLE) {//不支持同步调用
                    return Result.getFastResult(ErrorCode.NO_SUPPORT_THREAD, cmd);
                }
                accountAction.loginAccount(cmd, jsonParams);
                break;
            case BitdogCmd.REQUEST_USER_INFO_CMD:
                if (handleType == BitdogInterface.SYNC_HANDLE) {//不支持同步调用
                    return Result.getFastResult(ErrorCode.NO_SUPPORT_THREAD, cmd);
                }
                if (!TextUtils.isEmpty(WorkContext.ACCOUNT)) {
                    UserInfo userInfo = DBAction.getInstance().queryUserInfo(WorkContext.ACCOUNT);
                    if (userInfo != null) {
                        userInfo.setCmd(cmd);
                        userInfo.setExecResultCode(ErrorCode.SUCCESS);
                        EventBus.getDefault().post(userInfo);
                    }
                }
                accountAction.requestUserInfo(cmd);
                break;
            case BitdogCmd.SHOW_VERIFICATION_CMD:
                if (handleType == BitdogInterface.SYNC_HANDLE) {//不支持同步调用
                    return Result.getFastResult(ErrorCode.NO_SUPPORT_THREAD, cmd);
                }
                DataKey dataKey = accountAction.showVerification(cmd);
                EventBus.getDefault().post(dataKey);
                break;
            case BitdogCmd.REGISTER_CMD:
                return fixedRequest(cmd, jsonParams, handleType, new FixedRequestAction() {
                    @Override
                    public Result action(int cmd, JSONObject jsonObject, int handleType) {
                        if (jsonObject == null) {
                            return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                        }
                        String username = jsonObject.optString("username");
                        String password = jsonObject.optString("password");
                        String checkCode = jsonObject.optString("code");
                        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(checkCode)) {
                            return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                        }
                        return httpRequest.register(username, password, checkCode);
                    }
                });
            case BitdogCmd.SEND_EMAIL_CMD:
                Map<String, Object> sendEmail = new HashMap<>();
                sendEmail.put("username", "");
                return httpRequest.commonRequest(sendEmail, jsonParams, HttpAPI.sendEmailCode, cmd, handleType, true);
            case BitdogCmd.RESET_PASSWORD_CMD:
                Map<String, Object> resetPassword = new HashMap<>();
                resetPassword.put("user_name", "");
                resetPassword.put("password", "");
                resetPassword.put("code", "");
                resetPassword.put("session_id", "");
                try {
                    JSONObject json = new JSONObject(jsonParams);
                    if (json != null) {
                        String password = json.optString("password");
                        if (!TextUtils.isEmpty(password)) {
                            password = MD5Util.getMD5Encoding(password);
                            json.put("password", password);
                            json.put("session_id", StringCache.getInstance().queryCache("cookie_key", ""));
                            jsonParams = json.toString();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return httpRequest.commonRequest(resetPassword, jsonParams, HttpAPI.resetPassword, cmd, handleType, true);
            case BitdogCmd.SET_NICK_NAME_CMD:
                Map<String, Object> setNickName = new HashMap<>();
                setNickName.put("nick_name", "");
                return httpRequest.commonRequest(setNickName, jsonParams, HttpAPI.setNickName, cmd, handleType, true);
            case BitdogCmd.SET_REAL_NAME_CMD:
                Map<String, Object> setRealName = new HashMap<>();
                setRealName.put("real_name", "");
                return httpRequest.commonRequest(setRealName, jsonParams, HttpAPI.setRealName, cmd, handleType, true);
            case BitdogCmd.SET_PASSWORD_CMD:
                Map<String, Object> setPassword = new HashMap<>();
                setPassword.put("old_pwd", "");
                setPassword.put("new_pwd", "");
                try {
                    JSONObject json = new JSONObject(jsonParams);
                    if (json != null) {
                        String old_pwd = json.optString("old_pwd");
                        String new_pwd = json.optString("new_pwd");
                        if (!TextUtils.isEmpty(old_pwd) && !TextUtils.isEmpty(new_pwd)) {
                            old_pwd = MD5Util.getMD5Encoding(old_pwd);
                            new_pwd = MD5Util.getMD5Encoding(new_pwd);
                            json.put("old_pwd", old_pwd);
                            json.put("new_pwd", new_pwd);
                            jsonParams = json.toString();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return httpRequest.commonRequest(setPassword, jsonParams, HttpAPI.setPassword, cmd, handleType, true);
            case BitdogCmd.GET_LOGIN_HISTORY_CMD:
                int need_password = 0;
                try {
                    if (!TextUtils.isEmpty(jsonParams)) {
                        JSONObject jsonObject = new JSONObject(jsonParams);
                        if (jsonObject != null) {
                            need_password = jsonObject.optInt("need_password", 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    need_password = 0;
                }
                List<LoginRecord> records = DBAction.getInstance().getLoginHistory(need_password);
                LoginHistoryEvent event = new LoginHistoryEvent();
                event.setCmd(cmd);
                event.setExecResult(ErrorCode.EMPTY_RESULT);
                if (records != null) {
                    if (records.size() > 0) {
                        event.setExecResult(ErrorCode.SUCCESS);
                        event.setLoginRecordList(records);
                    }
                }
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(event);
                    return Result.getFastResult(event.getExecResult(), cmd);
                }
                return Result.getFastResult(event.getExecResult(), cmd).setObj(event);
            case BitdogCmd.UPLOAD_ICON_CMD:
                return fixedRequest(cmd, jsonParams, handleType, new FixedRequestAction() {
                    @Override
                    public Result action(int cmd, JSONObject jsonObject, int handleType) {
                        if (jsonObject == null) {
                            return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                        }
                        String file_path = jsonObject.optString("file_path");
                        if (TextUtils.isEmpty(file_path)) {
                            return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                        }
                        File file = new File(file_path);
                        if (file == null || !file.exists()) {
                            return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                        }
                        HttpResult<String> uploadResult = HttpHelper.getInstance().postFile(HttpAPI.uploadIcon, file, null, "Filedata");
                        if (uploadResult == null) {
                            return Result.getFastResult(ErrorCode.EMPTY_RESULT, cmd);
                        }
                        if (uploadResult.resultCode() != ErrorCode.SUCCESS) {
                            return Result.getFastResult(uploadResult.resultCode(), cmd).setObj(uploadResult.getResult());
                        } else {
                            return Result.getFastResult(ErrorCode.SUCCESS, cmd);
                        }
                    }
                });
            case BitdogCmd.COMMIT_SUGGEST_CMD:
                return httpRequest.postSuggest(cmd, jsonParams, handleType);
            case BitdogCmd.QUERY_MEDIA_FILE_CMD:
                List<ImageInfo> medias = DBAction.getInstance().queryDeviceMedia();
                Result mediaResult = new Result();
                mediaResult.setCmd(cmd);
                mediaResult.setExecResult(ErrorCode.EMPTY_RESULT);
                if (medias != null) {
                    mediaResult.setExecResult(ErrorCode.SUCCESS);
                    mediaResult.setObj(medias);
                }
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(mediaResult);
                    return null;
                }
                return mediaResult;
            case BitdogCmd.DELETE_MEDIA_FILE_CMD:
                if (TextUtils.isEmpty(jsonParams)) {
                    if (handleType == BitdogInterface.ASYNC_HANDLE) {
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                        return null;
                    }
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                }
                try {
                    if (jsonParams.startsWith("[")) {
                        JSONArray array = new JSONArray(jsonParams);
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                BitdogInterface.getInstance().exec(cmd, array.getString(i), BitdogInterface.SYNC_HANDLE);
                            }
                            if (handleType == BitdogInterface.ASYNC_HANDLE) {
                                EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS, cmd));
                                return null;
                            }
                            return Result.getFastResult(ErrorCode.SUCCESS, cmd);
                        } else {
                            if (handleType == BitdogInterface.ASYNC_HANDLE) {
                                EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                                return null;
                            }
                            return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                        }
                    }
                    JSONObject jsonObjectParams = new JSONObject(jsonParams);
                    if (jsonObjectParams != null) {
                        String path = jsonObjectParams.optString("path");
                        String fileName = jsonObjectParams.optString("file_name");
                        int playMode = jsonObjectParams.optInt("playMode");
                        if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(fileName)) {
                            File file = new File(path, fileName);
                            if (file != null && file.exists()) {
                                file.delete();
                            }
                            if (fileName.endsWith("264")) {
                                File img = new File(FileManager.getMediaVideoImg(), fileName.replace("h264", "jpeg"));
                                if (img != null) {
                                    img.delete();
                                }
                                String mp4 = FileManager.getMediaVideoDir(FileManager.PREVIEW_DIR);
                                switch (playMode) {
                                    case EqupInfo.PRE_VIEW_MODE:
                                    case EqupInfo.PRE_VIEW_MODE | EqupInfo.LOCALE_MODE:
                                    case EqupInfo.DIRECT_MODE:
                                        mp4 = FileManager.getMediaVideoDir(FileManager.PREVIEW_DIR);
                                        break;
                                    case EqupInfo.PLAY_BACK_MODE:
                                    case EqupInfo.PLAY_BACK_MODE | EqupInfo.LOCALE_MODE:
                                        mp4 = FileManager.getMediaVideoDir(FileManager.PLAYBACK_DIR);
                                        break;
                                    case EqupInfo.VR_MODE:
                                    case EqupInfo.VR_MODE | EqupInfo.LOCALE_MODE:
                                        mp4 = FileManager.getMediaVideoDir(FileManager.VR_DIR);
                                        break;
                                }
                                File mp4File = new File(mp4, fileName.replace("h264", "mp4"));
                                if (mp4File != null) {
                                    mp4File.delete();
                                }
                            }
                            DBAction.getInstance().deleteDeviceMedia(path, fileName);
                            if (handleType == BitdogInterface.ASYNC_HANDLE) {
                                EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS, cmd));
                                return null;
                            }
                            return Result.getFastResult(ErrorCode.SUCCESS, cmd);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                    return null;
                }
                return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
            case BitdogCmd.GET_LOCALE_IP:
                Result result = httpRequest.requestLocalIP();
                if (result != null) {
                    result.setCmd(cmd);
                } else {
                    result = Result.getFastResult(ErrorCode.RESPONSE_ERROR, cmd);
                }
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(result);
                    return null;
                }
                return result;
            case BitdogCmd.DELETE_LOGIN_RECORD:
                if (TextUtils.isEmpty(jsonParams)) {
                    if (handleType == BitdogInterface.ASYNC_HANDLE) {
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                        return null;
                    }
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                }
                try {
                    JSONObject jsonObject = new JSONObject(jsonParams);
                    String account = jsonObject.optString("account");
                    if (!TextUtils.isEmpty(account)) {
                        LoginRecord loginRecord = new LoginRecord();
                        loginRecord.setLoginType(LoginRecord.ACCOUNT);
                        loginRecord.setAccount(account);
                        DBAction.getInstance().deleteLoginRecord(loginRecord);
                        if (handleType == BitdogInterface.ASYNC_HANDLE) {
                            EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS, cmd));
                            return null;
                        }
                        return Result.getFastResult(ErrorCode.SUCCESS, cmd);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                    return null;
                }
                return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
            case BitdogCmd.UPDATE_FAVORITES_DEVICE_CMD:
            case BitdogCmd.DELETE_FAVORITES_DEVICE_CMD:
                if (TextUtils.isEmpty(jsonParams)) {
                    if (handleType == BitdogInterface.ASYNC_HANDLE) {
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                        return null;
                    }
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                }
                try {
                    JSONObject jsonObject = new JSONObject(jsonParams);
                    String device_id = jsonObject.optString("device_id");
                    int channel_id = jsonObject.optInt("channel_id", -1);
                    String name = jsonObject.optString("name", "");
                    if (TextUtils.isEmpty(device_id) || channel_id == -1) {
                        if (handleType == BitdogInterface.ASYNC_HANDLE) {
                            EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                            return null;
                        }
                        return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                    }
                    if (cmd == BitdogCmd.UPDATE_FAVORITES_DEVICE_CMD) {
                        if (TextUtils.isEmpty(name)) {
                            if (handleType == BitdogInterface.ASYNC_HANDLE) {
                                EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                                return null;
                            }
                            return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                        } else {
                            DBAction.getInstance().updateFavoritesPlayerDevice(device_id, channel_id, name);
                        }
                    } else {
                        DBAction.getInstance().deleteFavoritesPlayerDevice(device_id, channel_id);
                    }
                    clearDeviceRamCache();
                    if (handleType == BitdogInterface.ASYNC_HANDLE) {
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS, cmd));
                        return null;
                    }
                    return Result.getFastResult(ErrorCode.SUCCESS, cmd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                } else {
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                }
                break;
            case BitdogCmd.LOGIN_RECORD_DETAIL_CMD:
                List<LoginRecord> recordList = DBAction.getInstance().getLoginHistory(2);
                if (recordList == null || recordList.size() == 0) {
                    LoginAccountInfoEvent loginAccountInfoEvent = new LoginAccountInfoEvent();
                    loginAccountInfoEvent.setCmd(cmd);
                    loginAccountInfoEvent.setResultCode(ErrorCode.SUCCESS);
                    loginAccountInfoEvent.setLoginAccountInfos(new ArrayList<LoginAccountInfo>());
                    if (handleType == BitdogInterface.ASYNC_HANDLE) {
                        EventBus.getDefault().post(loginAccountInfoEvent);
                        return null;
                    } else {
                        return Result.getFastResult(loginAccountInfoEvent.getResultCode(), cmd).setObj(loginAccountInfoEvent);
                    }
                }
                LoginAccountInfoEvent loginAccountInfoEvent = httpRequest.requestAccountInfo(cmd, recordList);
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(loginAccountInfoEvent);
                } else {
                    return Result.getFastResult(loginAccountInfoEvent.getResultCode(), cmd).setObj(loginAccountInfoEvent);
                }
                break;
            case BitdogCmd.SWITCH_LOGIN_CMD:
                if (handleType == BitdogInterface.SYNC_HANDLE) {//不支持同步调用
                    return Result.getFastResult(ErrorCode.NO_SUPPORT_THREAD, cmd);
                }
                BitdogInterface.getInstance().exec(BitdogCmd.LOGOUT_CMD, "", BitdogInterface.SYNC_HANDLE);
                StringCache.getInstance().addCache(WorkContext.USER_ID_KEY, "");
                accountAction.loginAccount(cmd, jsonParams, false);
                break;
            case BitdogCmd.GET_MESSAGE_PUSH_PLATFORM_CMD:
                return httpRequest.commonRequest(null, jsonParams, HttpAPI.getMessagePush, cmd, handleType, false);
            case BitdogCmd.SET_MESSAGE_PUSH_PLATFORM_CMD:
                Map<String, Object> delete = new HashMap<>();
                delete.put("switch", "");
                delete.put("platform", "");
                return httpRequest.commonRequest(delete, jsonParams, HttpAPI.setMessagePush, cmd, handleType, true);

            case BitdogCmd.QUERY_CAPTURE_FILE_CMD:
                List<FaceAttrInfo> captureFaceAttrs = DBAction.getInstance().queryFaceAttrInfo(FaceAttrInfo.FACE_CAPTURE_ATTR);
                Result captureResult = new Result();
                captureResult.setCmd(cmd);
                captureResult.setExecResult(ErrorCode.EMPTY_RESULT);
                if (captureFaceAttrs != null) {
                    captureResult.setExecResult(ErrorCode.SUCCESS);
                    captureResult.setObj(captureFaceAttrs);
                }
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(captureResult);
                    return null;
                }
                return captureResult;

            case BitdogCmd.QUERY_COMPARED_FILE_CMD:
                List<FaceAttrInfo> compaterFaceAttrs = DBAction.getInstance().queryFaceAttrInfo(FaceAttrInfo.FACE_COMPARE_RESULT);
                Result compaterResult = new Result();
                compaterResult.setCmd(cmd);
                compaterResult.setExecResult(ErrorCode.EMPTY_RESULT);
                if (compaterFaceAttrs != null) {
                    compaterResult.setExecResult(ErrorCode.SUCCESS);
                    compaterResult.setObj(compaterFaceAttrs);
                }
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(compaterResult);
                    return null;
                }
                return compaterResult;

            case BitdogCmd.DELETE_AI_FACE_ATTR_CMD:
                if (TextUtils.isEmpty(jsonParams)) {
                    if (handleType == BitdogInterface.ASYNC_HANDLE) {
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                        return null;
                    }
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
                }
                try {
                    if (jsonParams.startsWith("[")) {
                        JSONArray array = new JSONArray(jsonParams);
                        for (int i = 0; i < array.length(); i++) {
                            BitdogInterface.getInstance().exec(cmd, array.getString(i), BitdogInterface.SYNC_HANDLE);
                        }
                        if (handleType == BitdogInterface.ASYNC_HANDLE) {
                            EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS, cmd));
                            return null;
                        }
                        return Result.getFastResult(ErrorCode.SUCCESS, cmd);
                    }
                    JSONObject jsonObjectParams = new JSONObject(jsonParams);
                    String capturePic = jsonObjectParams.optString("capture_pic_name");
                    String compareLibPic = jsonObjectParams.optString("compare_lib_pic_name");
                    String compareCapPic = jsonObjectParams.optString("compare_cap_pic_name");
                    if (!TextUtils.isEmpty(capturePic)) {
                        File file = new File(FileManager.getMediaImgDir(FileManager.AI_FACE_DIR) + capturePic);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    if (!TextUtils.isEmpty(compareLibPic)) {
                        File file = new File(FileManager.getMediaImgDir(FileManager.AI_FACE_DIR) + compareLibPic);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    if (!TextUtils.isEmpty(compareCapPic)) {
                        File file = new File(FileManager.getMediaImgDir(FileManager.AI_FACE_DIR) + compareCapPic);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    FaceAttrInfo info = new FaceAttrInfo();
                    info.setCapturePicName(capturePic);
                    info.setCompareLibPicName(compareLibPic);
                    info.setCompareCapPicName(compareCapPic);
                    DBAction.getInstance().deleteFaceAttrInfo(info);
                    if (handleType == BitdogInterface.ASYNC_HANDLE) {
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS, cmd));
                        return null;
                    }
                    return Result.getFastResult(ErrorCode.SUCCESS, cmd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                    return null;
                }
                return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
        }
        return null;
    }

    private void clearDeviceRamCache() {
        int[] cmds = new int[]{BitdogCmd.REQUEST_DEVICE_MANAGER_DEVICE_LIST_CMD,
                BitdogCmd.REQUEST_VR_PLAY_DEVICE_LIST_CMD,
                BitdogCmd.REQUEST_NO_VR_PLAY_DEVICE_LIST_CMD};
        for (int cmd : cmds) {
            BitdogInterface.getInstance().delData(WorkContext.GROUP_DEVICE_DATA_KEY + cmd);
        }
    }

    /**
     * 简化参数检查，统一请求流程
     *
     * @param cmd
     * @param jsonParams
     * @param handleType
     * @param fixedRequestAction
     * @return
     */
    private Result fixedRequest(int cmd, String jsonParams, int handleType, FixedRequestAction fixedRequestAction) {
        if (TextUtils.isEmpty(jsonParams)) {
            if (handleType == BitdogInterface.ASYNC_HANDLE) {
                EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                return null;
            }
            return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonParams);
            if (jsonObject == null) {
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
                    return null;
                }
                return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
            }
            Result result = null;
            if (fixedRequestAction != null) {
                result = fixedRequestAction.action(cmd, jsonObject, handleType);
            }
            if (result == null) {
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(Result.getFastResult(ErrorCode.UNKNOWN_ERROR, cmd));
                    return null;
                }
                return Result.getFastResult(ErrorCode.UNKNOWN_ERROR, cmd);
            } else {
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(result.setCmd(cmd));
                    return null;
                }
                return result.setCmd(cmd);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (handleType == BitdogInterface.ASYNC_HANDLE) {
            EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd));
            return null;
        }
        return Result.getFastResult(ErrorCode.PARAMS_ERROR, cmd);
    }

    interface FixedRequestAction {

        /**
         * 主要请求调用方法
         *
         * @param cmd
         * @param jsonObject
         * @param handleType
         * @return
         */
        Result action(int cmd, JSONObject jsonObject, int handleType);
    }

}
