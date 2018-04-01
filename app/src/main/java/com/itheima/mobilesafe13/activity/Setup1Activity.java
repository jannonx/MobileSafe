package com.itheima.mobilesafe13.activity;

import android.os.Bundle;

import com.itheima.mobilesafe13.R;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-13
 * @desc 第一个设置向导界面

 * @version $Rev: 20 $
 * @author $Author: goudan $
 * @Date  $Date: 2015-09-13 16:19:50 +0800 (Sun, 13 Sep 2015) $
 * @Id    $Id: Setup1Activity.java 20 2015-09-13 08:19:50Z goudan $
 * @Url   $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/activity/Setup1Activity.java $
 * 
 */
public class Setup1Activity extends BaseSetupActivity {
	
	protected void initView() {
		setContentView(R.layout.activity_setup1);
	}

	@Override
	protected void startNext() {
		startPage(Setup2Activity.class);
		
	}

	@Override
	protected void startPrev() {
		// TODO Auto-generated method stub
		
	}
}
