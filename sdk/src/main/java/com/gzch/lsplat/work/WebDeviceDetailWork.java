package com.gzch.lsplat.work;

import android.text.TextUtils;

import com.gzch.lsplat.work.api.HttpAPI;
import com.gzch.lsplat.work.cmd.BitdogCmd;
import com.gzch.lsplat.work.mode.AlarmDeviceInfo;
import com.gzch.lsplat.work.mode.CloudDeviceBean;
import com.gzch.lsplat.work.mode.CloudVideoBean;
import com.gzch.lsplat.work.mode.EventMsg;
import com.gzch.lsplat.work.mode.OrderFreeIPItem;
import com.gzch.lsplat.work.mode.OrderItem;
import com.gzch.lsplat.work.mode.VersionBean;
import com.gzch.lsplat.work.mode.event.AlarmDeviceList;
import com.gzch.lsplat.work.mode.event.CloudDeviceEvent;
import com.gzch.lsplat.work.mode.event.CloudVideoEvent;
import com.gzch.lsplat.work.mode.event.EventMsgEvent;
import com.gzch.lsplat.work.mode.event.JpushSingleEventMsg;
import com.gzch.lsplat.work.mode.event.OrderItemEvent;
import com.gzch.lsplat.work.mode.event.OrderItemFreeIPEvent;
import com.gzch.lsplat.work.net.HttpRequest;
import com.longse.lsapc.lsacore.BitdogInterface;
import com.longse.lsapc.lsacore.interf.LSCoreInterface;
import com.longse.lsapc.lsacore.mode.Result;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LY on 2018/1/31.
 */

public class WebDeviceDetailWork implements LSCoreInterface.Worker {

    private HttpRequest httpRequest = new HttpRequest();

    @Override
    public void init() {

    }

    @Override
    public boolean isMyCmd(int cmd) {
        if (cmd == BitdogCmd.EVENT_MSG_DEVICES_CMD) {
            return true;
        } else if (cmd == BitdogCmd.CLOUD_DEVICES_CMD) {
            return true;
        } else if (cmd == BitdogCmd.CLOUD_VIDEOS_CMD) {
            return true;
        } else if (cmd == BitdogCmd.CLOUD_VIDEOS_LOCATION_CMD) {
            return true;
        } else if (cmd == BitdogCmd.ORDER_TYPES_CMD) {
            return true;
        } else if (cmd == BitdogCmd.EVENT_MSG_SINGLE_CMD) {
            return true;
        } else if (cmd == BitdogCmd.DELETE_EVENT_MSG_CMD) {
            return true;
        } else if (cmd == BitdogCmd.CHECK_APP_VERSION_CMD) {
            return true;
        } else if (cmd == BitdogCmd.APPLY_UNBIND_CMD) {
            return true;
        } else if (cmd == BitdogCmd.EVENT_MSG_AMID_CMD) {
            return true;
        }else if (cmd == BitdogCmd.EVENT_MSG_BY_ID_CMD) {
            return true;
        } else if (cmd == BitdogCmd.CHANGE_REPLAY_CODE_TYPE_CMD) {
            return true;
        } else if (cmd == BitdogCmd.CHANGE_REPLAY_VIDEO_TYPE_CMD) {
            return true;
        } else if (cmd == BitdogCmd.ORDER_FREEIP_TYPES_CMD) {
            return true;
        } else return cmd == BitdogCmd.SEND_REGISTER_EMAIL_CMD;
    }

    @Override
    public Result worked(int cmd, String jsonParams, int handleType) {
        switch (cmd) {
            case BitdogCmd.EVENT_MSG_DEVICES_CMD:
                AlarmDeviceList alarmDeviceList = requestAlarmDevices(cmd);
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(alarmDeviceList);
                } else {
                    return Result.getFastResult(ErrorCode.SUCCESS, cmd).setObj(alarmDeviceList);
                }
                break;
            case BitdogCmd.CLOUD_DEVICES_CMD:
                CloudDeviceEvent cloudDeviceEvent = requestCloudDevices(cmd);
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(cloudDeviceEvent);
                } else {
                    return Result.getFastResult(ErrorCode.SUCCESS, cmd).setObj(cloudDeviceEvent);
                }
                break;
            case BitdogCmd.EVENT_MSG_SINGLE_CMD:
                Map<String, Object> searchMsgById = new HashMap<>();
                searchMsgById.put("device_id", "");
                searchMsgById.put("timeStamp", "");
                searchMsgById.put("am_id", "");
                EventMsgEvent eventMsgEvent = requestEventMsgById(cmd, searchMsgById, jsonParams);

                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(eventMsgEvent);
                } else {
                    return Result.getFastResult(ErrorCode.SUCCESS, cmd).setObj(eventMsgEvent);
                }
                break;
            case BitdogCmd.EVENT_MSG_BY_ID_CMD:
                Map<String, Object> eventMsgById = new HashMap<>();
                eventMsgById.put("device_id", "");
                eventMsgById.put("am_id", "");
                EventMsgEvent eventMsg = requestMsgById(cmd, eventMsgById, jsonParams);

                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(eventMsg);
                } else {
                    return Result.getFastResult(ErrorCode.SUCCESS, cmd).setObj(eventMsg);
                }
                break;
            case BitdogCmd.EVENT_MSG_AMID_CMD:
                Map<String, Object> searchMsgByAmId = new HashMap<>();
                searchMsgByAmId.put("am_id", "");
                JpushSingleEventMsg msgEvent = requestEventMsgByAmId(cmd, searchMsgByAmId, jsonParams);
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(msgEvent);
                } else {
                    return Result.getFastResult(ErrorCode.SUCCESS, cmd).setObj(msgEvent);
                }
                break;

            case BitdogCmd.CLOUD_VIDEOS_CMD:
                Map<String, Object> searchCloudVideos = new HashMap<>();
                searchCloudVideos.put("device_id", "");
                searchCloudVideos.put("vl_id", "");
                searchCloudVideos.put("channel", "");
                CloudVideoEvent cloudVideoEvent = requestCloudVideos(cmd, searchCloudVideos, jsonParams);

                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(cloudVideoEvent);
                } else {
                    return Result.getFastResult(ErrorCode.SUCCESS, cmd).setObj(cloudVideoEvent);
                }
                break;
            case BitdogCmd.CLOUD_VIDEOS_LOCATION_CMD:
                Map<String, Object> locationCloudVideos = new HashMap<>();
                locationCloudVideos.put("device_id", "");
                locationCloudVideos.put("time", "");
                locationCloudVideos.put("channel", "");
                CloudVideoEvent cloudVideoEvent1 = requestLocationCloudVideos(cmd, locationCloudVideos, jsonParams);

                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(cloudVideoEvent1);
                } else {
                    return Result.getFastResult(ErrorCode.SUCCESS, cmd).setObj(cloudVideoEvent1);
                }
                break;

            case BitdogCmd.ORDER_TYPES_CMD:
                Map<String, Object> orderTypes = new HashMap<>();
                orderTypes.put("device_id", "");
                orderTypes.put("channel", "");
                OrderItemEvent orderItemEvent = requestOrderTypes(cmd, orderTypes, jsonParams);

                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(orderItemEvent);
                } else {
                    return Result.getFastResult(ErrorCode.SUCCESS, cmd).setObj(orderItemEvent);
                }
                break;

            case BitdogCmd.ORDER_FREEIP_TYPES_CMD:
                Map<String, Object> orderFreeIPTypes = new HashMap<>();
                orderFreeIPTypes.put("device_id", "");
                orderFreeIPTypes.put("channel", "");
                OrderItemFreeIPEvent orderItemFreeIPEvent = requestFreeIPOrderTypes(cmd, orderFreeIPTypes, jsonParams);

                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(orderItemFreeIPEvent);
                } else {
                    return Result.getFastResult(ErrorCode.SUCCESS, cmd).setObj(orderItemFreeIPEvent);
                }
                break;

            case BitdogCmd.DELETE_EVENT_MSG_CMD:
                Map<String, Object> delete = new HashMap<>();
                delete.put("am_ids", "");
                delete.put("null_data", "");
                return httpRequest.commonRequest(delete, jsonParams, HttpAPI.delEventMsg, cmd, handleType, true);

            case BitdogCmd.CHECK_APP_VERSION_CMD:
                VersionBean versionBean = requestVersion(cmd);
                if (handleType == BitdogInterface.ASYNC_HANDLE) {
                    EventBus.getDefault().post(versionBean);
                } else {
                    return Result.getFastResult(ErrorCode.SUCCESS, cmd).setObj(versionBean);
                }
                break;
            case BitdogCmd.CHANGE_REPLAY_CODE_TYPE_CMD:
                Map<String, Object> codeType = new HashMap<>();
                codeType.put("device_id", "");
                codeType.put("channel", "");
                codeType.put("data_rate", "");
                return httpRequest.commonRequest(codeType, jsonParams, HttpAPI.changeDeviceRepalyDataRate, cmd, handleType, true);
            case BitdogCmd.CHANGE_REPLAY_VIDEO_TYPE_CMD:
                Map<String, Object> videoType = new HashMap<>();
                videoType.put("device_id", "");
                videoType.put("channel", "");
                videoType.put("video_type", "");
                return httpRequest.commonRequest(videoType, jsonParams, HttpAPI.changeDeviceRepalyVideoType, cmd, handleType, true);
            case BitdogCmd.APPLY_UNBIND_CMD:
                return httpRequest.postUnbindApply(cmd, jsonParams, handleType);
            case BitdogCmd.SEND_REGISTER_EMAIL_CMD:
                Map<String, Object> sendReEmail = new HashMap<>();
                sendReEmail.put("user_name","");
                return httpRequest.commonRequest(sendReEmail,jsonParams, HttpAPI.sendRegisterEmailCode,cmd,handleType,true);
        }

        return null;
    }

    /**
     * 请求报警设备列表
     *
     * @param cmd
     * @return
     */
    private AlarmDeviceList requestAlarmDevices(int cmd) {
        AlarmDeviceList alarmDeviceList = new AlarmDeviceList();
        Result result = httpRequest.fastAction(HttpAPI.getEventMsgDeviceList, null);
        alarmDeviceList.setCmd(cmd);
        if (result != null) {
            alarmDeviceList.setResultCode(result.getExecResult());
            Object obj = result.getObj();
            if (obj != null) {
                if (result.getExecResult() == ErrorCode.SUCCESS) {
                    if (obj instanceof JSONObject) {
                        JSONArray jsonArray = ((JSONObject) obj).optJSONArray("data");
                        AlarmDeviceInfo aLarmDeviceInfo = null;
                        final List<AlarmDeviceInfo> aLarmDevices = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            aLarmDeviceInfo = AlarmDeviceInfo.parse(jsonArray.optJSONObject(i));
                            if (aLarmDeviceInfo != null) {
                                aLarmDevices.add(aLarmDeviceInfo);
                            }
                        }
                        alarmDeviceList.setAlarmDeviceLists(aLarmDevices);
                    }
                } else if (obj instanceof String) {
                    alarmDeviceList.setErrMsg(String.valueOf(obj));
                }
            }
        }
        return alarmDeviceList;
    }

    /**
     * 请求云设备列表
     *
     * @param cmd
     * @return
     */
    private CloudDeviceEvent requestCloudDevices(int cmd) {
        CloudDeviceEvent cloudDeviceEvent = new CloudDeviceEvent();
        Result result = httpRequest.fastAction(HttpAPI.actionGetAllDevcieCloud, null);
        cloudDeviceEvent.setCmd(cmd);
        if (result != null) {
            cloudDeviceEvent.setResultCode(result.getExecResult());
            Object obj = result.getObj();
            if (obj != null) {
                if (result.getExecResult() == ErrorCode.SUCCESS) {
                    if (obj instanceof JSONObject) {
                        JSONArray jsonArray = ((JSONObject) obj).optJSONArray("data");
                        CloudDeviceBean cloudDeviceBean = null;
                        final List<CloudDeviceBean> cloudDeviceBeenList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            cloudDeviceBean = CloudDeviceBean.parse(jsonArray.optJSONObject(i));
                            if (cloudDeviceBean != null) {
                                cloudDeviceBeenList.add(cloudDeviceBean);
                            }
                        }
                        cloudDeviceEvent.setCloudDevcieBeans(cloudDeviceBeenList);
                    }
                } else if (obj instanceof String) {
                    cloudDeviceEvent.setErrMsg(String.valueOf(obj));
                }
            }
        }
        return cloudDeviceEvent;
    }

    /**
     * 请求单台云设备云端录像
     *
     * @param cmd
     * @param params
     * @param jsonParams
     * @return
     */
    private CloudVideoEvent requestCloudVideos(int cmd, Map<String, Object> params, String jsonParams) {
        CloudVideoEvent cloudVideoEvent = new CloudVideoEvent();
        Result result = httpRequest.commonRequest(params, jsonParams, HttpAPI.actionGetDeviceCloudStorageVideoList, cmd, BitdogInterface.SYNC_HANDLE, true);
        cloudVideoEvent.setCmd(cmd);
        if (result != null) {
            cloudVideoEvent.setResultCode(result.getExecResult());
            Object obj = result.getObj();
            if (obj != null) {
                if (result.getExecResult() == ErrorCode.SUCCESS) {
                    if (obj instanceof JSONObject) {
                        JSONArray jsonArray = ((JSONObject) obj).optJSONArray("data");
                        CloudVideoBean cloudVideoBean = null;
                        final List<CloudVideoBean> cloudVideoBeenList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            cloudVideoBean = CloudVideoBean.parse(jsonArray.optJSONObject(i));
                            if (cloudVideoBean != null) {
                                cloudVideoBeenList.add(cloudVideoBean);
                            }
                        }
                        cloudVideoEvent.setCloudVideoBeans(cloudVideoBeenList);
                    }
                } else if (obj instanceof String) {
                    cloudVideoEvent.setErrMsg(String.valueOf(obj));
                }
            }
        }
        return cloudVideoEvent;
    }

    /**
     * 请求定位查询云视频
     * @param cmd
     * @param params
     * @param jsonParams
     * @return
     */
    private CloudVideoEvent requestLocationCloudVideos(int cmd, Map<String, Object> params, String jsonParams) {
        CloudVideoEvent cloudVideoEvent = new CloudVideoEvent();
        Result result = httpRequest.commonRequest(params, jsonParams, HttpAPI.actionGetDevcieCloudByTime, cmd, BitdogInterface.SYNC_HANDLE, true);
        cloudVideoEvent.setCmd(cmd);
        if (result != null) {
            cloudVideoEvent.setResultCode(result.getExecResult());
            Object obj = result.getObj();
            if (obj != null) {
                if (result.getExecResult() == ErrorCode.SUCCESS) {
                    if (obj instanceof JSONObject) {
                        JSONArray jsonArray = ((JSONObject) obj).optJSONArray("data");
                        CloudVideoBean cloudVideoBean = null;
                        final List<CloudVideoBean> cloudVideoBeenList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            cloudVideoBean = CloudVideoBean.parse(jsonArray.optJSONObject(i));
                            if (cloudVideoBean != null) {
                                cloudVideoBeenList.add(cloudVideoBean);
                            }
                        }
                        cloudVideoEvent.setCloudVideoBeans(cloudVideoBeenList);
                    }
                } else if (obj instanceof String) {
                    cloudVideoEvent.setErrMsg(String.valueOf(obj));
                }
            }
        }
        return cloudVideoEvent;
    }

    /**
     * 请求Bitdog订单列表
     * @param cmd
     * @param params
     * @param jsonParams
     * @return
     */
    private OrderItemEvent requestOrderTypes(int cmd, Map<String, Object> params, String jsonParams) {
        OrderItemEvent orderItemEvent = new OrderItemEvent();
        Result result = httpRequest.commonRequest(params, jsonParams, HttpAPI.actionGetOrderType, cmd, BitdogInterface.SYNC_HANDLE, true);
        orderItemEvent.setCmd(cmd);
        if (result != null) {
            orderItemEvent.setResultCode(result.getExecResult());
            Object obj = result.getObj();
            if (obj != null) {
                if (result.getExecResult() == ErrorCode.SUCCESS) {
                    if (obj instanceof JSONObject) {
                        List<OrderItem> orderList = new ArrayList<OrderItem>();
                        JSONObject dataObj = null;
                        String timeInfo = null;
                        String isActive = null;
                        try {
                            dataObj = ((JSONObject) obj).optJSONObject("data");
                            timeInfo = dataObj.optString("endTime");
                            isActive = dataObj.optString("isActive");
                            JSONArray typelist = dataObj.getJSONArray("orderTypeList");
                            for (int i = 0; i < typelist.length(); i++) {
                                OrderItem orderItem = OrderItem.parse(String.valueOf(typelist.get(i)));
                                orderList.add(orderItem);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        orderItemEvent.setOrderItems(timeInfo, isActive, orderList);
                    }
                } else if (obj instanceof String) {
                    orderItemEvent.setErrMsg(String.valueOf(obj));
                }
            }
        }
        return orderItemEvent;
    }

    /**
     * 请求FreeIP订单列表
     * @param cmd
     * @param params
     * @param jsonParams
     * @return
     */
    private OrderItemFreeIPEvent requestFreeIPOrderTypes(int cmd, Map<String, Object> params, String jsonParams) {
        OrderItemFreeIPEvent orderItemFreeIPEvent = new OrderItemFreeIPEvent();
        Result result = httpRequest.commonRequest(params, jsonParams, HttpAPI.actionGetFreeipOrderType, cmd, BitdogInterface.SYNC_HANDLE, true);
        orderItemFreeIPEvent.setCmd(cmd);
        if (result != null) {
            orderItemFreeIPEvent.setResultCode(result.getExecResult());
            Object obj = result.getObj();
            if (obj != null) {
                if (result.getExecResult() == ErrorCode.SUCCESS) {
                    if (obj instanceof JSONObject) {
                        List<OrderFreeIPItem> orderFreeIPList = new ArrayList<OrderFreeIPItem>();
                        JSONObject dataObj = null;
                        String timeInfo = null;
                        String isActive = null;
                        try {
                            dataObj = ((JSONObject) obj).optJSONObject("data");
                            timeInfo = dataObj.optString("endTime");
                            isActive = dataObj.optString("isActive");
                            JSONArray typelist = dataObj.getJSONArray("orderTypeList");
                            for (int i = 0; i < typelist.length(); i++) {
                                OrderFreeIPItem orderFreeIPItem = OrderFreeIPItem.parse(String.valueOf(typelist.get(i)));
                                orderFreeIPList.add(orderFreeIPItem);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        orderItemFreeIPEvent.setOrderFreeIPItems(timeInfo, isActive, orderFreeIPList);
                    }
                } else if (obj instanceof String) {
                    orderItemFreeIPEvent.setErrMsg(String.valueOf(obj));
                }
            }
        }
        return orderItemFreeIPEvent;
    }

    /**
     * Bitdog 请求单个设备报警信息
     * @param cmd
     * @param params
     * @param jsonParams
     * @return
     */
    private EventMsgEvent requestEventMsgById(int cmd, Map<String, Object> params, String jsonParams) {
        EventMsgEvent eventMsgEvent = new EventMsgEvent();
        Result result = httpRequest.commonRequest(params, jsonParams, HttpAPI.getEventMsgById, cmd, BitdogInterface.SYNC_HANDLE, true);
        if (result != null) {
            eventMsgEvent.setResultCode(result.getExecResult());
            eventMsgEvent.setCmd(result.getCmd());
            Object obj = result.getObj();
            if (obj != null) {
                if (result.getExecResult() == ErrorCode.SUCCESS) {
                    if (obj instanceof JSONObject) {
                        JSONArray jsonArray = ((JSONObject) obj).optJSONArray("data");
                        EventMsg eventMsg = null;
                        final List<EventMsg> eventMsgList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            eventMsg = EventMsg.parse(jsonArray.optJSONObject(i));
                            if (eventMsg != null) {
                                eventMsgList.add(eventMsg);
                            }
                        }
                        eventMsgEvent.setEventMsgs(eventMsgList);
                    }
                } else if (obj instanceof String) {
                    eventMsgEvent.setErrMsg(String.valueOf(obj));
                }
            }
        }
        return eventMsgEvent;
    }

    /**
     * FreeIP 请求单个设备报警信息
     * @param cmd
     * @param params
     * @param jsonParams
     * @return
     */
    private EventMsgEvent requestMsgById(int cmd, Map<String, Object> params, String jsonParams) {//{"device_id":"","am_id":""}
        EventMsgEvent eventMsgEvent = new EventMsgEvent();
        String am_id = "";
        String deviceId = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonParams);
            deviceId = jsonObject.optString("device_id");
            am_id = jsonObject.optString("am_id");
        } catch (Exception e){
            e.printStackTrace();
        }
        Result result = httpRequest.commonRequest(params, jsonParams, HttpAPI.getEventMsgByIds, cmd, BitdogInterface.SYNC_HANDLE, true);
        if (result != null) {
            eventMsgEvent.setResultCode(result.getExecResult());
            eventMsgEvent.setCmd(result.getCmd());
            Object obj = result.getObj();
            if (obj != null) {
                if (result.getExecResult() == ErrorCode.SUCCESS) {
                    if (obj instanceof JSONObject) {
                        JSONArray jsonArray = ((JSONObject) obj).optJSONArray("data");
                        EventMsg eventMsg = null;
                        final List<EventMsg> eventMsgList = new ArrayList<>();
                        final List<String> eventMsgDateList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            eventMsg = EventMsg.parse(jsonArray.optJSONObject(i));
                            if (eventMsg != null) {
                                if (!eventMsgDateList.contains(eventMsg.getDateTimeString())){
                                    if (!TextUtils.isEmpty(eventMsg.getDateTimeString())){
                                        eventMsgDateList.add(eventMsg.getDateTimeString());
                                    }
                                }
                                eventMsg.setSortType(EventMsg.EVENT_MSG_CONTENT);
                                eventMsgList.add(eventMsg);
                            }
                        }
                        for (String time : eventMsgDateList){
                            eventMsg = new EventMsg();
                            eventMsg.setSortType(EventMsg.EVENT_MSG_DATE_TITLE);
                            eventMsg.setCreate_time(time + "000");
                            Calendar calendar = Calendar.getInstance();
                            try {
                                calendar.setTimeInMillis(Long.valueOf(eventMsg.getCreate_time()));
                            } catch (Exception e){
                                e.printStackTrace();
                                continue;
                            }
                            calendar.set(Calendar.HOUR_OF_DAY,23);
                            calendar.set(Calendar.MINUTE,59);
                            calendar.set(Calendar.SECOND,59);
                            calendar.set(Calendar.MILLISECOND,999);
                            eventMsg.setTitleTime(String.valueOf(calendar.getTimeInMillis()));
                            eventMsgList.add(eventMsg);
                        }
                        Collections.sort(eventMsgList, new Comparator<EventMsg>() {
                            @Override
                            public int compare(EventMsg o1, EventMsg o2) {
                                if (o1 == null || o2 == null)return 0;
                                String time1 = o1.getComparaTime();
                                String time2 = o2.getComparaTime();
                                if (TextUtils.isEmpty(time1) || TextUtils.isEmpty(time2))return 0;
                                try {
                                    long t1 = Long.valueOf(time1);
                                    long t2 = Long.valueOf(time2);
                                    if (t1 > t2)return -1;
                                    if (t1 < t2)return 1;
                                    if (t1 == t2)return 0;
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                return 0;
                            }
                        });
                        if (!"0".equals(am_id) && eventMsgList.size() > 0){
                            try {
                                if (BitdogInterface.getInstance().getData(WorkContext.EVENT_MSG_DATA_KEY + cmd + deviceId) != null){
                                    SoftReference<List<EventMsg>> dataCache = (SoftReference<List<EventMsg>>) BitdogInterface.getInstance().getData(WorkContext.EVENT_MSG_DATA_KEY + cmd + deviceId);
                                    if (dataCache != null){
                                        List<EventMsg> eventMsgs = dataCache.get();
                                        if (eventMsgs != null){
                                            int size = eventMsgs.size();
                                            if (eventMsgs.get(size - 1) != null){
                                                String timeEnd = eventMsgs.get(size - 1).getDateTimeString();
                                                String timeStart = eventMsgList.get(0).getDateTimeString();
                                                if (timeEnd != null && timeEnd.equals(timeStart)){
                                                    eventMsgList.remove(0);
                                                }
                                                eventMsgs.addAll(eventMsgList);
                                                eventMsgList.clear();
                                                eventMsgList.addAll(eventMsgs);
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        BitdogInterface.getInstance().putData(WorkContext.EVENT_MSG_DATA_KEY + cmd + deviceId,new SoftReference<List<EventMsg>>(eventMsgList));
                        eventMsgEvent.setEventMsgs(eventMsgList);
                    }
                } else if (obj instanceof String) {
                    eventMsgEvent.setErrMsg(String.valueOf(obj));
                }
            }
        }
        return eventMsgEvent;
    }

    private JpushSingleEventMsg requestEventMsgByAmId(int cmd, Map<String, Object> params, String jsonParams) {
        JpushSingleEventMsg eventMsgEvent = new JpushSingleEventMsg();
        Result result = httpRequest.commonRequest(params, jsonParams, HttpAPI.getEventMsgByAmId, cmd, BitdogInterface.SYNC_HANDLE, true);
        if (result != null) {
            eventMsgEvent.setResultCode(result.getExecResult());
            eventMsgEvent.setCmd(result.getCmd());
            Object obj = result.getObj();
            if (obj != null) {
                if (result.getExecResult() == ErrorCode.SUCCESS) {
                    if (obj instanceof JSONObject) {
                        JSONObject job = ((JSONObject) obj).optJSONObject("data");
                        EventMsg eventMsg;
                        eventMsg = EventMsg.parse(job);
                        eventMsgEvent.setEventMsgs(eventMsg);
                    }
                } else if (obj instanceof String) {
                    eventMsgEvent.setErrMsg(String.valueOf(obj));
                }
            }
        }
        return eventMsgEvent;
    }

    /**
     * 版本检测
     *
     * @param cmd
     * @return
     */
    private VersionBean requestVersion(int cmd) {
        VersionBean versionBean = new VersionBean();
        Result result = httpRequest.fastAction(HttpAPI.getServerVersionUrl, null);
        versionBean.setCmd(cmd);
        if (result != null) {
            versionBean.setResultCode(result.getExecResult());
            Object obj = result.getObj();
            if (obj != null) {
                if (result.getExecResult() == ErrorCode.SUCCESS) {
                    JSONObject dataObj = ((JSONObject) obj).optJSONObject("data");
                    versionBean = VersionBean.paseVersion(dataObj);
                } else if (obj instanceof String) {
                    versionBean.setErrMsg(String.valueOf(obj));
                }
            }
        }
        return versionBean;
    }


}
