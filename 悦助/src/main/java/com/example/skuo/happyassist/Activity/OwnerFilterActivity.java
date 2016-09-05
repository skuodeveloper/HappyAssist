package com.example.skuo.happyassist.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.skuo.happyassist.Class.Result.Building;
import com.example.skuo.happyassist.Class.Result.BuildingInfo;
import com.example.skuo.happyassist.Class.Result.Cell;
import com.example.skuo.happyassist.Class.Result.CellInfo;
import com.example.skuo.happyassist.Class.Result.Estate;
import com.example.skuo.happyassist.Class.Result.EstateInfo;
import com.example.skuo.happyassist.Class.Result.Group;
import com.example.skuo.happyassist.Class.Result.GroupInfo;
import com.example.skuo.happyassist.Class.Result.House;
import com.example.skuo.happyassist.Class.Result.HouseInfo;
import com.example.skuo.happyassist.Javis.Data.USERINFO;
import com.example.skuo.happyassist.Javis.http.GetHttp;
import com.example.skuo.happyassist.Javis.http.Interface;
import com.example.skuo.happyassist.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OwnerFilterActivity extends Activity {
    private final static int REQUEST_ESTATE_LIST = 100;
    private final static int REQUEST_GROUP_LIST = 101;
    private final static int REQUEST_BUILDING_LIST = 102;
    private final static int REQUEST_CELL_LIST = 103;
    private final static int REQUEST_ROOM_LIST = 104;

    private Context mContext;
    private ImageView iv_back;
    private Button btnReset, btnOk;
    private Spinner sp_Estate, sp_Group, sp_Building, sp_Cell, sp_Room;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                Map<String, Object> param;
                String jsonStr;
                switch (msg.what) {
                    case REQUEST_ESTATE_LIST:
                        param = new HashMap<String, Object>();
                        param.put("PropertyID", String.valueOf(USERINFO.PropertyID));
                        param.put("EstateID", String.valueOf(USERINFO.EstateID));

                        //请求数据，返回json
                        jsonStr = GetHttp.RequstGetHttp(Interface.GetEstateList, param);
                        Estate Est = JSON.parseObject(jsonStr, Estate.class);

                        ArrayAdapter<EstateInfo> myaAdapter;

                        ArrayList<EstateInfo> estateInfoArrayList = Est.Data.RepairEstateInfos;
                        EstateInfo empty = new EstateInfo();
                        estateInfoArrayList.add(0, empty);

                        //适配器
                        myaAdapter = new ArrayAdapter<EstateInfo>(mContext, android.R.layout.simple_spinner_item, estateInfoArrayList);
                        //设置样式
                        myaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //加载适配器
                        sp_Estate.setAdapter(myaAdapter);

                        sp_Group.setAdapter(null);
                        sp_Building.setAdapter(null);
                        sp_Cell.setAdapter(null);
                        sp_Room.setAdapter(null);
                        break;
                    case REQUEST_GROUP_LIST:
                        param = new HashMap<String, Object>();

                        EstateInfo est = (EstateInfo) sp_Estate.getSelectedItem();
                        param.put("EstateID", String.valueOf(est.GetID()));

                        //请求数据，返回json
                        jsonStr = GetHttp.RequstGetHttp(Interface.GetGroupList, param);
                        Group group = JSON.parseObject(jsonStr, Group.class);

                        ArrayAdapter<GroupInfo> groupInfoArrayAdapter;

                        ArrayList<GroupInfo> groupInfoArrayList = group.Data.GroupExs;
                        GroupInfo groupInfo = new GroupInfo();
                        groupInfoArrayList.add(0, groupInfo);

                        //适配器
                        groupInfoArrayAdapter = new ArrayAdapter<GroupInfo>(mContext, android.R.layout.simple_spinner_item, groupInfoArrayList);
                        //设置样式
                        groupInfoArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //加载适配器
                        sp_Group.setAdapter(groupInfoArrayAdapter);

                        sp_Building.setAdapter(null);
                        sp_Cell.setAdapter(null);
                        sp_Room.setAdapter(null);
                        break;
                    case REQUEST_BUILDING_LIST:
                        param = new HashMap<String, Object>();

                        GroupInfo groupInfo1 = (GroupInfo) sp_Group.getSelectedItem();
                        param.put("GroupID", String.valueOf(groupInfo1.GetID()));

                        //请求数据，返回json
                        jsonStr = GetHttp.RequstGetHttp(Interface.GetBuildingList, param);
                        Building building = JSON.parseObject(jsonStr, Building.class);

                        ArrayAdapter<BuildingInfo> buildingInfoArrayAdapter;

                        ArrayList<BuildingInfo> buildingInfoArrayList = building.Data.BuildingExs;
                        BuildingInfo buildingInfo = new BuildingInfo();
                        buildingInfoArrayList.add(0, buildingInfo);

                        //适配器
                        buildingInfoArrayAdapter = new ArrayAdapter<BuildingInfo>(mContext, android.R.layout.simple_spinner_item, buildingInfoArrayList);
                        //设置样式
                        buildingInfoArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //加载适配器
                        sp_Building.setAdapter(buildingInfoArrayAdapter);

                        sp_Cell.setAdapter(null);
                        sp_Room.setAdapter(null);
                        break;
                    case REQUEST_CELL_LIST:
                        param = new HashMap<String, Object>();

                        BuildingInfo buildingInfo1 = (BuildingInfo) sp_Building.getSelectedItem();
                        param.put("BuildingID", String.valueOf(buildingInfo1.GetID()));

                        //请求数据，返回json
                        jsonStr = GetHttp.RequstGetHttp(Interface.GetCellList, param);
                        Cell cell = JSON.parseObject(jsonStr, Cell.class);

                        ArrayAdapter<CellInfo> cellInfoArrayAdapter;

                        ArrayList<CellInfo> cellInfoArrayList = cell.Data.CellExs;
                        CellInfo cellInfo = new CellInfo();
                        cellInfoArrayList.add(0, cellInfo);

                        //适配器
                        cellInfoArrayAdapter = new ArrayAdapter<CellInfo>(mContext, android.R.layout.simple_spinner_item, cellInfoArrayList);
                        //设置样式
                        cellInfoArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //加载适配器
                        sp_Cell.setAdapter(cellInfoArrayAdapter);
                        sp_Room.setAdapter(null);
                        break;
                    case REQUEST_ROOM_LIST:
                        param = new HashMap<String, Object>();

                        CellInfo cellInfo1 = (CellInfo) sp_Cell.getSelectedItem();
                        param.put("CellID", String.valueOf(cellInfo1.GetID()));

                        //请求数据，返回json
                        jsonStr = GetHttp.RequstGetHttp(Interface.GetHouseList, param);
                        House house = JSON.parseObject(jsonStr, House.class);

                        ArrayAdapter<HouseInfo> houseInfoArrayAdapter;

                        ArrayList<HouseInfo> houseInfoArrayList = house.Data.HouseExs;
                        HouseInfo houseInfo = new HouseInfo();
                        houseInfoArrayList.add(0, houseInfo);

                        //适配器
                        houseInfoArrayAdapter = new ArrayAdapter<HouseInfo>(mContext, android.R.layout.simple_spinner_item, houseInfoArrayList);
                        //设置样式
                        houseInfoArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //加载适配器
                        sp_Room.setAdapter(houseInfoArrayAdapter);
                        break;
                }
                super.handleMessage(msg);
            } catch (Exception ex) {
                Toast.makeText(mContext, ex.getMessage().toString(), Toast.LENGTH_LONG);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_owner_filter);

        initView();

        MyThread myThread = new MyThread();
        myThread.setRequestCode(REQUEST_ESTATE_LIST);
        Thread thread = new Thread(myThread);
        thread.start();
    }

    View.OnClickListener hander = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.btnReset:
                    sp_Estate.setSelection(0, true);
                    sp_Group.setAdapter(null);
                    sp_Building.setAdapter(null);
                    sp_Cell.setAdapter(null);
                    sp_Room.setAdapter(null);
                    break;
                case R.id.btnOk:
                    Intent intent = new Intent();
                    EstateInfo estateInfo = (EstateInfo) sp_Estate.getSelectedItem();
                    intent.putExtra("EstateID", estateInfo.GetID());

                    GroupInfo groupInfo = (GroupInfo) sp_Group.getSelectedItem();
                    intent.putExtra("GroupID", groupInfo.GetID());

                    BuildingInfo buildingInfo = (BuildingInfo) sp_Building.getSelectedItem();
                    intent.putExtra("BuildingID", buildingInfo.GetID());

                    CellInfo cellInfo = (CellInfo) sp_Cell.getSelectedItem();
                    intent.putExtra("CellID", cellInfo.GetID());

                    HouseInfo houseInfo = (HouseInfo) sp_Room.getSelectedItem();
                    intent.putExtra("HouseID", houseInfo.GetID());

                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    };

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("筛选");

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(hander);

        btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(hander);

        btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(hander);

        sp_Estate = (Spinner) findViewById(R.id.spin_estate);
        sp_Estate.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        //EstateInfo est = (EstateInfo) sp_Estate.getSelectedItem();
                        //if (est.GetID() != 0) {
                        MyThread myThread = new MyThread();
                        myThread.setRequestCode(REQUEST_GROUP_LIST);

                        Thread thread = new Thread(myThread);
                        thread.start();
                        // }
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        //TODO
                    }
                });

        sp_Group = (Spinner) findViewById(R.id.spin_group);
        sp_Group.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        //GroupInfo est = (GroupInfo) sp_Group.getSelectedItem();
                        //if (est.GetID() != 0) {
                        MyThread myThread = new MyThread();
                        myThread.setRequestCode(REQUEST_BUILDING_LIST);

                        Thread thread = new Thread(myThread);
                        thread.start();
                        //}
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        //TODO
                    }
                });

        sp_Building = (Spinner) findViewById(R.id.spin_build);
        sp_Building.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        //BuildingInfo est = (BuildingInfo) sp_Building.getSelectedItem();
                        //if (est.GetID() != 0) {
                        MyThread myThread = new MyThread();
                        myThread.setRequestCode(REQUEST_CELL_LIST);

                        Thread thread = new Thread(myThread);
                        thread.start();
                        //}
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        //TODO
                    }
                });

        sp_Cell = (Spinner) findViewById(R.id.spin_cell);
        sp_Cell.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        //CellInfo est = (CellInfo) sp_Cell.getSelectedItem();
                        //if (est.GetID() != 0) {
                        MyThread myThread = new MyThread();
                        myThread.setRequestCode(REQUEST_ROOM_LIST);

                        Thread thread = new Thread(myThread);
                        thread.start();
                        //}
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        //TODO
                    }
                });

        sp_Room = (Spinner) findViewById(R.id.spin_room);
    }

    public class MyThread implements Runnable {
        private int RequestCode;

        private void setRequestCode(int requestCode) {
            this.RequestCode = requestCode;
        }

        public void run() {
            Message message = new Message();
            message.what = RequestCode;
            handler.sendMessage(message);
        }
    }
}
