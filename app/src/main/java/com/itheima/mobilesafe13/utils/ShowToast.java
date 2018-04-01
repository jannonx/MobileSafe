package com.itheima.mobilesafe13.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-14
 * @desc 土司的封装

 * @version $Rev: 23 $
 * @author $Author: goudan $
 * @Date  $Date: 2015-09-14 08:49:17 +0800 (Mon, 14 Sep 2015) $
 * @Id    $Id: ShowToast.java 23 2015-09-14 00:49:17Z goudan $
 * @Url   $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/utils/ShowToast.java $
 * 
 */
public class ShowToast {
	public static void show(final String mess,final Activity context){
		/*//判断是否是主线程
		if (Thread.currentThread().getName().equals("main")) {
			//主线程中弹出土司
			Toast.makeText(context, mess, 1).show();
		} else {
			//子线程
			context.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					Toast.makeText(context, mess, 1).show();
				}
			});
		}*/
		context.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				//自动判断是否是主线程
				Toast.makeText(context, mess, 1).show();
			}
		});
	}
}
