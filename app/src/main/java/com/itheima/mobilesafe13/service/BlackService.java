package com.itheima.mobilesafe13.service;

import java.lang.reflect.Method;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.dao.BlackDao;
import com.itheima.mobilesafe13.dao.ContactsDao;
import com.itheima.mobilesafe13.db.BlackDB;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-16
 * @desc 黑名单管理界面
 * 
 * @version $Rev: 92 $
 * @author $Author: goudan $
 * @Date $Date: 2015-09-22 16:38:38 +0800 (Tue, 22 Sep 2015) $
 * @Id $Id: BlackService.java 92 2015-09-22 08:38:38Z goudan $
 * @Url $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/activity/BlackActivity.java $
 * 
 */
public class BlackService extends Service {

	private SmsReceiver mSmsReceiver;
	private BlackDao mBlackDao;
	private TelephonyManager mTM;
	private PhoneStateListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	// 短信的拦截
	private class SmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取短信内容
			Object[] smsDatas = (Object[]) intent.getExtras().get("pdus");
			for (Object data : smsDatas) {
				//获取短信
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) data);
				//短信的号码
				String address = smsMessage.getDisplayOriginatingAddress();
				//判断是否是短信拦截
				int mode = mBlackDao.getMode(address);
System.out.println("mode" + mode);				
				if ((mode & BlackDB.SMS_MODE) != 0) {
					//拦截短信 
					abortBroadcast();//拦截短信
				}
			}

		}

	}

	@Override
	public void onCreate() {
		
		//提供服务级别
		startPrority();
		mBlackDao = new BlackDao(getApplicationContext());
		//注册短信拦截
		registSmsintercept();
		//注册电话拦截
		registTelintercept();

		super.onCreate();
	}

	private void startPrority() {
		Notification notification = new Notification(R.drawable.heima, "黑马13卫士", 0);
		Intent intent = new Intent();
		intent.setAction("www.itheima.com");
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
		notification.setLatestEventInfo(getApplicationContext(),"黑马卫士" , "打开黑马主界面", contentIntent );
	
		startForeground(1, notification);
		
	}

	private void registTelintercept() {
		// 电话拦截
		//监听电话状态
		//电话管理器
		
		mTM = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		listener = new PhoneStateListener(){

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				// 监听电话状态的改变
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING://响铃状态
					phoneIntercept(incomingNumber);
					break;

				default:
					break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}

			private void phoneIntercept(final String incomingNumber) {
				//判断是否是黑名单号码
				int mode = mBlackDao.getMode(incomingNumber);
				if ((mode & BlackDB.PHONE_MODE) != 0) {// 10  11
					//注册内容观察者
					deleteLog(incomingNumber);
					//电话拦截
					endCall();
					//删除日志  时间
					/*new Thread(){
						public void run() {
							//
							ContactsDao.deleteLog(getApplicationContext(), incomingNumber);
						};
					}.start();*/
					
				}
				
			}
			
		};
		mTM.listen(listener , PhoneStateListener.LISTEN_CALL_STATE);//监听电话状态
	}

	protected void deleteLog(final String number) {
		// 注册内容观察者
		Uri uri = Uri.parse("content://call_log/calls" );
		getContentResolver().registerContentObserver(uri, true,new ContentObserver(new Handler()) {

			@Override
			public void onChange(boolean selfChange) {
				// 日志发生变化
				ContactsDao.deleteLog(getApplicationContext(), number);
				// 取消注册
				getContentResolver().unregisterContentObserver(this);
				super.onChange(selfChange);
			}
			
		} );
		
	}

	protected void endCall() {
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//挂断屏蔽了
		try {
			//1. class
			Class clazz = Class.forName("android.os.ServiceManager");
			//2. method
			Method method = clazz.getDeclaredMethod("getService", String.class);
			//3. call
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			
			//4. aidl ibinder 转成可调用的对象
			ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
			iTelephony.endCall();// 挂断电话
			//删除电话日志
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void registSmsintercept() {
		mSmsReceiver = new SmsReceiver();
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		//级别为最高
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mSmsReceiver, filter);// 注册短信拦截
		System.out.println("黑名单拦截启动");
		
	}

	@Override
	public void onDestroy() {
		// 服务的关闭
		System.out.println("黑名单拦截关闭");
		unregisterReceiver(mSmsReceiver);// 取消短信拦截
		
		mTM.listen(listener, PhoneStateListener.LISTEN_NONE);//不监听
		super.onDestroy();
	}

}


