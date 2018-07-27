package com.ijiakj.radio.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ijiakj.radio.R;
import com.ijiakj.radio.utils.UIUtils;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

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
public class RadiosListAdapter extends BaseAdapter {
    private List<Radio> mDatas;

    public RadiosListAdapter() {
    }

    public RadiosListAdapter(List radios) {
        mDatas = radios;
    }

    public void refreshData(List<Radio> radios) {
        mDatas = radios;
    }

    public List<Radio> getRadios() {
        return mDatas;
    }

    public void addRadios(List<Radio> radios) {
        mDatas.addAll(radios);
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
            convertView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_common_lv, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.item_common_icon_iv);
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_common_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Radio radio = mDatas.get(position);
        String coverUrlSmall = radio.getCoverUrlSmall();

        if (coverUrlSmall == null || coverUrlSmall.length() <= 0) {
            viewHolder.icon.setImageResource(R.drawable.default_ic);
        } else {
            Picasso.with(UIUtils.getContext()).load(coverUrlSmall).placeholder(R.drawable.min_default).error(R.drawable.min_default).into(viewHolder.icon);
        }
        viewHolder.name.setText(radio.getRadioName());

        return convertView;
    }

    class ViewHolder {
        public ImageView icon;
        public TextView name;

    }
}
