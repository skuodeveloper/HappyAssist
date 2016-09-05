package com.example.skuo.happyassist.Javis.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.skuo.happyassist.R;
import com.example.skuo.happyassist.Util.Bimp;

/**
 * Created by Administrator on 16-7-28.
 */
@SuppressLint("HandlerLeak")
public class Adapter_Grid_View extends BaseAdapter {
    private LayoutInflater inflater;
    private int selectedPosition = -1;
    private boolean shape;
    private Context context;

    public Adapter_Grid_View(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public boolean isShape() {
        return shape;
    }

    public void setShape(boolean shape) {
        this.shape = shape;
    }

    public int getCount() {
        if (Bimp.tempSelectBitmap.size() == 9) {
            return 9;
        }
        return (Bimp.tempSelectBitmap.size() + 1);
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_published_grida,
                    parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView
                    .findViewById(R.id.item_grida_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == Bimp.tempSelectBitmap.size()) {
            holder.image.setImageBitmap(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.icon_addpic_unfocused));
            if (position == 9) {
                holder.image.setVisibility(View.GONE);
            }
        } else {
            holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
        }

        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
    }

//    Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    adapter.notifyDataSetChanged();
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    };
//
//    public void loading() {
//        new Thread(new Runnable() {
//            public void run() {
//                while (true) {
//                    if (Bimp.max == Bimp.tempSelectBitmap.size()) {
//                        Message message = new Message();
//                        message.what = 1;
//                        handler.sendMessage(message);
//                        break;
//                    } else {
//                        Bimp.max += 1;
//                        Message message = new Message();
//                        message.what = 1;
//                        handler.sendMessage(message);
//                    }
//                }
//            }
//        }).start();
//    }
}
