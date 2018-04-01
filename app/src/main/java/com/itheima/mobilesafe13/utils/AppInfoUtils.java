package com.itheima.mobilesafe13.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.itheima.mobilesafe13.domain.AppInfoBean;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

/**
 * @author Administrator
 * @desc  软件管家的工具类  所有APP信息 sd可用内存 手机可用内存 
 * @date 2015-9-21
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-26 11:27:05 +0800 (Sat, 26 Sep 2015) $
 * @Id $Id: AppInfoUtils.java 113 2015-09-26 03:27:05Z goudan $
 * @Rev $Rev: 113 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/utils/AppInfoUtils.java $
 *
 */
public class AppInfoUtils {
	/**
	 * @return
	 *     手机可用内存
	 */
	public static long getPhoneTotalMem(){
		File dataDirectory = Environment.getDataDirectory();
		return dataDirectory.getTotalSpace();
	}
	/**
	 * @return
	 *     手机可用内存
	 */
	public static long getPhoneAvailMem(){
		File dataDirectory = Environment.getDataDirectory();
		return dataDirectory.getFreeSpace();
	}

	/**
	 * @return
	 *     sd可用内存
	 */
	public static long getSDTotalMem(){
		File dataDirectory = Environment.getExternalStorageDirectory();
		return dataDirectory.getTotalSpace();
	}
	
	/**
	 * @return
	 *     sd可用内存
	 */
	public static long getSDAvailMem(){
		File dataDirectory = Environment.getExternalStorageDirectory();
		return dataDirectory.getFreeSpace();
	}
	
	/**
	 * @param context
	 * @param bean
	 *      bean 一定先设置包名 ，就可以把其他属性封装 
	 * @throws NameNotFoundException
	 */
	public static void getAppInfo(Context context,AppInfoBean bean) throws NameNotFoundException {
		PackageManager pm = context.getPackageManager();
		// 目的ApplicationInfo
		ApplicationInfo applicationInfo = pm.getApplicationInfo(bean.getPackName(), 0);
		
		//图标
		bean.setIcon(applicationInfo.loadIcon(pm));
		//名字
		bean.setAppName(applicationInfo.loadLabel(pm) + "");
		
		if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
			//系统app
			bean.setSystem(true);
		}
		
		if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){
			//安装在sd卡中
			bean.setSD(true);
		}
		
		//路径
		bean.setSourceDir(applicationInfo.sourceDir);
		
		//安装文件的大小
		bean.setSize(new File(applicationInfo.sourceDir).length());
		
	}
	
	public static int getAllInstalledAppNumber(Context context){
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		return installedPackages.size();
	}
	/**
	 * @return
	 *    获取所有安装的app信息 
	 */
	public static List<AppInfoBean> getAllInstalledAppInfos1(Context context){
		List<AppInfoBean> datas = new ArrayList<AppInfoBean>();
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		AppInfoBean bean = null;
		for (PackageInfo packageInfo : installedPackages) {
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			bean = new AppInfoBean();
			bean.setPackName(packageInfo.packageName);
			try {
				getAppInfo(context,bean);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			datas.add(bean);
		}
		
		return datas;
	}
	
	/**
	 * @return
	 *    获取所有安装的app信息 
	 */
	public static List<AppInfoBean> getAllInstalledAppInfos(Context context){
		List<AppInfoBean> datas = new ArrayList<AppInfoBean>();
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> installedApplications = pm.getInstalledApplications(0);
		AppInfoBean bean = null;
		for (ApplicationInfo applicationInfo : installedApplications) {
			//组织数据
			bean = new AppInfoBean();
			//包名
			bean.setPackName(applicationInfo.packageName);
			//图标
			bean.setIcon(applicationInfo.loadIcon(pm));
			//名字
			bean.setAppName(applicationInfo.loadLabel(pm) + "");
			
			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
				//系统app
				bean.setSystem(true);
			}
			
			if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){
				//安装在sd卡中
				bean.setSD(true);
			}
			
			//路径
			bean.setSourceDir(applicationInfo.sourceDir);
			
			//安装文件的大小
			bean.setSize(new File(applicationInfo.sourceDir).length());
			
			
			bean.setUid(applicationInfo.uid);
			datas.add(bean);
		}
		
		return datas;
	}

}
