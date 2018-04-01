package com.itheima.mobilesafe13.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe13.db.LockedDB;

/**
 * @author Administrator
 * @desc 程序锁数据库的业务dao
 * @date 2015-9-25
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-25 15:09:07 +0800 (Fri, 25 Sep 2015) $
 * @Id $Id: LockedDao.java 107 2015-09-25 07:09:07Z goudan $
 * @Rev $Rev: 107 $
 * @URL $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/dao/LockedDao.java $
 * 
 */
public class LockedDao {
	private LockedDB mLockedDB;
	private Context mContext;

	public LockedDao(Context context) {
		mLockedDB = new LockedDB(context);
		mContext = context;
	}

	/**
	 * 
	 * 对一个app完成加锁
	 * 
	 * @param packName
	 *            app的包名
	 */
	public void addLockedPackName(String packName) {

		SQLiteDatabase database = mLockedDB.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LockedDB.PACKNAME, packName);
		database.insert(LockedDB.LOCKED_TB, null, values);
		database.close();

		// 发送通知
		mContext.getContentResolver().notifyChange(LockedDB.URI, null);
	}

	/**
	 * 对一个app完成解锁
	 * 
	 * @param packName
	 */
	public void removeLockedPackName(String packName) {

		SQLiteDatabase database = mLockedDB.getWritableDatabase();

		database.delete(LockedDB.LOCKED_TB, LockedDB.PACKNAME + "=?",
				new String[] { packName });
		database.close();

		// 发送通知
		mContext.getContentResolver().notifyChange(LockedDB.URI, null);
	}

	/**
	 * 判断app是否是加锁的
	 * 
	 * @param packName
	 *            app包名
	 * @return 是否加锁
	 */
	public boolean isLocked(String packName) {
		boolean res = false;
		SQLiteDatabase database = mLockedDB.getReadableDatabase();
		Cursor cursor = database.rawQuery("select 1 from " + LockedDB.LOCKED_TB
				+ " where " + LockedDB.PACKNAME + " = ? ",
				new String[] { packName });
		if (cursor.moveToNext()) {
			res = true;
		}

		return res;
	}

	/**
	 * @return 所有加锁的app包名
	 */
	public List<String> getAllLockedAppPacknames() {
		List<String> lockedPacknames = new ArrayList<String>();
		SQLiteDatabase database = mLockedDB.getReadableDatabase();
		Cursor cursor = database.rawQuery("select " + LockedDB.PACKNAME
				+ "  from " + LockedDB.LOCKED_TB, null);
		while (cursor.moveToNext()) {
			lockedPacknames.add(cursor.getString(0));
		}

		return lockedPacknames;
	}
}
