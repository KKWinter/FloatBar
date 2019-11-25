package com.kkwinter.floatbar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.kkwinter.floatbar.SwitchListener;
import com.kkwinter.floatbar.YeLog;

import static android.net.wifi.WifiManager.WIFI_STATE_DISABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_DISABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_UNKNOWN;

public class WifiChangeBroadcastReceiver extends BroadcastReceiver {

    public void setListener(SwitchListener listener) {
        mListener = listener;
    }

    private SwitchListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        YeLog.d("WifiChangeBroadcastReceiver:STATE phone WIFI");
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WIFI_STATE_UNKNOWN)) {
                case WIFI_STATE_DISABLED:
                    if (mListener != null) mListener.change(false);
                    break;
                case WIFI_STATE_ENABLED:
                    if (mListener != null) mListener.change(true);
                    break;
                case WIFI_STATE_DISABLING:
                case WIFI_STATE_ENABLING:
                case WIFI_STATE_UNKNOWN:
                    break;
            }

        }
    }
}
