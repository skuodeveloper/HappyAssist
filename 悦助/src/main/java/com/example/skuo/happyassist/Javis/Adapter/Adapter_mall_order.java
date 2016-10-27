package com.example.skuo.happyassist.Javis.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.skuo.happyassist.Class.Result.OrderInfo;
import com.example.skuo.happyassist.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 16-6-28.
 */
public class Adapter_mall_order extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

    public Adapter_mall_order(Context context) {
        this.context = context;
    }

    public Adapter_mall_order(Context context, ArrayList<HashMap<String, Object>> arrayList) {
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
            currentView = LayoutInflater.from(context).inflate(R.layout.adapter_mall_order, null);

            holderView = new HolderView();
            holderView.tv_orderid = (TextView) currentView.findViewById(R.id.tv_orderid);
            holderView.tv_ordertime = (TextView) currentView.findViewById(R.id.tv_ordertime);
            holderView.tv_productname = (TextView) currentView.findViewById(R.id.tv_productname);
            holderView.tv_price = (TextView) currentView.findViewById(R.id.tv_price);
            holderView.tv_status = (TextView) currentView.findViewById(R.id.tv_status);

            currentView.setTag(holderView);
        } else {
            holderView = (HolderView) currentView.getTag();
        }

        if (arrayList.size() != 0) {
            Object obj = arrayList.get(position);
            OrderInfo god = (OrderInfo) obj;

            switch (god.Status) {
                case 1:
                    holderView.tv_status.setText("未付款");
                    break;
                case 2:
                    holderView.tv_status.setText("待发货");
                    break;
                case 3:
                    holderView.tv_status.setText("已发货");
                    break;
                case 4:
                    holderView.tv_status.setText("确认收货");
                    break;
                case 5:
                    holderView.tv_status.setText("已取消的订单");
                    break;
                case 6:
                    holderView.tv_status.setText("请求售后");
                    break;
                case 7:
                    holderView.tv_status.setText("售后完成");
                    break;
                default:
                    holderView.tv_status.setText("未定义");
                    break;
            }

            holderView.tv_productname.setText(god.GoodsNames);
            holderView.tv_orderid.setText(god.OrderCode);
            holderView.tv_ordertime.setText(god.OrderTime.replaceAll("T","").split("\\.")[0]);
            holderView.tv_price.setText("￥" + String.valueOf(god.Amount));
        }
        return currentView;
    }

    public class HolderView {
        //private ImageView iv_pic;
        private TextView tv_ordertime;
        private TextView tv_orderid;
        private TextView tv_productname;
        private TextView tv_price;
        private TextView tv_status;
    }
}
