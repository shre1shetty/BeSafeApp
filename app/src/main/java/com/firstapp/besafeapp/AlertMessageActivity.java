package com.firstapp.besafeapp;


import androidx.appcompat.app.AppCompatActivity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AlertMessageActivity extends AppCompatActivity {

    Button sendSms;
    public double lat,lang;
    String fullName,email,dob,womenChild,ContactNo;
    String textContact1,textContact2,textContact3;
    public static Handler handler;
    public static Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_message);

        sendSms = findViewById(R.id.sms_alert_button);
        lat=Home.currentLat;
        lang=Home.currentLang;
        fullName=Home.fullName;
        email=Home.email;
        dob=Home.dob;
        womenChild=Home.womenChild;
        ContactNo=Home.ContactNo;
        textContact1=Home.textContact1;
        textContact2=Home.textContact2;
        textContact3=Home.textContact3;

        NotificationManager manager=(NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        Log.d("contact",textContact1+""+textContact2+""+textContact3);
        Log.d("s22",fullName+""+email+""+dob+""+womenChild+""+ContactNo+"\n"+lat+"\n"+lang);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(textContact1, null, "I am in Danger...\nMy Details" + "\nFullName :-" + fullName + "\nDOB :-" + dob
                        + "\nContact No :-" + ContactNo + "\n" + lat + "\n" + lang, null, null);
                smsManager.sendTextMessage(textContact2, null, "I am in Danger...\nMy Details" + "\nFullName :-" + fullName + "\nDOB :-" + dob
                        + "\nContact No :-" + ContactNo + "\n" + lat + "\n" + lang, null, null);
                smsManager.sendTextMessage(textContact3, null, "I am in Danger...\nMy Details" + "\nFullName :-" + fullName + "\nDOB :-" + dob
                        + "\nContact No :-" + ContactNo + "\nLatitude :-" + lat + "\nLongitude :-" + lang, null, null);

                //display toast message
                Toast.makeText(AlertMessageActivity.this, "Message Sent Successfully", Toast.LENGTH_LONG).show();
                handler.postDelayed(this, 2 * 60 * 1000); // 2 minutes delay

            }
        };
        handler.postDelayed(runnable, 0); // Start the runnable immediately
        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(runnable); // Stop the runnable
                Intent intent=new Intent(AlertMessageActivity.this,SafeMessageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                startActivity(intent);
            }
        });
    }

}


