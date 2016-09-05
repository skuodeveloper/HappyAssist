package com.example.skuo.happyassist.Javis.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.skuo.happyassist.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 16-6-28.
 */
public class Adapter_door_list extends BaseAdapter {
    private Context mContext;
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

    public Adapter_door_list(Context context) {
        this.mContext = context;
    }

    public Adapter_door_list(Context context, ArrayList<HashMap<String, Object>> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        //TODO
        return (arrayList != null && arrayList.size() == 0) ? 10 : arrayList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(final int position, View currentView, ViewGroup arg2) {
        HolderView holderView = null;
        if (currentView == null) {
            holderView = new HolderView();
            currentView = LayoutInflater.from(mContext).inflate(R.layout.adapter_door_list, null);

            holderView.bt_open = (Button) currentView.findViewById(R.id.bt_open);
            holderView.bt_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holderView.bt_up_error = (Button) currentView.findViewById(R.id.bt_up_error);
            holderView.bt_up_error.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        } else {
            holderView = (HolderView) currentView.getTag();
        }

        return currentView;
    }

    public class HolderView {
        private TextView tv_name;
        private Button bt_open;
        private Button bt_up_error;
    }
}
