package com.nferzhuang.offwork;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import com.nferzhuang.offwork.receivers.MyAlarmReceiver;
import com.nferzhuang.offwork.utils.MyTime;
import com.nferzhuang.offwork.utils.Utils;
import com.nferzhuang.offwork.utils.WorkTime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OffWorkActivity extends BaseActivity
		implements View.OnClickListener {
	private static final String TAG = "OffWorkActivity";

	private Button signIn;
	private TextView signInTimeTips;
	private TextView offWorkTimeTips;
	private TextView offWorkLeftTips;
	private SharedPreferences preferences;
	private String today;
	private String signInTimeStr;
	private LinearLayout offWorkLeftLayout;
	private LinearLayout signInLayout;
	private WorkTime workTime;
	private MyTime signInTime;
	private MyTime offWorkTime;
	private Timer timer;
	private TimerTask task;
	private Handler handler;

	private PendingIntent alarmPendingIntent = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offwork);

		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		signInTimeTips = (TextView) findViewById(R.id.signInTimeTips);
		offWorkTimeTips = (TextView) findViewById(R.id.offWorkTimeTips);
		offWorkLeftTips = (TextView) findViewById(R.id.offWorkLeftTips);

		signIn = (Button) findViewById(R.id.signIn);
		signIn.setOnClickListener(this);

		offWorkLeftLayout = (LinearLayout) findViewById(R.id.offWorkLeftLayout);
		signInLayout = (LinearLayout) findViewById(R.id.signInLayout);

		getOverflowMenu();

		setTitle(getString(R.string.app_name));
	}

	public void onResume() {
		super.onResume();

		timer = new Timer();
		handler = new MyHandler(this) {
			@Override
			public void handleMessage(Message msg) {
				calcWorkLeftTime();
				super.handleMessage(msg);
			}
		};

		task = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		};

		today = Utils.getTodayString();
		signInTimeStr = preferences.getString(today, null);
		if (signInTimeStr == null) {
			initSignIn();
		} else {
			initOffWorkLeft();
		}
	}

	public void onPause() {
		super.onPause();

		timer.cancel();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settingsactionbar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startSettings();
			return true;
		case R.id.action_about:
			startAbout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void getOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initSignIn() {
		offWorkLeftLayout.setVisibility(View.GONE);
		signInLayout.setVisibility(View.VISIBLE);
	}

	public void initOffWorkLeft() {
		offWorkLeftLayout.setVisibility(View.VISIBLE);
		signInLayout.setVisibility(View.GONE);

		// get workTime from context(Preference)
		workTime = new WorkTime(getApplicationContext());
		if (!workTime.valid()) {
			Log.d(TAG, "WorkTime:" + workTime);
			startSettings();
			return;
		}

		// get signInTime
		signInTime = new MyTime(signInTimeStr);
		Log.d(TAG, "signInTime:" + signInTime);

		// get offWorkTime
		int time = signInTime.compare(workTime.getOnWorkStartTime());
		if (time < 0) {
			offWorkTime = workTime.getOffWorkStartTime();
		} else {
			offWorkTime = workTime.getOffWorkStartTime().add(time);
		}
		Log.d(TAG, "offWorkTime:" + offWorkTime);

		signInTimeTips.setText(getString(R.string.signInTime) + signInTime);
		offWorkTimeTips.setText(getString(R.string.offWorkTime) + offWorkTime);

		timer.schedule(task, 0, 1000);
	}

	public void calcWorkLeftTime() {
		MyTime now = Utils.getNowTime();
		Log.d(TAG, "now:" + now);
		if (now.compare(offWorkTime) >= 0) {
			offWorkLeftTips.setText(R.string.goHome);
			timer.cancel();
			return;
		}

		if (alarmPendingIntent == null) {
			startAlarm(offWorkTime.compare(now));
		}

		MyTime noonRestStartTime = workTime.getNoonRestStartTime();
		MyTime noonRestEndTime = workTime.getNoonRestEndTime();

		int workLeftSecond = 0;
		if (now.compare(noonRestStartTime) < 0) {
			Log.d(TAG,
					"now is less than noonRestStartTime:" + noonRestStartTime);
			int noonRestDuration = workTime.getNoonRestDuration();
			Log.d(TAG, "noonRestDuration:" + noonRestDuration);
			workLeftSecond = offWorkTime.compare(now) - noonRestDuration;
		} else if (now.compare(noonRestEndTime) < 0) {
			Log.d(TAG, "now is less than noonRestEndTime:" + noonRestEndTime);
			workLeftSecond = offWorkTime.compare(noonRestEndTime);
		} else {
			Log.d(TAG, "now is bigger than noonRestEndTime:" + noonRestEndTime);
			workLeftSecond = offWorkTime.compare(now);
		}

		MyTime workLeftTime = new MyTime(workLeftSecond);
		Log.d(TAG, "workLeftTime:" + workLeftTime);
		String tips = getString(R.string.offWorkLeft);
		tips += workLeftTime;
		offWorkLeftTips.setText(tips);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.signIn: {
			MyTime myTime = Utils.getNowTime();
			signInTimeStr = myTime.toString();
			Log.d(TAG, "signIn at:" + signInTimeStr);

			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(today, signInTimeStr);
			editor.commit();
			String configured = preferences.getString(WorkTime.PREF_CONFIGURED,
					null);
			if (configured != null) {
				initOffWorkLeft();
			} else {
				startSettings();
			}
		}
			break;
		default:
			break;
		}
	}

	public void startSettings() {
		Intent intent = new Intent(OffWorkActivity.this,
				SettingsActivity.class);
		startActivity(intent);
	}

	public void startAbout() {
		Intent intent = new Intent(OffWorkActivity.this, AboutActivity.class);
		startActivity(intent);
	}

	static class MyHandler extends Handler {
		WeakReference<OffWorkActivity> mActivity;

		MyHandler(OffWorkActivity activity) {
			mActivity = new WeakReference<OffWorkActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	public void startAlarm(int seconds) {
		// Construct an intent that will execute the AlarmReceiver
		Intent intent = new Intent(getApplicationContext(),
				MyAlarmReceiver.class);

		// Create a PendingIntent to be triggered when the alarm goes off
		alarmPendingIntent = PendingIntent.getBroadcast(this,
				MyAlarmReceiver.REQUEST_CODE, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		long triggerAtMillis = System.currentTimeMillis() + seconds * 1000;
		AlarmManager alarm = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, alarmPendingIntent);
		Log.d(TAG, "set alarm at " + triggerAtMillis + " ms later.");
	}
}
