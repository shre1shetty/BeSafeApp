package com.firstapp.besafeapp;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.Charset;

public class MyWearableListenerSevice extends WearableListenerService {
    private GoogleApiClient mGoogleApiClient;


    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals("/myPath")) {
            String message = new String(messageEvent.getData(), Charset.forName("UTF-8"));
            Log.d("shrevan", "Message received: " + message);
            Intent intent=new Intent(MyWearableListenerSevice.this,AlertMessageActivity.class);
            intent.putExtra("s1",true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
