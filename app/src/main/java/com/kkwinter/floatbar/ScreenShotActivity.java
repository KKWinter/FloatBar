package com.kkwinter.floatbar;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.view.Window;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class ScreenShotActivity extends Activity {

    public static final int REQUEST_MEDIA_PROJECTION = 0x2893;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(android.R.style.Theme_Dialog);//这个在这里设置 之后导致 的问题是 背景很黑
        super.onCreate(savedInstanceState);
        context = this;
        Utils.transparencyBar(this);
        //如下代码 只是想 启动一个透明的Activity 而上一个activity又不被pause
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0f);

        requestScreenShot();

    }


    public void requestScreenShot() {
        WMInstance.getInstance().disableIconView();
        startActivityForResult(createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
    }

    private Intent createScreenCaptureIntent() {
        //这里用media_projection代替Context.MEDIA_PROJECTION_SERVICE 是防止低于21 api编译不过
        return ((MediaProjectionManager) Objects.requireNonNull(getSystemService(Context.MEDIA_PROJECTION_SERVICE))).createScreenCaptureIntent();
    }

    private void toast(String str) {
        Toast.makeText(ScreenShotActivity.this, str, Toast.LENGTH_LONG).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION: {
                if (resultCode == RESULT_OK && data != null) {
                    ShotTer shotter = new ShotTer(ScreenShotActivity.this, resultCode, data);
                    shotter.startScreenShot(new ShotTer.OnShotListener() {
                        @Override
                        public void onFinish(File file) {
                            if (file != null) {
                                sendNotification(file);
                                toast("screenshot saved.");
                            } else {
                                toast("screenshot failed.");
                            }

                            finish(); // don't forget finish activity
                        }
                    });
                } else if (resultCode == RESULT_CANCELED) {
                    toast("shot cancel , please give permission.");
                    finish();
                } else {
                    toast("unknow exceptions!");
                    finish();
                }
            }
        }
    }

    private void sendNotification(File file) {
        final String CHANNEL_ID = "channel_id_1";
        final String CHANNEL_NAME = "channel_name_1";

        NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //只在Android O之上需要渠道
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
            //通知才能正常弹出
            if (mManager != null) {
                mManager.createNotificationChannel(notificationChannel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        YeLog.i("sendNotification:fileUrl=" + file.getAbsolutePath());
        Uri imageUri = FileProvider.getUriForFile(context, "com.xing.floatbar.easytouch.fileprovider", file);//通过FileProvider创建一个content类型的Uri
        YeLog.i("sendNotification:filePath=" + imageUri.getPath());
        intent.setDataAndType(imageUri, "image/*");//试了下上面分开写setData和setType不能实现相同效果
        List<ResolveInfo> resInfoList = context.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setTicker("screenshot...");
        builder.setContentTitle("screenshot");
        builder.setContentText("Picture saved in Album");
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.drawable.screenshot);


        builder.setLargeIcon(BitmapFactory.decodeFile(file.toString()));
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        if (mManager != null) {
            mManager.notify(0, builder.build());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WMInstance.getInstance().showIconView();
    }
}