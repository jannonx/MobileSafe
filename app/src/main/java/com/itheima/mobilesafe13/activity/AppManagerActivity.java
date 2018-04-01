package com.itheima.mobilesafe13.activity;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.domain.AppInfoBean;
import com.itheima.mobilesafe13.utils.AppInfoUtils;
import com.itheima.mobilesafe13.view.TextProgressView;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

/**
 * @author Administrator
 * @desc 软件管家界面
 * @date 2015-9-21
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-22 08:51:33 +0800 (Tue, 22 Sep 2015) $
 * @Id $Id: AppManagerActivity.java 83 2015-09-22 00:51:33Z goudan $
 * @Rev $Rev: 83 $
 * @URL $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/activity/AppManagerActivity.java $
 * 
 */
public class AppManagerActivity extends Activity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private TextView tv_rommem;
	private TextView tv_sdmem;
	private StickyListHeadersListView lv_datas;
	private LinearLayout ll_loading;

	// 所有app
	private List<AppInfoBean> allInstalledAppInfos = new Vector<AppInfoBean>();
	private long phoneAvailMem;
	private long sdAvailMem;
	private long sdTotalMem;
	private long phoneTotalMem;

	// 系统app容器
	private List<AppInfoBean> systemAllInstalledAppInfos = new Vector<AppInfoBean>();
	// 用户app容器
	private List<AppInfoBean> userAllInstalledAppInfos = new Vector<AppInfoBean>();

	private MyAdapter mAdapter;// 适配器

	private AppInfoBean clickedAppInfoBean;// 点击数据

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();

		initEvent();
		// initData();

		initPopupWindow();

		registRemoveAppReceiver();
		System.out.println("onCreate");
	}

	private void registRemoveAppReceiver() {
		mReceiver = new RemoveAppReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);

		filter.addDataScheme("package");
		registerReceiver(mReceiver, filter);
		System.out.println("注册广播");

	}

	// 删除app的监听
	private class RemoveAppReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			initData();// 初始化数据 卸载系统软件
			System.out.println("app remove............");
		}

	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		System.out.println("onResume");
		initData();// 初始化数据
		super.onResume();
	}

	private void initPopupWindow() {
		// 初始化弹出窗体
		View mPopupViewRoot = View.inflate(getApplicationContext(),
				R.layout.popupwindow_appmanager_view, null);

		LinearLayout ll_uninstall = (LinearLayout) mPopupViewRoot
				.findViewById(R.id.ll_appmanager_uninstall);
		LinearLayout ll_start = (LinearLayout) mPopupViewRoot
				.findViewById(R.id.ll_appmanager_start);
		LinearLayout ll_share = (LinearLayout) mPopupViewRoot
				.findViewById(R.id.ll_appmanager_share);
		LinearLayout ll_setting = (LinearLayout) mPopupViewRoot
				.findViewById(R.id.ll_appmanager_setting);

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.ll_appmanager_uninstall:
					// 卸载
					uninstall();
					break;
				case R.id.ll_appmanager_setting:
					// 设置
					setting();
					break;
				case R.id.ll_appmanager_share:
					// 分享
					share();
					break;
				case R.id.ll_appmanager_start:
					// 启动
					startApp();
					break;
				default:
					break;
				}

				// 关闭popupwindow
				mPW.dismiss();

			}
		};
		ll_uninstall.setOnClickListener(listener);
		ll_start.setOnClickListener(listener);
		ll_share.setOnClickListener(listener);
		ll_setting.setOnClickListener(listener);

		mPW = new PopupWindow(mPopupViewRoot, -2, -2);

		// 获取焦点 保证里面的组件可以点击
		mPW.setFocusable(true);

		// 设置背景
		mPW.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		// 外部点击消失
		mPW.setOutsideTouchable(true);

		// 动画
		mPW.setAnimationStyle(R.style.PPDialog);

	}

	protected void startApp() {
		// System.out.println("startApp" + clickedAppInfoBean.getPackName());
		Intent launchIntentForPackage = getPackageManager()
				.getLaunchIntentForPackage(clickedAppInfoBean.getPackName());
		startActivity(launchIntentForPackage);
	}

	protected void share() {
		// TODO Auto-generated method stub
		// 公众平台
		showShare();
	}

	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("hm13期测试");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(this);
	}

	protected void setting() {
		// TODO Auto-generated method stub
		// System.out.println("setting" + clickedAppInfoBean.getPackName());
		Intent setting = new Intent(
				"android.settings.APPLICATION_DETAILS_SETTINGS");
		setting.setData(Uri.parse("package:" + clickedAppInfoBean.getPackName()));
		startActivity(setting);
	}

	protected void uninstall() {
		// 删除
		// 应用app
		if (clickedAppInfoBean.isSystem()) {
			// 系统app

			try {
				RootTools.sendShell("mount -o remount rw /system", 5000);
				RootTools.sendShell(
						"rm -r " + clickedAppInfoBean.getSourceDir(), 5000);
				RootTools.sendShell("mount -o remount r /system", 5000);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RootToolsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// 用户app
			/*
			 * <intent-filter> <action android:name="android.intent.action.VIEW"
			 * /> <action android:name="android.intent.action.DELETE" />
			 * <category android:name="android.intent.category.DEFAULT" /> <data
			 * android:scheme="package" /> </intent-filter>
			 */
			Intent uninstall = new Intent("android.intent.action.DELETE");
			uninstall.setData(Uri.parse("package:"
					+ clickedAppInfoBean.getPackName()));
			startActivityForResult(uninstall, 0);
			// 刷新界面
		}
		// 系统app

		// System.out.println("uninstall"+ clickedAppInfoBean.getPackName());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// allInstalledAppInfos.remove(clickedAppInfoBean);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initEvent() {

		lv_datas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 显示popupWindow

				mPW.showAsDropDown(view, 45, -view.getHeight());
				// 保留点击的数据
				synchronized (allInstalledAppInfos) {
					clickedAppInfoBean = allInstalledAppInfos.get(position);
				}

			}
		});

		lv_datas.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				// 关闭pw
				if (mPW != null && mPW.isShowing()) {
					mPW.dismiss();
				}
			}
		});
		// 给lv添加滑动事件
		/*
		 * lv_datas.setOnScrollListener(new OnScrollListener() {
		 * 
		 * @Override public void onScrollStateChanged(AbsListView view, int
		 * scrollState) { // TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void onScroll(AbsListView view, int
		 * firstVisibleItem, int visibleItemCount, int totalItemCount) { //
		 * lv滑动到系统标签的时候 改变tv的内容 //lv滑动到系统标签 第一个数据
		 * 
		 * if (firstVisibleItem >= userAllInstalledAppInfos.size() + 1) {
		 * //系统app tag tv_tag.setText("系统软件(" +
		 * systemAllInstalledAppInfos.size() + ")"); } else { //用户app tag
		 * tv_tag.setText("用户软件(" + userAllInstalledAppInfos.size() + ")"); } }
		 * });
		 */

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:// 正在加载数据
				ll_loading.setVisibility(View.VISIBLE);
				lv_datas.setVisibility(View.GONE);
				// tv_tag.setVisibility(View.GONE);
				break;
			case FINISH:// 加载数据完成
				ll_loading.setVisibility(View.GONE);
				lv_datas.setVisibility(View.VISIBLE);
				// tv_tag.setVisibility(View.VISIBLE);

				// 显示数据 格式化数据
				tpv_rom_mess.setMessage("rom可以内存："
						+ Formatter.formatFileSize(getApplicationContext(),
								phoneAvailMem));
				// 进度比
				tpv_rom_mess.setProgress((phoneTotalMem - phoneAvailMem) * 1.0
						/ phoneTotalMem);
				tpv_sd_mess.setMessage("sd可用内存："
						+ Formatter.formatFileSize(getApplicationContext(),
								sdAvailMem));
				System.out.println("sdTotalMem:" + sdTotalMem + ":sdAvailMem:"
						+ sdAvailMem);
				System.out
						.println((sdTotalMem - sdAvailMem) * 1.0 / sdTotalMem);
				tpv_sd_mess.setProgress((sdTotalMem - sdAvailMem) * 1.0
						/ sdTotalMem);
				// tv_tag.setText("用户软件(" + userAllInstalledAppInfos.size() +
				// ")");

				// pb_progress.setProgress((int) (phoneAvailMem * 100 /
				// phoneTotalMem));
				// 更新lv数据
				mAdapter.notifyDataSetChanged();

			default:
				break;
			}
		};
	};
	private TextView tv_tag;
	private ProgressBar pb_progress;
	private TextProgressView tpv_rom_mess;
	private TextProgressView tpv_sd_mess;
	private PopupWindow mPW;
	private RemoveAppReceiver mReceiver;

	private void initData() {
		new Thread() {

			public void run() {
				synchronized (allInstalledAppInfos) {
					// 1. 发送加载进度的消息
					mHandler.obtainMessage(LOADING).sendToTarget();

					// 2. 获取数据
					// 所有app信息
					allInstalledAppInfos.clear();
					allInstalledAppInfos.addAll(AppInfoUtils
							.getAllInstalledAppInfos(getApplicationContext()));
					systemAllInstalledAppInfos.clear();
					userAllInstalledAppInfos.clear();
					// 分类
					for (AppInfoBean appInfoBean : allInstalledAppInfos) {
						if (appInfoBean.isSystem()) {
							// 系统app
							systemAllInstalledAppInfos.add(appInfoBean);
						} else {
							// 用户app
							userAllInstalledAppInfos.add(appInfoBean);
						}
					}
					allInstalledAppInfos.clear();
					allInstalledAppInfos.addAll(userAllInstalledAppInfos);
					allInstalledAppInfos.addAll(systemAllInstalledAppInfos);
					phoneAvailMem = AppInfoUtils.getPhoneAvailMem();
					phoneTotalMem = AppInfoUtils.getPhoneTotalMem();
					sdAvailMem = AppInfoUtils.getSDAvailMem();
					sdTotalMem = AppInfoUtils.getSDTotalMem();

					// 3. 发送数据加载完成
					mHandler.obtainMessage(FINISH).sendToTarget();
				}
			};
		}.start();

	}

	private class MyAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public synchronized int getCount() {
			// TODO Auto-generated method stub
			/*
			 * return userAllInstalledAppInfos.size() +
			 * systemAllInstalledAppInfos.size()
			 */
			synchronized (allInstalledAppInfos) {
				return allInstalledAppInfos.size();
			}

		}

		@Override
		public synchronized Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public synchronized long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public synchronized View getView(int position, View convertView,
				ViewGroup parent) {
			// view的缓存
			// 2种 1，view 2. TextView

			// 1. TextView 显示位置
			/*
			 * if (position == 0) { // 用户软件的标签 TextView tv_usertag = new
			 * TextView(getApplicationContext());
			 * tv_usertag.setBackgroundColor(Color.GRAY);// 背景灰色
			 * tv_usertag.setTextColor(Color.WHITE);// 字体白色
			 * tv_usertag.setText("用户软件(" + userAllInstalledAppInfos.size() +
			 * ")"); return tv_usertag; } else if (position ==
			 * userAllInstalledAppInfos.size() + 1) { // 系统软件的标签 TextView
			 * tv_usertag = new TextView(getApplicationContext());
			 * tv_usertag.setBackgroundColor(Color.GRAY);// 背景灰色
			 * tv_usertag.setTextColor(Color.WHITE);// 字体白色
			 * tv_usertag.setText("系统软件(" + systemAllInstalledAppInfos.size() +
			 * ")"); return tv_usertag; }
			 */

			// 用户软件或者系统软件
			ViewHolder viewHolder = null;
			if (convertView != null) {
				// 有缓存view 并且view不是TextView
				viewHolder = (ViewHolder) convertView.getTag();
			} else {
				// 没有缓存
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_appmanager_lv, null);

				viewHolder = new ViewHolder();

				viewHolder.tv_applocation = (TextView) convertView
						.findViewById(R.id.tv_appmanager_lv_applocation);
				viewHolder.tv_appname = (TextView) convertView
						.findViewById(R.id.tv_appmanager_lv_appname);
				viewHolder.tv_appsize = (TextView) convertView
						.findViewById(R.id.tv_appmanager_lv_appsize);

				viewHolder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_appmanager_lv_icon);

				// 设置标记
				convertView.setTag(viewHolder);
			}
			synchronized (allInstalledAppInfos) {

				// 取值赋值
				AppInfoBean bean = null;
				/*
				 * if (position <= userAllInstalledAppInfos.size()) { // 用户数据
				 * bean = userAllInstalledAppInfos.get(position); } else { //
				 * 系统数据 bean = systemAllInstalledAppInfos.get(position); }
				 */
				bean = allInstalledAppInfos.get(position);

				// 显示数据
				viewHolder.tv_applocation.setText(bean.isSD() ? "sd存储"
						: "rom存储");// 位置
				viewHolder.tv_appname.setText(bean.getAppName());// 名字
				viewHolder.tv_appsize.setText(Formatter.formatFileSize(
						getApplicationContext(), bean.getSize()));// 大小
				viewHolder.iv_icon.setImageDrawable(bean.getIcon());// 图标

				return convertView;
			}
		}

		@Override
		public synchronized View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			synchronized (allInstalledAppInfos) {
				AppInfoBean appInfoBean = allInstalledAppInfos.get(position);
				TextView tv_usertag = new TextView(getApplicationContext());
				tv_usertag.setBackgroundColor(Color.GRAY);// 背景灰色
				tv_usertag.setTextColor(Color.WHITE);// 字体白色
				if (!appInfoBean.isSystem()) {
					tv_usertag.setText("用户软件("
							+ userAllInstalledAppInfos.size() + ")");

				} else {
					tv_usertag.setText("系统软件("
							+ systemAllInstalledAppInfos.size() + ")");
				}
				return tv_usertag;
			}

		}

		@Override
		public synchronized long getHeaderId(int position) {
			synchronized (allInstalledAppInfos) {
				AppInfoBean appInfoBean = allInstalledAppInfos.get(position);
				if (!appInfoBean.isSystem()) {

					return 1;
				} else {
					return 2;
				}
			}

			// return 0;
		}

	}

	private class ViewHolder {
		TextView tv_appname;
		TextView tv_applocation;
		TextView tv_appsize;
		ImageView iv_icon;
	}

	private void initView() {
		setContentView(R.layout.acitivity_appmanager);

		// 显示app信息
		lv_datas = (StickyListHeadersListView) findViewById(R.id.lv_appmanager_viewdatas);

		mAdapter = new MyAdapter();

		lv_datas.setAdapter(mAdapter);

		ll_loading = (LinearLayout) findViewById(R.id.ll_progressbar_root);

		// tv_tag = (TextView) findViewById(R.id.tv_appmanager_lvtag);

		tpv_rom_mess = (TextProgressView) findViewById(R.id.tpv_appmanager_rom_mess);
		tpv_sd_mess = (TextProgressView) findViewById(R.id.tpv_appmanager_sd_mess);
	}
}
