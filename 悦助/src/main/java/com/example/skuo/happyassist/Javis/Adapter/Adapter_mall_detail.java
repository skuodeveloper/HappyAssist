package com.example.skuo.happyassist.Javis.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skuo.happyassist.Activity.AfterSaleActivity;
import com.example.skuo.happyassist.Activity.OrderDetailActivity;
import com.example.skuo.happyassist.Class.Result.GoodsInfo;
import com.example.skuo.happyassist.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 16-6-28.
 */
public class Adapter_mall_detail extends BaseAdapter {
    private final static int REQUEST_AFTER_SALE = 1;

    private GoodsInfo gInfo = null;
    private Context context;
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

    public Adapter_mall_detail(Context context) {
        this.context = context;
    }

    public Adapter_mall_detail(Context context, ArrayList<HashMap<String, Object>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    /**
     * 从服务器取图片
     *
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
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
            currentView = LayoutInflater.from(context).inflate(R.layout.adapter_mall_detail, null);

            holderView = new HolderView();
            holderView.imageView = (ImageView) currentView.findViewById(R.id.imageView);
            holderView.btnRequest = (Button) currentView.findViewById(R.id.btnRequest);
            holderView.goodsName = (TextView) currentView.findViewById(R.id.goodsName);
            holderView.status = (TextView) currentView.findViewById(R.id.status);
            holderView.quantity = (TextView) currentView.findViewById(R.id.quantity);

            currentView.setTag(holderView);
        } else {
            holderView = (HolderView) currentView.getTag();
        }

        if (arrayList.size() != 0) {
            Object obj = arrayList.get(position);
            gInfo = (GoodsInfo) obj;

            holderView.imageView.setImageBitmap(getHttpBitmap(gInfo.GoodsImg));
            holderView.goodsName.setText(gInfo.GoodsName);
            holderView.quantity.setText("数量：" + String.valueOf(gInfo.Quantity));

            switch (gInfo.RefundType) {
                case 1://退款
                    switch (gInfo.Status) {
                        case 1:
                            holderView.btnRequest.setVisibility(View.VISIBLE);
                            holderView.btnRequest.setText("退款");
                            holderView.btnRequest.setTag(gInfo);
                            holderView.status.setVisibility(View.GONE);
                            break;
                        case 2:
                            holderView.btnRequest.setVisibility(View.GONE);
                            holderView.status.setVisibility(View.VISIBLE);
                            holderView.status.setText("同意退款");
                            break;
                        case 3:
                            holderView.btnRequest.setVisibility(View.GONE);
                            holderView.status.setVisibility(View.VISIBLE);
                            holderView.status.setText("拒绝退款");
                            break;
                    }
                    break;
                case 2://退款退货
                    switch (gInfo.Status) {
                        case 1:
                            holderView.btnRequest.setVisibility(View.VISIBLE);
                            holderView.btnRequest.setText("退款退货");
                            holderView.btnRequest.setTag(gInfo);
                            holderView.status.setVisibility(View.GONE);
                            break;
                        case 2:
                            holderView.btnRequest.setVisibility(View.GONE);
                            holderView.status.setVisibility(View.VISIBLE);
                            holderView.status.setText("同意退款退货");
                            break;
                        case 3:
                            holderView.btnRequest.setVisibility(View.GONE);
                            holderView.status.setVisibility(View.VISIBLE);
                            holderView.status.setText("拒绝退款退货");
                            break;
                    }
                    break;
                case 3://换货
                    switch (gInfo.Status) {
                        case 1:
                            holderView.btnRequest.setVisibility(View.VISIBLE);
                            holderView.btnRequest.setText("换货");
                            holderView.btnRequest.setTag(gInfo);
                            holderView.status.setVisibility(View.GONE);
                            break;
                        case 2:
                            holderView.btnRequest.setVisibility(View.GONE);
                            holderView.status.setVisibility(View.VISIBLE);
                            holderView.status.setText("同意换货");
                            break;
                        case 3:
                            holderView.btnRequest.setVisibility(View.GONE);
                            holderView.status.setVisibility(View.VISIBLE);
                            holderView.status.setText("拒绝换货");
                            break;
                    }
                    break;
            }

            //退款
            holderView.btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AfterSaleActivity.goodsInfo = (GoodsInfo) v.getTag();
                    AfterSaleActivity.goodsList = OrderDetailActivity.data;

                    Intent intent = new Intent(context, AfterSaleActivity.class);
                    ((Activity) context).startActivityForResult(intent, REQUEST_AFTER_SALE);
                }
            });
        }
        return currentView;
    }

    public class HolderView {
        private ImageView imageView;
        private TextView status;
        private Button btnRequest;
        private TextView goodsName;
        private TextView amount;
        private TextView quantity;

    }
}
