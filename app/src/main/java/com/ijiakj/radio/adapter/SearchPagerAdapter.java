package com.ijiakj.radio.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ijiakj.radio.R;
import com.ijiakj.radio.activity.PlayActivity;
import com.ijiakj.radio.framework.Constant;
import com.ijiakj.radio.framework.RadioApplication;
import com.ijiakj.radio.utils.UIUtils;
import com.ijiakj.radio.widget.XListView;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 创建者     曹自飞
 * 创建时间   2016/9/1 0001 15:43
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SearchPagerAdapter extends PagerAdapter {
    private List<String> mTags;
    private Context mContext;
    private String mKeyword;
    private int mAPageNum = 1;
    private int mBPageNum = 1;
    private boolean mIsLoadMore = false;
    private boolean mIsRefresh = false;

    public SearchPagerAdapter(Context context, List tags, String keyword) {
        mContext = context;
        mTags = tags;
        mKeyword = keyword;
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

        View view = View.inflate(mContext, R.layout.listview_or_emptyview, null);
        XListView lv = (XListView) view.findViewById(R.id.search_lv);
        lv.setFocusable(true);

        View remindView = view.findViewById(R.id.category_lv_remind);
        Button empty = (Button) view.findViewById(R.id.error_btn_retry);
        ImageView errorImg = (ImageView) view.findViewById(R.id.error_img);
        empty.setBackground(null);
        errorImg.setVisibility(View.GONE);
        empty.setText("正在加载中...");


        // TextView loadHint = (TextView) view.findViewById(R.id.load_hint);
        lv.setEmptyView(remindView);
        // XListView lv = new XListView(mContext);
        lv.setPullRefreshEnable(true);
        lv.setPullLoadEnable(true);
        lv.setAutoLoadEnable(true);
        lv.setRefreshTime(getTime());

        if (position == 0) {
            loadAlbumsData(lv, empty, errorImg, null, position);
            lv.setOnItemClickListener(mOnAubmsItemClickListener);
        }
        if (position == 1) {
            loadRadiosData(lv, empty, errorImg, null, position);
            lv.setOnItemClickListener(mOnRadioItemClickListener);
        }

        lv.setSelector(R.drawable.selector_focused_list_bg);
        // lv.setBackgroundColor(Color.WHITE);
        container.addView(view);
        return view;
    }

    /**
     * 加载专辑搜索的数据
     *
     * @param lv
     */
    private void loadAlbumsData(final XListView lv, final TextView empty, final ImageView errorImg, final CommonListAdapter commonListAdapter, final int position) {

        Log.i("instantiateItem", " loadListData: " + position);
        if (position == 0) {
            //获取专辑搜索列表数据
            Map<String, String> map = new HashMap<String, String>();
            map.put(DTransferConstants.SEARCH_KEY, mKeyword);
            // map.put(DTransferConstants.CATEGORY_ID, mCategoryId);
            map.put(DTransferConstants.PAGE, String.valueOf(mAPageNum));
            CommonRequest.getSearchedAlbums(map, new IDataCallBack<SearchAlbumList>() {

                @Override
                public void onSuccess(SearchAlbumList searchAlbumList) {

                    if (searchAlbumList.getTotalPage() <= mAPageNum) {
                        lv.setPullLoadEnable(false);
                    } else {
                        lv.setPullLoadEnable(true);
                    }
                    List<Album> albums = searchAlbumList.getAlbums();
                    if (mIsLoadMore) {
                        //加载更多
                        if (albums.size() == 0) {
                            UIUtils.showToast(UIUtils.getContext(), "没有更多数据了!");
                        }

                        commonListAdapter.addAubms(albums,position);
                        mIsLoadMore = false;
                    } else if (mIsRefresh) {
                        //下拉刷新
                        commonListAdapter.refreshData(albums ,position);
                        mIsRefresh = false;
                    } else {
                        //正常加载
                        if (albums.size() == 0) {
                            errorImg.setVisibility(View.GONE);
                            empty.setText("此页空空如也!");
                        }

                        final CommonListAdapter commonListAdapter = new CommonListAdapter(albums, mContext);
                        lv.setAdapter(commonListAdapter);
                        lv.setXListViewListener(new XListView.IXListViewListener() {
                            @Override
                            public void onRefresh() {
                                mIsRefresh = true;
                                mAPageNum = 1;
                                loadAlbumsData(lv, empty, errorImg, commonListAdapter, position);
                            }

                            @Override
                            public void onLoadMore() {
                                lv.setPullLoadEnable(false);
                                mIsLoadMore = true;
                                loadAlbumsData(lv, empty, errorImg, commonListAdapter, position);

                            }
                        });
                    }
                    ++mAPageNum;
                    onLoad(lv);
                }

                @Override
                public void onError(int i, String s) {
                    errorImg.setVisibility(View.VISIBLE);
                    empty.setText("网络不给力,检测后再试试!");
                }

            });
        }
    }

    /**
     * 加载广播搜索的数据
     *
     * @param lv
     */
    private void loadRadiosData(final XListView lv, final TextView empty, final ImageView errorImg, final RadiosListAdapter radiosListAdapter, final int position) {
        if (position == 1) {
            //获取广播搜索列表数据
            Map<String, String> map = new HashMap<String, String>();
            map.put(DTransferConstants.SEARCH_KEY, mKeyword);
            map.put(DTransferConstants.PAGE, String.valueOf(mBPageNum));
            CommonRequest.getSearchedRadios(map, new IDataCallBack<RadioList>() {
                @Override
                public void onSuccess(RadioList radioList) {

                    if (radioList.getCurrentPage() == radioList.getTotalPage()) {
                        lv.setPullLoadEnable(false);
                    } else {
                        lv.setPullLoadEnable(true);
                    }

                    List<Radio> radios = radioList.getRadios();

                    if (mIsLoadMore) {
                        //加载更多
                        if (radios.size() == 0) {
                            UIUtils.showToast(UIUtils.getContext(), "没有更多数据了!");
                        }
                        radiosListAdapter.addRadios(radios);
                        mIsLoadMore = false;
                    } else if (mIsRefresh) {
                        //下拉刷新
                        radiosListAdapter.refreshData(radios);
                        mIsRefresh = false;
                    } else {
                        //正常加载

                        if (radios.size() == 0) {
                            errorImg.setVisibility(View.GONE);
                            empty.setText("此页空空如也!");
                        }
                        final RadiosListAdapter radiosListAdapter = new RadiosListAdapter(radios);
                        lv.setAdapter(radiosListAdapter);
                        lv.setXListViewListener(new XListView.IXListViewListener() {
                            @Override
                            public void onRefresh() {
                                mIsRefresh = true;
                                mBPageNum = 1;
                                loadRadiosData(lv, empty, errorImg, radiosListAdapter, position);
                            }

                            @Override
                            public void onLoadMore() {
                                lv.setPullLoadEnable(false);
                                mIsLoadMore = true;
                                loadRadiosData(lv, empty, errorImg, radiosListAdapter, position);

                            }
                        });

                    }
                    ++mBPageNum;
                    onLoad(lv);
                }

                @Override
                public void onError(int i, String s) {
                    errorImg.setVisibility(View.VISIBLE);
                    empty.setText("网络不给力,检测后再试试!");
                }
            });
        }
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTags.get(position);
    }

    /**
     * 专辑列表的条目点击事件
     */
    private AdapterView.OnItemClickListener mOnAubmsItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            //从当前listview中获取专辑列表数据
            XListView lv = (XListView) parent;
            HeaderViewListAdapter adapter = (HeaderViewListAdapter) lv.getAdapter();
            CommonListAdapter wrappedAdapter = (CommonListAdapter) adapter.getWrappedAdapter();
            List<Album> aubms = wrappedAdapter.getAubms();

            if (position > aubms.size()) {
                //点击了最后一条加载更多
                mIsLoadMore = true;
                loadAlbumsData(lv, null, null, wrappedAdapter, 0);
                return;
            } else {
                //存入数据传递集合
                RadioApplication.getDeliverMap().put(Constant.DeliverMap.PLAY_KEY, aubms.get(position - 1));

                //用于控制是否默认播放
                Constant.IS_REPLAY = true;

                //跳转
                Intent intent = new Intent(mContext, PlayActivity.class);
                mContext.startActivity(intent);
            }
        }
    };

    /**
     * 广播列表的条目点击事件
     */
    private AdapterView.OnItemClickListener mOnRadioItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            XListView lv = (XListView) parent;
            HeaderViewListAdapter adapter = (HeaderViewListAdapter) lv.getAdapter();
            RadiosListAdapter wrappedAdapter = (RadiosListAdapter) adapter.getWrappedAdapter();
            List<Radio> radios = wrappedAdapter.getRadios();

            if (position > radios.size()) {
                //点击了最后一条加载更多
                mIsLoadMore = true;
                loadRadiosData(lv, null, null, wrappedAdapter, 1);
                return;
            } else {
                Radio radio = radios.get(position - 1);
                //用于控制是否默认播放
                Constant.IS_REPLAY = true;

                //通过application类 传递数据
                Intent intent = new Intent(mContext, PlayActivity.class);
                Map<String, Object> protocolCacheMap = RadioApplication.getDeliverMap();
                protocolCacheMap.put(Constant.DeliverMap.PLAY_KEY, radio);
                mContext.startActivity(intent);
            }
        }
    };

    private void onLoad(XListView xLv) {
        xLv.stopRefresh();
        xLv.stopLoadMore();
        xLv.setRefreshTime(getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }
}
