package com.itheima.mobilesafe13.domain;

import android.graphics.drawable.Drawable;

/**
 * @author Administrator
 * @desc  app基本信息封装
 * @date 2015-9-21
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-26 11:27:05 +0800 (Sat, 26 Sep 2015) $
 * @Id $Id: AppInfoBean.java 113 2015-09-26 03:27:05Z goudan $
 * @Rev $Rev: 113 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/domain/AppInfoBean.java $
 *
 */
public class AppInfoBean {
	private int uid;
	
	
	public AppInfoBean(String packName){
		this.packName = packName;
	}
	public AppInfoBean(){
		
	}
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o instanceof AppInfoBean) {
			AppInfoBean b = (AppInfoBean) o;
			return this.packName.equals(b.getPackName());
		}
		return false;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return packName.hashCode();
	}
	private boolean isChecked;
	@Override
	public String toString() {
		return "AppInfoBean [icon=" + icon + ", appName=" + appName
				+ ", isSystem=" + isSystem + ", isSD=" + isSD + ", packName="
				+ packName + ", size=" + size + ", sourceDir=" + sourceDir
				+ "]";
	}
	private Drawable icon;//图标
	private String appName;//app名字
	private boolean isSystem;//是否是系统软件
	private boolean isSD;//是否安装在sd卡中
	private String packName;//app包名
	private long size;//占用的大小
	private String sourceDir;//安装路径
	private long memSize;//占用的内存大小
	public long getMemSize() {
		return memSize;
	}
	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	public boolean isSD() {
		return isSD;
	}
	public void setSD(boolean isSD) {
		this.isSD = isSD;
	}
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getSourceDir() {
		return sourceDir;
	}
	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
}
