package com.itheima.mobilesafe13.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.utils.ServiceUtils;
import com.itheima.mobilesafe13.utils.SmsUtils;
import com.itheima.mobilesafe13.utils.SmsUtils.SmsBaikeRestoreListener;
import com.itheima.mobilesafe13.view.SettingCenterItem;
import com.itheima.mobilesafe13.view.SettingCenterItem.OnToggleChangedListener;

public class AToolActivity extends Activity {
	private SettingCenterItem sci_phonequery;
	private SettingCenterItem sci_servicequery;
	private SettingCenterItem sci_smsbaike;
	private SettingCenterItem sci_smsrestore;
	private com.daimajia.numberprogressbar.NumberProgressBar pb_progress;
	private SettingCenterItem sci_applock;
	private SettingCenterItem sci_dogaccessibility;
	private SettingCenterItem sci_dogthread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();

		initEvent();

		initData();
	}

	private void initData() {
		// 初始化看门狗accessibility版
		sci_dogaccessibility.setToggleOn(ServiceUtils.isServiceRunning(
				getApplicationContext(),
				"com.itheima.mobilesafe13.service.MyAccessibilityService"));
		sci_dogthread.setToggleOn(ServiceUtils.isServiceRunning(
				getApplicationContext(),
				"com.itheima.mobilesafe13.service.WatchDogService"));

	}

	private void initEvent() {
		// 事件
		OnToggleChangedListener listener = new OnToggleChangedListener() {

			@Override
			public void onToggleChange(View v, boolean isOpen) {
				switch (v.getId()) {
				case R.id.sci_atool_mobilephone:
					// 电话查询
					Intent phone = new Intent(AToolActivity.this,
							PhoneLocationActivity.class);
					startActivity(phone);
					break;
				case R.id.sci_atool_servicephone:
					// 服务号码
					Intent serviceNumber = new Intent(AToolActivity.this,
							ServiceNumberActivity.class);
					startActivity(serviceNumber);
					break;
				case R.id.sci_atool_smsbaike:
					// 服务号码
					// 用户看到备份的进度
					smsBaike();
					break;
				case R.id.sci_atool_smsrestore:
					// 服务号码
					smsRestore();
					break;
				case R.id.sci_atool_applock:
					// 程序锁
					Intent applock = new Intent(AToolActivity.this,
							AppLockActivity.class);
					startActivity(applock);
					break;
				case R.id.sci_atool_watchdog2:
					if (isOpen) {
						// 启动服务
						Intent service = new Intent(
								getApplicationContext(),
								com.itheima.mobilesafe13.service.WatchDogService.class);
						startService(service);
					} else {
						Intent service = new Intent(
								getApplicationContext(),
								com.itheima.mobilesafe13.service.WatchDogService.class);
						stopService(service);
					}
					break;
				default:
					break;
				}

			}
		};

		sci_phonequery.setOnToggleChangedListener(listener);
		sci_servicequery.setOnToggleChangedListener(listener);
		sci_smsbaike.setOnToggleChangedListener(listener);
		sci_smsrestore.setOnToggleChangedListener(listener);
		sci_applock.setOnToggleChangedListener(listener);
		sci_dogthread.setOnToggleChangedListener(listener);

	}

	protected void smsRestore() {
		// TODO Auto-generated method stub
		ProgressDialog pd = new ProgressDialog(this);
		// 水平进度
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		SmsUtils.smsRestore(this, pd);
	}

	protected void smsBaike() {

		final ProgressDialog pd = new ProgressDialog(this);
		// 水平进度
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		SmsUtils.smsBaike(this, new SmsBaikeRestoreListener() {

			@Override
			public void show() {
				// pd.show();
				pb_progress.setVisibility(View.VISIBLE);

			}

			@Override
			public void setProgress(int currentProgress) {
				// TODO Auto-generated method stub
				// pd.setProgress(currentProgress);
				pb_progress.setProgress(currentProgress);
			}

			@Override
			public void setMax(int max) {
				// TODO Auto-generated method stub
				// pd.setMax(max);
				pb_progress.setMax(max);
			}

			@Override
			public void dismiss() {
				// TODO Auto-generated method stub
				// pd.dismiss();
				pb_progress.setVisibility(View.GONE);
			}
		});

	}

	private void initView() {

		setContentView(R.layout.activity_atool);
		sci_phonequery = (SettingCenterItem) findViewById(R.id.sci_atool_mobilephone);
		sci_servicequery = (SettingCenterItem) findViewById(R.id.sci_atool_servicephone);

		sci_smsbaike = (SettingCenterItem) findViewById(R.id.sci_atool_smsbaike);
		sci_smsrestore = (SettingCenterItem) findViewById(R.id.sci_atool_smsrestore);

		pb_progress = (com.daimajia.numberprogressbar.NumberProgressBar) findViewById(R.id.npb_test);
		pb_progress.setVisibility(View.GONE);

		sci_applock = (SettingCenterItem) findViewById(R.id.sci_atool_applock);

		sci_dogaccessibility = (SettingCenterItem) findViewById(R.id.sci_atool_watchdog1);
		sci_dogthread = (SettingCenterItem) findViewById(R.id.sci_atool_watchdog2);
	}
}
