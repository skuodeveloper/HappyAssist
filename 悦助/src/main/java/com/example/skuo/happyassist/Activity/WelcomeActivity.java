package com.example.skuo.happyassist.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.skuo.happyassist.R;

public class WelcomeActivity extends AppCompatActivity {
    private static final int GOTO_MAIN_ACTIVITY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 3000);//3秒跳转
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case GOTO_MAIN_ACTIVITY:
                    Intent intent = new Intent();
                    intent.setClass(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        };
    };
}
