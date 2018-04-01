package com.itheima.mobilesafe13.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.receiver.MyDeviceAdminReceiver;
import com.itheima.mobilesafe13.service.LostFindService;
import com.itheima.mobilesafe13.utils.MyConstaints;
import com.itheima.mobilesafe13.utils.SPUtils;
import com.itheima.mobilesafe13.utils.ServiceUtils;
import com.itheima.mobilesafe13.utils.ShowToast;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-13
 * @desc 第一个设置向导界面
 * 
 * @version $Rev: 30 $
 * @author $Author: goudan $
 * @Date $Date: 2015-09-14 17:11:41 +0800 (Mon, 14 Sep 2015) $
 * @Id $Id: Setup4Activity.java 30 2015-09-14 09:11:41Z goudan $
 * @Url $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/activity/Setup4Activity.java $
 * 
 */
public class Setup4Activity extends BaseSetupActivity {
	private CheckBox cb_isopenlostfind;
	private TextView tv_showState;
	private ComponentName mDeviceAdminSample;
	private DevicePolicyManager mDPM;

	protected void initView() {
		setContentView(R.layout.activity_setup4);
		cb_isopenlostfind = (CheckBox) findViewById(R.id.cb_setup4_isopenlostfind);
		tv_showState = (TextView) findViewById(R.id.tv_setup4_showstate);
		
		
		mDeviceAdminSample = new ComponentName(this,
					MyDeviceAdminReceiver.class);
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
	}

	@Override
	protected void initData() {
		// 初始化复选框的状态
		if (ServiceUtils.isServiceRunning(getApplicationContext(),
				"com.itheima.mobilesafe13.service.LostFindService")) {
			cb_isopenlostfind.setChecked(true);
		} else {
			cb_isopenlostfind.setChecked(false);
		}
		super.initData();
	}

	@Override
	protected void initEvent() {
		// 添加复选框状态改变事件
		cb_isopenlostfind
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// 状态变化 相应结果
						// 1. 文字
						if (isChecked) {
							
							//判断是否激活设备管理器 
							if (!mDPM.isAdminActive(mDeviceAdminSample)) {
								//没有激活
								//打开激活的界面
								 activeDevice();
							} else {
								// 开启防盗服务
								Intent service = new Intent(Setup4Activity.this,
										LostFindService.class);
								startService(service);
								
								// 开启
								tv_showState.setText("手机防盗已经开启");
							}
							
							
							// 状态不应该保存sp中 动态判断服务是否运行 ActivityManager

						} else {
							// 关闭
							// 关闭防盗服务
							Intent service = new Intent(Setup4Activity.this,
									LostFindService.class);
							stopService(service);
							tv_showState.setText("手机防盗已经关闭");
						}

					}

					private void activeDevice() {
						Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
						 intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
						 intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
						         "开启激活设备管理");
						 startActivityForResult(intent, 1);
					}
				});
		super.initEvent();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mDPM.isAdminActive(mDeviceAdminSample)) {
			// 开启防盗服务
			Intent service = new Intent(Setup4Activity.this,
					LostFindService.class);
			startService(service);
			
			// 开启
			tv_showState.setText("手机防盗已经开启");
		} else {
			//取消勾选复选框
			cb_isopenlostfind.setChecked(false);
			Toast.makeText(getApplicationContext(), "先激活，才能启动防盗服务", 1).show();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void startNext() {
		// 设置完成
		if (!cb_isopenlostfind.isChecked()) {
			// 提醒必须勾选
			ShowToast.show("请先勾选开启防盗", this);
		} else {
			//设置完成
			//保存设置完成的状态
			SPUtils.putBoolean(getApplicationContext(), MyConstaints.ISSETUPFINISH, true);
			//添加系统启动 自动开启防盗服务
			SPUtils.putBoolean(getApplicationContext(), MyConstaints.BOOTCOMPLETE, true);
			startPage(LostFindActivity.class);
		}

	}

	@Override
	protected void startPrev() {
		startPage(Setup3Activity.class);

	}
}
