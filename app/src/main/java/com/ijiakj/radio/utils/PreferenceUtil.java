package com.ijiakj.radio.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.ximalaya.ting.android.opensdk.model.album.Album;

public class PreferenceUtil {

    private static int MODE = Context.MODE_PRIVATE;// 定义访问模式为私有模式
    private static String PREFERENCENAME = "DPOPREFERENCE";
    public static String BINGACTIVE = "BINGACTIVE"; // 激活状态

    public static final String IS_FIRST = "is_first"; // 是否第一次启动
    public static final String IS_START_BUID_BIND = "is_start_buid_bind"; // 是否要启动引导绑定Activity
    public static final String IS_LOGIN = "is_login"; // 是否登录过，为true代表登录过。退出登录的时候把数据设置为false，并且清空数据表
    public static final String PREVIOUS_LOCA = "PREVIOUS_LOCA"; // 29.806651,121.606983（经度，纬度，以逗号分隔）上一闪所在的地理位置，设置百度地图进去显示的是上一次自己所在的地理位置


    /**
     * 是否是第一次启动
     *
     * @param context
     * @param key
     * @return
     */
    public static Boolean getIsFirstStart(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(
                PREFERENCENAME, MODE);
        return settings.getBoolean(key, true);
    }


    /**
     * 获取激活状态
     */
    public static String getBindActive(Context context) {
        SharedPreferences settings = context.getSharedPreferences(
                PREFERENCENAME, MODE);

        // return Constant.testBindActivePhone ; //test
        return settings.getString(BINGACTIVE, null);
    }


    /**
     * 获取id
     *
     * @return
     */
    public static String getId(Context context) {
        String strId = PreferenceUtil.getBindActive(context);
        if (TextUtils.isEmpty(strId)) {
            return "";
        } else {
            String id = "";
            String[] temp = strId.split(",");
            if (temp.length == 3) {
                id = temp[1];
            } else if (temp.length == 2) {
                id = temp[1];
            }
            // return Constant.testChildId ; //test
            return id;
        }
    }

    /**
     * 获取phone
     *
     * @return
     */
    public static String getPhone(Context context) {
        String strId = PreferenceUtil.getBindActive(context);
        if (TextUtils.isEmpty(strId)) {
            return "";
        } else {
            String id = "";
            String[] temp = strId.split(",");
            if (temp.length == 3) {
                id = temp[0];
            }
            // return Constant.testChildId ; //test
            return id;
        }
    }

    /**
     * 保存单曲album数据到sp中
     *
     * @param context
     * @param album
     */
    public static void setCurrAlbum(Context context, Album album) {
        SharedPreferences sp = context.getSharedPreferences("current_album_data", MODE);
        Editor edit = sp.edit();
        if (album == null) {
            edit.putString("CURRENT_ALBUM_DATA", null);
        } else {
            Gson gson = new Gson();
            String jsonAlbum = gson.toJson(album);
            edit.putString("CURRENT_ALBUM_DATA", jsonAlbum);
        }
        edit.commit();
    }

    /**
     * sp中获取当前album
     *
     * @param context
     * @return
     */
    public static Album getCurrAlbum(Context context) {
        SharedPreferences sp = context.getSharedPreferences("current_album_data", MODE);
        String jsonAlbum = sp.getString("CURRENT_ALBUM_DATA", "");
        if (jsonAlbum == null || jsonAlbum.equals("")) {
            return null;
        } else {
            Gson gson = new Gson();
            Album album = gson.fromJson(jsonAlbum, Album.class);
            return album;
        }

    }

    /**
     * 保存最后播放的声音
     *
     * @param context
     * @param jsonSound
     */
    public static void setLastPlay(Context context, String jsonSound, int palyType, int index) {
        SharedPreferences sp = context.getSharedPreferences("last_play_sound", MODE);
        Editor edit = sp.edit();
        edit.putString("LAST_PLAY_SOUND", jsonSound);
        edit.putInt("LAST_PLAY_TYPE", palyType);
        edit.putInt("LAST_PLAY_INDEX", index);
        edit.commit();
    }


    /**
     * 取出最后播放的声音
     *
     * @param context
     * @param defSound
     * @return
     */
    public static String getLastPlay(Context context, String defSound) {
        SharedPreferences sp = context.getSharedPreferences("last_play_sound", MODE);
        return sp.getString("LAST_PLAY_SOUND", defSound);
    }

    public static int getPlayType(Context context) {
        SharedPreferences sp = context.getSharedPreferences("last_play_sound", MODE);
        return sp.getInt("LAST_PLAY_TYPE", 0);
    }

    public static int getPlayIndex(Context context) {
        SharedPreferences sp = context.getSharedPreferences("last_play_sound", MODE);
        return sp.getInt("LAST_PLAY_INDEX", 0);
    }

    /**
     * 保存播放模式
     *
     * @param context
     * @param playMode
     */
    public static void setPlayMode(Context context, int playMode) {
        SharedPreferences sp = context.getSharedPreferences("play_mode", MODE);
        Editor edit = sp.edit();
        edit.putInt("PLAY_MODE", playMode);
        edit.commit();
    }

  /*  设置播放器模式，mode取值为PlayMode中的下列之一：
    PLAY_MODEL_SINGLE单曲播放
    PLAY_MODEL_SINGLE_LOOP 单曲循环播放
    PLAY_MODEL_LIST列表播放
            PLAY_MODEL_LIST_LOOP列表循环
    PLAY_MODEL_RANDOM 随机播放
    */

    /**
     * 取出播放模式
     *
     * @param context
     * @param defMode
     * @return
     */
    public static int getPlayMode(Context context, int defMode) {
        SharedPreferences sp = context.getSharedPreferences("play_mode", MODE);
        return sp.getInt("PLAY_MODE", defMode);
    }

    /**
     * 保存最后定位的区域码
     *
     * @param context
     * @param adCode
     */
    public static void setLastLocationAdCode(Context context, String adCode) {
        SharedPreferences sp = context.getSharedPreferences("location_adcode", MODE);
        Editor edit = sp.edit();
        edit.putString("LOCATION_ADCODE", adCode);
        edit.commit();
    }

    /**
     * 取出最后定位的区域码  没有的话默认返回"-1"
     *
     * @param context
     * @return
     */
    public static String getLastLocationAdCode(Context context) {

        SharedPreferences sp = context.getSharedPreferences("location_adcode", MODE);
        return sp.getString("LOCATION_ADCODE", "-1");

    }
	
	/**保存boolean值*/
	public static boolean setBooleanValue(Context context, String key,
			boolean value) {
		Editor editor = context.getSharedPreferences(PREFERENCENAME, MODE)
				.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}
	/**取出boolean值*/
	public static Boolean getBooleanValue(Context context, String key ,boolean defValue) {
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCENAME, MODE);
		return settings.getBoolean(key, defValue);
	}
	/**保存string值*/
	public static boolean setStringValue(Context context, String key, String value) {
		Editor editor = context.getSharedPreferences(PREFERENCENAME, MODE)
				.edit();
		editor.putString(key, value);
		return editor.commit();
	}
	/**取出string值*/
	public static String getStringValue(Context context, String key ,String defValue) {
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCENAME, MODE);
		return settings.getString(key, defValue);
	}
}
