package com.ijiakj.radio.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.ijiakj.radio.R;
import com.ijiakj.radio.activity.CategoryListActivity;
import com.ijiakj.radio.activity.MainActivity;
import com.ijiakj.radio.adapter.CategoryGvAdapter;
import com.ijiakj.radio.base.BaseFragment;
import com.ijiakj.radio.bean.BanerBean;
import com.ijiakj.radio.framework.Constant;
import com.ijiakj.radio.framework.RadioApplication;
import com.ijiakj.radio.net.NetUtil;
import com.ijiakj.radio.utils.AdUtils;
import com.ijiakj.radio.utils.UIUtils;
import com.ijiakj.radio.widget.HomePictureHolder;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

/**
 * 创建者 曹自飞 创建时间 2016/8/30 0030 17:53 描述 ${TODO}
 * <p/>
 * 更新者 $Author$ 更新时间 $Date$ 更新描述 ${TODO}
 */
public class CategoryFragment extends BaseFragment implements AdapterView.OnItemClickListener {

	private static final String TAG = "CategoryFragment";

	private String mArrNames[] = { "有声书", "国学书院", "相声评书", "戏曲", "儿童", "电台", "历史", "健康养生", "脱口秀", "娱乐", "资讯", "音乐", "广播剧", "电影", "诗歌",
			"情感生活", "旅游", "时尚生活", "人文", "英语" };

	private int mArrIcons[] = { R.drawable.book, R.drawable.platform, R.drawable.allegro, R.drawable.song, R.drawable.child,
			R.drawable.radio, R.drawable.history, R.drawable.health, R.drawable.xiu, R.drawable.recreation, R.drawable.information,
			R.drawable.music, R.drawable.brodcast, R.drawable.film, R.drawable.poetry, R.drawable.emotion, R.drawable.travel,
			R.drawable.life, R.drawable.education, R.drawable.english };

	private GridView mFragmentCategoryGv;
	private FrameLayout mFragmentCategoryFl;
	private List<Category> mCategories;
	private ScrollView mScrollView;
	private Context mContext;

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_category;
	}

	@Override
	protected void initView() {
		mContext = CategoryFragment.this.getContext();
		mScrollView = (ScrollView) mView.findViewById(R.id.scroll_view);
		mFragmentCategoryGv = (GridView) mView.findViewById(R.id.fragment_category_gv);
		mFragmentCategoryFl = (FrameLayout) mView.findViewById(R.id.fragment_category_fl);
	}

	@Override
	protected void initData() {
		// 设置初始化显示顶部
		mScrollView.smoothScrollTo(0, 20);
		loadData();
		List<String> names = new ArrayList<>();
		List<Drawable> icons = new ArrayList<>();
		for (int i = 0; i < mArrNames.length; i++) {
			names.add(mArrNames[i]);
			Drawable drawable = getResources().getDrawable(mArrIcons[i]);
			icons.add(drawable);
		}
		// 添加分类选项
		CategoryGvAdapter categoryGvAdapter = new CategoryGvAdapter(names, icons);
		mFragmentCategoryGv.setAdapter(categoryGvAdapter);
		mFragmentCategoryGv.setSelector(R.drawable.selector_item_gv_bg);
		mFragmentCategoryGv.setBackgroundColor(Color.WHITE);

		// 添加广告
		final HomePictureHolder homePictureHolder = new HomePictureHolder(mContext);

		mFragmentCategoryFl.addView(homePictureHolder.mHolderView);

		RequestParams params = new RequestParams(Constant.HOST + "api/advertising/get");
		params.addQueryStringParameter("site_code", "ijiaFM");
		params.addQueryStringParameter("channel", "common");
		AdUtils.addCommValue(mContext, params);
		x.http().post(params, new CommonCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {

			}

			@Override
			public void onFinished() {

			}

			@Override
			public void onSuccess(final String result) {
				if (!TextUtils.isEmpty(result) && result.contains("succesfully")) {
					// 解析数据
					Gson gson = new Gson();
					BanerBean banerBean = gson.fromJson(result, BanerBean.class);
					homePictureHolder.setDataAndRefreshHolderView(banerBean);
				}
			}
		});
	}

	private void loadData() {
		// 获取分类数据
		Map<String, String> map1 = new HashMap<String, String>();
		CommonRequest.getCategories(map1, new IDataCallBack<CategoryList>() {
			@Override
			public void onSuccess(CategoryList object) {
				mCategories = object.getCategories();
				String json = new Gson().toJson(object);
				Log.d(TAG, json);
			}

			@Override
			public void onError(int code, String message) {
				UIUtils.showToast(mContext, "网络环境不好!");
			}
		});
	}

	@Override
	protected void setListener() {
		mFragmentCategoryGv.setOnItemClickListener(CategoryFragment.this);

		MainActivity activity = (MainActivity) getActivity();
		activity.setOnMainCategorySelected(new MainActivity.onMainCategorySelected() {
			@Override
			public void mainCategorySelected() {
				// mScrollView.scrollTo(0,20);
			}
		});

		// 让屏幕跟着焦点滚动
		mFragmentCategoryGv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				int y = (int) view.getY();

				mScrollView.smoothScrollTo(0, mFragmentCategoryFl.getHeight() + y);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	@Override
	protected void processClick(View v) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		if (!NetUtil.isNetworkConnected(UIUtils.getContext())) {
			UIUtils.showToast(UIUtils.getContext(), "无网络连接,请检查后再重试!");
			return;
		}
		if (mCategories == null) {
			UIUtils.showToast(UIUtils.getContext(), "正在尝试连接,请稍等!");
			loadData();
			return;
		}
		Intent intent = new Intent(getActivity(), CategoryListActivity.class);
		String titleName = mArrNames[position];
		int temp = mCategories.size();
		for (int i = 0; i < temp; i++) {
			Category category = mCategories.get(i);
			if (category.getCategoryName().equals(titleName)) {
				Map<String, Object> protocolCacheMap = RadioApplication.getDeliverMap();
				protocolCacheMap.put(Constant.DeliverMap.CATEGORY_KEY_ID, String.valueOf(category.getId()));
				protocolCacheMap.put(Constant.DeliverMap.CATEGORY_KEY_NAME, titleName);
				protocolCacheMap.put(Constant.DeliverMap.CATEGORY_KEY_LOCID, position);
				break;
			}
		}
		startActivity(intent);
	}
}
