package com.itheima.mobilesafe13.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.dao.BlackDao;
import com.itheima.mobilesafe13.db.BlackDB;
import com.itheima.mobilesafe13.domain.BlackBean;
import com.itheima.mobilesafe13.utils.MyConstaints;
import com.itheima.mobilesafe13.utils.ShowToast;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-16
 * @desc 黑名单管理界面
 * 
 * @version $Rev: 39 $
 * @author $Author: goudan $
 * @Date $Date: 2015-09-16 16:55:04 +0800 (Wed, 16 Sep 2015) $
 * @Id $Id: WebBlackActivity.java 39 2015-09-16 08:55:04Z goudan $
 * @Url $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/activity/BlackActivity.java $
 * 
 */
public class WebBlackActivity extends Activity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private ImageView iv_add;
	private LinearLayout ll_loading;
	private ImageView iv_noData;
	private ListView lv_showDatas;
	private boolean misFirstShow;// 是否显示第一个数据

	// 存放黑名单数据的容器
	private List<BlackBean> mBlackBeans = new ArrayList<BlackBean>();

	private static final int COUNTPERPAGE = 10;// 规定每页多少条数据
	private int totalPages = 0;// 总页数
	private int currentPage = 1;// 当前页

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();// 界面
		initData();// 数据 有可能执行多次
		initEvent();// 事件

		initPopupWindow();// 初始化弹出窗体

		initAddBlackDialog();// 添加黑名单的对话框
	}

	private void initAddBlackDialog() {
		// ShowToast.show("shoudongAdd", this);
		// 通过对话框添加黑名单数据
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		// 自定义View
		View mDialogView = View.inflate(getApplicationContext(),
				R.layout.dialog_addblack, null);

		et_blackphone = (EditText) mDialogView
				.findViewById(R.id.et_dialog_addblack_phone);
		// 拦截模式的勾选
		final CheckBox cb_sms = (CheckBox) mDialogView
				.findViewById(R.id.cb_dialog_addblack_smsmode);
		final CheckBox cb_phone = (CheckBox) mDialogView
				.findViewById(R.id.cb_dialog_addblack_phonemode);
		// 添加
		Button bt_add = (Button) mDialogView
				.findViewById(R.id.bt_dialog_addblack_add);
		// 取消
		Button bt_cancle = (Button) mDialogView
				.findViewById(R.id.bt_dialog_addblack_cancel);

		bt_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 添加黑名单
				// 1. 判断黑名单号码不能为空
				String phone = et_blackphone.getText().toString().trim();
				if (TextUtils.isEmpty(phone)) {
					// 号码不能为空
					ShowToast.show("号码不能为空", WebBlackActivity.this);
					return;
				}
				// 2. 拦截模式至少勾选一个
				if (!cb_phone.isChecked() && !cb_sms.isChecked()) {
					// 号码不能为空
					ShowToast.show("拦截模式至少勾选一个", WebBlackActivity.this);
					return;
				}
				// 3. 添加黑名单数据
				BlackBean bean = new BlackBean();
				bean.setPhone(phone);
				int mode = 0;
				if (cb_phone.isChecked()) {
					mode |= BlackDB.PHONE_MODE;
				}

				if (cb_sms.isChecked()) {
					mode |= BlackDB.SMS_MODE;
				}

				bean.setMode(mode);
				mBlackDao.update(bean);// 添加到数据库中
				misFirstShow = true;
				// 4. 显示最新添加的黑名单数据
				currentPage = 1;
				initData();

				// 5. 关闭对话框
				mAlertDialog.dismiss();

			}
		});
		bt_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAlertDialog.dismiss();
			}
		});
		ab.setView(mDialogView);
		// 创建对话框
		mAlertDialog = ab.create();

	}

	private void initPopupWindow() {

		mContentView = View.inflate(getApplicationContext(),
				R.layout.popupwindow_addblackdata, null);

		// 获取子组件添加事件

		TextView tv_shoudong = (TextView) mContentView
				.findViewById(R.id.tv_popupwindow_addblack_shoudong);
		TextView tv_tel = (TextView) mContentView
				.findViewById(R.id.tv_popupwindow_addblack_tellog);
		TextView tv_sms = (TextView) mContentView
				.findViewById(R.id.tv_popupwindow_addblack_smslog);
		TextView tv_friends = (TextView) mContentView
				.findViewById(R.id.tv_popupwindow_addblack_friendslog);

		OnClickListener mListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 监听四个操作
				switch (v.getId()) {
				case R.id.tv_popupwindow_addblack_shoudong:// 手动
					shoudongAdd();
					break;
				case R.id.tv_popupwindow_addblack_tellog:// 电话
					tellogAdd();
					break;
				case R.id.tv_popupwindow_addblack_smslog:// 短信
					msmlogAdd();
					break;
				case R.id.tv_popupwindow_addblack_friendslog:// 好友
					friendsAdd();
					break;
				default:
					break;
				}
				// 关闭对话框
				mPW.dismiss();

			}
		};
		tv_shoudong.setOnClickListener(mListener);
		tv_tel.setOnClickListener(mListener);
		tv_sms.setOnClickListener(mListener);
		tv_friends.setOnClickListener(mListener);

		// 初始化弹出窗体

		mPW = new PopupWindow(mContentView, 130, -2);

		// 设置参数
		mPW.setFocusable(true);
		// 设置背景 1. 外部点击生效 2. 可以播放动画
		mPW.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mPW.setOutsideTouchable(true);

		// 初始化动画
		// 播放动画
		mPopupAnimation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
		mPopupAnimation.setDuration(400);

	}

	protected void friendsAdd() {
		Intent friends = new Intent(this, FriendsActivity.class);
		// 获取请求结果
		startActivityForResult(friends, 0);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 获取选择的好友
		if (data != null) {
			String safeNum = data.getStringExtra(MyConstaints.SAFENUMBER);
			// 显示编辑中
			// 显示对话框,自动显示号码
			showDialog(safeNum);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void msmlogAdd() {
		Intent friends = new Intent(this, SmsAcitivity.class);
		// 获取请求结果
		startActivityForResult(friends, 0);

	}

	protected void tellogAdd() {
		Intent friends = new Intent(this, TelAcitivity.class);
		// 获取请求结果
		startActivityForResult(friends, 0);

	}

	private void showDialog(String phone) {
		// 显示黑名单号码
		et_blackphone.setText(phone);
		// 显示对话框
		mAlertDialog.show();
	}

	protected void shoudongAdd() {
		showDialog("");
	}

	private void initEvent() {
		// 添加黑名单数据
		iv_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 添加新的黑名单数据
				// 判断窗体是否显示
				if (mPW != null && mPW.isShowing()) {
					// 关闭
					mPW.dismiss();
				} else {
					// 显示
					mPW.showAsDropDown(v);
					// 显示动画
					mContentView.startAnimation(mPopupAnimation);
				}
			}
		});

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING: // 加载数据
				// 加载数据进度条显示
				ll_loading.setVisibility(View.VISIBLE);
				// 隐藏 listview 和 nodata
				lv_showDatas.setVisibility(View.GONE);
				iv_noData.setVisibility(View.GONE);
				break;
			case FINISH:// 加载数据完成
				// 隐藏加载数据进度条
				ll_loading.setVisibility(View.GONE);

				if (mBlackBeans.isEmpty()) {
					// 没有数据 显示nodata 隐藏 listview
					iv_noData.setVisibility(View.VISIBLE);
					lv_showDatas.setVisibility(View.GONE);

				} else {
					// 有数据 显示listview 隐藏nodata
					lv_showDatas.setVisibility(View.VISIBLE);
					iv_noData.setVisibility(View.GONE);

					// 刷新界面
					mAdapter.notifyDataSetChanged();

					if (misFirstShow) {
						lv_showDatas.smoothScrollToPosition(0);// 滚动到第0条
						misFirstShow = false;
					}
					
					//添加 页面 信息
					tv_pagemess.setText(currentPage + "/" + totalPages);
					et_jumppage.setText(currentPage + "");
					et_jumppage.setSelection(et_jumppage.getText().toString().trim().length());
					
				}

				break;
			default:
				break;
			}

		};
	};
	private MyAdapter mAdapter;
	private BlackDao mBlackDao;
	private View mContentView;
	private PopupWindow mPW;
	private Animation mPopupAnimation;
	private AlertDialog mAlertDialog;
	private EditText et_blackphone;
	private EditText et_jumppage;
	private TextView tv_pagemess;

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mBlackBeans.size();
		}

		@Override
		public BlackBean getItem(int position) {
			// TODO Auto-generated method stub
			return mBlackBeans.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 界面缓存

			ViewHolder viewHolder = null;
			// 判断是否有缓存
			if (convertView == null) {
				// 没有缓存
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_black_lv, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_item_black_lv_blackmode);
				viewHolder.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_item_black_lv_blackphone);

				viewHolder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_item_black_lv_delete);
				// 设置标签
				convertView.setTag(viewHolder);
			} else {
				// 有缓存
				viewHolder = (ViewHolder) convertView.getTag();
			}

			// 取值
			final BlackBean bean = getItem(position);

			// 显示值
			viewHolder.tv_phone.setText(bean.getPhone());// 黑名单号码
			switch (bean.getMode()) {
			case BlackDB.SMS_MODE:// 短信拦截
				viewHolder.tv_mode.setText("短信拦截");
				break;
			case BlackDB.PHONE_MODE:// 短信拦截
				viewHolder.tv_mode.setText("电话拦截");
				break;
			case BlackDB.ALL_MODE:// 全部拦截
				viewHolder.tv_mode.setText("全部拦截");
				break;

			default:
				break;
			}
			// 删除
			viewHolder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder ab = new AlertDialog.Builder(WebBlackActivity.this);
					ab.setTitle("注意");
					ab.setMessage("是否真的删除数据?");
					ab.setPositiveButton("真删", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mBlackDao.delete(bean.getPhone());
							initData();
							//
							/*// 删除数据
							// 本地删除
							mBlackBeans.remove(bean);
							// 数据库删除
							mBlackDao.delete(bean.getPhone());
							// 更新新界面
							mAdapter.notifyDataSetChanged();*/
						}
					});
					ab.setNegativeButton("不删", null);
					ab.show();
					
				}
			});
			return convertView;
		}

	}

	private class ViewHolder {
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}

	private void initData() {
		// 数据过大 子线程访问
		new Thread() {
			public void run() {
				// 获取数据

				// 1. 发送正在加载数据的消息
				mHandler.obtainMessage(LOADING).sendToTarget();

				// 2. 获取当前页数据
				// 获取总页数
				totalPages = (int) Math.ceil(mBlackDao.getTotalRows() * 1.0
						/ COUNTPERPAGE);
				// 获取当前页数据
				mBlackBeans = mBlackDao.getPageData(currentPage, COUNTPERPAGE);

				// 模式耗时
				SystemClock.sleep(200);

				// 3. 加载完成
				mHandler.obtainMessage(FINISH).sendToTarget();
			};
		}.start();
	}

	/**
	 * 黑名单界面
	 */
	private void initView() {
		// view

		setContentView(R.layout.activity_black_web);

		// 获取子view
		// 添加黑名单数据的按钮
		iv_add = (ImageView) findViewById(R.id.iv_black_add);

		// 加载数据根布局

		ll_loading = (LinearLayout) findViewById(R.id.ll_progressbar_root);

		// 显示数据

		lv_showDatas = (ListView) findViewById(R.id.lv_black_showdata);

		mAdapter = new MyAdapter();
		lv_showDatas.setAdapter(mAdapter);// 绑定适配器

		mBlackDao = new BlackDao(this);

		// 没有数据

		iv_noData = (ImageView) findViewById(R.id.iv_black_nodata);

		// 跳转页面

		et_jumppage = (EditText) findViewById(R.id.et_web_black_tiaopage);
		tv_pagemess = (TextView) findViewById(R.id.tv_web_black_pagemess);

	}

	// 首页
	public void shou(View v) {
		currentPage = 1;
		initData();
	}

	// 未页
	public void wei(View v) {
		currentPage = totalPages;
		initData();
	}

	// 上页
	public void shang(View v) {
		if (currentPage == 1) {
			ShowToast.show("已经是第一页", this);
			return;
		}
		currentPage--;
		initData();
		
	}

	// 下页
	public void xia(View v) {
		if (currentPage == totalPages) {
			ShowToast.show("已经是未页", this);
			return;
		}
		currentPage++;
		initData();
	}

	// 跳转
	public void tiao(View v) {
		String pageStr = et_jumppage.getText().toString().trim();
		int page = Integer.parseInt(pageStr);
		if (page < 1 || page > totalPages) {
			ShowToast.show("请按套路出牌", this);
			return;
		}
		
		currentPage = page;
		initData();
	}
}
