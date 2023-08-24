package com.firstapp.besafeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.Toast;


public class SafeMessageActivity extends AppCompatActivity {

    String textContact1, textContact2, textContact3;
    Handler handler;
    Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_message);
        NotificationManager manager=(NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        textContact1=Home.textContact1;
        textContact2=Home.textContact2;
        textContact3=Home.textContact3;
        handler=AlertMessageActivity.handler;
        runnable=AlertMessageActivity.runnable;


                //initialize SMS manager
                SmsManager smsManager = SmsManager.getDefault();
                //send message
                smsManager.sendTextMessage(textContact1, null, "I am Safe Now...", null, null);
                smsManager.sendTextMessage(textContact2, null, "I am Safe Now...", null, null);
                smsManager.sendTextMessage(textContact3, null, "I am Safe Now...", null, null);

                //display toast message
                Toast.makeText(SafeMessageActivity.this, "Message Sent Successfully", Toast.LENGTH_LONG).show();

                handler.removeCallbacks(runnable); // Stop the runnable


    }
}
