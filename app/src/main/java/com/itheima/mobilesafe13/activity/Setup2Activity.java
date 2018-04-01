package com.itheima.mobilesafe13.activity;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.utils.MyConstaints;
import com.itheima.mobilesafe13.utils.SPUtils;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-13
 * @desc 第一个设置向导界面
 * 
 * @version $Rev: 22 $
 * @author $Author: goudan $
 * @Date $Date: 2015-09-13 16:57:06 +0800 (Sun, 13 Sep 2015) $
 * @Id $Id: Setup2Activity.java 22 2015-09-13 08:57:06Z goudan $
 * @Url $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/activity/Setup2Activity.java $
 * 
 */
public class Setup2Activity extends BaseSetupActivity {

	private ImageView iv_setup2_simbind_icon;

	@Override
	protected void initData() {
		// 初始化sim的状态
		String simNumber = SPUtils.getString(getApplicationContext(),
				MyConstaints.SIMNUMBER, null);
		if (TextUtils.isEmpty(simNumber)) {
			// 为绑定
			iv_setup2_simbind_icon.setImageResource(R.drawable.unlock);
		} else {
			// 已绑定
			iv_setup2_simbind_icon.setImageResource(R.drawable.lock);
		}

		super.initData();
	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_setup2);
		// sim卡的是否绑定的图标
		iv_setup2_simbind_icon = (ImageView) findViewById(R.id.iv_setup2_simbind_icon);
	}

	@Override
	protected void startNext() {
		// 不绑定 不进行页面切换
		// 初始化sim的状态
		String simNumber = SPUtils.getString(getApplicationContext(),
				MyConstaints.SIMNUMBER, null);
		if (TextUtils.isEmpty(simNumber)) {
			//没有绑定 不进行下一步
			Toast.makeText(getApplicationContext(), "请先绑定sim卡", 1).show();
		} else {
			//页面跳转
			startPage(Setup3Activity.class);
		}

		

	}

	@Override
	protected void startPrev() {
		startPage(Setup1Activity.class);

	}

	/**
	 * 绑定sim卡的事件
	 * 
	 * @param v
	 */
	public void bindSim(View v) {
		// 判断是否绑定
		String simNumber = SPUtils.getString(getApplicationContext(),
				MyConstaints.SIMNUMBER, null);
		if (TextUtils.isEmpty(simNumber)) {
			// 没有绑定
			// 绑定sim 取出sim信息 保存到sp中
			TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			String simSerialNumber = tm.getSimSerialNumber();
			// 保存
			SPUtils.putString(getApplicationContext(), MyConstaints.SIMNUMBER,
					simSerialNumber);

			// 修改图标
			iv_setup2_simbind_icon.setImageResource(R.drawable.lock);
		} else {
			// 已经绑定
			SPUtils.putString(getApplicationContext(), MyConstaints.SIMNUMBER,
					"");
			// 修改图标
			iv_setup2_simbind_icon.setImageResource(R.drawable.unlock);
		}

	}

}
