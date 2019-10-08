package com.gzch.lsplat.work;

import android.text.TextUtils;

import com.example.sdk.R;
import com.gzch.lsplat.work.action.DBAction;
import com.gzch.lsplat.work.api.HttpAPI;
import com.gzch.lsplat.work.cmd.BitdogCmd;
import com.gzch.lsplat.work.mode.DemoInfo;
import com.gzch.lsplat.work.mode.DeviceInfoCache;
import com.gzch.lsplat.work.mode.DirectInfo;
import com.gzch.lsplat.work.mode.EqupInfo;
import com.gzch.lsplat.work.mode.Group;
import com.gzch.lsplat.work.mode.Member;
import com.gzch.lsplat.work.mode.SerialLoginRecord;
import com.gzch.lsplat.work.mode.event.DataKey;
import com.gzch.lsplat.work.mode.event.DemoDeviceList;
import com.gzch.lsplat.work.mode.event.DeviceList;
import com.gzch.lsplat.work.mode.event.GroupList;
import com.gzch.lsplat.work.mode.event.MemberList;
import com.gzch.lsplat.work.mode.event.SerialLoginRecordEvent;
import com.gzch.lsplat.work.mode.event.SerialNumberDeviceInfoEvent;
import com.gzch.lsplat.work.mode.event.UpdateRAMDataEvent;
import com.gzch.lsplat.work.net.HttpRequest;
import com.longse.lsapc.lsacore.BitdogInterface;
import com.longse.lsapc.lsacore.interf.LSCoreInterface;
import com.longse.lsapc.lsacore.interf.ThreadWrapper;
import com.longse.lsapc.lsacore.mode.Result;
import com.longse.lsapc.lsacore.sapi.http.WiFiHelper;
import com.longse.lsapc.lsacore.sapi.sharedPreferences.StringCache;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lw on 2017/12/25.
 */

public class WebDeviceWork implements LSCoreInterface.Worker {

    private HttpRequest httpRequest = new HttpRequest();

    private Map<String, SoftReference<DeviceInfoCache>> deviceCache = new ConcurrentHashMap<>();

    @Override
    public void init() {
    }

    @Override
    public boolean isMyCmd(int cmd) {
        if (cmd >= BitdogCmd.REQUEST_DEVICE_LIST_CMD && cmd <= BitdogCmd.REQUEST_NO_VR_PLAY_DEVICE_LIST_CMD){
            return true;
        }
        if (cmd >= BitdogCmd.UPDATE_GROUP_NAME_CMD && cmd <= BitdogCmd.CREATE_GROUP_CMD){
            return true;
        }
        if (cmd >= BitdogCmd.SET_DEVICE_NAME_CMD && cmd <= BitdogCmd.DELETE_DIRECT_DEVICE_CMD){
            return true;
        }
        if (cmd >= BitdogCmd.CHECK_DEVICE_CODE_CMD && cmd <= BitdogCmd.CONNECT_WIFI_CMD){
            return true;
        }
        if (cmd == BitdogCmd.GRT_SERIAL_LOGIN_RECORD_CMD){
            return true;
        }
        if (cmd == BitdogCmd.INSERT_LOCALE_DEVICE_ACCOUNT || cmd == BitdogCmd.QUERY_LOCALE_DEVICE_ACCOUNT){
            return true;
        }
        if (cmd == BitdogCmd.CHECK_DEVICE_LOCALE_ACCOUNT_CMD){
            return true;
        }

        return cmd == BitdogCmd.REQUEST_SHARER_DEVICE_TOKEN_CMD || cmd == BitdogCmd.BIND_OLD_SHARE_DEVICE_CMD;
    }

    @Override
    public Result worked(int cmd, String jsonParams, int handleType) {
        switch (cmd){
            case BitdogCmd.CHECK_DEVICE_LOCALE_ACCOUNT_CMD:
                Map<String, Object> checkAccount = new HashMap<>();
                checkAccount.put("device_id","");
                checkAccount.put("local_user","");
                return httpRequest.commonRequest(checkAccount,jsonParams,HttpAPI.checkDeviceLocaleAccount,cmd,handleType,true);
            case BitdogCmd.REQUEST_DEVICE_LIST_CMD:
            case BitdogCmd.REQUEST_VR_DEVICE_LIST_CMD:
            case BitdogCmd.REQUEST_NO_VR_DEVICE_LIST_CMD:
                DeviceList deviceList = requestDeviceList(cmd);
                if (handleType == BitdogInterface.ASYNC_HANDLE){
                    EventBus.getDefault().post(deviceList);
                } else {
                    return Result.getFastResult(ErrorCode.SUCCESS,cmd).setObj(deviceList);
                }
                break;
            case BitdogCmd.REQUEST_DEVICE_GROUP_CMD:
                GroupList groupList = requestGroups(cmd);
                if (handleType == BitdogInterface.ASYNC_HANDLE){
                    EventBus.getDefault().post(groupList);
                } else {
                    return Result.getFastResult(ErrorCode.SUCCESS,cmd).setObj(groupList);
                }
                break;
            case BitdogCmd.GET_DIRECT_DEVICE_LIST:
                if (handleType == BitdogInterface.SYNC_HANDLE){
                    return Result.getFastResult(ErrorCode.NO_SUPPORT_THREAD,cmd);
                }
                final String k = WorkContext.GROUP_DEVICE_DATA_KEY + cmd;
                Group directGroupList = new Group();
                GroupList groupListDBList = new GroupList();
                List<EqupInfo> deviceDirectList = null;
                List<EqupInfo> deviceSNList = null;
                directGroupList.setGroupId("-1");
                directGroupList.setGroupPosition("-1");
                directGroupList.setGroupName(BitdogInterface.getInstance().getApplicationContext().getString(R.string.direct_devices));
                deviceDirectList = DBAction.getInstance().queryDirectDevice();
                deviceSNList = DBAction.getInstance().querySNPlayerDevice();
                if (deviceSNList != null && deviceSNList.size() > 0){
                    if (deviceDirectList == null){
                        deviceDirectList = deviceSNList;
                    } else {
                        deviceDirectList.addAll(deviceSNList);
                    }
                }
                DataKey dk = new DataKey();
                if (deviceDirectList != null && deviceDirectList.size() > 0){
                    directGroupList.setDevices(deviceDirectList);
                    List<Group> groupList2 = new ArrayList<>();
                    groupList2.add(directGroupList);
                    groupListDBList.setGroups(groupList2);
                    BitdogInterface.getInstance().putData(k,groupListDBList);
                    dk.setResultCode(ErrorCode.SUCCESS);
                    dk.setKEY(k);
                } else {
                    BitdogInterface.getInstance().delData(k);
                    dk.setResultCode(ErrorCode.EMPTY_RESULT);
                }
                dk.setCmd(cmd);
                EventBus.getDefault().post(dk);
                break;
            case BitdogCmd.REQUEST_DEVICE_MANAGER_DEVICE_LIST_CMD:
            case BitdogCmd.REQUEST_VR_PLAY_DEVICE_LIST_CMD:
            case BitdogCmd.REQUEST_NO_VR_PLAY_DEVICE_LIST_CMD:
                if (handleType == BitdogInterface.SYNC_HANDLE){
                    return Result.getFastResult(ErrorCode.NO_SUPPORT_THREAD,cmd);
                }
                final String key = WorkContext.GROUP_DEVICE_DATA_KEY + cmd;
                boolean needDirectDevice = true;//是否需要直连设备
                boolean needDeviceCache = true;//是否需要cache data
                if (!TextUtils.isEmpty(jsonParams)){
                    try {
                        JSONObject json = new JSONObject(jsonParams);
                        if (json != null){
                            String needDirect = json.optString("need_direct","1");
                            String needCache = json.optString("need_cache","1");
                            if ("0".equals(needDirect)){
                                needDirectDevice = false;
                            }
                            if ("0".equals(needCache)){
                                needDeviceCache = false;
                            }
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                Group favoritesGroup = null;
                List<EqupInfo> favorites = DBAction.getInstance().queryFavoritesPlayerDevice();
                if (favorites != null && favorites.size() > 0){
                    favoritesGroup = new Group();
                    favoritesGroup.setGroupId("-2");
                    favoritesGroup.setGroupPosition("-2");
                    favoritesGroup.setGroupName(BitdogInterface.getInstance().getApplicationContext().getString(R.string.favorites));
                    favoritesGroup.setDevices(favorites);
                }
                Group directGroup = new Group();
                GroupList groupListDB = null;
                List<EqupInfo> deviceDirect = null;
                List<EqupInfo> deviceSNLocale = null;
                String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
                if (cmd == BitdogCmd.REQUEST_VR_PLAY_DEVICE_LIST_CMD){
                    needDirectDevice = false;
                }
                if (needDirectDevice){
                    directGroup.setGroupId("-1");
                    directGroup.setGroupPosition("-1");
                    directGroup.setGroupName(BitdogInterface.getInstance().getApplicationContext().getString(R.string.direct_devices));
                    deviceDirect = DBAction.getInstance().queryDirectDevice();
                    deviceSNLocale = DBAction.getInstance().querySNPlayerDevice();
                }
                if (needDeviceCache){
                    boolean hasRAM = false;//内存中是否有数据
                    if (BitdogInterface.getInstance().getData(key) != null){
                        hasRAM = true;
                        DataKey d = new DataKey();
                        d.setResultCode(ErrorCode.SUCCESS);
                        d.setCmd(cmd);
                        d.setKEY(key);
                        if (!needDirectDevice){
                            GroupList ramData = (GroupList) BitdogInterface.getInstance().getData(key);
                            if (ramData.getGroups() != null){
                                Iterator<Group> groupIterator = ramData.getGroups().iterator();
                                while (groupIterator.hasNext()){
                                    if ("-1".equals(groupIterator.next().getGroupId())){
                                        groupIterator.remove();
                                    }
                                }
                            }
                        }
                        EventBus.getDefault().post(d);
                    }
                    if (!hasRAM){
                        //去数据库请求
                        if (!TextUtils.isEmpty(userId)){
                            List<EqupInfo> devices = null;
                            if (cmd == BitdogCmd.REQUEST_VR_PLAY_DEVICE_LIST_CMD){
                                devices = DBAction.getInstance().queryVRDevice(userId);
                            } else if (cmd == BitdogCmd.REQUEST_NO_VR_PLAY_DEVICE_LIST_CMD){
                                devices = DBAction.getInstance().queryNoVRDevice(userId);
                            } else {
                                devices = DBAction.getInstance().queryDevice(userId);
                            }
                            List<Group> groups = DBAction.getInstance().queryGroup(userId);
                            if (devices != null & groups != null && groups.size() > 0 && devices.size() > 0){
                                if (favorites != null && favorites.size() > 0){
                                    groups.add(0,favoritesGroup);
                                }
                                Collections.sort(devices);
                                List<EqupInfo> noGroupDevice = new ArrayList<>();
                                for (EqupInfo e: devices){
                                    boolean isAdd = false;
                                    if (e.getCateId() != null){
                                        for (Group g : groups){
                                            if (e.getCateId().equals(g.getGroupId())){
                                                e.setCateName(g.getGroupName());
                                                if (g.getDevices() == null){
                                                    g.setDevices(new ArrayList<EqupInfo>());
                                                }
                                                g.getDevices().add(e);
                                                isAdd = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!isAdd){
                                        noGroupDevice.add(e);
                                    }
                                    if (e.getInfoEntitys() != null){
                                        Collections.sort(e.getInfoEntitys());
                                    }
                                }
                                if (noGroupDevice != null && noGroupDevice.size() > 0){
                                    for (Group g : groups){
                                        if (g != null && "0".equals(g.getGroupId())){
                                            if (g.getDevices() != null){
                                                g.getDevices().addAll(noGroupDevice);
                                            } else {
                                                g.setDevices(noGroupDevice);
                                            }
                                            break;
                                        }
                                    }
                                }
                                if (groups.size() > 0 && (deviceDirect == null || devices.size() > deviceDirect.size())){
                                    Collections.sort(groups);
                                    groupListDB = new GroupList();
                                    groupListDB.setGroups(groups);
                                    BitdogInterface.getInstance().putData(key,groupListDB);
                                    DataKey d = new DataKey();
                                    d.setResultCode(ErrorCode.SUCCESS);
                                    d.setCmd(cmd);
                                    d.setKEY(key);
                                    EventBus.getDefault().post(d);
                                }
                            }
                        }
                    }
                }
                GroupList groupList1 = getDeviceGroupList(cmd);
                DataKey dataKey = new DataKey();
                dataKey.setCmd(cmd);
                if (needDirectDevice){
                    if (deviceDirect != null){
                        if (deviceSNLocale != null){
                            deviceDirect.addAll(deviceSNLocale);
                        }
                        directGroup.setDevices(deviceDirect);
                    } else {
                        if (deviceSNLocale != null){
                            directGroup.setDevices(deviceSNLocale);
                        }
                    }
                }
                if (groupList1 != null){
                    if (needDirectDevice){
                        if (deviceDirect != null && deviceDirect.size() > 0){
                            if (groupList1.getGroups() == null){
                                groupList1.setGroups(new ArrayList<Group>());
                            }
                            groupList1.getGroups().add(0,directGroup);
                        }
                    }
                    if (favorites != null && favorites.size() > 0){
                        if (groupList1.getGroups() == null){
                            groupList1.setGroups(new ArrayList<Group>());
                        }
                        groupList1.getGroups().add(0,favoritesGroup);
                    }
                    dataKey.setErrMsg(groupList1.getErrMsg());
                    dataKey.setResultCode(groupList1.getResultCode());
                    if (groupList1.getGroups() != null){
                        if (groupList1.getResultCode() == ErrorCode.SUCCESS){
                            BitdogInterface.getInstance().putData(key,groupList1);
                        }
                        dataKey.setKEY(key);
                    }
                } else {
                    dataKey.setResultCode(ErrorCode.EMPTY_RESULT);
                }
                EventBus.getDefault().post(dataKey);
                break;
            case BitdogCmd.UPDATE_GROUP_NAME_CMD:
                Map<String, Object> update = new HashMap<>();
                update.put("cate_id","");
                update.put("cate_name","");
                return httpRequest.commonRequest(update,jsonParams,HttpAPI.updateGroups,cmd,handleType,true);
            case BitdogCmd.DELETE_GROUP_CMD:
                Map<String, Object> delete = new HashMap<>();
                delete.put("cate_id","");
                return httpRequest.commonRequest(delete,jsonParams,HttpAPI.deleteGroups,cmd,handleType,true);
            case BitdogCmd.CREATE_GROUP_CMD:
                Map<String, Object> create = new HashMap<>();
                create.put("cat_name","");
                return httpRequest.commonRequest(create,jsonParams,HttpAPI.createGroup,cmd,handleType,true);
            case BitdogCmd.SET_DEVICE_NAME_CMD:
                Map<String, Object> params = new HashMap<>();
                params.put("device_id","");
                params.put("device_name","");
                return httpRequest.commonRequest(params,jsonParams,HttpAPI.setDeviceName,cmd,handleType,true);
            case BitdogCmd.UNBIND_DEVICE_CMD:
                Map<String, Object> unbind = new HashMap<>();
                unbind.put("device_id","");
                return httpRequest.commonRequest(unbind,jsonParams,HttpAPI.unbindDevice,cmd,handleType,true);
            case BitdogCmd.CHANGE_DEVICE_GROUP_CMD:
                Map<String, Object> changeDeviceGroup = new HashMap<>();
                changeDeviceGroup.put("device_id","");
                changeDeviceGroup.put("cate_id","");
                return httpRequest.commonRequest(changeDeviceGroup,jsonParams,HttpAPI.changeDeviceGroup,cmd,handleType,true);
            case BitdogCmd.GET_DEVICE_SHARE_USER_LIST_CMD:
                Map<String, Object> getMembers = new HashMap<>();
                getMembers.put("device_id","");
                Result result = httpRequest.commonRequest(getMembers,jsonParams,HttpAPI.getDeviceShareUserList,cmd, BitdogInterface.SYNC_HANDLE,true);
                MemberList memberList = new MemberList();
                memberList.setCmd(cmd);
                if (result == null){
                    memberList.setResultCode(ErrorCode.EMPTY_RESULT);
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(memberList);
                        return null;
                    }
                    return Result.getFastResult(ErrorCode.EMPTY_RESULT,cmd);
                }
                memberList.setResultCode(result.getExecResult());
                if (result.getExecResult() != ErrorCode.SUCCESS){
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        if (result.getObj() != null && result.getObj() instanceof String){
                            memberList.setErrMsg(String.valueOf(result.getObj()));
                        }
                        EventBus.getDefault().post(memberList);
                        return null;
                    }
                    return Result.getFastResult(result.getExecResult(),cmd).setObj(result.getObj());
                } else {
                    memberList.setResultCode(ErrorCode.RESPONSE_ERROR);
                    if (result.getObj() != null && result.getObj() instanceof JSONObject){
                        try {
                            JSONObject json = ((JSONObject) result.getObj());
                            JSONObject object = json.optJSONObject("data");
                            JSONArray array = object.optJSONArray("guessList");
                            Member bean = null;
                            JSONObject jsonBean = null;
                            List<Member> list = new ArrayList<>();
                            for (int j = 0; j < array.length(); j++) {
                                jsonBean = array.getJSONObject(j);
                                if (jsonBean != null) {
                                    bean = Member.parse(jsonBean);
                                    if (bean != null) {
                                        list.add(bean);
                                    }
                                }
                            }
                            memberList.setResultCode(ErrorCode.SUCCESS);
                            memberList.setMembers(list);
                            memberList.setDeviceVersion(object.getJSONObject("firmware").optString("version"));
                            memberList.setDeviceDownloadUrl(object.getJSONObject("firmware").optString("url"));
                            memberList.setChannelInfo(MemberList.ChannelInfo.parse(object.getJSONObject("channelInfo")));
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(memberList);
                        return null;
                    }
                    return Result.getFastResult(result.getExecResult(),cmd).setObj(result.getObj());
                }
            case BitdogCmd.BIND_DEVICE_CMD:
                Map<String, Object> bind = new HashMap<>();
                bind.put("device_id", "");
                bind.put("local_user", "");
                bind.put("local_pwd", "");
                bind.put("verify", "");
                return httpRequest.commonRequest(bind, jsonParams, HttpAPI.bindDevice, cmd, handleType, true,false);
            case BitdogCmd.SET_DEVICE_NAME_AND_GROUP_CMD:
                Map<String, Object> setNameGroup = new HashMap<String, Object>();
                setNameGroup.put("device_id", "");
                setNameGroup.put("cate_id", "");
                setNameGroup.put("device_name", "");
                return httpRequest.commonRequest(setNameGroup,jsonParams,HttpAPI.setDeviceNameAndGroup,cmd,handleType,true);
            case BitdogCmd.CHANGE_DEVICE_USER_PASSWORD_CMD:
                Map<String, Object> changeUserPassword = new HashMap<>();
                changeUserPassword.put("device_id", "");
                changeUserPassword.put("local_user", "");
                changeUserPassword.put("local_pwd", "");
                return httpRequest.commonRequest(changeUserPassword, jsonParams, HttpAPI.changeDeviceUserPassword, cmd, handleType, true);
            case BitdogCmd.BIND_SHARE_DEVICE_CMD:
                Map<String, Object> bindShareDevice = new HashMap<>();
                bindShareDevice.put("token", "");
                bindShareDevice.put("device_name", "");
                return httpRequest.commonRequest(bindShareDevice, jsonParams, HttpAPI.bindShareDevice, cmd, handleType, true);

            case BitdogCmd.BIND_OLD_SHARE_DEVICE_CMD:
                Map<String, Object> bindOldShareDevice = new HashMap<>();
                bindOldShareDevice.put("device_id", "");
                bindOldShareDevice.put("local_user", "");
                bindOldShareDevice.put("local_pwd", "");
                return httpRequest.commonRequest(bindOldShareDevice, jsonParams, HttpAPI.bindOldShareDevice, cmd, handleType, true);
            case BitdogCmd.REQUEST_SHARER_DEVICE_TOKEN_CMD:
                Map<String, Object> getShareDeviceToken = new HashMap<>();
                getShareDeviceToken.put("device_id", "");
                return httpRequest.commonRequest(getShareDeviceToken, jsonParams, HttpAPI.getShareDeviceToken, cmd, handleType, true);
            case BitdogCmd.UNBIND_SHARE_DEVICE_CMD:
                Map<String, Object> unbindShareDevice = new HashMap<>();
                unbindShareDevice.put("device_id", "");
                unbindShareDevice.put("user_ids", "");
                return httpRequest.commonRequest(unbindShareDevice, jsonParams, HttpAPI.unbindShareDevice, cmd, handleType, true);
            case BitdogCmd.GET_DEVICE_INFO_BY_SERIAL_NUMBER_CMD:
                int valid = 0;
                String equpId = "";
                String dev_name = "";
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonParams);
                    valid = jsonObject.optInt("valid",0);
                    equpId = jsonObject.optString("device_id");
                    dev_name = jsonObject.optString("dev_name");
                    if (valid == 1 && deviceCache != null && deviceCache.containsKey(equpId)){
                        SoftReference<DeviceInfoCache> softReference = deviceCache.get(equpId);
                        if (softReference != null && softReference.get() != null){
                            DeviceInfoCache deviceInfoCache = softReference.get();
                            if (deviceInfoCache != null){
                                if (deviceInfoCache.checkEqupInfo()){
                                    Result devResult = Result.getFastResult(ErrorCode.SUCCESS,cmd).setObj(deviceInfoCache.getData());
                                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                                        EventBus.getDefault().post(devResult);
                                        return null;
                                    }
                                    return devResult;
                                }
                            }
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                Map<String, Object> getDeviceInfoBySerial = new HashMap<>();
                getDeviceInfoBySerial.put("device_id","");
                Result getDeviceInfoBySerialResult = httpRequest.commonRequest(getDeviceInfoBySerial,jsonParams,HttpAPI.getDeviceInfoBySerialNumber,cmd, BitdogInterface.SYNC_HANDLE,true);
                SerialNumberDeviceInfoEvent serialNumberDeviceInfoEvent = new SerialNumberDeviceInfoEvent();
                serialNumberDeviceInfoEvent.setCmd(cmd);
                if (getDeviceInfoBySerialResult == null){
                    serialNumberDeviceInfoEvent.setResultCode(ErrorCode.EMPTY_RESULT);
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(serialNumberDeviceInfoEvent);
                        return null;
                    }
                    return Result.getFastResult(ErrorCode.EMPTY_RESULT,cmd);
                }
                serialNumberDeviceInfoEvent.setResultCode(getDeviceInfoBySerialResult.getExecResult());
                if (getDeviceInfoBySerialResult.getExecResult() != ErrorCode.SUCCESS){
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        if (getDeviceInfoBySerialResult.getObj() != null && getDeviceInfoBySerialResult.getObj() instanceof String){
                            serialNumberDeviceInfoEvent.setErrMsg(String.valueOf(getDeviceInfoBySerialResult.getObj()));
                        }
                        EventBus.getDefault().post(serialNumberDeviceInfoEvent);
                        return null;
                    }
                    return Result.getFastResult(getDeviceInfoBySerialResult.getExecResult(),cmd).setObj(getDeviceInfoBySerialResult.getObj());
                } else {
                    serialNumberDeviceInfoEvent.setResultCode(ErrorCode.RESPONSE_ERROR);
                    if (getDeviceInfoBySerialResult.getObj() != null && getDeviceInfoBySerialResult.getObj() instanceof JSONObject){
                        JSONObject json = ((JSONObject) getDeviceInfoBySerialResult.getObj());
                        if (json != null){
                            EqupInfo equpInfo = EqupInfo.parse(json.optJSONObject("data"));
                            if (equpInfo != null){
                                if (valid == 1){
                                    if (deviceCache != null){
                                        DeviceInfoCache deviceInfoCache = new DeviceInfoCache();
                                        deviceInfoCache.setData(json);
                                        deviceInfoCache.setTime(System.currentTimeMillis());
                                        deviceCache.put(equpInfo.getEqupId(),new SoftReference<DeviceInfoCache>(deviceInfoCache));
                                    }
                                }
                                serialNumberDeviceInfoEvent.setResultCode(ErrorCode.SUCCESS);
                                serialNumberDeviceInfoEvent.setDeviceInfo(equpInfo);
                                try {
                                    if (valid != 1 && jsonObject != null){
                                        int save = jsonObject.optInt("save");
                                        String account = jsonObject.optString("account");
                                        String password = jsonObject.optString("password");
                                        if (!TextUtils.isEmpty(dev_name)){
                                            equpInfo.setDeviceName(dev_name);
                                        }
                                        equpInfo.setLocalUser(account);
                                        equpInfo.setLocalPwd(password);
                                        if (save == 0){
                                            SerialLoginRecord serialLoginRecord = new SerialLoginRecord();
                                            serialLoginRecord.setDeviceId(equpInfo.getEqupId());
                                            serialLoginRecord.setAccount(account);
                                            serialLoginRecord.setPassword(password);
                                            DBAction.getInstance().insertSerialLoginRecord(serialLoginRecord);
                                            DBAction.getInstance().insertSNPlayerDevice(equpInfo);
                                        } else if (save == 1) {
                                            SerialLoginRecord serialLoginRecord = new SerialLoginRecord();
                                            serialLoginRecord.setDeviceId(equpInfo.getEqupId());
                                            serialLoginRecord.setAccount(account);
                                            DBAction.getInstance().insertSerialLoginRecord(serialLoginRecord);
                                            DBAction.getInstance().insertSNPlayerDevice(equpInfo);
                                        }
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(serialNumberDeviceInfoEvent);
                        return null;
                    }
                    return Result.getFastResult(getDeviceInfoBySerialResult.getExecResult(),cmd).setObj(getDeviceInfoBySerialResult.getObj());
                }
            case BitdogCmd.GET_DEMO_DEVICE_LIST_CMD:
                DemoDeviceList demos = new DemoDeviceList();
                demos.setCmd(cmd);
                if (handleType == BitdogInterface.ASYNC_HANDLE){
                    List<DemoInfo> demoInfoListLocale = DBAction.getInstance().queryDemoDevice();
                    if (demoInfoListLocale != null && demoInfoListLocale.size() > 0){
                        demos.setDemoInfos(demoInfoListLocale);
                        demos.setResultCode(ErrorCode.SUCCESS);
                        EventBus.getDefault().post(demos);
                    }
                }
                Result getDemos = httpRequest.commonRequest(null,jsonParams,HttpAPI.demoDeviceList,cmd, BitdogInterface.SYNC_HANDLE,false);
                if (getDemos == null){
                    demos.setResultCode(ErrorCode.EMPTY_RESULT);
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(demos);
                        return null;
                    }
                    return Result.getFastResult(ErrorCode.EMPTY_RESULT,cmd);
                }
                demos.setResultCode(getDemos.getExecResult());
                if (getDemos.getExecResult() != ErrorCode.SUCCESS){
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        if (getDemos.getObj() != null && getDemos.getObj() instanceof String){
                            demos.setErrMsg(String.valueOf(getDemos.getObj()));
                        }
                        EventBus.getDefault().post(demos);
                        return null;
                    }
                    return Result.getFastResult(getDemos.getExecResult(),cmd).setObj(getDemos.getObj());
                } else {
                    demos.setResultCode(ErrorCode.RESPONSE_ERROR);
                    if (getDemos.getObj() != null && getDemos.getObj() instanceof JSONObject){
                        JSONObject json = ((JSONObject) getDemos.getObj());
                        if (json != null){
                            List<DemoInfo> demoInfoList = DemoInfo.parse(json);
                            if (demoInfoList != null){
                                demos.setResultCode(ErrorCode.SUCCESS);
                                demos.setDemoInfos(demoInfoList);
                            }
                        }
                    }
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        if (demos.getDemoInfos() != null){
                            DBAction.getInstance().deleteAllDemoDevice();
                            for (DemoInfo demoInfo : demos.getDemoInfos()){
                                DBAction.getInstance().insertDemoDevice(demoInfo);
                            }
                        }
                        EventBus.getDefault().post(demos);
                        return null;
                    }
                    return Result.getFastResult(getDemos.getExecResult(),cmd).setObj(demos);
                }
            case BitdogCmd.GET_DEMO_PLAY_URL_CMD:
                Map<String, Object> getDemoUrl = new HashMap<>();
                getDemoUrl.put("device_name","");
                getDemoUrl.put("time","");
                getDemoUrl.put("type","");
                try {
                    JSONObject json = new JSONObject(jsonParams);
                    if (json != null){
                        Calendar mCalendar = Calendar.getInstance();
                        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                        json.put("time", String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY)));
                        jsonParams = json.toString();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
                Result demoResult = httpRequest.commonRequest(getDemoUrl,jsonParams,HttpAPI.getDemoDeviceUrl,cmd, BitdogInterface.SYNC_HANDLE,true);
                demoResult.setCmd(cmd);
                if (demoResult == null){
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.EMPTY_RESULT,cmd));
                        return null;
                    }
                    return Result.getFastResult(ErrorCode.EMPTY_RESULT,cmd);
                }
                demoResult.setExecResult(demoResult.getExecResult());
                if (demoResult.getExecResult() != ErrorCode.SUCCESS){
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(demoResult);
                        return null;
                    }
                    return demoResult;
                } else {
                    demoResult.setExecResult(ErrorCode.RESPONSE_ERROR);
                    if (demoResult.getObj() != null && demoResult.getObj() instanceof JSONObject){
                        JSONObject json = ((JSONObject) demoResult.getObj());
                        if (json != null){
                            String url = json.optJSONObject("data").optString("url");
                            demoResult.setObj(url);
                            demoResult.setExecResult(ErrorCode.SUCCESS);
                        }
                    }
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(demoResult);
                        return null;
                    }
                    return Result.getFastResult(demoResult.getExecResult(),cmd).setObj(demoResult.getObj());
                }
            case BitdogCmd.ADD_DEMO_DEVICE_PLAY_NUMBER_CMD:
                Map<String, Object> addNumber = new HashMap<>();
                addNumber.put("device_name", "");
                return httpRequest.commonRequest(addNumber, jsonParams, HttpAPI.addDemoDevicePlayNumber, cmd, handleType, true);
            case BitdogCmd.CHANGE_GROUP_ORDER_CMD:
                Map<String, Object> change_group_order = new HashMap<>();
                change_group_order.put("new_order", "");
                change_group_order.put("cate_id", "");
                return httpRequest.commonRequest(change_group_order, jsonParams, HttpAPI.changeDeviceGroupOrder, cmd, handleType, true);
            case BitdogCmd.CHANGE_DEVICE_ORDER_CMD:
                Map<String, Object> change_device_order = new HashMap<>();
                change_device_order.put("device_id", "");
                change_device_order.put("cate_id", "");
                change_device_order.put("order_num", "");
                return httpRequest.commonRequest(change_device_order, jsonParams, HttpAPI.changeDeviceOrder, cmd, handleType, true);
            case BitdogCmd.CHANGE_CHANNEL_ORDER_CMD:
                Map<String, Object> change_channel_order = new HashMap<>();
                change_channel_order.put("device_id", "");
                change_channel_order.put("channel", "");
                change_channel_order.put("order_num", "");
                return httpRequest.commonRequest(change_channel_order, jsonParams, HttpAPI.changeDeviceChannelOrder, cmd, handleType, true);
            case BitdogCmd.CHANGE_CHANNEL_NAME_CMD:
                Map<String, Object> change_channel_name_order = new HashMap<>();
                change_channel_name_order.put("device_id", "");
                change_channel_name_order.put("channel", "");
                change_channel_name_order.put("channel_name", "");
                return httpRequest.commonRequest(change_channel_name_order, jsonParams, HttpAPI.changeDeviceChannelName, cmd, handleType, true);
            case BitdogCmd.INSERT_DIRECT_DEVICE_CMD:
            case BitdogCmd.UPDATE_DIRECT_DEVICE_CMD:
            case BitdogCmd.DELETE_DIRECT_DEVICE_CMD:
                JSONObject reqObject = null;
                boolean paramsCheck = true;
                String old_url = "";
                try {
                    reqObject = new JSONObject(jsonParams);
                    if (reqObject != null){
                        old_url = reqObject.optString("old_url");
                        Iterator<String> keys = reqObject.keys();
                        if (keys != null){
                            String keyString ="";
                            while (keys.hasNext()){
                                keyString = keys.next();
                                if ("deviceId".equals(keyString)){
                                    continue;
                                }
                                if ("old_url".equals(keyString)){
                                    continue;
                                }
                                if (TextUtils.isEmpty(reqObject.optString(keyString))){
                                    paramsCheck = false;
                                    break;
                                }
                            }
                        }
                    } else {
                        paramsCheck = false;
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    paramsCheck = false;
                }
                if (!paramsCheck){
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
                        return null;
                    }
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
                } else {
                    DirectInfo directInfo = DirectInfo.parse(reqObject);
                    if (directInfo == null){
                        if (handleType == BitdogInterface.ASYNC_HANDLE){
                            EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
                            return null;
                        }
                        return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
                    }
//                    if (WorkContext.CONTEXT_APP == WorkContext.BITDOG_APP && (cmd == BitdogCmd.INSERT_DIRECT_DEVICE_CMD || cmd == BitdogCmd.UPDATE_DIRECT_DEVICE_CMD)){
//                        /**
//                         * 是否存在该序列号
//                         */
//                        Result deviceStatus = BitdogInterface.getInstance().exec(BitdogCmd.GET_DEVICE_INFO_BY_SERIAL_NUMBER_CMD,
//                                String.format("{\"device_id\":\"%s\",\"save\":%d,\"account\":\"%s\",\"password\":\"%s\"}",
//                                        directInfo.getDeviceId(),1,directInfo.getDeviceName(),directInfo.getPassword()),BitdogInterface.SYNC_HANDLE);
//                        if (deviceStatus != null && deviceStatus.getExecResult() == ErrorCode.SUCCESS){
//                            //设备存在，继续保存
//                        } else {
//                            if (deviceStatus == null){
//                                if (handleType == BitdogInterface.ASYNC_HANDLE){
//                                    EventBus.getDefault().post(Result.getFastResult(ErrorCode.HTTP_REQUEST_ERROR,cmd));
//                                    return null;
//                                }
//                                return Result.getFastResult(ErrorCode.HTTP_REQUEST_ERROR,cmd);
//                            } else {
//                                if (handleType == BitdogInterface.ASYNC_HANDLE){
//                                    EventBus.getDefault().post(Result.getFastResult(deviceStatus.getExecResult(),cmd).setObj(deviceStatus.getObj()));
//                                    return null;
//                                }
//                                return Result.getFastResult(deviceStatus.getExecResult(),cmd);
//                            }
//                        }
//                    }
                    EqupInfo equpInfo = directInfo.createEqupInfo();
                    if (equpInfo != null && TextUtils.isEmpty(equpInfo.getEqupId())){
                        equpInfo.setEqupId(equpInfo.getLocaleDeviceIp() + ":" + equpInfo.getLocaleDevicePort());
                    }
                    String user = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
                    if (cmd == BitdogCmd.INSERT_DIRECT_DEVICE_CMD){
                        boolean has = false;
                        if (equpInfo != null){
                            has = DBAction.getInstance().checkDirectDevice(equpInfo.getPrivateServer(),equpInfo.getPort() + "");
                        }
                        if (has){
                            if (handleType == BitdogInterface.ASYNC_HANDLE){
                                EventBus.getDefault().post(Result.getFastResult(ErrorCode.DIRECT_DEVICE_EXISTS,cmd));
                                return null;
                            }
                            return Result.getFastResult(ErrorCode.DIRECT_DEVICE_EXISTS,cmd);
                        }
                        DBAction.getInstance().deleteDevice(equpInfo,user);
                        DBAction.getInstance().insertDevice(equpInfo,user);
                        DBAction.getInstance().updatePlayCacheDirect(equpInfo,true);
                        EventBus.getDefault().post(UpdateRAMDataEvent.directDeviceDelete("{\"direct_url\":\"" + equpInfo.getDeviceConnectServer() + "\"}"));
                    } else if (cmd == BitdogCmd.UPDATE_DIRECT_DEVICE_CMD){
//                        DBAction.getInstance().deleteDevice(equpInfo,user);
//                        DBAction.getInstance().insertDevice(equpInfo2,user);
                        DBAction.getInstance().updateDeviceDirect(equpInfo,user,old_url);
                        DBAction.getInstance().updatePlayCacheDirectAccountPassword(equpInfo,user,old_url);
//                        DBAction.getInstance().updatePlayCacheDirect(equpInfo2,true);
                        JSONObject resetJson = new JSONObject();
                        try {
                            resetJson.put("direct_url",old_url);
                            resetJson.put("new_direct_url",equpInfo.getDeviceConnectServer());
                            resetJson.put("local_user",equpInfo.getLocalUser());
                            resetJson.put("local_pwd",equpInfo.getLocalPwd());
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        EventBus.getDefault().post(UpdateRAMDataEvent.directDeviceUpdatePassword(resetJson.toString()));
                    } else if (cmd == BitdogCmd.DELETE_DIRECT_DEVICE_CMD){
                        DBAction.getInstance().deleteDevice(equpInfo,user);
                        DBAction.getInstance().updatePlayCacheDirect(equpInfo,false);
                        EventBus.getDefault().post(UpdateRAMDataEvent.directDeviceDelete("{\"direct_url\":\"" + equpInfo.getDeviceConnectServer() + "\"}"));
                    }
                }
                if (handleType == BitdogInterface.ASYNC_HANDLE){
                    EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS,cmd));
                    return null;
                }
                return Result.getFastResult(ErrorCode.SUCCESS,cmd);
            case BitdogCmd.CHECK_DEVICE_CODE_CMD:
                Map<String, Object> checkDevice = new HashMap<>();
                checkDevice.put("device_id", "");
                return httpRequest.commonRequest(checkDevice, jsonParams, HttpAPI.checkDeviceCode, cmd, handleType, true);
            case BitdogCmd.CONNECT_WIFI_CMD:
                if (TextUtils.isEmpty(jsonParams)){
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
                    }
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
                }
                try {
                    JSONObject jsonObject1 = new JSONObject(jsonParams);
                    String account = jsonObject1.optString("account");
                    String password = jsonObject1.optString("password");
                    if (TextUtils.isEmpty(account)){
                        if (handleType == BitdogInterface.ASYNC_HANDLE){
                            EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
                        }
                        return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
                    }
                    WiFiHelper wiFiHelper = new WiFiHelper(BitdogInterface.getInstance().getApplicationContext());
                    int r = wiFiHelper.conn(account,password,false);
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(Result.getFastResult(r,cmd));
                    }
                    return Result.getFastResult(r,cmd);
                } catch (JSONException e){
                    e.printStackTrace();
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
                    }
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
                }
            case BitdogCmd.GRT_SERIAL_LOGIN_RECORD_CMD:
                List<SerialLoginRecord> records = DBAction.getInstance().querySerialLoginRecord();
                SerialLoginRecordEvent serialLoginRecordEvent = new SerialLoginRecordEvent();
                serialLoginRecordEvent.setCmd(cmd);
                serialLoginRecordEvent.setRecords(records);
                serialLoginRecordEvent.setResultCode(ErrorCode.SUCCESS);
                if (handleType == BitdogInterface.ASYNC_HANDLE){
                    EventBus.getDefault().post(serialLoginRecordEvent);
                }
                return Result.getFastResult(ErrorCode.SUCCESS,cmd).setObj(serialLoginRecordEvent);
            case BitdogCmd.DELETE_SERIAL_LOGIN_RECORD_CMD:
                if (TextUtils.isEmpty(jsonParams)){
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
                    }
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
                }
                try {
                    JSONObject jsonObject1 = new JSONObject(jsonParams);
                    if (jsonObject1 != null){
                        String deviceId = jsonObject1.optString("device_id");
                        if (!TextUtils.isEmpty(deviceId)){
                            DBAction.getInstance().delSerialLoginRecord(deviceId);
                            if (handleType == BitdogInterface.ASYNC_HANDLE){
                                EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS,cmd));
                            }
                            return Result.getFastResult(ErrorCode.SUCCESS,cmd);
                        }
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
                if (handleType == BitdogInterface.ASYNC_HANDLE){
                    EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
                }
                return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
            case BitdogCmd.INSERT_LOCALE_DEVICE_ACCOUNT:
                if (TextUtils.isEmpty(jsonParams)){
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
                    }
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
                }
                try {
                    JSONObject jsonObject1 = new JSONObject(jsonParams);
                    if (jsonObject1 != null){
                        String deviceId = jsonObject1.optString("device_id");
                        String account = jsonObject1.optString("account");
                        String password = jsonObject1.optString("password");
                        DBAction.getInstance().insertLocaleDevice(deviceId,account,password);
                        if (handleType == BitdogInterface.ASYNC_HANDLE){
                            EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS,cmd));
                        }
                        return Result.getFastResult(ErrorCode.SUCCESS,cmd);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                if (handleType == BitdogInterface.ASYNC_HANDLE){
                    EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
                }
                return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
            case BitdogCmd.QUERY_LOCALE_DEVICE_ACCOUNT:
                if (TextUtils.isEmpty(jsonParams)){
                    if (handleType == BitdogInterface.ASYNC_HANDLE){
                        EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
                    }
                    return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
                }
                try {
                    JSONObject jsonObject1 = new JSONObject(jsonParams);
                    if (jsonObject1 != null){
                        String deviceId = jsonObject1.optString("device_id");
                        String account = DBAction.getInstance().queryLocaleDeviceAccount(deviceId);
                        if (handleType == BitdogInterface.ASYNC_HANDLE){
                            EventBus.getDefault().post(Result.getFastResult(ErrorCode.SUCCESS,cmd).setObj(account));
                        }
                        return Result.getFastResult(ErrorCode.SUCCESS,cmd).setObj(account);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                if (handleType == BitdogInterface.ASYNC_HANDLE){
                    EventBus.getDefault().post(Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd));
                }
                return Result.getFastResult(ErrorCode.PARAMS_ERROR,cmd);
        }
        return null;
    }

    private GroupList getDeviceGroupList(int cmd){
        GroupList groupList = requestGroups(cmd);
        if (groupList == null)return null;
        if (groupList.getResultCode() != ErrorCode.SUCCESS){
            groupList.setCmd(cmd);
            return groupList;
        }
        if (groupList.getGroups() == null)return null;
        DeviceList deviceList = null;
        switch (cmd){
            case BitdogCmd.REQUEST_DEVICE_MANAGER_DEVICE_LIST_CMD:
                deviceList = requestDeviceList(BitdogCmd.REQUEST_DEVICE_LIST_CMD);
                break;
            case BitdogCmd.REQUEST_VR_PLAY_DEVICE_LIST_CMD:
                deviceList = requestDeviceList(BitdogCmd.REQUEST_VR_DEVICE_LIST_CMD);
                break;
            case BitdogCmd.REQUEST_NO_VR_PLAY_DEVICE_LIST_CMD:
                deviceList = requestDeviceList(BitdogCmd.REQUEST_NO_VR_DEVICE_LIST_CMD);
                break;
        }
        if (deviceList == null){
            return groupList;
        }
        if (deviceList.getResultCode() != ErrorCode.SUCCESS){
            groupList.setResultCode(deviceList.getResultCode());
            groupList.setErrMsg(deviceList.getErrMsg());
            groupList.setCmd(cmd);
            return groupList;
        }
        if (deviceList.getDevices() == null)return null;
        for (EqupInfo e: deviceList.getDevices()){
            if (e.getCateId() != null){
                for (Group g : groupList.getGroups()){
                    if (e.getCateId().equals(g.getGroupId())){
                        e.setCateName(g.getGroupName());
                        if (g.getDevices() == null){
                            g.setDevices(new ArrayList<EqupInfo>());
                        }
                        g.getDevices().add(e);
                    }
                }
            }
        }
        return groupList;
    }

    /**
     * 请求设备列表
     * @param cmd
     * @return
     */
    private DeviceList requestDeviceList(final int cmd){
        String api = HttpAPI.requestAllDevices;
        DeviceList deviceList = new DeviceList();
        switch (cmd){
            case BitdogCmd.REQUEST_DEVICE_LIST_CMD:
                api = HttpAPI.requestAllDevices;
                break;
            case BitdogCmd.REQUEST_VR_DEVICE_LIST_CMD:
                api = HttpAPI.requestVRDevices;
                break;
            case BitdogCmd.REQUEST_NO_VR_DEVICE_LIST_CMD:
                api = HttpAPI.requestNOVRDevices;
                break;
        }
        Result result = httpRequest.fastAction(api,null);
        deviceList.setCmd(cmd);
        if (result != null){
            deviceList.setResultCode(result.getExecResult());
            Object obj = result.getObj();
            if (obj != null){
                if (result.getExecResult() == ErrorCode.SUCCESS){
                    if (obj instanceof JSONObject) {
                        JSONArray array = ((JSONObject) obj).optJSONArray("data");
                        if (array != null) {
                            final List<EqupInfo> list = new ArrayList<EqupInfo>();
                            JSONObject arr = null;
                            EqupInfo e = null;
                            for (int i = 0; i < array.length(); i++) {
                                arr = array.optJSONObject(i);
                                if (arr != null) {
                                    e = EqupInfo.parse(arr);
                                    if (e != null) {
                                        if (e.getDeviceDetatilType() != null && e.getDeviceDetatilType().contains("IPC")){
                                            if (e.getInfoEntitys() != null && e.getInfoEntitys().size() > 0){
                                                e.getInfoEntitys().clear();
                                            }
                                        }
                                        if (e.getInfoEntitys() != null){
                                            Collections.sort(e.getInfoEntitys());
                                        }
                                        list.add(e);
                                    }
                                }
                            }
                            deviceList.setResultCode(ErrorCode.SUCCESS);
                            if (list.size() > 0){
                                BitdogInterface.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
                                        if (!TextUtils.isEmpty(userId)){
                                            if (WorkContext.firstStart2DelCacheDevice){
                                                WorkContext.firstStart2DelCacheDevice = false;
                                                DBAction.getInstance().deleteAllCacheDevice(userId);
                                            }
                                            DBAction.getInstance().insertDeviceList(list,userId);
                                            if (cmd == BitdogCmd.REQUEST_DEVICE_LIST_CMD){
                                                DBAction.getInstance().clearCache(list,userId);
                                            }
                                            List<EqupInfo> favorites = DBAction.getInstance().queryFavoritesPlayerDevice();
                                            if (favorites != null && favorites.size() > 0){
                                                List<EqupInfo> deleteList = new ArrayList<>();
                                                deleteList.addAll(favorites);
                                                for (EqupInfo device : favorites){
                                                    for (EqupInfo equpInfo : list){
                                                        if (device.getEqupId().equals(device.getEqupId())){
                                                            deleteList.remove(device);
                                                        }
                                                    }
                                                }
                                                if (deleteList.size() > 0){
                                                    for (EqupInfo device : deleteList){
                                                        DBAction.getInstance().deleteFavoritesPlayerDevice(device.getEqupId());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }, ThreadWrapper.THREAD_IO);
                            } else {
                                BitdogInterface.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (cmd == BitdogCmd.REQUEST_DEVICE_LIST_CMD){
                                            DBAction.getInstance().clearPlayCache();
                                        }
                                    }
                                }, ThreadWrapper.THREAD_IO);
                            }
                            Collections.sort(list);
                            deviceList.setDevices(list);
                            return deviceList;
                        }
                    }
                } else if (obj instanceof String){
                    deviceList.setErrMsg(String.valueOf(obj));
                }
            }
        }
        deviceList.setResultCode(ErrorCode.EMPTY_RESULT);
        return deviceList;
    }

    /**
     * 请求分组
     * @param cmd
     * @return
     */
    private GroupList requestGroups(int cmd){
        GroupList groupList = new GroupList();
        Result result = httpRequest.fastAction(HttpAPI.requestGroups,null);
        if (result != null){
            groupList.setResultCode(result.getExecResult());
            Object obj = result.getObj();
            if (obj != null){
                if (result.getExecResult() == ErrorCode.SUCCESS){
                    if (obj instanceof JSONObject) {
                        JSONArray jarray = ((JSONObject)obj).optJSONArray("data");
                        Group group = null;
                        final List<Group> groups = new ArrayList<>();
                        for (int i = 0; i < jarray.length(); i++) {
                            group = Group.parse(jarray.optJSONObject(i));
                            if (group != null){
                                groups.add(group);
                            }
                        }
                        if (groups.size() > 0){
                            BitdogInterface.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    String userId = StringCache.getInstance().queryCache(WorkContext.USER_ID_KEY,"");
                                    if (!TextUtils.isEmpty(userId)){
                                        if (WorkContext.firstStart2DelCacheDGroup){
                                            WorkContext.firstStart2DelCacheDGroup = false;
                                            DBAction.getInstance().deleteAllGroup();
                                        }
                                        DBAction.getInstance().insertGroups(groups,userId);
                                    }
                                }
                            }, ThreadWrapper.THREAD_IO);
                        }
                        Collections.sort(groups);
                        groupList.setGroups(groups);
                    }
                }else if (obj instanceof String){
                    groupList.setErrMsg(String.valueOf(obj));
                }
            }
        }
        return groupList;
    }
}
