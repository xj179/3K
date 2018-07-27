package com.ijiakj.radio.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.amap.api.location.AMapLocationClient;
import com.google.gson.Gson;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.ijiakj.radio.R;
import com.ijiakj.radio.activity.BroadcastListActivity;
import com.ijiakj.radio.activity.PlayActivity;
import com.ijiakj.radio.adapter.CategoryGvAdapter;
import com.ijiakj.radio.adapter.RadiosListAdapter;
import com.ijiakj.radio.base.BaseFragment;
import com.ijiakj.radio.framework.Constant;
import com.ijiakj.radio.framework.RadioApplication;
import com.ijiakj.radio.net.NetUtil;
import com.ijiakj.radio.utils.AdUtils;
import com.ijiakj.radio.utils.PreferenceUtil;
import com.ijiakj.radio.utils.ToolUtil;
import com.ijiakj.radio.utils.UIUtils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xutils.x;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;

/**
 * 创建者     曹自飞
 * 创建时间   2016/8/30 0030 17:53
 * 描述	     广播页面的内容
 * <p/>
 * 更新者     $Author$
 * <p/>
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */

public class BroadcastFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private String mArrNames[] = {"本地台", "曲艺台", "新闻台", "综合台", "生活台", "都市台"};

    private int mArrIcons[] = {R.drawable.local, R.drawable.song_yi,
            R.drawable.news, R.drawable.synthesize,
            R.drawable.broadcast_life, R.drawable.city,
    };
    private GridView mFragmentBroadcastGv;
    private ListView mFragmentBroadcastLv;
    private List<String> mNames;
    private ScrollView mCastScrollView;
    private AMapLocationClient mLocationClient;
    private Context mContext;
    
    private ImageView bordcast_ad;
	private IFLYNativeAd bordcastAd;
	private NativeADDataRef adItem;
	private RelativeLayout bordcast_lay;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bordcast;
    }

    @Override
    protected void initView() {
    	mContext = BroadcastFragment.this.getContext();
        mFragmentBroadcastGv = (GridView) mView.findViewById(R.id.fragment_bordcast_gv);
        mFragmentBroadcastLv = (ListView) mView.findViewById(R.id.fragment_bordcast_lv);
        mCastScrollView = (ScrollView) mView.findViewById(R.id.cast_scroll_view);
        
        bordcast_ad = (ImageView) mView.findViewById(R.id.bordcast_ad);
		bordcast_lay = (RelativeLayout) mView.findViewById(R.id.bordcast_lay);
		if (!TextUtils.isEmpty(AdUtils.getAdId(mContext, 2, 0))) {
			NativeAd(AdUtils.getAdId(mContext, 2, 0));
		}else {
			hideAd();
		}
    }

    @Override
    protected void initData() {
        //解决焦点移除屏幕外,屏幕不跟着焦点滚动问题!
        mFragmentBroadcastLv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int y = (int) view.getY();
                mCastScrollView.smoothScrollTo(0, mFragmentBroadcastGv.getHeight() + y);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //填充标签的数据
        mNames = new ArrayList<>();
        List<Drawable> icons = new ArrayList<>();
        for (int i = 0; i < mArrNames.length; i++) {
            mNames.add(mArrNames[i]);
            Drawable drawable = getResources().getDrawable(mArrIcons[i]);
            icons.add(drawable);
        }

        //添加选项
        CategoryGvAdapter bordcastGvAdapter = new CategoryGvAdapter(mNames, icons);
        mFragmentBroadcastGv.setAdapter(bordcastGvAdapter);
        mFragmentBroadcastGv.setSelector(R.drawable.selector_item_gv_bg);
        mFragmentBroadcastGv.setBackgroundColor(Color.WHITE);


        //热门列表
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIO_COUNT, "10");
        CommonRequest.getRankRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(RadioList radioList) {
                Gson gson = new Gson();
                String s = gson.toJson(radioList);
                Log.i("getRadios ", s);

                //添加
                RadiosListAdapter listAdapter = new RadiosListAdapter(radioList.getRadios());
                mFragmentBroadcastLv.setAdapter(listAdapter);
                mFragmentBroadcastLv.setSelector(R.drawable.selector_focused_list_bg);
                mFragmentBroadcastLv.setBackgroundColor(Color.WHITE);
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    @Override
    protected void setListener() {
        mFragmentBroadcastGv.setOnItemClickListener(this);
        mFragmentBroadcastLv.setOnItemClickListener(this);
    }

    @Override
    protected void processClick(View v) {

    }

    /**
     * GridView导航标签  以及 热门列表的点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (!NetUtil.isNetworkConnected(UIUtils.getContext())) {
            UIUtils.showToast(UIUtils.getContext(), "无网络连接,请检查后再重试!");
            return;
        }

        //GridView导航标签的点击事件
        if (parent instanceof GridView) {

            if (position == 0) {

                String lastLocationAdCode = PreferenceUtil.getLastLocationAdCode(UIUtils.getContext());

                if (lastLocationAdCode.equals("-1")) {
                    UIUtils.showToast(UIUtils.getContext(), "正在辨别您的所在区域,请稍后再次点击!");
                    return;
                }
            }

            Intent intent = new Intent(getActivity(), BroadcastListActivity.class);
            String s = mNames.get(position);
            intent.putExtra(Constant.CAST_KEY, s);
            intent.putExtra(Constant.CAST_POSITION_KEY, position + "");
            startActivity(intent);
        }
        //ListView 列表的点击事件
        if (parent instanceof ListView) {
            ListView lv = (ListView) parent;
            RadiosListAdapter adapter = (RadiosListAdapter) lv.getAdapter();
            Radio radio = adapter.getRadios().get(position);

            //用于控制是否默认播放
            Constant.IS_REPLAY = true;

            //通过application类 传递数据
            Intent intent = new Intent(getActivity(), PlayActivity.class);
            Map<String, Object> protocolCacheMap = RadioApplication.getDeliverMap();
            protocolCacheMap.put(Constant.DeliverMap.PLAY_KEY, radio);
            startActivity(intent);
        }
    }
    
    private void NativeAd(String adId) {
		if (bordcastAd == null) {
			bordcastAd = new IFLYNativeAd(mContext, adId, listener);
		}
		int count = 1; // 一次拉取的广告条数,当前仅支持一条
		bordcastAd.loadAd(count);
	}

	private IFLYNativeListener listener = new IFLYNativeListener() {

		@Override
		public void onConfirm() {

		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onAdFailed(AdError arg0) {
			hideAd();
		}

		@Override
		public void onADLoaded(List<NativeADDataRef> arg0) {
			adItem = arg0.get(0);
			ShowAD();
		}
	};

	private void ShowAD() {
		x.image().bind(bordcast_ad, adItem.getImage(), ToolUtil.imageOptions, new CommonCallback<Drawable>() {

			@Override
			public void onSuccess(Drawable arg0) {
				bordcast_ad.setScaleType(ScaleType.FIT_XY);
				bordcast_lay.setVisibility(View.VISIBLE);
			}

			@Override
			public void onFinished() {

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				hideAd();
			}

			@Override
			public void onCancelled(CancelledException arg0) {

			}
		});
		((TextView) mView.findViewById(R.id.ad_from)).setText(adItem.getAdSourceMark() + "");
		bordcast_ad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				adItem.onClicked(v);
				//上传用户点击的广告的url
				AdUtils.clickUploading(mContext, adItem.getImage(), "1" ,"广播_横幅");
			}
		});
		// 原生广告需上传点击位置
		bordcast_ad.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					bordcastAd.setParameter(AdKeys.CLICK_POS_DX, event.getX() + "");
					bordcastAd.setParameter(AdKeys.CLICK_POS_DY, event.getY() + "");
					break;
				case MotionEvent.ACTION_UP:
					bordcastAd.setParameter(AdKeys.CLICK_POS_UX, event.getX() + "");
					bordcastAd.setParameter(AdKeys.CLICK_POS_UY, event.getY() + "");
					break;
				default:
					break;
				}
				return false;
			}
		});
		if (adItem.onExposured(mView.findViewById(R.id.bordcast_ad))) {
			
		}
	}
	
	/**
	 * 显示软件正常页面（广告结束后的）
	 */
	public void hideAd(){
		bordcast_lay.setVisibility(View.GONE);
	}
}
