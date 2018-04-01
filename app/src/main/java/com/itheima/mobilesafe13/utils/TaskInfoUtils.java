package com.itheima.mobilesafe13.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.itheima.mobilesafe13.domain.AppInfoBean;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @author Administrator
 * @desc 获取进程相关的信息
 * @date 2015-9-22
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-22 10:32:31 +0800 (Tue, 22 Sep 2015) $
 * @Id $Id: TaskInfoUtils.java 85 2015-09-22 02:32:31Z goudan $
 * @Rev $Rev: 85 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/utils/TaskInfoUtils.java $
 * 
 */
public class TaskInfoUtils {
	/**
	 * @return 可用内存
	 */
	public static long getAvailMem(Context context) {
		// ActivityManager
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo(); // c/c++

		am.getMemoryInfo(outInfo);// 把内存的信息写到outInfo对象中
		return outInfo.availMem;

	}

	/**
	 * @return 总内存大小
	 */
	public static long getTotalMem() {
		// ActivityManager
		//  总内存的信息  /porc/meminfo文件
		File file = new File("/proc/meminfo");
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = reader.readLine();
			String size = line.substring(line.indexOf(':') + 1, line.length() - 2).trim();
			long totalMem = Long.parseLong(size) * 1024;// 把kb 转成 byte
			return totalMem;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * @param context
	 *    上下文
	 * @return
	 *     所有运行中的进程信息
	 */
	public static List<AppInfoBean> getAllRunningAppInfos(Context context){
		List<AppInfoBean> mAppInfoBeans = new ArrayList<AppInfoBean>();
		//1. 获取ActivityManager
		ActivityManager  am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		//2. 获取运行中的进程信息
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		
		//3. 去信息封装
		AppInfoBean bean = null;
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			//System.out.println(runningAppProcessInfo.processName);
			bean = new AppInfoBean();
			android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
			//占用的内存
			bean.setMemSize(processMemoryInfo[0].getTotalPrivateDirty() * 1024);
			bean.setPackName(runningAppProcessInfo.processName);
			
			try {
				AppInfoUtils.getAppInfo(context, bean);
				//正常添加
				mAppInfoBeans.add(bean);
			} catch (NameNotFoundException e) {
				// 没有名字的进程
				e.printStackTrace();
			}
			
		}
		
		return mAppInfoBeans;
	}
}
