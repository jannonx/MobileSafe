package com.itheima.mobilesafe13.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Menu;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.utils.MyConstaints;
import com.itheima.mobilesafe13.utils.SPUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-12
 * @desc splash界面
 * 
 * @version $Rev: 94 $
 * @author $Author: goudan $
 * @Date $Date: 2015-09-24 10:56:01 +0800 (Thu, 24 Sep 2015) $
 * @Id $Id: SplashActivity.java 94 2015-09-24 02:56:01Z goudan $
 * @Url $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/activity/SplashActivity.java $
 * 
 */
public class SplashActivity extends Activity {

	private static final int LOADMAIN = 1;

	private static final int NEWVERSION = 3;
	
	private RelativeLayout rl_root;
	private TextView tv_versiocode;
	private TextView tv_versioname;
	private PackageManager mPm;
	private AnimationSet mAS;
	private int mVersionCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题,界面显示之前
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//模拟数据 TODO 功能测试完删除下面代码
		
		// 界面
		initView();

		// 数据
		initData();

		// 初始化动画
		initAnimation();

		// 事件
		initEvent();
		
		

	}
	
	/**
	 * @param dbFileName
	 *      数据库文件的文件名（assert目录 ）
	 *      拷贝到/data/data/com.itheima.mobilesafe13/file
	 * @throws IOException 
	 */
	private void copyDB(String dbFileName) throws IOException{
		//判断文件是否已经拷贝过
		
		//流的拷贝
		//输出流
		File filesDir = getFilesDir();///data/data/com.itheima.mobilesafe13/file
		File file = new File(filesDir,dbFileName);
		if (file.exists()) { //文件存在
			return;
		}
		
		
		FileOutputStream fos = new FileOutputStream(file);
		// 输入流
		AssetManager assetManager = getAssets();
		InputStream inputStream = assetManager.open(dbFileName);
		
		
		byte[] buffer = new byte[1024 * 5];
		int len = inputStream.read(buffer);
		while (len != -1) {
			fos.write(buffer, 0, len);
			fos.flush();//刷新缓冲区的内容到目的
			//继续读取
			len = inputStream.read(buffer);
		}
		
		//关闭流
		inputStream.close();
		fos.close();
	}

	private void initAnimation() {
		// 补间动画

		// 旋转动画
		RotateAnimation ra = new RotateAnimation(0, 360,
				// 设置锚点
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);

		// 设置动画参数
		ra.setDuration(2000);
		// 补间 影子动画
		// 停留在动画结束的位置,保留影子的位置
		ra.setFillAfter(true);

		// 比例动画
		ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0f, 1.0f,
				// 设置锚点
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(2000);
		// 停留在动画结束的位置
		sa.setFillAfter(true);

		// Alpha动画
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(2000);
		// 停留在动画结束的位置
		aa.setFillAfter(true);

		// 动画集
		// false 每种动画用自己的动画插入器（数学函数）

		mAS = new AnimationSet(false);

		// 添加旋转动画
		mAS.addAnimation(ra);
		// 添加比例动画
		mAS.addAnimation(sa);
		// 添加渐变动画
		mAS.addAnimation(aa);
		// 播放动画
		rl_root.startAnimation(mAS);
	}

	private void initEvent() {
		// 监听动画开始和结束的事件
		mAS.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				//文件的拷贝
				copyFileThread("address.db");
				copyFileThread("commonnum.db");
				copyFileThread("antivirus.db");
				
				// 动画开始
				// 初始化数据 初始化网络(子线程)
				// 版本更新
				if (SPUtils.getBoolean(getApplicationContext(),
						MyConstaints.AUTOUPDATE, false)) {
					// 版本更新
					checkVersion();
				} else {
					// 不更新
					// 进入主界面 不干活 由动画结束来完成界面跳转
				}
			}

			private void copyFileThread(final String fileName) {
				new Thread(){
					public void run() {
						try {
							copyDB(fileName);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画结束
				// 是否进行版本更新，不进行版本更新，动画播完直接进入主界面
				if (SPUtils.getBoolean(getApplicationContext(),
						MyConstaints.AUTOUPDATE, false)) {
					// 版本更新 不干活
					//System.out.println("版本更新");
				} else {
					// 不更新
					// 进入主界面
					startHome();
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// 动画重复

			}
		});

	}
	
	/**
	 * 进入主界面
	 */
	private void startHome(){
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		startActivity(intent);
		
		//关闭自己
		finish();
	}

	// 版本更新检测
	protected void checkVersion() {
		//子线程中执行
		new Thread(){
			public void run() {
				readUrlData();
			};
		}.start();
		// 检测版本
		
		// 安装
		// 跳转
		
	}

	protected void readUrlData() {
		// //访问网络 url 数据格式
		//url http://188.188.4.100:8080/version13.json
		//数据格式 json  {"versionname":"冬瓜版","versioncode":"3","desc":"增加了账号加密处理，查找异性朋友等","downloadurl":"http://188.188.4.100:8080/xx.apk"}
		
		Message msg = mHandler.obtainMessage();
		int errorCode = 0;
		long startTime = System.currentTimeMillis();
		try {
			URL uri = new URL(getResources().getString(R.string.versionurl));
			//连接
			HttpURLConnection con = (HttpURLConnection) uri.openConnection();
			
			//设置属性
			con.setConnectTimeout(5000);//设置超时时间为5秒
			con.setRequestMethod("GET");//get请求
			int code = con.getResponseCode();//200 success 404 notfount 500 内部错误 
			if (code == 200){
				//请求成功
				//把流转成json数据（string）
				String json = stream2json(con.getInputStream());
				//解析json
				
				mVersionInfo = parseJson(json);
				
				//检测版本
				if (mVersionCode == mVersionInfo.versionCode) {
					//版本一致
					//进入主界面
					msg.what = LOADMAIN;
					
				} else {
					//有新版本
					// 提示用户是否更新
					// 否 进入主界面
					// 是 下载--提醒用户是否安装
						//
					//显示是否更新的对话框 
					msg.what = NEWVERSION;
				}
				
			} else {
				// 非200 如：404 500
				msg.what = code;
				
			}
		} catch (MalformedURLException e) {
			// url错误
			msg.what = 10087;//URLException
			e.printStackTrace();
		} catch (NotFoundException e) {
			// 找不到
			msg.what = 10088;//NotFoundException
			e.printStackTrace();
		} catch (IOException e) {
			// io异常
			msg.what = 10089;//IOException
			e.printStackTrace();
		} catch (JSONException e) {
			msg.what = 10090;//JSONException
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//延时处理
			long endTime = System.currentTimeMillis();
			if (endTime - startTime < 2000) {
				//动画没播完
				SystemClock.sleep(2000 - (endTime - startTime));
			}
			//统一发消息
			mHandler.sendMessage(msg);
		}
		
	}
	
	/**
	 * 是否下载的对话框
	 * @param versionInfo 
	 */
	private void showIsDownLoadDialog() {
		//getApplicationContext() Context类型
		AlertDialog.Builder ab = new AlertDialog.Builder(this);//context Activity类型
		//设置不可以取消
		//ab.setCancelable(false);
		ab.setTitle("提醒");
		ab.setMessage("有新版本是否下载：\n新版本功能：" + mVersionInfo.desc);
		ab.setPositiveButton("下载", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载
				downloadNewApk(mVersionInfo);
				
			}
		}).setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 主界面
				startHome();
				
			}
		});
		ab.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// 取消
				//进入主界面
				startHome();
				
			}
		});
		ab.show();//显示对话框
		
		
	}

	/**
	 * 下载新的apk
	 * @param versionInfo
	 */
	protected void downloadNewApk(VersionInfo versionInfo) {
		
		
		HttpUtils httpUtils = new HttpUtils();
		//放到缓存目录
		File sdDir = Environment.getExternalStorageDirectory();//getCacheDir();
		
		File apkFile = new File(sdDir,"newms.apk");
		if (apkFile.exists()) {
			//删除
			apkFile.delete();
		}
		httpUtils.download(versionInfo.downloadUrl,sdDir.getAbsolutePath() +  "/newms.apk", new RequestCallBack<File>() {
			
			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				// 主线程
				// 安装apk
				/*
				 *  <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="content" />
                <data android:scheme="file" />
                <data android:mimeType="application/vnd.android.package-archive" />
            </intent-filter>
				 */
				
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				File file = new File(Environment.getExternalStorageDirectory(),"newms.apk");
				
				//数据和mimeType同时制定
				intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
				startActivityForResult(intent, 1);
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// 主线程
				//进入主界面
System.out.println("onFailure" + arg0);					
				startHome();
				
			}
		});
		
	}
	
	//监控打开的Activity的结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//进入主界面
		startHome();
		super.onActivityResult(requestCode, resultCode, data);
	}

	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//主线程中 
			switch (msg.what) {
			case LOADMAIN:
				Toast.makeText(getApplicationContext(), "已经最新版", 1).show();
				startHome();//进入主界面
				break;
			case NEWVERSION:
				showIsDownLoadDialog();//显示对话框
				break;
			default:
				//异常，进入主界面
				switch (msg.what) {
				case 404:
					Toast.makeText(getApplicationContext(), "网络资源不存在", 1).show();
					break;
				case 500:
					Toast.makeText(getApplicationContext(), "网络服务内部错误", 1).show();
					break;
				case 10090:
					Toast.makeText(getApplicationContext(), "json格式错误", 1).show();
					break;
				case 10089:
					Toast.makeText(getApplicationContext(), "没有网络", 1).show();
					break;
				default:
					Toast.makeText(getApplicationContext(), "服务器错误", 1).show();
					break;
				}
				startHome();//进入主界面
				
				break;
			}
			
		};
	};

	private VersionInfo mVersionInfo;
	
	private class VersionInfo{
		String versionName;//版本名
		int versionCode;//版本号
		String downloadUrl;//新版本下载路径
		String desc;//新版本的功能描述
	}
	
	/**
	 * 解析json数据  alt + shift + j
	 * @param json
	 * @throws JSONException 
	 */
	private VersionInfo parseJson(String json) throws JSONException {
		VersionInfo info = new VersionInfo();
		//解析json数据
		JSONObject jsonObject = new JSONObject(json);
		
		info.desc = jsonObject.getString("desc");//版本名
		info.versionName = jsonObject.getString("versionname");
		info.versionCode = jsonObject.getInt("versioncode");
		info.downloadUrl = jsonObject.getString("downloadurl");
		
		return info;
	}

	/**
	 * 把io流转成字符串
	 * @param is
	 * @return
	 * @throws IOException 
	 */
	private String stream2json(InputStream is) throws IOException{
		StringBuilder mess = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		String line = reader.readLine();
		while (line != null) {
			//把每行信息拼接一起
			mess.append(line);
			//继续读取 
			line = reader.readLine();
			
		}
		return mess + "";
	}

	private void initData() {
		// 组织数据
		// 版本名 从清单文件中配置
		// api PackageManager 静态数据 ActivityManager 动态数据,内存的使用

		// Manager 结尾 getContext();

		mPm = getPackageManager();

		try {
			PackageInfo packageInfo = mPm.getPackageInfo(getPackageName(), 0);
			// 版本号
			mVersionCode = packageInfo.versionCode;
			
			// 版本名
			String versionName = packageInfo.versionName;
			// 显示数据
			tv_versioname.setText(versionName);

			tv_versiocode.setText(mVersionCode + "");

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initView() {
		// 初始化界面
		setContentView(R.layout.activity_splash);

		// 获取splash界面的根布局
		rl_root = (RelativeLayout) findViewById(R.id.rl_splash_root);

		// 版本号
		tv_versiocode = (TextView) findViewById(R.id.tv_splash_versioncode);

		// 版本名
		tv_versioname = (TextView) findViewById(R.id.tv_splash_versionname);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
