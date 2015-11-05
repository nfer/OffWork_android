package com.nferzhuang.offwork;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {
	private static final String TAG = "AboutActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		setTitle(getString(R.string.about));
		setLeftImgBtn(R.drawable.topbar_back, new OnLButtonClickListener() {
			@Override
			public void onLeftBtnClick() {
				finish();
			}
		}, true);

		TextView aboutDetail2 = (TextView) findViewById(R.id.aboutDetail2);
		aboutDetail2.setMovementMethod(LinkMovementMethod.getInstance());

		String version = getAppVersionName(getApplicationContext());
		Log.d(TAG, "version:" + version);

		TextView versionView = (TextView) findViewById(R.id.version);
		versionView.setText(getString(R.string.version) + version);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Log.d(TAG, "action_home");
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception", e);
		}
		return versionName;
	}

}
