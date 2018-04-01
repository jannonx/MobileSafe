package com.itheima.mobilesafe13.view;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.dao.LockedDao;
import com.itheima.mobilesafe13.domain.AppInfoBean;

/**
 * @author Administrator
 * @desc 程序锁的基本fragment
 * @date 2015-9-25
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-25 15:55:36 +0800 (Fri, 25 Sep 2015) $
 * @Id $Id: BaseLockFragment.java 108 2015-09-25 07:55:36Z goudan $
 * @Rev $Rev: 108 $
 * @URL $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/view/BaseLockFragment.java $
 * 
 */
public class BaseLockFragment extends Fragment {

	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private StickyListHeadersListView lv_showlocks;
	private MyAdapter mAdapter;
	private LockedDao mLockedDao;
	private List<AppInfoBean> mDatas;

	public BaseLockFragment() {
		mDatas = new ArrayList<AppInfoBean>();

	}

	public void setLockedDao(LockedDao lockedDao) {
		this.mLockedDao = lockedDao;
	}

	private Handler mHandler = new Handler() {
		private ProgressDialog pd;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:// 加载数据
				pd = new ProgressDialog(getActivity());
				pd.setTitle("注意");
				pd.setMessage("正在玩命加载数据。。。。");
				pd.show();
			case FINISH:
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
					pd = null;
				}
				// 显示数据
				mAdapter.notifyDataSetChanged();

				break;

			default:
				break;
			}
		};
	};
	private List<AppInfoBean> allInstalledAppInfos;

	public void setAllInstalledAppInfos(List<AppInfoBean> allInstalledAppInfos) {
		this.allInstalledAppInfos = allInstalledAppInfos;
	}

	private List<String> allLockedPackageNames;

	public void setAllLockedPackageNames(List<String> allLockedPackageNames) {
		this.allLockedPackageNames = allLockedPackageNames;
	}

	public void initData() {
		// 耗时操作
		new Thread() {
			public void run() {
				// 1. 发送加载数据的消息
				mHandler.obtainMessage(LOADING).sendToTarget();

				// 2.获取数据
				mDatas.clear();

				for (AppInfoBean appInfoBean : allInstalledAppInfos) {
					if ((BaseLockFragment.this instanceof LockedFragment)
							&& allLockedPackageNames.contains(appInfoBean
									.getPackName())) {// mLockedDao.isLocked(appInfoBean.getPackName()))
														// {
						// 加锁
						mDatas.add(appInfoBean);
					} else if ((BaseLockFragment.this instanceof UnLockedFragment)
							&& !allLockedPackageNames.contains(appInfoBean
									.getPackName())) {//
						// 未加锁
						mDatas.add(appInfoBean);
					} else {
						// 不做处理
					}
				}

				// 3. 数据加载完成的消息
				mHandler.obtainMessage(FINISH).sendToTarget();

			};
		}.start();
	}

	private class MyAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDatas.size();
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
			if (convertView == null) {
				convertView = View.inflate(getActivity(),
						R.layout.item_applock_fragment, null);

				viewHolder = new ViewHolder();

				viewHolder.iv_appicon = (ImageView) convertView
						.findViewById(R.id.iv_item_applock_fg_icon);
				viewHolder.iv_lockOrunlock = (ImageView) convertView
						.findViewById(R.id.iv_item_applock_fg_lockorunlock);
				viewHolder.tv_appname = (TextView) convertView
						.findViewById(R.id.tv_item_applock_fg_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			// 赋值
			if (position < 0 || position >= mDatas.size()) {
				return convertView;
			}
			final AppInfoBean appInfoBean = mDatas.get(position);
			viewHolder.iv_appicon.setImageDrawable(appInfoBean.getIcon());// 图标

			viewHolder.tv_appname.setText(appInfoBean.getAppName());// 名字

			if (BaseLockFragment.this instanceof LockedFragment) {
				viewHolder.iv_lockOrunlock.setImageResource(R.drawable.unlock);
			} else {
				viewHolder.iv_lockOrunlock.setImageResource(R.drawable.lock);
			}
			final View rootView = convertView;
			// 添加加锁或解锁的事件
			viewHolder.iv_lockOrunlock
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (BaseLockFragment.this instanceof LockedFragment) {
								// 解锁的业务
								mLockedDao.removeLockedPackName(appInfoBean
										.getPackName());
								// 解锁动画
								rootView.startAnimation(AnimationUtils
										.loadAnimation(getActivity(),
												R.anim.unlocked_translate));
							} else {
								// 加锁的业务
								mLockedDao.addLockedPackName(appInfoBean
										.getPackName());
								// 加锁动画
								rootView.startAnimation(AnimationUtils
										.loadAnimation(getActivity(),
												R.anim.locked_translate));

							}
							new Thread() {
								public void run() {
									SystemClock.sleep(500);
									getActivity().runOnUiThread(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub

											// 界面处理
											mDatas.remove(appInfoBean);
											mAdapter.notifyDataSetChanged();// 更新界面

										}
									});
								};
							}.start();

						}
					});

			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView tv_tag = new TextView(getActivity());
			tv_tag.setBackgroundColor(Color.GRAY);
			tv_tag.setTextSize(18);
			tv_tag.setTextColor(Color.WHITE);

			// 条件
			AppInfoBean bean = mDatas.get(position);
			if (bean.isSystem()) {
				tv_tag.setText("系统软件");
			} else {
				tv_tag.setText("用户软件");
			}
			return tv_tag;
		}

		@Override
		public long getHeaderId(int position) {
			AppInfoBean bean = mDatas.get(position);
			if (bean.isSystem()) {
				return 1;
			} else {
				return 2;
			}
		}

	}

	private class ViewHolder {
		ImageView iv_appicon;
		TextView tv_appname;
		ImageView iv_lockOrunlock;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onActivityCreated(savedInstanceState);
		System.out.println("onActivityCreated" + getClass().getName());
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		System.out.println("onAttach" + getClass().getName());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("onCreate" + getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		lv_showlocks = (StickyListHeadersListView) View.inflate(getActivity(),
				R.layout.fragment_lock_view, null);

		if (mAdapter == null)
			mAdapter = new MyAdapter();
		// 每次都要做适配器
		lv_showlocks.setAdapter(mAdapter);

		return lv_showlocks;
		/*
		 * TextView tv = new TextView(getActivity()); //判断子类类型 if (this
		 * instanceof LockedFragment) { tv.setText("已加锁"); } else {
		 * tv.setText("未加锁"); } tv.setTextSize(20); return tv;
		 */
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initData();
		System.out.println("onStart" + getClass().getName());
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("onStop" + getClass().getName());
	}

}
