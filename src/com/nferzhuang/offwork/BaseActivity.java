package com.nferzhuang.offwork;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public abstract class BaseActivity extends Activity {
	private static final String TAG = "BaseActivity";

	private TextView mTitleTextView;
	private ImageButton mRightImageBtn;
	private ImageButton immLeftImageBtngBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);

		View actionbarLayout = LayoutInflater.from(this)
				.inflate(R.layout.custom_actionbar, null, false);
		getActionBar().setCustomView(actionbarLayout);

		mTitleTextView = (TextView) (actionbarLayout
				.findViewById(R.id.middle_title));
		mTitleTextView.setText(R.string.app_name);

		immLeftImageBtngBack = (ImageButton) (actionbarLayout
				.findViewById(R.id.left_imbt));

		mRightImageBtn = (ImageButton) (actionbarLayout
				.findViewById(R.id.right_imbt));
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onDestroy();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	protected void setTitle(final String title) {
		mTitleTextView.setText(title);
	}

	protected void setLeftImgBtn(final int resBgID,
			final OnLButtonClickListener listener, final boolean visible) {
		if (visible) {
			immLeftImageBtngBack.setBackgroundResource(resBgID);
			immLeftImageBtngBack.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (listener != null) {
						listener.onLeftBtnClick();
					}
				}
			});
			immLeftImageBtngBack.setVisibility(View.VISIBLE);
		} else
			immLeftImageBtngBack.setVisibility(View.INVISIBLE);
	}

	protected void setRightImgBtn(final int resBgID,
			final OnRButtonClickListener listener, final boolean visible) {
		if (visible) {
			mRightImageBtn.setBackgroundResource(resBgID);
			mRightImageBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (listener != null) {
						listener.onRightBtnClick();
					}
				}
			});
			mRightImageBtn.setVisibility(View.VISIBLE);
		} else
			mRightImageBtn.setVisibility(View.GONE);
	}

	protected void setActionBarVisible(final boolean visible) {
		if (visible)
			getActionBar().show();
		else
			getActionBar().hide();
	}

	public interface OnLButtonClickListener {
		public void onLeftBtnClick();
	}

	public interface OnRButtonClickListener {
		public void onRightBtnClick();
	}

}
