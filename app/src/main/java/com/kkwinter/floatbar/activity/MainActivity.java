package com.kkwinter.floatbar.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kkwinter.floatbar.App;
import com.kkwinter.floatbar.utils.ContextUtil;
import com.kkwinter.floatbar.utils.Preference;
import com.kkwinter.floatbar.R;
import com.kkwinter.floatbar.WMInstance;
import com.kkwinter.floatbar.YeLog;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        boolean display = Preference.getBoolean(ContextUtil.getAppContext(), Preference.KEY_DISPLAY);
        if (display) {
            YeLog.i("MainActivity:canDrawOverlays=" + Settings.canDrawOverlays(this));
            if (!Settings.canDrawOverlays(App.getApp())) {

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
