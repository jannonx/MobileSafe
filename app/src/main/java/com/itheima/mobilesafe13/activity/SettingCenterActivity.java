package com.itheima.mobilesafe13.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.service.BlackService;
import com.itheima.mobilesafe13.service.IncomingShowLocationService;
import com.itheima.mobilesafe13.utils.MyConstaints;
import com.itheima.mobilesafe13.utils.SPUtils;
import com.itheima.mobilesafe13.utils.ServiceUtils;
import com.itheima.mobilesafe13.view.SettingCenterItem;
import com.itheima.mobilesafe13.view.SettingCenterItem.OnToggleChangedListener;
import com.itheima.mobilesafe13.view.ShowLocationStyleDialog;

/**
 * @author Administrator
 * 
 */
public class SettingCenterActivity extends Activity {
	private RelativeLayout rl_autoupdate;
	private ImageView iv_autoupdate;
	private RelativeLayout rl_blackintercept;
	private ImageView iv_blackintercept;
	private SettingCenterItem sci_autoupdate;
	private SettingCenterItem sci_blackintercept;
	private SettingCenterItem sci_showlocation;
	private SettingCenterItem sci_locationStyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		initEvent();
		initData();
	}

	private void initData() {
		
		//初始化样式名
		int locationStyleIndex = SPUtils.getInt(getApplicationContext(), MyConstaints.LOCATIONSTYLEINDEX, 0);
		sci_locationStyle.setText("归属地样式(" + 
		ShowLocationStyleDialog.styleNames[locationStyleIndex] 
		                                		   + ")");

		// 设置黑名单初始化状态
		sci_blackintercept.setToggleOn(ServiceUtils.isServiceRunning(
				getApplicationContext(),
				"com.itheima.mobilesafe13.service.BlackService"));

		// 设置来电归属地初始化状态
		sci_showlocation
				.setToggleOn(ServiceUtils
						.isServiceRunning(getApplicationContext(),
								"com.itheima.mobilesafe13.service.IncomingShowLocationService"));

		// 设置自动更新的初始化状态
		sci_autoupdate.setToggleOn(SPUtils.getBoolean(getApplicationContext(),
				MyConstaints.AUTOUPDATE, false));

	}

	private void initEvent() {
		OnToggleChangedListener onToggleChangedListener = new OnToggleChangedListener() {

			@Override
			public void onToggleChange(View v, boolean isOpen) {
				// 判断v 类型
				switch (v.getId()) {
				case R.id.sci_settingcenter_autoupdate: {
					// 自动更新
					// 自动更新设置
					SPUtils.putBoolean(getApplicationContext(),
							MyConstaints.AUTOUPDATE, isOpen);
					break;
				}
				case R.id.sci_settingcenter_blackintercept: {
					// 黑名单拦截
					if (isOpen) {
						// 打开服务
						Intent blackService = new Intent(
								SettingCenterActivity.this, BlackService.class);
						startService(blackService);
					} else {
						// 关闭服务
						Intent blackService = new Intent(
								SettingCenterActivity.this, BlackService.class);
						stopService(blackService);

					}
					break;
				}
				case R.id.sci_settingcenter_showlocation: {

					// com.itheima.mobilesafe13.service.IncomingShowLocationService
					// 黑名单拦截
					if (isOpen) {
						// 打开服务
						Intent showLocationService = new Intent(
								SettingCenterActivity.this,
								IncomingShowLocationService.class);
						startService(showLocationService);
					} else {
						// 关闭服务
						Intent showLocationService = new Intent(
								SettingCenterActivity.this,
								IncomingShowLocationService.class);
						stopService(showLocationService);

					}
					break;
				}
				case R.id.sci_settingcenter_locationstyle: {
					// 归属地样式
					// 弹出对话框
					ShowLocationStyleDialog dialog = new ShowLocationStyleDialog(SettingCenterActivity.this,sci_locationStyle);
					
					dialog.show();
					break;
				}

				default:
					break;
				}

			}
		};
		// 自动更新添加事件
		sci_autoupdate.setOnToggleChangedListener(onToggleChangedListener);

		sci_blackintercept.setOnToggleChangedListener(onToggleChangedListener);

		sci_showlocation.setOnToggleChangedListener(onToggleChangedListener);

		sci_locationStyle.setOnToggleChangedListener(onToggleChangedListener);
		/*
		 * // 黑名单拦截事件 rl_blackintercept.setOnClickListener(new OnClickListener()
		 * {
		 * 
		 * @Override public void onClick(View v) { // 黑名单拦截服务的控制 if
		 * (ServiceUtils.isServiceRunning(getApplicationContext(),
		 * "com.itheima.mobilesafe13.service.BlackService")) { // 关闭服务 Intent
		 * blackService = new Intent( SettingCenterActivity.this,
		 * BlackService.class); stopService(blackService); // 开关的关闭
		 * iv_blackintercept.setImageResource(R.drawable.off); } else { // 打开服务
		 * Intent blackService = new Intent( SettingCenterActivity.this,
		 * BlackService.class); startService(blackService); // 开关的打开
		 * iv_blackintercept.setImageResource(R.drawable.on); }
		 * 
		 * } });
		 */
		/*
		 * // /自动更新添加事件 rl_autoupdate.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // boolean autoUpdate =
		 * SPUtils .getBoolean(getApplicationContext(), MyConstaints.AUTOUPDATE,
		 * false); // 保存是否自动更新 SPUtils.putBoolean(getApplicationContext(),
		 * MyConstaints.AUTOUPDATE, !autoUpdate);
		 * 
		 * // 改变开关的图标
		 * 
		 * iv_autoupdate.setImageResource(!autoUpdate ? R.drawable.on :
		 * R.drawable.off); } });
		 */

	}

	private void initView() {
		setContentView(R.layout.activity_settingcenter);
		sci_autoupdate = (SettingCenterItem) findViewById(R.id.sci_settingcenter_autoupdate);
		sci_blackintercept = (SettingCenterItem) findViewById(R.id.sci_settingcenter_blackintercept);
		sci_showlocation = (SettingCenterItem) findViewById(R.id.sci_settingcenter_showlocation);
		sci_locationStyle = (SettingCenterItem) findViewById(R.id.sci_settingcenter_locationstyle);
	}
}
