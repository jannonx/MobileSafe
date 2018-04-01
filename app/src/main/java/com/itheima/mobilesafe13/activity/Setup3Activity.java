package com.itheima.mobilesafe13.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.utils.MyConstaints;
import com.itheima.mobilesafe13.utils.SPUtils;
import com.itheima.mobilesafe13.utils.ShowToast;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-13
 * @desc 第一个设置向导界面
 * 
 * @version $Rev: 26 $
 * @author $Author: goudan $
 * @Date $Date: 2015-09-14 11:32:58 +0800 (Mon, 14 Sep 2015) $
 * @Id $Id: Setup3Activity.java 26 2015-09-14 03:32:58Z goudan $
 * @Url $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/activity/Setup3Activity.java $
 * 
 */
public class Setup3Activity extends BaseSetupActivity {
	private EditText et_safenumber;

	protected void initView() {
		setContentView(R.layout.activity_setup3);
		// 安全号码的编辑框
		et_safenumber = (EditText) findViewById(R.id.et_setup3_safenumber);
	}

	/**
	 * 设置安全号码的事件
	 * 
	 * @param v
	 */
	public void selectSafeNumber(View v) {
		// 启动新的界面来显示所有好友信息
		Intent intent = new Intent(this, FriendsActivity.class);
		// 点击某个好友，关闭界面，在好友编辑框显示选择的好友
		startActivityForResult(intent, 0);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 获取选择的好友
		if (data != null) {
			String safeNum = data.getStringExtra(MyConstaints.SAFENUMBER);
			// 显示编辑中
			et_safenumber.setText(safeNum);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void initData() {
		// 回显安全号码
		et_safenumber.setText(SPUtils.getString(this, MyConstaints.SAFENUMBER,
				""));
		// 设置光标停留的位置
		et_safenumber.setSelection(et_safenumber.getText().toString().trim()
				.length());
		super.initData();
	}

	@Override
	protected void startNext() {
		// 添加获取保存安全号码
		String safeNumber = et_safenumber.getText().toString().trim();
		// 判断是否为空
		if (TextUtils.isEmpty(safeNumber)) {
			// 空
			ShowToast.show("安全号码不能为空", this);
		} else {
			// 有安全号码
			// 保存安全到sp中
			SPUtils.putString(getApplicationContext(), MyConstaints.SAFENUMBER,
					safeNumber);
			// 跳转到下一个页面
			startPage(Setup4Activity.class);
		}

	}

	@Override
	protected void startPrev() {
		startPage(Setup2Activity.class);

	}
}
