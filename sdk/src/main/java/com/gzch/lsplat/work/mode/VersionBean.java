package com.gzch.lsplat.work.mode;

import org.json.JSONObject;

import java.io.Serializable;

public class VersionBean implements Serializable {
	/**
	 * web请求 结果码 code值
	 */
	private int resultCode;

	/**
	 * 请求错误提示
	 */
	private String errMsg;

	/**
	 * 请求的命令
	 */
	private int cmd;

	private static final long serialVersionUID = 1L;
	
	private String versionName;

	private int versionCode;

	private String appName ;
	
	private String updateStrategy;//   升级策略，1为强制更新，0为建议更新
	
	private String downloadLink;   //下载地址
	
	private String updateLog ;//更新日志

	/**
	 * 公告版本
	 * 该值比本地值大，显示公告
	 */
	private int announcementVersion = 0;

	/**
	 * 公告内容
	 */
	private String announcementContent = "";

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getUpdateStrategy() {
		return updateStrategy;
	}

	public void setUpdateStrategy(String updateStrategy) {
		this.updateStrategy = updateStrategy;
	}

	public String getDownloadLink() {
		return downloadLink;
	}

	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

	public String getUpdateLog() {
		return updateLog;
	}
	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
	}

	public int getAnnouncementVersion() {
		return announcementVersion;
	}

	public void setAnnouncementVersion(int announcementVersion) {
		this.announcementVersion = announcementVersion;
	}

	public String getAnnouncementContent() {
		return announcementContent;
	}

	public void setAnnouncementContent(String announcementContent) {
		this.announcementContent = announcementContent;
	}

	public static VersionBean paseVersion(JSONObject object){
		VersionBean bean = new VersionBean();
		try{
			bean.setAppName(object.optString("appName"));
			bean.setDownloadLink(object.optString("downloadLink"));
			bean.setUpdateLog(object.optString("updateLog"));
			bean.setUpdateStrategy(object.optString("updateStrategy"));
			bean.setVersionName(object.optString("versionName"));
			bean.setVersionCode(object.optInt("versionCode",0));
			bean.setAnnouncementVersion(object.optInt("showmess",0));
			bean.setAnnouncementContent(object.optString("message"));
		}catch(Exception e){
			e.printStackTrace();
		}
		return bean;
	}

	@Override
	public String toString() {
		return "VersionBean{" +
				"resultCode=" + resultCode +
				", errMsg='" + errMsg + '\'' +
				", cmd=" + cmd +
				", versionName='" + versionName + '\'' +
				", versionCode=" + versionCode +
				", appName='" + appName + '\'' +
				", updateStrategy='" + updateStrategy + '\'' +
				", downloadLink='" + downloadLink + '\'' +
				", updateLog='" + updateLog + '\'' +
				'}';
	}
}
