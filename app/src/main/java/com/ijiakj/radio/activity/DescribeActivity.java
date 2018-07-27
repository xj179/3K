package com.ijiakj.radio.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ijiakj.radio.R;
import com.ijiakj.radio.base.BaseActivity;
import com.ijiakj.radio.framework.RadioApplication;
import com.ijiakj.radio.widget.ObservableScrollView;
import com.ijiakj.radio.widget.ObservableScrollView.OnScrollBottomListener;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.track.Track;

public class DescribeActivity extends BaseActivity {

	private TextView mDescribeNameTv;
	private TextView mDescribeCloseBtn;
	private TextView mDescribeAnchorTv;
	private TextView mDescribeCountTv;
	private TextView mDescribeCategoryTv;
	private TextView mDescribeContentTv;

	private ObservableScrollView describe_scroll;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_describe;
	}

	@Override
	protected void initView() {
		mDescribeNameTv = (TextView) findViewById(R.id.describe_name_tv);
		mDescribeAnchorTv = (TextView) findViewById(R.id.describe_Anchor_tv);
		mDescribeCountTv = (TextView) findViewById(R.id.describe_count_tv);
		mDescribeCategoryTv = (TextView) findViewById(R.id.describe_category_tv);
		mDescribeContentTv = (TextView) findViewById(R.id.describe_content_tv);
		mDescribeCloseBtn = (TextView) findViewById(R.id.describe_close_btn);
		describe_scroll = (ObservableScrollView) findViewById(R.id.describe_scroll);
	}

	@Override
	protected void initData() {
		RadioApplication.getInstance().addActivity(this);
		PlayableModel currSound = mXmPlayerManager.getCurrSound();
		if (currSound instanceof Track) {
			Track track = (Track) currSound;
			mDescribeNameTv.setText("名称 : " + track.getTrackTitle());
			mDescribeAnchorTv.setText("主播 : "
					+ (TextUtils.isEmpty(track.getAnnouncer().getNickname()) || track.getAnnouncer().getNickname().equals("null") ? "未知主播"
							: track.getAnnouncer().getNickname()));
			mDescribeCountTv.setText("播放 : " + String.valueOf(track.getPlayCount()) + " 次");
			mDescribeCategoryTv.setText("分类 : 专辑");
			String str = "暂无";
			if (!TextUtils.isEmpty(track.getTrackIntro()) && !track.getTrackIntro().equals("null")) {
				str = track.getTrackIntro();
			} else if (!TextUtils.isEmpty(track.getRadioName()) && !track.getRadioName().equals("null")) {
				str = track.getRadioName();
			}
			mDescribeContentTv.setText("内容简介 : " + str);
		}
		if (currSound instanceof Radio) {
			Radio radio = (Radio) currSound;
			mDescribeNameTv.setText("名称 : " + radio.getRadioName());
			mDescribeAnchorTv
					.setText("主播 : "
							+ (TextUtils.isEmpty(radio.getProgramName()) || radio.getProgramName().equals("null") ? "未知主播" : radio
									.getProgramName()));
			mDescribeCountTv.setText("播放 : " + String.valueOf(radio.getRadioPlayCount()) + " 次");
			mDescribeCategoryTv.setText("分类 : 广播");
			String str = "暂无";
			if (!TextUtils.isEmpty(radio.getRadioDesc()) && !radio.getRadioDesc().equals("null")) {
				str = radio.getRadioDesc();
			} else if (!TextUtils.isEmpty(radio.getRadioName()) && !radio.getRadioName().equals("null")) {
				str = radio.getRadioName();
			}
			mDescribeContentTv.setText("内容简介 : " + str);
		}
	}

	@Override
	protected void setListener() {
		mDescribeCloseBtn.setOnClickListener(this);
		describe_scroll.registerOnScrollViewScrollToBottom(new OnScrollBottomListener() {

			@Override
			public void srollToBottom() {
				mDescribeCloseBtn.requestFocus();
			}
		});
	}

	@Override
	protected void processClick(View v) {
		switch (v.getId()) {
		case R.id.describe_close_btn:
			rollback();
			break;
		}
	}

	/**
	 * 回退到播放页面
	 */
	private void rollback() {
		finish();
		Intent intent = new Intent(DescribeActivity.this, PlayActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			rollback();
			break;

		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}
}
