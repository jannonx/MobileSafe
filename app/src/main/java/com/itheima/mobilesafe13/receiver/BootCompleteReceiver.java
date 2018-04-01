package com.itheima.mobilesafe13.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.itheima.mobilesafe13.activity.Setup4Activity;
import com.itheima.mobilesafe13.service.LostFindService;
import com.itheima.mobilesafe13.utils.MyConstaints;
import com.itheima.mobilesafe13.utils.SPUtils;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-14
 * @desc 系统启动完成的广播监听

 * @version $Rev: 27 $
 * @author $Author: goudan $
 * @Date  $Date: 2015-09-14 14:30:29 +0800 (Mon, 14 Sep 2015) $
 * @Id    $Id: BootCompleteReceiver.java 27 2015-09-14 06:30:29Z goudan $
 * @Url   $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/receiver/BootCompleteReceiver.java $
 * 
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 系统启动完成
		
		// 1. 检测sim卡是否变更
		//获取当前的sim卡 和 保存的SIM卡比较
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String currentSerialNumber = tm.getSimSerialNumber();
		//if (!currentSerialNumber.equals(SPUtils.getString(context, MyConstaints.SIMNUMBER, ""))){
		if (currentSerialNumber.equals(SPUtils.getString(context, MyConstaints.SIMNUMBER, ""))){//测试
			//sim卡不一致
			//发送短信给安全号码
			SmsManager smsManager = SmsManager.getDefault();
			//
			smsManager.sendTextMessage(SPUtils.getString(context, MyConstaints.SAFENUMBER, "110"), null, "i am xiaotou  xinhao", null, null);
		}
		// 2. 启动防盗服务
		if (SPUtils.getBoolean(context, MyConstaints.BOOTCOMPLETE, false)) {
			// 启动服务
			// 开启防盗服务
			Intent service = new Intent(context,
					LostFindService.class);
			context.startService(service); 
		}

	}

}
