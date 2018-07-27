package com.ijiakj.radio.framework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 创建者     曹自飞
 * 创建时间   2016/11/10 0010 18:46
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class CloseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Constant.CLOSE_APP_ALL)) {
            //关闭应用
            RadioApplication.getInstance().exit();
        }
    }
}

