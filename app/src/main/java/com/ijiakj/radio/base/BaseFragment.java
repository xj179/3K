package com.ijiakj.radio.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ijiakj.radio.R;

/**
 * 创建者     曹自飞
 * 创建时间   2016/8/30 0030 18:17
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    protected  View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(), null);
        initView();
        initData();
        setListener();
        return mView;
    }

    protected abstract int getLayoutId();


    protected abstract void initView();


    protected abstract void initData();


    protected abstract void setListener();

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            //后退的前面一个fragment
            getFragmentManager().popBackStack();
        } else {
            processClick(v);
        }
    }

    //处理公用按钮之外的点击事件
    protected abstract void processClick(View v);
    
    private Toast mToast;

	public void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}
}
