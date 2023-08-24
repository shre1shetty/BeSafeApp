package com.firstapp.besafeapp;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.firstapp.besafeapp.databinding.ActivityMainBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.Charset;

public class MainActivity extends Activity{
    private GoogleApiClient mGoogleApiClient;
    private TextView mTextView;
    private ActivityMainBinding binding;
    private SensorManager mSensorManager;
    private Sensor mHeartSensor;
    private TextView si;
    private TextView ss;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();


        mTextView = binding.text;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mHeartSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        si=findViewById(R.id.text);

        ss=findViewById(R.id.shrevan);
        Log.d("s2","svhsb");
        mSensorManager.registerListener(mSensorEventListener, mHeartSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }
    private SensorEventListener mSensorEventListener = new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Log.d("s1","shrevan");
            final SensorEvent event1=sensorEvent;
            String s2=Float.toString(event1.values[0]);

            si.setText(Float.toString(event1.values[0]));
            Float i=event1.values[0];
            if (i>120){
                count=count+1;
                if(count>5)
                {
                    ss.setText("danger");
                    String text="shrevan";
                    Wearable.getMessageClient(MainActivity.this).sendMessage(
                            "TSHIVWWGDI3R8FQI7", "/myPath",text.getBytes(Charset.forName("UTF-8"))).addOnCompleteListener(
                            new OnCompleteListener<Integer>() {
                                @Override
                                public void onComplete(@NonNull Task<Integer> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("sssk", "Message sent successfully");
                                    } else {
                                        Log.d("ssak", "Message failed to send");
                                    }
                                }
                            }

                    );


                }
            }
            else
            {
                ss.setText("");
                count=0;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


}