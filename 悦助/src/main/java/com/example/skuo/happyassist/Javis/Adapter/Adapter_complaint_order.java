package com.example.skuo.happyassist.Javis.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.skuo.happyassist.Class.Result.ComplaintInfo;
import com.example.skuo.happyassist.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 16-6-28.
 */
public class Adapter_complaint_order extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

    public Adapter_complaint_order(Context context) {
        this.context = context;
    }

    public Adapter_complaint_order(Context context, ArrayList<HashMap<String, Object>> arrayList) {
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
            currentView = LayoutInflater.from(context).inflate(R.layout.adapter_complaint_order, null);
            holderView = new HolderView();

            holderView.ID = (TextView) currentView.findViewById(R.id.ID);
            holderView.Status = (TextView) currentView.findViewById(R.id.Status);
            holderView.Info = (TextView) currentView.findViewById(R.id.Info);
            holderView.Time = (TextView) currentView.findViewById(R.id.Time);
            holderView.UserName = (TextView) currentView.findViewById(R.id.UserName);
            holderView.EstateName = (TextView) currentView.findViewById(R.id.EstateName);

            currentView.setTag(holderView);
        } else {
            holderView = (HolderView) currentView.getTag();
        }

        if (arrayList.size() != 0) {
            Object obj = arrayList.get(position);
            ComplaintInfo complaintInfo = (ComplaintInfo) obj;

            switch (complaintInfo.Status) {
                case 1:
                    holderView.Status.setText("待处理");
                    break;
                case 2:
                    holderView.Status.setText("处理中");
                    break;
                case 3:
                    holderView.Status.setText("已完成");
                    break;
                default:
                    holderView.Status.setText("已结束");
                    break;
            }

            holderView.ID.setText(String.valueOf(complaintInfo.ID));
            holderView.Info.setText(complaintInfo.Info);
            holderView.Time.setText(complaintInfo.Time.replaceAll("T", " "));
            holderView.UserName.setText(complaintInfo.UserName);
            holderView.EstateName.setText(complaintInfo.EstateName);

        }

        return currentView;
    }

    public class HolderView {
        private TextView ID;
        private TextView Status;
        private TextView Time;
        private TextView Info;
        private TextView UserName;
        private TextView EstateName;
    }
}
