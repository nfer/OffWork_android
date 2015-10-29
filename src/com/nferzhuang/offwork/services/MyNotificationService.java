package com.nferzhuang.offwork.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.nferzhuang.offwork.OffWorkActivity;
import com.nferzhuang.offwork.R;

public class MyNotificationService extends IntentService {
	private static final String TAG = "MyNotificationService";
	public static final int NOTIF_ID = 56;

	public MyNotificationService() {
		super(TAG);
	}

	// This describes what will happen when service is triggered
	@Override
	protected void onHandleIntent(Intent intent) {
		// Let's also create notification
		createNotification();
	}

	// Construct compatible notification
	private void createNotification() {
		// Construct pending intent to serve as action for notification item
		Intent intent = new Intent(this, OffWorkActivity.class);
		intent.putExtra("message", getString(R.string.goHome));
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// Create notification
		Notification noti = new NotificationCompat.Builder(this)
				.setAutoCancel(true)
				.setContentTitle(getString(R.string.goHomeTitle))
				.setContentText(getString(R.string.goHome))
				.setSmallIcon(R.drawable.icon)
				.setContentIntent(pIntent)
				.build();

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(
				Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(NOTIF_ID, noti);
	}
}