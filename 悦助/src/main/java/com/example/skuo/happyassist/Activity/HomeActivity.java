package com.example.skuo.happyassist.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.skuo.happyassist.Class.Result.UserImage;
import com.example.skuo.happyassist.JPush.ExampleUtil;
import com.example.skuo.happyassist.Javis.Adapter.Adapter_myGrid;
import com.example.skuo.happyassist.Javis.Data.USERINFO;
import com.example.skuo.happyassist.Javis.MyView.MyGridView;
import com.example.skuo.happyassist.Javis.http.GetHttp;
import com.example.skuo.happyassist.Javis.http.Interface;
import com.example.skuo.happyassist.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * @author http://blog.csdn.net/finddreams
 * @Description:主页
 */
public class HomeActivity extends Activity {
    private ImageView imageView;
    private final static int IMAGE_REQUEST_CODE = 1;
    private final static int REQUEST_SHOW_IMAGE = 2;
    /**
     * 存储网络返回的数据
     */
    private HashMap<String, Object> hashMap;


    public static boolean isForeground = false;
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    TagAliasCallback callback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

        //注册极光接收
        registerMessageReceiver();

        setAliasAndTags();

        //请求网络数据
        new WareTask().execute();
    }

    /**
     * 设置推送的别名与标签
     */
    private void setAliasAndTags() {
        final String alias = "alias_AccountID_" + USERINFO.AccountID;
        final Set<String> tags = new HashSet<String>();

        String tag = "tag_AccountID_" + USERINFO.AccountID;
        tags.add(tag);
        tag = "tag_EstateID_" + USERINFO.EstateID;
        tags.add(tag);
        tag = "tag_PropertyID_" + USERINFO.PropertyID;
        tags.add(tag);

        callback = new TagAliasCallback() {
            @Override
            public void gotResult(int statusCode, String s, Set<String> set) {
                Log.i("JPush", "Jpush status: " + statusCode);//状态
                switch (statusCode){
                    case 6001:
                        break;
                    case 6002://超时
                        JPushInterface.setAliasAndTags(HomeActivity.this, alias, tags, callback);
                        break;
                }
            }
        };

        JPushInterface.setAliasAndTags(this, alias, tags, callback);
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
                    case REQUEST_SHOW_IMAGE:
                        UserImage userImage = (UserImage) msg.obj;

                        if (userImage.Data != null)
                            imageView.setImageBitmap(getHttpBitmap(userImage.Data));
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                Toast.makeText(HomeActivity.this, ex.getMessage().toString(), Toast.LENGTH_SHORT);
            }
        }
    };

    private void initView() {
        MyGridView gridview;

        imageView = (ImageView) findViewById(R.id.imageView);
        //setImageView();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, UserInfoActivity.class);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });

        gridview = (MyGridView) findViewById(R.id.gridview);
        gridview.setAdapter(new Adapter_myGrid(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        //物业管理员或小区管理员可以进入
                        if (USERINFO.AccountType == 2 || USERINFO.AccountType == 5) {
                            intent = new Intent(HomeActivity.this, RepairOrdersActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 1:
                        intent = new Intent(HomeActivity.this, MyOrdersActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        //物业管理员或小区管理员可以进入
                        if (USERINFO.AccountType == 2 || USERINFO.AccountType == 5) {
                            intent = new Intent(HomeActivity.this, ComplaintOrdersActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 3:
                        intent = new Intent(HomeActivity.this, MallOrdersActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(HomeActivity.this, HouseKeepOrdersActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(HomeActivity.this, OwnerInquiryActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        //intent = new Intent(HomeActivity.this,PunchSignActivity.class);
                        Toast.makeText(HomeActivity.this, "该功能尚未开放!", Toast.LENGTH_SHORT).show();
                        break;
                    case 7:
//                        intent = new Intent(HomeActivity.this, DoorInspectActivity.class);
//                        startActivity(intent);
                        Toast.makeText(HomeActivity.this, "该功能尚未开放!", Toast.LENGTH_SHORT).show();
                        break;
                    case 8:
                        intent = new Intent(HomeActivity.this, PunchSignActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                //请求网络数据
                new WareTask().execute();
                break;
        }
    }

    /*设置个人头像*/
    private void setImageView() {
        String path = Environment.getExternalStorageDirectory() + "/head.jpg";
        File picture = new File(path);

        if (picture.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bm);
        } else {
            imageView.setBackgroundResource(R.drawable.head1);
        }
    }

    private class WareTask extends AsyncTask<Void, Void, HashMap<String, Object>> {
        @Override
        protected HashMap<String, Object> doInBackground(Void... arg0) {
            try {
                //请求数据，返回json
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("ID", USERINFO.AccountID);

                String jsonStr = GetHttp.RequstGetHttp(Interface.GetAccountPicture, params);
                UserImage userImage = JSON.parseObject(jsonStr, UserImage.class);

                hashMap = new HashMap<>();
                hashMap.put("data", userImage);
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
                msg.what = REQUEST_SHOW_IMAGE;
                msg.obj = result.get("data");
                myHandler.handleMessage(msg);
            }
        }
    }

    /**
     * 从服务器取图片
     *
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 一下均为极光推送代码
     */
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                //setCostomMsg(showMsg.toString());
            }
        }

    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


}
