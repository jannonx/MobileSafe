package com.itheima.mobilesafe13.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe13.R;
import com.itheima.mobilesafe13.dao.ContactsDao;
import com.itheima.mobilesafe13.domain.ContactBean;
import com.itheima.mobilesafe13.utils.MyConstaints;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-14
 * @desc 显示所有好友信息的界面

 * @version $Rev: 36 $
 * @author $Author: goudan $
 * @Date  $Date: 2015-09-16 15:49:15 +0800 (Wed, 16 Sep 2015) $
 * @Id    $Id: FriendsActivity.java 36 2015-09-16 07:49:15Z goudan $
 * @Url   $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/activity/FriendsActivity.java $
 * 
 */
public class FriendsActivity extends BaseSmsTelFriendsActivity {

	@Override
	public List<ContactBean> getDatas() {
		// TODO Auto-generated method stub
		return  ContactsDao.getContacts(getApplicationContext());
	}

}
