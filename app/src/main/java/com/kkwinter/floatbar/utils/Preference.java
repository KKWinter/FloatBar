package com.kkwinter.floatbar.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {

    public static String FILE_NAME = "setting";
    public static final String KEY_DISPLAY = "KEY_DISPLAY";


    public static void persistBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, true);
    }

}
