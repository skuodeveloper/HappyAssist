package com.example.skuo.happyassist.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.skuo.happyassist.Class.Result.ImageInfo;
import com.example.skuo.happyassist.Class.Result.RepairImage;
import com.example.skuo.happyassist.Class.Result.RepairInfo;
import com.example.skuo.happyassist.Javis.Adapter.Adapter_Image_Gallery;
import com.example.skuo.happyassist.Javis.Data.USERINFO;
import com.example.skuo.happyassist.Javis.http.GetHttp;
import com.example.skuo.happyassist.Javis.http.Interface;
import com.example.skuo.happyassist.R;
import com.example.skuo.happyassist.Util.PublicWay;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("deprecation")
public class RepairDetailActivity extends BaseActivity {
    private Context mContext;
    private RepairInfo infos;
    /**
     * 存储网络返回的数据
     */
    private HashMap<String, Object> hashMap;

    private static final int REQUEST_SHOW_IMAGE = 1;
    private static final int REQUEST_CALL_PHONE = 2;
    private static final int REQUEST_DONE_CODE = 3;
    private static final int REQUEST_DISPATCH_CODE = 4;
    public static final String IMAGES = "com.nostra13.example.universalimageloader.IMAGES";
    public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
    public static int ActionType = 0;
    public static int PrePage = 0;

    private String[] imageUrls;
    Adapter_Image_Gallery adapter_image_gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        infos = (RepairInfo) getIntent().getSerializableExtra("Infos");
        PrePage = getIntent().getIntExtra("PAGE", 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_detail);

        initView();

        //请求网络数据
        new WareTask().execute();
    }

    View.OnClickListener hander = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent;

            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.iv_call:
                    //用intent启动拨打电话
                    String mobile = infos.UserPhone;

                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE);
                        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((OwnerInquiryActivity) mContext, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
                            return;
                        } else {
                            //上面已经写好的拨号方法
                            callDirectly(mobile);
                        }
                    } else {
                        //上面已经写好的拨号方法
                        callDirectly(mobile);
                    }
                    break;
                case R.id.btnDone:
                    intent = new Intent(RepairDetailActivity.this, WorksheetHandleActivity.class);
                    intent.putExtra("ID", String.valueOf(infos.ID));
                    intent.putExtra("PAGE", 2);
                    PublicWay.activityList.clear();
                    startActivityForResult(intent, REQUEST_DONE_CODE);
                    break;
                case R.id.btnOffer:
                    intent = new Intent(RepairDetailActivity.this, DispatchActivity.class);
                    intent.putExtra("ID", String.valueOf(infos.ID));
                    intent.putExtra("EstateID", infos.EstateID);
                    intent.putExtra("PAGE", 2);
                    startActivityForResult(intent, REQUEST_DISPATCH_CODE);
                    break;
            }
        }
    };

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
                        ArrayList<ImageInfo> imageInfo = (ArrayList<ImageInfo>) msg.obj;
                        imageUrls = new String[imageInfo.size()];
                        for (int i = 0; i < imageInfo.size(); i++)
                            imageUrls[i] = imageInfo.get(i).ImgUrl;

                        adapter_image_gallery.mImageUrls = imageUrls;
                        adapter_image_gallery.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT);
            }
        }
    };

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("工单详情");

        ((Button) findViewById(R.id.btnDone)).setOnClickListener(hander);
        ((Button) findViewById(R.id.btnOffer)).setOnClickListener(hander);

        ((ImageView) findViewById(R.id.iv_back)).setOnClickListener(hander);
        ((ImageView) findViewById(R.id.iv_call)).setOnClickListener(hander);

        ((TextView) findViewById(R.id.tv_orderid)).setText(String.valueOf(infos.ID));
        ((TextView) findViewById(R.id.tv_name)).setText(infos.UserName);
        ((TextView) findViewById(R.id.tv_address)).setText(infos.Address);

        switch (infos.Status) {
            case 1:
                ((TextView) findViewById(R.id.tv_status)).setText("待处理");
                break;
            case 2:
                ((TextView) findViewById(R.id.tv_status)).setText("处理中");

                //一般管理员
                if (USERINFO.AccountType == 6) {
                    ((Button) findViewById(R.id.btnOffer)).setVisibility(View.GONE);
                } else {
                    //((Button) findViewById(R.id.btnDone)).setVisibility(View.GONE);
                    ((Button) findViewById(R.id.btnOffer)).setVisibility(View.GONE);
                }

                break;
            case 3:
                ((TextView) findViewById(R.id.tv_status)).setText("已完成");
                ((Button) findViewById(R.id.btnDone)).setVisibility(View.GONE);
                ((Button) findViewById(R.id.btnOffer)).setVisibility(View.GONE);
                break;
            default:
                ((TextView) findViewById(R.id.tv_status)).setText("已结束");
                ((Button) findViewById(R.id.btnDone)).setVisibility(View.GONE);
                ((Button) findViewById(R.id.btnOffer)).setVisibility(View.GONE);
                break;
        }
        ((TextView) findViewById(R.id.tv_time)).setText(infos.Time);
        ((TextView) findViewById(R.id.tv_info)).setText(infos.Info);
        ((TextView) findViewById(R.id.tv_comment)).setText(infos.Comment);

        adapter_image_gallery = new Adapter_Image_Gallery(mContext, this.imageLoader, imageUrls);
        Gallery gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setAdapter(adapter_image_gallery);
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startImagePagerActivity(position);
            }
        });
    }

    /**
     * 预览图片
     *
     * @param position
     */
    private void startImagePagerActivity(int position) {
        Intent intent = new Intent(this, ImagePagerActivity.class);
        intent.putExtra(IMAGES, imageUrls);
        intent.putExtra(IMAGE_POSITION, position);
        startActivity(intent);
    }

    //拨打电话
    private void callDirectly(String mobile) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:" + mobile));
        startActivity(intent);
    }

    private class WareTask extends AsyncTask<Void, Void, HashMap<String, Object>> {
        @Override
        protected HashMap<String, Object> doInBackground(Void... arg0) {
            try {
                hashMap = new HashMap<>();

                //请求数据，返回json
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("RepairID", infos.ID);

                String jsonStr = "";
                if (infos.Status == 3){
                    //已完成
                    jsonStr = GetHttp.RequstGetHttp(Interface.GetRepairHandleImageList, params);
                    RepairImage Rep = JSON.parseObject(jsonStr, RepairImage.class);
                    hashMap.put("data", Rep.Data.RepairHandleImageExs);
                } else {
                    jsonStr = GetHttp.RequstGetHttp(Interface.GetRepairImageList, params);
                    RepairImage Rep = JSON.parseObject(jsonStr, RepairImage.class);
                    hashMap.put("data", Rep.Data.RepairImages);
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DONE_CODE:
                if (ActionType == 1) {
                    ActionType = 0;
                    if (PrePage == 1)
                        MyOrdersActivity.ActionType = 1;
                    else
                        RepairOrdersActivity.ActionType = 1;

                    this.finish();
                }
                break;
            case REQUEST_DISPATCH_CODE:
                if (ActionType == 1) {
                    ActionType = 0;
                    if (PrePage == 1)
                        MyOrdersActivity.ActionType = 1;
                    else
                        RepairOrdersActivity.ActionType = 1;

                    this.finish();
                }
                break;
        }
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
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
}
