package com.kkwinter.floatbar.utils;

import android.support.annotation.Keep;
import android.util.Log;

import com.kkwinter.floatbar.BuildConfig;

@Keep
public class YeLog {

    public static Boolean LOG = false;   //TODO:  日志开关,一直关着就行
    private final static String TAG = "CT_floatbar_v" + BuildConfig.VERSION_NAME;    //当前版本号

    private YeLog() {

    }

    private static String getLogTag(String tag) {
        return tag == null ? TAG : tag;
    }

    public static void dp(String format, String... args) {
        if (LOG) {
            i(String.format(format, (Object[]) args));
        }
    }


    public static void ip(String format, String... args) {
        if (LOG) {
            i(String.format(format, (Object[]) args));
        }
    }

    public static void i(String msg) {
        if (LOG) {
            if (msg != null) {
                Log.i(getLogTag(TAG), msg);
            }
        }
    }


    public static void d(String msg) {

        if (LOG) {
            if (msg != null) {
                Log.d(getLogTag(TAG), msg);
            }
        }
    }

    public static void w(String msg) {

        if (LOG) {
            if (msg != null) {
                Log.w(getLogTag(TAG), msg);
            }
        }
    }


    public static void w(Exception ex) {
        if (LOG) {
            if (ex != null) {
                ex.printStackTrace();
            }
        }
    }


    public static void e(String msg) {
        if (LOG) {
            if (msg != null) {
                Log.e(getLogTag(TAG), msg);
            }
        }
    }


    public static void i(String tag, String msg) {
        if (LOG) {
            if (msg != null) {
                Log.i(getLogTag(tag), msg);
            }
        }
    }


    public static void d(String tag, String msg) {
        if (LOG) {
            if (msg != null) {
                Log.d(getLogTag(tag), msg);
            }
        }
    }


    public static void w(String tag, String msg) {
        if (LOG) {
            if (msg != null) {
                Log.w(getLogTag(tag), msg);
            }
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (LOG) {
            if (msg != null) {
                Log.w(getLogTag(tag), msg, tr);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (LOG) {
            if (msg != null) {
                Log.e(getLogTag(tag), msg);
            }
        }
    }
    public static void e(String tag, String msg, Throwable tr) {
        if (LOG) {
            if (msg != null) {
                Log.e(getLogTag(tag), msg, tr);
            }
        }
    }
}
