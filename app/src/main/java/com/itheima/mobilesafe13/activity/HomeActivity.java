package com.itheima.mobilesafe13.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.utils.Md5Utils;
import com.itheima.mobilesafe13.utils.MyConstaints;
import com.itheima.mobilesafe13.utils.SPUtils;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-12
 * @desc 手机卫士的主界面

 * @version $Rev: 113 $
 * @author $Author: goudan $
 * @Date  $Date: 2015-09-26 11:27:05 +0800 (Sat, 26 Sep 2015) $
 * @Id    $Id: HomeActivity.java 113 2015-09-26 03:27:05Z goudan $
 * @Url   $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/activity/HomeActivity.java $
 * 
 */
public class HomeActivity extends Activity {
	private ImageView iv_logo;
	private ImageView iv_setting;
	private GridView gv_tool;
	
	private static final String[] names = new String[]{"手机防盗","通讯卫士","软件管家","进程管理","流量统计","病毒查杀","缓存清理","高级工具"};
	private static final String[] descs = new String[]{"手机丢失好找","防骚扰防监听","方便管理软件","保持手机通畅","注意流量超标","手机安全保障","手机快步如飞","特性处理更好"};
	private static final int[] icons = new int[]{R.drawable.sjfd,R.drawable.srlj,R.drawable.rjgj,R.drawable.jcgl,
		R.drawable.lltj,R.drawable.sjsd,R.drawable.hcql,R.drawable.szzx};
	private AlertDialog mAD;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//初始化界面
		initView();
		
		startAnimation();//开始动画
		
		initData();// 设置数据
		
		initEvent();//初始化事件
	}

	private void initEvent() {
		
		//添加设置中心的按钮事件
		iv_setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 跳转到设置中心界面
				Intent settingCenter = new Intent(HomeActivity.this,SettingCenterActivity.class);
				startActivity(settingCenter);
				
			}
		});
		//给GridView添加item点击事件
		gv_tool.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				switch (position) {
				case 2://软件管家
				{
					Intent black = new Intent(HomeActivity.this,AppManagerActivity.class);
					startActivity(black);
					break;
				}
				case 5://病毒查杀
				{
					Intent black = new Intent(HomeActivity.this,AntiVirusActivity.class);
					startActivity(black);
					break;
				}
				case 6://缓存的获取和清理
				{
					Intent black = new Intent(HomeActivity.this,CacheInfoActivity.class);
					startActivity(black);
					break;
				}
				case 4://流量统计
				{
					Intent black = new Intent(HomeActivity.this,ConnectivityActivity.class);
					startActivity(black);
					break;
				}
				case 3://进程管家
				{
					//时间判断
					long currentTimeMillis = System.currentTimeMillis();
					long clearTime = SPUtils.getLong(getApplicationContext(), MyConstaints.CLEARTIME, 0l);
					long dis = currentTimeMillis - clearTime;
					/*if (dis < 30000) {
						Toast.makeText(getApplicationContext(), "您的手机非常干净，无需清理", 1).show();
						return;
					}*/
					Intent black = new Intent(HomeActivity.this,TaskManagerActivity.class);
					startActivity(black);
					break;
				}
				case 0: //手机防盗
					//首次使用 显示设置密码的对话框
					//已经设置过密码，显示输入密码的对话
					String password = SPUtils.getString(getApplicationContext(), MyConstaints.PASSWORD, null);
					if (TextUtils.isEmpty(password)) {
						//没有设置过密码
						//显示设置密码的对话框
						showSetPasswordDialog();
					} else {
						//设置过密码，显示输入密码的对话
System.out.println("设置过密码");						
						showEnterPasswordDialog();
					}
					break;
				case 1 ://黑名单管理
					Intent black = new Intent(HomeActivity.this,AndroidBlackActivity.class);
					startActivity(black);
					break;
				case 7://高级工具
					Intent atool = new Intent(HomeActivity.this,AToolActivity.class);
					startActivity(atool);
					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * 显示输入密码的对话框
	 */
	protected void showEnterPasswordDialog() {
		
		showMyDialog(false);
		
	}

	/**
	 * 设置密码的对话框
	 */
	protected void showSetPasswordDialog() {
		showMyDialog(true);
	}

	/**
	 * @param isSetPassword
	 * 		true 设置密码  false 输入密码
	 */
	private void showMyDialog(final boolean isSetPassword) {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		//把布局转换成View
		View layout = View.inflate(getApplicationContext(), R.layout.dialog_setpassword, null);
		final TextView tv_title = (TextView) layout.findViewById(R.id.tv_dialog_title);
		//获取view的子组件
		final EditText et_pass1 = (EditText) layout.findViewById(R.id.et_dialog_password1);
		final EditText et_pass2 = (EditText) layout.findViewById(R.id.et_dialog_password2);
		
		final Button bt_confirm = (Button) layout.findViewById(R.id.bt_dialog_confirm);
		final Button bt_cancel = (Button) layout.findViewById(R.id.bt_dialog_cancel);
		
		//判断下是否是输入密码
		// 隐藏pass2 修改标题
		if (!isSetPassword) {
			//输入密码
			et_pass2.setVisibility(View.GONE);
			tv_title.setText("输入密码");
		}
		
		
		final View.OnClickListener listener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//判断 v 的类型
				if (v == bt_confirm) {
					//先取两个密码
					String pass1 = et_pass1.getText().toString().trim();
					String pass2 = et_pass2.getText().toString().trim();
					
					if (!isSetPassword) {
						//输入密码
						pass2 = "aaa";
					}
					//判断是否为空
					if (TextUtils.isEmpty(pass2) || TextUtils.isEmpty(pass1)) {
						//提醒密码不能为空
						Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
						
						return;
					} 
					
					if (!isSetPassword) {
						//输入密码  都是MD5值的比较 
						if (Md5Utils.encode(pass1).equals(SPUtils.getString(getApplicationContext(), MyConstaints.PASSWORD, ""))) {
							//密码成功
							//跳转到手机防盗界面
							Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
							startActivity(intent);
						} else {
							//密码错误
							Toast.makeText(getApplicationContext(), "密码错误", 0).show();
							return;
						}
					} else {
						//设置密码
						if (pass1.equals(pass2)) {
							//密码一致
							// 保存设置密码
							//保存密码
							//先对密码进行MD5加密
							SPUtils.putString(getApplicationContext(), MyConstaints.PASSWORD, Md5Utils.encode(pass1));
							Toast.makeText(getApplicationContext(), "密码设置成功", 0).show();
						} else {
							//密码不一致
							Toast.makeText(getApplicationContext(), "两次密码不一致", 0).show();
							return;
						}
					}
					
					
					//是否一致
					mAD.dismiss();
				} else {
					//取消
					//关闭对话框
					mAD.dismiss();
				}
			}
		};
		//给两个按钮设置同一个监听事件
		bt_cancel.setOnClickListener(listener);
		bt_confirm.setOnClickListener(listener);
		
		ab.setView(layout);
		mAD = ab.create();
		mAD.show();
	}

	private void initData() {
		MyAdapater adapater = new MyAdapater();
		//设置适配器
		gv_tool.setAdapter(adapater);
		
	}

	/**
	 * 对logo添加旋转动画
	 */
	private void startAnimation() {
		// 属性动画来完成logo的旋转
		//target  要完成动画的View
		//propertyName  setXXX   把set后面单词首字符改成小写
		ObjectAnimator oa = ObjectAnimator.ofFloat(iv_logo, "rotationY" ,0,60,90,120,180,240,300,360);
		//属性动画   对属性变化过程一系列的操作组成动画
		
		oa.setDuration(2000);//一次动画完成的时间
		oa.setRepeatCount(ObjectAnimator.INFINITE);//-1
		//开始播放动画
		oa.start();
		
		
		
	}
	
	private class MyAdapater extends BaseAdapter{

		@Override
		public int getCount() {
			// 数据的个数
			return names.length;
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
			// 界面转换成View
			View view = View.inflate(getApplicationContext(), R.layout.item_home_gv, null);
			
			//view的子组件
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_item_home_gv_icon);
			//标题
			TextView tv_title = (TextView) view.findViewById(R.id.tv_item_home_gv_title);
			
//			描述
			TextView tv_desc = (TextView) view.findViewById(R.id.tv_item_home_gv_desc);
			
			//赋值
			
			//图片
			iv_icon.setImageResource(icons[position]);
			//标题
			tv_title.setText(names[position]);
//			描述 
			tv_desc.setText(descs[position]);
			return view;
		}
		
	}
	
	

	private void initView() {
		setContentView(R.layout.activity_home);
		
		//获取布局文件中所有子View
		
		//获取logo
		iv_logo = (ImageView) findViewById(R.id.iv_home_logo);
		
		//设置的按钮
		iv_setting = (ImageView) findViewById(R.id.iv_home_setting);
		
		//GridView
		gv_tool = (GridView) findViewById(R.id.gv_home_tools);
	}
}
