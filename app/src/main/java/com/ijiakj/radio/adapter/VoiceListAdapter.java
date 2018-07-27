package com.ijiakj.radio.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ijiakj.radio.R;
import com.ijiakj.radio.utils.ToolUtil;
import com.ijiakj.radio.utils.UIUtils;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者     曹自飞
 * 创建时间   2016/9/1 0001 15:56
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class VoiceListAdapter extends BaseAdapter {
    private List<Track> mDatas;
    private Context mContext;
    private final XmPlayerManager mXmPlayerManager;
    private AnimationDrawable mCurrentAnim;


    public VoiceListAdapter(Context context, List<Track> tracks) {
        mDatas = tracks;
        mContext = context;
        mXmPlayerManager = XmPlayerManager.getInstance(mContext);
    }

    public void refreshData(List<Track> albums) {
        mDatas = albums;
    }

    public List<Track> getTracks() {
        return mDatas;
    }

    public void addTracks(List<Track> albums) {
        mDatas.addAll(albums);
    }

    public void inverted() {
        List<Track> list = new ArrayList();
        list.addAll(mDatas);
        mDatas.clear();
        for (int i = list.size() - 1; i >= 0; i--) {
            mDatas.add(list.get(i));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_voice_lv, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.item_voice_fluctuate);
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_voice_name);
            viewHolder.time = (TextView) convertView.findViewById(R.id.item_voice_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //是否显示波动器
        int currentIndex = mXmPlayerManager.getCurrentIndex();
        if (currentIndex == position) {
            //此处必须先显示再控制开停,否则无效!
            viewHolder.icon.setVisibility(View.VISIBLE);

            viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            viewHolder.time.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            mCurrentAnim = (AnimationDrawable) viewHolder.icon.getDrawable();

            //控制波动器的启动与停止
            if (mXmPlayerManager.isPlaying()) {
                mCurrentAnim.start();
            } else {
                mCurrentAnim.stop();
            }

        } else {
            viewHolder.icon.setVisibility(View.INVISIBLE);
            viewHolder.name.setTextColor(Color.BLACK);
            viewHolder.time.setTextColor(Color.BLACK);
        }

        Track track = mDatas.get(position);
        viewHolder.name.setText(track.getTrackTitle());
        viewHolder.time.setText(ToolUtil.formatTime(track.getDuration() * 1000));

        return convertView;
    }

    public void startAnim() {
        if (mCurrentAnim != null) {
            mCurrentAnim.start();
        }
    }

    public void stopAnim() {
        if (mCurrentAnim != null) {
            mCurrentAnim.stop();
        }
    }

    class ViewHolder {
        public ImageView icon;
        public TextView name;
        public TextView time;
    }
}
