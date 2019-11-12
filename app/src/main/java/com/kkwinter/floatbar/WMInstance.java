package com.kkwinter.floatbar;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;

import static com.kkwinter.floatbar.SettingActivity.KEY_DISPLAY;

public class WMInstance {

    private SharedPreferences sp;
    WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private DevicePolicyManager devicePolicyManager;

    @SuppressLint("StaticFieldLeak")
    private static WMInstance wmInstance;
    private TouchView touchView;
    private View mainView;
    View adView;
    private BluetoothAdapter defaultAdapter;

    private WifiManager wifiManager;

    private ViewHolder viewHolder;

    private Boolean isTurnOn;

    boolean isShowMainView;

    boolean isShowIconView;

    private Context context;
    private CameraManager camManager;// 声明CameraManager对象
    private ABCService mAbService;
    private boolean isBackCameraOpen = false;

    private WifiChangeBroadcastReceiver wifiChangeBroadcastReceiver;

    private BlueChangeBroadcastReceiver blueChangeBroadcastReceiver;

    CameraManager.AvailabilityCallback availabilityCallback = new CameraManager.AvailabilityCallback() {
        @Override
        public void onCameraAvailable(@NonNull String cameraId) {
            super.onCameraAvailable(cameraId);

            if (isBackCameraAndFlash(cameraId)) {
                YeLog.i("onCameraAvailable");
                isBackCameraOpen = false;
            }
        }


        @Override
        public void onCameraUnavailable(@NonNull String cameraId) {
            super.onCameraUnavailable(cameraId);

            if (isBackCameraAndFlash(cameraId)) {
                YeLog.i("onCameraUnavailable");
                isBackCameraOpen = true;
                isTurnOn = false;
            }
        }
    };

    CameraManager.TorchCallback torchCallback = new CameraManager.TorchCallback() {
        @Override
        public void onTorchModeUnavailable(@NonNull String cameraId) {
            super.onTorchModeUnavailable(cameraId);
        }


        @Override
        public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
            super.onTorchModeChanged(cameraId, enabled);
            YeLog.i("onTorchModeChanged enabled=" + enabled);
            if (isShowMainView) {
                if (enabled) {
                    viewHolder.itemFlashLight.setImageResource(R.drawable.flashlight);
                    isTurnOn = true;
                } else {
                    viewHolder.itemFlashLight.setImageResource(
                            R.drawable.flashlight_close);
                    isTurnOn = false;
                }
            }
        }
    };

    private WMInstance() {

        ABServiceListener abServiceListener = new ABServiceListener() {

            @Override
            public void onCreate(AccessibilityService abService) {
                mAbService = (ABCService) abService;
            }

        };
        ABCService.setABServiceListener(abServiceListener);
        this.context = App.getApp();
        isTurnOn = false;
        isShowIconView = false;
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(
                Activity.DEVICE_POLICY_SERVICE);
        wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (camManager != null) {
            camManager.registerAvailabilityCallback(availabilityCallback, App.HANDLER);
            camManager.registerTorchCallback(torchCallback, App.HANDLER);
        }

        wifiChangeBroadcastReceiver = App.getApp().wifiChangeBroadcastReceiver;
        blueChangeBroadcastReceiver = App.getApp().blueChangeBroadcastReceiver;
        wifiChangeBroadcastReceiver.setListener(new SwitchListener() {
            @Override
            public void change(boolean enabled) {
                if (isShowMainView) {
                    if (enabled) {
                        viewHolder.itemWifi.setImageResource(R.drawable.wifi);
                    } else {
                        viewHolder.itemWifi.setImageResource(R.drawable.wifi_close);
                    }
                }
            }
        });

        blueChangeBroadcastReceiver.setListener(new SwitchListener() {
            @Override
            public void change(boolean enabled) {
                if (isShowMainView) {
                    if (enabled) {
                        viewHolder.itemBluetooth.setImageResource(R.drawable.bluetooth);
                    } else {
                        viewHolder.itemBluetooth.setImageResource(R.drawable.bluetooth_close);
                    }
                }
            }
        });
        createWM();
        sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
    }


    public void showIconView() {
        if (!isShowIconView) {
            addIconView();
        }
    }


    public void disableIconView() {

        if (isShowIconView) {
            windowManager.removeView(wmInstance.touchView);
            isShowIconView = false;
        } else {
            if (touchView != null) {
                windowManager.removeView(wmInstance.touchView);
                isShowIconView = false;
            }

        }

    }


    /**
     * @return WMInstance
     * @Title: getInstance
     * @Description: TODO
     */
    public static WMInstance getInstance() {
        if (wmInstance == null) {
            wmInstance = new WMInstance();
        }
        return wmInstance;
    }


    public static void launchAppDetail(Context context, String appPkg) {

        final String GOOGLE_PLAY = "com.android.vending";//这里对应的是谷歌商店，跳转别的商店改成对应的即可
        try {
            if (TextUtils.isEmpty(appPkg)) {
                return;
            }
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage(GOOGLE_PLAY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void createWM() {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }


    private class ViewHolder {
        private View bgView;
        private ImageView itemCancel;
        private ImageView itemPhone;
        private ImageView itemHome;
        private ImageView itemPower;
        private ImageView itemCar;
        private ImageView itemHotel;
        private ImageView itemLunch;
        private ImageView itemAirport;

        private ImageView itemBluetooth;

        private ImageView itemAirplane;

        private ImageView itemWifi;

        private ImageView itemFlashLight;

        private ImageView itemScreenshot;
    }


    private static boolean isAirModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
    }


    private void addMainView() {
        if (!isDisplay()) return;
        try {
            if (touchView != null) {

                try {
                    windowManager.removeView(touchView);
                    isShowIconView = false;
                } catch (Exception e) {
                }

            }

            initMainView();
//            if (wmBottom != null) {
//                if (App.getApp().getResources().getConfiguration().orientation ==
//                        Configuration.ORIENTATION_LANDSCAPE) {
//                    wmBottom.setVisibility(View.GONE);
//                } else {
//                    wmBottom.setVisibility(View.VISIBLE);
//                }
//            }
            layoutParams.alpha = 1f;
            layoutParams.x = 0;
            layoutParams.y = 0;
            DisplayMetrics dm = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(dm);
            layoutParams.width = dm.widthPixels;
            layoutParams.height = dm.heightPixels;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
            windowManager.addView(mainView, layoutParams);
            isShowMainView = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Drawable getBG() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(Utils.dp2px(15));
        drawable.setColor(0xCC171b19);
        drawable.setShape(GradientDrawable.RECTANGLE);
        return drawable;
    }

    ViewGroup wmBottom;

    private void initMainView() {
        if (mainView == null) {
            mainView = LayoutInflater.from(context).inflate(R.layout.wm_main_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.bgView = mainView.findViewById(R.id.wm_main_bgview);
            viewHolder.itemCancel = mainView.findViewById(R.id.wm_item_cancel);
            viewHolder.itemPhone = mainView.findViewById(R.id.wm_item_phone);
            viewHolder.itemHome = mainView.findViewById(R.id.wm_item_home);
            viewHolder.itemPower = mainView.findViewById(R.id.wm_item_power);
            viewHolder.itemWifi = mainView.findViewById(R.id.wm_item_wifi);
            viewHolder.itemBluetooth = mainView.findViewById(R.id.wm_item_bluetooth);
            viewHolder.itemAirplane = mainView.findViewById(R.id.wm_item_airplane);
            viewHolder.itemFlashLight = mainView.findViewById(R.id.wm_item_flashlight);
            viewHolder.itemScreenshot = mainView.findViewById(R.id.wm_item_screenshot);
            viewHolder.itemCar = mainView.findViewById(R.id.wm_item_car);
            viewHolder.itemHotel = mainView.findViewById(R.id.wm_item_hotel);
            viewHolder.itemLunch = mainView.findViewById(R.id.wm_item_lunch);
            viewHolder.itemAirport = mainView.findViewById(R.id.wm_item_airport);
            View wmTop = mainView.findViewById(R.id.wm_top);
            wmBottom = mainView.findViewById(R.id.wm_bottom);
            wmTop.setBackground(getBG());
            wmBottom.setBackground(getBG());

            viewHolder.itemBluetooth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    blueToothClick();
                }
            });

            viewHolder.itemAirplane.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    airplaneClick();
                }
            });

            viewHolder.itemHotel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hotelClick();
                }
            });

            viewHolder.itemAirport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    airportClick();
                }
            });

            viewHolder.itemLunch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    lunchClick();
                }

            });

            viewHolder.itemCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carClick();
                }
            });

            viewHolder.bgView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    addIconView();
                }
            });

            viewHolder.itemCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    cancelClick();

                }
            });

            viewHolder.itemWifi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    wifiClick();
                }
            });
            viewHolder.itemPhone.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    phoneClick();
                }
            });
            viewHolder.itemHome.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    homeClick();
                }
            });
            viewHolder.itemPower.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    powerClick();
                }
            });

            viewHolder.itemFlashLight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flashLightClick();
                }
            });

            viewHolder.itemScreenshot.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    screenShotClick();
                }

            });
        }

        int wifiState = wifiManager.getWifiState();
        if (wifiState == 1) {
            viewHolder.itemWifi.setImageResource(R.drawable.wifi_close);
        } else if (wifiState == 3) {
            viewHolder.itemWifi.setImageResource(R.drawable.wifi);
        }

        if (defaultAdapter.isEnabled()) {
            viewHolder.itemBluetooth.setImageResource(R.drawable.bluetooth);
        } else {
            viewHolder.itemBluetooth.setImageResource(R.drawable.bluetooth_close);
        }

        if (isAirModeOn(context)) {
            viewHolder.itemAirplane.setImageResource(R.drawable.airplane);
        } else {
            viewHolder.itemAirplane.setImageResource(R.drawable.airplane_close);
        }

        if (isTurnOn) {
            viewHolder.itemFlashLight.setImageResource(R.drawable.flashlight);
        } else {
            viewHolder.itemFlashLight.setImageResource(R.drawable.flashlight_close);
        }

    }


    private boolean isBackCameraAndFlash(String camId) {
        try {
            CameraCharacteristics characteristics = Objects.requireNonNull(camManager)
                    .getCameraCharacteristics(camId);
            Boolean hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            Boolean isFacingBack = facing != null &&
                    CameraCharacteristics.LENS_FACING_BACK == facing;

            if (hasFlash != null && isFacingBack && hasFlash) {
                return true;
            }

        } catch (CameraAccessException ignored) {
        }
        return false;
    }


    private boolean isBackCameraAndFlash() {
        try {
            for (String camId : camManager != null ? camManager.getCameraIdList() : new String[0]) {
                if (isBackCameraAndFlash(camId)) return true;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void screenShotClick() {
        addIconView();
        PermissionActivity.launch(PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE);
    }


    private void flashLightClick() {
        if (isBackCameraOpen) {
            Toast.makeText(context, "You can't use flashlight while using camera",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (isTurnOn) {
            lightSwitch(true);
//            viewHolder.itemFlashLight.setImageResource(R.drawable.flashlight_close);
//            isTurnOn = false;
        } else {
            lightSwitch(false);
//            viewHolder.itemFlashLight.setImageResource(R.drawable.flashlight);
//            isTurnOn = true;
        }
    }


    private void powerClick() {
        addIconView();
        final ComponentName componentName = new ComponentName(context, PowerAdminReceiver.class);
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.lockNow();
        } else {// 第一次运行程序
            AlertDialog.Builder builder = new AlertDialog.Builder(App.getApp(),
                    R.style.Theme_AppCompat_Light_Dialog_Alert);
            builder.setCancelable(false);
            builder.setMessage("Please jump to system settings and activate device admin.");
            builder.setPositiveButton("jump", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PowerActivity.launch(componentName);
                }
            });

            builder.setNegativeButton("cancel", null);
            Dialog dialog = builder.create();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Objects.requireNonNull(dialog.getWindow())
                        .setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                Objects.requireNonNull(dialog.getWindow())
                        .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            dialog.show();

        }
    }


    private void homeClick() {
        addIconView();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(intent);
    }


    private void phoneClick() {
        addIconView();
        PermissionActivity.launch(PermissionUtils.CODE_CAMERA);
    }


    private void wifiClick() {
        int wifiState = wifiManager.getWifiState();

        if (wifiState == 1) {

            wifiManager.setWifiEnabled(true);
            viewHolder.itemWifi.setImageResource(R.drawable.wifi);

        } else if (wifiState == 3) {
            wifiManager.setWifiEnabled(false);
            viewHolder.itemWifi.setImageResource(R.drawable.wifi_close);

        }
    }


    private void cancelClick() {

        if (!Utils.isAccessibilitySettingsOn(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(App.getApp(),
                    R.style.Theme_AppCompat_Light_Dialog_Alert);
            builder.setCancelable(false);
            builder.setMessage("Please jump to system settings and activate accessibility server.");
            builder.setPositiveButton("jump", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addIconView();
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    context.startActivity(intent);
                }
            });

            builder.setNegativeButton("cancel", null);
            Dialog dialog = builder.create();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Objects.requireNonNull(dialog.getWindow())
                        .setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                Objects.requireNonNull(dialog.getWindow())
                        .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            dialog.show();

            return;
        }
        if (mAbService != null) {
            mAbService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        }
    }


    private void carClick() {
        if (!isDisplay()) return;
        addIconView();

        layoutParams.alpha = 1f;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.START | Gravity.TOP;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {

            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        final View view1 = LayoutInflater.from(context).inflate(R.layout.waiting, null);

        View hideView = view1.findViewById(R.id.layout_hide);

        hideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                windowManager.removeView(view1);

            }
        });

        windowManager.addView(view1, layoutParams);

        adView = LayoutInflater.from(context).inflate(R.layout.ad_layout, null);

    }


    private void lunchClick() {
        if (!isDisplay()) return;
        addIconView();

        layoutParams.alpha = 1f;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.START | Gravity.TOP;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {

            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        final View view1 = LayoutInflater.from(context).inflate(R.layout.waiting, null);

        View hideView = view1.findViewById(R.id.layout_hide);

        hideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                windowManager.removeView(view1);

            }
        });

        windowManager.addView(view1, layoutParams);

        adView = LayoutInflater.from(context).inflate(R.layout.ad_layout, null);

    }


    private void airportClick() {
        if (!isDisplay()) return;
        addIconView();

        layoutParams.alpha = 1f;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.START | Gravity.TOP;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {

            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        final View view1 = LayoutInflater.from(context).inflate(R.layout.waiting, null);

        View hideView = view1.findViewById(R.id.layout_hide);

        hideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                windowManager.removeView(view1);

            }
        });

        windowManager.addView(view1, layoutParams);

        adView = LayoutInflater.from(context).inflate(R.layout.ad_layout, null);

    }


    private void hotelClick() {
        if (!isDisplay()) return;
        addIconView();

        layoutParams.alpha = 1f;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.START | Gravity.TOP;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        final View view1 = LayoutInflater.from(context).inflate(R.layout.waiting, null);

        View hideView = view1.findViewById(R.id.layout_hide);

        hideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                windowManager.removeView(view1);

            }
        });

        windowManager.addView(view1, layoutParams);

        if (adView == null) {
            adView = LayoutInflater.from(context).inflate(R.layout.ad_layout, null);
        }
    }


    void airplaneClick() {
        if (isShowMainView) {
            addIconView();
            Intent intent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
            //            boolean flag = isAirModeOn(context);
            //            if (flag) {
            //                viewHolder.itemAirplane.setImageResource(R.drawable.airplane);
            //            } else {
            //                viewHolder.itemAirplane.setImageResource(R.drawable.airplane_close);
            //            }
            //            Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, flag ? 1 : 0);
            //            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            //            intent.putExtra("state", flag);
            //            context.sendBroadcast(intent);
        }
    }


    private void blueToothClick() {

        if (defaultAdapter.isEnabled()) {
            defaultAdapter.disable();
            viewHolder.itemBluetooth.setImageResource(R.drawable.bluetooth_close);
        } else {

            defaultAdapter.enable();
            viewHolder.itemBluetooth.setImageResource(R.drawable.bluetooth);
        }
    }


    /**
     * 手电筒控制方法
     */
    private void lightSwitch(final boolean lightStatus) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            try {
                camManager.setTorchMode("0", !lightStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void addIconView() {
        if (!isDisplay()) return;
        try {
            if (mainView != null && windowManager != null) {
                try {
                    windowManager.removeView(mainView);
                    isShowMainView = false;
                } catch (Exception ignore) {
                }
            }
            if (touchView == null) {
                touchView = new TouchView(context, windowManager, layoutParams);
                touchView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addMainView();
                    }
                });
            }

            layoutParams.x = touchView.getIconViewX();
            layoutParams.y = touchView.getIconViewY();
            layoutParams.width = TouchView.ICON_WIDTH;
            layoutParams.height = TouchView.ICON_HEIGHT;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
            if (windowManager != null) windowManager.addView(touchView, layoutParams);

            isShowIconView = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isDisplay() {
        return sp.getBoolean(KEY_DISPLAY, true);
    }

}
