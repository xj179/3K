package com.ijiakj.radio.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DensityUtil {  
	
	private static int MODE = Context.MODE_MULTI_PROCESS;// 定义访问模式为私有模式
	  
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;
        System.out.println("scale==== "+ scale );
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    
    
    /**
     * 将sp值转换为px值，保证文字大小不变
     * 
     * @param spValue
     * @param
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */ 
    public static int sp2px(Context context, float spValue) { 
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (spValue * fontScale + 0.5f); 
    } 
    
    /**保存点击稍后更新的时间*/
	public static void setLastClick(Context context,String time) {
		Editor editor = context.getSharedPreferences(
				"lastclickdate", MODE).edit();
		editor.putString("lastclickdatef", time); 
		editor.commit();
	}
	
	/**获得点击稍后更新的时间*/
	public static String getLastClick(Context context) {
		SharedPreferences share = context.getApplicationContext().getSharedPreferences(
				"lastclickdate", MODE);
		String time = "-1";
		time = share.getString("lastclickdatef", "-1");
		return time;
	}

    public static String getDate(){//获得当前日期
        SimpleDateFormat formatter = new SimpleDateFormat ("dd");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }
}  
