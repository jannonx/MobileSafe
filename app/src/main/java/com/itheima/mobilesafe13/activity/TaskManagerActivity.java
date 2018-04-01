package com.itheima.mobilesafe13.activity;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.domain.AppInfoBean;
import com.itheima.mobilesafe13.service.ScreenOffClearTaskService;
import com.itheima.mobilesafe13.utils.AppInfoUtils;
import com.itheima.mobilesafe13.utils.MyConstaints;
import com.itheima.mobilesafe13.utils.SPUtils;
import com.itheima.mobilesafe13.utils.ServiceUtils;
import com.itheima.mobilesafe13.utils.TaskInfoUtils;
import com.itheima.mobilesafe13.view.SettingCenterItem;
import com.itheima.mobilesafe13.view.SettingCenterItem.OnToggleChangedListener;
import com.itheima.mobilesafe13.view.TextProgressView;

/**
 * @author Administrator
 * @desc 进程管家
 * @date 2015-9-22
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-22 16:38:38 +0800 (Tue, 22 Sep 2015) $
 * @Id $Id: TaskManagerActivity.java 92 2015-09-22 08:38:38Z goudan $
 * @Rev $Rev: 92 $
 * @URL $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/activity/TaskManagerActivity.java $
 * 
 */
public class TaskManagerActivity extends Activity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private ListView lv_datas;
	private TextProgressView tpv_memory_info;
	private TextProgressView tpv_processnumber_info;
	private LinearLayout ll_loading;
	private TextView tv_lv_tag;

	private long totalMem;
	private long availMem;
	private int allInstalledAppNumber;

	// 用户进程数据
	private List<AppInfoBean> userAppInfoBeans = new Vector<AppInfoBean>();
	// 系统进程数据
	private List<AppInfoBean> sysAppInfoBeans = new Vector<AppInfoBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();

		initData();

		initEvent();
		
		initAniamtion();
		
		showUp();
	}
	

	@Override
	protected void onResume() {
		initData();
		super.onResume();
	}
	/**
	 * 箭头向上
	 */
	private void showUp(){
		iv_arrow1.setImageResource(R.drawable.drawer_arrow_up);
		iv_arrow2.setImageResource(R.drawable.drawer_arrow_up);
		
		//开始动画
		iv_arrow1.startAnimation(aa1);
		iv_arrow2.startAnimation(aa2);
		
	}
	
	/**
	 * 箭头向下
	 */
	private void showDown(){
		//停止动画
		iv_arrow1.clearAnimation();
		iv_arrow2.clearAnimation();
		
		iv_arrow1.setImageResource(R.drawable.drawer_arrow_down);
		iv_arrow2.setImageResource(R.drawable.drawer_arrow_down);
	}

	private void initAniamtion() {
		//半透明到透明
		
		aa1 = new AlphaAnimation(0.5f, 1f);
		aa1.setDuration(500);
		aa1.setRepeatCount(Animation.INFINITE);//无限次数
		
		
		aa2 = new AlphaAnimation(1f, 0.5f);
		aa2.setDuration(500);
		aa2.setRepeatCount(Animation.INFINITE);//无限次数
		
		
	}

	private void initEvent() {
		
		//添加抽屉的item点击事件 
		sci_showsystem.setOnToggleChangedListener(new OnToggleChangedListener() {
			
			@Override
			public void onToggleChange(View v, boolean isOpen) {
				//记录是否显示系统进程的状态
				SPUtils.putBoolean(getApplicationContext(), MyConstaints.SHOWSYSTEM, isOpen);
				
				//更新lv
				mAdapter.notifyDataSetChanged();
			}
		});
		sci_cleartask.setOnToggleChangedListener(new OnToggleChangedListener() {
			
			@Override
			public void onToggleChange(View v, boolean isOpen) {
				//注册锁屏清理进程
				if (isOpen) {
					//注册锁屏清理进程
					Intent service = new Intent(TaskManagerActivity.this,ScreenOffClearTaskService.class);
					startService(service);
				} else {
					//取消注册
					Intent service = new Intent(TaskManagerActivity.this,ScreenOffClearTaskService.class);
					stopService(service);
				}
				
			}
		});
		
		//监听抽屉打开或关闭事件
		sd_arrow.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			
			@Override
			public void onDrawerOpened() {
				showDown();
				
			}
		});
		
		sd_arrow.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				showUp();
				
			}
		});
		// lv item点击事件
		lv_datas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position == 0 || position == userAppInfoBeans.size() + 1) {
					// 点击是标签
					return;
				}
				// 点击条目 改变 checkbox的状态
				// 获取条目数据
				AppInfoBean itemAtPosition = (AppInfoBean) lv_datas
						.getItemAtPosition(position);
				// 改变数据的状态
				itemAtPosition.setChecked(!itemAtPosition.isChecked());

				if (itemAtPosition.getPackName().equals(getPackageName())) {
					// 自己
					itemAtPosition.setChecked(false);
				}
				// 更新界面
				mAdapter.notifyDataSetChanged();

			}
		});
		// lv滑动事件
		lv_datas.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 判断第一个显示数据的位置
				if (firstVisibleItem >= userAppInfoBeans.size() + 1) {
					// 系统标签
					tv_lv_tag.setText("系统软件(" + sysAppInfoBeans.size() + ")");
				} else {
					// 用户
					tv_lv_tag.setText("用户软件(" + userAppInfoBeans.size() + ")");
				}

			}
		});

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:// 正在加载数据
				ll_loading.setVisibility(View.VISIBLE);
				lv_datas.setVisibility(View.GONE);
				tv_lv_tag.setVisibility(View.GONE);
				break;
			case FINISH:// 加载数据完成
				ll_loading.setVisibility(View.GONE);
				lv_datas.setVisibility(View.VISIBLE);
				tv_lv_tag.setVisibility(View.VISIBLE);

				//初始化显示 
				viewMemNumMess();

				// 3. lv的数据更新
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}

		
	};
	private void viewMemNumMess() {
		// 显示数据
		// 1. 显示进程个数的信息
		
		int taskNumber = (userAppInfoBeans.size() + sysAppInfoBeans
				.size());
		tpv_processnumber_info.setMessage("运行中的进程：" + taskNumber);
		tpv_processnumber_info.setProgress(taskNumber * 1.0
				/ allInstalledAppNumber);

		// 2. 内存的使用
		tpv_memory_info.setMessage("可用内存/总内存："
				+ Formatter.formatFileSize(getApplicationContext(),
						availMem)
				+ "/"
				+ Formatter.formatFileSize(getApplicationContext(),
						totalMem));
		tpv_memory_info.setProgress((totalMem - availMem) * 1.0
				/ totalMem);
	};
	private MyAdapter mAdapter;
	private ActivityManager mAM;
	private AlphaAnimation aa1;
	private AlphaAnimation aa2;
	private ImageView iv_arrow1;
	private ImageView iv_arrow2;
	private SlidingDrawer sd_arrow;
	private SettingCenterItem sci_showsystem;
	private SettingCenterItem sci_cleartask;

	private void initData() {
		
		//初始化清理进程的状态
		sci_cleartask.setToggleOn(ServiceUtils.isServiceRunning(getApplicationContext(), "com.itheima.mobilesafe13.service.ScreenOffClearTaskService"));
		
		//初始化 是否显示系统进程的状态
		sci_showsystem.setToggleOn(SPUtils.getBoolean(this, MyConstaints.SHOWSYSTEM, true));
		// 子线程获取数据

		new Thread() {

			public void run() {
				// 1. 发送正在数据
				mHandler.obtainMessage(LOADING).sendToTarget();

				// 2. 获取数据

				List<AppInfoBean> allRunningAppInfos = TaskInfoUtils
						.getAllRunningAppInfos(getApplicationContext());
				sysAppInfoBeans.clear();
				userAppInfoBeans.clear();
				// 分类数据
				for (AppInfoBean appInfoBean : allRunningAppInfos) {
					if (appInfoBean.isSystem()) {
						// 系统
						sysAppInfoBeans.add(appInfoBean);
					} else {
						// 用户
						userAppInfoBeans.add(appInfoBean);
					}
				}

				// 内存

				totalMem = TaskInfoUtils.getTotalMem();
				availMem = TaskInfoUtils.getAvailMem(getApplicationContext());

				// 总app个数

				allInstalledAppNumber = AppInfoUtils
						.getAllInstalledAppNumber(getApplicationContext());

				SystemClock.sleep(1000);
				// 3. 发送数据加载完成的消息
				mHandler.obtainMessage(FINISH).sendToTarget();

			};
		}.start();

	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (SPUtils.getBoolean(getApplicationContext(), MyConstaints.SHOWSYSTEM, true)) {
				//显示系统进程
				// 数据个数
				return userAppInfoBeans.size() + 1 + sysAppInfoBeans.size() + 1;
			
			} else {
				//不显示系统进程O
				return userAppInfoBeans.size() + 1;
			}
		}
		

		@Override
		public AppInfoBean getItem(int position) {
			AppInfoBean bean = null;
			// 工具position 获取bean
			if (position <= userAppInfoBeans.size()) {
				bean = userAppInfoBeans.get(position - 1);
			} else {
				// 系统
				bean = sysAppInfoBeans.get(position
						- (userAppInfoBeans.size() + 2));
			}
			return bean;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// view
			// 2个标签
			if (position == 0) {
				// 用户app标签
				TextView tv_usertag = new TextView(getApplicationContext());
				tv_usertag.setBackgroundColor(Color.GRAY);// 背景灰色
				tv_usertag.setTextColor(Color.WHITE);// 字体白色
				tv_usertag.setText("用户软件(" + userAppInfoBeans.size() + ")");
				return tv_usertag;
			} else if (position == userAppInfoBeans.size() + 1) {
				// 系统app标签
				TextView tv_usertag = new TextView(getApplicationContext());
				tv_usertag.setBackgroundColor(Color.GRAY);// 背景灰色
				tv_usertag.setTextColor(Color.WHITE);// 字体白色
				tv_usertag.setText("系统软件(" + sysAppInfoBeans.size() + ")");
				return tv_usertag;
			} else {
				// 自定义View
				ViewHolder viewHolder = null;
				if (convertView == null || convertView instanceof TextView) {
					// 创建view
					convertView = View.inflate(getApplicationContext(),
							R.layout.item_taskmanager_lv, null);
					viewHolder = new ViewHolder();

					// 名字
					viewHolder.tv_appname = (TextView) convertView
							.findViewById(R.id.tv_taskmanager_lv_taskname);
					// 内存
					viewHolder.tv_appmemsize = (TextView) convertView
							.findViewById(R.id.tv_taskmanager_lv_taskmemorysize);
					// 图标
					viewHolder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_taskmanager_lv_icon);
					// 是否勾选
					viewHolder.cb_checked = (CheckBox) convertView
							.findViewById(R.id.cb_taskmanager_isselect);
					convertView.setTag(viewHolder);
				} else {
					// convertView R.layout.item_taskmanager_lv
					viewHolder = (ViewHolder) convertView.getTag();
				}

				// 显示信息
				final AppInfoBean bean = getItem(position);

				viewHolder.tv_appname.setText(bean.getAppName());
				viewHolder.tv_appmemsize.setText(Formatter.formatFileSize(
						getApplicationContext(), bean.getMemSize()));
				viewHolder.iv_icon.setImageDrawable(bean.getIcon());
				// 复选框

				viewHolder.cb_checked
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								// 记录勾选的状态
								bean.setChecked(isChecked);

							}
						});

				// 设置复选框的初始状态
				viewHolder.cb_checked.setChecked(bean.isChecked());

				// 设置自己隐藏
				if (bean.getPackName().equals(getPackageName())) {
					// 自己
					viewHolder.cb_checked.setVisibility(View.GONE);
				} else {
					// 不是自己
					viewHolder.cb_checked.setVisibility(View.VISIBLE);
				}
				return convertView;
			}

		}

	}

	private class ViewHolder {
		TextView tv_appname;
		CheckBox cb_checked;
		TextView tv_appmemsize;
		ImageView iv_icon;
	}

	private void initView() {
		setContentView(R.layout.acitivity_taskmanager);
		// lv显示进程信息
		lv_datas = (ListView) findViewById(R.id.lv_tastmanager_viewdatas);

		mAdapter = new MyAdapter();
		lv_datas.setAdapter(mAdapter);

		// 内存信息
		tpv_memory_info = (TextProgressView) findViewById(R.id.tpv_tastmanager_memory_mess);
		// 进程信息
		tpv_processnumber_info = (TextProgressView) findViewById(R.id.tpv_tastmanager_processnumber_mess);

		// 显示加载数据进度
		ll_loading = (LinearLayout) findViewById(R.id.ll_progressbar_root);

		// lv的tag
		tv_lv_tag = (TextView) findViewById(R.id.tv_tastmanager_lvtag);
		
		
		iv_arrow1 = (ImageView) findViewById(R.id.iv_arrow1);
		iv_arrow2 = (ImageView) findViewById(R.id.iv_arrow2);
		
		sd_arrow = (SlidingDrawer) findViewById(R.id.slidingDrawer1);
		
		sci_showsystem = (SettingCenterItem) findViewById(R.id.sci_sd_showsystem);
		sci_cleartask = (SettingCenterItem) findViewById(R.id.sci_sd_cleartask);

		mAM = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	}

	/**
	 * @param v
	 * 
	 *            反选
	 */
	public void fanSelect(View v) {
		// user
		for (AppInfoBean bean : userAppInfoBeans) {
			// 过滤自己
			if (bean.getPackName().equals(getPackageName())) {
				continue;
			}
			// 设置反选状态
			bean.setChecked(!bean.isChecked());
		}

		// system
		for (AppInfoBean bean : sysAppInfoBeans) {

			// 设置反选状态
			bean.setChecked(!bean.isChecked());
		}

		// 通知ui
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * @param v
	 *            全选
	 */
	public void selectAll(View v) {
		for (AppInfoBean bean : userAppInfoBeans) {
			// 过滤自己
			if (bean.getPackName().equals(getPackageName())) {
				continue;
			}
			// 设置反选状态
			bean.setChecked(true);
		}

		// system
		for (AppInfoBean bean : sysAppInfoBeans) {

			// 设置反选状态
			bean.setChecked(true);
		}

		// 通知ui
		mAdapter.notifyDataSetChanged();
	}
	
	/**
	 * @param v
	 * 
	 * 清理选中的进程
	 */
	public void clearTask(View v){
		
		long clearSize = 0;//记录清理的总内存大小
		int clearNumber = 0;//记录清理进程个数
		
		for (int i = 0; i < userAppInfoBeans.size() ; i++){
			AppInfoBean bean = userAppInfoBeans.get(i);
			if (bean.isChecked()) {
				clearNumber++;
				clearSize += bean.getMemSize();
				//清理
				mAM.killBackgroundProcesses(bean.getPackName());
				//界面效果
				userAppInfoBeans.remove(i--);// indexof bean equals&hashcode  index 
				
			}
		}
		
		for (int i = sysAppInfoBeans.size() - 1; i >= 0 ; i--){
			AppInfoBean bean = sysAppInfoBeans.get(i);
			if (bean.isChecked()) {
				clearNumber++;
				clearSize += bean.getMemSize();
				//清理
				mAM.killBackgroundProcesses(bean.getPackName());
				//界面效果
				sysAppInfoBeans.remove(i);// indexof bean equals&hashcode  index 
				
			}
		}
		
		//提醒清理工作
		Toast.makeText(getApplicationContext(), "清理了" + clearNumber + "个进程，释放了" + 
		Formatter.formatFileSize(getApplicationContext(), clearSize)
		+ "内存", 1).show();
		
		
		//listview界面更新 
		mAdapter.notifyDataSetChanged();
		//内存显示
		availMem += clearSize;
		viewMemNumMess();
		
		//记录下清理的时间
		if ((sysAppInfoBeans.size() + userAppInfoBeans.size()) < 3){
			SPUtils.putLong(getApplicationContext(), MyConstaints.CLEARTIME, System.currentTimeMillis());
		}
		
		/*
		for (AppInfoBean bean : userAppInfoBeans) {// iterator  iterator.remove
			if (bean.isChecked()) {
				//清理
				mAM.killBackgroundProcesses(bean.getPackName());
				//界面效果
				userAppInfoBeans.remove(bean);/// 
			}
		}

		// system
		for (AppInfoBean bean : sysAppInfoBeans) {

			if (bean.isChecked()) {
				//清理
				mAM.killBackgroundProcesses(bean.getPackName());
				//界面效果
				sysAppInfoBeans.remove(bean);
			}
		}*/
		
		///清理进程
		
		
	}
}
