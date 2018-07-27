package com.ijiakj.radio.update;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ijiakj.radio.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;



/**
 * 更新提示
 * @author sky63
 *
 */
public class UpdateDailog extends AlertDialog {
	
	// 定义回调事件，用于dialog的点击事件
	public interface OnCustomDialogListener {
		public void executeDownload();
	}

	//版本更新的内容
	private String mUpdateContent;
	private OnCustomDialogListener customDialogListener;
	private TextView mUpdateContentTV;
	private Button mOkButton ;
	private Button mCancenBtn ;
	private ImageView noWifiIV;
	private boolean isMustUpdata = false;
	private Context context;
	private String yesStr = "";
	private String noStr = "";
	private String titleStr = "";
	private TextView mTitleTV;

	public UpdateDailog(Context context, String updateContent,String yesStr,String noStr,String titleStr,
			OnCustomDialogListener customDialogListener,boolean isMustUpdata) {
		super(context);
		this.mUpdateContent = updateContent;
		this.customDialogListener = customDialogListener;
		this.isMustUpdata = isMustUpdata;
		this.context = context;
		this.yesStr = yesStr;
		this.noStr = noStr;
		this.titleStr = titleStr;
	}
	public UpdateDailog(Context context, String updateContent,String yesStr,String noStr,String titleStr,
			OnCustomDialogListener customDialogListener) {
		super(context);
		this.mUpdateContent = updateContent;
		this.customDialogListener = customDialogListener;
		this.context = context;
		this.yesStr = yesStr;
		this.noStr = noStr;
		this.titleStr = titleStr;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_dialog);
		// 设置标题
		mUpdateContentTV = (TextView) findViewById(R.id.umeng_update_content) ;
		mOkButton = (Button) findViewById(R.id.umeng_update_id_ok);
		mCancenBtn = (Button) findViewById(R.id.umeng_update_id_cancel);
		noWifiIV = (ImageView) findViewById(R.id.umeng_update_wifi_indicator);
		mTitleTV = (TextView) findViewById(R.id.title_tv);
		
		if (isMustUpdata) {
			mCancenBtn.setVisibility(View.GONE);
		}
		mOkButton.setText(yesStr);
		mCancenBtn.setText(noStr);
		mTitleTV.setText(titleStr);
		mUpdateContentTV.setText(mUpdateContent);
		mOkButton.setOnClickListener(clickListener);
		mCancenBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UpdateDailog.this.dismiss();
			}
		});
		if (!NetUtils.isWifi(getContext())) {
			noWifiIV.setVisibility(View.VISIBLE);
		}
		
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			customDialogListener.executeDownload();
			UpdateDailog.this.dismiss();
		}
	};

	
	public boolean onTouchEvent(android.view.MotionEvent event) {
		return false ;
	};
}
