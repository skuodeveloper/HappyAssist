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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.skuo.happyassist.Class.Request.RequestParam;
import com.example.skuo.happyassist.Class.Result.User;
import com.example.skuo.happyassist.Javis.Adapter.Adapter_door_list;
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

public class DoorInspectActivity extends Activity {
    private final static int PAGESIZE = 10;
    private final static int REQUEST_FILTER_CODE = 1;
    private static final int LOAD_DATA_FINISH = 1;//上拉刷新
    private static final int REFRESH_DATA_FINISH = 2;//下拉刷新
    private static final int OPEN_WAIT_DIALOG = 3;
    private static final int CLOSE_WAIT_DIALOG = 4;
    private static final int LAST_PAGE_ALREADY = 5;
    private static final int JUMP_TO_DETAIL = 6;

    private TextView bt_all,bt_group,bt_build,bt_cell,bt_estate;
    private View show_all,show_group,show_build,show_cell,show_estate;
    private ImageView iv_back,iv_refresh;
    //private ListView listView_door;
    private Adapter_door_list apt_door;

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
    private RequestParam req = null;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_inspect);

        req = new RequestParam();
        mContext = this;

        initView();

        reSearch();
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
                    apt_door.notifyDataSetChanged();
                    GroupList.onRefreshComplete();    //下拉刷新完成
                    break;
                case LOAD_DATA_FINISH:
                    apt_door.notifyDataSetChanged();
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

    /**
     * 重新检索
     */
    private void reSearch() {
        req.page = 1;
        req.pageSize = PAGESIZE;

        req.AccountID = USERINFO.AccountID;
        req.AccountType = USERINFO.AccountType;
        req.PropertyID = USERINFO.PropertyID;

        req.Status = currentStatus;
        req.Phone = "";
        pageIndex = 1;

        //请求网络数据
        new WareTask().execute();
    }

    View.OnClickListener hander = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.iv_refresh:
                    Intent intent = new Intent(DoorInspectActivity.this,OwnerFilterActivity.class);
                    startActivity(intent);
                    break;
                case R.id.bt_all:
                    show_all.setBackgroundColor(getResources().getColor(R.color.bg_Black));
                    show_group.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_build.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_cell.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_estate.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    break;
                case R.id.bt_group:
                    show_all.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_group.setBackgroundColor(getResources().getColor(R.color.bg_Black));
                    show_build.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_cell.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_estate.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    break;
                case R.id.bt_build:
                    show_all.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_group.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_build.setBackgroundColor(getResources().getColor(R.color.bg_Black));
                    show_cell.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_estate.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    break;
                case R.id.bt_cell:
                    show_all.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_group.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_build.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_cell.setBackgroundColor(getResources().getColor(R.color.bg_Black));
                    show_estate.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    break;
                case R.id.bt_estate:
                    show_all.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_group.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_build.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_cell.setBackgroundColor(getResources().getColor(R.color.bg_Gray));
                    show_estate.setBackgroundColor(getResources().getColor(R.color.bg_Black));
                    break;
                default:
                    break;
            }
        }
    };

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("门禁巡检");

        bt_all = (TextView) findViewById(R.id.bt_all);
        bt_group = (TextView) findViewById(R.id.bt_group);
        bt_build = (TextView) findViewById(R.id.bt_build);
        bt_estate = (TextView) findViewById(R.id.bt_estate);
        bt_cell = (TextView) findViewById(R.id.bt_cell);

        show_all = findViewById(R.id.show_all);
        show_group = findViewById(R.id.show_group);
        show_build = findViewById(R.id.show_build);
        show_cell = findViewById(R.id.show_cell);
        show_estate = findViewById(R.id.show_estate);

        bt_all.setOnClickListener(hander);
        bt_group.setOnClickListener(hander);
        bt_build.setOnClickListener(hander);
        bt_estate.setOnClickListener(hander);
        bt_cell.setOnClickListener(hander);

        iv_back =(ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(hander);

        iv_refresh = (ImageView) findViewById(R.id.iv_refresh);
        iv_refresh.setOnClickListener(hander);
        GroupList = (MyCustomListView) findViewById(R.id.GroupList);

        apt_door = new Adapter_door_list(mContext, arrayList);
        GroupList.setAdapter(apt_door);
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

                if (apt_door != null) {
                    apt_door.notifyDataSetChanged();
                }
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
                    req.pageSize = PAGESIZE;
                    req.page = pageIndex;
                    req.Status = currentStatus;

                    req.EstateID = data.getIntExtra("EstateID", 0);
                    req.GroupID = data.getIntExtra("GroupID",0);
                    req.BuildingID = data.getIntExtra("BuildingID",0);
                    req.CellID = data.getIntExtra("CellID",0);
                    req.HouseID = data.getIntExtra("HouseID",0);
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
}
