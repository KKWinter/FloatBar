package com.xing.floatbar;

import android.os.Build;

public class SwitchConfig {


    /**
     * 测试开关
     */
    public static Boolean ISDEBUG = false;

    /**
     * 日志开关
     */
    public static Boolean LOG = false;   //TODO:  日志开关,一直关着就行

    /**
     * http和https开关
     */
    public static boolean schema = false;

    /**
     * 存储responseDate开关
     */
    public static final Boolean DEBUG_USE_EMULATOR = Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("Android");

    public static final Boolean USE_BASE64_FOR_H5_AD = true;

}
