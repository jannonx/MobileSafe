package com.itheima.mobilesafe13.service;

import java.util.List;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.BaseAdapter;

import com.itheima.mobilesafe13.receiver.MyDeviceAdminReceiver;
import com.itheima.mobilesafe13.utils.MyConstaints;
import com.itheima.mobilesafe13.utils.SPUtils;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-14
 * @desc 手机防盗服务
 * 
 * @version $Rev: 31 $
 * @author $Author: goudan $
 * @Date $Date: 2015-09-16 10:30:38 +0800 (Wed, 16 Sep 2015) $
 * @Id $Id: LostFindService.java 31 2015-09-16 02:30:38Z goudan $
 * @Url $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/service/LostFindService.java $
 * 
 */
public class LostFindService extends Service {

	private SmsReceiver mSmsReceiver;
	private ComponentName mDeviceAdminSample;
	private DevicePolicyManager mDPM;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	// 短信的广播接收者
	private class SmsReceiver extends BroadcastReceiver {
		boolean isPlaying = false;// 音乐是否播放的标记

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println(">>>>>>>>>>>>>>>>>>.");
			// 获取短信内容
			Object[] smsDatas = (Object[]) intent.getExtras().get("pdus");
			for (Object data : smsDatas) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) data);
				String body = smsMessage.getDisplayMessageBody();
				System.out.println(body + "body");
				// 根据短信内容进行拦截
				if (body.equals("#*music*#")) {
					if (!isPlaying) {
						MediaPlayer mediaPlayer = MediaPlayer.create(context,
								com.itheima.mobilesafe13.R.raw.qqqg);
						mediaPlayer
								.setOnCompletionListener(new OnCompletionListener() {

									@Override
									public void onCompletion(MediaPlayer mp) {
										// 音乐播完
										isPlaying = false;
									}
								});
						mediaPlayer.start();// 开始播放
						isPlaying = true;
					}
					abortBroadcast();// 停止广播传递
				} else if (body.equals("#*gps*#")) {
					getlocation();
					abortBroadcast();// 停止广播传递
				} else if (body.equals("#*wipedata*#")) {
					// 清除sd卡的数据
					mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
					abortBroadcast();// 停止广播传递
				} else if (body.equals("#*lockscreen*#")) {
					System.out.println("lockscreen");
					// 重置密码
					mDPM.resetPassword("110",
							DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
					// 锁屏
					mDPM.lockNow();
					abortBroadcast();// 停止广播传递
				}
			}

		}

	}
	
	

	@Override
	public void onCreate() {
		// 第一次初始化
		super.onCreate();
		// 注册短信的拦截广播

		mSmsReceiver = new SmsReceiver();
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		// 优先级
		filter.setPriority(Integer.MAX_VALUE);

		registerReceiver(mSmsReceiver, filter);

		// 初始化设备管理的对象

		mDeviceAdminSample = new ComponentName(this,
				MyDeviceAdminReceiver.class);
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

	}

	public void getlocation() {
		// 定位api
		final LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		// 获取定位的提供方式
		List<String> allProviders = lm.getAllProviders();
		for (String prov : allProviders) {
			System.out.println(prov);
		}
		// provider 定位方式
		// 0 时间和空间 如果位置变化 就自动监听
		lm.requestLocationUpdates("gps", 0, 0, new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			// location监听回调结果
			@Override
			public void onLocationChanged(Location location) {
				// 位置发生变化 监听
				float accuracy = location.getAccuracy();// 精确度
				double latitude = location.getLatitude();// 纬度
				double longitude = location.getLongitude();// 经度
				double altitude = location.getAltitude();// 海拔
				//坐标位置 
				String mess = "accuracy:" + accuracy + "\n" + "latitude:"
						+ latitude + "\n" + "longitude:" + longitude + "\n"
						+ "altitude:" + altitude + "\n";
				//发短信给安全号码
				//发送短信给安全号码
				SmsManager smsManager = SmsManager.getDefault();
				
				smsManager.sendTextMessage(SPUtils.getString(getApplicationContext(), MyConstaints.SAFENUMBER, "110"), null, mess, null, null);
			
				//停止监控
				lm.removeUpdates(this);
			}
		});

	}

	@Override
	public void onDestroy() {
		// 服务销毁
		// 取消注册
		unregisterReceiver(mSmsReceiver);
		super.onDestroy();
	}

}
