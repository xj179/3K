package com.ijiakj.radio.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.ijiakj.radio.framework.Constant;
import com.ijiakj.radio.framework.RadioApplication;

/**
 * 广告工具类
 * 
 * 广告类型：1表示开屏广告，2表示旗帜广告，3表示信息流广告。
 * 
 * 记录广告位编号： 1： 0=开屏广告 2： 0=详情页面旗帜广告， 1=宜忌页面旗帜广告
 * 
 */
public class AdUtils {

	/** 获取此应用所有广告id,没有返回id的广告位不予显示,此方法在应用每次启动时调用 */
	public static void getAllAdId(final Context mContext) {
		RequestParams params = new RequestParams(Constant.HOST + "api/advertising/findAdvId");
		addCommValue(mContext, params);
		params.addQueryStringParameter("package_name", getAppPackageName(mContext));
		if (RadioApplication.mUserMsg != null && RadioApplication.mUserMsg.length != 0) {
			params.addQueryStringParameter("user_id", RadioApplication.mUserMsg[0]);
			params.addQueryStringParameter("site_code", RadioApplication.mUserMsg[1]);
			params.addQueryStringParameter("project_code", RadioApplication.mUserMsg[2]);
		}else {
			params.addQueryStringParameter("user_id", "");
			params.addQueryStringParameter("site_code", "s_common_old");
			params.addQueryStringParameter("project_code", "s_common");
		}
		x.http().post(params, new CommonCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
			}

			@Override
			public void onFinished() {
			}

			@Override
			public void onSuccess(String response) {
				if (response != null) {
					try {
						JSONObject ob = new JSONObject(response);
						if (ob.getString("ret_code").equals("0")) {
							JSONArray list = ob.getJSONArray("list");
							// show_type1表示开屏广告，show_type2表示旗帜广告，show_type3表示信息流广告。
							for (int i = 0; i < list.length(); i++) {
								PreferenceUtil.setStringValue(mContext, "show_type" + list.getJSONObject(i).getString("show_type"), list
										.getJSONObject(i).getString("adv_flow"));
							}
						}else {
							for (int j = 1; j < 4; j++) {
								PreferenceUtil.setStringValue(mContext, "show_type" + j,"");
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 
	 * @param mContext
	 * @param typenum
	 *            广告类型：1表示开屏广告，2表示旗帜广告，3表示信息流广告。
	 * @param num
	 *            广告位的编号，从零开始
	 * @return 此广告的广告id，空字符串表示不显示此广告
	 */
	public static String getAdId(Context mContext, int typenum, int num) {
		String Ad_id = PreferenceUtil.getStringValue(mContext, "show_type" + typenum, "");
		String[] adList = Ad_id.split(",");
		if (adList != null && adList.length > num && !TextUtils.isEmpty(adList[num])) {
			return adList[num];
		}
		return "";
	}

	/**
	 * 
	 * @param mContext
	 * @param url
	 *            点击的广告的url
	 * @param type
	 *            点击的广告类型：1表示开屏广告，2表示旗帜广告，3表示信息流广告。
	 */
	public static void clickUploading(final Context mContext, String url, String type, String loc_name) {
		if (!TextUtils.isEmpty(url)) {
			RequestParams params = new RequestParams(Constant.HOST + "api/advertising/clickAdv");
			addCommValue(mContext, params);
			params.addQueryStringParameter("type", type);
			params.addQueryStringParameter("url", url);
			params.addQueryStringParameter("loc_name", loc_name);
			params.addQueryStringParameter("package_name", getAppPackageName(mContext));
			if (RadioApplication.mUserMsg != null && RadioApplication.mUserMsg.length != 0) {
				params.addQueryStringParameter("user_id", RadioApplication.mUserMsg[0]);
				params.addQueryStringParameter("site_code", RadioApplication.mUserMsg[1]);
				params.addQueryStringParameter("project_code", RadioApplication.mUserMsg[2]);
			}else {
				params.addQueryStringParameter("user_id", "");
				params.addQueryStringParameter("site_code", "s_common_old");
				params.addQueryStringParameter("project_code", "s_common");
			}
			x.http().post(params, new CommonCallback<String>() {

				@Override
				public void onCancelled(CancelledException arg0) {
				}

				@Override
				public void onError(Throwable arg0, boolean arg1) {
				}

				@Override
				public void onFinished() {
				}

				@Override
				public void onSuccess(String response) {
				}
			});
		}
	}

	/** 添加公共参数 */
	public static void addCommValue(Context context, RequestParams params) {
//		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		params.addQueryStringParameter("device", "Android");
		params.addQueryStringParameter("userkey", "0");
		params.addQueryStringParameter("systemVer", android.os.Build.MODEL + android.os.Build.VERSION.RELEASE);
//		params.addQueryStringParameter("imei", tm.getDeviceId() == null ? "" : tm.getDeviceId());
		params.addQueryStringParameter("imei", CTelephoneInfo.getInstance(context).getImei());
		params.addQueryStringParameter("key", Constant.API_KEY);
		params.addQueryStringParameter("time", System.currentTimeMillis() + "");
		params.addQueryStringParameter("user_token", "");
		StringBuffer auth = new StringBuffer();
		StringBuffer token = new StringBuffer();
		for (KeyValue entry : params.getQueryStringParams()) {
			if ("".equals(auth.toString())) {
				auth.append(entry.key);
			} else {
				auth.append("," + entry.key);
			}
			token.append(entry.value);
		}
		params.addQueryStringParameter("auth", Base32.encode((auth.toString()).getBytes()));
		params.addQueryStringParameter("token", MD5.GetMD5Code(token.toString()));
	}
	
	/**
	 * 返回当前程序包名
	 */
	public static String getAppPackageName(Context context) {
		String packageName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			packageName = pi.packageName;
		} catch (Exception e) {
		}
		return packageName;
	}
}
