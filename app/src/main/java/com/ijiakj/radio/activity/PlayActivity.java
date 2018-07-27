package com.ijiakj.radio.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ijiakj.radio.R;
import com.ijiakj.radio.base.BaseActivity;
import com.ijiakj.radio.framework.Constant;
import com.ijiakj.radio.framework.RadioApplication;
import com.ijiakj.radio.net.NetUtil;
import com.ijiakj.radio.utils.PreferenceUtil;
import com.ijiakj.radio.utils.ToolUtil;
import com.ijiakj.radio.utils.UIUtils;
import com.ijiakj.radio.widget.CircleImageView;
import com.ijiakj.radio.widget.VoiceListPopupWindow;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.track.CommonTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建者 曹自飞 创建时间 2016/9/6 0006 11:45 描述 播放页面
 * <p/>
 * 更新者 $Author$ 更新时间 $Date$ 更新描述 ${TODO}
 */
public class PlayActivity extends BaseActivity {
	private SeekBar mPlaySeekBar;
	private TextView mCurrentTime;
	private TextView mTotalTime;
	private ImageView mListIv;
	private ImageView mPrevIv;
	private ImageView mPlayOrPauseIv;
	private ImageView mNextIv;
	private ImageView mDescribeIv;
	private ImageView mTitleBackIv;
	private ImageView mTitleSerachIv;
	private TextView mTitleTv;

	private XmPlayerManager mXmPlayerManager;
	private boolean mUpdateProgress = true;
	private CircleImageView mIconBgIv;
	private TextView mVoiceName;
	private RelativeLayout mProgressOrTimeRl,mIconBgIvLay;
	private Album mAlbum;
	/**均匀旋转动画*/
	private ObjectAnimator refreshingAnimation;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_play;
	}

	@Override
	protected void initView() {
		mTitleBackIv = (ImageView) findViewById(R.id.title_back_iv);
		mIconBgIv = (CircleImageView) findViewById(R.id.icon_bg_iv);
		mIconBgIvLay = (RelativeLayout) findViewById(R.id.icon_bg_iv_lay);
		mTitleSerachIv = (ImageView) findViewById(R.id.title_serach_iv);
		mTitleTv = (TextView) findViewById(R.id.title_tv);
		mPlaySeekBar = (SeekBar) findViewById(R.id.play_seek_bar);
		mCurrentTime = (TextView) findViewById(R.id.current_time);
		mTotalTime = (TextView) findViewById(R.id.total_time);
		mListIv = (ImageView) findViewById(R.id.list_iv);
		mPrevIv = (ImageView) findViewById(R.id.prev_iv);
		mPlayOrPauseIv = (ImageView) findViewById(R.id.play_or_pause_iv);
		mNextIv = (ImageView) findViewById(R.id.next_iv);
		mDescribeIv = (ImageView) findViewById(R.id.describe_iv);
		mVoiceName = (TextView) findViewById(R.id.voice_name);
		mProgressOrTimeRl = (RelativeLayout) findViewById(R.id.progress_or_time_rl);
		mTitleSerachIv.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void initData() {
		RadioApplication.getInstance().addActivity(this);
		mVoiceName.setVisibility(View.GONE);
		mXmPlayerManager = XmPlayerManager.getInstance(this);
		refreshingAnimation = ObjectAnimator.ofFloat(mIconBgIvLay, "rotation", 0f, 360.0f);
		refreshingAnimation.setDuration(13000);
		refreshingAnimation.setInterpolator(new LinearInterpolator());//不停顿
		refreshingAnimation.setRepeatCount(-1);//设置动画重复次数
		refreshingAnimation.setRepeatMode(ValueAnimator.RESTART);//动画重复模式
		refreshingAnimation.setInterpolator(new LinearInterpolator());
	}

	private void playBannerVoice() {
		// 从应用map中取出id
		String otherId = (String) RadioApplication.getDeliverMap().get(
				Constant.DeliverMap.OTHERID);
		if (otherId != null) {
			playAlbum(otherId);
		}
	}

	private void playAlbum(String id) {
		// 获取声音列表id
		Map<String, String> map = new HashMap<String, String>();
		map.put(DTransferConstants.ALBUM_ID, id);
		map.put(DTransferConstants.SORT, "asc");
		// map.put(DTransferConstants.PAGE, mPage);
		CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
			@Override
			public void onSuccess(TrackList trackList) {
				Log.i("getHotTracks", trackList.getTracks().toString());

				mXmPlayerManager.playList(trackList, 0);
			}

			@Override
			public void onError(int i, String s) {

			}
		});
	}

	@Override
	protected void setListener() {
		mTitleBackIv.setOnClickListener(this);
		mPrevIv.setOnClickListener(this);
		mPlayOrPauseIv.setOnClickListener(this);
		mNextIv.setOnClickListener(this);
		mListIv.setOnClickListener(this);
		mDescribeIv.setOnClickListener(this);
		// 监听进度条的改变
		mPlaySeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
		// 监听播放状态
		mXmPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
	}

	@Override
	protected void processClick(View v) {
		if (v.getId()==R.id.title_back_iv) {
			moveTaskToBack(true);
			return;
		}else if (!NetUtil.isNetworkConnected(UIUtils.getContext())) {
			UIUtils.showToast(UIUtils.getContext(), "无网络连接,请检查后再重试!");
			return;
		}
		switch (v.getId()) {
		case R.id.prev_iv: // 上一曲
			mXmPlayerManager.playPre();
			break;
		case R.id.play_or_pause_iv: // 播放或暂停
			if (mXmPlayerManager.isPlaying()) {
				mXmPlayerManager.pause();
			} else {
				mXmPlayerManager.play();
			}
			break;
		case R.id.next_iv: // 下一曲
			mXmPlayerManager.playNext();
			break;
		case R.id.list_iv: // 声音列表
			// 1.弹出列表框
			// 2.显示数据
			// 实例化SelectPicPopupWindow
			CommonTrackList commonTrackList = mXmPlayerManager
					.getCommonTrackList();
			List tracks = commonTrackList.getTracks();
			VoiceListPopupWindow popupWindow = new VoiceListPopupWindow(
					PlayActivity.this, tracks);
			// 显示窗口
			popupWindow.showAtLocation(
					PlayActivity.this.findViewById(R.id.play_activity_root),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
			break;
		case R.id.describe_iv: // 描述
			Intent intent = new Intent(PlayActivity.this,
					DescribeActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	/**
	 * 进度条的监听
	 */
	private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mXmPlayerManager.seekToByPercent(seekBar.getProgress()
					/ (float) seekBar.getMax());
			mUpdateProgress = true;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mUpdateProgress = false;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (seekBar.isFocused()) {
				mXmPlayerManager.seekToByPercent(seekBar.getProgress()
						/ (float) seekBar.getMax());
				mUpdateProgress = false;
			}

		}

	};

	/**
	 * 播放状态的监听
	 */
	private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {
		@SuppressLint("NewApi")
		@Override
		public void onPlayStart() {
			mPlayOrPauseIv.setImageResource(R.drawable.selector_pause_button);
			if (refreshingAnimation.isStarted()) {
				refreshingAnimation.resume();
			}else {
				refreshingAnimation.start();
			}
			// 每当开始播放一个声音的的时候,把声音保存到sp里.
			int currPlayType = mXmPlayerManager.getCurrPlayType();
			Gson gson = new Gson();
			switch (currPlayType) {
			// 专辑数据存入sp
			case XmPlayListControl.PLAY_SOURCE_TRACK: {
				List<Track> playList = mXmPlayerManager.getPlayList();
				String jsonSound = gson.toJson(playList);
				PreferenceUtil.setLastPlay(PlayActivity.this, jsonSound, 1,
						mXmPlayerManager.getCurrentIndex());
				if (mAlbum != null) {
					PreferenceUtil.setCurrAlbum(UIUtils.getContext(), mAlbum);
				}
			}
				break;
			// 广播数据存入sp
			case XmPlayListControl.PLAY_SOURCE_RADIO: {
				PlayableModel currSound = mXmPlayerManager.getCurrSound();
				String jsonSound = gson.toJson(currSound);
				PreferenceUtil.setLastPlay(PlayActivity.this, jsonSound, 2, 0);
				PreferenceUtil.setCurrAlbum(UIUtils.getContext(), null);
			}
				break;
			}
		}

		@SuppressLint("NewApi")
		@Override
		public void onPlayPause() {
			mPlayOrPauseIv.setImageResource(R.drawable.selector_play_button);
			refreshingAnimation.pause();
		}

		@SuppressLint("NewApi")
		@Override
		public void onPlayStop() {
			mPlayOrPauseIv.setImageResource(R.drawable.selector_play_button);
			refreshingAnimation.pause();
		}

		@Override
		public void onSoundPlayComplete() {
		}

		@Override
		public void onSoundPrepared() {
			mPlaySeekBar.setEnabled(true);
		}

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@Override
		public void onSoundSwitch(PlayableModel playableModel,
				PlayableModel playableModel1) {
			updatePlayPage();
			// updateButtonStatus();
		}

		@Override
		public void onBufferingStart() {
		}

		@Override
		public void onBufferingStop() {
		}

		@Override
		public void onBufferProgress(int i) {
		}

		@Override
		public void onPlayProgress(int currPos, int duration) {
			mCurrentTime.setText(ToolUtil.formatTime(currPos));
			mTotalTime.setText(ToolUtil.formatTime(duration));
			if (!mPlaySeekBar.isFocused()) { // 当没有获取焦点的时候允许播放进度更新界面进度!
				mUpdateProgress = true;
			}
			if (mUpdateProgress && duration != 0) {
				mPlaySeekBar
						.setProgress((int) (100 * currPos / (float) duration));
			}
		}

		@Override
		public boolean onError(XmPlayerException e) {
			return false;
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		// 根据点击广告带过来的id播放声音
		if (Constant.IS_BANNER_PLAYE) {
			playBannerVoice();
			Constant.IS_BANNER_PLAYE = false;
		}

		if (Constant.IS_REPLAY) {
			Map<String, Object> deliverMap = RadioApplication.getDeliverMap();
			Object o = deliverMap.get(Constant.DeliverMap.PLAY_KEY);
			if (o instanceof Album) {
				// 播放专辑
				mAlbum = (Album) o;
				playAlbum(String.valueOf(mAlbum.getId()));
				mTitleTv.setText(mAlbum.getAlbumTitle());
			}
			if (o instanceof Radio) {
				// 播放广播
				Radio radio = (Radio) o;
				mXmPlayerManager.playRadio(radio);
				mTitleTv.setText(radio.getRadioName());
			}
			Constant.IS_REPLAY = false;
		}
		// 跑马灯请求焦点
		// mVoiceName.requestFocus();

		// 更新播放界面
		updatePlayPage();
	}
	@SuppressLint("NewApi")
	private void updatePlayPage() {

		getCurPlayData();

		if (!TextUtils.isEmpty(mTitle)) {
			mTitleTv.setText(mTitle);
		}

		// XmPlayListControl.PLAY_SOURCE_NONE = 1 // 当前没有声音;
		// XmPlayListControl.PLAY_SOURCE_TRACK = 2 // 点播类型;
		// XmPlayListControl.PLAY_SOURCE_RADIO = 3;// 直播类型
		int currPlayType = mXmPlayerManager.getCurrPlayType();
		if (XmPlayListControl.PLAY_SOURCE_RADIO == currPlayType) {
			mProgressOrTimeRl.setVisibility(View.INVISIBLE);
			// 直播不需要进度条,所以隐藏!
			mPlaySeekBar.setVisibility(View.INVISIBLE);
			mCurrentTime.setVisibility(View.INVISIBLE);
			mTotalTime.setVisibility(View.INVISIBLE);
		} else {
			mProgressOrTimeRl.setVisibility(View.VISIBLE);
			mPlaySeekBar.setVisibility(View.VISIBLE);
			mCurrentTime.setVisibility(View.VISIBLE);
			mTotalTime.setVisibility(View.VISIBLE);
		}

		if (mXmPlayerManager.isPlaying()) {
			mPlayOrPauseIv.setImageResource(R.drawable.selector_pause_button);
			if (refreshingAnimation.isStarted()) {
				refreshingAnimation.resume();
			}else {
				refreshingAnimation.start();
			}
		} else {
			mPlayOrPauseIv.setImageResource(R.drawable.selector_play_button);
			refreshingAnimation.pause();
		}

		if (!TextUtils.isEmpty(mCoverUrl)) {
			Picasso.with(PlayActivity.this).load(mCoverUrl)
					.placeholder(R.drawable.default_ic)
					.error(R.drawable.default_ic).into(mIconBgIv);
		} else {
			mIconBgIv.setImageResource(R.drawable.default_ic);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);// true对任何Activity都适用
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
