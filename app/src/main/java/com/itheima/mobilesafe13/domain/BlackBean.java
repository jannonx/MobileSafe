package com.itheima.mobilesafe13.domain;

/**
 * @author Administrator
 * @comp 黑马程序员
 * @date 2015-9-16
 * @desc 黑名单数据的封装类

 * @version $Rev: 31 $
 * @author $Author: goudan $
 * @Date  $Date: 2015-09-16 10:30:38 +0800 (Wed, 16 Sep 2015) $
 * @Id    $Id: BlackBean.java 31 2015-09-16 02:30:38Z goudan $
 * @Url   $URL: https://188.188.4.100/svn/mobilesafeserver/trunk/MobileSafe13/src/com/itheima/mobilesafe13/domain/BlackBean.java $
 * 
 */
public class BlackBean {
	private String phone;
	@Override
	public String toString() {
		return "BlackBean [phone=" + phone + ", mode=" + mode + "]";
	}
	private int mode;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
}
