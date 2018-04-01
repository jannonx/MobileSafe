package com.itheima.mobilesafe13.utils;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-14
 * @desc 服务的工具类

 * @version $Rev: 26 $
 * @author $Author: goudan $
 * @Date  $Date: 2015-09-14 11:32:58 +0800 (Mon, 14 Sep 2015) $
 * @Id    $Id: ServiceUtils.java 26 2015-09-14 03:32:58Z goudan $
 * @Url   $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/utils/ServiceUtils.java $
 * 
 */
public class ServiceUtils {
	public static boolean isServiceRunning(Context context,String serviceName){
		boolean res = false;
		
		//ActivityManager
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取系统中所有运行的服务
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			
			res = runningServiceInfo.service.getClassName().equals(serviceName);
			if (res)
				break;
		}
		return res;
	}
}
