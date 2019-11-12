package com.xing.floatbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import static android.net.wifi.WifiManager.WIFI_STATE_DISABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_DISABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_UNKNOWN;

/**
 * Created by jiantao.tu on 2018/11/2.
 */
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
                case WIFI_STATE_DISABLED: {
                    if (mListener != null) mListener.change(false);
                    break;
                }
                case WIFI_STATE_DISABLING: {
                    break;
                }
                case WIFI_STATE_ENABLED: {
                    if (mListener != null) mListener.change(true);
                    break;
                }
                case WIFI_STATE_ENABLING: {
                    break;
                }
                case WIFI_STATE_UNKNOWN: {
                    break;
                }
            }

        }
    }
}
