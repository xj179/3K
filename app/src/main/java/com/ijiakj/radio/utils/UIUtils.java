package com.ijiakj.radio.utils;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import com.ijiakj.radio.framework.RadioApplication;


public class UIUtils {
    /**
     * 得到上下文
     */
    public static Context getContext() {
        return RadioApplication.getContext();
    }

    /**
     * 得到Resource对象
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 得到String.xml中的字符信息
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 得到String.xml中的字符数组信息
     */
    public static String[] getStrings(int resId) {
        return getResources().getStringArray(resId);
    }

    /**
     * 得到color.xml中的颜色信息
     */
    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    /**
     * 得到应用程序的包名
     *
     * @return
     */
    public static String getPackageName() {
        return getContext().getPackageName();
    }

    /**
     * dip-->px
     *
     * @param dip
     * @return
     */
    public static int dip2px(int dip) {
        //1. px/(ppi/160) = dp
        //2. px/dip = density;

        /*
        240x320  ppi=120
        320x480  ppi=160  1px=1dp
        480x800  ppi=240  1.5px=1dp
        720x1280 ppi=320  2px=1dp

         */


        float density = getResources().getDisplayMetrics().density;
//       float densityDpi = getResources().getDisplayMetrics().densityDpi;
        int px = (int) (dip * density + .5f);
        return px;
    }

    public static int px2Dip(int px) {
        float density = getResources().getDisplayMetrics().density;
        // px/dip = density;
        int dip = (int) (px / density + .5f);
        return dip;
    }

    private static Toast toast;

    public static void showToast(Context context,
                                 String content) {
        if (toast == null) {
            toast = Toast.makeText(context,
                    content,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
