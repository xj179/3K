package com.ijiakj.radio.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ijiakj.radio.R;
import com.ijiakj.radio.adapter.RadiosListAdapter;
import com.ijiakj.radio.base.BaseActivity;
import com.ijiakj.radio.framework.Constant;
import com.ijiakj.radio.framework.RadioApplication;
import com.ijiakj.radio.net.NetUtil;
import com.ijiakj.radio.utils.PreferenceUtil;
import com.ijiakj.radio.utils.UIUtils;
import com.ijiakj.radio.widget.CircleImageView;
import com.ijiakj.radio.widget.XListView;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListByCategory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BroadcastListActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ImageView mTitleBackIv;
    private ImageView mTitleSearchIv;
    private TextView mTitleTv;
    private RelativeLayout mBottomPlayRl;
    private ImageView mBottomPlayFluctuate;
    private CircleImageView mBottomPlayIv;
    private TextView mBottomPlayTv;
    private XListView mActivityBroadcastLv;
    private int mPageNum = 1;
    private boolean mIsLoadMore = false;
    private RadiosListAdapter mAdapter;
    private List<Radio> mRadios;
    private Button mEmpty;
    private ImageView mErrorImg;
    private String mAdCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	registerReceiver(myReceiver, new IntentFilter(Constant.UPDATA_BOTTOMVIEW));
    }
    
    @Override
    protected int getLayoutId() {
        return R.layout.activity_broadcast_list;
    }

    @Override
    protected void initView() {
        mTitleBackIv = (ImageView) findViewById(R.id.title_back_iv);
        mTitleSearchIv = (ImageView) findViewById(R.id.title_serach_iv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mTitleTv.setGravity(Gravity.CENTER);
        mTitleTv.setFocusable(false);
        mActivityBroadcastLv = (XListView) findViewById(R.id.activity_broadcast_lv);

        //listview 异常转态提醒
        View remindView = findViewById(R.id.activity_broadcast_lv_empty);
        mEmpty = (Button) findViewById(R.id.error_btn_retry);
        mErrorImg = (ImageView) findViewById(R.id.error_img);
        mErrorImg.setVisibility(View.GONE);
        mEmpty.setText("正在加载中...");
        mActivityBroadcastLv.setEmptyView(remindView);
        mActivityBroadcastLv.setPullRefreshEnable(true);
        mActivityBroadcastLv.setPullLoadEnable(true);
        mActivityBroadcastLv.setAutoLoadEnable(true);
        mActivityBroadcastLv.setXListViewListener(mIXListViewListener);
        mActivityBroadcastLv.setRefreshTime(getTime());
        mBottomPlayRl = (RelativeLayout) findViewById(R.id.bottom_play_rl);
        mBottomPlayFluctuate = (ImageView) findViewById(R.id.bottom_play_fluctuate);
        mBottomPlayIv = (CircleImageView) findViewById(R.id.bottom_play_iv);
        mBottomPlayTv = (TextView) findViewById(R.id.bottom_play_tv);
    }

    @Override
    protected void initData() {
        RadioApplication.getInstance().addActivity(this);
        //初始化标题栏
        Intent intent = getIntent();
        String cast_key = intent.getStringExtra(Constant.CAST_KEY);
        mTitleTv.setText(cast_key);
        String position = intent.getStringExtra(Constant.CAST_POSITION_KEY);

        if (position.equals("0")) {  //GridView 的 0 索引处是本地台,需调用CommonRequest.getRadios 获得数据
            mAdCode = "";
            String adCode = PreferenceUtil.getLastLocationAdCode(UIUtils.getContext());
            for (int i = 0; i < 2; i++) {
                mAdCode += adCode.charAt(i);
            }
            mAdCode += "0000";
            //本地台的数据加载处理
            loadAndRefreshNative();
            return;
        } else {
            //其他台的数据加载处理
            loadOtherData(position);
        }

    }

    /**
     * 本地台的数据加载处理
     */
    private void loadAndRefreshNative() {
        //获取数据
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOTYPE, "2");
        map.put(DTransferConstants.PROVINCECODE, mAdCode);
        map.put(DTransferConstants.PAGE, String.valueOf(mPageNum));

        CommonRequest.getRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(RadioList radioList) {
                if (radioList.getTotalPage() <= mPageNum) {
                    mActivityBroadcastLv.setPullLoadEnable(false);
                }
                if (mIsLoadMore) {
                    //加载更多数据
                    if (radioList.getTotalPage() <= mPageNum) {
                        mActivityBroadcastLv.setPullLoadEnable(false);
                        UIUtils.showToast(UIUtils.getContext(), "没有更多数据了!");
                    } else {
                        mActivityBroadcastLv.setPullLoadEnable(true);
                    }
                    if (radioList.getRadios().size() == 0) {
                        UIUtils.showToast(UIUtils.getContext(), "没有更多数据了!");
                    }
                    mAdapter.getRadios().addAll(radioList.getRadios());
                    mAdapter.notifyDataSetChanged();
                    mIsLoadMore = false;
                } else {

                    //正常加载数据
                    if (radioList.getRadios().size() == 0) {
                        mErrorImg.setVisibility(View.GONE);
                        mEmpty.setText("此页空空如也!");

                    }
                    mAdapter = new RadiosListAdapter(radioList.getRadios());
                    mActivityBroadcastLv.setAdapter(mAdapter);
                    mActivityBroadcastLv.setSelector(R.drawable.selector_focused_list_bg);
                    mActivityBroadcastLv.setBackgroundColor(Color.WHITE);
                    mAdapter.notifyDataSetChanged();
                }
                //上下拉刷新还原.
                ++mPageNum;
                onLoad();
            }

            @Override
            public void onError(int i, String s) {
                mErrorImg.setVisibility(View.VISIBLE);
                mEmpty.setText("网络不给力,检查后点击重试!");
                mEmpty.setBackgroundResource(R.drawable.btn);
                mEmpty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //重新加载第一页
                        mPageNum = 1;
                        initData();
                    }
                });
            }
        });
    }

    /**
     * 其他台的数据加载处理
     *
     * @param position
     */
    private void loadOtherData(String position) {
        //根据position 获取数据
        String radioCategoryId = null;
        switch (position) {

            case "1": //曲艺台
                radioCategoryId = "8";
                break;
            case "2"://新闻台
                radioCategoryId = "5";
                break;
            case "3"://综合台
                radioCategoryId = "12";
                break;
            case "4"://生活台
                radioCategoryId = "10";
                break;
            case "5"://都市台
                radioCategoryId = "13";
                break;

        }

        //其他台获取数据的接口
        Map<String, String> maps = new HashMap<String, String>();
        maps.put(DTransferConstants.RADIO_CATEGORY_ID, radioCategoryId);
        maps.put(DTransferConstants.PAGE, String.valueOf(mPageNum));
        CommonRequest.getRadiosByCategory(maps, new IDataCallBack<RadioListByCategory>() {
            @Override
            public void onSuccess(RadioListByCategory object) {
                if (object.getTotalPage() <= mPageNum) {
                    mActivityBroadcastLv.setPullLoadEnable(false);
                } else {
                    mActivityBroadcastLv.setPullLoadEnable(true);
                }
                if (mIsLoadMore) {
                    //加载更多数据
                    if (object.getRadios().size() == 0) {
                        UIUtils.showToast(UIUtils.getContext(), "没有更多数据了!");
                    }
                    mRadios = mAdapter.getRadios();
                    mRadios.addAll(object.getRadios());
                    mAdapter.notifyDataSetChanged();
                    mIsLoadMore = false;

                } else {
                    //正常加载数据
                    if (object.getRadios().size() == 0) {
                        mErrorImg.setVisibility(View.GONE);
                        mEmpty.setText("此页空空如也!");
                    }
                    mAdapter = new RadiosListAdapter(object.getRadios());
                    mActivityBroadcastLv.setAdapter(mAdapter);
                    mActivityBroadcastLv.setSelector(R.drawable.selector_focused_list_bg);
                    mActivityBroadcastLv.setBackgroundColor(Color.WHITE);
                    mAdapter.notifyDataSetChanged();

                }
                //上下拉刷新还原.
                ++mPageNum;
                onLoad();
            }

            @Override
            public void onError(int code, String message) {
                mErrorImg.setVisibility(View.VISIBLE);
                mEmpty.setText("网络不给力,检查后点击重试!");
                mEmpty.setBackgroundResource(R.drawable.btn);
                mEmpty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //重新加载第一页
                        mPageNum = 1;
                        initData();
                    }

                });
            }
        });
    }


    @Override
    protected void setListener() {
        mTitleBackIv.setOnClickListener(this);
        mActivityBroadcastLv.setOnItemClickListener(this);
        mTitleSearchIv.setOnClickListener(this);
        mBottomPlayRl.setOnClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (!NetUtil.isNetworkConnected(UIUtils.getContext())) {
            UIUtils.showToast(UIUtils.getContext(), "无网络连接,请检查后再重试!");
        }

        ListView lv = (ListView) parent;
        HeaderViewListAdapter viewListAdapter = (HeaderViewListAdapter) lv.getAdapter();
        RadiosListAdapter wrappedAdapter = (RadiosListAdapter) viewListAdapter.getWrappedAdapter();
        List<Radio> radios = wrappedAdapter.getRadios();

        if (position > radios.size()) {
            //加载更多
            mIsLoadMore = true;
            initData();
        } else {
            Radio radio = radios.get(position - 1);
            //用于控制是否默认播放
            Constant.IS_REPLAY = true;

            //通过application类 传递数据
            Intent intent = new Intent(BroadcastListActivity.this, PlayActivity.class);
            Map<String, Object> protocolCacheMap = RadioApplication.getDeliverMap();
            protocolCacheMap.put(Constant.DeliverMap.PLAY_KEY, radio);
            startActivity(intent);
        }
    }

    /**
     * 下拉刷新,上拉加载更多监听!
     */
    private XListView.IXListViewListener mIXListViewListener = new XListView.IXListViewListener() {
        @Override
        public void onRefresh() {
            //重新加载第一页
            mPageNum = 1;
            initData();
        }

        @Override
        public void onLoadMore() {
            //加载下一页数据,并已追加的形式显示出来.
            mActivityBroadcastLv.setPullLoadEnable(false);
            mIsLoadMore = true;
            initData();
        }
    };

    private void onLoad() {
        mActivityBroadcastLv.stopRefresh();
        mActivityBroadcastLv.stopLoadMore();
        mActivityBroadcastLv.setRefreshTime(getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

}
