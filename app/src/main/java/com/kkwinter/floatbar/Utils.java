package com.kkwinter.floatbar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by jiantao.tu on 2018/9/20.
 */
public class Utils {
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + ABCService.class.getCanonicalName();
        YeLog.i("Utils-isAccessibilitySettingsOn:service:" + service);
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            YeLog.i("Utils-isAccessibilitySettingsOn:accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            YeLog.e("Utils-isAccessibilitySettingsOn:Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(
                ':');

        if (accessibilityEnabled == 1) {
            YeLog.i("Utils-isAccessibilitySettingsOn:***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    YeLog.i("Utils-isAccessibilitySettingsOn:-------------- > accessibilityService :: " + accessibilityService + " " +
                            service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        YeLog.i("Utils-isAccessibilitySettingsOn:We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            YeLog.i("Utils-isAccessibilitySettingsOn:***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }


    @SuppressLint("SdCardPath")
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        if (sdDir == null) sdDir = new File("/sdcard");
        return sdDir.toString();
    }


    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static int px2dp(int pxVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                pxVal, Resources.getSystem().getDisplayMetrics());
    }


    public static int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, Resources.getSystem().getDisplayMetrics());
    }


    /**
     * 修改状态栏为全透明
     */
    @TargetApi(19)
    public static void transparencyBar(Activity activity) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

    }


    /**
     * 物理返回键
     */

    /**
     * 物理home键
     */
    public static void openHome(Context context) {
        try {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            home.addCategory(Intent.CATEGORY_HOME);
            context.startActivity(home);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 电源键【锁屏】
     */

    /**
     * 打开相机
     */

    /**
     * 截屏
     */

    /**
     * 手电筒
     */

    /**
     * isWifiOn: return获取的wifi的状态，true开启，false关闭
     * changeWifiStatus： return改变之后的结果
     */
    public static boolean isWifiOn(Context context) {

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        /*
         {@link #WIFI_STATE_DISABLED},   1
         {@link #WIFI_STATE_DISABLING},  0
         {@link #WIFI_STATE_ENABLED},    3
         {@link #WIFI_STATE_ENABLING},   2
         {@link #WIFI_STATE_UNKNOWN}     4
         */
        return wifiManager.getWifiState() == 3;
    }

    public static boolean changeWifiStatus(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        /*
         android Q上被废弃
         */
        boolean result = wifiManager.getWifiState() == 3;
        wifiManager.setWifiEnabled(!result);
        return !result;
    }


    /**
     * isBlueToothOn: return获取的蓝牙状态，true开启，false关闭
     * changeBlueToothStatus: return改变之后的结果
     */
    public static boolean isBlueToothOn() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        return defaultAdapter.isEnabled();
    }

    public static boolean changeBlueToothStatus() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (isBlueToothOn()) {
            defaultAdapter.disable();
            return false;
        } else {
            defaultAdapter.enable();
            return true;
        }
    }


    /**
     * 飞行模式
     */
    public static boolean isAirPlaneOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
    }


    public static void changeAirPlaneStatus(Context context) {
        //在后台startActivity无效
        Intent intent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
