package com.kkwinter.floatbar;

import android.support.annotation.Keep;
import android.util.Log;

@Keep
public class YeLog {

    private final static String TAG = "CT_floatbar_v" + BuildConfig.VERSION_NAME;    //当前版本号

    private YeLog() {

    }

    private static String getLogTag(String tag) {
        return tag == null ? TAG : tag;
    }

    public static void dp(String format, String... args) {
        if (SwitchConfig.LOG) {
            i(String.format(format, (Object[]) args));
        }
    }


    public static void ip(String format, String... args) {
        if (SwitchConfig.LOG) {
            i(String.format(format, (Object[]) args));
        }
    }


    public static void ep(String format, String... args) {
        if (SwitchConfig.LOG) {
            e(String.format(format, (Object[]) args));
        }
    }

    public static void info(String msg) {
        if (msg != null) {
            Log.i(getLogTag(TAG), msg);
        }
    }

    public static void info(String tag, String msg) {
        if (msg != null) {
            Log.i(getLogTag(tag), msg);
        }
    }

    public static void debug(String tag, String msg){
        if (msg != null){
            Log.d(getLogTag(tag), msg);
        }
    }

    public static void error(String tag, String msg){
        if (msg != null){
            Log.e(getLogTag(tag), msg);
        }
    }



    public static void i(String msg) {
        if (SwitchConfig.LOG) {
            if (msg != null) {
                Log.i(getLogTag(TAG), msg);
            }
        }
    }


    public static void d(String msg) {

        if (SwitchConfig.LOG) {
            if (msg != null) {
                Log.d(getLogTag(TAG), msg);
            }
        }
    }

    public static void w(String msg) {

        if (SwitchConfig.LOG) {
            if (msg != null) {
                Log.w(getLogTag(TAG), msg);
            }
        }
    }


    public static void w(Exception ex) {
        if (SwitchConfig.LOG) {
            if (ex != null) {
                ex.printStackTrace();
            }
        }
    }


    public static void e(String msg) {
        if (SwitchConfig.LOG) {
            if (msg != null) {
                Log.e(getLogTag(TAG), msg);
            }
        }
    }


    public static void i(String tag, String msg) {
        if (SwitchConfig.LOG) {
            if (msg != null) {
                Log.i(getLogTag(tag), msg);
            }
        }
    }


    public static void d(String tag, String msg) {
        if (SwitchConfig.LOG) {
            if (msg != null) {
                Log.d(getLogTag(tag), msg);
            }
        }
    }


    public static void w(String tag, String msg) {
        if (SwitchConfig.LOG) {
            if (msg != null) {
                Log.w(getLogTag(tag), msg);
            }
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (SwitchConfig.LOG) {
            if (msg != null) {
                Log.w(getLogTag(tag), msg, tr);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (SwitchConfig.LOG) {
            if (msg != null) {
                Log.e(getLogTag(tag), msg);
            }
        }
    }
    public static void e(String tag, String msg, Throwable tr) {
        if (SwitchConfig.LOG) {
            if (msg != null) {
                Log.e(getLogTag(tag), msg, tr);
            }
        }
    }
}
