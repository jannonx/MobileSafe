package com.itheima.mobilesafe13.domain;

public class NumberAndName {
	private String number;//服务号码
	private String name;//服务名字
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "NumberAndName [number=" + number + ", name=" + name + "]";
	}
}
