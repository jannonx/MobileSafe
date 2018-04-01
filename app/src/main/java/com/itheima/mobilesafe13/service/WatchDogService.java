package com.itheima.mobilesafe13.service;

import java.util.List;

import com.itheima.mobilesafe13.activity.EnterPassLockActivity;
import com.itheima.mobilesafe13.dao.LockedDao;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;

/**
 * @author Administrator
 * @desc 看门狗 线程监控
 * @date 2015-9-26
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-26 09:38:15 +0800 (Sat, 26 Sep 2015) $
 * @Id $Id: WatchDogService.java 112 2015-09-26 01:38:15Z goudan $
 * @Rev $Rev: 112 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/service/WatchDogService.java $
 *
 */
public class WatchDogService extends Service {
	private LockedDao mLockedDao;
	private ShuRenReceiver mShuRenReceiver;
	private String shuren;

	private class ShuRenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			shuren = intent.getStringExtra("shuren");
		}

	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private boolean isRunning;
	private ActivityManager mAm;
	private List<String> allLockedAppPacknames;
	
	@Override
	public void onCreate() {
		mLockedDao = new LockedDao(getApplicationContext());
		mShuRenReceiver = new ShuRenReceiver();
		// 注册熟人的广播
		IntentFilter filter = new IntentFilter("itheima.shuren");
		registerReceiver(mShuRenReceiver, filter);
		
		mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		
		allLockedAppPacknames = mLockedDao.getAllLockedAppPacknames();
		//启动监控线程
		startDog();
		
		super.onCreate();
	}

	private void startDog() {
		// TODO Auto-generated method stub
		new Thread(){
			public void run() {
				isRunning = true;
				List<RunningTaskInfo> runningTasks = null;
				RunningTaskInfo runningTaskInfo = null;
				String packName = null;
				while(isRunning) {
					//监控任务栈
					runningTasks = mAm.getRunningTasks(1);
					//最新打开的任务栈
					runningTaskInfo = runningTasks.get(0);
					packName = runningTaskInfo.topActivity.getPackageName();
					// 判断是否加锁
					if (allLockedAppPacknames.contains(packName)){//if (mLockedDao.isLocked(packName)) {//内存优化
						// 判断是否是熟人
						if (packName.equals(shuren)) {
							// 放行
						} else {
							// 加锁
							// 弹出界面 输入密码
							Intent intent = new Intent(getApplicationContext(),
									EnterPassLockActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packname", packName);
							startActivity(intent);
							// 密码正确 访问
							// 密码错误 继续拦截
						}

					} else {
						// 放行
					}
					SystemClock.sleep(200);
				}
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// 取消注册
		unregisterReceiver(mShuRenReceiver);
		isRunning = false;
		super.onDestroy();
	}

}
