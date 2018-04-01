package com.itheima.mobilesafe13.service;

import com.itheima.mobilesafe13.dao.AddressDao;
import com.itheima.mobilesafe13.view.MyToast;

import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class IncomingShowLocationService extends Service {

	private TelephonyManager mTM;
	private PhoneStateListener listener;
	private MyToast mToast;
	private OutCallReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class OutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取外拨的号码
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			String location = AddressDao.getLocation(number);
			mToast.show(location);
			
		}
		
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		System.out.println("show location start");
		//注册来电状态
		registPhoneState();
		//注册外拨电话
		registOutCall();
		
		mToast = new MyToast(getApplicationContext());
		super.onCreate();
	}

	private void registOutCall() {
		//初始化外拨电话
		
		mReceiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(mReceiver, filter);
		
	}

	private void registPhoneState() {
		mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new PhoneStateListener() {

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				// 监听状态
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE://停止
System.out.println("CALL_STATE_IDLE");					
					mToast.hiden();
					break;
				case TelephonyManager.CALL_STATE_RINGING://响铃状态
					//显示归属地
					showLocation(incomingNumber);
					break;
					
				case TelephonyManager.CALL_STATE_OFFHOOK://通话
					//mToast.hiden();
					break;

				default:
					break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}
			
		};
		
		//监听电话状态
		mTM.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	protected void showLocation(String incomingNumber) {
		// 1. 获取归属地
		String location = AddressDao.getLocation(incomingNumber);
		// 2. toast显示归属地
		//Toast.makeText(getApplicationContext(), location, 1).show();
		mToast.show(location);
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("show location stop");
		mTM.listen(listener, PhoneStateListener.LISTEN_NONE);
		
		unregisterReceiver(mReceiver);//取消外拨电话的注册
		super.onDestroy();
	}

}
