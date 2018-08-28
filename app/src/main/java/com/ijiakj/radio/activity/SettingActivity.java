package com.ijiakj.radio.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ijiakj.radio.BuildConfig;
import com.ijiakj.radio.R;
import com.ijiakj.radio.base.BaseActivity;

public class SettingActivity extends AppCompatActivity {

    TextView version_tv ;

    ImageView back_btn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
    }

    protected void initView() {
        version_tv = (TextView) findViewById(R.id.version_tv);
        back_btn = (ImageView) findViewById(R.id.back_btn);

        version_tv.setText("V "+ BuildConfig.VERSION_NAME);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
