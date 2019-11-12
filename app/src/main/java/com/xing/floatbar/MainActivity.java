package com.xing.floatbar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import static com.xing.floatbar.SettingActivity.KEY_DISPLAY;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static int FLOATING_WINDOW_CODE = 100;

    private Dialog dialog;
    private SharedPreferences sp;
    private RelativeLayout rl_container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View viewById = findViewById(R.id.setting);
        sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
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
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        rl_container = findViewById(R.id.rl_container);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // AppOpsManager.permissionToOp()
        boolean display = sp.getBoolean(KEY_DISPLAY, true);
        if (display) {
            YeLog.i("MainActivity:canDrawOverlays=" + Settings.canDrawOverlays(this));
            if (!Settings.canDrawOverlays(App.getApp())) {
                if (dialog != null && dialog.isShowing()) return;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(
                        "Please jump to system settings and activate window display function.");
                builder.setPositiveButton("jump", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                dialog = builder.create();
                dialog.show();
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
