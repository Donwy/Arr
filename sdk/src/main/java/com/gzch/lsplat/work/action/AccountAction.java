package com.gzch.lsplat.work.action;

import android.text.TextUtils;

import com.gzch.lsplat.work.ErrorCode;
import com.gzch.lsplat.work.WorkContext;
import com.gzch.lsplat.work.cmd.BitdogCmd;
import com.gzch.lsplat.work.mode.LoginRecord;
import com.gzch.lsplat.work.mode.UserInfo;
import com.gzch.lsplat.work.mode.event.DataKey;
import com.gzch.lsplat.work.net.HttpRequest;
import com.longse.lsapc.lsacore.BitdogInterface;
import com.longse.lsapc.lsacore.mode.Result;
import com.longse.lsapc.lsacore.sapi.sharedPreferences.StringCache;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lw on 2017/12/22.
 */

public class AccountAction {

    private HttpRequest httpRequest = new HttpRequest();

    /**
     * 账号登录
     * @param cmd
     * @param jsonParams
     */
    public void loginAccount(int cmd, String jsonParams){
        loginAccount(cmd,jsonParams,true);
    }

    public void loginAccount(int cmd, String jsonParams, boolean needIP){
        String local_ip = "";
        String account = "";
        int port = 0;
        int nosslport = 0;
        boolean isRun = false;
        if (!TextUtils.isEmpty(jsonParams)){
            try {
                JSONObject json = new JSONObject(jsonParams);
                if (json != null){
                    String userName = json.optString("account");
                    String password = json.optString("password");
                    local_ip = json.optString("local_ip");
                    int save = json.optInt("save",0);
                    isRun = true;
                    loginPlatform(userName,local_ip,needIP,port,nosslport);
                    Result r = httpRequest.login(userName,password);
                    if (r != null){
                        if (r.getExecResult() != ErrorCode.SUCCESS){
                            EventBus.getDefault().post(r.setCmd(cmd));
                            if (r.getExecResult() == ErrorCode.CONNECT_UNABLE){
                                return;
                            }
                        } else {
//                            if (r.getObj() != null && r.getObj() instanceof JSONObject){
//                                JSONObject data = (JSONObject)r.getObj();
////                                KLog.getInstance().d("loginAccount http request data = %s" ,data.optString("data")).print();
//                                HttpHelper.getInstance().addCookie(data.optString("data"));
//                            }
                            WorkContext.ACCOUNT = userName;
                            EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS,cmd));
                            LoginRecord loginRecord = new LoginRecord();
                            loginRecord.setAccount(userName);
                            loginRecord.setPassword(save == 0 ? password : "");
                            loginRecord.setLoginType(LoginRecord.ACCOUNT);
                            loginRecord.setTime(String.valueOf(System.currentTimeMillis()));
                            DBAction.getInstance().insertLoginRecord(loginRecord);
                            account = userName;
                        }
                    }
                }
            } catch (JSONException e){
                e.printStackTrace();
                EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
            }
        }
        if (!isRun){
            loginPlatform("",local_ip,needIP,port,nosslport);
        }
    }

    private void loginPlatform(String account, String local_ip, boolean needIP, int port, int nosslport){
        if (TextUtils.isEmpty(local_ip) && needIP){
            Result result = httpRequest.requestLocalIP();
            if (result != null){
                int code = result.getExecResult();
                if (code == ErrorCode.SUCCESS){
//                    local_ip = String.valueOf(result.getObj());
                    if (result.getObj() != null && result.getObj() instanceof JSONObject){
                        JSONObject data = (JSONObject) result.getObj();
                        local_ip = data.optString("user_server_ip");
                        port = data.optInt("user_server_port");
                        nosslport = data.optInt("tcp_user_server_port");
                    }
                }
            }
        }
        String platformParams = "{\"user_name\":\"%s\",\"local_ip\":\"%s\",\"port\":%d,\"no_ssl_port\":%d}";
        BitdogInterface.getInstance().exec(BitdogCmd.LOGIN_PLATFORM_CMD,
                String.format(platformParams,account,local_ip,port,nosslport), BitdogInterface.ASYNC_HANDLE);
    }

    /**
     * 微信已经授权登录过了的话，调该方法自动登录
     * @param cmd
     * @param token 微信授权码
     * @param local_ip 登录平台时使用的IP
     */
    public void wechatLogin(int cmd, String token, String local_ip){
        loginPlatform("",local_ip,true,0,0);
        if (TextUtils.isEmpty(token)){
            EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
            return;
        }
        String account = "";
        Result r = httpRequest.wechatLogin(token);
        if (r != null){
            if (r.getExecResult() != ErrorCode.SUCCESS){
                EventBus.getDefault().post(r.setCmd(cmd));
                if (r.getExecResult() == ErrorCode.CONNECT_UNABLE){
                    return;
                }
            } else {
                EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS,cmd));
                if (r.getObj() != null && r.getObj() instanceof JSONObject){
                    UserInfo userInfo = UserInfo.parse(((JSONObject)r.getObj()).optJSONObject("data"));
                    if (userInfo != null){
                        StringCache.getInstance().addCache(WorkContext.USER_ID_KEY,userInfo.getUserId());

                        userInfo.setCmd(BitdogCmd.REQUEST_USER_INFO_CMD);
                        userInfo.setExecResultCode(ErrorCode.SUCCESS);
                        EventBus.getDefault().post(userInfo);

                        account = userInfo.getUserId();
                        LoginRecord loginRecord = new LoginRecord();
                        loginRecord.setLoginType(LoginRecord.WECHAT);
                        loginRecord.setTime(String.valueOf(System.currentTimeMillis()));
                        loginRecord.setToken(token);
                        loginRecord.setUserId(account);
                        DBAction.getInstance().insertLoginRecord(loginRecord);
                    }
                }
            }
        }
    }

    /**
     * facebook已经授权登录过了的话，调该方法自动登录
     * @param cmd
     * @param token
     * @param local_ip
     */
    public void facebookLogin(int cmd, String token, String local_ip){
        loginPlatform("",local_ip,true,0,0);
        if (TextUtils.isEmpty(token)){
            EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
            return;
        }
        String account = "";
        Result r = httpRequest.facebookLogin(token);
        if (r != null){
            if (r.getExecResult() != ErrorCode.SUCCESS){
                EventBus.getDefault().post(r.setCmd(cmd));
                if (r.getExecResult() == ErrorCode.CONNECT_UNABLE){
                    return;
                }
            } else {
                EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS,cmd));
                if (r.getObj() != null && r.getObj() instanceof JSONObject){
                    UserInfo userInfo = UserInfo.parse(((JSONObject)r.getObj()).optJSONObject("data"));
                    if (userInfo != null){
                        StringCache.getInstance().addCache(WorkContext.USER_ID_KEY,userInfo.getUserId());

                        userInfo.setCmd(BitdogCmd.REQUEST_USER_INFO_CMD);
                        userInfo.setExecResultCode(ErrorCode.SUCCESS);
                        EventBus.getDefault().post(userInfo);

                        account = userInfo.getUserId();
                        LoginRecord loginRecord = new LoginRecord();
                        loginRecord.setLoginType(LoginRecord.FACEBOOK);
                        loginRecord.setTime(String.valueOf(System.currentTimeMillis()));
                        loginRecord.setToken(token);
                        loginRecord.setUserId(account);
                        DBAction.getInstance().insertLoginRecord(loginRecord);
                    }
                }
            }
        }
    }

    /**
     * Line已经授权登录过了的话，调该方法自动登录
     * @param cmd
     * @param token
     * @param local_ip
     */
    public void lineLogin(int cmd, String token, String local_ip){
        loginPlatform("",local_ip,true,0,0);
        if (TextUtils.isEmpty(token)){
            EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
            return;
        }
        String account = "";
        Result r = httpRequest.lineLogin(token);
        if (r != null){
            if (r.getExecResult() != ErrorCode.SUCCESS){
                EventBus.getDefault().post(r.setCmd(cmd));
            } else {
                EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS,cmd));
                if (r.getObj() != null && r.getObj() instanceof JSONObject){
                    UserInfo userInfo = UserInfo.parse(((JSONObject)r.getObj()).optJSONObject("data"));
                    if (userInfo != null){
                        StringCache.getInstance().addCache(WorkContext.USER_ID_KEY,userInfo.getUserId());

                        userInfo.setCmd(BitdogCmd.REQUEST_USER_INFO_CMD);
                        userInfo.setExecResultCode(ErrorCode.SUCCESS);
                        EventBus.getDefault().post(userInfo);

                        account = userInfo.getUserId();
                        LoginRecord loginRecord = new LoginRecord();
                        loginRecord.setLoginType(LoginRecord.FACEBOOK);
                        loginRecord.setTime(String.valueOf(System.currentTimeMillis()));
                        loginRecord.setToken(token);
                        loginRecord.setUserId(account);
                        DBAction.getInstance().insertLoginRecord(loginRecord);
                    }
                }
            }
        }
    }

    /**
     * Twitter已经授权登录过了的话，调该方法自动登录
     * @param cmd
     * @param accessToken
     * @param tokenSecret
     * @param local_ip
     */
    public void twitterLogin(int cmd, String accessToken, String tokenSecret, String local_ip){
        loginPlatform("",local_ip,true,0,0);
        if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(tokenSecret)){
            EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
            return;
        }
        String account = "";
        Result r = httpRequest.twitterLogin(accessToken,tokenSecret);
        if (r != null){
            if (r.getExecResult() != ErrorCode.SUCCESS){
                EventBus.getDefault().post(r.setCmd(cmd));
                if (r.getExecResult() == ErrorCode.CONNECT_UNABLE){
                    return;
                }
            } else {
                EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS,cmd));
                if (r.getObj() != null && r.getObj() instanceof JSONObject){
                    UserInfo userInfo = UserInfo.parse(((JSONObject)r.getObj()).optJSONObject("data"));
                    if (userInfo != null){
                        StringCache.getInstance().addCache(WorkContext.USER_ID_KEY,userInfo.getUserId());

                        userInfo.setCmd(BitdogCmd.REQUEST_USER_INFO_CMD);
                        userInfo.setExecResultCode(ErrorCode.SUCCESS);
                        EventBus.getDefault().post(userInfo);

                        account = userInfo.getUserId();
                        LoginRecord loginRecord = new LoginRecord();
                        loginRecord.setLoginType(LoginRecord.TWITTER);
                        loginRecord.setTime(String.valueOf(System.currentTimeMillis()));
                        loginRecord.setToken(accessToken);
                        loginRecord.setToken2(tokenSecret);
                        loginRecord.setUserId(account);
                        DBAction.getInstance().insertLoginRecord(loginRecord);
                    }
                }
            }
        }
    }

    /**
     * Google+ 已经授权登录过了的话，调该方法自动登录
     * @param cmd
     * @param clientId
     * @param local_ip
     */
    public void googleLogin(int cmd, String clientId, String local_ip){
        loginPlatform("",local_ip,true,0,0);
        if (TextUtils.isEmpty(clientId)){
            EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
            return;
        }
        String account = "";
        Result r = httpRequest.googleLogin(clientId);
        if (r != null){
            if (r.getExecResult() != ErrorCode.SUCCESS){
                EventBus.getDefault().post(r.setCmd(cmd));
                if (r.getExecResult() == ErrorCode.CONNECT_UNABLE){
                    return;
                }
            } else {
                EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS,cmd));
                if (r.getObj() != null && r.getObj() instanceof JSONObject){
                    UserInfo userInfo = UserInfo.parse(((JSONObject)r.getObj()).optJSONObject("data"));
                    if (userInfo != null){
                        StringCache.getInstance().addCache(WorkContext.USER_ID_KEY,userInfo.getUserId());

                        userInfo.setCmd(BitdogCmd.REQUEST_USER_INFO_CMD);
                        userInfo.setExecResultCode(ErrorCode.SUCCESS);
                        EventBus.getDefault().post(userInfo);

                        account = userInfo.getUserId();
                        LoginRecord loginRecord = new LoginRecord();
                        loginRecord.setLoginType(LoginRecord.GOOGLE);
                        loginRecord.setTime(String.valueOf(System.currentTimeMillis()));
                        loginRecord.setToken(clientId);
                        loginRecord.setUserId(account);
                        DBAction.getInstance().insertLoginRecord(loginRecord);
                    }
                }
            }
        }
    }

    public void requestUserInfo(int cmd){
        Result r = httpRequest.requestUserInfo();
        UserInfo userInfo = null;
        if (r != null){
            if (r.getExecResult() != ErrorCode.SUCCESS){
                userInfo = new UserInfo();
                userInfo.setCmd(cmd);
                userInfo.setExecResultCode(r.getExecResult());
                userInfo.setObj(r.getObj());
            } else {
                if (r.getObj() != null && r.getObj() instanceof JSONObject){
                    userInfo = UserInfo.parse(((JSONObject)r.getObj()).optJSONObject("data"));
                    if (userInfo != null){
                        StringCache.getInstance().addCache(WorkContext.USER_ID_KEY,userInfo.getUserId());
//                        BitdogInterface.getInstance().exec(BitdogCmd.JPUSH_AUTO_START_CMD,userInfo.getUserId(),BitdogInterface.ASYNC_HANDLE);
                        userInfo.setCmd(cmd);
                        userInfo.setExecResultCode(ErrorCode.SUCCESS);
                    }
                }
            }
        } else {
            userInfo = new UserInfo();
            userInfo.setCmd(cmd);
            userInfo.setExecResultCode(ErrorCode.RESPONSE_ERROR);
        }
        EventBus.getDefault().post(userInfo);
        if (userInfo != null && userInfo.getExecResultCode() == ErrorCode.SUCCESS){
            DBAction.getInstance().insertUserInfo(userInfo.getEmail(),userInfo);
        }
    }

    public DataKey showVerification(int cmd){
        return httpRequest.showVerification(cmd);
    }

}
