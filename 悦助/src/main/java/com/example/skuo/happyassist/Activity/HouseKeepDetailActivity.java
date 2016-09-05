package com.example.skuo.happyassist.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.skuo.happyassist.Class.Result.HouseKeepInfo;
import com.example.skuo.happyassist.R;

import java.util.HashMap;

@SuppressWarnings("deprecation")
public class HouseKeepDetailActivity extends BaseActivity {
    private Context mContext;
    private HouseKeepInfo infos;
    /**
     * 存储网络返回的数据
     */
    private HashMap<String, Object> hashMap;

    private static final int REQUEST_CALL_PHONE = 2;
    private static final int REQUEST_DONE_CODE = 3;
    public static int ActionType = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        infos = (HouseKeepInfo) getIntent().getSerializableExtra("Infos");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housekeep_detail);

        initView();
    }

    View.OnClickListener hander = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent;

            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.iv_call:
                    //用intent启动拨打电话
                    String mobile = infos.Tel;

                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE);
                        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((OwnerInquiryActivity) mContext, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
                            return;
                        } else {
                            //上面已经写好的拨号方法
                            callDirectly(mobile);
                        }
                    } else {
                        //上面已经写好的拨号方法
                        callDirectly(mobile);
                    }
                    break;
                case R.id.btnDispatch:
                    intent = new Intent(HouseKeepDetailActivity.this, HousekeepDispatchActivity.class);
                    intent.putExtra("ID", String.valueOf(infos.ID));

                    startActivityForResult(intent, REQUEST_DONE_CODE);
                    break;
            }
        }
    };

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("家政订单详情");

        ((Button) findViewById(R.id.btnDispatch)).setOnClickListener(hander);

        ((ImageView) findViewById(R.id.iv_back)).setOnClickListener(hander);
        ((ImageView) findViewById(R.id.iv_call)).setOnClickListener(hander);

        ((TextView) findViewById(R.id.tv_orderid)).setText(String.valueOf(infos.ID));
        ((TextView) findViewById(R.id.tv_name)).setText(infos.UserName);
        ((TextView) findViewById(R.id.tv_address)).setText(infos.ServiceAddress);

        switch (infos.Status) {
            case 1:
                ((TextView) findViewById(R.id.tv_status)).setText("已提交");
                ((TextView) findViewById(R.id.btnDispatch)).setVisibility(View.VISIBLE);
                break;
            case 2:
                ((TextView) findViewById(R.id.tv_status)).setText("派单中");
                ((LinearLayout) findViewById(R.id.ll_show)).setVisibility(View.VISIBLE);
                ((View) findViewById(R.id.ll_show1)).setVisibility(View.VISIBLE);

                ((TextView) findViewById(R.id.tv_staff)).setText(infos.ServiceStaff);
                ((TextView) findViewById(R.id.tv_stafftel)).setText(infos.StaffTel);
                ((TextView) findViewById(R.id.tv_status)).setText("派单中");
                break;
            case 3:
                ((TextView) findViewById(R.id.tv_status)).setText("已完成");
                ((LinearLayout) findViewById(R.id.ll_show)).setVisibility(View.VISIBLE);
                ((View) findViewById(R.id.ll_show1)).setVisibility(View.VISIBLE);

                ((TextView) findViewById(R.id.tv_staff)).setText(infos.ServiceStaff);
                ((TextView) findViewById(R.id.tv_stafftel)).setText(infos.StaffTel);
                break;
            case 4:
                ((TextView) findViewById(R.id.tv_status)).setText("已取消");
                break;
        }

        ((TextView) findViewById(R.id.tv_time)).setText(infos.AppointmentTime);
        ((TextView) findViewById(R.id.tv_info)).setText(infos.ServiceName);
        ((TextView) findViewById(R.id.tv_remark)).setText(infos.Remark);
        ((TextView) findViewById(R.id.tv_comment)).setText(infos.Level + "分");
    }

    //拨打电话
    private void callDirectly(String mobile) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:" + mobile));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DONE_CODE:
                if (ActionType == 1) {
                    ActionType = 0;
                    HouseKeepOrdersActivity.ActionType = 1;
                    this.finish();
                }
                break;
        }
    }
}
