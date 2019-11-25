package com.kkwinter.floatbar.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.kkwinter.floatbar.utils.ContextUtil;
import com.kkwinter.floatbar.utils.Preference;
import com.kkwinter.floatbar.R;
import com.kkwinter.floatbar.WMInstance;

public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        boolean display = Preference.getBoolean(ContextUtil.getAppContext(), Preference.KEY_DISPLAY);
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
                Preference.persistBoolean(ContextUtil.getAppContext(), Preference.KEY_DISPLAY, checked);

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

