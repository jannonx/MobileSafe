package com.itheima.mobilesafe13.view;

import com.itheima.mobilesafe13.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TextProgressView extends RelativeLayout {

	private TextView tv_mess;
	private ProgressBar pb_progress;

	public TextProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView();
	}

	private void initView() {
		View rootView = View.inflate(getContext(), R.layout.view_messageprogress, this);
		
		tv_mess = (TextView) rootView.findViewById(R.id.tv_progresstext_message);
		
		pb_progress = (ProgressBar) rootView.findViewById(R.id.pb_progresstext_progressscale);
		pb_progress.setMax(100);
	}
	
	/**
	 * 
	 * @param progressScale
	 * 	    百分比 如： 10%  0.1
	 */
	public void setProgress(double  progressScale) {
		//设置进度条的进度
		pb_progress.setProgress((int)Math.round(100 * progressScale));
	}
	
	/**
	 * @param mess
	 *      显示的值
	 */
	public void setMessage(String mess) {
		tv_mess.setText(mess);
	}

	public TextProgressView(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
	
	

}
