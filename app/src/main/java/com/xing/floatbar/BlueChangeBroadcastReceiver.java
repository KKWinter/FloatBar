package com.xing.floatbar;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by jiantao.tu on 2018/11/2.
 */
public class BlueChangeBroadcastReceiver extends BroadcastReceiver {

    public void setListener(SwitchListener listener) {
        mListener = listener;
    }

    private SwitchListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        YeLog.d("BlueChangeBroadcastReceiver:STATE 手机蓝牙");
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    if (mListener != null) mListener.change(false);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    break;
                case BluetoothAdapter.STATE_ON:
                    if (mListener != null) mListener.change(true);
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    break;
            }
        }
    }

}
