package com.itheima.mobilesafe13.activity;

import java.util.List;

import com.itheima.mobilesafe13.dao.ContactsDao;
import com.itheima.mobilesafe13.domain.ContactBean;

public class TelAcitivity extends BaseSmsTelFriendsActivity {

	@Override
	public List<ContactBean> getDatas() {
		// TODO Auto-generated method stub
		return ContactsDao.getTelContact(getApplicationContext());
	}

}
