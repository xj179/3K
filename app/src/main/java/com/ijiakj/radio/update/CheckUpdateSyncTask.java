package com.ijiakj.radio.update;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.ijiakj.radio.framework.Constant;
import com.ijiakj.radio.framework.RadioApplication;
import com.ijiakj.radio.net.HttpClientUtil;
import com.ijiakj.radio.update.UpdateDailog.OnCustomDialogListener;
import com.ijiakj.radio.utils.ToolUtil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class CheckUpdateSyncTask extends AsyncTask<String, Void, String> {

	private Context mContext;
	private String serverCode;
	private String versionName;
	private String apkURL;
	private String title;
	private String updateContent;
	private boolean isMustUpdate = false;
	private int updateType; // 0:手动升级;1：强制升级;
	private static final int MustUpdate = 1; // 1：强制升级;
	private static final int optionalUpdate = 0; // 0:手动升级

	public CheckUpdateSyncTask(Context context) {
		this.mContext = context;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected String doInBackground(String... params) {
		String versionString = getAppVersionCode(mContext) + "";
		String packageNameString = getAppPackageName(mContext);
		System.out.println(versionString + "   ," + packageNameString);
		Map<String, String> map = new HashMap<String, String>();
		/*
		 * String device_token = JPushInterface.getRegistrationID(mContext);
		 * map.put("device_token", device_token == null ?
		 * PreferenceUtil.getStringValue(mContext, "REGISTRATION_ID") :
		 * device_token);
		 */
		map.put("phone_brand", "common");
		map.put("version_code", getAppVersionCode(mContext) + "");
		map.put("package_name", getAppPackageName(mContext));
		map.put("version_name", getAppVersionName(mContext));
		map.put("phone_model", android.os.Build.MODEL);
		map.put("language", FileUtil.getLanguage(mContext));
		HttpClientUtil.addCommValue(mContext, map);
		if (RadioApplication.mUserMsg != null
				&& RadioApplication.mUserMsg.length != 0) {
			map.put("id", RadioApplication.mUserMsg[0]);
			map.put("site_code", RadioApplication.mUserMsg[1]);
			map.put("project_code", RadioApplication.mUserMsg[2]);
		}
//		String result = HttpClientUtil.postss(Constant.updateApp, map);
		String result = "";
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject info = new JSONObject(result);
				int resultCode = info.getInt("ret_code");

				// 得到服务器的版本信息成功，并且版本大于当前版本,开启线程下载最新的apk
				if (resultCode == 0) {
					serverCode = info.getString("version_code");
					apkURL = info.getString("apk_url");
					updateContent = info.getString("update_content");
					updateType = info.getInt("forced_update");
					String apkName = apkURL.substring(apkURL.lastIndexOf("/")+1).replace(".apk", "");

					if (updateType == MustUpdate) {
						isMustUpdate = true;
					}
					if (FileUtil.ifCanInstallApk(mContext, apkName, serverCode)) {
						ToolUtil.installApk(mContext, FileUtil.getAPKPath(apkName));
					}else {
						downloadApk(updateContent,info.getString("update_title"),
								info.getString("button_yes"),
								info.getString("button_no"),
								 apkURL, apkName,
								isMustUpdate);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 有新版本，弹出提示框，开启服务下载apk
	 * 
	 * @param updateContent
	 *            更新提示框内容
	 * @param notificeTitle
	 *            通知标题
	 * @param downloadUrl
	 *            apk地址
	 * @param appName
	 *            生成apk的名字(这个自己随意，建议使用应用
	 *            的包名_V+服务器的版本号（或者版本名字）。例如:com.dp.op_v3(5.1.02))不用传后缀.
	 */
	public void downloadApk(final String updateContent,  final String titleStr,final String yesStr,
			final String noStr, final String downloadUrl,
			final String appName, final boolean isMustUpdata) {

		new UpdateDailog(mContext, updateContent, yesStr, noStr, titleStr,
				new OnCustomDialogListener() {

					@Override
					public void executeDownload() {
						// 开启更新服务UpdateService
						// 这里为了把update更好模块化，可以传一些updateService依赖的值
						// 如布局ID，资源ID，动态获取的标题,这里以app_name为例
						Intent updateIntent = new Intent(mContext,
								UpdateService.class);
						updateIntent.putExtra("app_name", appName);
						updateIntent.putExtra("notificeTitle", titleStr);
						updateIntent.putExtra("url", downloadUrl);
						mContext.startService(updateIntent);
					}
				}, isMustUpdata).show();
	}

	/**
	 * 返回当前程序版本名
	 */
	public static int getAppVersionCode(Context context) {
		int versioncode = -1;
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versioncode = pi.versionCode;
		} catch (Exception e) {
			Log.e("CheckUpdateSyncTask", "Exception", e);
		}
		return versioncode;
	}

	/**
	 * 返回当前程序版本名
	 */
	public static String getAppVersionName(Context context) {
		String versioncode = "0";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versioncode = pi.versionName;
		} catch (Exception e) {
			Log.e("CheckUpdateSyncTask", "Exception", e);
		}
		return versioncode;
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
			Log.e("CheckUpdateSyncTask", "Exception", e);
		}
		return packageName;
	}
}
