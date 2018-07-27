package com.ijiakj.radio.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.ijiakj.radio.R;
import com.ijiakj.radio.framework.Constant;
import com.ijiakj.radio.framework.RadioApplication;
import com.ijiakj.radio.utils.AdUtils;
import com.ijiakj.radio.utils.UIUtils;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;
import java.util.Random;

import org.xutils.x;

/**
 * 创建者 曹自飞 创建时间 2016/9/1 0001 15:56 描述 ${TODO}
 * <p/>
 * 更新者 $Author$ 更新时间 $Date$ 更新描述 ${TODO}
 */
public class CommonListAdapter extends BaseAdapter {
	private List<Album> mDatas;
	private int loc;
	private Context mContext;

	public CommonListAdapter(Context context) {
		mContext = context;
	}

	public CommonListAdapter(List albums, Context context) {
		mDatas = albums;
		mContext = context;
	}

	public void refreshData(List<Album> albums, int loc) {
		mDatas = albums;
		this.loc = loc;
		addAd();
	}

	public void addAubms(List<Album> albums, int loc) {
		this.loc = loc;
		if (mDatas != null && albums.size() > 0) {
			mDatas.addAll(albums);
			addAd();
			notifyDataSetChanged();
		}
	}

	private void addAd() {
		String num = RadioApplication.getDeliverMap().get(Constant.DeliverMap.CATEGORY_KEY_LOCID)+"";
		if (!TextUtils.isEmpty(num)&&!num.equals("null")) {
			String adId = AdUtils.getAdId(mContext, 3, Integer.valueOf(num).intValue());
			if (!TextUtils.isEmpty(adId)) {
				if (loc == 0 && mDatas.get(6).getId() != Constant.DATA_ID_FLAG) {
					Album object = new Album();
					object.setId(Constant.DATA_ID_FLAG);
					mDatas.add(6, object);
				}
			}
		}
	}

	public List<Album> getAubms() {
		return mDatas;
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
			viewHolder.item_lay = (RelativeLayout) convertView.findViewById(R.id.item_lay);
			viewHolder.play = (ImageView) convertView.findViewById(R.id.item_common_iv);
			viewHolder.name = (TextView) convertView.findViewById(R.id.item_common_tv);
			viewHolder.ad_tag = (TextView) convertView.findViewById(R.id.ad_tag);
			viewHolder.ad_from = (TextView) convertView.findViewById(R.id.ad_from);
			viewHolder.view = convertView;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Album album = mDatas.get(position);
		if (loc == 0 && album.getId() == Constant.DATA_ID_FLAG) {
			NativeAd(viewHolder);
			viewHolder.item_lay.setVisibility(View.GONE);
			viewHolder.play.setVisibility(View.GONE);
			viewHolder.ad_tag.setVisibility(View.VISIBLE);
			viewHolder.ad_from.setVisibility(View.VISIBLE);
			viewHolder.icon.setImageResource(R.drawable.min_default);
			viewHolder.name.setText(RadioApplication.getDeliverMap().get(Constant.DeliverMap.CATEGORY_KEY_NAME) + ""
					+ RadioApplication.getDeliverMap().get(Constant.DeliverMap.CATEGORY_KEY_LOCID));
			viewHolder.view.setTag(R.string.commonlistadapter_tag, Constant.DATA_ID_FLAG);
			RadioApplication.getDeliverMap().put(Constant.DeliverMap.NATIVEADDATAREF_AD, adItem);
		} else {
			String coverUrlSmall = album.getCoverUrlSmall();
			viewHolder.item_lay.setVisibility(View.VISIBLE);
			viewHolder.play.setVisibility(View.VISIBLE);
			viewHolder.ad_tag.setVisibility(View.GONE);
			viewHolder.ad_from.setVisibility(View.GONE);
			if (coverUrlSmall == null || coverUrlSmall.length() <= 0) {
				viewHolder.icon.setImageResource(R.drawable.min_default);
			} else {
				Picasso.with(UIUtils.getContext()).load(coverUrlSmall).placeholder(R.drawable.min_default).error(R.drawable.min_default)
						.into(viewHolder.icon);
			}
			viewHolder.name.setText(album.getAlbumTitle());
			convertView.setTag(R.string.commonlistadapter_tag, 1111111);
		}
		return convertView;
	}

	class ViewHolder {
		public ImageView icon;
		public TextView name;
		public ImageView play;
		public View view;
		public TextView ad_tag;
		public TextView ad_from;
		public RelativeLayout item_lay;
	}

	private IFLYNativeAd nativeAd;
	private NativeADDataRef adItem;
	private ViewHolder holder;

	private void NativeAd(ViewHolder holder) {
		String adId = AdUtils.getAdId(mContext, 3, (int) RadioApplication.getDeliverMap().get(Constant.DeliverMap.CATEGORY_KEY_LOCID));
		this.holder = holder;
		if (nativeAd == null) {
			nativeAd = new IFLYNativeAd(mContext, adId, listener);
		}
		int count = 1; // 一次拉取的广告条数,当前仅支持一条
		nativeAd.loadAd(count);
		holder.view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					nativeAd.setParameter(AdKeys.CLICK_POS_DX, event.getX() + "");
					nativeAd.setParameter(AdKeys.CLICK_POS_DY, event.getY() + "");
					break;
				case MotionEvent.ACTION_UP:
					nativeAd.setParameter(AdKeys.CLICK_POS_UX, event.getX() + "");
					nativeAd.setParameter(AdKeys.CLICK_POS_UY, event.getY() + "");
					break;
				default:
					break;
				}
				return false;
			}
		});
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
		}

		@Override
		public void onADLoaded(List<NativeADDataRef> arg0) {
			if ((int) holder.view.getTag(R.string.commonlistadapter_tag) == Constant.DATA_ID_FLAG) {
				adItem = arg0.get(0);
				Picasso.with(UIUtils.getContext()).load(adItem.getImage()).placeholder(R.drawable.min_default)
						.error(R.drawable.min_default).into(holder.icon);
				(holder.name).setText(adItem.getTitle());
				holder.ad_from.setText(adItem.getAdSourceMark());
				holder.item_lay.setVisibility(View.VISIBLE);
				RadioApplication.getDeliverMap().put(Constant.DeliverMap.COMM_IMAG_URL, adItem.getImage() + "");
			}
		}
	};
}
