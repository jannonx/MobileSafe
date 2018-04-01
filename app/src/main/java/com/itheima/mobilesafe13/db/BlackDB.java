package com.itheima.mobilesafe13.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-16
 * @desc 黑名单数据库 

 * @version $Rev: 31 $
 * @author $Author: goudan $
 * @Date  $Date: 2015-09-16 10:30:38 +0800 (Wed, 16 Sep 2015) $
 * @Id    $Id: BlackDB.java 31 2015-09-16 02:30:38Z goudan $
 * @Url   $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/db/BlackDB.java $
 * 
 */
public class BlackDB extends SQLiteOpenHelper {
	
	private final static int VERSION = 1;
	
	//表名
	public static final String BLACKTB = "blacktb";
	
	//黑名单电话
	public static final String PHONE = "phone";
	//拦截模式
	public static final String MODE = "mode";
	
	//短信拦截
	public static final int SMS_MODE = 1 << 0;   // 01
	//电话拦截
	public static final int PHONE_MODE = 1 << 1; // 10
	//全部拦截
	public static final int ALL_MODE = SMS_MODE | PHONE_MODE;
	

	public BlackDB(Context context) {
		super(context, "black.db", null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 第一个创建执行
		//创建表的操作
		db.execSQL("create table blacktb(_id integer primary key autoincrement,phone text,mode integer)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//版本号发生变化，调用该方法
		db.execSQL("drop table blacktb");
		onCreate(db);
	}

}
