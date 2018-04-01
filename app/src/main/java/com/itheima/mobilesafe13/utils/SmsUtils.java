package com.itheima.mobilesafe13.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.RecoverySystem.ProgressListener;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.itheima.mobilesafe13.utils.SmsUtils.SmsJsonData.Sms;

/**
 * @author Administrator
 * @desc 短信备份还原的tool  格式：json
 * @date 2015-9-20
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-21 08:57:12 +0800 (Mon, 21 Sep 2015) $
 * @Id $Id: SmsUtils.java 71 2015-09-21 00:57:12Z goudan $
 * @Rev $Rev: 71 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/utils/SmsUtils.java $
 *
 */
public class SmsUtils {
	/**
	 * @param str
	 *     特殊字符串
	 * @return
	 *     原字符串
	 */
	private static String convert2source(String str){
		String res = "";
		for (int i = 0; i < str.length(); i++) {
			res += convert2source(str.charAt(i));
		}
		return res;
	}
	
	/**
	 * @param str
	 *     原字符串
	 * @return
	 *     特殊字符串
	 */
	private static String convert2ts(String str){
		String res = "";
		for (int i = 0; i < str.length(); i++) {
			res += convert2ts(str.charAt(i));
		}
		return res;
	}
	/**
	 * 
	 * @param c
	 *       json 格式字符
	 * @return
	 *       转义字符
	 */
	private static char convert2source(char c){
		char res = '\u0000';
		//" ★  { 卍  } 卐 [ ◎ ] ¤ : ℗ , ✿
		switch (c) {
		case '★':
			res = '"';
			break;
		case '卍':
			res = '{';
			break;
		case '卐':
			res = '}';
			break;
		case '◎':
			res = '[';
			break;
		case '¤':
			res = ']';
			break;
		case '℗':
			res = ':';
			break;
		case '✿':
			res = ',';
			break;

		default:
			res = c;
			break;
		}
		return res;
	}
	
	/**
	 * 
	 * @param is
	 *            输入流
	 * @return 字符串
	 */
	public static String stream2String(InputStream is) {
		StringBuilder mess = new StringBuilder();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		try {
			String line = reader.readLine();
			while (line != null) {
				mess.append(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				is.close();
			} catch (Exception e) {

			}
		}

		
		return mess.toString();
	}
	/**
	 * 
	 * @param c
	 *       json 格式字符
	 * @return
	 *       转义字符
	 */
	private static char convert2ts(char c){
		char res = '\u0000';
		//" ★  { 卍  } 卐 [ ◎ ] ¤ : ℗ , ✿
		switch (c) {
		case '"':
			res = '★';
			break;
		case '{':
			res = '卍';
			break;
		case '}':
			res = '卐';
			break;
		case '[':
			res = '◎';
			break;
		case ']':
			res = '¤';
			break;
		case ':':
			res = '℗';
			break;
		case ',':
			res = '✿';
			break;

		default:
			res = c;
			break;
		}
		return res;
	}
	
	public static void smsRestore(final Activity context,final ProgressDialog pd) {
		class Data{
			int progress;
		}
		
		final Data dataProgress = new Data();
		System.out.println("smsRestore");
		File file = null;
		//文件 sd
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			//有sd卡
			//是否有剩余空间
			//获取sd剩余空间
			
				//正常
			file = new File(Environment.getExternalStorageDirectory(),"smses.json");
			// 文件不存
			
		} else {
			throw new RuntimeException("sd 卡挂载不成功，或者没有sd卡");
		}
		
		// 备份文件存在
		
		
		try {
			//1.获取json信息
			String smsJson = stream2String(new FileInputStream(file));
			//2 .class
			Gson gson = new Gson();
			final SmsJsonData jsonData = gson.fromJson(smsJson, SmsJsonData.class);
System.out.println(jsonData.smses.size() + "<<<<<<<<<<<<<<,");			
			//循环取短信 写短信
			final Uri uri = Uri.parse("content://sms");
			
			pd.setMax(jsonData.smses.size());
			pd.show();
			//子线程写短信
			Thread thread = new Thread(){
				public void run() {
					for (Sms sms : jsonData.smses) {
						ContentValues values = new ContentValues();
						values.put("address", sms.address);
						values.put("body", EncodeUtils.encode(convert2source(sms.body),MyConstaints.MUSIC));
						//values.put("date", Long.parseLong(sms.date));
						//values.put("type", Integer.parseInt(sms.type));
						
						//取一条写一条
						System.out.println(sms.address + "<<<<<<<<<<<<<<<<");
						System.out.println(EncodeUtils.encode(convert2source(sms.body),MyConstaints.MUSIC) + "<<<<<<<<<<<<<<<<<");
						System.out.println(sms.body + "<<<<<<<<<<<<<<<<");
						System.out.println(sms.date + "<<<<<<<<<<<<<<<<");
						System.out.println(sms.type + "<<<<<<<<<<<<<<<<");
						context.getContentResolver().insert(uri, values );
						System.out.println(sms.address + "<<<<<<<<<<<<<<<<");
						
						//备份进度
						dataProgress.progress++;
						context.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								pd.setProgress(dataProgress.progress);
							}
						});
					}//end for
					
					//关闭对话框
					context.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							pd.dismiss();
						}
					});
				};
			};
			thread.join();
			thread.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e + "******************8");
			throw new RuntimeException(e.getMessage());
		}
		
		
		
		
		
	}
	
	public class SmsJsonData{
		public List<Sms> smses;
		
		public class Sms{
			public String address;//	8888
			public String body;//	#*music*#
			public String date;//	1442219008301
			public String type;//	1
		}
	}
	
	public interface SmsBaikeRestoreListener{
		void show();
		void dismiss();
		void setMax(int max);
		void setProgress(int currentProgress);
	}
	
	public static void smsBaike(final Activity context,final SmsBaikeRestoreListener pd){
		class Data{
			int progress;
		}
		
		final Data dataProgress = new Data();
		File file = null;
		//文件 sd
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			//有sd卡
			//是否有剩余空间
			//获取sd剩余空间
			long freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
			if (freeSpace  < 1024 * 1024 * 5) {
				//小于5m
				throw new RuntimeException("sd 卡空间不足，请先删除信息释放空间");
			} else {
				//正常
				file = new File(Environment.getExternalStorageDirectory(),"smses.json");
			}
		} else {
			throw new RuntimeException("sd 卡挂载不成功，或者没有sd卡");
		}
		
		try {
			final PrintWriter out = new PrintWriter(file);
			out.println("{\"smses\":[");
			//短信数据库
			Uri uri = Uri.parse("content://sms");
			final Cursor cursor = context.getContentResolver().query(uri , new String[]{"address",  "date",  "body", "type"}, null, null, null);
			
			//多少条数据
			pd.setMax(cursor.getCount());
			pd.show();
			//子线程拷贝
			new Thread(){
				public void run() {
					String sms = null;
					//int number = 0;
					while (cursor.moveToNext()) {
						//游标循环一条数据 ，写一条数据
						//取一条短信{"address":"132333","date":"322143432432","body":"hello","type":"1"},
						//  " ★  { 卍  } 卐 [ ◎ ] ¤ : ℗ , ✿
						sms  = "{";
						sms += "\"address\":\"" + cursor.getString(0) + "\"";
						sms += ",\"date\":\"" + cursor.getString(1) + "\"";
						sms += ",\"body\":\"" + convert2ts(EncodeUtils.encode(cursor.getString(2),MyConstaints.MUSIC)) + "\"";
						sms += ",\"type\":\"" + cursor.getString(3) + "\"}"; 
						
						//判断是否是最后一条信息
						if (cursor.isLast()) {
							sms += "]}";
						} else {
							sms += ",";
						}
						
						
						//写到文件中
						out.println(sms);
						out.flush();
						
						
						//显示更新的进度
						dataProgress.progress++;
						context.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								pd.setProgress(dataProgress.progress);
							}
						});
						
					}
					//备份结束
					out.close();
					cursor.close();
					//关闭对话框
					context.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							pd.dismiss();
						}
					});
				};
			}.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// can't reach
			System.out.println("e:" + e);
			throw new RuntimeException(e.getMessage());
		}
		
		
	}
}
