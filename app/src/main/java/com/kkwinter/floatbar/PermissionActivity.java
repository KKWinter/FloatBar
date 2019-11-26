package com.kkwinter.floatbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.Window;
import android.widget.Toast;

/**
 * Created by jiantao.tu on 2018/9/21.
 */
public class PermissionActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {


    public final static String PERMISSION_KEY = "PERMISSION_KEY";


    public static void launch(int permissionCode) {
        Intent intent = new Intent("com.xing.floatbar.PermissionActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(PERMISSION_KEY, permissionCode);
        App.getApp().startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.transparencyBar(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0f);
        int code = getIntent().getIntExtra(PERMISSION_KEY, -1);
        if (code == -1) return;
        PermissionUtils.requestPermission(this, code, mPermissionGrant, permissionOver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant, permissionOver);
        YeLog.i("PermissionUtils:onRequestPermissionsResult");
    }

    PermissionUtils.PermissionOver permissionOver = new PermissionUtils.PermissionOver() {
        @Override
        public void over() {
            finish();
        }
    };

    public PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_RECORD_AUDIO:
                    Toast.makeText(App.getApp(), "Result Permission Grant CODE_RECORD_AUDIO", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_GET_ACCOUNTS:
                    Toast.makeText(App.getApp(), "Result Permission Grant CODE_GET_ACCOUNTS", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_READ_PHONE_STATE:
                    Toast.makeText(App.getApp(), "Result Permission Grant CODE_READ_PHONE_STATE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_CALL_PHONE:
                    Toast.makeText(App.getApp(), "Result Permission Grant CODE_CALL_PHONE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_CAMERA:
                    Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case PermissionUtils.CODE_ACCESS_FINE_LOCATION:
                    Toast.makeText(App.getApp(), "Result Permission Grant CODE_ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_ACCESS_COARSE_LOCATION:
                    Toast.makeText(App.getApp(), "Result Permission Grant CODE_ACCESS_COARSE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                    Toast.makeText(App.getApp(), "Result Permission Grant CODE_READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
                    intent = new Intent(PermissionActivity.this, ScreenShotActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    break;
                case PermissionUtils.CODE_PERMISSION_WRITE_SECURE_SETTINGS:
                    break;
                default:
                    break;
            }
        }
    };

}
