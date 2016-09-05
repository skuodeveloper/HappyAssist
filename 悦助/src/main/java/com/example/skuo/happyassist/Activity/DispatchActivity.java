package com.example.skuo.happyassist.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.skuo.happyassist.Class.Request.RequestParam;
import com.example.skuo.happyassist.Class.Result.Manager;
import com.example.skuo.happyassist.Class.Result.ManagerInfo;
import com.example.skuo.happyassist.Javis.Adapter.Adapter_dispatch;
import com.example.skuo.happyassist.Javis.Tools.Utils;
import com.example.skuo.happyassist.Javis.http.GetHttp;
import com.example.skuo.happyassist.Javis.http.Interface;
import com.example.skuo.happyassist.Javis.http.PostHttp;
import com.example.skuo.happyassist.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DispatchActivity extends AppCompatActivity {
    private final static int PAGESIZE = 100;
    private static final int OPEN_WAIT_DIALOG = 2;
    private static final int CLOSE_WAIT_DIALOG = 3;
    private final static int REQUEST_DISPATCH_CODE = 4;

    protected Context mContext;
    private ImageView iv_back;
    private ListView listView_dispatch;
    private Adapter_dispatch apt_dispatch;
    private String sRepairID;
    private int sEstateID;
    private ProgressDialog dialog = null;

    /**
     * 存储网络返回的数据
     */
    private HashMap<String, Object> hashMap;
    /**
     * 存储网络返回的数据中的data字段
     */
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

    public static int PrePage = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        sRepairID = getIntent().getStringExtra("ID");
        sEstateID = getIntent().getIntExtra("EstateID",-1);
        PrePage = getIntent().getIntExtra("PAGE", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);

        initView();

        //请求网络数据
        new WareTask().execute();
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

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OPEN_WAIT_DIALOG:
                    openDialog();
                    break;
                case CLOSE_WAIT_DIALOG:
                    closeDialog();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("派单处理");

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.iv_back:
                                finish();
                                break;
                        }
                    }
                }
        );

        ((Button) findViewById(R.id.btnSubmit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 根据RadioButton的选择情况确定用户名
                for (int i = 0, j = listView_dispatch.getCount(); i < j; i++) {
                    View child = listView_dispatch.getChildAt(i);
                    RadioButton rdoBtn = (RadioButton) child
                            .findViewById(R.id.radio_btn);
                    if (rdoBtn.isChecked()) {
                        Object obj = (Object) arrayList.get(i);
                        ManagerInfo mInfo = (ManagerInfo) obj;
                        new WareTask1(mInfo).execute();
                        break;
                    }
                }
            }
        });

        listView_dispatch = (ListView) findViewById(R.id.listView_dispatch);
        apt_dispatch = new Adapter_dispatch(this, arrayList);
        listView_dispatch.setAdapter(apt_dispatch);
    }

    private class WareTask extends AsyncTask<Void, Void, HashMap<String, Object>> {

        @Override
        protected HashMap<String, Object> doInBackground(Void... arg0) {
            try {
                Map<String, Object> param = new HashMap<String, Object>();
                RequestParam req = new RequestParam();
                req.page = 1;
                req.pageSize = PAGESIZE;
                req.EstateID = sEstateID;
                param = Utils.objectToMap(req);

                //请求数据，返回json
                String jsonStr = GetHttp.RequstGetHttp(Interface.GetCommonManagerList, param);
                Manager Rep = JSON.parseObject(jsonStr, Manager.class);

                hashMap = new HashMap<>();
                hashMap.put("data", Rep.Data.RepairCommonManagers);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return hashMap;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(HashMap<String, Object> result) {

            //如果网络数据请求失败，那么显示默认的数据
            if (result != null && result.get("data") != null) {
                arrayList = (ArrayList<HashMap<String, Object>>) result.get("data");
                if (apt_dispatch != null) {
                    apt_dispatch.arrayList = arrayList;
                    apt_dispatch.notifyDataSetChanged();
                }
            }
        }
    }

    private class WareTask1 extends AsyncTask<Void, Void, HashMap<String, Object>> {
        ManagerInfo managerInfo;

        public WareTask1(ManagerInfo managerInfo) {
            this.managerInfo = managerInfo;
        }

        @Override
        protected void onPreExecute() {
            handler.sendEmptyMessage(OPEN_WAIT_DIALOG);
        }


        @Override
        protected HashMap<String, Object> doInBackground(Void... arg0) {
            try {
                Map<String, String> params = new HashMap<String, String>();

                params.put("AccountID", String.valueOf(managerInfo.AccountID));
                params.put("TrueName", managerInfo.TrueName);

                if (PrePage == 1)//投诉建议
                {
                    params.put("ComplaintID", String.valueOf(sRepairID));
                    PostHttp.RequstPostHttp(Interface.SubmitAssignHandle1, params);
                }
                else
                {
                    params.put("RepairID", String.valueOf(sRepairID));
                    PostHttp.RequstPostHttp(Interface.SubmitAssignHandle, params);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(HashMap<String, Object> result) {
            if (PrePage == 1)//投诉建议
                ComplaintDetailActivity.ActionType = 1;
            else//报修
                RepairDetailActivity.ActionType = 1;

            finish();
            handler.sendEmptyMessage(CLOSE_WAIT_DIALOG);
        }
    }
}
