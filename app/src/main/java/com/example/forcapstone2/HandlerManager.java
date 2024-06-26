// HandlerManager.java
package com.example.forcapstone2;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

public class HandlerManager {
    private MyApp myApp;
    private Handler handler;

    public HandlerManager(MyApp myApp) {
        this.myApp = myApp;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 핸들러 로직
            }
        };
    }

    public Handler getHandler() {
        return handler;
    }
}
