package com.nferzhuang.offwork.utils;

import java.util.Locale;

public class MyTime {
	private int hour = 0;
	private int minute = 0;
	private int second = 0;

	public MyTime(String timeStr) {
		if (timeStr == null)
			return;

		switch (timeStr.length()) {
		case 2:
			this.hour = Integer.parseInt(timeStr);
			break;
		case 5:
			this.hour = Integer.parseInt(timeStr.substring(0, 2));
			this.minute = Integer.parseInt(timeStr.substring(3, 5));
			break;
		case 8:
			this.hour = Integer.parseInt(timeStr.substring(0, 2));
			this.minute = Integer.parseInt(timeStr.substring(3, 5));
			this.second = Integer.parseInt(timeStr.substring(6, 8));
			break;
		default:
			break;
		}
	}

	public MyTime(int second) {
		this.hour = second / 3600;
		this.minute = (second - this.hour *3600) / 60;
		this.second = second - this.hour *3600 - this.minute *60;
	}

	public String toString() {
		return String.format(Locale.CHINA, "%02d:%02d:%02d", this.hour, this.minute,
				this.second);
	}

	public int value() {
		return this.hour * 3600 + this.minute *60 + this.second;
	}

	public MyTime add(int second) {
		return new MyTime(this.value() + second);
	}

	public int compare(MyTime time) {
		return (this.hour - time.hour) * 3600 + (this.minute - time.minute)
				* 60 + (this.second - time.second);
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}
}
