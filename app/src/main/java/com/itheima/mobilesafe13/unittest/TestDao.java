package com.itheima.mobilesafe13.unittest;

import com.itheima.mobilesafe13.dao.ContactsDao;
import com.itheima.mobilesafe13.utils.ServiceUtils;

import android.test.AndroidTestCase;

public class TestDao extends AndroidTestCase{
	public void testgetContacts(){
		ServiceUtils.isServiceRunning(getContext(), "");
		//System.out.println(ContactsDao.getContacts(getContext()));
	}
}
