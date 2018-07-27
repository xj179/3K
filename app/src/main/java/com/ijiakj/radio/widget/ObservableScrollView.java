package com.ijiakj.radio.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * @author 作者 :Pater
 * @version 创建时间：2017年5月10日 上午11:50:59 类说明
 */
public class ObservableScrollView extends ScrollView {

	private OnScrollBottomListener _listener;
	private int _calCount;

	public interface OnScrollBottomListener {
		void srollToBottom();
	}

	public void registerOnScrollViewScrollToBottom(OnScrollBottomListener l) {
		_listener = l;
	}

	public void unRegisterOnScrollViewScrollToBottom() {
		_listener = null;
	}

	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		View view = this.getChildAt(0);
		if (this.getHeight() + this.getScrollY() == view.getHeight()) {
			_calCount++;
			if (_calCount == 1) {
				if (_listener != null) {
					_listener.srollToBottom();
				}
			}
		} else {
			_calCount = 0;
		}
	}
}
