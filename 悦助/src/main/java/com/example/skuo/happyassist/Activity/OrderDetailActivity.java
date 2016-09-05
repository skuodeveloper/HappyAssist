package com.example.skuo.happyassist.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.skuo.happyassist.Class.Result.Goods;
import com.example.skuo.happyassist.Class.Result.GoodsInfo;
import com.example.skuo.happyassist.Class.Result.GoodsList;
import com.example.skuo.happyassist.Javis.Adapter.Adapter_mall_detail;
import com.example.skuo.happyassist.Javis.http.GetHttp;
import com.example.skuo.happyassist.Javis.http.Interface;
import com.example.skuo.happyassist.Javis.http.PostHttp;
import com.example.skuo.happyassist.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailActivity extends Activity {
    private final static int REQUEST_AFTER_SALE = 1;
    private final static int REQUEST_SHOW_DETAIL = 2;
    private static final int OPEN_WAIT_DIALOG = 3;
    private static final int CLOSE_WAIT_DIALOG = 4;

    private LinearLayout ll_xlistView;
    private TextView tv_orderid, tv_time, tv_status, tv_price, tv_receiver, tv_mobile, tv_address;
    private Button btnDeliver;
    private ImageView iv_back;
    private ListView listView_detail;
    private Adapter_mall_detail apt_detail;
    private ProgressDialog dialog = null;
    private Context mContext;

    private ArrayList<GoodsInfo> GoodsInfoExs;

    private String ID;
    public static int ActionType = 0;
    public static GoodsList data = null;

    /**
     * 存储网络返回的数据
     */
    private HashMap<String, Object> hashMap;
    /**
     * 存储网络返回的数据中的data字段
     */
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        ID = this.getIntent().getStringExtra("ID");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_deatil);

        initView();
    }

    View.OnClickListener hander = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                switch (v.getId()) {
                    case R.id.iv_back:
                        finish();
                        break;
                    case R.id.btnDeliver:
                        MallOrdersActivity.ActionType = 1;

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("ID", String.valueOf(ID));

                        PostHttp.RequstPostHttp(Interface.SendGoods, params);
                        finish();
                        break;
                }
            } catch (Exception ex) {
                Toast.makeText(mContext, ex.getMessage().toString(), Toast.LENGTH_LONG);
            }
        }
    };

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("订单详情");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(hander);

        btnDeliver = (Button) findViewById(R.id.btnDeliver);
        btnDeliver.setOnClickListener(hander);

        apt_detail = new Adapter_mall_detail(this, arrayList);
        listView_detail = (ListView) findViewById(R.id.listView_detail);
        listView_detail.setAdapter(apt_detail);

        ll_xlistView = (LinearLayout) findViewById(R.id.ll_xlistView);

        //请求网络数据
        new WareTask().execute();
    }

    /*
      handle
    */
    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case OPEN_WAIT_DIALOG:
                    openDialog();
                    break;
                case CLOSE_WAIT_DIALOG:
                    closeDialog();
                    break;
                case REQUEST_SHOW_DETAIL:
                    Object obj = msg.obj;
                    GoodsList gList = (GoodsList) obj;

                {//判断所有商品是否处理完毕，是就关闭本activity,回到前activity
//                    boolean closefg = true;
//                    for (GoodsInfo gInfo : gList.GoodsInfoExs) {
//                        if (gInfo.Status == 1) {
//                            closefg = false;
//                            break;
//                        }
//                    }

                    if (gList.Status == 7) {
                        MallOrdersActivity.ActionType = 1;
                        finish();
                    }
                }

                tv_orderid = (TextView) findViewById(R.id.tv_orderid);
                tv_orderid.setText(gList.OrderCode);

                tv_time = (TextView) findViewById(R.id.tv_time);
                tv_time.setText(gList.OrderTime);

                tv_status = (TextView) findViewById(R.id.tv_status);
                switch (gList.Status) {
                    case 1:
                        tv_status.setText("未付款");
                        break;
                    case 2:
                        tv_status.setText("待发货");
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 0, 0, 50);
                        ll_xlistView.setLayoutParams(layoutParams);
                        btnDeliver.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        tv_status.setText("已发货");
                        break;
                    case 4:
                        tv_status.setText("确认收货");
                        break;
                    case 5:
                        tv_status.setText("已取消的订单");
                        break;
                    case 6:
                        tv_status.setText("请求售后");
                        break;
                    case 7:
                        tv_status.setText("售后完成");
                        break;
                    default:
                        tv_status.setText("未定义");
                        break;
                }

                tv_price = (TextView) findViewById(R.id.tv_price);
                tv_price.setText("￥" + String.valueOf(gList.Amount));

                tv_receiver = (TextView) findViewById(R.id.tv_receiver);
                tv_receiver.setText(gList.Receriver);

                tv_mobile = (TextView) findViewById(R.id.tv_mobile);
                tv_mobile.setText(gList.PhoneNo);

                tv_address = (TextView) findViewById(R.id.tv_address);
                tv_address.setText(gList.RecAddress);

                hashMap.clear();
                hashMap.put("data", gList.GoodsInfoExs);
                arrayList.clear();
                arrayList.addAll((Collection<? extends HashMap<String, Object>>) hashMap.get("data"));
                apt_detail.notifyDataSetChanged();
                break;
            }
        }
    };

    /**
     * 打开等待进度条
     */
    private void openDialog() {
        if (dialog == null) {
            dialog = ProgressDialog.show(mContext, "", "正在加载...");
            dialog.show();
        }
    }

    /**
     * 关闭等待进度条
     */
    private void closeDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private class WareTask extends AsyncTask<Void, Void, HashMap<String, Object>> {

        @Override
        protected void onPreExecute() {
            myHandler.sendEmptyMessage(OPEN_WAIT_DIALOG);
        }

        @Override
        protected HashMap<String, Object> doInBackground(Void... arg0) {
            try {
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("ID", ID);

                //请求数据，返回json
                String jsonStr = GetHttp.RequstGetHttp(Interface.GetOrderDetail, param);
                Goods goods = JSON.parseObject(jsonStr, Goods.class);
                data = goods.Data;
                hashMap = new HashMap<>();
                hashMap.put("data", goods.Data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return hashMap;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(HashMap<String, Object> result) {
            myHandler.sendEmptyMessage(CLOSE_WAIT_DIALOG);

            try {
                if (result != null && result.get("data") != null) {
                    Message msg = new Message();
                    msg.what = REQUEST_SHOW_DETAIL;
                    msg.obj = result.get("data");
                    myHandler.handleMessage(msg);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_AFTER_SALE:
                if (this.ActionType == 1) {
                    {
                        this.ActionType = 0;

                        //请求网络数据
                        new WareTask().execute();
                    }
                }
                break;
            default:
                break;
        }
    }
}
