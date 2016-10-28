package com.example.skuo.happyassist.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.skuo.happyassist.Class.Result.SignIn;
import com.example.skuo.happyassist.Class.Result.SignInInfo;
import com.example.skuo.happyassist.Javis.Data.USERINFO;
import com.example.skuo.happyassist.Javis.MyView.MyCalendar;
import com.example.skuo.happyassist.Javis.http.GetHttp;
import com.example.skuo.happyassist.Javis.http.Interface;
import com.example.skuo.happyassist.Javis.http.PostHttp;
import com.example.skuo.happyassist.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author baiyuliang
 */
@SuppressLint("SimpleDateFormat")
public class PunchSignActivity extends Activity {
    private final static int REQUEST_SHOW_DETAIL = 1;

    private LinearLayout ll;
    private MyCalendar c1;
    private Date date;
    private String nowday;
    private long nd = 1000 * 24L * 60L * 60L;//一天的毫秒数
    SimpleDateFormat simpleDateFormat, sd1, sd2;
    private Context mContext;

    private ArrayList<String> signday = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch_sign);

        mContext = this;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        nowday = simpleDateFormat.format(new Date());
        sd1 = new SimpleDateFormat("yyyy");
        sd2 = new SimpleDateFormat("dd");

        initView();

        //请求网络数据
        new WareTask().execute();
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
                        ArrayList<SignInInfo> signInInfos = (ArrayList<SignInInfo>)msg.obj;
                        String YYMM = new SimpleDateFormat("yyyy-MM").format(new Date());
                        for (int i = 0; i < signInInfos.size(); i++) {
                            if (signInInfos.get(i).SignInStatus == 1) {
                                //signday.add(YYMM + "," + getDay(i + 1));
                                signday.add(YYMM + "," + String.valueOf(i + 1));
                            }
                        }

                        List<String> listDate = getDateList();
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

                        ll.removeAllViews();
                        for (int i = 0; i < listDate.size(); i++) {
                            c1 = new MyCalendar(PunchSignActivity.this);
                            c1.setLayoutParams(params);

                            try {
                                date = simpleDateFormat.parse(listDate.get(i));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            c1.setTheDay(date);
                            c1.setSignDay(signday);
                            ll.addView(c1);
                        }
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
                params.put("AccountID", USERINFO.AccountID);
                params.put("Year", new Date().getYear() + 1900);
                params.put("Month", new Date().getMonth() + 1);

                String jsonStr = GetHttp.RequstGetHttp(Interface.GetSignInList, params);
                SignIn signIn = JSON.parseObject(jsonStr, SignIn.class);

                hashMap.put("data", signIn.Data.CurMonthSignInInfos);
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

    private void initView() {
//        List<String> listDate = getDateList();
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        ((TextView) findViewById(R.id.tv_top_title)).setText("打卡签到");

        ll = (LinearLayout) findViewById(R.id.ll);
//        for (int i = 0; i < listDate.size(); i++) {
//            c1 = new MyCalendar(this);
//            c1.setLayoutParams(params);
//
//            try {
//                date = simpleDateFormat.parse(listDate.get(i));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            c1.setTheDay(date);
//            c1.setSignDay(signday);
//            ll.addView(c1);
//        }

        Button btnSignDay = (Button) findViewById(R.id.signday);
        btnSignDay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("AccountID", String.valueOf(USERINFO.AccountID));
                params.put("PropertyID", String.valueOf(USERINFO.PropertyID));
                params.put("EstateID", String.valueOf(USERINFO.EstateID));

                String jsonStr = PostHttp.RequstPostHttp(Interface.SignIn, params);
                SignIn signIn = JSON.parseObject(jsonStr, SignIn.class);

                Toast.makeText(mContext, signIn.ErrorMsg, Toast.LENGTH_LONG).show();

                //请求网络数据
                new WareTask().execute();
            }
        });

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    public List<String> getDateList() {
        List<String> list = new ArrayList<String>();
        Date date = new Date();
        int nowMon = date.getMonth() + 1;
        String yyyy = sd1.format(date);
        String dd = sd2.format(date);

        list.add(yyyy + "-" + getMon(nowMon) + "-" + dd);
        return list;
    }

    public String getMon(int mon) {
        String month = "";
        if (mon < 10) {
            month = "0" + mon;
        } else {
            month = "" + mon;
        }
        return month;
    }

    public String getDay(int day) {
        String sday = "";
        if (day < 10) {
            sday = "0" + day;
        } else {
            sday = "" + day;
        }
        return sday;
    }
}
