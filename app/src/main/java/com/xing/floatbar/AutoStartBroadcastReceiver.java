package com.xing.floatbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class AutoStartBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        YeLog.i("AutoStartBroadcastReceiver:auto start BroadcastReceiver");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            YeLog.i("AutoStartBroadcastReceiver:into BroadcastReceiver");

            if (Settings.canDrawOverlays(App.getApp())) {
                WMInstance.getInstance().showIconView();
            }
        }
    }
}
