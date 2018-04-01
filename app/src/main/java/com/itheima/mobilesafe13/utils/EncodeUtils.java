package com.itheima.mobilesafe13.utils;

import java.io.UnsupportedEncodingException;

/**
 * @author Administrator
 * @desc  字符串加密算法
 * @date 2015-9-21
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-21 08:57:12 +0800 (Mon, 21 Sep 2015) $
 * @Id $Id: EncodeUtils.java 71 2015-09-21 00:57:12Z goudan $
 * @Rev $Rev: 71 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/utils/EncodeUtils.java $
 *
 */
public class EncodeUtils {
	/**
	 * @param str
	 *        原字符串
	 * @return
	 *        加密后的字符串
	 */
	public static  String encode(String str,byte seed) {
		try {
			byte[] bytes = str.getBytes("gbk");
			for (int i = 0; i < bytes.length; i++){
				bytes[i] ^= seed;
			}
			return new String(bytes,"gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
