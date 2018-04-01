package com.itheima.mobilesafe13.receiver;

import java.util.List;

import com.itheima.mobilesafe13.domain.AppInfoBean;
import com.itheima.mobilesafe13.utils.TaskInfoUtils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WidgetClearTaskReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//清理进程
		List<AppInfoBean> allRunningAppInfos = TaskInfoUtils.getAllRunningAppInfos(context);
		for (AppInfoBean appInfoBean : allRunningAppInfos) {
			am.killBackgroundProcesses(appInfoBean.getPackName());
		}
		System.out.println("清理进程widget");
	}
	

}
