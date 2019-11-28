package com.kkwinter.floatbar;

import android.app.Application;
import android.os.Handler;

import com.kkwinter.floatbar.anr.ANRWatchDog;
import com.kkwinter.floatbar.flashlight.FlashLightManager;
import com.kkwinter.floatbar.utils.ContextUtil;

public class App extends Application {

    private static App app;

    public static App getApp() {
        return app;
    }

    public final static Handler HANDLER = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        ContextUtil.init(this);
        new ANRWatchDog().start();
        FlashLightManager.getInstance().init(this.getApplicationContext());
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        FlashLightManager.getInstance().killFlashLight();
    }


}