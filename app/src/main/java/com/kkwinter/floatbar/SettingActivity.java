package com.kkwinter.floatbar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingActivity extends Activity {

    private SharedPreferences sp;
    public static final String KEY_DISPLAY = "KEY_DISPLAY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        boolean display = sp.getBoolean(KEY_DISPLAY, true);

        final Switch displayControl = findViewById(R.id.s_w);

        if (display && WMInstance.getInstance().isShowIconView) {
            displayControl.setChecked(true);
            displayControl.setSwitchTextAppearance(SettingActivity.this, R.style.s_true);
        } else {
            displayControl.setChecked(false);
            displayControl.setSwitchTextAppearance(SettingActivity.this, R.style.s_false);
        }

        displayControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                //控制开关字体颜色
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean(KEY_DISPLAY, checked);
                edit.apply();
                if (checked) {
                    displayControl.setSwitchTextAppearance(SettingActivity.this, R.style.s_true);
                    WMInstance.getInstance().showIconView();
                } else {
                    WMInstance.getInstance().disableIconView();
                    displayControl.setSwitchTextAppearance(SettingActivity.this, R.style.s_false);
                }

            }
        });

    }
}

