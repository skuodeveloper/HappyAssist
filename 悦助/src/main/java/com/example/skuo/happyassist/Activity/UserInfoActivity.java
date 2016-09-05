package com.example.skuo.happyassist.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.skuo.happyassist.Class.Result.UserImage;
import com.example.skuo.happyassist.Javis.Data.USERINFO;
import com.example.skuo.happyassist.Javis.MyView.MyImageView;
import com.example.skuo.happyassist.Javis.Tools.Utils;
import com.example.skuo.happyassist.Javis.http.GetHttp;
import com.example.skuo.happyassist.Javis.http.Interface;
import com.example.skuo.happyassist.Javis.http.PostHttp;
import com.example.skuo.happyassist.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {
    private ImageView iv_back;
    private Button btnSave, btnLogout;
    private MyImageView imageView;
    private TextView tv_name,tv_phone;
    private static final int BACK = 1;
    private static final int SAVE = 2;
    private static final int ALBUM_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int CROP_REQUEST_CODE = 3;
    private static final String IMAGE_UNSPECIFIED = "image/*";

    private static final int REQUEST_SHOW_IMAGE = 2;

    /**
     * 存储网络返回的数据
     */
    private HashMap<String, Object> hashMap;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

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
                    case REQUEST_SHOW_IMAGE:
                        UserImage userImage = (UserImage) msg.obj;

                        if (userImage.Data != null)
                            imageView.setImageBitmap(getHttpBitmap(userImage.Data));
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                Toast.makeText(UserInfoActivity.this, ex.getMessage().toString(), Toast.LENGTH_SHORT);
            }
        }
    };

    protected void show(int action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);

        switch (action) {
            case BACK:
                builder.setMessage("个人信息没有保存，确认退出吗？");
                break;
            case SAVE:
                builder.setMessage("您确认需要上传个人信息吗？");
                break;
        }

        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, String> params = new HashMap<String, String>();

                try {
                    Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    String imageBase64 = Utils.bitmapToBase64(image);
                    params.put("base64string", imageBase64);
                    params.put("ID", String.valueOf(USERINFO.AccountID));

                    PostHttp.RequstPostHttp(Interface.UploadAccountPhoto, params);

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);

                    dialog.dismiss();
                    finish();
                } catch (Exception ex) {
                    Toast.makeText(UserInfoActivity.this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("个人资料修改");

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setText(USERINFO.TrueName);

        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_phone.setText(USERINFO.Phone);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show(BACK);
                finish();
            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(SAVE);
            }
        });

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.clear();
                editor.commit();

                finish();
                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳到的界面
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
                startActivity(intent);
            }
        });

        imageView = (MyImageView) findViewById(R.id.imageView);
        //setImageView();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomAlertDialog();
            }
        });
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

    private void showCustomAlertDialog() {
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.show();
        Window win = alertDialog.getWindow();

        WindowManager.LayoutParams lp = win.getAttributes();
        win.setGravity(Gravity.CENTER);
        lp.alpha = 0.9f;
        win.setAttributes(lp);
        win.setContentView(R.layout.dialog);

        TextView camera_phone = (TextView) win.findViewById(R.id.camera_phone);
        camera_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                systemPhoto();
            }
        });
        TextView camera_camera = (TextView) win.findViewById(R.id.camera_camera);
        camera_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cameraPhoto();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ALBUM_REQUEST_CODE:
                if (data == null) {
                    return;
                }
                startCrop(data.getData());
                break;
            case CAMERA_REQUEST_CODE:
                File picture = new File(Environment.getExternalStorageDirectory()
                        + "/head.jpg");
                startCrop(Uri.fromFile(picture));
                break;
            case CROP_REQUEST_CODE:
                if (data == null) {
                    // TODO 如果之前以后有设置过显示之前设置的图片 否则显示默认的图片
                    return;
                }
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    imageView.setImageBitmap(photo); //把图片显示在ImageView控件上

                    //此处可以把Bitmap保存到sd卡中，具体请看：http://www.cnblogs.com/linjiqin/archive/2011/12/28/2304940.html
                    String path = Environment.getExternalStorageDirectory() + "/head.jpg";
                    saveImage(photo, path);

                    alertDialog.dismiss();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 判断sdcard卡是否可用
     *
     * @return 布尔类型 true 可用 false 不可用
     */
    private boolean isSDCardCanUser() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /*
    * *将突破保存到本地
    * */
    public static void saveImage(Bitmap photo, String spath) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false));
            photo.compress(Bitmap.CompressFormat.JPEG, 75, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开系统相册
     */
    private void systemPhoto() {
        Intent intent = new Intent();
        intent.setType(IMAGE_UNSPECIFIED);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, ALBUM_REQUEST_CODE);
    }

    /**
     * 调用相机拍照
     */
    private void cameraPhoto() {
        try {
            String sdStatus = Environment.getExternalStorageState();
            /* 检测sd是否可用 */
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(this, "SD卡不可用！", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.
                    getExternalStorageDirectory(), "head.jpg")));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 开始裁剪
     *
     * @param uri
     */
    private void startCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");//调用Android系统自带的一个图片剪裁页面,
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
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
}
