package com.itheima.mobilesafe13.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.dao.ContactsDao;
import com.itheima.mobilesafe13.domain.ContactBean;
import com.itheima.mobilesafe13.utils.MyConstaints;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-14
 * @desc 显示所有好友信息的界面

 * @version $Rev: 36 $
 * @author $Author: goudan $
 * @Date  $Date: 2015-09-16 15:49:15 +0800 (Wed, 16 Sep 2015) $
 * @Id    $Id: BaseSmsTelFriendsActivity.java 36 2015-09-16 07:49:15Z goudan $
 * @Url   $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/activity/BaseSmsTelFriendsActivity.java $
 * 
 */
public abstract class BaseSmsTelFriendsActivity extends ListActivity {
	private static final int LOADING = 1;
	protected static final int FINISH = 2;
	private ListView lv_datas;

	private List<ContactBean> mDatas = new ArrayList<ContactBean>();//默认数组大小是10个

	private MyAdapater mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//显示所有的好友 
		//1. 数据 好友信息
		
		//2. 定义适配
		//3. 给ListView设置适配器
		initView();
		
		initData();
		
		initEvent();
		
	}
	
	private void initEvent() {
		// 给ListView添加item点击事件
		lv_datas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 获取点击item的数据
				//会调用adapter的getItem方法
				ContactBean bean = mDatas.get(position);
				//ContactBean bean = (ContactBean) lv_datas.getItemAtPosition((int) lv_datas.getItemIdAtPosition(position));
				Intent data = new Intent();
				//设置安全号码
				data.putExtra(MyConstaints.SAFENUMBER, bean.getPhone());
				//保存数据
				setResult(1, data );
				//关闭自己
				finish();
			}
		});
		
	}

	private Handler mHanlder = new Handler(){
		private ProgressDialog pd;

		public void handleMessage(android.os.Message msg) {
			//在主线程中执行
			switch (msg.what) {
			case LOADING:
				//对话框显示加载数据
				lv_datas.setVisibility(View.GONE);
				pd = new ProgressDialog(BaseSmsTelFriendsActivity.this);
				pd.setTitle("注意");
				pd.setMessage("正在玩命加载数据......");
				pd.show();
				break;
			case FINISH:
				//关闭对话框
				pd.dismiss();
				lv_datas.setVisibility(View.VISIBLE);
				//更新数据
				mAdapter.notifyDataSetChanged();//通知界面刷新数据
				break;
			default:
				break;
			}
		};
	};
	
	private void initData() {
		new Thread(){
			public void run() {
				// 1. 提醒用户正在加载数据
				mHanlder.obtainMessage(LOADING).sendToTarget();
				// Message msg = mHanlder.obtainMessage(LOADING)
				// target.sendMessage(this);
				// mHandler.sendMessage(msg);
				
				//2. 加载数据
				mDatas = getDatas();
				SystemClock.sleep(1000);//模拟耗时2秒
				//3. 数据加载完成
				// 发送数据加载完成的消息
				mHanlder.obtainMessage(FINISH).sendToTarget();
				
			};
		}.start();
		
	}
	
	public abstract List<ContactBean> getDatas();

	private class MyAdapater extends BaseAdapter{
 
		@Override
		public int getCount() {
			//返回数据的大小
System.out.println("getCount()");			
			return mDatas.size();
		}

		@Override
		public ContactBean getItem(int position) {
			// 获取适配器中的数据
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			//swing 模式
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
System.out.print(convertView);	
			ViewHolder holder = null;
			// 缓存View
			if(convertView == null) {
				//没有缓存
				//布局转成view
				convertView = View.inflate(getApplicationContext(), R.layout.item_contacts_lv, null);
System.out.println(":" + convertView);				
				holder = new ViewHolder();
				//把子View设置给ViewHolder
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_item_contact_name);
				holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_item_contact_phone);
				//避免重复findViewByid
				//设置标记给contentView
				convertView.setTag(holder);
			}  else {
System.out.println(":" + convertView);
				//取出标记
				holder = (ViewHolder) convertView.getTag();
			}
			
			//获取数据
			ContactBean contactBean = getItem(position);
			
			//设置数据
			holder.tv_name.setText(contactBean.getName());
			holder.tv_phone.setText(contactBean.getPhone());
			
			
			return convertView;
		}
		
	}
	
	private class ViewHolder{
		TextView tv_name;
		TextView tv_phone;
	}

	private void initView() {
		lv_datas = getListView();
		mAdapter = new MyAdapater();
		//设置适配器
		lv_datas.setAdapter(mAdapter);
		
	}
}
