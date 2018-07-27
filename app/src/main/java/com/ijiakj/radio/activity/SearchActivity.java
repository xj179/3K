package com.ijiakj.radio.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ijiakj.radio.R;
import com.ijiakj.radio.adapter.SearchPagerAdapter;
import com.ijiakj.radio.base.BaseActivity;
import com.ijiakj.radio.framework.RadioApplication;
import com.ijiakj.radio.utils.UIUtils;
import com.ijiakj.radio.widget.FlowLayout;
import com.ijiakj.radio.widget.PagerSlidingTabStrip;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SearchActivity extends BaseActivity {

    private ImageView mTitleBackIv;
    private ImageView mTitleSearchIv;
    private EditText mTitleEt;
    private LinearLayout mSearchHotLl;
    private PagerSlidingTabStrip mSearchTabs;
    private ViewPager mSearchViewpager;
    private FlowLayout mFlowLayout;
    
    @Override
    protected int getLayoutId() {
        return R.layout.activity_serach;
    }

    @Override
    protected void initView() {
        mTitleBackIv = (ImageView) findViewById(R.id.title_back_iv);
        mTitleSearchIv = (ImageView) findViewById(R.id.title_serach_iv);
        mTitleEt = (EditText) findViewById(R.id.title_et);
        mSearchHotLl = (LinearLayout) findViewById(R.id.search_hot_ll);
        mFlowLayout = (FlowLayout) findViewById(R.id.flow_layout);
        mSearchTabs = (PagerSlidingTabStrip) findViewById(R.id.search_tabs);
        mSearchViewpager = (ViewPager) findViewById(R.id.search_viewpager);
    }

    @Override
    protected void initData() {
        RadioApplication.getInstance().addActivity(this);

        mSearchTabs.setNextFocusDownId(R.id.search_viewpager);
        //获取热搜词
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TOP, "10");
        CommonRequest.getHotWords(map, new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                List<HotWord> hotWordLists = hotWordList.getHotWordList();
                //添加热词
                addHotWord(hotWordLists);
            }

            @Override
            public void onError(int i, String s) {

            }
        });

}

    /**
     * 添加热词
     *
     * @param hotWordList1
     */
    private void addHotWord(List<HotWord> hotWordList1) {
        for (int i = 0; i < hotWordList1.size(); i++) {
            //view
            final TextView tv = new TextView(UIUtils.getContext());
            //data
            final String data = hotWordList1.get(i).getSearchword();
            //data+view
            tv.setText(data);
            tv.setGravity(Gravity.CENTER);
            int padding = UIUtils.dip2px(5);
            tv.setPadding(padding, padding, padding, padding);

            tv.setTextColor(Color.WHITE);
            tv.setFocusable(true);
            //tv.setBackgroundResource(R.drawable.bg_shape_hot_tv);

            /*--------------- 通过代码去完成一个shape文件的效果 ---------------*/
            GradientDrawable normalBg = new GradientDrawable();
            //设置圆角
            normalBg.setCornerRadius(UIUtils.dip2px(8));
            //设置填充颜色
            Random random = new Random();
            int alpha = 255;
            int red = random.nextInt(170) + 30;  //30-200
            int green = random.nextInt(170) + 30;//30-200
            int blue = random.nextInt(170) + 30; //30-200
            int argb = Color.argb(alpha, red, green, blue);
            normalBg.setColor(argb);

            GradientDrawable pressedBg = new GradientDrawable();
            //设置圆角
            pressedBg.setCornerRadius(UIUtils.dip2px(8));

            //设置填充颜色
            pressedBg.setColor(getResources().getColor(R.color.text_black));

            GradientDrawable focusedBg = new GradientDrawable();
            //设置圆角
            focusedBg.setCornerRadius(UIUtils.dip2px(8));

            //设置填充颜色
            focusedBg.setColor(getResources().getColor(R.color.yellow));

            /*--------------- 通过代码完成selector文件所对应效果 ---------------*/

            StateListDrawable selectorBg = new StateListDrawable();
            // View.PRESSED_ENABLED_STATE_SET
            selectorBg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressedBg);
            // View.ENABLED_FOCUSED_STATE_SET
            selectorBg.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focusedBg);
            // View.ENABLED_STATE_SET
            selectorBg.addState(new int[]{android.R.attr.state_enabled}, normalBg);
            // View.FOCUSED_STATE_SET
            selectorBg.addState(new int[]{android.R.attr.state_focused}, focusedBg);
            // View.WINDOW_FOCUSED_STATE_SET
            selectorBg.addState(new int[]{android.R.attr.state_window_focused}, normalBg);
            // View.EMPTY_STATE_SET
            selectorBg.addState(new int[]{}, normalBg);

            tv.setBackgroundDrawable(selectorBg);

            tv.setClickable(true);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s2 = tv.getText().toString().trim();
                    mTitleEt.setText(s2);
                    //光标设置在最后
                    mTitleEt.setSelection(mTitleEt.getText().length());
                    search();
                }
            });

            mFlowLayout.addView(tv);
        }
    }

    @Override
    protected void setListener() {
        mTitleBackIv.setOnClickListener(this);
        mTitleSearchIv.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) {
        switch (v.getId()) {
            case R.id.title_back_iv:   //后退
                finish();
                break;
            case R.id.title_serach_iv: //搜索

                //1.获取搜索后的数据
                //2.显示数据
                //3.隐藏热词
                search();
                break;
            default:
                break;
        }
    }

    private void search() {
        String keyword = mTitleEt.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)){
            Toast.makeText(getApplicationContext(), "请输入关键字!", Toast.LENGTH_SHORT).show();
            return;
        }

        mSearchHotLl.setVisibility(View.GONE);
        mSearchTabs.setVisibility(View.VISIBLE);
        mSearchViewpager.setVisibility(View.VISIBLE);

        List<String> searchTabs = new ArrayList<>();
        searchTabs.add("专辑");
        searchTabs.add("广播");

        // Toast.makeText(getApplicationContext(), keyword, Toast.LENGTH_SHORT).show();

        //设置搜集结果的列表适配器
        SearchPagerAdapter searchPagerAdapter = new SearchPagerAdapter(this, searchTabs, keyword);
        mSearchViewpager.setAdapter(searchPagerAdapter);
        mSearchTabs.setViewPager(mSearchViewpager);

        //关闭输入法
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mTitleEt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


}
