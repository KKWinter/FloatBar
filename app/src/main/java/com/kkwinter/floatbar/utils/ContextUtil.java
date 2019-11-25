package com.kkwinter.floatbar.utils;

import android.app.Application;
import android.content.Context;

public class ContextUtil {

    private static Context globalAppContext;

    public synchronized static void init(Context context) {
        if (context != null) {
            if (context instanceof Application) {
                globalAppContext = context;
            } else {
                globalAppContext = context.getApplicationContext();
            }
        }
    }

    public synchronized static Context getAppContext() {
        if (globalAppContext == null) {
            try {
                Application application = (Application) Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication").invoke(null, (Object[]) null);
                if (application != null) {
                    globalAppContext = application;
                    return application;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Application application = (Application) Class.forName("android.app.AppGlobals")
                        .getMethod("getInitialApplication").invoke(null, (Object[]) null);
                if (application != null) {
                    globalAppContext = application;
                    return application;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new IllegalStateException("ContextUtil is not initialed.");
        }
        return globalAppContext;
    }

}
