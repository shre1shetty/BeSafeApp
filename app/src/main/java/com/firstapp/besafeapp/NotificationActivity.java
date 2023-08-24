package com.firstapp.besafeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationActivity extends AppCompatActivity {

    private Button start, stop;
    double currentLat, currentLang;
    String fullName, email, dob, womenChild, ContactNo, lat, lng;
    String textContact1, textContact2, textContact3;
    TextView textLat,textLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        start = findViewById(R.id.startbtn);
        stop = findViewById(R.id.stopbtn);
        currentLat = Home.currentLat;
        currentLang = Home.currentLang;
        String latLang = currentLat + "," + currentLang;
        fullName = Home.fullName;
        email = Home.email;
        dob = Home.dob;
        womenChild = Home.womenChild;
        ContactNo = Home.ContactNo;
        textContact1 = Home.textContact1;
        textContact2 = Home.textContact2;
        textContact3 = Home.textContact3;
        textLat=findViewById(R.id.lat);
        textLng=findViewById(R.id.lng);


        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Code to send the message
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(textContact1, null, "I am in Danger...\nPlease Help Me...\nMy Details" + "\nFullName :-" + fullName + "\nEmail :-" + email + "\nDOB :-" + dob
                        + "\nWomen or Child :-" + womenChild + "\nContact No :-" + ContactNo, null, null);
                smsManager.sendTextMessage(textContact2, null, "I am in Danger...\nPlease Help Me...\nMy Details" + "\nFullName :-" + fullName + "\nEmail :-" + email + "\nDOB :-" + dob
                        + "\nWomen or Child :-" + womenChild + "\nContact No :-" + ContactNo, null, null);
                smsManager.sendTextMessage(textContact3, null, "I am in Danger...\nPlease Help Me...\nMy Details" + "\nFullName :-" + fullName + "\nEmail :-" + email + "\nDOB :-" + dob
                        + "\nWomen or Child :-" + womenChild + "\nContact No :-" + ContactNo, null, null);

                //display toast message
                Toast.makeText(NotificationActivity.this, "Message Sent Successfully", Toast.LENGTH_LONG).show();

                handler.postDelayed(this, 2 * 60 * 1000); // 2 minutes delay
            }
        };

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(runnable, 0); // Start the runnable immediately
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable); // Stop the runnable
                Toast.makeText(NotificationActivity.this, "Stop", Toast.LENGTH_LONG).show();

            }
        });
    }
}

