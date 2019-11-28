package com.kkwinter.floatbar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.kkwinter.floatbar.utils.ContextUtil;
import com.kkwinter.floatbar.utils.Preference;
import com.kkwinter.floatbar.R;
import com.kkwinter.floatbar.WMInstance;

public class MainActivity extends Activity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = ContextUtil.getAppContext();

        View viewById = findViewById(R.id.setting);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent("com.setting");
                startActivity(intent);
            }
        });

        View aboutus = findViewById(R.id.aboutus);
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent("com.aboutus");
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean display = Preference.getBoolean(context, Preference.KEY_DISPLAY);
        if (display) {
            if (!Settings.canDrawOverlays(context)) {

                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Please jump to system settings and activate window display function.")
                        .setPositiveButton("jump", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create()
                        .show();
            } else {
                WMInstance.getInstance().showIconView();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
