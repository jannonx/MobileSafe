package com.itheima.mobilesafe13.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.dao.AntiVirusDao;
import com.itheima.mobilesafe13.domain.AppInfoBean;
import com.itheima.mobilesafe13.utils.AppInfoUtils;
import com.itheima.mobilesafe13.utils.Md5Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * @author Administrator
 * @desc 病毒查杀
 * @date 2015-9-24
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-24 16:03:57 +0800 (Thu, 24 Sep 2015) $
 * @Id $Id: AntiVirusActivity.java 100 2015-09-24 08:03:57Z goudan $
 * @Rev $Rev: 100 $
 * @URL $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/activity/AntiVirusActivity.java $
 * 
 */
public class AntiVirusActivity extends Activity {
	protected static final int SCANING = 1;
	protected static final int FINISH = 2;
	protected static final int STARTSCAN = 3;
	private boolean isInitAnimatorAndEvent = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();

		// initEvent();

		// startScan();

		checkVersion();// 检测版本
	}

	public String getPointNumber(int number) {
		String str = "";
		for (int i = 0; i < number; i++) {
			str += ".";
		}
		return str;
	}

	private boolean isShowPoint = true;

	private void checkVersion() {
		// 显示联网的对话框
		final AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("注意");
		ab.setMessage("正在尝试联网......");
		

		final AlertDialog alertDialog = ab.create();
		alertDialog.show();

		// 点的显示问题
		new Thread() {
			public void run() {
				isShowPoint = true;
				class Data {
					int number = 1;
				}
				final Data data = new Data();
				data.number = 1;
				while (isShowPoint) {
					runOnUiThread(new Runnable() {
						public void run() {
							alertDialog.setMessage("正在尝试联网"
									+ getPointNumber(data.number++ % 7));
						}
					});

					SystemClock.sleep(500);
				}
			};
		}.start();

		HttpUtils utils = new HttpUtils();
		utils.configTimeout(5000);// 设置网络超时时间
		utils.send(HttpMethod.GET,
				getResources().getString(R.string.virusversionurl),
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						alertDialog.dismiss();
						isShowPoint = false;
						Toast.makeText(getApplicationContext(), "没有网络", 1)
								.show();
						// 主线中执行
						startScan();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// 主线中执行
						alertDialog.dismiss();
						isShowPoint = false;
						// 获取版本号判断是否有新的病毒
						final int serverVersion = Integer.parseInt(arg0.result);

						// 判断版本
						// 获取自己的版本
						int version = AntiVirusDao.getCurrentVirusVersion();

						if (version != serverVersion) {
							// 有新病毒
							AlertDialog.Builder ab = new AlertDialog.Builder(
									AntiVirusActivity.this);
							ab.setTitle("有新病毒");
							ab.setMessage("是否下载更新");
							ab.setPositiveButton("下载更新",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// 下载新病毒
											downloadNewVirus(serverVersion);
										}

									});
							ab.setNegativeButton("下次再说",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

											startScan();
										}
									});
							ab.show();

						} else {
							// 没有新病毒
							Toast.makeText(getApplicationContext(), "病毒库当前最新",
									1).show();
							startScan();
						}

					}
				});

	}

	private void downloadNewVirus(final int serverVersion) {
		//
		HttpUtils utils = new HttpUtils();
		utils.configTimeout(5000);// 设置网络超时时间
		utils.send(HttpMethod.GET, getResources()
				.getString(R.string.virusdatas), new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				// 更新病毒库失败
				Toast.makeText(getApplicationContext(), "更新病毒库失败", 1).show();
				startScan();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				String jsonData = arg0.result;
				// 解析
				try {
					JSONObject jsonObject = new JSONObject(jsonData);
					String md5 = jsonObject.getString("md5");
					String desc = jsonObject.getString("desc");
					AntiVirusDao.updateVirus(md5, desc);

					AntiVirusDao.updateVirusVersion(serverVersion);
					// 更新成功
					Toast.makeText(getApplicationContext(), "病毒库更新成功", 1)
							.show();
					startScan();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

	}

	@Override
	protected void onDestroy() {
		// 关闭扫描病毒的线程
		interruptScaning = true;
		super.onDestroy();
	}

	private void initEvent() {
		mASOpen.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
				// 禁用
				bt_rescan.setEnabled(false);

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				bt_rescan.setEnabled(true);
			}

			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub

			}
		});

		// 动画结束开始扫描
		mASClose.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				// 动画结束 开始扫描
				startScan();

			}

			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 重新扫描
		bt_rescan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// startScan();
				closeShowScaningProgress();
			}
		});

	}

	private Handler mHandler = new Handler() {
		private boolean mIsVirus;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case STARTSCAN:// 开始扫描
				ll_scanappinfo.removeAllViews();// 移动掉所有的view
				// 隐藏扫描结果
				ll_scan_result.setVisibility(View.GONE);
				// 显示扫描进度
				ll_scanProgress.setVisibility(View.VISIBLE);
				// 隐藏动画
				ll_animator_result.setVisibility(View.GONE);
				break;
			case SCANING:// 正在扫描
				// 获取结果
				ScanAppInfo info = (ScanAppInfo) msg.obj;
				// 进度的显示

				cp_scanprogress.setProgress((int) Math
						.round(info.currentProgress * 100.0 / info.max));

				// 扫描信息

				// View
				View view = View.inflate(getApplicationContext(),
						R.layout.item_antivirus_ll, null);
				TextView tv_name = (TextView) view
						.findViewById(R.id.tv_item_antivirus_lv_name);
				ImageView iv_icon = (ImageView) view
						.findViewById(R.id.iv_item_antivirus_lv_icon);
				ImageView iv_isVirus = (ImageView) view
						.findViewById(R.id.iv_item_antivirus_isvirus);

				tv_name.setText(info.appName);
				iv_icon.setImageDrawable(info.icon);
				if (info.isVirus) {
					mIsVirus = info.isVirus;
					iv_isVirus.setImageResource(R.drawable.check_status_red);
				} else {
					iv_isVirus.setImageResource(R.drawable.check_status_green);
				}

				// 添加到LinearLayout中
				ll_scanappinfo.addView(view, 0);

				// 更新文本
				tv_scaninfo.setText("正在扫描:" + info.appName);
				break;
			case FINISH:// 扫描完成
				// 隐藏扫描进度

				// 拍照
				ll_scanProgress.setDrawingCacheEnabled(true);
				ll_scanProgress
						.setDrawingCacheQuality(LinearLayout.DRAWING_CACHE_QUALITY_HIGH);
				// 获取进度完成的照片
				Bitmap progressImage = ll_scanProgress.getDrawingCache();// 前提
																			// setDrawingCacheEnabled(true);
				// 左边
				Bitmap leftBitmap = getLeftImage(progressImage);
				// 右半
				Bitmap rightBitmap = getRightImage(progressImage);

				// 设置图片
				iv_left.setImageBitmap(leftBitmap);
				iv_right.setImageBitmap(rightBitmap);
				// TODO 删掉 //先取出宽度 只初始化一次
				if (!isInitAnimatorAndEvent) {
					initCloseShowScaningProgress();
					initOpenShowResult();
					initEvent();
					isInitAnimatorAndEvent = true;
				}
				ll_scanProgress.setVisibility(View.GONE);// 进度

				ll_scan_result.setVisibility(View.VISIBLE);// 结果

				// 显示动画的背景
				ll_animator_result.setVisibility(View.VISIBLE);

				if (mIsVirus) {
					tv_isvirusshow.setText("手机有病毒，看下面记录");
				} else {
					tv_isvirusshow.setText("手机无病毒，手机很安全");
				}

				openShowResult();// 打开拍照界面显示结果的动画

				break;
			default:
				break;
			}
		};
	};
	private LinearLayout ll_scan_result;
	private TextView tv_isvirusshow;
	private Button bt_rescan;
	private LinearLayout ll_scanProgress;
	private CircleProgress cp_scanprogress;
	private TextView tv_scaninfo;
	private LinearLayout ll_scanappinfo;
	private LinearLayout ll_animator_result;
	private ImageView iv_left;
	private ImageView iv_right;
	private AnimatorSet mASClose;
	private AnimatorSet mASOpen;
	private boolean interruptScaning = false;// 是否中断扫描

	private void startScan() {
		// 耗时
		new Thread() {
			public void run() {
				// 1.发送开始扫描的消息
				mHandler.obtainMessage(STARTSCAN).sendToTarget();

				// 2.开始扫描每个安装的apk
				// 获取所有安装的apk
				List<AppInfoBean> allInstalledAppInfos = AppInfoUtils
						.getAllInstalledAppInfos(getApplicationContext());
				int progress = 0;
				for (AppInfoBean appInfoBean : allInstalledAppInfos) {
					if (interruptScaning) {
						return;
					}
					progress++;
					// apk安装目录
					String sourceDir = appInfoBean.getSourceDir();
					// 计算MD5
					String md5 = Md5Utils.getFileMd5(sourceDir);
					// 判断
					boolean isVirus = AntiVirusDao.isVirus(md5);

					// 封装扫描结果
					// 图标 名字 是否病毒
					ScanAppInfo info = new ScanAppInfo();
					info.icon = appInfoBean.getIcon();// 图标
					info.appName = appInfoBean.getAppName();// 名字
					info.isVirus = isVirus;
					info.max = allInstalledAppInfos.size();
					info.currentProgress = progress;

					// 发送扫描结果
					Message msg = mHandler.obtainMessage(SCANING);
					msg.obj = info;
					mHandler.sendMessage(msg);// 发送扫描结果

					SystemClock.sleep(200);
				}

				// 扫描结束
				mHandler.obtainMessage(FINISH).sendToTarget();
			};
		}.start();

	}

	private void initCloseShowScaningProgress() {

		mASClose = new AnimatorSet();
		ll_animator_result.measure(0, 0);// 布局参数随意测量
		// 添加属性动画
		int width = ll_animator_result.getMeasuredWidth() / 2;
		System.out.println("width:" + width);
		// 1. 添加左图移动离开的动画 0 到 -width
		// iv_left.setTranslationX(translationX)
		ObjectAnimator leftTransationOut = ObjectAnimator.ofFloat(iv_left,
				"translationX", -width, 0);

		// 2. 添加左图离开alpha动画
		// iv_left.setAlpha(alpha);
		ObjectAnimator leftAlphaOut = ObjectAnimator.ofFloat(iv_left, "alpha",
				0f, 1.0f);

		// 3. 添加右图移动离开的动画 0 到 -width
		ObjectAnimator rightTransationOut = ObjectAnimator.ofFloat(iv_right,
				"translationX", width, 0);

		// 4. 添加右图离开alpha动画
		ObjectAnimator rightAlphaOut = ObjectAnimator.ofFloat(iv_right,
				"alpha", 0f, 1.0f);

		// 5. 显示结果的view alpha渐变显示
		ObjectAnimator resultAlphaShow = ObjectAnimator.ofFloat(ll_scan_result,
				"alpha", 1.0f, 0f);

		mASClose.playTogether(leftTransationOut, leftAlphaOut,
				rightTransationOut, rightAlphaOut, resultAlphaShow);
		mASClose.setDuration(1000);
	}

	/**
	 * 关闭拍照背景，从新开始扫描
	 */
	protected void closeShowScaningProgress() {
		// 照片打开的动画

		// 属性动画
		// AnimatorSet as = new AnimatorSet();

		// 关闭
		mASClose.start();// 开始动画

	}

	private void initOpenShowResult() {
		// 属性动画
		// AnimatorSet as = new AnimatorSet();
		ll_animator_result.measure(0, 0);// 布局参数随意测量
		// 添加属性动画
		// int width = iv_left.getMeasuredWidth();
		int width = ll_animator_result.getMeasuredWidth() / 2;
		System.out.println("width:" + width);
		mASOpen = new AnimatorSet();
		// 添加属性动画

		// 1. 添加左图移动离开的动画 0 到 -width
		// iv_left.setTranslationX(translationX)
		ObjectAnimator leftTransationOut = ObjectAnimator.ofFloat(iv_left,
				"translationX", 0, -width);

		// 2. 添加左图离开alpha动画
		// iv_left.setAlpha(alpha);
		ObjectAnimator leftAlphaOut = ObjectAnimator.ofFloat(iv_left, "alpha",
				1.0f, 0f);

		// 3. 添加右图移动离开的动画 0 到 -width
		ObjectAnimator rightTransationOut = ObjectAnimator.ofFloat(iv_right,
				"translationX", 0, width);

		// 4. 添加右图离开alpha动画
		ObjectAnimator rightAlphaOut = ObjectAnimator.ofFloat(iv_right,
				"alpha", 1.0f, 0f);

		// 5. 显示结果的view alpha渐变显示
		ObjectAnimator resultAlphaShow = ObjectAnimator.ofFloat(ll_scan_result,
				"alpha", 0f, 1.0f);

		mASOpen.playTogether(leftTransationOut, leftAlphaOut,
				rightTransationOut, rightAlphaOut, resultAlphaShow);
		mASOpen.setDuration(1000);
	}

	protected void openShowResult() {
		// 照片打开的动画

		mASOpen.start();// 开始动画

	}

	protected Bitmap getLeftImage(Bitmap progressImage) {
		// TODO Auto-generated method stub
		int width = progressImage.getWidth() / 2;
		int height = progressImage.getHeight();
		// 空的画纸
		Bitmap leftImage = Bitmap.createBitmap(width, height,
				progressImage.getConfig());

		// 画板
		Canvas canvas = new Canvas(leftImage);// 把画纸放到画板中

		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		canvas.drawBitmap(progressImage, matrix, paint);
		return leftImage;
	}

	protected Bitmap getRightImage(Bitmap progressImage) {
		// TODO Auto-generated method stub
		int width = progressImage.getWidth() / 2;
		int height = progressImage.getHeight();
		// 空的画纸
		Bitmap rightImage = Bitmap.createBitmap(width, height,
				progressImage.getConfig());

		// 画板
		Canvas canvas = new Canvas(rightImage);// 把画纸放到画板中

		Matrix matrix = new Matrix();
		matrix.setTranslate(-width, 0);// 画原图的右半部分
		Paint paint = new Paint();
		canvas.drawBitmap(progressImage, matrix, paint);
		return rightImage;
	}

	private class ScanAppInfo {
		Drawable icon;
		String appName;
		boolean isVirus;
		int max;
		int currentProgress;
	}

	private void initView() {
		setContentView(R.layout.activity_antivirus);

		// 扫描结果的根布局

		ll_scan_result = (LinearLayout) findViewById(R.id.ll_antivirus_showresult);

		tv_isvirusshow = (TextView) findViewById(R.id.tv_antivirus_scanresult);
		bt_rescan = (Button) findViewById(R.id.bt_antivirus_rescan);

		// 扫描进度

		ll_scanProgress = (LinearLayout) findViewById(R.id.ll_antivirus_scanprogerss);

		cp_scanprogress = (CircleProgress) findViewById(R.id.cp_antivirus_progress);
		tv_scaninfo = (TextView) findViewById(R.id.tv_antivirus_scaninfos);

		// 扫描app信息

		ll_scanappinfo = (LinearLayout) findViewById(R.id.ll_antivirus_scanappinfo);

		ll_animator_result = (LinearLayout) findViewById(R.id.ll_antivirus_animator_result);

		iv_left = (ImageView) findViewById(R.id.iv_antivirus_leftimage);
		iv_right = (ImageView) findViewById(R.id.iv_antivirus_rightimage);

	}
}
