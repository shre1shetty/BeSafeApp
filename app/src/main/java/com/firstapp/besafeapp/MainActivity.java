package com.firstapp.besafeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set the title
        getSupportActionBar().setTitle("BeSafe App");
        authProfile=FirebaseAuth.getInstance();

        //Open login activity
        Button buttonLogin=findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        //Open Register Activity
        TextView textRegister=findViewById(R.id.textview_register_link);
        textRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

    }



    @Override
    protected void onStart(){
        super.onStart();
        if(authProfile.getCurrentUser()!=null){
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .build();
            Toast.makeText(MainActivity.this, "Already Logged In!", Toast.LENGTH_SHORT).show();
            startService(new Intent(this,SERVICEv.class));
            //Start the Home Activity
            startActivity(new Intent(MainActivity.this,Home.class));
            finish(); //Close MainActivity
        }
        else
        {
            Toast.makeText(MainActivity.this, "You can Login now!", Toast.LENGTH_SHORT).show();
        }
    }
}



