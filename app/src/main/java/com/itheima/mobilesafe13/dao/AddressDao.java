package com.itheima.mobilesafe13.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itheima.mobilesafe13.domain.NumberAndName;
import com.itheima.mobilesafe13.domain.ServiceNameAndType;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-16
 * @desc 归属地数据库的封装
 * 
 * @version $Rev: 58 $
 * @author $Author: goudan $
 * @Date $Date: 2015-09-20 10:03:54 +0800 (Sun, 20 Sep 2015) $
 * @Id $Id: AddressDao.java 58 2015-09-20 02:03:54Z goudan $
 * @Url $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/dao/BlackDao.java $
 * 
 */
public class AddressDao {
	public static final String DBPATHPHONE = "/data/data/com.itheima.mobilesafe13/files/address.db";
	public static final String DBPATHSERVICE = "/data/data/com.itheima.mobilesafe13/files/commonnum.db";

	/**
	 * @param type
	 *            服务类型
	 * @return 具体服务类型的具体数据
	 */
	public static List<NumberAndName> getNumberAndNames(ServiceNameAndType type) {
		List<NumberAndName> mServiceNameAndTypes = new ArrayList<NumberAndName>();
		// 获取数据库
		SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATHSERVICE,
				null, SQLiteDatabase.OPEN_READONLY);
		// 查询
		Cursor cursor = database.rawQuery("select name,number from  table"
				+ type.getOut_id(), null);
		NumberAndName bean = null;
		while (cursor.moveToNext()) {
			bean = new NumberAndName();
			bean.setName(cursor.getString(0));// 名字
			bean.setNumber(cursor.getString(1));// 外键

			mServiceNameAndTypes.add(bean);
		}
		
		// 游标没关
		cursor.close();
		return mServiceNameAndTypes;
	}

	/**
	 * @return 获取所有服务的类型
	 */
	public static List<ServiceNameAndType> getAllServiceTypes() {
		List<ServiceNameAndType> mServiceNameAndTypes = new ArrayList<ServiceNameAndType>();
		// 获取数据库
		SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATHSERVICE,
				null, SQLiteDatabase.OPEN_READONLY);
		// 查询
		Cursor cursor = database.rawQuery("select name,idx from classlist ",
				null);
		ServiceNameAndType bean = null;
		while (cursor.moveToNext()) {
			bean = new ServiceNameAndType();
			bean.setName(cursor.getString(0));// 名字
			bean.setOut_id(cursor.getInt(1));// 外键

			mServiceNameAndTypes.add(bean);
		}
		
		// 游标没关
		cursor.close();
		return mServiceNameAndTypes;
	}

	/**
	 * @param number
	 *            手机号如：18899997777或者固定电话如：075512344321
	 * @return 归属地信息
	 */
	public static String getLocation(String number) {
		String location = "";

		// 是否是手机号
		Pattern p = Pattern.compile("1[34578]{1}[0-9]{9}");
		Matcher m = p.matcher(number);
		boolean b = m.matches();
		if (b) {
			location = getMobileLocation(number.substring(0, 7));
		} else {
			if (number.charAt(1) == '1' || number.charAt(1) == '2') {
				// 2位区号
				location = getPhoneLocation(number.substring(1, 3));
			} else {
				// 3位区号
				location = getPhoneLocation(number.substring(1, 4));
			}
		}
		return location.substring(0, location.length() - 2); //
	}

	/**
	 * @param mobileNumber
	 *            手机号的前七位
	 * @return 归属地信息
	 */
	private static String getMobileLocation(String mobileNumber) {
		String location = "未知截掉";
		//
		// 获取数据库
		SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATHPHONE,
				null, SQLiteDatabase.OPEN_READONLY);
		// 查询
		Cursor cursor = database
				.rawQuery(
						"select location from data2 where id=(select outkey from data1 where id=?)",
						new String[] { mobileNumber });

		if (cursor.moveToNext()) {
			// 获取归属地信息
			location = cursor.getString(0);
		}

		// 游标没关
		cursor.close();
		return location;
	}

	/**
	 * @param phoneNumber
	 *            输入的是固话的区号
	 * @return 位置信息
	 */
	private static String getPhoneLocation(String phoneNumber) {
		String location = "未知截掉";
		//
		// 获取数据库
		SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATHPHONE,
				null, SQLiteDatabase.OPEN_READONLY);
		// 查询
		Cursor cursor = database.rawQuery(
				"select location from data2 where area=?",
				new String[] { phoneNumber });

		if (cursor.moveToNext()) {
			// 获取归属地信息
			location = cursor.getString(0);
		}

		// 游标没关
		cursor.close();

		return location;
	}
}
