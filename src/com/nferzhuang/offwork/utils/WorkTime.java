package com.nferzhuang.offwork.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class WorkTime {
	public static final int WORKTIME_OK = 0;
	// onWorkEnd - onWorkStart
	public static final int WORKTIME_ONWORK_RANGE_ERROR = 1;
	// noonRestStart - onWorkEnd
	public static final int WORKTIME_ONWORK_TIME_ERROR = 2;
	// noonRestEnd - noonRestStart
	public static final int WORKTIME_NOONREST_RANGE_ERROR = 3;
	// offWorkStart - noonRestEnd
	public static final int WORKTIME_NOONREST_TIME_ERROR = 4;
	// offWorkEnd - offWorkStart
	public static final int WORKTIME_OFFWORK_RANGE_ERROR = 5;
	// (onWorkEnd - onWorkStart) == (offWorkEnd - offWorkStart)
	public static final int WORKTIME_ON_OFF_RANGE_DIFF_ERROR = 6;
	// offWorkStart - onWorkStart - (noonRestEnd - noonRestStart) != 8
	public static final int WORKTIME_DURATION_ERROR = 7;
	// onWorkEnd == onWorkStart
	public static final int WORKTIME_NOT_FLEXIBLE_ERROR = 8;

	public static final String PREF_ONWORKSTART = "onWorkStart";
	public static final String PREF_ONWORKEND = "onWorkEnd";
	public static final String PREF_NOONRESTSTART = "noonRestStart";
	public static final String PREF_NOONRESTEND = "noonRestEnd";
	public static final String PREF_OFFWORKSTART = "offWorkStart";
	public static final String PREF_OFFWORKEND = "offWorkEnd";
	public static final String PREF_CONFIGURED = "configured";

	private MyTime onWorkStartTime;
	private MyTime onWorkEndTime;
	private MyTime noonRestStartTime;
	private MyTime noonRestEndTime;
	private MyTime offWorkStartTime;
	private MyTime offWorkEndTime;

	public WorkTime(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		onWorkStartTime = new MyTime(
				preferences.getString(PREF_ONWORKSTART, null));
		onWorkEndTime = new MyTime(preferences.getString(PREF_ONWORKEND, null));
		noonRestStartTime = new MyTime(
				preferences.getString(PREF_NOONRESTSTART, null));
		noonRestEndTime = new MyTime(
				preferences.getString(PREF_NOONRESTEND, null));
		offWorkStartTime = new MyTime(
				preferences.getString(PREF_OFFWORKSTART, null));
		offWorkEndTime = new MyTime(
				preferences.getString(PREF_OFFWORKEND, null));
	}

	public WorkTime(String onWorkStartStr, String onWorkEndStr,
			String noonRestStartStr, String noonRestEndStr,
			String offWorkStartStr, String offWorkEndStr) {
		onWorkStartTime = new MyTime(onWorkStartStr);
		onWorkEndTime = new MyTime(onWorkEndStr);
		noonRestStartTime = new MyTime(noonRestStartStr);
		noonRestEndTime = new MyTime(noonRestEndStr);
		offWorkStartTime = new MyTime(offWorkStartStr);
		offWorkEndTime = new MyTime(offWorkEndStr);
	}

	public String toString() {
		return onWorkStartTime.toString() + "," + onWorkEndTime.toString() + ","
				+ noonRestStartTime.toString() + ","
				+ noonRestEndTime.toString() + "," + offWorkStartTime.toString()
				+ "," + offWorkEndTime.toString();
	}

	public boolean valid() {
		return error() == WORKTIME_OK;
	}

	public int error() {
		if (onWorkEndTime.compare(onWorkStartTime) < 0)
			return WORKTIME_ONWORK_RANGE_ERROR;
		else if (noonRestStartTime.compare(onWorkEndTime) < 0)
			return WORKTIME_ONWORK_TIME_ERROR;
		else if (noonRestEndTime.compare(noonRestStartTime) < 0)
			return WORKTIME_NOONREST_RANGE_ERROR;
		else if (offWorkStartTime.compare(noonRestEndTime) < 0)
			return WORKTIME_NOONREST_TIME_ERROR;
		else if (offWorkEndTime.compare(offWorkStartTime) < 0)
			return WORKTIME_OFFWORK_RANGE_ERROR;
		else if (offWorkEndTime.compare(offWorkStartTime) != onWorkEndTime
				.compare(onWorkStartTime))
			return WORKTIME_ON_OFF_RANGE_DIFF_ERROR;
		else if (offWorkStartTime.compare(onWorkStartTime)
				- noonRestEndTime.compare(noonRestStartTime) != 8 * 60 * 60)
			return WORKTIME_DURATION_ERROR;
		else if (onWorkEndTime.compare(onWorkStartTime) == 0)
			return WORKTIME_NOT_FLEXIBLE_ERROR;
		else
			return WORKTIME_OK;
	}

	public MyTime getOnWorkStartTime() {
		return onWorkStartTime;
	}

	public MyTime getOnWorkEndTime() {
		return onWorkEndTime;
	}

	public MyTime getNoonRestStartTime() {
		return noonRestStartTime;
	}

	public MyTime getNoonRestEndTime() {
		return noonRestEndTime;
	}

	public MyTime getOffWorkStartTime() {
		return offWorkStartTime;
	}

	public MyTime getOffWorkEndTime() {
		return offWorkEndTime;
	}

	public int getNoonRestDuration() {
		return noonRestEndTime.compare(noonRestStartTime);
	}
}
