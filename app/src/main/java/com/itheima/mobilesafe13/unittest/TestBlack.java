package com.itheima.mobilesafe13.unittest;

import java.util.List;

import com.itheima.mobilesafe13.dao.AddressDao;
import com.itheima.mobilesafe13.dao.BlackDao;
import com.itheima.mobilesafe13.dao.LockedDao;
import com.itheima.mobilesafe13.db.BlackDB;
import com.itheima.mobilesafe13.domain.AppInfoBean;
import com.itheima.mobilesafe13.domain.ServiceNameAndType;
import com.itheima.mobilesafe13.utils.AppInfoUtils;
import com.itheima.mobilesafe13.utils.SmsUtils;
import com.itheima.mobilesafe13.utils.TaskInfoUtils;

import android.test.AndroidTestCase;

public class TestBlack extends AndroidTestCase {
	public void testAppInfo(){
		LockedDao dao = new LockedDao(getContext());
		dao.addLockedPackName("aa.bb.cc");
		System.out.println(dao.getAllLockedAppPacknames());
		//System.out.println(TaskInfoUtils.getAllRunningAppInfos(getContext()));
	}
	public void testSmsBaike(){
		//SmsUtils.smsBaike(getContext());
		//SmsUtils.smsRestore(getContext());
	}
	public void testLocation(){
		List<ServiceNameAndType> allServiceTypes = AddressDao.getAllServiceTypes();
		for (ServiceNameAndType serviceNameAndType : allServiceTypes) {
			System.out.println(AddressDao.getNumberAndNames(serviceNameAndType));
		}
	}
	public void testTotal(){
		BlackDao dao = new BlackDao(getContext());
		System.out.println(dao.getPageData(1, 5));
	}
	public void testFindAll(){
		BlackDao dao = new BlackDao(getContext());
		System.out.println(dao.findAll());
	}
	public void testAdd(){
		//测试添加黑名单数据
		BlackDao dao = new BlackDao(getContext());
		for (int i = 0; i< 100; i++) {
			dao.add("110" + i, BlackDB.PHONE_MODE);
		}
	}
	

}
