package com.kkwinter.floatbar.anr;

import android.os.Looper;

class ANRException extends RuntimeException {
    public ANRException() {
        super("The application is not responding. Change it to BUG quickly....");
        Thread mainThread = Looper.getMainLooper().getThread();
        setStackTrace(mainThread.getStackTrace());
    }
}
