package com.kkwinter.floatbar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.kkwinter.floatbar.App;
import com.kkwinter.floatbar.WMInstance;
import com.kkwinter.floatbar.YeLog;

public class ScreenBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        YeLog.i("ScreenBroadcastReceiver:screen BroadcastReceiver");
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            YeLog.i("ScreenBroadcastReceiver:screen off BroadcastReceiver");
            if (Settings.canDrawOverlays(App.getApp())) {
                WMInstance.getInstance().showIconView();
            }
        } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
            YeLog.i("ScreenBroadcastReceiver:ACTION_CLOSE_SYSTEM_DIALOGS BroadcastReceiver");
            if (Settings.canDrawOverlays(App.getApp())) {
                WMInstance.getInstance().showIconView();
            }
        } else if (Intent.ACTION_CONFIGURATION_CHANGED.equals(intent.getAction())) {
            if (Settings.canDrawOverlays(App.getApp())) {
                WMInstance.getInstance().showIconView();
            }
        }
    }
}