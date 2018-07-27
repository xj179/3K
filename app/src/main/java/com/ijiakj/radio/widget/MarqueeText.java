package com.ijiakj.radio.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 创建者     曹自飞
 * 创建时间   2016/10/28 0028 10:13
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
    public class MarqueeText extends TextView {
        public MarqueeText(Context con) {
            super(con);
        }
        public MarqueeText(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
        public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
        @Override
        public boolean isFocused() {
            return true;
        }
        @Override
        protected void onFocusChanged(boolean focused, int direction,
                                      Rect previouslyFocusedRect) {
        }
    }
