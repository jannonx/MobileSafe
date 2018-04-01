package com.itheima.mobilesafe13.service;

import java.util.List;

import com.itheima.mobilesafe13.domain.AppInfoBean;
import com.itheima.mobilesafe13.utils.TaskInfoUtils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * @author Administrator
 * @desc  锁屏清理进程 
 * @date 2015-9-22
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-22 16:38:38 +0800 (Tue, 22 Sep 2015) $
 * @Id $Id: ScreenOffClearTaskService.java 92 2015-09-22 08:38:38Z goudan $
 * @Rev $Rev: 92 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/service/ScreenOffClearTaskService.java $
 *
 */
public class ScreenOffClearTaskService extends Service {

	private ClearTaskReceiver mReceiver;
	private ActivityManager am;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//广播
	private class ClearTaskReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//清理进程
			List<AppInfoBean> allRunningAppInfos = TaskInfoUtils.getAllRunningAppInfos(context);
			for (AppInfoBean appInfoBean : allRunningAppInfos) {
				am.killBackgroundProcesses(appInfoBean.getPackName());
			}
			System.out.println("清理进程");
		}
		
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		System.out.println("锁屏清理进程服务开启");
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		mReceiver = new ClearTaskReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);
		
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("锁屏清理进程服务关闭");
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

}
