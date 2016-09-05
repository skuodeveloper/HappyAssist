package com.example.skuo.happyassist.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skuo.happyassist.Class.Result.GoodsInfo;
import com.example.skuo.happyassist.Class.Result.GoodsList;
import com.example.skuo.happyassist.Javis.http.Interface;
import com.example.skuo.happyassist.Javis.http.PostHttp;
import com.example.skuo.happyassist.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AfterSaleActivity extends Activity {
    public static GoodsInfo goodsInfo;
    public static GoodsList goodsList;
    int test = 0;
    View.OnClickListener hander = new View.OnClickListener() {
        public void onClick(View v) {
            Map<String, String> params = new HashMap<String, String>();
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.btnAgree:
                    OrderDetailActivity.ActionType = 1;

                    params.put("RefundID", String.valueOf(goodsInfo.GoodsRefundID));
                    PostHttp.RequstPostHttp(Interface.Refund, params);
                    finish();
                    break;
                case R.id.btnRefuse:
                    OrderDetailActivity.ActionType = 1;

                    params.put("RefundID", String.valueOf(goodsInfo.GoodsRefundID));
                    PostHttp.RequstPostHttp(Interface.Refuse, params);
                    finish();
                    break;
            }
        }
    };
    private ImageView iv_back;
    private Button btnAgree, btnRefuse;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_sale);

        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("售后详情");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(hander);

        btnAgree= (Button) findViewById(R.id.btnAgree);
        btnAgree.setOnClickListener(hander);

        btnRefuse= (Button) findViewById(R.id.btnRefuse);
        btnRefuse.setOnClickListener(hander);

        TextView tv_orderid = (TextView) findViewById(R.id.tv_orderid);
        tv_orderid.setText(goodsList.OrderCode);

        TextView tv_time = (TextView) findViewById(R.id.tv_time);
        tv_time.setText(goodsList.OrderTime);

        TextView tv_price = (TextView) findViewById(R.id.tv_price);
        tv_price.setText("￥" + String.valueOf(goodsInfo.Amount));

        TextView tv_receiver = (TextView) findViewById(R.id.tv_receiver);
        tv_receiver.setText(goodsList.Receriver);

        TextView tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        tv_mobile.setText(goodsList.PhoneNo);

        TextView tv_address = (TextView) findViewById(R.id.tv_address);
        tv_address.setText(goodsList.RecAddress);

        TextView tv_reason = (TextView) findViewById(R.id.tv_reason);
        switch (goodsInfo.Reason){
            case 1:
                tv_reason.setText("收到商品与描述不符");
                break;
            case 2:
                tv_reason.setText("商品错发/漏发");
                break;
            case 3:
                tv_reason.setText("商品质量问题");
                break;
            case 4:
                tv_reason.setText("收到商品已破损");
                break;
            case 5:
                tv_reason.setText("我不想要了");
                break;
            case 6:
                tv_reason.setText("缺货");
                break;
            case 7:
                tv_reason.setText("其他");
                break;
        }

        TextView tv_remark = (TextView) findViewById(R.id.tv_remark);
        tv_remark.setText(goodsInfo.Remark);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(getHttpBitmap(goodsInfo.GoodsImg));

        TextView goodsName = (TextView) findViewById(R.id.goodsName);
        goodsName.setText(goodsInfo.GoodsName);

        TextView amount = (TextView) findViewById(R.id.amount);
        amount.setText("￥" + goodsInfo.Amount/goodsInfo.Quantity);

        TextView quantity = (TextView) findViewById(R.id.quantity);
        quantity.setText("数量：" + goodsInfo.Quantity);
    }
}
