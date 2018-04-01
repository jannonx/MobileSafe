package com.itheima.mobilesafe13;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Process;

/**
 * @author Administrator
 * @desc  一个apk 对应唯一一个application，在所有功能执行之前先运行application
 * @date 2015-9-26
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-26 11:27:05 +0800 (Sat, 26 Sep 2015) $
 * @Id $Id: MyApplication.java 113 2015-09-26 03:27:05Z goudan $
 * @Rev $Rev: 113 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/MyApplication.java $
 *
 */
public class MyApplication extends Application {
	
	private void writeExceptionMessage2File(String mess,File file){
		PrintWriter out;
		try {
			out = new PrintWriter(file);
			out.println(mess);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onCreate() {
		//在所有功能执行之前执行
		//监控任务异常的状态
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				StringBuilder message = new StringBuilder();
				//反射求出手机机型信息
				//1. class
				Class type = Build.class;
				//2. 属性
				Field[] declaredFields = type.getDeclaredFields();
				for (Field field : declaredFields) {
					
					try {
						Object value = field.get(null);
						message.append(field.getName() + ":" + value + "\n");
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 捕获任何线程抛出的异常
				//System.out.println(thread.getName() + "出现异常了:" + ex);
//				错误信息 保存到文件 sdcard中
				message.append(ex.toString());
				writeExceptionMessage2File(message + "", new File("/sdcard/mserror.txt"));
				//异常保存
				//增强用户体验
				
				//死之前 说句话
				Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(getPackageName());
				startActivity(launchIntentForPackage);
				//挂掉
				Process.killProcess(Process.myPid());
			}
		});
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}
	
}
