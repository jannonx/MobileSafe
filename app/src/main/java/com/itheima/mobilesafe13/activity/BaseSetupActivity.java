package com.itheima.mobilesafe13.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.itheima.mobilesafe13.R;
 
public abstract class BaseSetupActivity extends Activity {
	private GestureDetector mGD;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		initView();
		
		initEvent();
		
		initData();
		
		initGuesture();
	}
	
	/**
	 * 添加手势识别器
	 */
	private void initGuesture() {
		//绑定onTouch事件
		mGD = new GestureDetector(new MyOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				// e1 按下的点
				// e2 松开的点
				// velocityX x方向速度  单位 px/s
				// velocityY y方向的速度
				//判断 是否是x方向滑动
				if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e2.getY()- e1.getY())) {
					//横向滑动
					//判断速度
					if (Math.abs(velocityX) > 50){//
						//判断方向
						if (velocityX < 0) {
							//从左往右滑
							prevPage(null);
						} else {
							//从右往左滑
							nextPage(null);
						}
					}
				}
				return true;
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGD != null) {
			mGD.onTouchEvent(event);
			return true;//消费事件  handler  
		}
		return super.onTouchEvent(event);
	}

	public void startPage(Class type) {
		Intent intent = new Intent(this,type);
		startActivity(intent);
		finish();//关闭自己
	}
	
	protected abstract void startNext();
	protected abstract void startPrev();
	
	/**
	 * 跳转到下一个页面的按钮事件
	 * @param v
	 */
	public void nextPage(View v) {
		//下一个页面
		startNext();//
		//位移动画,页面切换才执行
		nextPageAnimation();
	}
	/**
	 * 跳转到上一个页面的按钮事件
	 * @param v
	 */
	public void prevPage(View v) {
		startPrev();
		//上一个页面的动画
		prevPageAnimation();
	}

	private void nextPageAnimation() {
		//下一个页面切换的动画
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_exit_anim);
	}
	private void prevPageAnimation() {
		//下一个页面切换的动画
		overridePendingTransition(R.anim.prev_enter_anim, R.anim.prev_exit_anim);
	}

	/**
	 * 子类覆盖此方法完成数据的显示
	 */
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 子类覆盖此方法完成事件的初始化 
	 */
	protected void initEvent() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 子类需要覆盖此方法，完成界面的显示
	 */
	protected void initView() {
		//界面显示
		
	}
	
	private class MyOnGestureListener implements OnGestureListener{

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
