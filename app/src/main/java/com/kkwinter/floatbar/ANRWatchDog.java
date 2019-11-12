package com.kkwinter.floatbar;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by jiantao.tu on 2018/3/26.
 */

public class ANRWatchDog extends Thread {

    private static final int MESSAGE_WATCHDOG_TIME_TICK = 0;
    /**
     * 判定Activity发生了ANR的时间，必须要小于5秒，否则等弹出ANR，可能就被用户立即杀死了。
     */
    private static final int ACTIVITY_ANR_TIMEOUT = 1000;

    private static final int ANR_TIME=5000;


    private static int timeTick = 0;


    private static Handler watchDogHandler = new android.os.Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            synchronized (ANRWatchDog.class){
                timeTick=0;
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
            synchronized (ANRWatchDog.class){
                timeTick+=ACTIVITY_ANR_TIMEOUT;
                //如果相等，说明过了ACTIVITY_ANR_TIMEOUT的时间后watchDogHandler仍没有处理消息，已经ANR了
                if (timeTick >= ANR_TIME) {
                    throw new ANRException();
                }
            }
        }
    }
}