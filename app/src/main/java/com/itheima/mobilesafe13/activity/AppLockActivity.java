package com.itheima.mobilesafe13.activity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.dao.LockedDao;
import com.itheima.mobilesafe13.db.LockedDB;
import com.itheima.mobilesafe13.domain.AppInfoBean;
import com.itheima.mobilesafe13.utils.AppInfoUtils;
import com.itheima.mobilesafe13.view.AppLockHeadView;
import com.itheima.mobilesafe13.view.AppLockHeadView.OnLockChangeListener;
import com.itheima.mobilesafe13.view.BaseLockFragment;
import com.itheima.mobilesafe13.view.LockedFragment;
import com.itheima.mobilesafe13.view.UnLockedFragment;

/**
 * @author Administrator
 * @desc 程序锁的界面
 * @date 2015-9-25
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-25 15:55:36 +0800 (Fri, 25 Sep 2015) $
 * @Id $Id: AppLockActivity.java 108 2015-09-25 07:55:36Z goudan $
 * @Rev $Rev: 108 $
 * @URL $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/activity/AppLockActivity.java $
 * 
 */
public class AppLockActivity extends FragmentActivity {
	private AppLockHeadView alhv_op;
	private FrameLayout fl_content;
	private BaseLockFragment lockedFragment;
	private BaseLockFragment unLockedFragment;
	private LockedDao mLockedDao;
	private List<String> allLockedAppPacknames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		initEvent();
		initFragment();
		initData();
	}

	private void initFragment() {
		List<AppInfoBean> allInstalledAppInfos = AppInfoUtils
				.getAllInstalledAppInfos(this);
		
		//移除自己
		
		allInstalledAppInfos.remove(new AppInfoBean(getPackageName()));
		
		//对allInstalledAppInfos排序
		Collections.sort(allInstalledAppInfos, new Comparator<AppInfoBean>() {

			@Override
			public int compare(AppInfoBean lhs, AppInfoBean rhs) {
				// TODO Auto-generated method stub
				if (rhs.isSystem()) {
					return -1;
				} else {
					return 3;
				}
				//return 0;
			}
		});
		mLockedDao = new LockedDao(this);
		allLockedAppPacknames = mLockedDao.getAllLockedAppPacknames();
		lockedFragment = new LockedFragment();
		unLockedFragment = new UnLockedFragment();
		lockedFragment.setLockedDao(mLockedDao);
		lockedFragment.setAllInstalledAppInfos(allInstalledAppInfos);
		lockedFragment.setAllLockedPackageNames(allLockedAppPacknames);
		
		unLockedFragment.setLockedDao(mLockedDao);
		unLockedFragment.setAllInstalledAppInfos(allInstalledAppInfos);
		unLockedFragment.setAllLockedPackageNames(allLockedAppPacknames);
		
		//注册内容观察者
		ContentObserver observer = new ContentObserver(new Handler()) {

			@Override
			public void onChange(boolean selfChange) {
				// TODO Auto-generated method stub
				super.onChange(selfChange);
				System.out.println("收到通知。。。。。。。。。。。");
				allLockedAppPacknames.clear();
				allLockedAppPacknames.addAll(mLockedDao.getAllLockedAppPacknames());
			}
			
		};
		getContentResolver().registerContentObserver(LockedDB.URI, true, observer);
		
		
		
		
	}

	private void initData() {
		selectFragment(false);

	}

	private void selectFragment(boolean isLocked) {
		// 初始化两个fragment 并且默认替换掉fragmentLayout的内容
		// 1. fragmentmanager
		FragmentManager supportFragmentManager = getSupportFragmentManager();
		// 2. 开启事务
		FragmentTransaction beginTransaction = supportFragmentManager
				.beginTransaction();
		// 3. 替换
		if (isLocked) {
			beginTransaction.replace(R.id.fl_applock_content,
					lockedFragment, "lock");
		} else {
			beginTransaction.replace(R.id.fl_applock_content,
					unLockedFragment, "unlock");
		}
		// 4. 提交
		beginTransaction.commit();
	}

	private void initEvent() {
		alhv_op.setOnLockChangeListener(new OnLockChangeListener() {

			@Override
			public void onLockChanged(boolean isLocked) {
				selectFragment(isLocked);
			}
		});

	}

	private void initView() {
		setContentView(R.layout.activity_applock);
		alhv_op = (AppLockHeadView) findViewById(R.id.alhv_head_tool);
		fl_content = (FrameLayout) findViewById(R.id.fl_applock_content);
	}
}
