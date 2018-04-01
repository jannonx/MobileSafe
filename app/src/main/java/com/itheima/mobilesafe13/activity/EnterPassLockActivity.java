package com.itheima.mobilesafe13.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itheima.mobilesafe13.R;

/**
 * @author Administrator
 * @desc  看门狗的输入密码界面
 * @date 2015-9-25
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-26 08:49:38 +0800 (Sat, 26 Sep 2015) $
 * @Id $Id: EnterPassLockActivity.java 111 2015-09-26 00:49:38Z goudan $
 * @Rev $Rev: 111 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/activity/EnterPassLockActivity.java $
 *
 */
public class EnterPassLockActivity extends Activity {
	private EditText et_pass;
	private ImageView iv_icon;
	private String packName;
	private HomeReceiver mHomeReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		initView();
		initData();
		initHomeReceiver();
	
		
	}
	private class HomeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//判断事件类型 
			if (intent.getAction().contains(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				//home键的广播
				loadMain();
			}
			
		}
		
	}
	private void initHomeReceiver() {
		//注册home键的广播
		
		mHomeReceiver = new HomeReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(mHomeReceiver, filter);
		
	}
	
	@Override
	protected void onDestroy() {
//		取消home键的广播注册
		unregisterReceiver(mHomeReceiver);
		super.onDestroy();
	}
	private void initData() {
		packName = getIntent().getStringExtra("packname");
		PackageManager pm = getPackageManager();
		try {
			iv_icon.setImageDrawable(pm.getApplicationIcon(packName));
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//图标
		
		
	}
	//监听home键 广播
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		// 监听按键的信息
		//监听返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//进入主界面
			loadMain();
		}
		return super.onKeyDown(keyCode, event);
	}
	private void loadMain() {
		// 手机的主界面
		/*
		 * <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.HOME" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.MONKEY"/>
            </intent-filter>
		 */
		
		Intent main = new Intent("android.intent.action.MAIN");
		main.addCategory("android.intent.category.HOME");
		main.addCategory("android.intent.category.DEFAULT");
		main.addCategory("android.intent.category.MONKEY");
		startActivity(main);//进入手机主界面
		
		//关闭自己
		finish();
		
	}
	public void enter(View v){
		String password = et_pass.getText().toString().trim();
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(getApplicationContext(), "密码不能为空", 1).show();
			return ;
		}
		
		if (password.equals("1")) {
			// 告诉看门狗是熟人，放行
			Intent intent = new Intent("itheima.shuren");
			intent.putExtra("shuren", packName);
			sendBroadcast(intent);
			//关闭
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "坏人", 1).show();
			return ;
		}
	}
	private void initView() {
		setContentView(R.layout.activity_lock_pass);
		
		et_pass = (EditText) findViewById(R.id.et_lock_pass_password);
		iv_icon = (ImageView) findViewById(R.id.iv_lock_pass_appicon);
		
		
	}
}
