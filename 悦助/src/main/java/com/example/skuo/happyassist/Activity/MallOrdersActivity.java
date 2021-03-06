package com.example.skuo.happyassist.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.skuo.happyassist.Class.Request.RequestParam;
import com.example.skuo.happyassist.Class.Result.Order;
import com.example.skuo.happyassist.Class.Result.OrderInfo;
import com.example.skuo.happyassist.Javis.Adapter.Adapter_mall_order;
import com.example.skuo.happyassist.Javis.Data.USERINFO;
import com.example.skuo.happyassist.Javis.MyView.MyCustomListView;
import com.example.skuo.happyassist.Javis.Tools.Utils;
import com.example.skuo.happyassist.Javis.http.GetHttp;
import com.example.skuo.happyassist.Javis.http.Interface;
import com.example.skuo.happyassist.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class MallOrdersActivity extends FragmentActivity {
    private final static int PAGESIZE = 10;
    private final static int REQUEST_FILTER_CODE = 1;
    private static final int LOAD_DATA_FINISH = 1;//上拉刷新
    private static final int REFRESH_DATA_FINISH = 2;//下拉刷新
    private static final int OPEN_WAIT_DIALOG = 3;
    private static final int CLOSE_WAIT_DIALOG = 4;
    private static final int LAST_PAGE_ALREADY = 5;
    private static final int JUMP_TO_DETAIL = 6;

    private ImageView iv_back, iv_refresh;
    private TextView bt_all, bt_waiting, bt_processing, bt_completed;
    private View show_all, show_waitting, show_processing, show_completed;

    private Adapter_mall_order rep_Adapter;
    private MyCustomListView GroupList;//自定义ListView

    protected Context mContext;
    private ProgressDialog dialog = null;
    private boolean adddata = false;//记录是否累加
    /**
     * 加载数据条数
     */
    private int TotalCount = 0;

    /**
     * 请求数据的页数
     */
    private int pageIndex = 1;

    /**
     * 请求条件
     */
    private RequestParam req = new RequestParam();
    ;

    /**
     * 当前页面状态
     */
    private int currentStatus = 0;

    /**
     * 存储网络返回的数据
     */
    private HashMap<String, Object> hashMap;
    /**
     * 存储网络返回的数据中的data字段
     */
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

    public static int ActionType = 0;

    View.OnClickListener hander = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.iv_refresh:
                    Intent intent = new Intent(MallOrdersActivity.this, RepairFilterActivity.class);
                    startActivityForResult(intent, REQUEST_FILTER_CODE);
                    break;
                case R.id.bt_all:
                    bt_all.setTextColor(getResources().getColor(R.color.darkgreen));
                    bt_waiting.setTextColor(getResources().getColor(R.color.bg_Gray));
                    bt_processing.setTextColor(getResources().getColor(R.color.bg_Gray));
                    bt_completed.setTextColor(getResources().getColor(R.color.bg_Gray));

                    show_all.setBackgroundColor(getResources().getColor(R.color.darkgreen));
                    show_waitting.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_processing.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_completed.setBackgroundColor(getResources().getColor(R.color.bg_Gray));

                    currentStatus = 0;
                    reSearch(currentStatus);
                    break;
                case R.id.bt_waiting:
                    bt_all.setTextColor(getResources().getColor(R.color.bg_Gray));
                    bt_waiting.setTextColor(getResources().getColor(R.color.darkgreen));
                    bt_processing.setTextColor(getResources().getColor(R.color.bg_Gray));
                    bt_completed.setTextColor(getResources().getColor(R.color.bg_Gray));

                    show_all.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_waitting.setBackgroundColor(getResources().getColor(R.color.darkgreen));
                    show_processing.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_completed.setBackgroundColor(getResources().getColor(R.color.bg_Gray));


                    currentStatus = 2;
                    reSearch(currentStatus);
                    break;
                case R.id.bt_processing:
                    bt_all.setTextColor(getResources().getColor(R.color.bg_Gray));
                    bt_waiting.setTextColor(getResources().getColor(R.color.bg_Gray));
                    bt_processing.setTextColor(getResources().getColor(R.color.darkgreen));
                    bt_completed.setTextColor(getResources().getColor(R.color.bg_Gray));

                    show_all.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_waitting.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_processing.setBackgroundColor(getResources().getColor(R.color.darkgreen));
                    show_completed.setBackgroundColor(getResources().getColor(R.color.bg_Gray));

                    currentStatus = 3;
                    reSearch(currentStatus);
                    break;
                case R.id.bt_completed:
                    bt_all.setTextColor(getResources().getColor(R.color.bg_Gray));
                    bt_waiting.setTextColor(getResources().getColor(R.color.bg_Gray));
                    bt_processing.setTextColor(getResources().getColor(R.color.bg_Gray));
                    bt_completed.setTextColor(getResources().getColor(R.color.darkgreen));

                    show_all.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_waitting.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_processing.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_completed.setBackgroundColor(getResources().getColor(R.color.darkgreen));

                    currentStatus = 6;
                    reSearch(currentStatus);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_order);
        mContext = this;

        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("商城订单");

        bt_all = (TextView) findViewById(R.id.bt_all);
        bt_waiting = (TextView) findViewById(R.id.bt_waiting);
        bt_processing = (TextView) findViewById(R.id.bt_processing);
        bt_completed = (TextView) findViewById(R.id.bt_completed);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_refresh = (ImageView) findViewById(R.id.iv_refresh);

        show_all = findViewById(R.id.show_all);
        show_waitting = findViewById(R.id.show_waiting);
        show_processing = findViewById(R.id.show_processing);
        show_completed = findViewById(R.id.show_completed);

        bt_all.setOnClickListener(hander);
        bt_waiting.setOnClickListener(hander);
        bt_processing.setOnClickListener(hander);
        bt_completed.setOnClickListener(hander);
        iv_back.setOnClickListener(hander);
        iv_refresh.setOnClickListener(hander);

        GroupList = (MyCustomListView) findViewById(R.id.GroupList);

        rep_Adapter = new Adapter_mall_order(mContext, arrayList);
        GroupList.setAdapter(rep_Adapter);
        GroupList.setOnRefreshListener(new MyCustomListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO 下拉刷新
                loadData(0);
            }
        });
        GroupList.setOnLoadListener(new MyCustomListView.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                // TODO 加载更多
                loadData(1);
            }
        });

        GroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                try {
                    //myHandler.sendEmptyMessage(JUMP_TO_DETAIL);
                    Message msg = new Message();
                    msg.what = JUMP_TO_DETAIL;
                    msg.obj = arrayList.get(position - 1);
                    myHandler.handleMessage(msg);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        //请求网络数据
        reSearch(currentStatus);
    }


    /*
   上下拉刷新加载数据方法
 */
    public void loadData(final int type) {
        new Thread() {
            @Override
            public void run() {
                switch (type) {
                    case 0://这里是下拉刷新
                        reSearch(currentStatus);
                        break;
                    case 1:
                        //最后一页
                        if ((pageIndex * PAGESIZE) < TotalCount) {
                            if (req != null) {
                                pageIndex++;
                                req.page++;
                                adddata = true;

                                //请求网络数据
                                new WareTask().execute();
                            }
                        } else {
                            myHandler.sendEmptyMessage(LAST_PAGE_ALREADY);
                        }

                        break;
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (type == 0) {//下拉刷新
                    //通知Handler
                    myHandler.sendEmptyMessage(REFRESH_DATA_FINISH);
                } else if (type == 1) {//上拉刷新
                    //通知Handler
                    myHandler.sendEmptyMessage(LOAD_DATA_FINISH);
                }
            }
        }.start();
    }

    /*
      handle
    */
    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_DATA_FINISH:
                    rep_Adapter.notifyDataSetChanged();
                    GroupList.onRefreshComplete();    //下拉刷新完成
                    break;
                case LOAD_DATA_FINISH:
                    rep_Adapter.notifyDataSetChanged();
                    GroupList.onLoadMoreComplete();    //加载更多完成
                    break;
                case OPEN_WAIT_DIALOG:
                    openDialog();
                    break;
                case CLOSE_WAIT_DIALOG:
                    closeDialog();
                    break;
                case LAST_PAGE_ALREADY:
                    Toast.makeText(mContext, "已经最后一页了", Toast.LENGTH_SHORT).show();
                    break;
                case JUMP_TO_DETAIL:
                    try {
                        Intent intent = new Intent(mContext, OrderDetailActivity.class);

                        OrderInfo rep = (OrderInfo) msg.obj;
                        intent.putExtra("ID", String.valueOf(rep.ID));

                        startActivityForResult(intent, JUMP_TO_DETAIL);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                default:
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

    /**
     * 重新检索
     *
     * @param status
     */
    private void reSearch(int status) {
        //req = new RequestParam();
        req.page = 1;
        req.Status = status;
        req.pageSize = PAGESIZE;
        req.EstateID = USERINFO.EstateID;
        req.AccountType = USERINFO.AccountType;
        req.AccountID = USERINFO.AccountID;
        req.PropertyID = USERINFO.PropertyID;
        pageIndex = 1;
        req.PhoneNo = "";

        //请求网络数据
        new WareTask().execute();
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
                param = Utils.objectToMap(req);
                //请求数据，返回json
                String jsonStr = GetHttp.RequstGetHttp(Interface.GetGoodsOrderList, param);
                Order Rep = JSON.parseObject(jsonStr, Order.class);

                TotalCount = Rep.Data.TotalCount;
                hashMap = new HashMap<>();
                hashMap.put("data", Rep.Data.GoodsOrderExs);
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
                //如果网络数据请求失败，那么显示默认的数据
                if (result != null && result.get("data") != null) {
                    //得到data字段的数据
                    if (!adddata)
                        arrayList.clear();
                    else
                        adddata = false;

                    arrayList.addAll((Collection<? extends HashMap<String, Object>>) result.get("data"));

                    if (rep_Adapter != null) {
                        rep_Adapter.notifyDataSetChanged();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_FILTER_CODE:
                if (data != null) {
                    req = new RequestParam();
                    req.AccountID = USERINFO.AccountID;
                    req.AccountType = USERINFO.AccountType;
                    req.PropertyID = USERINFO.PropertyID;
                    if (USERINFO.AccountType == 5 || USERINFO.AccountType == 6 )//小区管理员 或一般管理员
                        req.EstateID = USERINFO.EstateID;
                    else
                        req.EstateID = data.getIntExtra("EstateID", 0);
                    req.StartDate = data.getStringExtra("StartDate");
                    req.EndDate = data.getStringExtra("EndDate");
                    req.pageSize = PAGESIZE;
                    req.page = pageIndex;
                    req.Status = currentStatus;
                    req.PhoneNo = "";

                    //请求网络数据
                    new WareTask().execute();
                }
                break;
            case JUMP_TO_DETAIL:
                if (ActionType == 1) {
                    ActionType = 0;
                    reSearch(currentStatus);
                }
                break;
            default:
                break;
        }
    }
}
