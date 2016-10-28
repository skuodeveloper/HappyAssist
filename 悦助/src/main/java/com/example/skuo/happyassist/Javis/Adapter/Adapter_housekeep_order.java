package com.example.skuo.happyassist.Javis.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.skuo.happyassist.Class.Result.HouseKeepInfo;
import com.example.skuo.happyassist.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 16-6-28.
 */
public class Adapter_housekeep_order extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

    public Adapter_housekeep_order(Context context) {
        this.context = context;
    }

    public Adapter_housekeep_order(Context context, ArrayList<HashMap<String, Object>> arrayList) {
        this.context = context;
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
            currentView = LayoutInflater.from(context).inflate(R.layout.adapter_housekeep_order, null);

            holderView = new HolderView();
            holderView.tv_orderid = (TextView) currentView.findViewById(R.id.tv_orderid);
            holderView.tv_ordertime = (TextView) currentView.findViewById(R.id.tv_ordertime);
            holderView.tv_Contacts = (TextView) currentView.findViewById(R.id.tv_Contacts);
            holderView.tv_content = (TextView) currentView.findViewById(R.id.tv_content);
            holderView.tv_EstateName = (TextView) currentView.findViewById(R.id.tv_EstateName);
            holderView.tv_status = (TextView) currentView.findViewById(R.id.tv_status);

            currentView.setTag(holderView);
        } else {
            holderView = (HolderView) currentView.getTag();
        }

        if (arrayList.size() != 0) {
            Object obj = arrayList.get(position);
            HouseKeepInfo hkInfo = (HouseKeepInfo) obj;

            switch (hkInfo.Status) {
                case 1:
                    holderView.tv_status.setText("未派单");
                    break;
                case 2:
                    holderView.tv_status.setText("派单中");
                    break;
                case 3:
                    holderView.tv_status.setText("已完成");
                    break;
                case 4:
                    holderView.tv_status.setText("已取消");
                    break;
            }

            holderView.tv_orderid.setText(String.valueOf(hkInfo.ID));
            holderView.tv_Contacts.setText(hkInfo.Contacts);
            holderView.tv_content.setText(hkInfo.ServiceName);
            holderView.tv_EstateName.setText(hkInfo.EstateName);
            holderView.tv_ordertime.setText(hkInfo.AppointmentTime);

        }
        return currentView;
    }

    public class HolderView {
        private TextView tv_orderid;
        private TextView tv_Contacts;
        private TextView tv_content;
        private TextView tv_EstateName;
        private TextView tv_ordertime;
        private TextView tv_status;
    }
}
