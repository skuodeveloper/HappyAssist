package com.example.skuo.happyassist.Javis.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skuo.happyassist.Javis.Tools.BaseViewHolder;
import com.example.skuo.happyassist.R;

/**
 * @author http://blog.csdn.net/finddreams
 * @Description:gridview的Adapter
 */
public class Adapter_myGrid extends BaseAdapter {
    public String[] img_text;
    public int[] imgs = {R.drawable.app_transfer, R.drawable.app_fund,
            R.drawable.app_phonecharge, R.drawable.app_creditcard,
            R.drawable.app_movie, R.drawable.app_lottery,
            R.drawable.app_facepay, R.drawable.app_close, R.drawable.app_plane};
    private Context mContext;

    public Adapter_myGrid(Context mContext) {
        super();
        this.mContext = mContext;
        img_text = mContext.getResources().getStringArray(R.array.home_grid_texts);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return img_text.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_grid_item, parent, false);
        }
        TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
        ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);

        iv.setBackgroundResource(imgs[position]);
        tv.setText(img_text[position]);
        return convertView;
    }
}
