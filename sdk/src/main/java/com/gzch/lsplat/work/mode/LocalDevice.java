package com.gzch.lsplat.work.mode;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocalDevice implements Parcelable {

	private String DEVICE_TYPE;

	private int DevChannelNum;
	
	private String deviceId ;
	
	private String deviceIp ;
	
	private int devicePort ;
	
	private String deviceAccount;
	
	private String devicePwd;

	public String getDEVICE_TYPE() {
		return DEVICE_TYPE;
	}

	public void setDEVICE_TYPE(String DEVICE_TYPE) {
		this.DEVICE_TYPE = DEVICE_TYPE;
	}

	public int getDevChannelNum() {
		return DevChannelNum;
	}

	public void setDevChannelNum(int devChannelNum) {
		DevChannelNum = devChannelNum;
	}

	public String getDeviceAccount() {
		return deviceAccount;
	}

	public void setDeviceAccount(String deviceAccount) {
		this.deviceAccount = deviceAccount;
	}

	public String getDevicePwd() {
		return devicePwd;
	}

	public void setDevicePwd(String devicePwd) {
		this.devicePwd = devicePwd;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public int getDevicePort() {
		return devicePort;
	}

	public void setDevicePort(int devicePort) {
		this.devicePort = devicePort;
	}

	@Override
	public int describeContents() {
		// TODO 自动生成的方法存根
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO 自动生成的方法存根
		dest.writeString(DEVICE_TYPE);
		dest.writeInt(DevChannelNum);
		dest.writeString(deviceId);
		dest.writeString(deviceIp);
		dest.writeInt(devicePort);
		dest.writeString(deviceAccount);
		dest.writeString(devicePwd);
	}
	
	 @SuppressWarnings("unchecked")
	public static final Creator<LocalDevice> CREATOR = new Creator(){
		   
	        @Override
	        public LocalDevice createFromParcel(Parcel source) {

	            // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错 
	        	LocalDevice p = new LocalDevice();
				p.setDEVICE_TYPE(source.readString());
				p.setDevChannelNum(source.readInt());
	            p.setDeviceId(source.readString()); 
	            p.setDeviceIp(source.readString()); 
	            p.setDevicePort(source.readInt());
	            p.setDeviceAccount(source.readString());
	            p.setDevicePwd(source.readString());
	            return p; 
	        } 
	   
	        @Override
	        public LocalDevice[] newArray(int size) { 

	            return new LocalDevice[size]; 
	        } 
	    };

	    public static LocalDevice parse(String str){
			if (TextUtils.isEmpty(str))return null;
			try {
				JSONObject jsonObject = new JSONObject(str);
				if (jsonObject != null)return parse(jsonObject);
			} catch (JSONException e){
				e.printStackTrace();
			}
			return null;
		}

	/**
	 * /**
	 * "DEVICE_TYPE": "IPCAMERA",
	 "DevChannelNum": 1,
	 "device_id": "9871821321281",
	 "dev_local_ip": "192.168.1.106",
	 "dev_local_port": 55522
	 * @param json
	 * @return
	 */
		public static LocalDevice parse(JSONObject json){
			if (json == null)return null;
			LocalDevice device = new LocalDevice();
			device.setDEVICE_TYPE(json.optString("DEVICE_TYPE"));
			device.setDevChannelNum(json.optInt("DevChannelNum"));
			device.setDeviceId(json.optString("device_id"));
			device.setDeviceIp(json.optString("dev_local_ip"));
			device.setDevicePort(json.optInt("dev_local_port"));
			return device;
		}

	    public EqupInfo item2EqupInfo(){
			EqupInfo dev = new EqupInfo();
			dev.setLocalUser(deviceAccount);
			dev.setLocalPwd(devicePwd);
			dev.setEqupId(deviceId);
			dev.setLocalePlayer(true);
			dev.setLocaleDeviceIp(deviceIp);
			dev.setLocaleDevicePort(devicePort);
			dev.setDeviceDetatilType(DEVICE_TYPE);
			if ("IPC_5".equals(getDEVICE_TYPE())){
				dev.setPlayMode(EqupInfo.VR_MODE | EqupInfo.LOCALE_MODE);
			} else {
				dev.setPlayMode(EqupInfo.PRE_VIEW_MODE | EqupInfo.LOCALE_MODE);
			}
			if (DevChannelNum > 1){
				dev.setEquoModle(EqupInfo.SynscDeviceModle("NVR_" + DevChannelNum));
				List<ChannelInfoEntity> channelInfoEntities = new ArrayList<>();
				ChannelInfoEntity channelInfoEntity = null;
				for (int i = 1; i <= DevChannelNum; i++){
					channelInfoEntity = new ChannelInfoEntity();
					channelInfoEntity.setOrder_num(i);
					channelInfoEntity.setChannel(i);
					channelInfoEntity.setChannel_name("CH" + i);
					channelInfoEntity.setDevice_id(deviceId);
					channelInfoEntities.add(channelInfoEntity);
				}
				dev.setInfoEntitys(channelInfoEntities);
			} else {
				dev.setEquoModle(EqupInfo.SynscDeviceModle("IPC_" + DevChannelNum));
			}
			return dev;
		}

	@Override
	public String toString() {
		return "LocalDevice{" +
				"DEVICE_TYPE='" + DEVICE_TYPE + '\'' +
				", DevChannelNum=" + DevChannelNum +
				", deviceId='" + deviceId + '\'' +
				", deviceIp='" + deviceIp + '\'' +
				", devicePort=" + devicePort +
				", deviceAccount='" + deviceAccount + '\'' +
				", devicePwd='" + devicePwd + '\'' +
				'}';
	}
}
