package com.example.alan.sdkdemo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.alan.sdkdemo.util.BitmapUtil;
import com.example.alan.sdkdemo.widget.ZoomImageView;
import com.example.alan.sdkdemo.widget.ZoomViewPager;
import com.vcrtc.webrtc.RTCManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @author ricardo
 */
public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private List<String> imagePaths;
    private ZoomViewPager pager;
    ViewGroup.LayoutParams params;

    public ViewPagerAdapter(Context context, List imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

//    public ViewPagerAdapter(Context context, List imagePaths, ZoomViewPager pager) {
//        this.context = context;
//        this.imagePaths = imagePaths;
//        this.pager = pager;
//    }


    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        ZoomImageView iv = new ZoomImageView(container.getContext(), null);
        ImageView iv = new ImageView(container.getContext());
        if (onItemImageListener != null) {
            iv.setOnClickListener((view) -> onItemImageListener.onClick());
//            iv.setOnCutListener(bitmap -> onItemImageListener.onCutBitmap(bitmap));
        }

        Bitmap bitmap;
        if (RTCManager.isIsShitongPlatform()) {
            bitmap = com.example.alan.sdkdemo.util.BitmapUtil
                    .formatBitmap16_9(com.example.alan.sdkdemo.util.BitmapUtil
                            .getImage(imagePaths.get(position)), 1920, 1080);
        } else {
            bitmap = BitmapUtil.getImage(imagePaths.get(position));
        }
        iv.setScaleType(ImageView.ScaleType.CENTER);
        Glide.with(context).load(bitmap).into(iv);

//        iv.setImageBitmap(bitmap);
        // 添加到ViewPager容器
        container.addView(iv, params);

        // 返回填充的View对象
        return iv;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        ((ZoomImageView) object).reset();
        container.removeView((View) object);
    }

    private OnItemImageListener onItemImageListener;

    void setOnItemImageListener(OnItemImageListener onItemImageListener) {
        this.onItemImageListener = onItemImageListener;
    }

    public interface OnItemImageListener {
        void onClick();

        void onCutBitmap(Bitmap bitmap);
    }
}
