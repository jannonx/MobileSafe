package com.itheima.mobilesafe13.view;

import javax.crypto.spec.IvParameterSpec;

import com.itheima.mobilesafe13.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingCenterItem extends RelativeLayout {

	private TextView tv_desc;
	private View rootView;
	private boolean isOpen = false;//开关关闭
	private ImageView iv_toggle;
	/**
	 * 布局文件中实例化调用
	 * 
	 * @param context
	 * @param attrs
	 */
	public SettingCenterItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();

		initData(attrs);
		
		initEvent();
	}
	//设置view里的tv的值
	public void setText(String desc){
		tv_desc.setText(desc);
	}
	
	//接口 暴露给需要调用该功能的程序员
	public interface OnToggleChangedListener{
		void onToggleChange(View v,boolean isOpen);
	}
	
	private OnToggleChangedListener mOnToggleChangedListener;
	
	public void setOnToggleChangedListener(OnToggleChangedListener listener){
		this.mOnToggleChangedListener = listener;
	}
	
	public void setToggleOn(boolean isOpen){
		//保存当前的状态
		this.isOpen = isOpen;
		if (isOpen) {
			//设置iv为打开的图片
			iv_toggle.setImageResource(R.drawable.on);
		} else {
			iv_toggle.setImageResource(R.drawable.off);
		}
	}

	private void initEvent() {
		
		
		//给rootView添加点击事件
		rootView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 切换开关的状态
				isOpen = !isOpen;
				
				if (isOpen) {
					//设置iv为打开的图片
					iv_toggle.setImageResource(R.drawable.on);
				} else {
					iv_toggle.setImageResource(R.drawable.off);
				}
				
				//接口
				if (mOnToggleChangedListener != null) {
					//设置了监听器
					mOnToggleChangedListener.onToggleChange(SettingCenterItem.this,isOpen);
				}
			}
		});
		
	}

	private void initData(AttributeSet attrs) {
		// 取出属性
		String desc = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.itheima.mobilesafe13",
				"desc");
		// 取背景选择器
		String bgtype = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.itheima.mobilesafe13",
				"bgselector");
		
		boolean isDisableToggle = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/com.itheima.mobilesafe13", "isdisabletoggle", false);

		if (isDisableToggle) {
			//禁用
			iv_toggle.setVisibility(View.GONE);
		}
		// 设置属性
		tv_desc.setText(desc);

		// 根据bgtype 设置背景选择器
		switch (Integer.parseInt(bgtype)) {
		case 0:// first
			rootView.setBackgroundResource(R.drawable.iv_first_selector);
			break;
		case 1:// middle
			rootView.setBackgroundResource(R.drawable.iv_middle_selector);
			break;
		case 2:// last
			rootView.setBackgroundResource(R.drawable.iv_last_selector);
			break;

		default:
			break;
		}

	}

	private void initView() {

		// 自定义控件
		// view已经添加到Relativelayout中

		rootView = View.inflate(getContext(), R.layout.view_setting_item_view,
				this);

		tv_desc = (TextView) rootView
				.findViewById(R.id.tv_view_settingview_item_desc);
		
		iv_toggle = (ImageView) rootView.findViewById(R.id.iv_view_settingview_item_toggle);
	}

	/**
	 * 代码中实例化
	 * 
	 * @param context
	 */
	public SettingCenterItem(Context context) {
		this(context, null);

	}

}
