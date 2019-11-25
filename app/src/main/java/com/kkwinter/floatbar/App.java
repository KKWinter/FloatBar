package com.kkwinter.floatbar;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraManager;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.kkwinter.floatbar.anr.ANRWatchDog;
import com.kkwinter.floatbar.receiver.BlueChangeBroadcastReceiver;
import com.kkwinter.floatbar.receiver.ScreenBroadcastReceiver;
import com.kkwinter.floatbar.receiver.WifiChangeBroadcastReceiver;
import com.kkwinter.floatbar.utils.ContextUtil;

import static android.content.Intent.ACTION_CONFIGURATION_CHANGED;

public class App extends Application {

    private static App app;

    public static App getApp() {
        return app;
    }

    public final static Handler HANDLER = new Handler();

    public ScreenBroadcastReceiver mScreenReceiver;
    public WifiChangeBroadcastReceiver wifiChangeBroadcastReceiver;
    public BlueChangeBroadcastReceiver blueChangeBroadcastReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        ContextUtil.init(this);
        new ANRWatchDog().start();

        registerScreenReceiver();
        registerBlueReceiver();
        registerWifiReceiver();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterScreenReceiver();
        unregisterBlueReceiver();
        unregisterWifiReceiver();
        unregisterCamera();
    }

    void unregisterCamera() {
        CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (camManager != null) {
            camManager.unregisterAvailabilityCallback(WMInstance.getInstance().availabilityCallback);
            camManager.unregisterTorchCallback(WMInstance.getInstance().torchCallback);
        }
    }

    public void registerScreenReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(ACTION_CONFIGURATION_CHANGED);
        mScreenReceiver = new ScreenBroadcastReceiver();
        registerReceiver(mScreenReceiver, filter);
    }

    public void registerBlueReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        blueChangeBroadcastReceiver = new BlueChangeBroadcastReceiver();
        registerReceiver(blueChangeBroadcastReceiver, filter);
    }

    public void registerWifiReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifiChangeBroadcastReceiver = new WifiChangeBroadcastReceiver();
        registerReceiver(wifiChangeBroadcastReceiver, filter);
    }

    public void unregisterBlueReceiver() {
        if (blueChangeBroadcastReceiver != null) {
            unregisterReceiver(blueChangeBroadcastReceiver);
            blueChangeBroadcastReceiver = null;
        }
    }

    public void unregisterWifiReceiver() {
        if (wifiChangeBroadcastReceiver != null) {
            unregisterReceiver(wifiChangeBroadcastReceiver);
            wifiChangeBroadcastReceiver = null;
        }
    }

    public void unregisterScreenReceiver() {
        if (mScreenReceiver != null) {
            unregisterReceiver(mScreenReceiver);
            mScreenReceiver = null;
        }
    }
}