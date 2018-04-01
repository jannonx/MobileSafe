package com.itheima.mobilesafe13.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.dao.AddressDao;
import com.itheima.mobilesafe13.domain.NumberAndName;
import com.itheima.mobilesafe13.domain.ServiceNameAndType;

/**
 * @author Administrator
 * @desc
 * @date 2015-9-18
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-18 15:18:09 +0800 (Fri, 18 Sep 2015) $
 * @Id $Id: ServiceNumberActivity.java 53 2015-09-18 07:18:09Z goudan $
 * @Rev $Rev: 53 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/activity/ServiceNumberActivity.java $
 *
 */
public class ServiceNumberActivity extends Activity {
	protected static final int LOADING = 1;

	protected static final int FINISH = 2;

	private ExpandableListView elv_showmess;
	
	//保存Elv 组数据  和 每组对应的数据
	//private HashMap<ServiceNameAndType, List<NumberAndName>> mDatas = new HashMap<ServiceNameAndType, List<NumberAndName>>();
	private List<ServiceNameAndType> mServiceNameAndTypes = new ArrayList<ServiceNameAndType>();
	private List<List<NumberAndName>> mNumberAndNames = new ArrayList<List<NumberAndName>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initEvent();
	}
	
	private void initEvent() {
		// 添加列表点击事件
		elv_showmess.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				//获取当前数据 拨打电话
				String number = mNumberAndNames.get(groupPosition).get(childPosition).getNumber();
				//启动外拨电话界面
				Intent call = new Intent(Intent.ACTION_CALL);
				call.setData(Uri.parse("tel:" + number));
				startActivity(call);
				return true;
			}
		});
		
	}

	//handler处理结果
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//处理消息
			switch (msg.what) {
			case LOADING:
//				正在加载数据
				ll_loading.setVisibility(View.VISIBLE);
				elv_showmess.setVisibility(View.GONE);
				break;
			case FINISH:
				//加载数据完成
				ll_loading.setVisibility(View.GONE);
				elv_showmess.setVisibility(View.VISIBLE);
				
				//刷新数据
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

	private LinearLayout ll_loading;

	private MyAdapter mAdapter;

	private void initData() {
		//子线程取数据
		new Thread(){
			public void run() {
				//1. 取数据的消息 
				mHandler.obtainMessage(LOADING).sendToTarget();
				//2. 取数据
				
				List<ServiceNameAndType> allServiceTypes = AddressDao.getAllServiceTypes();
				//获取组的信息
				mServiceNameAndTypes = allServiceTypes;
				for (ServiceNameAndType serviceNameAndType : allServiceTypes) {
					List<NumberAndName> numberAndNames = AddressDao.getNumberAndNames(serviceNameAndType);
					//添加数据
					mNumberAndNames.add(numberAndNames);
				}
				//3. 发送取数据完成的消息
				
				mHandler.obtainMessage(FINISH).sendToTarget();
				
			};
		}.start();
	}
	
	private class MyAdapter extends BaseExpandableListAdapter{

		@Override
		public NumberAndName getChild(int groupPosition, int childPosition) {
			// 
			return mNumberAndNames.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// 组的子View的显示
			TextView tv = null;
			if (convertView == null) {
				tv = new TextView(getApplicationContext());
				tv.setTextSize(18);
				tv.setTextColor(Color.BLACK);
				//tv.setBackgroundColor(Color.GRAY);
			} else {
				tv = (TextView) convertView;
			}
			
			tv.setBackgroundResource(R.drawable.iv_middle_selector);
			//赋值
			NumberAndName numberAndName = mNumberAndNames.get(groupPosition).get(childPosition);
			tv.setText(numberAndName.getName() + " " + numberAndName.getNumber());
			return tv;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// 每个组多少个数据
			
			return mNumberAndNames.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getGroupCount() {
			// 获取多少个组
			return mServiceNameAndTypes.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// 组的显示
			TextView tv = null;
			if (convertView == null) {
				tv = new TextView(getApplicationContext());
				tv.setTextSize(20);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.BLUE);
			} else {
				tv = (TextView) convertView;
			}
			
			//赋值
			tv.setText(mServiceNameAndTypes.get(groupPosition).getName());
			
			return tv;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
		
	}



	private void initView() {
		setContentView(R.layout.activity_servicenumber);
		ll_loading = (LinearLayout) findViewById(R.id.ll_progressbar_root);
		elv_showmess = (ExpandableListView) findViewById(R.id.elv_servicenumber_show);
		elv_showmess.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mAdapter = new MyAdapter();
		elv_showmess.setAdapter(mAdapter);
	}
}
