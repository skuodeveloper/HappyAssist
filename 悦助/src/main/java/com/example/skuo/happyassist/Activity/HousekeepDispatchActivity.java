package com.example.skuo.happyassist.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.skuo.happyassist.Javis.http.Interface;
import com.example.skuo.happyassist.Javis.http.PostHttp;
import com.example.skuo.happyassist.R;

import java.util.HashMap;
import java.util.Map;

public class HousekeepDispatchActivity extends Activity {
    private String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ID = getIntent().getStringExtra("ID");
        setContentView(R.layout.activity_housekeep_dispatch);

        Button btnDispatch = (Button) findViewById(R.id.btnDispatch);
        assert btnDispatch != null;

        btnDispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("ID", String.valueOf(ID));

                    EditText editStaff = (EditText) findViewById(R.id.et_Staff);
                    params.put("ServiceStaff", editStaff.getText().toString());

                    EditText editStaffTel = (EditText) findViewById(R.id.et_StaffTel);
                    params.put("StaffTel", editStaffTel.getText().toString());

                    PostHttp.RequstPostHttp(Interface.AppointmentDispatch, params);

                    HouseKeepDetailActivity.ActionType = 1;
                    finish();
                } catch (Exception ex) {
                    Toast.makeText(HousekeepDispatchActivity.this, ex.getMessage().toString(), Toast.LENGTH_SHORT);
                }
            }
        });
    }
}
