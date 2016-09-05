package com.example.skuo.happyassist.Javis.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.skuo.happyassist.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 16-7-26.
 */
public class Adapter_Image_Gallery extends BaseAdapter {
    public String[] mImageUrls;
    private Context mContext;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;

    public Adapter_Image_Gallery(Context context, ImageLoader imageLoader, String[] imageUrls) {
        this.mContext = context;
        this.mImageUrls = imageUrls;
        this.mImageLoader = imageLoader;

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory()
                .cacheOnDisc()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public int getCount() {
        int Count = 0;
        if (mImageUrls != null)
            Count = mImageUrls.length;

        return Count;
    }

    @Override
    public Object getItem(int position) {
        return mImageUrls[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (ImageView) convertView;
        if (imageView == null) {
            imageView = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.item_gallery_image, parent, false);
        }

        mImageLoader.displayImage(mImageUrls[position], imageView, options);
        return imageView;
    }
}
