package com.ijiakj.radio.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ijiakj.radio.R;
import com.ijiakj.radio.framework.RadioApplication;

import java.util.List;

/**
 * 创建者     曹自飞
 * 创建时间   2016/8/31 0031 11:10
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class CategoryGvAdapter extends BaseAdapter {
    private List<String> mNames;
    private List<Drawable> mIcons;

    public CategoryGvAdapter(List<String> names, List<Drawable> icons) {
        mNames = names;
        mIcons = icons;
    }

    @Override
    public int getCount() {
        return mNames == null ? 0 : mNames.size();
    }

    @Override
    public Object getItem(int position) {
        return mNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(RadioApplication.getContext()).inflate(R.layout.item_category_gv, null);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.item_broadcast_iv);
            holder.tv = (TextView) convertView.findViewById(R.id.item_broadcast_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.iv.setImageDrawable(mIcons.get(position));
        holder.tv.setText(mNames.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView tv;
        ImageView iv;
    }
}



