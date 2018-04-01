package com.itheima.mobilesafe13.service;

import java.util.Timer;
import java.util.TimerTask;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.receiver.MyAppWidgetProvider;
import com.itheima.mobilesafe13.utils.TaskInfoUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

/**
 * @author Administrator
 * @desc  清理进程的widget的服务
 * @date 2015-9-22
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-22 17:31:07 +0800 (Tue, 22 Sep 2015) $
 * @Id $Id: TaskWidgetService.java 93 2015-09-22 09:31:07Z goudan $
 * @Rev $Rev: 93 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/service/TaskWidgetService.java $
 *
 */
public class TaskWidgetService extends Service {

	private AppWidgetManager mAWM;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		mAWM = AppWidgetManager.getInstance(getApplicationContext());
		System.out.println("widget  service create");
		
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				updateWidgetMessage();
				
			}
		};
		timer.schedule(task, 0 , 1000 * 2);
		super.onCreate();
	}
	
	protected void updateWidgetMessage() {
		ComponentName provider = new ComponentName(getApplicationContext(), MyAppWidgetProvider.class);
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
		views.setTextViewText(R.id.tv_process_count, "运行中的软件:" + TaskInfoUtils.getAllRunningAppInfos(getApplicationContext()).size());
		views.setTextViewText(R.id.tv_process_memory, "可用内存:" + Formatter.formatFileSize(getApplicationContext(),
				TaskInfoUtils.getAvailMem(getApplicationContext())));
		
		Intent intent = new Intent();
		intent.setAction("widget.clear.task");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent , 0);
		views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent );
		// 更新widget界面
		mAWM.updateAppWidget(provider, views);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("widget  service stop");
		super.onDestroy();
	}

}
