package com.itheima.mobilesafe13.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe13.R;

/**
 * @author Administrator
 * @desc 程序锁界面的头布局封装view
 * @date 2015-9-25
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-25 10:32:10 +0800 (Fri, 25 Sep 2015) $
 * @Id $Id: AppLockHeadView.java 105 2015-09-25 02:32:10Z goudan $
 * @Rev $Rev: 105 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/view/AppLockHeadView.java $
 * 
 */
public class AppLockHeadView extends RelativeLayout {

	private View rootView;
	private TextView tv_lock;
	private TextView tv_unlock;

	public AppLockHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		initEvent();
	}
	
	private OnLockChangeListener mOnLockChangeListener;
	
	public void setOnLockChangeListener(OnLockChangeListener onLockChangeListener){
		this.mOnLockChangeListener = onLockChangeListener;
	}
	
	/**
	 * @author Administrator
	 * @desc 按钮的回调接口
	 * @date 2015-9-25
	 * @Author $Author: goudan $ Id Rev URL
	 * @Date $Date: 2015-09-25 10:32:10 +0800 (Fri, 25 Sep 2015) $
	 * @Id $Id: AppLockHeadView.java 105 2015-09-25 02:32:10Z goudan $
	 * @Rev $Rev: 105 $
	 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/view/AppLockHeadView.java $
	 *
	 */
	public interface OnLockChangeListener{
		/**
		 * @param isLocked
		 *       true 已加锁  false  未加锁
		 */
		void onLockChanged(boolean isLocked);
	}

	private void initEvent() {
		OnClickListener listener = new OnClickListener() {
			boolean isLock = false;
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tv_applock_lock:// 加锁
					tv_lock.setBackgroundResource(R.drawable.tab_left_pressed);
					tv_unlock.setBackgroundResource(R.drawable.tab_right_default);
					tv_lock.setTextColor(Color.WHITE);
					tv_unlock.setTextColor(Color.GRAY);
					
					isLock = true;
					break;
				case R.id.tv_applock_unlock:// 解锁
					tv_lock.setBackgroundResource(R.drawable.tab_left_default);
					tv_unlock.setBackgroundResource(R.drawable.tab_right_pressed);
					tv_lock.setTextColor(Color.GRAY);
					tv_unlock.setTextColor(Color.WHITE);
					isLock = false;
					break;
				default:
					break;
				}
				//处理回调 把数据状态回调给用户
				if (mOnLockChangeListener != null) {
					mOnLockChangeListener.onLockChanged(isLock);
				}

			}
		};
		tv_lock.setOnClickListener(listener);
		tv_unlock.setOnClickListener(listener);
	}

	private void initView() {
		// 把布局转成view添加到容器中

		rootView = View.inflate(getContext(), R.layout.view_applockhead, this);

		tv_lock = (TextView) rootView.findViewById(R.id.tv_applock_lock);
		tv_unlock = (TextView) rootView.findViewById(R.id.tv_applock_unlock);

	}

	public AppLockHeadView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

}
