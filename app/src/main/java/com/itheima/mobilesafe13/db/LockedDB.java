package com.itheima.mobilesafe13.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class LockedDB extends SQLiteOpenHelper {
	
	public static final String LOCKED_TB = "locked_tb";
	public static final Uri URI = Uri.parse("content://itheima13.locked");
	
	public static final String PACKNAME = "packname";
	public LockedDB(Context context) {
		//super(arg0, arg1, arg2, arg3)
		super(context, "locked.db", null,1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table locked_tb(_id integer primary key autoincrement,packname text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table locked_tb");
		onCreate(db);
	}

}
