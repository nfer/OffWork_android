package com.nferzhuang.offwork;

import com.nferzhuang.offwork.utils.MyTime;
import com.nferzhuang.offwork.utils.WorkTime;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class SettingsActivity extends BaseActivity {
	private static final String TAG = "SettingsActivity";

	private Button onWorkStart;
	private Button onWorkEnd;
	private Button noonRestStart;
	private Button noonRestEnd;
	private Button offWorkStart;
	private Button offWorkEnd;
	private WorkTime workTime;

	private SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		settings = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		onWorkStart = (Button) findViewById(R.id.onWorkStart);
		onWorkEnd = (Button) findViewById(R.id.onWorkEnd);
		noonRestStart = (Button) findViewById(R.id.noonRestStart);
		noonRestEnd = (Button) findViewById(R.id.noonRestEnd);
		offWorkStart = (Button) findViewById(R.id.offWorkStart);
		offWorkEnd = (Button) findViewById(R.id.offWorkEnd);

		InitButton(onWorkStart, R.string.onWorkTimeStart,
				WorkTime.PREF_ONWORKSTART);
		InitButton(onWorkEnd, R.string.onWorkTimeEnd, WorkTime.PREF_ONWORKEND);
		InitButton(noonRestStart, R.string.noonRestTimeStart,
				WorkTime.PREF_NOONRESTSTART);
		InitButton(noonRestEnd, R.string.noonRestTimeEnd,
				WorkTime.PREF_NOONRESTEND);
		InitButton(offWorkStart, R.string.offWorkTimeStart,
				WorkTime.PREF_OFFWORKSTART);
		InitButton(offWorkEnd, R.string.offWorkTimeEnd,
				WorkTime.PREF_OFFWORKEND);

		setTitle(getString(R.string.settings));
		setLeftImgBtn(R.drawable.topbar_back, new OnLButtonClickListener() {
			@Override
			public void onLeftBtnClick() {
				if (workTime == null || !workTime.valid()) {
					Log.d(TAG, "WorkTime:" + workTime);
					showSettingNoSaveDialog();
					return;
				}
				workTime.save(getApplicationContext());
				finish();
			}
		}, true);
		setRightImgBtn(R.drawable.topbar_complete, listener, true);

		getWorkTime();
	}

	public void InitButton(Button btn, int stringId, String prefString) {
		InitButtonText(btn, stringId, prefString);
		InitButtonClick(btn);
	}

	public void InitButtonClick(final Button btn) {
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MyTime time = new MyTime(btn.getText().toString());

				new TimePickerDialog(SettingsActivity.this,
						new TimePickerDialog.OnTimeSetListener() {
					public void onTimeSet(TimePicker view, int hour,
							int minute) {
						btn.setText(String.format("%02d:%02d", hour, minute));
					}
				}, time.getHour(), time.getMinute(), true).show();
			}
		});
	}

	private void InitButtonText(Button btn, int stringId, String prefString) {
		String tempStr = settings.getString(prefString, getString(stringId));
		btn.setText(tempStr);
	}

	OnRButtonClickListener listener = new OnRButtonClickListener() {
		@Override
		public void onRightBtnClick() {
			getWorkTime();

			if (workTime.error() != WorkTime.WORKTIME_OK) {
				Log.w(TAG, "nfer workTime:" + workTime);
				showAlertDialog(workTime.error());
				return;
			}

			Log.d(TAG, "all configs are OK.");
			workTime.save(getApplicationContext());

			finish();
		}
	};

	private void showAlertDialog(int error) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				SettingsActivity.this);
		String errmsg = null;
		switch (error) {
		case WorkTime.WORKTIME_ONWORK_RANGE_ERROR:
			errmsg = getString(R.string.onwork_range_error);
			break;
		case WorkTime.WORKTIME_ONWORK_TIME_ERROR:
			errmsg = getString(R.string.onwork_time_error);
			break;
		case WorkTime.WORKTIME_NOONREST_RANGE_ERROR:
			errmsg = getString(R.string.noonrest_range_error);
			break;
		case WorkTime.WORKTIME_NOONREST_TIME_ERROR:
			errmsg = getString(R.string.noonrest_time_error);
			break;
		case WorkTime.WORKTIME_OFFWORK_RANGE_ERROR:
			errmsg = getString(R.string.offwork_range_error);
			break;
		case WorkTime.WORKTIME_ON_OFF_RANGE_DIFF_ERROR:
			errmsg = getString(R.string.on_off_range_diff_error);
			break;
		case WorkTime.WORKTIME_DURATION_ERROR:
			errmsg = getString(R.string.duration_error);
			break;
		case WorkTime.WORKTIME_NOT_FLEXIBLE_ERROR:
			errmsg = getString(R.string.not_flexible_error);
			break;
		default:
			break;
		}

		dialog.setTitle(getString(R.string.errtitle));
		dialog.setMessage(errmsg);

		dialog.setPositiveButton(getString(R.string.OK),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		dialog.create().show();
	}

	private void getWorkTime() {
		String onWorkStartStr = onWorkStart.getText().toString();
		String onWorkEndStr = onWorkEnd.getText().toString();
		String noonRestStartStr = noonRestStart.getText().toString();
		String noonRestEndStr = noonRestEnd.getText().toString();
		String offWorkStartStr = offWorkStart.getText().toString();
		String offWorkEndStr = offWorkEnd.getText().toString();

		workTime = new WorkTime(onWorkStartStr, onWorkEndStr, noonRestStartStr,
				noonRestEndStr, offWorkStartStr, offWorkEndStr);
	}

	private void showSettingNoSaveDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				SettingsActivity.this);

		dialog.setTitle(getString(R.string.errtitle));
		dialog.setMessage(R.string.setting_no_save);

		dialog.setPositiveButton(getString(R.string.OK),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		dialog.create().show();
	}
}
