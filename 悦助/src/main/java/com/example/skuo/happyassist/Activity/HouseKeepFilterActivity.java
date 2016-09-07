package com.example.skuo.happyassist.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.skuo.happyassist.Class.Result.Estate;
import com.example.skuo.happyassist.Class.Result.EstateInfo;
import com.example.skuo.happyassist.Javis.Data.USERINFO;
import com.example.skuo.happyassist.Javis.http.GetHttp;
import com.example.skuo.happyassist.Javis.http.Interface;
import com.example.skuo.happyassist.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HouseKeepFilterActivity extends AppCompatActivity {
    private final static int REQUEST_ESTATE_LIST = 100;
    private final static int REQUEST_STATUS_LIST = 101;
    private final static int DATE_START = 1;
    private final static int DATE_END = 2;
    private static int selectIndex = -1;
    private static String etStart;
    private static String etEnd;
    Runnable r = new Runnable() {
        public void run() {
            Message message = new Message();
            message.what = REQUEST_ESTATE_LIST;
            handler.sendMessage(message);
        }
    };
    private Context mContext;
    private Spinner spin_estate,spin_status;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case REQUEST_ESTATE_LIST:
                        Map<String, Object> param = new HashMap<String, Object>();
                        param.put("PropertyID", String.valueOf(USERINFO.PropertyID));
                        param.put("EstateID", String.valueOf(USERINFO.EstateID));

                        //请求数据，返回json
                        String jsonStr = GetHttp.RequstGetHttp(Interface.GetEstateList, param);
                        Estate Est = JSON.parseObject(jsonStr, Estate.class);

                        ArrayAdapter<EstateInfo> estAdapter;

                        ArrayList<EstateInfo> estlist = Est.Data.RepairEstateInfos;

                        EstateInfo Empty = new EstateInfo();
                        estlist.add(0, Empty);

                        //适配器
                        estAdapter = new ArrayAdapter<EstateInfo>(mContext, android.R.layout.simple_spinner_item, estlist);
                        //设置样式
                        estAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //加载适配器
                        spin_estate.setAdapter(estAdapter);

                        if (selectIndex != -1) {
                            spin_estate.setSelection(selectIndex);
                        }
                        break;
                }
                super.handleMessage(msg);
            } catch (Exception ex) {
                Toast.makeText(mContext, ex.getMessage().toString(), Toast.LENGTH_LONG);
            }
        }
    };
    private EditText et_Start, et_End;
    View.OnClickListener hander = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.btnDone:
                    Intent intent = new Intent();
                    EstateInfo est = (EstateInfo) spin_estate.getSelectedItem();
                    intent.putExtra("EstateID", est.GetID());
                    intent.putExtra("StartDate", et_Start.getText().toString());
                    intent.putExtra("EndDate", et_End.getText().toString());

                    selectIndex = spin_estate.getSelectedItemPosition();
                    etStart = et_Start.getText().toString();
                    etEnd = et_End.getText().toString();

                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case R.id.btnReset:
                    spin_estate.setSelection(0, true);// 默认选中项
                    et_Start.setText("");
                    et_End.setText("");
                    break;
                case R.id.dateStart:
                    showDialog(DATE_START);
                    break;
                case R.id.dateEnd:
                    showDialog(DATE_END);
                    break;
            }
        }
    };
    private Calendar c = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housekeep_filter);
        mContext = this;

        initView();

        //请求网络数据
        Thread thread = new Thread(r);
        thread.start();
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("过滤");

        spin_estate = (Spinner) findViewById(R.id.spin_estate);
        et_Start = (EditText) findViewById(R.id.et_Start);
        et_Start.setText(etStart);
        et_End = (EditText) findViewById(R.id.et_End);
        et_End.setText(etEnd);

        findViewById(R.id.btnDone).setOnClickListener(hander);
        findViewById(R.id.btnReset).setOnClickListener(hander);
        findViewById(R.id.iv_back).setOnClickListener(hander);
        findViewById(R.id.dateStart).setOnClickListener(hander);
        findViewById(R.id.dateEnd).setOnClickListener(hander);
    }

    /**
     * 创建日期及时间选择对话框
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        final EditText et = null;

        switch (id) {
            case DATE_START:
                c = Calendar.getInstance();
                dialog = new DatePickerDialog(
                        this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                                et_Start.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                            }
                        },
                        c.get(Calendar.YEAR), // 传入年份
                        c.get(Calendar.MONTH), // 传入月份
                        c.get(Calendar.DAY_OF_MONTH) // 传入天数
                );
                break;
            case DATE_END:
                c = Calendar.getInstance();
                dialog = new DatePickerDialog(
                        this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                                et_End.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                            }
                        },
                        c.get(Calendar.YEAR), // 传入年份
                        c.get(Calendar.MONTH), // 传入月份
                        c.get(Calendar.DAY_OF_MONTH) // 传入天数
                );
                break;
        }
        return dialog;
    }
}
