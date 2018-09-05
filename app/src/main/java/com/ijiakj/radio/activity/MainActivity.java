package com.ijiakj.radio.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ijiakj.radio.R;
import com.ijiakj.radio.adapter.MainFragmentAdapter;
import com.ijiakj.radio.base.BaseActivity;
import com.ijiakj.radio.fragment.BroadcastFragment;
import com.ijiakj.radio.fragment.CategoryFragment;
import com.ijiakj.radio.framework.Constant;
import com.ijiakj.radio.framework.RadioApplication;
import com.ijiakj.radio.update.CheckUpdateSyncTask;
import com.ijiakj.radio.utils.LocationUtil;
import com.ijiakj.radio.utils.PreferenceUtil;
import com.ijiakj.radio.utils.UIUtils;
import com.ijiakj.radio.widget.CircleImageView;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements
		ViewPager.OnPageChangeListener {

	private TextView mMainCategoryTv;
	private TextView mMainBroadcastTv;
	private ViewPager mMainViewpager;
	private CircleImageView mBottomPlayIv;
	private ImageView mBottomPlayFluctuate;
	private TextView mBottomPlayTv;
	private RelativeLayout mBottomPlayRl;
	private ImageView mMainSerach, main_setting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceiver(myReceiver, new IntentFilter(Constant.UPDATA_BOTTOMVIEW));
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_main;
	}

	@Override
	protected void initView() {
		mMainCategoryTv = (TextView) findViewById(R.id.main_category_tv);
		mMainBroadcastTv = (TextView) findViewById(R.id.main_broadcast_tv);
		mMainSerach = (ImageView) findViewById(R.id.main_serach);
		main_setting = (ImageView) findViewById(R.id.main_setting);
		mMainViewpager = (ViewPager) findViewById(R.id.main_viewpager);
		mBottomPlayRl = (RelativeLayout) findViewById(R.id.bottom_play_rl);
		mBottomPlayIv = (CircleImageView) findViewById(R.id.bottom_play_iv);
		mBottomPlayFluctuate = (ImageView) findViewById(R.id.bottom_play_fluctuate);
		mBottomPlayTv = (TextView) findViewById(R.id.bottom_play_tv);

		Log.i("TAG", "initView: 测试提交...");
		Log.i("TAG", "initView: 测试提交2...");
	}

	@Override
	protected void initData() {
		long timeMillis = System.currentTimeMillis();
		Log.d("czf", "initData" + timeMillis);
		RadioApplication.getInstance().addActivity(this);
		// 添加常驻通知栏的播放器(程序启动顶部通知栏)
		/*mXmPlayerManager.init(
				(int) System.currentTimeMillis(),
				XmNotificationCreater.getInstanse(this).createNotification(
						this.getApplicationContext(), MainActivity.class));*/
		// 初始化标题
		updateTitle(0);
		// 定位
		LocationUtil.location();
		// viewpager+fragment 页面框架
		List<Fragment> fragments = new ArrayList<>();
		fragments.add(new CategoryFragment());
		fragments.add(new BroadcastFragment());
		MainFragmentAdapter mainAdapter = new MainFragmentAdapter(
				getSupportFragmentManager(), fragments);
		mMainViewpager.setAdapter(mainAdapter);

//		new CheckUpdateSyncTask(this).execute() ; //检查更新
	}

	@Override
	protected void setListener() {
		mMainViewpager.addOnPageChangeListener(this);
		mMainCategoryTv.setOnClickListener(this);
		mMainBroadcastTv.setOnClickListener(this);
		mBottomPlayRl.setOnClickListener(this);
		mMainSerach.setOnClickListener(this);
		main_setting.setOnClickListener(this);
	}
	
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			updateBottomPlayPage(mBottomPlayIv, mBottomPlayTv, mBottomPlayFluctuate);
		}
	};
	
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myReceiver);
	};

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		if (position == 0) {
			mOnMainCategorySelected.mainCategorySelected();
		}
		updateTitle(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	protected void processClick(View v) {
		switch (v.getId()) {
		case R.id.main_category_tv:
			updateTitle(0);
			mMainViewpager.setCurrentItem(0);
			break;
		case R.id.main_broadcast_tv:
			updateTitle(1);
			mMainViewpager.setCurrentItem(1);
			break;
		case R.id.bottom_play_rl: {
			if (mXmPlayerManager.getCurrSound() == null) {
				UIUtils.showToast(UIUtils.getContext(), "请选择一个您喜欢听的节目吧!");
				return;
			}
			Intent intent = new Intent(this, PlayActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.main_serach: {
			Intent intent = new Intent(this, SearchActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.main_setting: {
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
		}
			break;
		}
	}

	/**
	 * 根据选择更新标题
	 *
	 * @param position
	 */
	private void updateTitle(int position) {
		Drawable nav_up = getResources().getDrawable(
				R.drawable.shape_bottom_drawable);
		nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
				nav_up.getMinimumHeight());
		switch (position) {
		case 0: // 分类
			mMainCategoryTv.setCompoundDrawables(null, null, null, nav_up);
			mMainBroadcastTv.setCompoundDrawables(null, null, null, null);
			break;
		case 1: // 广播
			mMainBroadcastTv.setCompoundDrawables(null, null, null, nav_up);
			mMainCategoryTv.setCompoundDrawables(null, null, null, null);
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateBottomPlayPage(mBottomPlayIv, mBottomPlayTv, mBottomPlayFluctuate);
	}

	public interface onMainCategorySelected {
		void mainCategorySelected();
	}

	private onMainCategorySelected mOnMainCategorySelected;

	public void setOnMainCategorySelected(onMainCategorySelected l) {
		mOnMainCategorySelected = l;
	}

	/**
	 * 初始化播放模式
	 */
	public void initPlayMode() {

		int playMode = PreferenceUtil.getPlayMode(this, 0);

		/*
		 * 设置播放器模式，mode取值为PlayMode中的下列之一： PLAY_MODEL_SINGLE单曲播放
		 * PLAY_MODEL_SINGLE_LOOP 单曲循环播放 PLAY_MODEL_LIST列表播放
		 * PLAY_MODEL_LIST_LOOP列表循环 PLAY_MODEL_RANDOM 随机播放
		 */
		switch (playMode) {
		case 1: // PLAY_MODEL_SINGLE 单曲播放
			mXmPlayerManager
					.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE);
			break;// PLAY_MODEL_SINGLE_LOOP 单曲循环播放
		case 2:
			mXmPlayerManager
					.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP);
			break;// PLAY_MODEL_LIST 列表播放
		case 3:
			mXmPlayerManager
					.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST);
			break;// PLAY_MODEL_LIST_LOOP 列表循环
		case 4:
			mXmPlayerManager
					.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
			break;// PLAY_MODEL_RANDOM 随机播放
		case 5:
			mXmPlayerManager
					.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM);
			break;
		default:
			break;
		}
	}

}
