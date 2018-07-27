package com.ijiakj.radio.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ijiakj.radio.R;
import com.ijiakj.radio.adapter.VoiceListAdapter;
import com.ijiakj.radio.framework.Constant;
import com.ijiakj.radio.framework.RadioApplication;
import com.ijiakj.radio.net.NetUtil;
import com.ijiakj.radio.utils.PreferenceUtil;
import com.ijiakj.radio.utils.UIUtils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 创建者     曹自飞
 * 创建时间   2016/9/9 0009 10:55
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class VoiceListPopupWindow extends PopupWindow {
    private TextView mEmpty;
    private TextView mListShowOrder;
    private int mPage = 1;
    private TextView btn_cancel;
    private View mMenuView;
    private XListView mVoiceListLv;
    private boolean mIsLoadMore = false;
    private boolean mIsRefresh = false;
    private TextView mPlayMode;
    private int mPlayMode1;
    private String mOrder = "asc";   //asc正序或desc倒序，默认为asc正序
    private XmPlayerManager mXmPlayerManager;

    public VoiceListPopupWindow(Activity context, List<Track> tracks) {
        super(context);
        mXmPlayerManager = XmPlayerManager.getInstance(context);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.view_list_dialog, null);

        mPlayMode = (TextView) mMenuView.findViewById(R.id.play_mode);
        mListShowOrder = (TextView) mMenuView.findViewById(R.id.list_show_order);
        btn_cancel = (TextView) mMenuView.findViewById(R.id.btn_cancel);
        mVoiceListLv = (XListView) mMenuView.findViewById(R.id.voice_list_lv);
        mEmpty = (TextView) mMenuView.findViewById(R.id.empty);
        mEmpty.setText("正在加载中...");
        mVoiceListLv.setEmptyView(mEmpty);

        List<Track> playList = mXmPlayerManager.getPlayList();
        //给列表设置适配器
        final VoiceListAdapter adapter = new VoiceListAdapter(context, playList);

        // loadTrackList(mVoiceListLv, adapter, mOrder);
        mVoiceListLv.setAdapter(adapter);
        mVoiceListLv.setSelector(R.drawable.selector_focused_list_bg);
        mVoiceListLv.setBackgroundColor(Color.WHITE);
        mVoiceListLv.setPullRefreshEnable(true);
        if (playList.size()<3) {
        	mVoiceListLv.setPullLoadEnable(false);
		}else {
			mVoiceListLv.setPullLoadEnable(true);
		}
        mVoiceListLv.setAutoLoadEnable(true);
        mVoiceListLv.setRefreshTime(getTime());

        //初始化播放模式
        mPlayMode1 = PreferenceUtil.getPlayMode(context, 3);
        switchPalyMode(mXmPlayerManager);

        //显示当前播放的声音
        int currentIndex = mXmPlayerManager.getCurrentIndex();
        mVoiceListLv.setSelection(currentIndex);

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.mystyle);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        //各种事件监听
        setListener(context, adapter);
    }

    /**
     * PopupWindow的各种监听事件
     *
     * @param context
     * @param adapter
     */
    private void setListener(final Context context, final VoiceListAdapter adapter) {
        mVoiceListLv.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                mIsRefresh = true;
                loadTrackList(mVoiceListLv, adapter, mOrder);
            }

            @Override
            public void onLoadMore() {
                loadMoreData(adapter);
            }
        });

        //声音列表的
        mVoiceListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!NetUtil.isNetworkConnected(UIUtils.getContext())) {
                    UIUtils.showToast(UIUtils.getContext(), "无网络连接,请检查后再重试!");
                    return;
                }

                //每次点击刷新适配器
                ListView listView = (ListView) parent;
                HeaderViewListAdapter adapter = (HeaderViewListAdapter) listView.getAdapter();
                final VoiceListAdapter wrappedAdapter = (VoiceListAdapter) adapter.getWrappedAdapter();
                wrappedAdapter.notifyDataSetChanged();

                if (position >= wrappedAdapter.getCount()) {
                    //点击了最后一条,需要加载更多.
                    loadMoreData(wrappedAdapter);
                }
                mXmPlayerManager.play(position - 1);
            }
        });

        mXmPlayerManager.addPlayerStatusListener(new IXmPlayerStatusListener() {

            @Override
            public void onPlayStart() {
                adapter.startAnim();
                adapter.notifyDataSetChanged();
                //  PreferenceUtil.setCurrAlbum(UIUtils.getContext(), mAlbum);
            }

            @Override
            public void onPlayPause() {
                adapter.stopAnim();
            }

            @Override
            public void onPlayStop() {
                adapter.stopAnim();
            }

            @Override
            public void onSoundPlayComplete() {

            }

            @Override
            public void onSoundPrepared() {

            }

            @Override
            public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {

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
            public void onPlayProgress(int i, int i1) {

            }

            @Override
            public boolean onError(XmPlayerException e) {
                return false;
            }
        });

        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        //取消按钮
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //销毁弹出框
                dismiss();
            }
        });

        mPlayMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayMode1 == 5) {
                    mPlayMode1 = 1;
                } else {
                    mPlayMode1++;
                }

                PreferenceUtil.setPlayMode(context, mPlayMode1);

       /* 设置播放器模式，mode 取值为 PlayMode 中的下列之一：
        PLAY_MODEL_SINGLE 单曲播放
        PLAY_MODEL_SINGLE_LOOP 单曲循环播放
        PLAY_MODEL_LIST 列表播放
        PLAY_MODEL_LIST_LOOP 列表循环
        PLAY_MODEL_RANDOM 随机播放*/

                switchPalyMode(mXmPlayerManager);
            }
        });


        mListShowOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //asc正序或desc倒序，默认为asc正序
                if (mOrder.equals("asc")) {
                    mOrder = "desc";
                } else {
                    mOrder = "asc";
                }
                // adapter.inverted();
                mIsRefresh = true;
                mPage = 1;
                loadTrackList(mVoiceListLv, adapter, mOrder);
            }
        });
    }

    private void loadMoreData(VoiceListAdapter adapter) {
        mVoiceListLv.setPullLoadEnable(false);
        mIsLoadMore = true;
        List<Track> playList = mXmPlayerManager.getPlayList();

        if (playList.size() % 20 != 0) {
            onLoaded(mVoiceListLv);
            return;
        } else {
            mPage = (playList.size() / 20 + 1);
        }

        loadTrackList(mVoiceListLv, adapter, mOrder);
    }

    private void switchPalyMode(XmPlayerManager xmPlayerManager) {
        switch (mPlayMode1) {
            case 1: // PLAY_MODEL_SINGLE 单曲播放
                xmPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE);
                mPlayMode.setText("单曲播放");
                break;// PLAY_MODEL_SINGLE_LOOP 单曲循环播放
            case 2:
                xmPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP);
                mPlayMode.setText("单曲循环播放");
                break;// PLAY_MODEL_LIST 列表播放
            case 3:
                xmPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST);
                mPlayMode.setText("列表播放");
                break;// PLAY_MODEL_LIST_LOOP 列表循环
            case 4:
                xmPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
                mPlayMode.setText("列表循环播放");
                break;// PLAY_MODEL_RANDOM 随机播放
            case 5:
                xmPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM);
                mPlayMode.setText("随机播放");
                break;

            default:
                break;
        }
    }

    /**
     * 加载声音列表数据
     *
     * @param xListView
     * @param adapter
     * @param order
     */
    public void loadTrackList(final XListView xListView, final VoiceListAdapter adapter, String order) {
        Object o = RadioApplication.getDeliverMap().get(Constant.DeliverMap.PLAY_KEY);

        if (o == null) {
            //内存中没有就去sp取
            o = PreferenceUtil.getCurrAlbum(UIUtils.getContext());
        }
        if (o instanceof Album) {
            Album album = (Album) o;
            //获取声音列表id
            Map<String, String> map = new HashMap<String, String>();
            map.put(DTransferConstants.ALBUM_ID, String.valueOf(album.getId()));
            map.put(DTransferConstants.SORT, order);
            map.put(DTransferConstants.PAGE, String.valueOf(mPage));
            CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
                @Override
                public void onSuccess(TrackList trackList) {
                    if (trackList.getTotalPage() <= trackList.getCurrentPage()) {
                        xListView.setPullLoadEnable(false);
                    } else {
                        xListView.setPullLoadEnable(true);
                    }
                    if (mIsLoadMore) {
                        //加载更多数据
                        if (trackList.getTracks().size() == 0) {
                            UIUtils.showToast(UIUtils.getContext(), "没有更多数据了!");
                        }

                        List<Track> playList = mXmPlayerManager.getPlayList();

                        playList.addAll(trackList.getTracks());
                        mXmPlayerManager.playList(playList, mXmPlayerManager.getCurrentIndex());

                        adapter.addTracks(trackList.getTracks());

                        adapter.notifyDataSetChanged();
                        mIsLoadMore = false;
                    } else if (mIsRefresh) {
                        //下拉刷新数据
                        List<Track> playList = mXmPlayerManager.getPlayList();
                        adapter.refreshData(playList);
                        adapter.notifyDataSetChanged();
                        mIsRefresh = false;
                    }

                    // mPage++;
                    onLoaded(xListView);
                }

                @Override
                public void onError(int i, String s) {
                    xListView.setPullLoadEnable(true);
                    mEmpty.setText("数据加载失败!");
                }
            });
        } else {
            //获取在播放的电台
            onLoaded(xListView);
            xListView.setPullLoadEnable(false);
            List<Track> playList = mXmPlayerManager.getPlayList();
            adapter.refreshData(playList);
            adapter.notifyDataSetChanged();
        }

    }

    private void onLoaded(XListView xLv) {
        xLv.stopRefresh();
        xLv.stopLoadMore();
        xLv.setRefreshTime(getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }
}
