package com.kkwinter.floatbar;

import android.os.Looper;

/**
 * Created by jiantao.tu on 2018/3/26.
 */

class ANRException extends RuntimeException {
    public ANRException() {
        super("The application is not responding. Change it to BUG quickly....");
//        ILog.error(4);
        Thread mainThread = Looper.getMainLooper().getThread();
        setStackTrace(mainThread.getStackTrace());
    }
}
