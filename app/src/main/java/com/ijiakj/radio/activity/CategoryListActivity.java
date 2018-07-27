package com.ijiakj.radio.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ijiakj.radio.R;
import com.ijiakj.radio.adapter.CategoryPagerAdapter;
import com.ijiakj.radio.base.BaseActivity;
import com.ijiakj.radio.base.LoadingPager;
import com.ijiakj.radio.framework.Constant;
import com.ijiakj.radio.framework.RadioApplication;
import com.ijiakj.radio.utils.UIUtils;
import com.ijiakj.radio.widget.CircleImageView;
import com.ijiakj.radio.widget.PagerSlidingTabStrip;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.tag.Tag;
import com.ximalaya.ting.android.opensdk.model.tag.TagList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建者 曹自飞 创建时间 2016/8/31 0031 19:26 描述 ${TODO}
 * <p/>
 * 更新者 $Author$ 更新时间 $Date$ 更新描述 ${TODO}
 */
public class CategoryListActivity extends BaseActivity implements View.OnClickListener {

	private static final String TAG = "CategoryListActivity";
	private PagerSlidingTabStrip mCategoryTabs;
	private ViewPager mCategoryViewpager;
	private ImageView mTitleBackIv;
	private ImageView mBottomPlayFluctuate;
	private TextView mTitleTv;
	private TextView mBottomPlayTv;
	private CircleImageView mBottomPlayIv;
	private RelativeLayout mBottomPlayRl;
	private ImageView mTitleSerachIv;
	private FrameLayout mCategoryResultFl;
	private List<Tag> mTagList;
	private LoadingPager mLoadingPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceiver(myReceiver, new IntentFilter(Constant.UPDATA_BOTTOMVIEW));
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_category_list;
	}

	@Override
	protected void initView() {
		mTitleBackIv = (ImageView) findViewById(R.id.title_back_iv);
		mTitleSerachIv = (ImageView) findViewById(R.id.title_serach_iv);
		mTitleTv = (TextView) findViewById(R.id.title_tv);
		mTitleTv.setGravity(Gravity.CENTER);
		mTitleTv.setFocusable(false);
		mCategoryResultFl = (FrameLayout) findViewById(R.id.category_result_fl);
		// mCategoryTabs = (PagerSlidingTabStripExtends)
		// findViewById(R.id.category_tabs);
		// mCategoryViewpager = (ViewPager)
		// findViewById(R.id.category_viewpager);
		mBottomPlayRl = (RelativeLayout) findViewById(R.id.bottom_play_rl);
		mBottomPlayFluctuate = (ImageView) findViewById(R.id.bottom_play_fluctuate);
		mBottomPlayIv = (CircleImageView) findViewById(R.id.bottom_play_iv);
		mBottomPlayTv = (TextView) findViewById(R.id.bottom_play_tv);

		// 内容区加载各种视图
		mLoadingPager = new LoadingPager(UIUtils.getContext()) {
			@Override
			public LoadedResult initData() {
				return CategoryListActivity.this.loadData();
			}

			@Override
			public View initSuccessView() {
				return CategoryListActivity.this.initSuccessView();
			}
		};

		mCategoryResultFl.addView(mLoadingPager);
	}

	/**
	 * 内容区成功视图
	 *
	 * @return
	 */
	private View initSuccessView() {

		View view = View.inflate(getApplicationContext(), R.layout.categroy_succeed, null);
		mCategoryTabs = (PagerSlidingTabStrip) view.findViewById(R.id.category_tabs);
		mCategoryViewpager = (ViewPager) view.findViewById(R.id.category_viewpager);
		CategoryPagerAdapter pagerAdapter = new CategoryPagerAdapter(CategoryListActivity.this, mTagList);
		mCategoryViewpager.setAdapter(pagerAdapter);
		mCategoryTabs.setViewPager(mCategoryViewpager);

		return view;
	}

	private LoadingPager.LoadedResult loadData() {

		Map<String, Object> protocolCacheMap = RadioApplication.getDeliverMap();
		String id = (String) protocolCacheMap.get(Constant.DeliverMap.CATEGORY_KEY_ID);

		final Thread thread = Thread.currentThread();
		// 获取分类标题标签
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put(DTransferConstants.CATEGORY_ID, id);
		map1.put(DTransferConstants.TYPE, "0");
		CommonRequest.getTags(map1, new IDataCallBack<TagList>() {
			@Override
			public void onSuccess(TagList tagList) {

				synchronized (thread) {
					mTagList = tagList.getTagList();
					Gson gson = new Gson();
					String s = gson.toJson(mTagList);
					Log.i("CategoryListActivity ", s);
					// 唤醒
					thread.notify();
				}
			}

			@Override
			public void onError(int i, String s) {
				synchronized (thread) {
					// 唤醒
					thread.notify();
				}
			}
		});

		synchronized (thread) {
			try {
				// 子线程在此处等待,在数据加载有结果后唤醒!
				thread.wait();

				// 唤醒后返回结果
				if (mTagList == null) {
					return LoadingPager.LoadedResult.ERROR;
				} else if (mTagList.size() == 0) {
					return LoadingPager.LoadedResult.EMPTY;
				} else {
					return LoadingPager.LoadedResult.SUCCESS;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return LoadingPager.LoadedResult.ERROR;
			}
		}
	}

	@Override
	protected void initData() {
		RadioApplication.getInstance().addActivity(this);
		Map<String, Object> protocolCacheMap = RadioApplication.getDeliverMap();
		String name = (String) protocolCacheMap.get(Constant.DeliverMap.CATEGORY_KEY_NAME);
		mTitleTv.setText(name);
		// 触发加载数据
		mLoadingPager.triggerLoadData();
	}

	@Override
	protected void setListener() {
		mTitleBackIv.setOnClickListener(this);
		mBottomPlayRl.setOnClickListener(this);
		mTitleSerachIv.setOnClickListener(this);
	}

	@Override
	protected void processClick(View v) {
		switch (v.getId()) {
		case R.id.title_back_iv:
			finish();
			break;
		case R.id.title_serach_iv: {
			Intent intent = new Intent(this, SearchActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.bottom_play_rl:
			Intent intent = new Intent(this, PlayActivity.class);
			startActivity(intent);
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

}
