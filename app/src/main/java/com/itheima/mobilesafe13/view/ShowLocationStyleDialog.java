package com.itheima.mobilesafe13.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.utils.MyConstaints;
import com.itheima.mobilesafe13.utils.SPUtils;

public class ShowLocationStyleDialog extends Dialog {

	
	private ListView lv_datas;
	
	public static final  String[] styleNames = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
	public static final  int[]  bgColors = new int[]{R.drawable.call_locate_white,R.drawable.call_locate_orange
			,R.drawable.call_locate_blue,R.drawable.call_locate_gray,R.drawable.call_locate_green};

	public ShowLocationStyleDialog(Context context, int theme) {
		super(context, theme);
		//
		
		//设置样式
		Window mWindow = getWindow();
		LayoutParams layoutParams = mWindow.getAttributes();
		//设置对齐方式底部对齐
		layoutParams.gravity = Gravity.BOTTOM;
		
		//设置参数
		mWindow.setAttributes(layoutParams);
		
	}
	private SettingCenterItem sci_style = null;
	public ShowLocationStyleDialog(Context context,SettingCenterItem sci_style) {
		this(context,R.style.LocationStyle);// 0 代表对话框默认的样式
		// TODO Auto-generated constructor stub
		this.sci_style = sci_style;
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		initView();
		
		initEvent();
	}
	
	private void initEvent() {
		// lv的item点击事件
		lv_datas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 保存点击的位置
				SPUtils.putInt(getContext(), MyConstaints.LOCATIONSTYLEINDEX, position);
				//设置文本
				sci_style.setText("归属地样式(" + styleNames[position]  + ")");
				//关闭对话框
				dismiss();
				
			}
		});
		
	}

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return styleNames.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getContext(), R.layout.item_locationstyledialog_lv, null);
			} 
			
			View v_stylebg = convertView.findViewById(R.id.v_dialogsytle_bg);
			TextView tv_name = (TextView) convertView.findViewById(R.id.tv_dialogstyle_name);
			ImageView iv_select = (ImageView) convertView.findViewById(R.id.iv_dialogstyle_select);
			
			tv_name.setText(styleNames[position]);//样式名字
			v_stylebg.setBackgroundResource(bgColors[position]);//样式背景
			
			//样式的标记（整数） 保存sp中
			if (position == SPUtils.getInt(getContext(), MyConstaints.LOCATIONSTYLEINDEX, 0)) {
				iv_select.setVisibility(View.VISIBLE);
			} else {
				iv_select.setVisibility(View.GONE);
			}
			return convertView;
		}
		
	}

	private void initView() {
		setContentView(R.layout.dialog_locationsytle_view);
		lv_datas = (ListView) findViewById(R.id.lv_locationstyle_dialog_content);
		
		MyAdapter adapter = new MyAdapter();
		lv_datas.setAdapter(adapter);
	}

}
