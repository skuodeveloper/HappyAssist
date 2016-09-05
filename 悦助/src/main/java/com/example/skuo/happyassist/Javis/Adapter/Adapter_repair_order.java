package com.example.skuo.happyassist.Javis.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.skuo.happyassist.Class.Result.RepairInfo;
import com.example.skuo.happyassist.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 16-6-28.
 */
public class Adapter_repair_order extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

    public Adapter_repair_order(Context context) {
        this.context = context;
    }

    public Adapter_repair_order(Context context, ArrayList<HashMap<String, Object>> arrayList) {
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
            currentView = LayoutInflater.from(context).inflate(R.layout.adapter_repair_order, null);
            holderView = new HolderView();

            holderView.ID = (TextView) currentView.findViewById(R.id.ID);
            holderView.Type = (TextView) currentView.findViewById(R.id.Type);
            holderView.Status = (TextView) currentView.findViewById(R.id.Status);
            holderView.Info = (TextView) currentView.findViewById(R.id.Info);
            holderView.Time = (TextView) currentView.findViewById(R.id.Time);
            holderView.UserName = (TextView) currentView.findViewById(R.id.UserName);
            holderView.EstateName = (TextView) currentView.findViewById(R.id.EstateName);
            holderView.ll_name = (LinearLayout) currentView.findViewById(R.id.ll_name);
            holderView.ll_show = (LinearLayout) currentView.findViewById(R.id.ll_show);

            currentView.setTag(holderView);
        } else {
            holderView = (HolderView) currentView.getTag();
        }

        if (arrayList.size() != 0) {
            Object obj = arrayList.get(position);
            RepairInfo rep = (RepairInfo) obj;

            switch (rep.Status) {
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

            switch (rep.OrderType) {
                case 1:
                    holderView.Type.setText("报修工单");
                    break;
                case 2:
                    holderView.Type.setText("建议工单");
                    break;
                default:
                    holderView.ll_show.setVisibility(View.GONE);
                    break;
            }

            holderView.ID.setText(String.valueOf(rep.ID));
            holderView.Info.setText(rep.Info);
            holderView.Time.setText(rep.Time.replaceAll("T", " "));
            holderView.UserName.setText(rep.UserName);

            if (rep.EstateName == null)
                holderView.ll_name.setVisibility(View.GONE);
            else
                holderView.EstateName.setText(rep.EstateName);

//            holderView.cb_choice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton arg0, boolean choice) {
//                    listener.getChoiceData(position, choice);
//                }
//            });
        }

        return currentView;
    }

    public class HolderView {
        private TextView ID;
        private TextView Type;
        private TextView Status;
        private TextView Time;
        private TextView Info;
        private TextView UserName;
        private TextView EstateName;
        private LinearLayout ll_name;
        private LinearLayout ll_show;
    }
}
