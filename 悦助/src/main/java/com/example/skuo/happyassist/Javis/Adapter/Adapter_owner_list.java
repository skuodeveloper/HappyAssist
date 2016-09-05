package com.example.skuo.happyassist.Javis.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skuo.happyassist.Activity.AuthenActivity;
import com.example.skuo.happyassist.Activity.OwnerInquiryActivity;
import com.example.skuo.happyassist.Class.Result.UserInfo;
import com.example.skuo.happyassist.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 16-6-28.
 */
public class Adapter_owner_list extends BaseAdapter {
    private static final int REQUEST_CODE_ASK_CALL_PHONE = 123;
    private static final int JUMP_TO_DETAIL = 6;
    private Context mContext;
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

    public Adapter_owner_list(Context context) {
        this.mContext = context;
    }

    public Adapter_owner_list(Context context, ArrayList<HashMap<String, Object>> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        //TODO
        return (arrayList != null && arrayList.size() == 0) ? 0 : arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View currentView, ViewGroup arg2) {
        HolderView holderView = null;
        if (currentView == null) {
            holderView = new HolderView();
            currentView = LayoutInflater.from(mContext).inflate(R.layout.adapter_owner_list, null);
            holderView.tv_name = (TextView) currentView.findViewById(R.id.tv_name);
            holderView.tv_address = (TextView) currentView.findViewById(R.id.tv_address);
            holderView.tv_status = (TextView) currentView.findViewById(R.id.tv_status);
            holderView.tv_authen = (TextView) currentView.findViewById(R.id.tv_authen);
            holderView.tv_logintime = (TextView) currentView.findViewById(R.id.tv_logintime);
            holderView.iv_call = (ImageView) currentView.findViewById(R.id.iv_call);

            currentView.setTag(holderView);
        } else {
            holderView = (HolderView) currentView.getTag();
        }

        if (arrayList.size() != 0) {
            Object obj = arrayList.get(position);
            final UserInfo userInfo = (UserInfo) obj;

            switch (userInfo.IsAuthen) {
                case 1:
                    holderView.tv_status.setText("未认证");
                    holderView.tv_authen.setVisibility(View.VISIBLE);
                    holderView.tv_authen.setTag(userInfo);
                    holderView.tv_authen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //showCustomAlertDialog();
                            Intent intent = new Intent(mContext, AuthenActivity.class);
                            intent.putExtra("UserAccountID", userInfo.ID);
                            intent.putExtra("HouseID", userInfo.HouseID);
                            intent.putExtra("UserName", userInfo.UserName);
                            intent.putExtra("Phone", userInfo.Phone);

                            ((Activity) mContext).startActivityForResult(intent, JUMP_TO_DETAIL);
                        }
                    });
                    break;
                case 2:
                    holderView.tv_status.setText("已认证");
                    holderView.tv_authen.setVisibility(View.GONE);
                    break;
                case 3:
                    holderView.tv_status.setText("认证失败");
                    holderView.tv_authen.setVisibility(View.GONE);
                    break;
            }

            holderView.tv_name.setText(userInfo.UserName);
            holderView.tv_address.setText(userInfo.Address);
            if (userInfo.LastGetIntegralTime != null)
                holderView.tv_logintime.setText(userInfo.LastGetIntegralTime.replaceAll("T", " "));
            holderView.iv_call.setTag(userInfo.Phone);

            holderView.iv_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //用intent启动拨打电话
                    if (v.getTag() == null)
                        return;

                    String mobile = v.getTag().toString();

                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE);
                        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((OwnerInquiryActivity) mContext, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_CALL_PHONE);
                        } else {
                            //上面已经写好的拨号方法
                            callDirectly(mobile);
                        }
                    } else {
                        //上面已经写好的拨号方法
                        callDirectly(mobile);
                    }
                }
            });
        }

        return currentView;
    }

    //拨打电话
    private void callDirectly(String mobile) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:" + mobile));
        mContext.startActivity(intent);
    }

    private void showCustomAlertDialog() {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.show();
        Window win = alertDialog.getWindow();

        WindowManager.LayoutParams lp = win.getAttributes();
        win.setGravity(Gravity.CENTER);
        lp.alpha = 0.9f;
        win.setAttributes(lp);
        win.setContentView(R.layout.dialog);

    }

    public class HolderView {
        private ImageView iv_call;
        private TextView tv_name;
        private TextView tv_authen;
        private TextView tv_address;
        private TextView tv_status;
        private TextView tv_logintime;
    }
}
