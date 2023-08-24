package com.firstapp.besafeapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

public class Alert extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.media.VOLUME_DOWN")) {
            // send message here
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+919082127051", null,"I am Safe Now...", null, null);
            Toast.makeText(context, "Volume down key pressed", Toast.LENGTH_SHORT).show();
        }
    }
}