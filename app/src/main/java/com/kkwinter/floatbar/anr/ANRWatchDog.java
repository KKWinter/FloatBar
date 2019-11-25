package com.kkwinter.floatbar.anr;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 判定发生了ANR的时间，必须要小于5秒，否则等弹出ANR，可能就被用户立即杀死了。
 */
public class ANRWatchDog extends Thread {

    private static final int MESSAGE_WATCHDOG_TIME_TICK = 0;

    private static final int ACTIVITY_ANR_TIMEOUT = 1000;

    private static final int ANR_TIME = 5000;

    private static int timeTick = 0;


    //主线程堵塞的时候，handler这里也不会执行
    private static Handler watchDogHandler = new android.os.Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            synchronized (ANRWatchDog.class) {
                timeTick = 0;
            }
        }
    };

    @Override
    public void run() {
        while (true) {
            watchDogHandler.sendEmptyMessage(MESSAGE_WATCHDOG_TIME_TICK);

            try {
                Thread.sleep(ACTIVITY_ANR_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (ANRWatchDog.class) {
                timeTick += ACTIVITY_ANR_TIMEOUT;
                //如果发生了ANR，handler中没有处理消息，timeTick大于5秒就判定发生了ANR
                if (timeTick >= ANR_TIME) {
                    throw new ANRException();
                }
            }
        }
    }
}