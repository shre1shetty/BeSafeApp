package com.firstapp.besafeapp;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyService extends Service {

    private Alert alert;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_DOWN");

        alert = new Alert();
        registerReceiver(alert, filter);

        return super.onStartCommand(intent, flags, startId);
           }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alert);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}