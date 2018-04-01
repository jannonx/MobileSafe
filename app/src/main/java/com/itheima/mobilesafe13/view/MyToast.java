package com.itheima.mobilesafe13.view;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.utils.MyConstaints;
import com.itheima.mobilesafe13.utils.SPUtils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Administrator
 * @desc 自定义Toast
 * @date 2015-9-18
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-20 10:03:54 +0800 (Sun, 20 Sep 2015) $
 * @Id $Id: MyToast.java 58 2015-09-20 02:03:54Z goudan $
 * @Rev $Rev: 58 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/view/MyToast.java $
 * 
 */
public class MyToast implements OnTouchListener {

	private WindowManager mWM;
	private WindowManager.LayoutParams mParams;
	private View mView;
	private Context mContext ;
	private float downX;
	private float downY;

	public MyToast(Context context) {
		// 0. WindowManager
		mContext = context;
		mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		// 1. params
		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.format = PixelFormat.TRANSLUCENT;
		//去掉toast原装动画
		mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		mParams.setTitle("Toast");
		mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				/*| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE*/;
		//设置gravity
		mParams.gravity = Gravity.LEFT | Gravity.TOP;//坐标原点为坐上角
		mParams.x = SPUtils.getInt(mContext, MyConstaints.TOASTX, 0);
		mParams.y = SPUtils.getInt(mContext, MyConstaints.TOASTY, 0);
		// 2. View
		
		
	}
	
	/**
	 * @param styleIndex
	 * 		归属地样式的标记
	 */
	private void setBackGroundStyle(int styleIndex){
		mView.setBackgroundResource(ShowLocationStyleDialog.bgColors[styleIndex]);
	}
	
	

	public void show(String location) {
		 mView = View.inflate(mContext, R.layout.sys_toast, null);
		 //设置背景样式
		 setBackGroundStyle(SPUtils.getInt(mContext, MyConstaints.LOCATIONSTYLEINDEX, 0));
		 TextView tv_mess = (TextView) mView.findViewById(R.id.tv_toast_text);
		 tv_mess.setText(location);
		 mView.setOnTouchListener(this);//设置touch事件
		 mWM.addView(mView, mParams);
	}
	
	public void hiden(){
		if (mView != null) {
            // note: checking parent() just to make sure the view has
            // been added...  i have seen cases where we get here when
            // the view isn't yet added, so let's try not to crash.
            if (mView.getParent() != null) {
                //mView赋值了 但是没有mWM.addView(mView, mParams);
                mWM.removeView(mView);
            }

            mView = null;
        }
		
	}



	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		// 触摸事件
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getRawX();
			downY = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			
			float moveX = event.getRawX();
			float moveY = event.getRawY();
			
			float dx = moveX - downX;
			float dy = moveY - downY;
			
			//改变toast的参数
			mParams.x += dx;
			mParams.y += dy;
			
			// x y新的坐标
			//判断越界
			if(mParams.x  < 0 ) {
				mParams.x = 0;
			} else if (mParams.x > mWM.getDefaultDisplay().getWidth() - mView.getWidth()) {
				mParams.x = mWM.getDefaultDisplay().getWidth() - mView.getWidth();
			}
			
			if (mParams.y < 0) {
				mParams.y = 0;
			} else if (mParams.y > mWM.getDefaultDisplay().getHeight() - mView.getHeight()) {
				mParams.y = mWM.getDefaultDisplay().getHeight() - mView.getHeight();
			}
			
			//改变view的位置
			mWM.updateViewLayout(mView, mParams);
			
			//该位置变为新的起始位置 
			downX = moveX;
			downY = moveY;
			
			
			break;
		case MotionEvent.ACTION_UP:
			
			//保存toast的位置
			SPUtils.putInt(mContext, MyConstaints.TOASTX, mParams.x);
			SPUtils.putInt(mContext, MyConstaints.TOASTY, mParams.y);
			break;

		default:
			break;
		}
		return true;//自己消费掉
	}
}
