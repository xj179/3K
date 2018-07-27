package com.ijiakj.radio.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;

import org.xutils.x;
import org.xutils.common.Callback.Cancelable;
import org.xutils.common.Callback.ProgressCallback;
import org.xutils.http.RequestParams;

import com.ijiakj.radio.R;
import com.ijiakj.radio.activity.MainActivity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

/***
 * 更新版本
 * 
 * @author
 * 
 */
public class UpdateService extends Service {
	private static final int TIMEOUT = 10 * 1000;// 超时
	private static final int DOWN_OK = 1;
	private static final int DOWN_ERROR = 0;

	private String down_url;
	private String app_name;
	private String notificTitle;
	private String downPath;
	private String updateContent;

	private NotificationManager notificationManager;
	private Notification notification;

	private Intent updateIntent;
	private PendingIntent pendingIntent;

	private int notification_id = 0;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			app_name = intent.getStringExtra("app_name");
			notificTitle = intent.getStringExtra("notificeTitle");
			down_url = intent.getStringExtra("url");
			updateContent = intent.getStringExtra("updateContent");
			FileUtil.deleteFile(new File(Environment.getExternalStorageDirectory()
					+ "/" + FileUtil.downloadDir) ,app_name);
			// 创建文件
			downPath = FileUtil.createFile(app_name);
			createNotification();
			createThread();
		}
		return super.onStartCommand(intent, flags, startId);

	}
	
	/***
	 * 更新UI
	 */
	@SuppressLint("HandlerLeak")
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_OK:
				// 下载完成，点击安装
				Uri uri = Uri.fromFile(FileUtil.updateFile);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(uri,
						"application/vnd.android.package-archive");

				pendingIntent = PendingIntent.getActivity(
						UpdateService.this, 0, intent, 0);

				notification.setLatestEventInfo(UpdateService.this,
						app_name, getString(R.string.down_success),
						pendingIntent);

				notificationManager.notify(notification_id, notification);

				stopService(updateIntent);

				// 调用安装代码
				if (downPath != null) {
					installApk(UpdateService.this, downPath);
				}
				break;
			case DOWN_ERROR:
				Intent reloadIntent = new Intent();
				reloadIntent.putExtra("app_name", app_name);
				reloadIntent.putExtra("notificeTitle", notificTitle);
				reloadIntent.putExtra("url", down_url);
				reloadIntent.putExtra("updateContent", updateContent);
				reloadIntent.setClass(UpdateService.this,
						ReDownloadActivity.class); // 跳
													// 到重新下载的activity(这个activity是一个Dialog样式的Activity)

				pendingIntent = PendingIntent.getActivity(
						UpdateService.this, 0, reloadIntent, 0);

				notification.setLatestEventInfo(UpdateService.this,
						app_name, getString(R.string.down_fail),
						pendingIntent);
				notificationManager.notify(notification_id, notification);
				stopService(updateIntent);
				break;

			default:
				stopService(updateIntent);
				break;
			}

		}

	};

	/***
	 * 开线程下载
	 */
	public void createThread() {

//		final Message message = new Message();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					downLoadFile(down_url, FileUtil.updateFile.toString(),
							new ProgressCallback<File>() {

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
								public void onSuccess(File arg0) {
									Message message = new Message();
									message.what = DOWN_OK;
									handler.sendMessage(message);
								}

								@Override
								public void onLoading(long arg0, long arg1,
										boolean arg2) {
									int num1 = (int)arg1;
									int num2 = (int)arg0;
									// 创建一个数值格式化对象
									NumberFormat numberFormat = NumberFormat.getInstance();
									// 设置精确到小数点后2位
									numberFormat.setMaximumFractionDigits(2);
									String result = numberFormat.format((float)num1/(float)num2*100);
									
									contentView.setTextViewText(R.id.notificationPercent,
											result + "%");
									contentView.setProgressBar(R.id.notificationProgress, 100,
											Double.valueOf(result).intValue(), false);
									// show_view
									notificationManager.notify(notification_id, notification);

								}

								@Override
								public void onStarted() {

								}

								@Override
								public void onWaiting() {

								}

							});

				} catch (Exception e) {
					e.printStackTrace();
					Message message = new Message();
					message.what = DOWN_ERROR;
					handler.sendMessage(message);
				}
			}
		}).start();
	}
	
	private Toast mToast;
	public void showToast(String text) {
	        if(mToast == null) {
	            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
	        } else {
	            mToast.setText(text);
	            mToast.setDuration(Toast.LENGTH_SHORT);
	        }
	        mToast.show();
	}

	/***
	 * 创建通知栏
	 */
	RemoteViews contentView;

	public void createNotification() {
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		/***
		 * 在这里我们用自定的view来显示Notification
		 */
		contentView = new RemoteViews(getPackageName(),
				R.layout.notification_item);
		contentView.setTextViewText(R.id.notificationTitle, notificTitle);
		contentView.setTextViewText(R.id.notificationPercent, "0%");
		contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
		notification.contentView = contentView;
		updateIntent = new Intent(this, MainActivity.class);
		updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
		notification.contentIntent = pendingIntent;
		notificationManager.notify(notification_id, notification);
	}

	/***
	 * 下载文件
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public long downloadUpdateFile(String down_url, String file)
			throws Exception {
		int down_step = 5;// 提示step
		int totalSize;// 文件总大小
		int downloadCount = 0;// 已经下载好的大小
		int updateCount = 0;// 已经上传的文件大小
		InputStream inputStream;
		OutputStream outputStream;
		URL url = new URL(down_url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setConnectTimeout(TIMEOUT);
		httpURLConnection.setReadTimeout(TIMEOUT);
		// 获取下载文件的size
		totalSize = httpURLConnection.getContentLength();
		if (httpURLConnection.getResponseCode() == 404) {
			throw new Exception("fail!");
		}
		inputStream = httpURLConnection.getInputStream();
		outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉
		byte buffer[] = new byte[1024];
		int readsize = 0;
		while ((readsize = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, readsize);
			downloadCount += readsize;// 时时获取下载到的大小
			/**
			 * 每次增张5%
			 */
			if (updateCount == 0
					|| (downloadCount * 100 / totalSize - down_step) >= updateCount) {
				updateCount += down_step;
				// 改变通知栏
				// notification.setLatestEventInfo(this, "正在下载...", updateCount
				// + "%" + "", pendingIntent);
				contentView.setTextViewText(R.id.notificationPercent,
						updateCount + "%");
				contentView.setProgressBar(R.id.notificationProgress, 100,
						updateCount, false);
				// show_view
				notificationManager.notify(notification_id, notification);

			}

		}
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
		inputStream.close();
		outputStream.close();
		return downloadCount;

	}

	/**
	 * 安装APK文件
	 */
	public void installApk(Context context, String apkPth) {
		File apkfile = new File(apkPth);
		if (!apkfile.exists()) {
			return;
		}
		if (checkCompleteness(context, apkPth)) {
			// 通过Intent安装APK文件
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
					"application/vnd.android.package-archive");
			context.startActivity(i);
		}else {
			Toast.makeText(context, "apk不完整，正在重新下载", Toast.LENGTH_LONG).show();
			createNotification();
			createThread();
		}
	}

	/**
	 * 文件下载，保存到指定文件
	 * 
	 * @param <T>
	 */
	public static synchronized <T> Cancelable downLoadFile(String url,
			String filepath, ProgressCallback<File> callback) {
		RequestParams params = new RequestParams(url);
		// 设置断点续传
		params.setAutoResume(true);
		params.setSaveFilePath(filepath);
		Cancelable cancelable = x.http().get(params, callback);
		return cancelable;
	}

	/**
	 * 检测apk完整性
	 * 
	 * @param context
	 * @param archiveFilePath
	 * @return
	 */
	public static boolean checkCompleteness(Context context,
			String archiveFilePath) {
		boolean result = false;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath,
					PackageManager.GET_ACTIVITIES);
			if (info != null) {
				result = true;
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
}
