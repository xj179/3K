package com.ijiakj.radio.utils;

import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * 创建者     曹自飞
 * 创建时间   2016/11/10 0010 09:25
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class LocationUtil {

    private static AMapLocationClient mLocationClient;

    /**
     * 得到当前位置经纬度
     */
    public static void location() {

        AMapLocationClientOption mLocationOption = null;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(UIUtils.getContext());
        }
        // 声明mLocationOption对象
        mLocationOption = new AMapLocationClientOption();
        // 设置定位监听
        mLocationClient.setLocationListener(new AMapLocationListener() {

            @Override
            public void onLocationChanged(AMapLocation amapLocation) {

                if (amapLocation != null && !TextUtils.isEmpty(amapLocation.getAdCode())) {
                    //把区域码保存到本地
                    PreferenceUtil.setLastLocationAdCode(UIUtils.getContext(), amapLocation.getAdCode());

                    onDestroy();
                } else {
                    String lastLocationAdCode = PreferenceUtil.getLastLocationAdCode(UIUtils.getContext());
                    if (lastLocationAdCode.equals("-1")) {
                        PreferenceUtil.setLastLocationAdCode(UIUtils.getContext(), "440000");//进行过定位了,并且失败了,就提供默认区域码 440000(广东)
                    }
                }
                Log.d("czf3" + amapLocation.getAddress(), Thread.currentThread().getName());

            }
        });

        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        // 初始化定位参数
        // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        // 设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        // 设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        // 设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        // 设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        // 给定位客户端对象设置定位参数
        // LocationManager locManager=(LocationManager)
        mLocationClient.setLocationOption(mLocationOption);
        Log.d("czf2", Thread.currentThread().getName());

        mLocationClient.startLocation();
    }


    public static void onDestroy() {

        if (mLocationClient != null) {
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }
}
