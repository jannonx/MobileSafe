package com.itheima.mobilesafe13.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe13.db.BlackDB;
import com.itheima.mobilesafe13.domain.BlackBean;
import com.itheima.mobilesafe13.domain.ContactBean;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-16
 * @desc 对黑名单数据的操作
 * 
 * @version $Rev: 43 $
 * @author $Author: goudan $
 * @Date $Date: 2015-09-17 11:29:15 +0800 (Thu, 17 Sep 2015) $
 * @Id $Id: BlackDao.java 43 2015-09-17 03:29:15Z goudan $
 * @Url $URL:
 *      https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com
 *      /itheima/mobilesafe13/dao/BlackDao.java $
 * 
 */
public class BlackDao {
	// 黑名单数据库
	private BlackDB mBlackDB;

	public BlackDao(Context context) {
		mBlackDB = new BlackDB(context);
	}

	public void update(BlackBean bean) {
		update(bean.getPhone(), bean.getMode());
	}
	
	/**
	 * @param phone
	 *      号码
	 * @return
	 *    0 不拦截  SMS_MODE 短信拦截  PHONE_MODE 电话拦截  ALL_MODE 全部拦截
	 */
	public int getMode(String phone){
		int mode = 0;
		SQLiteDatabase database = mBlackDB.getReadableDatabase();
		Cursor cursor = database.rawQuery("select "
				+ BlackDB.MODE + " from " + BlackDB.BLACKTB
				+ " where  " + BlackDB.PHONE + "=? ", new String[]{phone});
		if (cursor.moveToNext()) {
			mode = cursor.getInt(0);//获取模式
		}
		return mode;
	}

	/**
	 * 如果有 先删除再添加 没有 直接添加
	 * 
	 * @param phone
	 *            修改的黑名单号码
	 * @param mode
	 *            新的拦截模式
	 */
	public void update(String phone, int mode) {
		// 删除
		delete(phone);
		// 添加
		add(phone, mode);
	}

	/**
	 * @param phone
	 *            要删除的黑名单号码
	 * @return true 删除成功 false删除失败
	 */
	public boolean delete(String phone) {
		// 1.获取数据库
		SQLiteDatabase database = mBlackDB.getWritableDatabase();
		// 2. 删除
		int count = database.delete(BlackDB.BLACKTB, BlackDB.PHONE + "=?",
				new String[] { phone });
		// 3. 数据的关闭
		database.close();
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param data
	 *            黑名单数据的对象
	 */
	public void add(BlackBean data) {
		add(data.getPhone(), data.getMode());
	}

	/**
	 * 添加黑名单数据
	 * 
	 * @param phone
	 *            黑名单号码
	 * @param mode
	 *            拦截模式 SMS_MODE 短信 PHONE_MODE 电话 ALL_MODE 全部
	 */
	public void add(String phone, int mode) {
		// 1.获取数据库
		SQLiteDatabase database = mBlackDB.getWritableDatabase();
		// 2.设置数据
		ContentValues values = new ContentValues();
		values.put(BlackDB.PHONE, phone);
		values.put(BlackDB.MODE, mode);
		// 3. 添加数据
		database.insert(BlackDB.BLACKTB, null, values);

		// 4.关闭数据库
		database.close();

	}

	/**
	 * @return 所有的黑名单数据
	 */
	public List<BlackBean> findAll() {
		List<BlackBean> datas = new ArrayList<BlackBean>();
		// 获取只读数据库
		SQLiteDatabase database = mBlackDB.getReadableDatabase();
		Cursor cursor = database.rawQuery("select " + BlackDB.PHONE + ","
				+ BlackDB.MODE + " from " + BlackDB.BLACKTB
				+ " order by _id desc", null);
		BlackBean data = null;
		while (cursor.moveToNext()) {
			// 有数据
			// 1. 封装数据
			data = new BlackBean();
			// 设置黑名单号码
			data.setPhone(cursor.getString(0));
			// 设置拦截模式
			data.setMode(cursor.getInt(1));

			// 2. 添加数据
			datas.add(data);
		}
		return datas;
	}

	/**
	 * @param pageNumber
	 *            页码从1开始
	 * @return 当前页的数据
	 */
	public List<BlackBean> getPageData(int pageNumber,int countsPerPage) {
		List<BlackBean> beans = new ArrayList<BlackBean>();
		// sql
		int startIndex = (pageNumber - 1) * countsPerPage;
		SQLiteDatabase database = mBlackDB.getReadableDatabase();
		Cursor cursor = database.rawQuery("select " + BlackDB.PHONE + ","
				+ BlackDB.MODE + " from " + BlackDB.BLACKTB + " order by _id desc limit ?,?", new String[]{startIndex + "",countsPerPage +""});
		
		BlackBean data = null;
		while (cursor.moveToNext()) {
			// 有数据
			// 1. 封装数据
			data = new BlackBean();
			// 设置黑名单号码
			data.setPhone(cursor.getString(0));
			// 设置拦截模式
			data.setMode(cursor.getInt(1));

			// 2. 添加数据
			beans.add(data);
		}
		return beans;
	}
	
	/**
	 * @param startRow
	 * 		起始行
	 * @param countPerLoad
	 *      分配加载多少条数据 
	 * @return
	 *      新加载的数据
	 */
	public List<BlackBean> loadMore(int startRow,int countPerLoad){
		List<BlackBean> beans = new ArrayList<BlackBean>();
		// sql
		int startIndex = startRow;
		SQLiteDatabase database = mBlackDB.getReadableDatabase();
		Cursor cursor = database.rawQuery("select " + BlackDB.PHONE + ","
				+ BlackDB.MODE + " from " + BlackDB.BLACKTB + " order by _id desc limit ?,?", new String[]{startIndex + "",countPerLoad +""});
		
		BlackBean data = null;
		while (cursor.moveToNext()) {
			// 有数据
			// 1. 封装数据
			data = new BlackBean();
			// 设置黑名单号码
			data.setPhone(cursor.getString(0));
			// 设置拦截模式
			data.setMode(cursor.getInt(1));

			// 2. 添加数据
			beans.add(data);
		}
		return beans;
	}

	public int getTotalRows() {
		int totalRows = 0;
		// sql
		SQLiteDatabase database = mBlackDB.getReadableDatabase();
		Cursor cursor = database.rawQuery("select count(1) from blacktb", null);
		if (cursor.moveToNext()) {
			totalRows = cursor.getInt(0);
		}
		return totalRows;
	}
}
