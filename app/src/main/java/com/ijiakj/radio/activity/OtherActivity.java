package com.ijiakj.radio.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.ijiakj.radio.R;
import com.ijiakj.radio.framework.Constant;

import java.io.File;


/**
 * 创建者：zp
 * 描述 ：主界面
 */

public class OtherActivity extends AppCompatActivity implements View.OnClickListener {
    WebView mWebview;
    WebSettings mWebSettings;
    private ImageView homeImg, leftImg, resImg, rigImg;
    private long exitTime = 0;
    private String otherUrl;
    //    private ProgressDialog progressFirst;
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        initData();
    }


    public void initData() {
        otherUrl = getIntent().getExtras().getString(Constant.WEB_URL) ;
        mWebview = (WebView) findViewById(R.id.web_view);

        //声明WebSettings子类
        mWebSettings = mWebview.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
//        mWebSettings.setJavaScriptEnabled(true);
        //支持插件
//        webSettings.setPluginsEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        }

        mWebSettings.setDomStorageEnabled(true);
        //设置自适应屏幕，两者合用
        mWebSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        mWebSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小


        // 缩放操作
        mWebSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        mWebSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        mWebSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        // 设置WebView属性，能够执行Javascript脚本
        mWebSettings.setJavaScriptEnabled(true);
        //其他细节操作
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        mWebSettings.setAllowFileAccess(true); //设置可以访问文件
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        mWebSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        mWebview.setBackgroundColor(Color.TRANSPARENT);// 设置其背景为透明
        mWebview.clearCache(true);//清除当前webview访问的历史记录
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setAppCacheMaxSize(1024 * 1024 * 8);//设置缓存大小
        //设置缓存路径
        String appCacheDir = Environment.getExternalStorageDirectory().getPath() + "/2048master/cache";
        File fileSD = new File(appCacheDir);
        if (!fileSD.exists()) {
            fileSD.mkdir();
        }
        mWebSettings.setAppCachePath(appCacheDir);
        mWebSettings.setAllowContentAccess(true);
        mWebSettings.setAppCacheEnabled(true);

        mWebview.clearHistory();//只会webview访问历史记录里的所有记录除了当前访问记录
        mWebview.clearFormData();//这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
//        mWebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebview.loadUrl(otherUrl);

        //设置不用系统浏览器打开,直接显示在当前Webview
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadData(url, "text/html", "utf-8");
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //注意：super句话一定要删除，或者注释掉，否则又走handler.cancel()默认的不支持https的了。
                //super.onReceivedSslError(view, handler, error);
                //handler.cancel(); // Android默认的处理方式
                //handler.handleMessage(Message msg); // 进行其他处理
                handler.proceed(); // 接受所有网站的证书
            }
        });

        //设置WebChromeClient类
        mWebview.setWebChromeClient(new WebChromeClient() {
            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
//                mtitle.setText(title);

            }

            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    if (dialog != null) {
                        dialog.show();
                    }

                } else if (newProgress == 100) {
                    if (dialog != null) {
                        if (dialog.isShowing()) {//加载URL完成后，进度加载提示框消失
                            dialog.dismiss();
                        }
                    }
                }

            }
        });

        //设置WebViewClient类
        mWebview.setWebViewClient(new WebViewClient() {
            //设置加载前的函数
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                System.out.println("开始加载了");
//                beginLoading.setText("开始加载了");
            }

            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
//                endLoading.setText("结束加载了");
            }
        });

/*        if (NetworkDeviceUtils.isNetworkConnected(this)) {
            //有网络网络加载
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            //无网时本地缓存加载
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        }*/
//
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
        return true;
    }


    //销毁Webview
    @Override
    protected void onDestroy() {
        if (mWebview != null) {
            mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();
            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }


        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
/*        switch (v.getId()) {
            case R.id.home_iv:
                mWebview.clearHistory(); // 清除
                mWebview.loadUrl(otherUrl);
                break;
            case R.id.left_iv:
                if (mWebview.canGoBack()) {
                    mWebview.goBack();
                } else {
                    if ((System.currentTimeMillis() - exitTime) > 2000) {
                        Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        exitTime = System.currentTimeMillis();
                    } else {
                        finish();
                    }
                }
                break;
            case R.id.refest_iv:
                mWebview.reload();
                break;
            case R.id.right_iv:
                mWebview.goForward();
                break;

        }*/

    }
}
