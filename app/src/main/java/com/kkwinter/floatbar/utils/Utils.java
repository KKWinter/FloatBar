package com.kkwinter.floatbar.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.kkwinter.floatbar.R;

import java.io.File;
import java.util.Objects;

public class Utils {

    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + ABCService.class.getCanonicalName();
        YeLog.i("Utils-isAccessibilitySettingsOn:service:" + service);

        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            YeLog.i("Utils-isAccessibilitySettingsOn:accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            YeLog.e("Utils-isAccessibilitySettingsOn:Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            YeLog.i("Utils-isAccessibilitySettingsOn:***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    YeLog.i("Utils-isAccessibilitySettingsOn:-------------- > accessibilityService :: " + accessibilityService + " " + service);
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
     * 打开相机
     */
    public static void takePhoto() {
        PermissionActivity.launch(PermissionUtils.CODE_CAMERA);
    }

    /**
     * 返回键
     * home键
     * 锁屏
     * 截屏
     */
    public static void performByABCService(final Context context, ABCService abcService, int eventType) {

        if (!Utils.isAccessibilitySettingsOn(context)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
            builder.setCancelable(false);
            builder.setMessage("Please jump to system settings and activate accessibility server.");
            builder.setPositiveButton("jump", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            builder.setNegativeButton("cancel", null);
            Dialog dialog = builder.create();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Objects.requireNonNull(dialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                Objects.requireNonNull(dialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            dialog.show();

            return;
        }

        if (abcService != null) {
            abcService.performGlobalAction(eventType);

        }

    }


    /**
     * 飞行模式
     */
    public static boolean getAirPlaneState(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
    }


    public static void changeAirPlaneState(Context context) {
        try {
            // TODO: 2019-11-28  在小米设备上，后台startActivity无效； android 10上也会限制后台startActivity
            Intent intent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
