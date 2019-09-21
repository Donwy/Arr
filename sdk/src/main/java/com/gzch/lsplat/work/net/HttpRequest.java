package com.gzch.lsplat.work.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.gzch.lsplat.work.ErrorCode;
import com.gzch.lsplat.work.WorkContext;
import com.gzch.lsplat.work.action.DBAction;
import com.gzch.lsplat.work.api.HttpAPI;
import com.gzch.lsplat.work.cmd.BitdogCmd;
import com.gzch.lsplat.work.file.FileManager;
import com.gzch.lsplat.work.mode.EqupInfo;
import com.gzch.lsplat.work.mode.Group;
import com.gzch.lsplat.work.mode.LoginAccountInfo;
import com.gzch.lsplat.work.mode.LoginRecord;
import com.gzch.lsplat.work.mode.event.DataKey;
import com.gzch.lsplat.work.mode.event.GroupList;
import com.gzch.lsplat.work.mode.event.LoginAccountInfoEvent;
import com.gzch.lsplat.work.mode.event.UpdateRAMDataEvent;
import com.gzch.lsplat.work.utils.MD5Util;
import com.longse.lsapc.lsacore.BitdogInterface;
import com.longse.lsapc.lsacore.interf.ThreadWrapper;
import com.longse.lsapc.lsacore.mode.HttpResult;
import com.longse.lsapc.lsacore.mode.Result;
import com.longse.lsapc.lsacore.sapi.http.HttpHelper;
import com.longse.lsapc.lsacore.sapi.log.KLog;
import com.longse.lsapc.lsacore.sapi.sharedPreferences.StringCache;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lw on 2017/12/20.
 */

public class HttpRequest {

    private static final String SHOW_VERIFICATION_CODE_KEY = "show_verification_code_key";

    private String buildParams(Map<String,?> paramKV){
        String params = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("requestNo", System.currentTimeMillis() + "");
            jsonObject.put("liveTime","30");
            if (paramKV == null || paramKV.size() == 0){
                jsonObject.put("param","");
            } else {
                JSONObject jsonParams = new JSONObject();
                for (Map.Entry<String,?> m : paramKV.entrySet()){
                    if (m.getKey() != null && m.getValue() != null){
                        if (paramKV.size() == 1){
                            jsonObject.put("param",m.getValue());
                            return jsonObject.toString();
                        }
                        jsonParams.put(m.getKey(),m.getValue());
                    }
                }
                jsonObject.put("param",jsonParams);
            }
            params = jsonObject.toString();
        } catch (JSONException e){
            e.printStackTrace();
        }
        return params;
    }

    /**
     * 请求服务器IP
     * 加州：52.52.7.160  加州：52.52.7.160    //52.28.117.1 法兰克福    //52.220.173.92  新加坡   //183.62.249.124内网测试
     */
    private static final String ServerIP_KEY = "http_req_server_ip";
    private static final String ServerPort_KEY = "http_req_server_port";
    private static final String ServerTcpPort_KEY = "http_req_server_tcp_port";
    public Result requestLocalIP(){
        boolean needThread = false;
        String serverIP = StringCache.getInstance().queryCache(ServerIP_KEY,"");
        String serverPort = StringCache.getInstance().queryCache(ServerPort_KEY,"0");
        String serverTcpPort = StringCache.getInstance().queryCache(ServerTcpPort_KEY,"0");
        JSONObject data = new JSONObject();
        if (WorkContext.CONTEXT_APP == WorkContext.BITDOG_APP){
            try {
                int port = Integer.valueOf(serverPort);
                int tcpPort = Integer.valueOf(serverTcpPort);
                if (!TextUtils.isEmpty(serverIP) && port != 0 && tcpPort != 0){
                    needThread = true;
                    data.put("user_server_ip",serverIP);
                    data.put("user_server_port",port);
                    data.put("tcp_user_server_port",tcpPort);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            if (!TextUtils.isEmpty(serverIP)){
                try {
                    int port = Integer.valueOf(serverPort);
                    int tcpPort = Integer.valueOf(serverTcpPort);
                    needThread = true;
                    data.put("user_server_ip",serverIP);
                    data.put("user_server_port",port);
                    data.put("tcp_user_server_port",tcpPort);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        KLog.getInstance().d("requestLocalIP request local ip needThread = " + needThread).print();
        if (needThread){
            BitdogInterface.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    getServerIP();
                }
            }, ThreadWrapper.THREAD_IO);
            return Result.getFastResult(ErrorCode.SUCCESS,0).setObj(data);
        } else {
            return getServerIP();
        }
    }

    private Result getServerIP(){
        HttpResult<String> response = HttpHelper.getInstance().postString(HttpAPI.getLocalIP,buildParams(null),null);
        if (response == null)return Result.getFastResult(ErrorCode.RESPONSE_ERROR,0);
        int code = response.resultCode();
        if (code == ErrorCode.SUCCESS){
            String str = response.getResult();
            if (!TextUtils.isEmpty(str)){
                try {
                    KLog.getInstance().d(HttpAPI.getLocalIP + " request local ip result.").pjson(str).print();
                    JSONObject json = new JSONObject(str);
                    if (json != null){
                        JSONObject data = json.optJSONObject("data");
                        if (data != null){
                            String ip = data.optString("user_server_ip");
                            int port = data.optInt("user_server_port");
                            int tcpPort = data.optInt("tcp_user_server_port");
                            StringCache.getInstance().addCache(ServerIP_KEY,ip);
                            StringCache.getInstance().addCache(ServerPort_KEY,port + "");
                            StringCache.getInstance().addCache(ServerTcpPort_KEY,tcpPort + "");
                            if (!TextUtils.isEmpty(ip)){
                                return Result.getFastResult(ErrorCode.SUCCESS,0).setObj(data);
                            }
                        }
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            return Result.getFastResult(ErrorCode.RESPONSE_ERROR,0);
        } else {
            return Result.getFastResult(code,0);
        }
    }

    /**
     * 通用网络请求方法，只判断请求是否成功
     * 可从返回的Result中解析详细的返回数据
     * @param api
     * @param params
     * @return
     */
    public Result fastAction(String api, Map<String,?> params){
        if (TextUtils.isEmpty(api)){
            return Result.getFastResult(ErrorCode.PARAMS_ERROR,0);
        }
        HttpResult<String> response = HttpHelper.getInstance().postString(api,buildParams(params),null);
        if (response == null)return Result.getFastResult(ErrorCode.RESPONSE_ERROR,0);
        int code = response.resultCode();
        if (code == ErrorCode.SUCCESS){
            String str = response.getResult();
            if (!TextUtils.isEmpty(str)){
                try {
                    KLog.getInstance().d(api + " params = %s ," + " http request result = %s" ,params,str).tag("HttpRequest").print();
                    JSONObject json = new JSONObject(str);
                    if (json != null){
                        int c = json.optInt("code");
                        if (c == 0){
                            return Result.getFastResult(ErrorCode.SUCCESS,0).setObj(json);
                        } else if (c == 15) {
                            // COKIE 过期 清空
                            HttpHelper.getInstance().clearCookie();
                            return Result.getFastResult(c,0).setObj("msg");
                        } else {
                            return Result.getFastResult(c,0).setObj(json.optString("msg"));
                        }
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            return Result.getFastResult(ErrorCode.RESPONSE_ERROR,0);
        } else {
            return Result.getFastResult(code,0);
        }
    }

    /**
     * 账号登录
     * @param account
     * @param pwd
     * @return
     */
    public Result login(String account, String pwd){
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd)){
            return Result.getFastResult(ErrorCode.PARAMS_ERROR,0);
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("password", MD5Util.getMD5Encoding(pwd));
        params.put("user_name", account);
        return fastAction(HttpAPI.login,params);
    }

    /**
     * 微信登录
     * @param token
     */
    public Result wechatLogin(String token){
        if (TextUtils.isEmpty(token)){
            return Result.getFastResult(ErrorCode.PARAMS_ERROR,0);
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("refresh_token", token);
        return fastAction(HttpAPI.wechatLogin,params);
    }

    /**
     * facebook登录
     * @param token
     */
    public Result facebookLogin(String token){
        if (TextUtils.isEmpty(token)){
            return Result.getFastResult(ErrorCode.PARAMS_ERROR,0);
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        return fastAction(HttpAPI.facebookLogin,params);
    }

    /**
     * line登录
     * @param token
     */
    public Result lineLogin(String token){
        if (TextUtils.isEmpty(token)){
            return Result.getFastResult(ErrorCode.PARAMS_ERROR,0);
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        return fastAction(HttpAPI.lineLogin,params);
    }

    /**
     * Twitter 登录
     * @param accessToken
     * @param tokenSecret
     * @return
     */
    public Result twitterLogin(String accessToken, String tokenSecret){
        if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(tokenSecret)){
            return Result.getFastResult(ErrorCode.PARAMS_ERROR,0);
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("access_token", accessToken);
        params.put("token_secret", tokenSecret);
        return fastAction(HttpAPI.twitterLogin,params);
    }

    public Result googleLogin(String clientId){
        if (TextUtils.isEmpty(clientId)){
            return Result.getFastResult(ErrorCode.PARAMS_ERROR,0);
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("id_token", clientId);
        params.put("google", "");
        return fastAction(HttpAPI.googleLogin,params);
    }

    /**
     * 请求用户信息
     * @return
     */
    public Result requestUserInfo(){
        return fastAction(HttpAPI.requestUserInfo,null);
    }

    /**
     * 显示验证码
     * 验证码Bitmap通过KEY值去取
     * @param cmd
     * @return
     */
    public DataKey showVerification(int cmd){
        HttpResult<Bitmap> resultBitmap = HttpHelper.getInstance().getBitmap(HttpAPI.showVerificationCode,buildParams(null),null);
        DataKey dataKey = new DataKey();
        dataKey.setCmd(cmd);
        dataKey.setResultCode(ErrorCode.RESPONSE_ERROR);
        if (resultBitmap != null){
            if (resultBitmap.resultCode() == ErrorCode.SUCCESS){
                Bitmap bitmap = resultBitmap.getResult();
                if (bitmap == null){
                    KLog.getInstance().d("get Verification code bitmap null work.").print();
                }
                WeakReference<Bitmap> bitmapWeakReference = new WeakReference<Bitmap>(bitmap);
                BitdogInterface.getInstance().putData(SHOW_VERIFICATION_CODE_KEY,bitmapWeakReference);
                dataKey.setKEY(SHOW_VERIFICATION_CODE_KEY);
                dataKey.setResultCode(ErrorCode.SUCCESS);
                KLog.getInstance().d("get Verification code success.").print();
            } else {
                dataKey.setResultCode(resultBitmap.resultCode());
            }
        }
        return dataKey;
    }

    /**
     * 注册账号
     * @param user 用户名
     * @param password 密码
     * @param code 验证码
     * @return
     */
    public Result register(String user, String password, String code){
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(password) || TextUtils.isEmpty(code)){
            return Result.getFastResult(ErrorCode.PARAMS_ERROR,0);
        }
        Map<String, String> checkUserParams = new HashMap<String, String>();
        checkUserParams.put("user_name", user);
        Result result = fastAction(HttpAPI.checkUserName,checkUserParams);
        if (result == null){
            return Result.getFastResult(ErrorCode.RESPONSE_ERROR,0);
        }
        if (result.getExecResult() != ErrorCode.SUCCESS){
            return result;
        } else {
            Map<String, String> register = new HashMap<String, String>();
            register.put("user_name", user);
            register.put("password", MD5Util.getMD5Encoding(password));
            register.put("captcha", code);
            return fastAction(HttpAPI.register,register);
        }
    }

    public Result fixedRequest(Map<String,?> params, String api){
        if (TextUtils.isEmpty(api)){
            return Result.getFastResult(ErrorCode.PARAMS_ERROR,0);
        }
        if (params == null){
            params = new HashMap<>();
        }
        return fastAction(api,params);
    }

    /**
     * 按比例缩小图片的像素以达到压缩的目的
     *
     * @author JPH
     * @param imgPath
     * @date 2014-12-5下午11:30:59
     * @url http://blog.csdn.net/fengyuzhengfan/article/details/41759835
     */
    private void compressImageByPixel(String imgPath, float px, String outPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int width = newOpts.outWidth;
        int height = newOpts.outHeight;
        float maxSize = px;// 默认120px
        int be = 1;
        if (width > height && width > maxSize) {// 缩放比,用高或者宽其中较大的一个数据进行计算
            be = (int) (newOpts.outWidth / maxSize);
        } else if (width < height && height > maxSize) {
            be = (int) (newOpts.outHeight / maxSize);
        }
        be++;
        newOpts.inSampleSize = be;// 设置采样率
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        try {
            FileOutputStream fos = new FileOutputStream(new File(outPath));
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交反馈
     * @param cmd
     * @param jsonParams
     * @param handleType
     * @return
     */
    public Result postSuggest(int cmd, String jsonParams, int handleType){
        try {
            JSONObject jsonObject = new JSONObject(jsonParams);
            if (jsonObject != null){
                String picPath = jsonObject.optString("pic");
                String message = jsonObject.optString("message");
                String email = jsonObject.optString("email");
                String phone = jsonObject.optString("phone");
                if (TextUtils.isEmpty(message) || TextUtils.isEmpty(email)){
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
                        return null;
                    }
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
                }
                String uploadPicUrl = "";
                //1,提交图片
                if (!TextUtils.isEmpty(picPath)){
                    File file = new File(picPath);
                    if (file != null && file.isFile()){
                        String path = FileManager.getCacheFileDir() + file.getName();
                        compressImageByPixel(picPath,100f,path);
                        file = new File(path);
                        if (file != null && file.exists()){
                            HttpResult<String> picResult = HttpHelper.getInstance().postFile(HttpAPI.uploadSuggestPic,file,null,"imageFile");
                            file.delete();
                            if (picResult != null){
                                if (picResult.resultCode() != ErrorCode.SUCCESS){
                                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                                        EventBus.getDefault().post(Result.getFastResult(picResult.resultCode(),cmd).setObj(picResult.getResult()));
                                        return null;
                                    }
                                    return Result.getFastResult(picResult.resultCode(),cmd).setObj(picResult.getResult());
                                } else {
                                    try {
                                        if (!TextUtils.isEmpty(picResult.getResult())){
                                            JSONObject json = new JSONObject(picResult.getResult());
                                            if (json != null){
                                                int code = json.optInt("code");
                                                if (code == 0){
                                                    uploadPicUrl = json.optString("data");
                                                } else {
                                                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                                                        EventBus.getDefault().post(Result.getFastResult(code,cmd).setObj(json.optString("msg")));
                                                        return null;
                                                    }
                                                    return Result.getFastResult(code,cmd).setObj(json.optString("msg"));
                                                }
                                            }
                                        }
                                    } catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
                //2,提交内容  {"message":"","email":"","phone":"","pic_path":""}
                Map<String, Object> commit = new HashMap<>();
                commit.put("message", "");
                commit.put("email", "");
                commit.put("phone", "");
                commit.put("pic", "");
                jsonObject.put("pic",uploadPicUrl);
                return commonRequest(commit, jsonObject.toString(), HttpAPI.postSuggestText, cmd, handleType, true,false);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        if (handleType == BitdogInterface.ASYNC_HANDLE){
            EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
            return null;
        }
        return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
    }

    /**
     * 通用请求方法
     * @param params 请求参数的key
     * @param jsonParams 请求参数
     * @param api 请求接口
     * @param cmd 请求命令
     * @param handleType 请求线程
     * @param needParams 是否需要参数,false时不获取参数直接执行
     * @param needCheckParams 是否需要检测参数是否为空字符串
     * @return
     */
    public Result commonRequest(Map<String, Object> params, String jsonParams, String api, int cmd, int handleType, boolean needParams, boolean needCheckParams){
        Result result = Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
        if (needParams){
            if (TextUtils.isEmpty(jsonParams) || params == null || params.size() == 0){
                if (handleType == BitdogInterface.ASYNC_HANDLE){
                    EventBus.getDefault().post(result);
                    return null;
                } else {
                    return result;
                }
            }
            try {
                JSONObject jsonObject = new JSONObject(jsonParams);
                if (jsonObject != null){
                    Object value = null;
                    for (Map.Entry<String, Object> m : params.entrySet()){
                        value = jsonObject.opt(m.getKey());
                        if (value != null){
                            m.setValue(value);
                            if (needCheckParams){
                                if (value instanceof String){
                                    if (TextUtils.isEmpty(((String)value))){
                                        if (handleType == BitdogInterface.ASYNC_HANDLE){
                                            EventBus.getDefault().post(result);
                                            return null;
                                        } else {
                                            return result;
                                        }
                                    }
                                }
                            }
                        } else {
                            if (handleType == BitdogInterface.ASYNC_HANDLE){
                                EventBus.getDefault().post(result);
                                return null;
                            } else {
                                return result;
                            }
                        }
                    }
                    result = fixedRequest(params,api);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        } else {
            result = fixedRequest(null,api);
        }
        if (result != null){
            result.setCmd(cmd);
            if (result.getExecResult() == ErrorCode.SUCCESS){
                filterHandle(cmd,jsonParams);
            }
            if (handleType == BitdogInterface.ASYNC_HANDLE){
                EventBus.getDefault().post(result);
                return null;
            } else {
                return result;
            }
        } else {
            result = new Result();
        }
        result.setExecResult(ErrorCode.PARAMS_ERROR);
        result.setCmd(cmd);
        if (handleType == BitdogInterface.ASYNC_HANDLE){
            EventBus.getDefault().post(result);
        } else {
            return result;
        }
        return result;
    }

    public Result commonRequest(Map<String, Object> params, String jsonParams, String api, int cmd, int handleType, boolean needParams){
        return commonRequest(params,jsonParams,api,cmd,handleType,needParams,true);
    }

    private void filterHandle(final int cmd,final String jsonParams){
        BitdogInterface.getInstance().post(new Runnable() {
            @Override
            public void run() {
                if (cmd == BitdogCmd.DELETE_GROUP_CMD){
                    clearDeviceRamCache();
                    String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
                    try {
                        JSONObject jsonObject = new JSONObject(jsonParams);
                        if (jsonObject != null){
                            String cate_id = jsonObject.optString("cate_id");
                            if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(cate_id)){
                                Group group = new Group();
                                group.setGroupId(cate_id);
                                DBAction.getInstance().deleteGroup(group,userId);
                            }
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                } else if (cmd == BitdogCmd.UPDATE_GROUP_NAME_CMD){
                    String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
                    try {
                        JSONObject jsonObject = new JSONObject(jsonParams);
                        if (jsonObject != null){
                            String cate_id = jsonObject.optString("cate_id");
                            String cate_name = jsonObject.optString("cate_name");
                            if (TextUtils.isEmpty(cate_id))return;

                            int[] cmds = new int[]{BitdogCmd.REQUEST_DEVICE_MANAGER_DEVICE_LIST_CMD,
                                    BitdogCmd.REQUEST_VR_PLAY_DEVICE_LIST_CMD,
                                    BitdogCmd.REQUEST_NO_VR_PLAY_DEVICE_LIST_CMD};
                            for (int cmd : cmds){
                                Object obj = BitdogInterface.getInstance().getData(WorkContext.GROUP_DEVICE_DATA_KEY + cmd);
                                if (obj != null && obj instanceof GroupList){
                                    GroupList groupList = ((GroupList)obj);
                                    if (groupList.getGroups() != null){
                                        for (Group group : groupList.getGroups()){
                                            if (cate_id.equals(group.getGroupId())){
                                                group.setGroupName(cate_name);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            if (!TextUtils.isEmpty(userId)){
                                Group newGroup = new Group();
                                newGroup.setGroupName(cate_name);
                                DBAction.getInstance().updateGroup(cate_id,userId,newGroup);
                            }
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                } else if (cmd == BitdogCmd.SET_DEVICE_NAME_CMD){
                    clearDeviceRamCache();
                    String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
                    try {
                        JSONObject jsonObject = new JSONObject(jsonParams);
                        if (jsonObject != null){
                            String device_id = jsonObject.optString("device_id");
                            String device_name = jsonObject.optString("device_name");
                            if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(device_id)){
                                DBAction.getInstance().updateDeviceName(userId,device_id,device_name);
                            }
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                } else if (cmd == BitdogCmd.UNBIND_DEVICE_CMD){
                    clearDeviceRamCache();
                    String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
                    try {
                        JSONObject jsonObject = new JSONObject(jsonParams);
                        if (jsonObject != null){
                            String device_id = jsonObject.optString("device_id");
                            EqupInfo equpInfo = new EqupInfo();
                            equpInfo.setDirect(false);
                            equpInfo.setEqupId(device_id);
                            if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(device_id)){
                                EventBus.getDefault().post(UpdateRAMDataEvent.deviceDelete(device_id));
                                clearDeviceRamCache();
                                DBAction.getInstance().deleteDevice(equpInfo,userId);
                                DBAction.getInstance().deletePlayCacheDevice(equpInfo,userId);
                                DBAction.getInstance().deleteFavoritesPlayerDevice(device_id);
                            }
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                } else if (cmd == BitdogCmd.CHANGE_DEVICE_GROUP_CMD
                        || cmd == BitdogCmd.BIND_DEVICE_CMD
                        || cmd == BitdogCmd.BIND_SHARE_DEVICE_CMD){
                    clearDeviceRamCache();
                } else if (cmd == BitdogCmd.SET_PASSWORD_CMD){
                    if (!TextUtils.isEmpty(WorkContext.ACCOUNT)){
                        LoginRecord loginRecord = new LoginRecord();
                        loginRecord.setAccount(WorkContext.ACCOUNT);
                        loginRecord.setPassword("");
                        loginRecord.setLoginType(LoginRecord.ACCOUNT);
                        loginRecord.setTime(String.valueOf(System.currentTimeMillis()));
                        DBAction.getInstance().insertLoginRecord(loginRecord);
                    }
                } else if (cmd == BitdogCmd.CHANGE_REPLAY_CODE_TYPE_CMD || cmd == BitdogCmd.CHANGE_REPLAY_VIDEO_TYPE_CMD){
                    try {
                        clearDeviceRamCache();
                        JSONObject jsonObject = new JSONObject(jsonParams);
                        if (jsonObject != null){
                            String device_id = jsonObject.optString("device_id");
                            int channel_id = -1;
                            if (jsonObject.has("channel")){
                                channel_id = jsonObject.optInt("channel",-1);
                            }
                            if (jsonObject.has("channel_id")){
                                channel_id = jsonObject.optInt("channel_id",-1);
                            }
                            int data_rate = jsonObject.optInt("data_rate",-1);
                            int video_type = jsonObject.optInt("video_type",-1);
                            DBAction.getInstance().updateReplayStreamVideoTypeCache(device_id,channel_id,data_rate,video_type);
                            EventBus.getDefault().post(UpdateRAMDataEvent.playbackChangedReplaySetting(jsonParams));
                            BitdogInterface.getInstance().exec(BitdogCmd.REQUEST_DEVICE_MANAGER_DEVICE_LIST_CMD, "{\"need_cache\":\"0\"}", BitdogInterface.ASYNC_HANDLE);
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                } else if (cmd == BitdogCmd.CHANGE_DEVICE_USER_PASSWORD_CMD){
                    try {
                        clearDeviceRamCache();
                        JSONObject jsonObject = new JSONObject(jsonParams);
                        if (jsonObject != null){
                            String device_id = jsonObject.optString("device_id");
                            String local_user = jsonObject.optString("local_user");
                            String local_pwd = jsonObject.optString("local_pwd");
                            DBAction.getInstance().updateDeviceUserPasswordCache(device_id,local_user,local_pwd);
                            EventBus.getDefault().post(UpdateRAMDataEvent.devicePasswordChanged(jsonParams));
                            BitdogInterface.getInstance().exec(BitdogCmd.REQUEST_DEVICE_MANAGER_DEVICE_LIST_CMD, "{\"need_cache\":\"0\"}", BitdogInterface.ASYNC_HANDLE);
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        }, ThreadWrapper.THREAD_IO);
    }

    private void clearDeviceRamCache(){
        int[] cmds = new int[]{BitdogCmd.REQUEST_DEVICE_MANAGER_DEVICE_LIST_CMD,
                BitdogCmd.REQUEST_VR_PLAY_DEVICE_LIST_CMD,
                BitdogCmd.REQUEST_NO_VR_PLAY_DEVICE_LIST_CMD};
        for (int cmd : cmds){
            BitdogInterface.getInstance().delData(WorkContext.GROUP_DEVICE_DATA_KEY + cmd);
        }
    }
    /**
     * 提交设备解绑申请
     * @param cmd
     * @param jsonParams
     * @param handleType
     * @return
     */
    public Result postUnbindApply(int cmd, String jsonParams, int handleType) {
        try {
            JSONObject jsonObject = new JSONObject(jsonParams);
            if (jsonObject != null){
                String picPath = jsonObject.optString("pic");
                String device_id = jsonObject.optString("device_id");
                String email = jsonObject.optString("email");
                if (TextUtils.isEmpty(device_id) || TextUtils.isEmpty(email)){
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
                        return null;
                    }
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
                }
                String uploadPicUrl = "";
                //1,提交图片
                if (!TextUtils.isEmpty(picPath)){
                    File file = new File(picPath);
                    if (file != null && file.isFile()){
//                        String path = FileManager.getCacheFileDir() + file.getName();
//                        compressImageByPixel(picPath,100f,path);
                        file = new File(picPath);
                        if (file != null && file.exists()){
                            HttpResult<String> picResult = HttpHelper.getInstance().postFile(HttpAPI.actionSinglePic,file,null,"imageFiles");
                            if (picResult != null){
                                if (picResult.resultCode() != ErrorCode.SUCCESS){
                                    file.delete();
                                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                                        EventBus.getDefault().post(Result.getFastResult(picResult.resultCode(),cmd).setObj(picResult.getResult()));
                                        return null;
                                    }
                                    return Result.getFastResult(picResult.resultCode(),cmd).setObj(picResult.getResult());
                                } else {
                                    try {
                                        if (!TextUtils.isEmpty(picResult.getResult())){
                                            JSONObject json = new JSONObject(picResult.getResult());
                                            if (json != null){
                                                int code = json.optInt("code");
                                                if (code == 0){
                                                    uploadPicUrl = json.optString("data");
                                                } else {
                                                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                                                        EventBus.getDefault().post(Result.getFastResult(code,cmd).setObj(json.optString("msg")));
                                                        return null;
                                                    }
                                                    return Result.getFastResult(code,cmd).setObj(json.optString("msg"));
                                                }
                                            }
                                        }
                                    } catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
                //2,提交内容  {"device_id":"","email":"","pic":""}
                Map<String, Object> commit = new HashMap<>();
                commit.put("device_id", "");
                commit.put("email", "");
                commit.put("pic", "");
                jsonObject.put("pic",uploadPicUrl);
                return commonRequest(commit, jsonObject.toString(), HttpAPI.actionApplyForUnitingDevice, cmd, handleType, true,false);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        if (handleType == BitdogInterface.ASYNC_HANDLE){
            EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
            return null;
        }
        return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
    }

    public LoginAccountInfoEvent requestAccountInfo(int cmd, List<LoginRecord> loginRecords){
        List<LoginAccountInfo> loginAccountInfoList = new ArrayList<>();
        LoginAccountInfoEvent loginAccountInfoEvent = new LoginAccountInfoEvent();
        loginAccountInfoEvent.setCmd(cmd);
        if (loginRecords == null || loginRecords.size() == 0){
            loginAccountInfoEvent.setResultCode(ErrorCode.EMPTY_RESULT);
            return loginAccountInfoEvent;
        }
        JSONArray array = new JSONArray();
        for (LoginRecord loginRecord : loginRecords){
            if (loginRecord != null){
                if (LoginRecord.ACCOUNT.equals(loginRecord.getLoginType())){
                    if (!TextUtils.isEmpty(loginRecord.getAccount())){
                        array.put(loginRecord.getAccount());
                    }
                }
            }
        }
        if (array.length() == 0){
            loginAccountInfoEvent.setResultCode(ErrorCode.EMPTY_RESULT);
            return loginAccountInfoEvent;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("requestNo", System.currentTimeMillis() + "");
            jsonObject.put("liveTime","30");
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("user_names",array);
            jsonObject.put("param",jsonParams);

            HttpResult<String> response = HttpHelper.getInstance().postString(HttpAPI.accountInfo,jsonObject.toString(),null);
            if (response == null){
                loginAccountInfoEvent.setResultCode(ErrorCode.RESPONSE_ERROR);
                return loginAccountInfoEvent;
            }
            int code = response.resultCode();
            if (code == ErrorCode.SUCCESS){
                String str = response.getResult();
                if (!TextUtils.isEmpty(str)){
                    try {
                        KLog.getInstance().d(HttpAPI.accountInfo + " http request result = %s" ,str).print();
                        JSONObject json = new JSONObject(str);
                        if (json != null){
                            int c = json.optInt("code");
                            if (c == 0){
                                loginAccountInfoEvent.setResultCode(ErrorCode.SUCCESS);
                                JSONArray jsonArray = json.optJSONArray("data");
                                if (jsonArray != null){
                                    int length = jsonArray.length();
                                    int len = loginRecords.size();
                                    JSONObject jsonItem = null;
                                    String account = "";
                                    for (int i = 0; i < length; i++){
                                        jsonItem = jsonArray.optJSONObject(i);
                                        if (jsonItem != null){
                                            for (int j = 0; j < len; j++){
                                                account = loginRecords.get(j).getAccount();
                                                if (!TextUtils.isEmpty(account)){
                                                    if (account.equals(jsonItem.optString("user_name"))){
                                                        LoginAccountInfo loginAccountInfo = new LoginAccountInfo();
                                                        loginAccountInfo.setAccount(account);
                                                        loginAccountInfo.setIconUrl(jsonItem.optString("user_img"));
                                                        loginAccountInfo.setPassword(loginRecords.get(j).getPassword());
                                                        loginAccountInfo.setUserId(jsonItem.optString("user_id"));
                                                        loginAccountInfo.setUserName(jsonItem.optString("nickname"));
                                                        loginAccountInfoList.add(loginAccountInfo);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    loginAccountInfoEvent.setLoginAccountInfos(loginAccountInfoList);
                                }
                                return loginAccountInfoEvent;
                            } else {
                                loginAccountInfoEvent.setResultCode(c);
                                loginAccountInfoEvent.setErrMsg(json.optString("msg"));
                                return loginAccountInfoEvent;
//                                return Result.getFastResult(c,0).setObj(json.optString("msg"));
                            }
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                loginAccountInfoEvent.setResultCode(ErrorCode.RESPONSE_ERROR);
            } else {
                loginAccountInfoEvent.setResultCode(code);
            }
        } catch (JSONException e){
            e.printStackTrace();
            loginAccountInfoEvent.setResultCode(ErrorCode.RESPONSE_ERROR);
        }
        return loginAccountInfoEvent;
    }
}
