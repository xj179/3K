package com.ijiakj.radio.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.iflytek.voiceads.NativeADDataRef;
import com.ijiakj.radio.R;
import com.ijiakj.radio.activity.PlayActivity;
import com.ijiakj.radio.framework.Constant;
import com.ijiakj.radio.framework.RadioApplication;
import com.ijiakj.radio.net.NetUtil;
import com.ijiakj.radio.utils.AdUtils;
import com.ijiakj.radio.utils.UIUtils;
import com.ijiakj.radio.widget.XListView;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.tag.Tag;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 创建者 曹自飞 创建时间 2016/9/1 0001 15:43 描述 ${TODO}
 * <p/>
 * 更新者 $Author$ 更新时间 $Date$ 更新描述 ${TODO}
 */
public class CategoryPagerAdapter extends PagerAdapter {
	private List<Tag> mTags;
	private Context mContext;

	private int mReqPage = 1;
	private boolean mIsLoadMore = false;

	public CategoryPagerAdapter(Context context, List tags) {
		mContext = context;
		mTags = tags;
	}

	@Override
	public int getCount() {
		return mTags == null ? 0 : mTags.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@SuppressLint("NewApi")
	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		mReqPage = 1;
		View view = View.inflate(mContext, R.layout.listview_or_emptyview, null);
		XListView xLv = (XListView) view.findViewById(R.id.search_lv);
		View remindView = view.findViewById(R.id.category_lv_remind);
		Button empty = (Button) view.findViewById(R.id.error_btn_retry);
		ImageView errorImg = (ImageView) view.findViewById(R.id.error_img);
		empty.setBackground(null);
		errorImg.setVisibility(View.GONE);
		empty.setText("正在加载中...");
		xLv.setEmptyView(remindView);

		CommonListAdapter commonListAdapter = new CommonListAdapter(mContext);
		int pagePosition = position;
		xLv.setPullRefreshEnable(true);
		xLv.setPullLoadEnable(true);
		xLv.setAutoLoadEnable(true);
		// 设置各种监听
		setListener(xLv, empty, errorImg, commonListAdapter, pagePosition);

		xLv.setRefreshTime(getTime());
		// 设置适配器
		xLv.setAdapter(commonListAdapter);
		xLv.setSelector(R.drawable.selector_focused_list_bg);
		xLv.setBackgroundColor(Color.WHITE);

		GradientDrawable gradientDrawable = new GradientDrawable();
		gradientDrawable.setColor(mContext.getResources().getColor(R.color.bg));

		container.addView(view);

		// 加载数据
		loadListData(xLv, empty, errorImg, commonListAdapter, pagePosition);
		return view;
	}

	private void setListener(final XListView xLv, final TextView empty, final ImageView errorImg,
			final CommonListAdapter commonListAdapter, final int pagePosition) {

		xLv.setXListViewListener(new XListView.IXListViewListener() {
			@Override
			public void onRefresh() {
				// 重新加载第一页
				mReqPage = 1;
				loadListData(xLv, empty, errorImg, commonListAdapter, pagePosition);
			}

			@Override
			public void onLoadMore() {
				// 加载下一页数据,并以追加的形式显示出来.
				mIsLoadMore = true;
				loadListData(xLv, empty, errorImg, commonListAdapter, pagePosition);
				xLv.setPullLoadEnable(false);
			}
		});

		xLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				if ((int) view.getTag(R.string.commonlistadapter_tag) == Constant.DATA_ID_FLAG) {
					NativeADDataRef adRef = (NativeADDataRef)RadioApplication.getDeliverMap().get(Constant.DeliverMap.NATIVEADDATAREF_AD);
					if (adRef!=null) {
						adRef.onClicked(view);
					}
					AdUtils.clickUploading(mContext, RadioApplication.getDeliverMap().get(Constant.DeliverMap.COMM_IMAG_URL)+"", "3",
							RadioApplication.getDeliverMap().get(Constant.DeliverMap.CATEGORY_KEY_NAME)+"");
					return;
				}
				if (!NetUtil.isNetworkConnected(UIUtils.getContext())) {
					UIUtils.showToast(UIUtils.getContext(), "无网络连接,请检查后再重试!");
					return;
				}

				// 从当前listview中获取专辑列表数据
				ListAdapter adapter = (ListAdapter) parent.getAdapter();
				HeaderViewListAdapter ViewListAdapter = (HeaderViewListAdapter) adapter;
				CommonListAdapter wrappedAdapter = (CommonListAdapter) ViewListAdapter.getWrappedAdapter();
				List<Album> aubms = wrappedAdapter.getAubms();

				if (position > aubms.size()) {
					// 点击了最后一条加载更多
					mIsLoadMore = true;
					loadListData(xLv, empty, errorImg, commonListAdapter, pagePosition);
					return;
				} else {
					// 存入数据传递集合
					RadioApplication.getDeliverMap().put(Constant.DeliverMap.PLAY_KEY, aubms.get(position - 1));
					// 用于控制是否默认播放
					Constant.IS_REPLAY = true;
					// 跳转
					Intent intent = new Intent(mContext, PlayActivity.class);
					mContext.startActivity(intent);
				}
			}
		});

	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTags.get(position).getTagName();
	}

	/**
	 * @param commonListAdapter
	 * @param position
	 * @des 加载列表数据
	 */
	private void loadListData(final XListView xLv, final TextView empty, final ImageView errorImg,
			final CommonListAdapter commonListAdapter, final int position) {
		// 获取列表数据
		final Map<String, String> map = new HashMap<String, String>();
		map.put(DTransferConstants.CATEGORY_ID, (String) RadioApplication.getDeliverMap().get(Constant.DeliverMap.CATEGORY_KEY_ID));
		map.put(DTransferConstants.CALC_DIMENSION, "3");
		map.put(DTransferConstants.TAG_NAME, mTags.get(position).getTagName());
		map.put(DTransferConstants.PAGE, String.valueOf(mReqPage));
		CommonRequest.getAlbumList(map, new IDataCallBack<AlbumList>() {
			@SuppressLint("NewApi")
			@Override
			public void onSuccess(AlbumList albumList) {
				if (albumList.getTotalPage() <= mReqPage) {
					xLv.setPullLoadEnable(false);
				} else {
					xLv.setPullLoadEnable(true);
				}

				List<Album> albums = albumList.getAlbums();
				if (mIsLoadMore) {
					// 加载更多数据
					if (albumList.getAlbums().size() == 0) {
						UIUtils.showToast(UIUtils.getContext(), "没有更多了!");
					}
					commonListAdapter.addAubms(albums, position);
					mIsLoadMore = false;
				} else {
					if (albumList.getAlbums().size() == 0) {
						errorImg.setVisibility(View.GONE);
						empty.setBackground(null);
						empty.setText("此页空空如也!");
					}
					// 正常加载
					commonListAdapter.refreshData(albumList.getAlbums(), position);
					commonListAdapter.notifyDataSetChanged();
				}

				++mReqPage;
				onLoad(xLv);
				Log.i("albumList", albums.toString());

			}

			@Override
			public void onError(int i, String s) {

				errorImg.setVisibility(View.VISIBLE);
				empty.setBackgroundResource(R.drawable.btn);
				empty.setText("网络不给力,检查后点击重试!");
				empty.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// 重新加载第一页
						mReqPage = 1;
						loadListData(xLv, empty, errorImg, commonListAdapter, position);
					}
				});

			}
		});

	}

	private void onLoad(XListView xLv) {
		xLv.stopRefresh();
		xLv.stopLoadMore();
		xLv.setRefreshTime(getTime());
	}

	private String getTime() {
		return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
	}

}
