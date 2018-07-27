package com.ijiakj.radio.widget;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xutils.x;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ijiakj.radio.R;
import com.ijiakj.radio.activity.PlayActivity;
import com.ijiakj.radio.activity.WebActivity;
import com.ijiakj.radio.base.BaseHolder;
import com.ijiakj.radio.bean.BanerBean;
import com.ijiakj.radio.framework.Constant;
import com.ijiakj.radio.framework.RadioApplication;
import com.ijiakj.radio.utils.UIUtils;


/**
 * 自动轮播图
 */
public class HomePictureHolder extends BaseHolder<Object> {
    private List<BanerBean.AdvertisingEntity> mPictures = new ArrayList<>();
    private LinearLayout mItemHomePictureContainerIndicator;
    private ViewPager mItemHomePicturePager;
    private Context mContext;

    public HomePictureHolder(Context mContext) {
    	this.mContext = mContext;
    }

    /**
     * @return
     * @des 决定HomePictureHolder所能提供的视图长什么样子
     */
    @Override
    public View initHolderView() {
        View holderView = View.inflate(RadioApplication.getContext(), R.layout.item_home_pictures, null);

        mItemHomePicturePager = (ViewPager) holderView.findViewById(R.id.item_home_picture_pager);
        mItemHomePictureContainerIndicator = (LinearLayout) holderView.findViewById(R.id.item_home_picture_container_indicator);
        // ButterKnife.inject(this, holderView);

        return holderView;
    }

    /**
     * @param
     * @des 数据和视图的具体绑定
     */
    @SuppressWarnings("deprecation")
	@Override
    public void refreshHolderView(Object o) {

        //保存局变量到成员变量中

        BanerBean bannerBean = (BanerBean) o;

        mPictures = bannerBean.advertising;
        HomePicturePagerAdapter ada = new HomePicturePagerAdapter();
        //为mItemHomePicturePager绑定数据
        mItemHomePicturePager.setAdapter(ada);
        if (mPictures==null) {
			return;
		}

        // 为mItemHomePictureContainerIndicator绑定数据
        for (int i = 0; i < mPictures.size(); i++) {
            ImageView ivIndicator = new ImageView(UIUtils.getContext());
            ivIndicator.setImageResource(R.drawable.indicator_white_shape);
            //默认选中第一页
            if (i == 0) {
                ivIndicator.setImageResource(R.drawable.indicator_blue_shape);
            }
            int width = UIUtils.dip2px(10);
            int height = UIUtils.dip2px(10);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            params.leftMargin = UIUtils.dip2px(6);
            //params.bottomMargin = UIUtils.dip2px(6);

            mItemHomePictureContainerIndicator.addView(ivIndicator, params);
        }

        //滑动的时候切换indicator
        mItemHomePicturePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                position = position % mPictures.size();
                //切换效果
                for (int i = 0; i < mPictures.size(); i++) {
                    ImageView ivIndicator = (ImageView) mItemHomePictureContainerIndicator.getChildAt(i);
                    //1.还原所有的效果
                    ivIndicator.setImageResource(R.drawable.indicator_white_shape);
                    //2.选中应该选中的效果
                    if (i == position) {
                        ivIndicator.setImageResource(R.drawable.indicator_blue_shape);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //设置viewpager默认选中最大值的中间值
        int index = 10 / 2;

        if (mPictures.size() != 0) {
        	try {
        		int diff = 10 / 2 % mPictures.size();
                index = index - diff;
                Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
                mFirstLayout.setAccessible(true);
                mFirstLayout.set(mItemHomePicturePager, true);
                mItemHomePicturePager.setCurrentItem(index);
			} catch (Exception e) {
				// TODO: handle exception
			}
        }


        //完成自动轮播
        final AutoScrollTask autoScrollTask = new AutoScrollTask();
        autoScrollTask.start();
        //按下去的时候停止轮播
        mItemHomePicturePager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        autoScrollTask.stop();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        autoScrollTask.start();
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }

    class AutoScrollTask implements Runnable {
        /**
         * 开始轮播
         */
        public void start() {
            //先remove一下
            RadioApplication.getMainThreadHandler().removeCallbacks(this);

            RadioApplication.getMainThreadHandler().postDelayed(this, 3000);
        }

        /**
         * 停止轮播
         */
        public void stop() {
            RadioApplication.getMainThreadHandler().removeCallbacks(this);
        }

        @Override
        public void run() {
            //切换viewpager到下一页
            int currentItem = mItemHomePicturePager.getCurrentItem();
            currentItem++;
            //完成切换
            mItemHomePicturePager.setCurrentItem(currentItem);

            //再次调用start
            start();
        }
    }

    class HomePicturePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mPictures != null) {
                //return mPictures.size();
                return Integer.MAX_VALUE;
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            //position = Integer.MAX_VALUE / 2;
            position = position % mPictures.size();
            //view
            ImageView iv = new ImageView(UIUtils.getContext());

            //设置scaleType
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

            iv.setFocusable(true);

            //图片的加载
            String url = mPictures.get(position).advImg;
            if (url != null && url != "") {
//                Picasso.with(UIUtils.getContext()).load(url).placeholder(R.drawable.default_banner).error(R.drawable.default_banner).into(iv);
                x.image().bind(iv, url);
            } else {
                iv.setImageResource(R.drawable.default_banner);
            }

            /**
             * 轮播图图片的点击事件
             */
            final int finalPosition = position;
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BanerBean.AdvertisingEntity advertisingEntity = mPictures.get(finalPosition);

                    switch (advertisingEntity.advType) {
                        //    0：文本广告；1：web广告;5:应用广告;6:电台
                        case "0":
                        case "1":
                        case "5":
                            //open_type 0：浏览器打开;1：内部webview打开
                            if (advertisingEntity.openType == "0") {
                                //浏览器
                                jumpToBrowser(advertisingEntity);
                            } else {
                                //获取到web地址用webView打开
                                jumpToWebView(advertisingEntity);
                            }
                            break;
                        case "6":
                            //跳转到播放界面,更加专辑id 播放声音
                            jumpToPlay(advertisingEntity);
                            break;
                        default:
                            break;
                    }
                }
            });

            //加入容器
			container.addView(iv);
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * 跳转到浏览器
     *
     * @param advertisingEntity
     */
    private void jumpToBrowser(BanerBean.AdvertisingEntity advertisingEntity) {
        if (advertisingEntity.webUrl == null || advertisingEntity.webUrl.equals("")) {
            return;
        }

        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(advertisingEntity.webUrl);
        intent.setData(content_url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        UIUtils.getContext().startActivity(intent);
    }

    /**
     * 跳转到webView
     *
     * @param advertisingEntity
     */
    private void jumpToWebView(BanerBean.AdvertisingEntity advertisingEntity) {
        if (advertisingEntity.webUrl == null || advertisingEntity.webUrl.equals("")) {
            return;
        }
        Intent intent = new Intent(UIUtils.getContext(), WebActivity.class);
        intent.putExtra(Constant.WEB_URL, advertisingEntity.webUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        UIUtils.getContext().startActivity(intent);
    }

    /**
     * 跳转到播放界面
     *
     * @param advertisingEntity
     */
    private void jumpToPlay(BanerBean.AdvertisingEntity advertisingEntity) {
        Intent intent = new Intent(UIUtils.getContext(), PlayActivity.class);
        Map<String, Object> deliverMap = RadioApplication.getDeliverMap();
        deliverMap.put(Constant.DeliverMap.OTHERID, advertisingEntity.otherId);
        Constant.IS_BANNER_PLAYE = true;
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        UIUtils.getContext().startActivity(intent);
    }

}

