package com.kkwinter.floatbar;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.kkwinter.floatbar.receiver.PowerAdminReceiver;
import com.kkwinter.floatbar.utils.ContextUtil;
import com.kkwinter.floatbar.utils.Preference;

import java.util.Objects;

public class WMInstance {

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private DevicePolicyManager devicePolicyManager;

    private Context context;
    private static WMInstance wmInstance;

    private TouchView touchView;
    private View mainView;
    private ViewHolder viewHolder;


    boolean isShowMainView;
    public boolean isShowIconView;

    private BluetoothAdapter defaultAdapter;
    private WifiManager wifiManager;
    private Boolean isTurnOn;
    private CameraManager camManager;// 声明CameraManager对象
    private ABCService mAbService;
    private boolean isBackCameraOpen = false;


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


    public static WMInstance getInstance() {
        if (wmInstance == null) {
            wmInstance = new WMInstance();
        }
        return wmInstance;
    }

    private WMInstance() {
        this.context = ContextUtil.getAppContext();
        isTurnOn = false;
        isShowIconView = false;

        ABCService.setABServiceListener(new ABServiceListener() {
            @Override
            public void onCreate(AccessibilityService abService) {
                mAbService = (ABCService) abService;
            }

        });

        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Activity.DEVICE_POLICY_SERVICE);
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (camManager != null) {
            camManager.registerAvailabilityCallback(availabilityCallback, App.HANDLER);
            camManager.registerTorchCallback(torchCallback, App.HANDLER);
        }
        createWM();
    }


    private void createWM() {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }


    //弹出圆球悬浮窗
    public void showIconView() {
        if (!isShowIconView) {
            addIconView();
        }
    }

    //关闭圆球悬浮窗
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

    //添加圆球悬浮窗
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
        return Preference.getBoolean(ContextUtil.getAppContext(), Preference.KEY_DISPLAY);
    }


    private class ViewHolder {
        private View bgView;
        private ImageView itemCancel;
        private ImageView itemPhoto;
        private ImageView itemPower;

        private ImageView itemHome;
        private ImageView itemScreenshot;
        private ImageView itemFlashLight;

        private ImageView itemWifi;
        private ImageView itemBluetooth;
        private ImageView itemAirplane;
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


    private void initMainView() {
        if (mainView == null) {
            mainView = LayoutInflater.from(context).inflate(R.layout.wm_main_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.bgView = mainView.findViewById(R.id.wm_main_bgview);

            viewHolder.itemCancel = mainView.findViewById(R.id.wm_item_cancel);
            viewHolder.itemPhoto = mainView.findViewById(R.id.wm_item_photo);
            viewHolder.itemPower = mainView.findViewById(R.id.wm_item_power);

            viewHolder.itemHome = mainView.findViewById(R.id.wm_item_home);
            viewHolder.itemScreenshot = mainView.findViewById(R.id.wm_item_screenshot);
            viewHolder.itemFlashLight = mainView.findViewById(R.id.wm_item_flashlight);

            viewHolder.itemWifi = mainView.findViewById(R.id.wm_item_wifi);
            viewHolder.itemBluetooth = mainView.findViewById(R.id.wm_item_bluetooth);
            viewHolder.itemAirplane = mainView.findViewById(R.id.wm_item_airplane);
        }

        Log.i("test", "initMainView: >>>>");

        viewHolder.bgView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(context, "bgview", Toast.LENGTH_SHORT).show();
                addIconView();
            }
        });

        viewHolder.itemCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelClick();

            }
        });

        viewHolder.itemPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                phoneClick();
            }
        });


        viewHolder.itemPower.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                powerClick();
            }
        });


        viewHolder.itemHome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                addIconView();
                Utils.openHome(context);

            }
        });

        viewHolder.itemScreenshot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                screenShotClick();
            }

        });


        viewHolder.itemFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashLightClick();
            }
        });


        viewHolder.itemWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: 2019-11-26 会ANR
                viewHolder.itemWifi.setImageResource(Utils.changeWifiStatus(context) ? R.drawable.wifi : R.drawable.wifi_close);

            }
        });

        viewHolder.itemBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: 2019-11-26 ANR
                viewHolder.itemBluetooth.setImageResource(Utils.changeBlueToothStatus() ? R.drawable.bluetooth : R.drawable.bluetooth_close);

            }
        });


        viewHolder.itemAirplane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addIconView();
                Utils.changeAirPlaneStatus(context.getApplicationContext());
            }
        });


        viewHolder.itemWifi.setImageResource(Utils.isWifiOn(context) ? R.drawable.wifi : R.drawable.wifi_close);

        viewHolder.itemBluetooth.setImageResource(Utils.isBlueToothOn() ? R.drawable.bluetooth : R.drawable.bluetooth_close);

        viewHolder.itemAirplane.setImageResource(Utils.isAirPlaneOn(context) ? R.drawable.airplane : R.drawable.airplane_close);


        if (isTurnOn) {
            viewHolder.itemFlashLight.setImageResource(R.drawable.flashlight);
        } else {
            viewHolder.itemFlashLight.setImageResource(R.drawable.flashlight_close);
        }

    }


    private boolean isBackCameraAndFlash(String camId) {
        try {
            CameraCharacteristics characteristics = Objects.requireNonNull(camManager).getCameraCharacteristics(camId);
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


    private void phoneClick() {
        addIconView();
        PermissionActivity.launch(PermissionUtils.CODE_CAMERA);
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


}
