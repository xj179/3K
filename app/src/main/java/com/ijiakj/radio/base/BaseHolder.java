package com.ijiakj.radio.base;

import android.view.View;


public abstract class BaseHolder<HOLDEBEANTYPE> {
    public View mHolderView;
    public HOLDEBEANTYPE mData;

    //创建ViewHolder
    public BaseHolder() {
        mHolderView = initHolderView();

        //初始化ViewHolder持有的对象-->到时候子类自行实现

        //找一个holder对象,绑定得到根视图身上
        mHolderView.setTag(this);
    }



    public void setDataAndRefreshHolderView(HOLDEBEANTYPE data) {
        //保存数据到成员变量
        mData = data;

        refreshHolderView(data);
    }

    //子类提供具体View
    public abstract View initHolderView();


    public abstract void refreshHolderView(HOLDEBEANTYPE data);
}
