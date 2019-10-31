package com.example.alan.sdkdemo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ricardo
 * 2019/10/28.
 */
public class ZoomViewPager extends ViewPager {
    private boolean canSlide = true;
    public ZoomViewPager(@NonNull Context context) {
        super(context);
    }

    public ZoomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (canSlide){
            return super.onInterceptTouchEvent(ev);
        }else {
            return false;
        }
    }

    public void setCanSlide(boolean canSlide){
        this.canSlide = canSlide;
    }
}
