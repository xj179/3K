package com.ijiakj.radio.update;

import com.ijiakj.radio.R;

import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
/*
 * 报警信息Activity
 */
public class ReDownloadActivity extends Activity implements OnClickListener
{

	private  ReDownloadActivity instance;
	private  Button mOKBtn, mCancelBtn;
	private TextView mContentTV, mTitle ;
	private ImageView mIndicatorIV ;	

	private  String down_url ;
	private String app_name;
	private String notificTitle ;
	private String updateContent ;

	protected void onCreate(android.os.Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// set this flag so this activity will stay in front of the keyguard
		int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		getWindow().addFlags(flags);

		setContentView(R.layout.update_dialog);
		initView() ;
		
		app_name = getIntent().getStringExtra("app_name");
		notificTitle = getIntent().getStringExtra("notificeTitle");
		down_url = getIntent().getStringExtra("url");
		updateContent = getIntent().getStringExtra("updateContent");
		
		instance = this;

	}

	private void initView() {
		mOKBtn = (Button) findViewById(R.id.umeng_update_id_ok) ;
		mCancelBtn = (Button) findViewById(R.id.umeng_update_id_cancel);
		mContentTV = (TextView) findViewById(R.id.umeng_update_content);
		mTitle = (TextView) findViewById(R.id.title_tv) ;
		mIndicatorIV = (ImageView) findViewById(R.id.umeng_update_wifi_indicator);
		
//		mIndicatorIV.setImageResource(android.R.drawable.);
		mOKBtn.setText(getString(R.string.ok));
		mCancelBtn.setText(R.string.cancel);
		mTitle.setText(R.string.down_update);
		mContentTV.setText(R.string.redown);
		
		mOKBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.umeng_update_id_ok:
			// 开启更新服务UpdateService
			Intent updateIntent = new Intent(this, UpdateService.class);
			updateIntent.putExtra("app_name", app_name);
			updateIntent.putExtra("notificeTitle", notificTitle);
			updateIntent.putExtra("url", down_url);
			updateIntent.putExtra("updateContent", updateContent); // 更新内容传过去，，下载失败的时候重新下载用到
			this.startService(updateIntent);
			
			finish();
			break;
		case R.id.umeng_update_id_cancel:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 点击其它的地方关闭Activity
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		finish();
		return true;
	}

}

