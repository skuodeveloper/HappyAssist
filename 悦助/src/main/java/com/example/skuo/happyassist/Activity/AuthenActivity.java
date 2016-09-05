package com.example.skuo.happyassist.Activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.skuo.happyassist.Class.Result.Household;
import com.example.skuo.happyassist.Class.Result.UserDetail;
import com.example.skuo.happyassist.Class.Result.UserInfo;
import com.example.skuo.happyassist.Javis.http.GetHttp;
import com.example.skuo.happyassist.Javis.http.Interface;
import com.example.skuo.happyassist.Javis.http.PostHttp;
import com.example.skuo.happyassist.R;

import java.util.HashMap;
import java.util.Map;

public class AuthenActivity extends Activity {
    private final static int REQUEST_SHOW_DETAIL = 1;

    private int UserAccountID;
    private int HouseID;
    private int HouseholdID = 0;
    private String UserName;
    private String Phone;

    private TextView regedit, authen;
    private Button btnSearch, btnSubmit, btnCancel,btnAuthen,btnUnAuthen;
    private LinearLayout ll_regedit, ll_authen, ll_2button, ll_3button;

    private TextView r_name, r_sex, r_telphone, r_usrinfo;
    private EditText r_idcard, r_address;

    private TextView a_sex, a_usrinfo, a_birthday, a_relation, a_idcard, a_address;
    private EditText a_name, a_telphone;

    private static final String Relations[] = {"", "户主", "夫妻", "子女", "父母", "亲戚", "朋友", "租客", "其他"};
    private static final String Sex[] = {"", "男", "女"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserAccountID = getIntent().getIntExtra("UserAccountID", 0);
        HouseID = getIntent().getIntExtra("HouseID", 0);
        UserName = getIntent().getStringExtra("UserName");
        Phone = getIntent().getStringExtra("Phone");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authen);

        initView();

        //请求网络数据
        new WareTask().execute();
    }

    View.OnClickListener hander = new View.OnClickListener() {
        public void onClick(View v) {
            Map<String, String> params = new HashMap<String, String>();

            switch (v.getId()) {
                case R.id.regedit:
                    regedit.setBackgroundColor(getResources().getColor(R.color.lightskyblue));
                    authen.setBackgroundColor(getResources().getColor(R.color.bg_Gray_light));
                    ll_regedit.setVisibility(View.VISIBLE);
                    ll_authen.setVisibility(View.GONE);
                    ll_2button.setVisibility(View.VISIBLE);
                    ll_3button.setVisibility(View.GONE);
                    break;
                case R.id.authen:
                    regedit.setBackgroundColor(getResources().getColor(R.color.bg_Gray_light));
                    authen.setBackgroundColor(getResources().getColor(R.color.lightskyblue));
                    ll_regedit.setVisibility(View.GONE);
                    ll_authen.setVisibility(View.VISIBLE);
                    ll_2button.setVisibility(View.GONE);
                    ll_3button.setVisibility(View.VISIBLE);
                    break;
                case R.id.btnSubmit:
                    params.put("UserAccountID", String.valueOf(UserAccountID));
                    params.put("UserName", r_name.getText().toString());
                    params.put("MobileNo", r_telphone.getText().toString());
                    params.put("IDCard", r_idcard.getText().toString());
                    params.put("UserAddress", r_address.getText().toString());
                    params.put("Birth", "");

                    PostHttp.RequstPostHttp(Interface.SubmitHouseholdInfo, params);
                    OwnerInquiryActivity.ActionType = 1;
                    finish();
                    break;
                case R.id.btnAuthen:
                    if (HouseholdID > 0) {
                        params.put("UserAccountID", String.valueOf(UserAccountID));
                        params.put("HouseholdID", String.valueOf(HouseholdID));

                        PostHttp.RequstPostHttp(Interface.Authen, params);
                        OwnerInquiryActivity.ActionType = 1;
                        finish();
                    } else {
                        Toast.makeText(AuthenActivity.this, "请确认住户信息!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnUnAuthen:
                    if (HouseholdID > 0) {
                        params.put("UserAccountID", String.valueOf(UserAccountID));
                        params.put("HouseholdID", String.valueOf(HouseholdID));

                        PostHttp.RequstPostHttp(Interface.UnAuthen, params);
                        OwnerInquiryActivity.ActionType = 1;
                        finish();
                    } else {
                        Toast.makeText(AuthenActivity.this, "请确认住户信息!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnCancel:
                    finish();
                    break;
                case R.id.btnSearch:
                    Map<String, Object> param = new HashMap<String, Object>();
                    param.put("UserName", a_name.getText().toString());
                    param.put("Phone", a_telphone.getText().toString());
                    param.put("HouseID", String.valueOf(HouseID));

                    String jsonStr = GetHttp.RequstGetHttp(Interface.GetHouseholdInfo, param);
                    Household household = JSON.parseObject(jsonStr, Household.class);

                    if (household.Data != null) {
                        HouseholdID = household.Data.ID;
                        a_name.setText(household.Data.UserName);
                        a_telphone.setText(household.Data.MobileNo);
                        a_idcard.setText(household.Data.IDCard);
                        a_address.setText(household.Data.UserAddress);
                        if (household.Data.Birth != null)
                            a_birthday.setText(household.Data.Birth.split("T")[0]);

                        a_sex.setText(Sex[household.Data.Sex]);
                        a_relation.setText(Relations[household.Data.Relations]);
                    } else {
                        HouseholdID = 0;

                        a_name.setText("");
                        a_telphone.setText("");
                        a_idcard.setText("");
                        a_address.setText("");
                        a_birthday.setText("");

                        a_sex.setText("");
                    }

                    break;
            }
        }
    };

    /**
     * 初始化View
     */
    private void initView() {
        regedit = (TextView) findViewById(R.id.regedit);
        regedit.setOnClickListener(hander);
        authen = (TextView) findViewById(R.id.authen);
        authen.setOnClickListener(hander);

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(hander);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(hander);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(hander);

        btnAuthen = (Button) findViewById(R.id.btnAuthen);
        btnAuthen.setOnClickListener(hander);

        btnUnAuthen = (Button) findViewById(R.id.btnUnAuthen);
        btnUnAuthen.setOnClickListener(hander);

        ll_regedit = (LinearLayout) findViewById(R.id.ll_regedit);
        ll_authen = (LinearLayout) findViewById(R.id.ll_authen);

        ll_2button = (LinearLayout) findViewById(R.id.ll_2button);
        ll_3button = (LinearLayout) findViewById(R.id.ll_3button);

        r_name = (TextView) findViewById(R.id.r_name);
        r_sex = (TextView) findViewById(R.id.r_sex);
        r_telphone = (TextView) findViewById(R.id.r_telphone);
        r_usrinfo = (TextView) findViewById(R.id.r_usrinfo);
        r_idcard = (EditText) findViewById(R.id.r_idcard);
        r_address = (EditText) findViewById(R.id.r_address);

        a_sex = (TextView) findViewById(R.id.a_sex);
        //a_usrinfo = (TextView) findViewById(R.id.a_usrinfo);
        a_birthday = (TextView) findViewById(R.id.a_birthday);
        a_relation = (TextView) findViewById(R.id.a_relation);
        a_idcard = (TextView) findViewById(R.id.a_idcard);
        a_address = (TextView) findViewById(R.id.a_address);
        a_name = (EditText) findViewById(R.id.a_name);
        a_telphone = (EditText) findViewById(R.id.a_telphone);
    }

    /*
       handle
     */
    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                super.handleMessage(msg);
                switch (msg.what) {
                    case REQUEST_SHOW_DETAIL:
                        UserInfo userInfo = (UserInfo) msg.obj;

                        r_name.setText(userInfo.UserName);
                        r_telphone.setText(userInfo.Phone);
                        a_telphone.setText(userInfo.Phone);
                        r_usrinfo.setText(userInfo.EstateName + userInfo.BuildingName + userInfo.CellName + userInfo.HouseCode);
                        r_idcard.setText(userInfo.IDCard);
                        r_address.setText(userInfo.Address);
                        switch (userInfo.Sex) {
                            case 1:
                                r_sex.setText("男");
                                break;
                            case 2:
                                r_sex.setText("女");
                                break;
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    private class WareTask extends AsyncTask<Void, Void, HashMap<String, Object>> {
        @Override
        protected HashMap<String, Object> doInBackground(Void... arg0) {
            HashMap<String, Object> hashMap = new HashMap<>();

            try {
                //请求数据，返回json
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("UserAccountID", UserAccountID);

                String jsonStr = GetHttp.RequstGetHttp(Interface.GetUserAccountInfo, params);
                UserDetail userDetail = JSON.parseObject(jsonStr, UserDetail.class);

                hashMap.put("data", userDetail.Data);
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
                Message msg = new Message();
                msg.what = REQUEST_SHOW_DETAIL;
                msg.obj = result.get("data");
                myHandler.handleMessage(msg);
            }
        }
    }
}
