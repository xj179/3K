package com.ijiakj.radio.framework;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xutils.x;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Process;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ijiakj.radio.net.MultiPartStack;
import com.ijiakj.radio.update.FileUtil;
import com.ijiakj.radio.utils.AdUtils;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import cn.jpush.android.api.JPushInterface;

/**
 * 爱加电台入口程序
 * 2016.8.26
 */
public class RadioApplication extends Application {

    public static final String MAIN_PROCESS_NAME = "com.ijiakj.radio";
    private static Context mContext;
    private static int mMainThreadId;
    private static Handler mMainThreadHandler;
    private static RequestQueue mQueues;

    public final static String APP_SECRET = "21726925d3a86d8099d44af772f3b8c6";// (应用私钥)
    /**
     * 数据传递的集合
     */
    private static Map<String, Object> mDeliverMap = new HashMap<>();

    public static Map<String, Object> getDeliverMap() {
        return mDeliverMap;
    }
    public static String[] mUserMsg;
    public static final Uri CONTENT_URI = Uri
    			.parse("content://com.dp.op.db.provider/T_USER?notify=true");
        @SuppressLint("NewApi")
    	private void getUserMsg() {
        	try {
    			Cursor c1 = getContentResolver().query(CONTENT_URI,
    					new String[] { "SERVER_ID", "SET_CODE", "PROJECT_CODE" },
    					null, null, null, null);
    			if (c1 != null && c1.getCount() > 0) {
    				if (c1.moveToFirst()) {
    					mUserMsg = new String[] { c1.getString(0), c1.getString(1),
    							c1.getString(2) };
    					c1.close();
    				}
    			}
    		} catch (Exception e) {
    			// TODO: handle exception
    		}
        }

    /**
     * 获取上下文
     *
     * @return
     */
    public static Context getContext() {
        return mContext;
    }
    
    public static RequestQueue getHttpQueue(){
        return mQueues;
    }

    /**
     * 获取主线程id
     *
     * @return
     */
    public static int getMainThreadId() {
        return mMainThreadId;
    }

    /**
     * 获取主线程handler
     *
     * @return
     */
    public static Handler getMainThreadHandler() {
    	if (mMainThreadHandler == null) {
    		//得到主线程Handler
            mMainThreadHandler = new Handler();
		}
        return mMainThreadHandler;
    }

    @Override
    //程序的入口
    public void onCreate() {
    	x.Ext.init(this);
//    	getUserMsg();
        //上下文
        mContext = getApplicationContext();
        AdUtils.getAllAdId(mContext);
        //得到主线程id
        mMainThreadId = android.os.Process.myTid();
        mMainThreadHandler = new Handler();
        CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		
		mQueues= Volley.newRequestQueue(mContext,new MultiPartStack());

		//极光初始化
        JPushInterface.setDebugMode(true);  //调试模式
        JPushInterface.init(this);

        String pName = null;
        int pid = Process.myPid();
        Log.d("czf", "pid" + pid);
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        int size = runningAppProcesses.size();
        for (int i = 0; i < size; i++) {
            ActivityManager.RunningAppProcessInfo runningAppProcessInfo = runningAppProcesses.get(i);
            if (runningAppProcessInfo.pid == pid) {
                pName = runningAppProcessInfo.processName;
                break;
            }
        }
        if (pName != null && MAIN_PROCESS_NAME.equals(pName)) {
            CommonRequest.getInstanse().init(getApplicationContext(), APP_SECRET);
        }
        
        // 获取手机平台
     	Constant.PLATFORM = FileUtil.getPlatformType() ;
        super.onCreate();

    }

    @Override
    public void onTerminate() {
        XmPlayerManager.release();
        super.onTerminate();
    }

    private List<Context> mList = new LinkedList<Context>();
    private static RadioApplication instance;

    public RadioApplication() {

    }

    public synchronized static RadioApplication getInstance() {
        if (null == instance) {
            instance = new RadioApplication();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Context context) {
        mList.add(context);
    }

    public void exit() {
        try {
            for (Context context : mList) {
                if (context != null) {
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                    if (context instanceof Service) {
                        ((Service) context).stopSelf();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
