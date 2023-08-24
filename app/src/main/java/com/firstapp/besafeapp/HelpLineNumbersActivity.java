package com.firstapp.besafeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HelpLineNumbersActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    public String national,state;
    // creating variables for our list view.
    private ListView helpLineName,helpLineNo;

    // creating a new array list.
    ArrayList<String> helpNameArrayList;
    ArrayList<String> helpNoArrayList;

    // creating a variable for database reference.
    DatabaseReference reference;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_line_numbers);
        getSupportActionBar().setTitle("HelpLine Numbers");

        // initializing variables for listviews.
        helpLineName = findViewById(R.id.listView_helpLine);
        helpLineNo=findViewById(R.id.listView_helpLine_no);
        helpLineName.setClickable(true);
        helpLineNo.setClickable(true);
        helpLineName.setEnabled(false);
        // initializing our array list
        helpNameArrayList = new ArrayList<String>();
        helpNoArrayList=new ArrayList<String>();


        // calling a method to get data from
        // Firebase and set data to list view
        initializeListView();

    }

    private void initializeListView() {
        // creating a new array adapter for our list view.
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, helpNameArrayList);
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, helpNoArrayList);
        // Get the layout parameters of the first ListView
        ViewGroup.LayoutParams layoutParams = helpLineName.getLayoutParams();
        // Set the height of the second ListView to be the same as the first ListView
        helpLineNo.setLayoutParams(layoutParams);
        // below line is used for getting reference
        // of our Firebase Database.
        reference = FirebaseDatabase.getInstance().getReference("HelpLine Numbers");

        // in below line we are calling method for add child event
        // listener to get the child of our database.
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Long g=snapshot.getValue(Long.class);
                helpNameArrayList.add(snapshot.getKey());
                helpNoArrayList.add(String.valueOf(g));
                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();

                helpLineNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String value = (String)parent.getItemAtPosition(position);
                        Intent intent=new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+value));
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // below line is used for setting
        // an adapter to our list view.
        helpLineName.setAdapter(adapter1);
        helpLineNo.setAdapter(adapter2);
    }
}





