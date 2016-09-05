package com.example.skuo.happyassist.Javis.Adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.skuo.happyassist.Activity.OwnerInquiryActivity;
import com.example.skuo.happyassist.Class.Result.ManagerInfo;
import com.example.skuo.happyassist.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 16-6-28.
 */
public class Adapter_dispatch extends BaseAdapter {
    private final static int REQUEST_CODE_ASK_CALL_PHONE = 123;
    public ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
    private Context mContext;
    private HashMap<String, Boolean> states = new HashMap<String, Boolean>();//用于记录每个RadioButton的状态，并保证只可选一个

    public Adapter_dispatch(Context context) {
        this.mContext = context;
    }

    public Adapter_dispatch(Context context, ArrayList<HashMap<String, Object>> arrayList) {
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
        return getItem(position);
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

            currentView = LayoutInflater.from(mContext).inflate(R.layout.adapter_dispatch, null);
            holderView.tv_name = (TextView) currentView.findViewById(R.id.trueName);
            holderView.iv_call = (ImageView) currentView.findViewById(R.id.iv_call);

            currentView.setTag(holderView);
        } else {
            holderView = (HolderView) currentView.getTag();
        }

        if (arrayList.size() != 0) {
            Object obj = arrayList.get(position);
            ManagerInfo managerInfo = (ManagerInfo) obj;

            holderView.tv_name.setTag(String.valueOf(managerInfo.AccountID));
            holderView.tv_name.setText(managerInfo.TrueName);
            holderView.iv_call.setTag(managerInfo.Phone);

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

            final RadioButton radio = (RadioButton) currentView.findViewById(R.id.radio_btn);
            holderView.rdBtn = radio;
            holderView.rdBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //重置，确保最多只有一项被选中
                    for (String key : states.keySet()) {
                        states.put(key, false);
                    }
                    states.put(String.valueOf(position), radio.isChecked());
                    Adapter_dispatch.this.notifyDataSetChanged();
                }
            });

            boolean res = false;
            if (states.get(String.valueOf(position)) == null || states.get(String.valueOf(position)) == false) {
                res = false;
                states.put(String.valueOf(position), false);
            } else
                res = true;

            holderView.rdBtn.setChecked(res);
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

    public class HolderView {
        private RadioButton rdBtn;
        private ImageView iv_call;
        private TextView tv_name;
    }
}
