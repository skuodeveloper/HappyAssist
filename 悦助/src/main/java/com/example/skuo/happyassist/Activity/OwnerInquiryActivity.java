package com.example.skuo.happyassist.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.skuo.happyassist.Class.Request.RequestParam;
import com.example.skuo.happyassist.Class.Result.User;
import com.example.skuo.happyassist.Javis.Adapter.Adapter_owner_list;
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

public class OwnerInquiryActivity extends AppCompatActivity {
    private final static int PAGESIZE = 10;
    private final static int REQUEST_FILTER_CODE = 1;
    private static final int LOAD_DATA_FINISH = 1;//上拉刷新
    private static final int REFRESH_DATA_FINISH = 2;//下拉刷新
    private static final int OPEN_WAIT_DIALOG = 3;
    private static final int CLOSE_WAIT_DIALOG = 4;
    private static final int LAST_PAGE_ALREADY = 5;
    private static final int JUMP_TO_DETAIL = 6;
    public static int ActionType = 0;
    protected Context mContext;
    private ImageView iv_back, iv_refresh, iv_call;
    private TextView bt_all, bt_waiting, bt_processing, bt_completed, bt_back;
    private View show_all, show_waitting, show_processing, show_completed;
    private Adapter_owner_list apt_owner;
    private MyCustomListView GroupList;//自定义ListView
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
    private RequestParam req = null;
    /**
     * 当前页面状态
     */
    private int currentStatus = 0;
    View.OnClickListener hander = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.iv_refresh:
                    Intent intent = new Intent(OwnerInquiryActivity.this, OwnerFilterActivity.class);
                    startActivityForResult(intent, REQUEST_FILTER_CODE);
                    break;
                case R.id.bt_all:
                    show_all.setBackgroundColor(getResources().getColor(R.color.bg_Black));
                    show_waitting.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_processing.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_completed.setBackgroundColor(getResources().getColor(R.color.bg_Gray));

                    currentStatus = 0;
                    reSearch();
                    break;
                case R.id.bt_waiting:
                    show_all.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_waitting.setBackgroundColor(getResources().getColor(R.color.bg_Black));
                    show_processing.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_completed.setBackgroundColor(getResources().getColor(R.color.bg_Gray));

                    currentStatus = 1;
                    reSearch();
                    break;
                case R.id.bt_processing:
                    show_all.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_waitting.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_processing.setBackgroundColor(getResources().getColor(R.color.bg_Black));
                    show_completed.setBackgroundColor(getResources().getColor(R.color.bg_Gray));

                    currentStatus = 2;
                    reSearch();
                    break;
                case R.id.bt_completed:
                    show_all.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_waitting.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_processing.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_completed.setBackgroundColor(getResources().getColor(R.color.bg_Black));

                    currentStatus = 3;
                    reSearch();
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 存储网络返回的数据
     */
    private HashMap<String, Object> hashMap;
    /**
     * 存储网络返回的数据中的data字段
     */
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
    /*
       handle
     */
    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_DATA_FINISH:
                    apt_owner.notifyDataSetChanged();
                    GroupList.onRefreshComplete();    //下拉刷新完成
                    break;
                case LOAD_DATA_FINISH:
                    apt_owner.notifyDataSetChanged();
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
//                    try {
//                        Intent intent = new Intent(mContext, RepairDetailActivity.class);
//
//                        RepairInfo rep = (RepairInfo) msg.obj;
//                        intent.putExtra("Infos", rep);
//
//                        startActivityForResult(intent, JUMP_TO_DETAIL);
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_inquiry);

        req = new RequestParam();
        mContext = this;

        initView();

        reSearch();
    }

    /**
     * 重新检索
     */
    private void reSearch() {
        req.page = 1;
        req.pageSize = PAGESIZE;

        req.AccountID = USERINFO.AccountID;
        req.AccountType = USERINFO.AccountType;
        req.PropertyID = USERINFO.PropertyID;
        req.EstateID = USERINFO.EstateID;

        req.Status = currentStatus;
        req.Phone = "";
        pageIndex = 1;

        //请求网络数据
        new WareTask().execute();
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
                        reSearch();
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

    //拨打电话
    private void callDirectly(String mobile) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:" + mobile));
        startActivity(intent);
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("业主查询");

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(hander);

        iv_refresh = (ImageView) findViewById(R.id.iv_refresh);
        iv_refresh.setOnClickListener(hander);

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

        GroupList = (MyCustomListView) findViewById(R.id.GroupList);

        apt_owner = new Adapter_owner_list(mContext, arrayList);
        GroupList.setAdapter(apt_owner);
        GroupList.setOnRefreshListener(new MyCustomListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(0);
            }
        });
        GroupList.setOnLoadListener(new MyCustomListView.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                loadData(1);
            }
        });
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
                    req.pageSize = PAGESIZE;
                    req.page = pageIndex;
                    req.Status = currentStatus;

                    req.EstateID = data.getIntExtra("EstateID", 0);
                    req.GroupID = data.getIntExtra("GroupID", 0);
                    req.BuildingID = data.getIntExtra("BuildingID", 0);
                    req.CellID = data.getIntExtra("CellID", 0);
                    req.HouseID = data.getIntExtra("HouseID", 0);
                    req.Phone = "";

                    //请求网络数据
                    new WareTask().execute();
                }
                break;
            case JUMP_TO_DETAIL:
                if (ActionType == 1) {
                    ActionType = 0;
                    reSearch();
                }
                break;
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
                param = Utils.objectToMap(req);
                //请求数据，返回json
                String jsonStr = GetHttp.RequstGetHttp(Interface.GetUserAccountList, param);
                User user = JSON.parseObject(jsonStr, User.class);

                TotalCount = user.Data.TotalCount;
                hashMap = new HashMap<>();
                hashMap.put("data", user.Data.UserAccountExs);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return hashMap;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(HashMap<String, Object> result) {
            myHandler.sendEmptyMessage(CLOSE_WAIT_DIALOG);

            //如果网络数据请求失败，那么显示默认的数据
            if (result != null && result.get("data") != null) {
                //得到data字段的数据
                if (!adddata)
                    arrayList.clear();
                else
                    adddata = false;

                arrayList.addAll((Collection<? extends HashMap<String, Object>>) result.get("data"));

                if (apt_owner != null) {
                    apt_owner.notifyDataSetChanged();
                }
            }
        }
    }
}
