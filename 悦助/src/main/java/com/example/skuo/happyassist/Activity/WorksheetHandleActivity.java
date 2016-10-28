package com.example.skuo.happyassist.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.skuo.happyassist.Javis.Adapter.Adapter_Grid_View;
import com.example.skuo.happyassist.Javis.Data.USERINFO;
import com.example.skuo.happyassist.Javis.Tools.Utils;
import com.example.skuo.happyassist.Javis.http.Interface;
import com.example.skuo.happyassist.Javis.http.PostHttp;
import com.example.skuo.happyassist.R;
import com.example.skuo.happyassist.Util.Bimp;
import com.example.skuo.happyassist.Util.FileUtils;
import com.example.skuo.happyassist.Util.ImageItem;
import com.example.skuo.happyassist.Util.PublicWay;
import com.example.skuo.happyassist.Util.Res;

import java.util.HashMap;
import java.util.Map;

public class WorksheetHandleActivity extends AppCompatActivity {
    private static final int TAKE_PICTURE = 1;
    private static final int TAKE_ALBUM = 2;
    private static final int TAKE_Gallery = 3;
    private static final int OPEN_WAIT_DIALOG = 4;
    private static final int CLOSE_WAIT_DIALOG = 5;
    public static Bitmap bimap;
    public static int PrePage = 0;
    private static String sRepairID, sRemark;
    private GridView noScrollgridview = null;
    private Adapter_Grid_View adapter;
    private View parentView;
    private Context mContext;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    private ProgressDialog dialog = null;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TAKE_PICTURE:
                    adapter.notifyDataSetChanged();
                    break;
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
    private EditText et_remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if (getIntent().getStringExtra("ID") != null)
            sRepairID = getIntent().getStringExtra("ID");

        if (getIntent().getIntExtra("PAGE", -1) != -1)
            PrePage = getIntent().getIntExtra("PAGE", 0);

        Res.init(this);
        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon_addpic_unfocused);

        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_selectimg, null);
        setContentView(parentView);

        initView();
    }

    public void update() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                        Message message = new Message();
                        message.what = TAKE_PICTURE;
                        handler.sendMessage(message);
                        break;
                    } else {
                        Bimp.max += 1;
                        Message message = new Message();
                        message.what = TAKE_PICTURE;
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();
    }

    @SuppressLint("Assert")
    private void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("工单处理");
        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

        et_remark = (EditText) findViewById(R.id.remark);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop = new PopupWindow(WorksheetHandleActivity.this);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        view.findViewById(R.id.item_popupwindows_camera).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        view.findViewById(R.id.item_popupwindows_Photo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(WorksheetHandleActivity.this,
                        AlbumActivity.class);

                startActivityForResult(intent, TAKE_ALBUM);

                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        view.findViewById(R.id.item_popupwindows_cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        parent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new Adapter_Grid_View(this);

        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(WorksheetHandleActivity.this, R.anim.activity_translate_in));

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_remark.getWindowToken(), 0);

                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(WorksheetHandleActivity.this, GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivityForResult(intent, TAKE_Gallery);
                }
            }
        });

        ImageView iv = (ImageView) findViewById(R.id.iv_back);
        assert iv != null;
        iv.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      Bimp.tempSelectBitmap.clear();
                                      for (int i = 0; i < PublicWay.activityList.size(); i++) {
                                          if (null != PublicWay.activityList.get(i)) {
                                              PublicWay.activityList.get(i).finish();
                                          }
                                      }
                                  }
                              }
        );


        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        assert btnSubmit != null;
        btnSubmit.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             sRemark = et_remark.getText().toString();
                                             new WareTask().execute();
                                         }
                                     }
        );
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

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    FileUtils.saveBitmap(bm, fileName);

                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);
                    this.update();
                }
                break;
            case TAKE_ALBUM:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
                    this.update();
                }
                break;
            case TAKE_Gallery:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
                    this.update();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Bimp.tempSelectBitmap.clear();
            for (int i = 0; i < PublicWay.activityList.size(); i++) {
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
        }
        return true;
    }

    private class WareTask extends AsyncTask<Void, Void, HashMap<String, Object>> {

        @Override
        protected void onPreExecute() {
            handler.sendEmptyMessage(OPEN_WAIT_DIALOG);
        }

        @Override
        protected HashMap<String, Object> doInBackground(Void... arg0) {
            //HashMap<String, Object> hashMap = new HashMap<>();
            Map<String, String> params = new HashMap<String, String>();
            params.put("ID", sRepairID);
            params.put("Remark", sRemark);
            params.put("AccountID", String.valueOf(USERINFO.AccountID));
            params.put("TrueName", USERINFO.TrueName);

            try {
                if (PrePage == 1)//投诉建议
                {
                    for (ImageItem imageItem : Bimp.tempSelectBitmap) {
                        String imageBase64 = Utils.bitmapToBase64(imageItem.getBitmap());
                        params.put("base64string", imageBase64);

                        PostHttp.RequstPostHttp(Interface.UploadComplaintPhoto, params);
                    }

                    PostHttp.RequstPostHttp(Interface.SubmitComplaintHandle, params);
                } else//报修
                {
                    for (ImageItem imageItem : Bimp.tempSelectBitmap) {
                        String imageBase64 = Utils.bitmapToBase64(imageItem.getBitmap());
                        params.put("base64string", imageBase64);

                        PostHttp.RequstPostHttp(Interface.UploadRepairPhoto, params);
                    }

                    PostHttp.RequstPostHttp(Interface.SubmitRepairHandle, params);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(HashMap<String, Object> result) {
            Bimp.tempSelectBitmap.clear();
            for (int i = 0; i < PublicWay.activityList.size(); i++) {
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }

            if (PrePage == 1)//投诉建议
                ComplaintDetailActivity.ActionType = 1;
            else//报修
                RepairDetailActivity.ActionType = 1;
            handler.sendEmptyMessage(CLOSE_WAIT_DIALOG);
        }
    }
}
