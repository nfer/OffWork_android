package com.nferzhuang.offwork.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
	public static String getTodayString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		return sdf.format(new Date());
	}

	public static MyTime getNowTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
		Date curDate = new Date();
		return new MyTime(sdf.format(curDate));
	}
}
