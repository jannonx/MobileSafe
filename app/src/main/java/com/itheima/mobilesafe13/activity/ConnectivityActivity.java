package com.itheima.mobilesafe13.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.domain.AppInfoBean;
import com.itheima.mobilesafe13.utils.AppInfoUtils;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Administrator
 * @desc  流量统计的界面
 * @date 2015-9-26
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-26 11:27:05 +0800 (Sat, 26 Sep 2015) $
 * @Id $Id: ConnectivityActivity.java 113 2015-09-26 03:27:05Z goudan $
 * @Rev $Rev: 113 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/activity/ConnectivityActivity.java $
 *
 */
public class ConnectivityActivity extends ListActivity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 3;
	private ListView lv_showdatas;
	
	private List<ConectivityBean>  mConectivityBeans = new ArrayList<ConnectivityActivity.ConectivityBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		int d = 3/0;
		initView();
		
		initData();
	}
	
	private Handler mHandler = new Handler(){
		private ProgressDialog pd;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
				//显示对话框
				
				pd = new ProgressDialog(ConnectivityActivity.this);
				pd.setTitle("注意");
				pd.setMessage("正在读取流量信息");
				pd.show();
				break;
			case FINISH://读取数据完成
				pd.dismiss();
				//显示数据
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};
	private MyAdapter mAdapter;
	private ConnectivityManager mCM;
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mConectivityBeans.size();
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
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (convertView != null) {
				//有缓存复用
				viewHolder = (ViewHolder) convertView.getTag();
			} else {
				convertView = View.inflate(getApplicationContext(), R.layout.item_connectivity_lv, null);
				
				viewHolder = new ViewHolder();
				
				viewHolder.tv_rcv = (TextView) convertView.findViewById(R.id.tv_connectivity_lv_rcv);
				viewHolder.tv_snd = (TextView) convertView.findViewById(R.id.tv_connectivity_lv_snd);
				viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_connectivity_lv_type);
				viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_connectivity_lv_icon);
				
				convertView.setTag(viewHolder);
			}
			
			//显示
			
			ConectivityBean conectivityBean = mConectivityBeans.get(position);
			
			
			viewHolder.iv_icon.setImageDrawable(conectivityBean.icon);//图标
			viewHolder.tv_rcv.setText(Formatter.formatFileSize(getApplicationContext(), conectivityBean.rcvSize));
			viewHolder.tv_snd.setText(Formatter.formatFileSize(getApplicationContext(), conectivityBean.sndSize));
			viewHolder.tv_type.setText(conectivityBean.type);
			
			return convertView;
		}
		
	}
	private class ViewHolder{
		TextView tv_snd;
		TextView tv_rcv;
		ImageView iv_icon;
		TextView tv_type;
	}
	
	/**
	 * @param path
	 *      流量的路径
	 * @return
	 *      流量的大小 byte单位 -1 没有流量
	 */
	private long readFile(String path){
		long size = -1;
		File file = new File(path);
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String line = reader.readLine();
			size = Long.parseLong(line);
			reader.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return size;
	}

	private void initData() {
		new Thread(){
			public void run() {
				//1. 发送加载数据消息
				mHandler.obtainMessage(LOADING).sendToTarget();
				//2. 读取数据
				List<AppInfoBean> allInstalledAppInfos = AppInfoUtils.getAllInstalledAppInfos(getApplicationContext());
				String rcvPath = null;
				String sndPath = null;
				for (AppInfoBean appInfoBean : allInstalledAppInfos) {
					rcvPath = "/proc/uid_stat/" + appInfoBean.getUid() + "/tcp_rcv";
					sndPath = "/proc/uid_stat/" + appInfoBean.getUid() + "/tcp_snd";
					
					//是否有流量信息
					long rcvSize = readFile(rcvPath);
					long sndSize = readFile(sndPath);
					
					if (rcvSize == -1 && sndSize == -1) {
						//没有流量
					} else {
						//有流量
						
						ConectivityBean bean = new ConectivityBean();
						
						bean.icon = appInfoBean.getIcon();
						bean.sndSize = sndSize;
						bean.rcvSize = rcvSize;
						bean.type = mCM.getActiveNetworkInfo().getTypeName();//网络类型的名字
						mConectivityBeans.add(bean);
					}
				}
				
				//2.发送数据完成的消息 
				mHandler.obtainMessage(FINISH).sendToTarget();
				
			};
		}.start();
		
	}
	
	private class ConectivityBean{
		public long rcvSize;//接收大小
		public long sndSize;//发送大小
		public Drawable icon;//图标
		public String type;//类型
	}

	private void initView() {
		lv_showdatas = getListView();
		mAdapter = new MyAdapter();
		lv_showdatas.setAdapter(mAdapter);
		
		mCM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	}
}
