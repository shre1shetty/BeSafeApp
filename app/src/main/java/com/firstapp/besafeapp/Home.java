package com.firstapp.besafeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private NotificationManager manager;
    //Initialize variables
    Spinner spType;
    Button btFind;
    SupportMapFragment supportMapFragment;
    GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static double currentLat=0,currentLang=0;
    MarkerOptions markerOptions1,markerOptions2;
    private Intent serviceIntent;
    public static String fullName,email,dob,womenChild,ContactNo;
    public static String textContact1,textContact2,textContact3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        spType = findViewById(R.id.sp_type);
        btFind = findViewById(R.id.btnFind);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        serviceIntent=new Intent(this,MyService.class);
        startService(serviceIntent);


        //Initialize Array of place type
        String[] placeTypeList = {"atm", "bank", "hospital", "movie_theater", "restaurant"};
        //Initialize array of place name
        String[] placeNameList = {"ATM", "Bank", "Hospital", "Movie Theater", "Restaurant"};

        spType.setAdapter(new ArrayAdapter<>(Home.this, android.R.layout.simple_spinner_dropdown_item, placeNameList));

        //Initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Check permission
        if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //when permission is granted
            //call method
            getCurrentLocation();
        }
        else{
            ActivityCompat.requestPermissions(Home.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get Selected Position of spinner
                int i=spType.getSelectedItemPosition();
                String url="https://maps.googleapis.com/maps/api/place/nearbysearch/json"+
                        "?location="+currentLat+","+currentLang+
                        "&radius=1000"+//
                        "&types="+placeTypeList[i]+
                        "&sensor=true"+
                        "&key=AIzaSyDrMvp_rKiu56TkaGl807_fS596ZsoZDwA";

                new PlaceTask().execute(url);


            }
        });

        //Open HelpLine Numbers Activity
        Button buttonHelpLineNo=findViewById(R.id.button_helpLine);
        buttonHelpLineNo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(Home.this, HelpLineNumbersActivity.class);
                startActivity(intent);
            }
        });

        authProfile=FirebaseAuth.getInstance();
    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    currentLat=location.getLatitude();
                    currentLang=location.getLongitude();
                    //ssss
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap=googleMap;
                            LatLng latLng=new LatLng(currentLat,currentLang);
                            markerOptions1=new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(latLng).title("Current Location");
                            mMap.addMarker(markerOptions1);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat,currentLang),15));
                        }
                    });
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==44){
            if(grantResults.length> 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }

    private class PlaceTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            String data=null;
            try {
                data=downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {
        URL url=new URL(string);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream stream=connection.getInputStream();
        BufferedReader reader=new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder=new StringBuilder();
        String line="";
        while((line=reader.readLine())!=null){
            builder.append(line);
        }
        String data=builder.toString();
        reader.close();
        return data;
    }

    private class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List<HashMap<String,String>> mapList=null;
            JSONObject object=null;
            try {
                object=new JSONObject(strings[0]);
                mapList=jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            mMap.clear();
            LatLng currentlatLng=new LatLng(currentLat,currentLang);
            markerOptions1=new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(currentlatLng).title("Current Location");
            mMap.addMarker(markerOptions1);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat,currentLang),15));
            for (int i=0;i<hashMaps.size();i++){
                HashMap<String,String> hashMapList=hashMaps.get(i);
                double lat= Double.parseDouble(hashMapList.get("lat"));
                double lng=Double.parseDouble(hashMapList.get("lng"));
                String name=hashMapList.get("name");
                LatLng latLng=new LatLng(lat,lng);
                markerOptions2=new MarkerOptions().position(latLng).title(name);
                mMap.addMarker(markerOptions2);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),15));

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serviceIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent1 = new Intent(Home.this, SafeMessageActivity.class);

        intent1.putExtra("Safe", true);

        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent1 = PendingIntent.getActivity(Home.this, 0, intent1, PendingIntent.FLAG_ONE_SHOT);

        Intent intent2 = new Intent(Home.this, AlertMessageActivity.class);

        intent2.putExtra("Help", false);

        intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent2 = PendingIntent.getActivity(Home.this, 1, intent2, PendingIntent.FLAG_ONE_SHOT);

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, App.CHANNEL_ONE_ID);
        notification.setSmallIcon(R.drawable.security)
                .setContentTitle("BeSafe App")
                .setContentTitle(" ")
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        notification.setOngoing(true);

        notification.addAction(R.drawable.ic_launcher_foreground, "Safe", pendingIntent1);

        notification.addAction(R.drawable.ic_launcher_foreground, "Help", pendingIntent2);
        manager.notify(1, notification.build());

        if (authProfile.getCurrentUser() != null) {
            final String[] childU = new String[1];
            FirebaseUser firebaseuser = authProfile.getCurrentUser();
            String userID = firebaseuser.getUid();
            //Extracting user reference from db for registered user
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

            referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                    if (readUserDetails != null) {
                        fullName = readUserDetails.fullName;
                        email = firebaseuser.getEmail();
                        dob = readUserDetails.dob;
                        womenChild = readUserDetails.womenChild;
                        ContactNo = readUserDetails.ContactNo;
                        Log.d("ssv", dob);
                    } else {
                        Toast.makeText(Home.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            referenceProfile.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot locationSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot bumpSnapshot : locationSnapshot.getChildren()) {
                            WriteContactDetails writeUserDetails = locationSnapshot.getValue(WriteContactDetails.class);
                            childU[0] = locationSnapshot.getKey();
                            if (writeUserDetails != null) {
                                textContact1 = writeUserDetails.Contact1;
                                textContact2 = writeUserDetails.Contact2;
                                textContact3 = writeUserDetails.Contact3;
                                Log.d("s23", textContact1 + "" + textContact2 + "" + textContact3);
                            } else {
                                Toast.makeText(Home.this, "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Home.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //Creating action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu items
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //when any item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.menu_edit_profile){
            Intent intent=new Intent(Home.this, UserProfileActivity.class);
            startActivity(intent);
        }
        else if (id==R.id.menu_reset_contacts){
            Intent intent=new Intent(Home.this,ResetContactActivity.class);
            startActivity(intent);
        }
       else if(id==R.id.upload_pic){
            Intent intent=new Intent(Home.this,UploadProfilePicActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.currentLocation){
            Intent intent=new Intent(Home.this,NotificationActivity.class);
            startActivity(intent);
        }

        else if(id==R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(Home.this,"Logged out",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(Home.this,MainActivity.class);

            //Clear stack to prevent user coming back to Home on pressing back button after logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }
        else{
            Toast.makeText(Home.this,"Something went wrong",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

}

