package com.kkwinter.floatbar.utils;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class ABCService extends AccessibilityService {

    private static ABServiceListener mABServiceListener;

    public static void setABServiceListener(ABServiceListener abServiceListener) {
        mABServiceListener = abServiceListener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("test", "onCreate: >>");
        if (mABServiceListener != null) {
            mABServiceListener.onCreate(this);
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 此方法是在主线程中回调过来的，所以消息是阻塞执行的
    }

    @Override
    public void onInterrupt() {
    }
}

