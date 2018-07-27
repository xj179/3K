package com.ijiakj.radio.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ijiakj.radio.R;
import com.ijiakj.radio.framework.SystemBarTintManager;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

/**
 * 创建者 曹自飞 创建时间 2016/8/30 0030 10:29 描述 简单公共性的抽取
 * <p/>
 * 更新者 $Author$ 更新时间 $Date$ 更新描述 ${TODO}
 */
public abstract class BaseActivity extends FragmentActivity implements
		View.OnClickListener {
	private static final String TAG = "BaseActivity";
	public XmPlayerManager mXmPlayerManager;
	protected String mTitle;
	protected String mCoverUrl;
	protected String mMsg;
	private String mCoverSmall;
	public Context mContext;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintResource(R.color.title_color);// 通知栏所需颜色
		}
		if (mXmPlayerManager == null) {
			mXmPlayerManager = XmPlayerManager.getInstance(this);
		}
		mContext = this;
		setContentView(getLayoutId());
		initView();
		initData();
		setListener();
	}

	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	protected abstract int getLayoutId();

	protected abstract void initView();

	protected abstract void initData();

	protected abstract void setListener();

	/**
	 * 公共点击事件
	 *
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.back) {
			finish();
		} else {
			processClick(v);
		}
	}

	/**
	 * 处理公共点击事件以外的事情
	 *
	 * @param v
	 */
	protected abstract void processClick(View v);

	/**
	 * 更新底部播放状态
	 *
	 * @param bottomPlayIcon
	 * @param bottomPlayTv
	 * @param bottomPlayFluctuate
	 */
	public void updateBottomPlayPage(ImageView bottomPlayIcon,
			TextView bottomPlayTv, ImageView bottomPlayFluctuate) {
		Animation operatingAnim = AnimationUtils.loadAnimation(this,
				R.anim.rotate);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		// 获取当前播放声音的数据
		getCurPlayData();
		if (!TextUtils.isEmpty(mCoverSmall)) {
			Picasso.with(this).load(mCoverSmall)
					.placeholder(R.drawable.min_default)
					.error(R.drawable.min_default).into(bottomPlayIcon);
		}
		// 波动动画
		bottomPlayFluctuate.setImageResource(R.drawable.animation1);
		AnimationDrawable animationDrawable = (AnimationDrawable) bottomPlayFluctuate
				.getDrawable();
		if (mXmPlayerManager.isPlaying()) {
			bottomPlayIcon.startAnimation(operatingAnim);
			animationDrawable.start();
		} else {
			animationDrawable.stop();
			bottomPlayIcon.clearAnimation();
		}
		if (mTitle != null) {
			bottomPlayTv.setText(mTitle);
		}

	}

	/**
	 * 获取当前播放声音数据
	 */
	public void getCurPlayData() {

		PlayableModel model = mXmPlayerManager.getCurrSound();

		if (model != null) {
			mTitle = null;
			mMsg = null;
			mCoverUrl = null;
			mCoverSmall = null;
			if (model instanceof Track) {
				Track info = (Track) model;
				mTitle = info.getTrackTitle();
				mMsg = info.getAnnouncer() == null ? "未知" : info.getAnnouncer()
						.getNickname();
				mCoverUrl = info.getCoverUrlLarge();
				mCoverSmall = info.getCoverUrlMiddle();
			} else if (model instanceof Schedule) {
				Schedule program = (Schedule) model;
				mMsg = program.getRelatedProgram().getProgramName();
				mTitle = program.getRelatedProgram().getProgramName();
				mCoverUrl = program.getRelatedProgram().getBackPicUrl();
				mCoverSmall = program.getRelatedProgram().getBackPicUrl();
			} else if (model instanceof Radio) {
				Radio radio = (Radio) model;
				mTitle = radio.getRadioName();
				mMsg = radio.getRadioDesc();
				mCoverUrl = radio.getCoverUrlLarge();
				mCoverSmall = radio.getCoverUrlSmall();
			}

		}
	}

	private Toast mToast;

	public void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}
}
