package com.kkwinter.floatbar;

import android.accessibilityservice.AccessibilityService;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.PixelFormat;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.kkwinter.floatbar.flashlight.FlashLightManager;
import com.kkwinter.floatbar.utils.ABCService;
import com.kkwinter.floatbar.utils.ABServiceListener;
import com.kkwinter.floatbar.utils.ContextUtil;
import com.kkwinter.floatbar.utils.Preference;
import com.kkwinter.floatbar.utils.Utils;

public class WMInstance {

    private Context context;
    private Handler handler = new Handler();

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private TouchView touchView;
    private View mainView;
    private ViewHolder viewHolder;

    public boolean isShowMainView = false;
    public boolean isShowIconView = false;

    private ABCService mAbService;
    private BluetoothAdapter defaultAdapter;
    private WifiManager wifiManager;
    private FlashLightManager flashLightManager;
    private boolean flashLightState = false;
    private boolean wifiState = false;
    private boolean blueToothState = false;
    private boolean airPlaneState = false;


    private static WMInstance wmInstance;

    public static WMInstance getInstance() {
        if (wmInstance == null) {
            wmInstance = new WMInstance();
        }
        return wmInstance;
    }

    private WMInstance() {
        this.context = ContextUtil.getAppContext();
        ABCService.setABServiceListener(new ABServiceListener() {
            @Override
            public void onCreate(AccessibilityService abService) {
                mAbService = (ABCService) abService;
            }

        });
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        flashLightManager = FlashLightManager.getInstance();

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    }


    //弹出圆球悬浮窗
    public void showIconView() {
        if (!isShowIconView) {
            addIconView();
        }
    }

    //关闭圆球悬浮窗
    public void disableIconView() {
        if (touchView != null) {
            windowManager.removeView(wmInstance.touchView);
            isShowIconView = false;
        }
    }

    //添加圆球悬浮窗
    private void addIconView() {
        if (!Preference.getBoolean(context, Preference.KEY_DISPLAY)) return;

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
            windowManager.addView(touchView, layoutParams);

            isShowIconView = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ViewHolder {
        private View bgView;
        private ImageView itemBack;
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
        if (!Preference.getBoolean(context, Preference.KEY_DISPLAY)) return;

        try {
            if (touchView != null && windowManager != null) {
                try {
                    windowManager.removeView(touchView);
                    isShowIconView = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //获取初始化状态
            if (wifiManager != null) wifiState = wifiManager.getWifiState() == 3;
            if (defaultAdapter != null) blueToothState = defaultAdapter.isEnabled();
            if (flashLightManager != null) flashLightState = flashLightManager.getFlashLightState();
            airPlaneState = Utils.getAirPlaneState(context);

            initMainView();

            layoutParams.alpha = 1f;
            layoutParams.x = 0;
            layoutParams.y = 0;
            DisplayMetrics dm = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(dm);
            layoutParams.width = dm.widthPixels;
            layoutParams.height = dm.heightPixels;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
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

            viewHolder.itemBack = mainView.findViewById(R.id.wm_item_back);
            viewHolder.itemHome = mainView.findViewById(R.id.wm_item_home);
            viewHolder.itemPower = mainView.findViewById(R.id.wm_item_power);

            viewHolder.itemPhoto = mainView.findViewById(R.id.wm_item_photo);
            viewHolder.itemScreenshot = mainView.findViewById(R.id.wm_item_screenshot);
            viewHolder.itemFlashLight = mainView.findViewById(R.id.wm_item_flashlight);

            viewHolder.itemWifi = mainView.findViewById(R.id.wm_item_wifi);
            viewHolder.itemBluetooth = mainView.findViewById(R.id.wm_item_bluetooth);
            viewHolder.itemAirplane = mainView.findViewById(R.id.wm_item_airplane);
        }

        //设置初始状态
        viewHolder.itemFlashLight.setImageResource(flashLightState ? R.drawable.flashlight : R.drawable.flashlight_close);

        viewHolder.itemWifi.setImageResource(wifiState ? R.drawable.wifi : R.drawable.wifi_close);

        viewHolder.itemBluetooth.setImageResource(blueToothState ? R.drawable.bluetooth : R.drawable.bluetooth_close);

        viewHolder.itemAirplane.setImageResource(airPlaneState ? R.drawable.airplane : R.drawable.airplane_close);


        //设置点击事件
        viewHolder.bgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIconView();
            }
        });

        viewHolder.itemBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addIconView();
                Utils.performByABCService(context, mAbService, AccessibilityService.GLOBAL_ACTION_BACK);
            }
        });

        viewHolder.itemHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addIconView();
                Utils.performByABCService(context, mAbService, AccessibilityService.GLOBAL_ACTION_HOME);
            }
        });

        viewHolder.itemPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: 2019-11-28 api 28
                addIconView();
                Utils.performByABCService(context, mAbService, AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN);
            }
        });


        viewHolder.itemPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addIconView();
                Utils.takePhoto();
            }
        });

        viewHolder.itemScreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: 2019-11-28  api 28
                addIconView();
                Utils.performByABCService(context, mAbService, AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT);
            }
        });


        viewHolder.itemFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flashLightState = !flashLightState;
                flashLightManager.startFlashLight(flashLightState);
                viewHolder.itemFlashLight.setImageResource(flashLightState ? R.drawable.flashlight : R.drawable.flashlight_close);
            }
        });


        viewHolder.itemWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addIconView();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        wifiState = !wifiState;
                        wifiManager.setWifiEnabled(wifiState);
                        viewHolder.itemWifi.setImageResource(wifiState ? R.drawable.wifi : R.drawable.wifi_close);
                    }
                }, 100);
            }
        });

        viewHolder.itemBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addIconView();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        blueToothState = !blueToothState;
                        if (blueToothState) {
                            defaultAdapter.enable();
                        } else {
                            defaultAdapter.disable();
                        }
                        viewHolder.itemBluetooth.setImageResource(blueToothState ? R.drawable.bluetooth : R.drawable.bluetooth_close);
                    }
                }, 100);

            }
        });


        viewHolder.itemAirplane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addIconView();
                Utils.changeAirPlaneState(context.getApplicationContext());
            }
        });

    }


}
