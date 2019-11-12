package com.kkwinter.floatbar;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;

/**
 * Created by jiantao.tu on 2018/9/21.
 */
public class PowerActivity extends Activity {

    private final static String COMPONENT_KEY = "component_key";

    public static void launch(ComponentName componentName) {
        Intent intent = new Intent(App.getApp(), PowerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(COMPONENT_KEY, componentName);
        App.getApp().startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.transparencyBar(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0f);
        ComponentName componentName = getIntent().getParcelableExtra(COMPONENT_KEY);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "One key lock screen need to active");
        startActivity(intent);
        finish();
    }


}
