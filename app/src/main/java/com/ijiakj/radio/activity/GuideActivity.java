package com.ijiakj.radio.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.ijiakj.radio.R;
import com.ijiakj.radio.bean.K3Bean;
import com.ijiakj.radio.framework.Constant;
import com.ijiakj.radio.framework.MyConstant;
import com.ijiakj.radio.net.VolleyInterface;
import com.ijiakj.radio.net.VolleyUtils;
import com.ijiakj.radio.utils.AdUtils;
import com.ijiakj.radio.utils.Base64Encoded;
import com.ijiakj.radio.utils.ToolUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.x;

import java.util.List;

/**
 * 导航页面
 */
public class GuideActivity extends Activity {

	private IFLYNativeAd nativeAd;
	private NativeADDataRef adItem;
	private RelativeLayout native_lay;
	private ImageView native_ad;
	private TextView skip_ad;
	public boolean jumpFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
		setContentView(R.layout.activity_guide);
		native_ad = (ImageView) findViewById(R.id.native_ad);
		skip_ad = (TextView) findViewById(R.id.skip_ad);
		native_lay = (RelativeLayout) findViewById(R.id.native_lay);
		if (!TextUtils.isEmpty(AdUtils.getAdId(GuideActivity.this, 1, 0))) {
			NativeAd(AdUtils.getAdId(GuideActivity.this, 1, 0));
		}else {
			jumpFlag = true;
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					/*Intent intent = new Intent(GuideActivity.this,
							MainActivity.class);
					startActivity(intent);
					GuideActivity.this.finish();*/
					init3K() ;
				}
			}, 1000);
		}
	}

	private void NativeAd(String adId) {
		if (nativeAd == null) {
			nativeAd = new IFLYNativeAd(this, adId, listener);
		}
		int count = 1; // 一次拉取的广告条数,当前仅支持一条
		nativeAd.loadAd(count);
	}

	private IFLYNativeListener listener = new IFLYNativeListener() {

		@Override
		public void onConfirm() {
		}

		@Override
		public void onCancel() {
		}

		@Override
		public void onAdFailed(AdError arg0) {
			displayMain();
		}

		@Override
		public void onADLoaded(List<NativeADDataRef> arg0) {
			adItem = arg0.get(0);
			ShowAD();
		}
	};

	private void ShowAD() {
		x.image().bind(native_ad, adItem.getImage(), ToolUtil.imageOptions, new CommonCallback<Drawable>() {

			@Override
			public void onSuccess(Drawable arg0) {
				native_lay.setVisibility(View.VISIBLE);
				skip_ad.setVisibility(View.VISIBLE);
			}

			@Override
			public void onFinished() {

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				displayMain();
			}

			@Override
			public void onCancelled(CancelledException arg0) {

			}
		});
		((TextView) findViewById(R.id.ad_from)).setText(adItem.getAdSourceMark() + "");
		native_ad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				adItem.onClicked(v);
				// 上传用户点击的广告的url
				AdUtils.clickUploading(GuideActivity.this, adItem.getImage(), "1", "开屏");
			}
		});
		skip_ad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				displayMain();
			}
		});
		// 原生广告需上传点击位置
		native_ad.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					nativeAd.setParameter(AdKeys.CLICK_POS_DX, event.getX() + "");
					nativeAd.setParameter(AdKeys.CLICK_POS_DY, event.getY() + "");
					break;
				case MotionEvent.ACTION_UP:
					nativeAd.setParameter(AdKeys.CLICK_POS_UX, event.getX() + "");
					nativeAd.setParameter(AdKeys.CLICK_POS_UY, event.getY() + "");
					break;
				default:
					break;
				}
				return false;
			}
		});
		if (adItem.onExposured(this.findViewById(R.id.native_ad))) {
			// showMessage("曝光成功");
		}
		timer.start();
	}

	public void displayMain() {
		if (!jumpFlag) {
			jumpFlag = true;
			Intent intent = new Intent(GuideActivity.this, MainActivity.class);
			startActivity(intent);
			GuideActivity.this.finish();
		}
	}

	private CountDownTimer timer = new CountDownTimer(5000, 1000) {

		@Override
		public void onTick(long millisUntilFinished) {
			skip_ad.setText("点击跳过 " + (millisUntilFinished / 1000));
		}

		@Override
		public void onFinish() {
			displayMain();
		}
	};



	private  void init3K(){
		VolleyUtils.requestGet(this, MyConstant.HOST_3K_URL, "3K", new VolleyInterface(this) {
			@Override
			public void onSuccess(String result) {
				if (!TextUtils.isEmpty(result)) {
					try {
						JSONObject object = new JSONObject(result) ;
						if (object.getString("rt_code").equals("200")) {
							String  str = object.getString("data");
							str = Base64Encoded.getUidFromBase64(str) ;

                            Gson gson = new Gson();
                            K3Bean b = gson.fromJson(str,K3Bean.class);

							/**
							 * 返回的标识为1 你就跳转到 他们的给地址 loding出来就行了
							 * 为0的时候 是关着的 那就跳转到 你正常的首页就行了列
							 * show_url 这个字段为1 的时候 证明开关是打开的
							 */
							if (b.getShow_url().equals("1")) {   //开关打开
								Intent intent = new Intent(GuideActivity.this,WebActivity.class);
								intent.putExtra(Constant.WEB_URL, b.getUrl()) ;
//								Intent intent = new Intent(GuideActivity.this,OtherActivity.class);
//								intent.putExtra(Constant.WEB_URL, "http://www.baidu.com") ;
								startActivity(intent);
								GuideActivity.this.finish();
								return ;
							} else {  //开关为关闭状态
								Intent intent = new Intent(GuideActivity.this,
										MainActivity.class);
								startActivity(intent);
								GuideActivity.this.finish();
							}
							Log.i("TAG", "init3K: " + b.toString());
						}
					} catch (JSONException e) {
						Intent intent = new Intent(GuideActivity.this,
								MainActivity.class);
						startActivity(intent);
						GuideActivity.this.finish();
					}
				}
			}

			@Override
			public void onError(VolleyError error) {
				Log.i("TAG", "onError: " + error);
				Intent intent = new Intent(GuideActivity.this,
						MainActivity.class);
				startActivity(intent);
				GuideActivity.this.finish();
			}
		});
	}
}
