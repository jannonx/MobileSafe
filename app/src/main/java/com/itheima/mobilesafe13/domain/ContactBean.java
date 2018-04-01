package com.itheima.mobilesafe13.domain;

public class ContactBean {
	private String name;//名字
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	private String phone;//电话
	@Override
	public String toString() {
		return "ContactBean [name=" + name + ", phone=" + phone + "]";
	}
	
}
