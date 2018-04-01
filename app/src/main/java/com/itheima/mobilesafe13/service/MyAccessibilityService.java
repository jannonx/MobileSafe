package com.itheima.mobilesafe13.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.accessibility.AccessibilityEvent;

import com.itheima.mobilesafe13.activity.EnterPassLockActivity;
import com.itheima.mobilesafe13.dao.LockedDao;

/**
 * @author Administrator
 * @desc 看门狗监控
 * @date 2015-9-25
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-26 09:38:15 +0800 (Sat, 26 Sep 2015) $
 * @Id $Id: MyAccessibilityService.java 112 2015-09-26 01:38:15Z goudan $
 * @Rev $Rev: 112 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/service/MyAccessibilityService.java $
 * 
 */
public class MyAccessibilityService extends AccessibilityService {
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
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			
			String packName = event.getPackageName() + "";
			System.out.println("packName" + packName);
			// 判断是否加锁
			if (mLockedDao.isLocked(packName)) {
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
		}
	}

	@Override
	public void onCreate() {
		mLockedDao = new LockedDao(getApplicationContext());
		mShuRenReceiver = new ShuRenReceiver();
		IntentFilter filter = new IntentFilter("itheima.shuren");
		registerReceiver(mShuRenReceiver, filter);
		// 注册熟人的广播
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// 取消注册
		unregisterReceiver(mShuRenReceiver);
		super.onDestroy();
	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub

	}

}
