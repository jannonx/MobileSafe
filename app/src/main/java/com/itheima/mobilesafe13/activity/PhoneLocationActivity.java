package com.itheima.mobilesafe13.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.dao.AddressDao;

public class PhoneLocationActivity extends Activity {
	private EditText et_phone;
	private TextView tv_showlocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();

		initEvent();
	}

	private void initEvent() {
		// 监听文本的变化事件
		et_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 文本变化
				// 动态查询
				query(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// 文件变化前
				System.out.println("beforeTextChanged");
			}

			@Override
			public void afterTextChanged(Editable s) {
				// 文件变化后
				System.out.println("afterTextChanged");
			}
		});

	}

	public void query(View v) {
		// 查询
		String phone = et_phone.getText().toString().trim();

		if (TextUtils.isEmpty(phone)) {
			// 抖动效果
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_phone.startAnimation(shake);

			// 震动效果
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(new long[] { 300, 100, 200, 300, 200, 200, 400,
					100, 500, 200 }, 3);
			// ShowToast.show("号码不能为空", this);
			return;
		}

		// 查询
		// 判断移动号还是固定号码
		try {
			String location = AddressDao.getLocation(phone);
			// 显示
			tv_showlocation.setText("归属地:\n" + location);
		} catch (Exception e) {
			//tv_showlocation.setText("归属地:\n" + location);
		}
		

	}

	private void initView() {
		setContentView(R.layout.activity_phonelocation);
		et_phone = (EditText) findViewById(R.id.et_phonelocation_phone);
		tv_showlocation = (TextView) findViewById(R.id.tv_phonelocation_showlocation);
	}
}
