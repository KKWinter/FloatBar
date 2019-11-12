/**
 * Copyright © 2014 All rights reserved.
 *
 * @Title: PowerAdminReceiver.java
 * @Prject: WindowManagerTest
 * @Package: com.example.windowmanagertest
 * @Description: TODO
 * @author: raot  719055805@qq.com
 * @date: 2014年9月12日 上午10:45:04
 * @version: V1.0
 */
package com.kkwinter.floatbar;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;


public class PowerAdminReceiver extends DeviceAdminReceiver {
    // implement onEnabled(), onDisabled(), …
    public void onEnabled(Context context, Intent intent) {
        YeLog.i("PowerAdminReceiver:MyAdmin enabled");
    }

    public void onDisabled(Context context, Intent intent) {
        YeLog.i("PowerAdminReceiver:MyAdmin disabled");
    }
}
