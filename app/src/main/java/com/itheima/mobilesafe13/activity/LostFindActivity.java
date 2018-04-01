package com.itheima.mobilesafe13.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.utils.MyConstaints;
import com.itheima.mobilesafe13.utils.SPUtils;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-13
 * @desc 手机防盗界面

 * @version $Rev: 27 $
 * @author $Author: goudan $
 * @Date  $Date: 2015-09-14 14:30:29 +0800 (Mon, 14 Sep 2015) $
 * @Id    $Id: LostFindActivity.java 27 2015-09-14 06:30:29Z goudan $
 * @Url   $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/activity/LostFindActivity.java $
 * 
 */
public class LostFindActivity extends Activity {
	private TextView tv_safenumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//直接进行逻辑判断 是否设置向导完成
		if (SPUtils.getBoolean(getApplicationContext(), MyConstaints.ISSETUPFINISH, false)) {
			//设置向导完成
			initView();
			initData();
		} else {
			//设置向导未完成,进入第一个设置向导界面
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			//关闭自己
			finish();
			
		}
	}
	
	public void enterSetup1(View v){
		//进入第一个设置向导界面
		Intent setup1 = new Intent(this,Setup1Activity.class);
		startActivity(setup1);
		finish();
	}

	private void initData() {
		// 显示安全号码
		tv_safenumber.setText(SPUtils.getString(getApplicationContext(), MyConstaints.SAFENUMBER, ""));
		
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		setContentView(R.layout.activity_lostfind);
		
		tv_safenumber = (TextView) findViewById(R.id.tv_lostfind_safenumber);
		
	}
}
