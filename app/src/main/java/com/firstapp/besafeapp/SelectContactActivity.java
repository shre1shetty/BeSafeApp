package com.firstapp.besafeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectContactActivity extends AppCompatActivity {

    private static final int RESULT_PICK_CONTACT=1;
    private EditText editTextContact1, editTextContact2, editTextContact3;
    private ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        getSupportActionBar().setTitle("Select 3 Contacts");

        progressBar = findViewById(R.id.progressBar);
        editTextContact1 = findViewById(R.id.editText_contact_1);
        editTextContact2 = findViewById(R.id.editText_contact_2);
        editTextContact3 = findViewById(R.id.editText_contact_3);

        //selectContact User
        Button buttonSelectContact = findViewById(R.id.button_select_contact);
        buttonSelectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textContact1 = editTextContact1.getText().toString();
                String textContact2 = editTextContact2.getText().toString();
                String textContact3 = editTextContact3.getText().toString();

                //Validate mobile number using matcher and pattern
                String contactRegex = "[6-9][0-9]{9}"; //First no. can be {6,7,8,9} and rest 9 nos. can be any no.
                Matcher contactMatcher1;
                Matcher contactMatcher2;
                Matcher contactMatcher3;
                Pattern contactPattern = Pattern.compile(contactRegex);
                contactMatcher1 = contactPattern.matcher(textContact1);
                contactMatcher2 = contactPattern.matcher(textContact2);
                contactMatcher3 = contactPattern.matcher(textContact3);

                if (TextUtils.isEmpty(textContact1)) {
                    Toast.makeText(SelectContactActivity.this, "Please Enter Your Contact No.", Toast.LENGTH_LONG).show();
                    editTextContact1.setError("Contact No. is Required");
                    editTextContact1.requestFocus();
                } else if (TextUtils.isEmpty(textContact2)) {
                    Toast.makeText(SelectContactActivity.this, "Please Enter Your Contact No.", Toast.LENGTH_LONG).show();
                    editTextContact2.setError("Contact No. is Required");
                    editTextContact2.requestFocus();
                } else if (TextUtils.isEmpty(textContact3)) {
                    Toast.makeText(SelectContactActivity.this, "Please Enter Your Contact No.", Toast.LENGTH_LONG).show();
                    editTextContact3.setError("Contact No. is Required");
                    editTextContact3.requestFocus();
                } else if (textContact1.length() != 10) {
                    Toast.makeText(SelectContactActivity.this, "Please re-Enter Your Contact No.", Toast.LENGTH_LONG).show();
                    editTextContact1.setError("Contact No. should be 10 digits");
                    editTextContact1.requestFocus();
                } else if (textContact1.length() != 10) {
                    Toast.makeText(SelectContactActivity.this, "Please re-Enter Your Contact No.", Toast.LENGTH_LONG).show();
                    editTextContact2.setError("Contact No. should be 10 digits");
                    editTextContact2.requestFocus();
                } else if (textContact1.length() != 10) {
                    Toast.makeText(SelectContactActivity.this, "Please re-Enter Your Contact No.", Toast.LENGTH_LONG).show();
                    editTextContact3.setError("Contact No. should be 10 digits");
                    editTextContact3.requestFocus();
                } else if (!contactMatcher1.find()) {
                    Toast.makeText(SelectContactActivity.this, "Please re-Enter Your Contact No.", Toast.LENGTH_LONG).show();
                    editTextContact1.setError("Contact No. is not valid");
                    editTextContact1.requestFocus();
                } else if (!contactMatcher2.find()) {
                    Toast.makeText(SelectContactActivity.this, "Please re-Enter Your Contact No.", Toast.LENGTH_LONG).show();
                    editTextContact2.setError("Contact No. is not valid");
                    editTextContact2.requestFocus();
                } else if (!contactMatcher3.find()) {
                    Toast.makeText(SelectContactActivity.this, "Please re-Enter Your Contact No.", Toast.LENGTH_LONG).show();
                    editTextContact3.setError("Contact No. is not valid");
                    editTextContact3.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    SelectContact(textContact1, textContact2, textContact3);
                }
                // Intent intent=new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                //startActivityForResult(intent,RESULT_PICK_CONTACT);
            }
        });
    }
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==RESULT_OK)
        {
            switch (requestCode) {
                case RESULT_PICK_CONTACT;
                    contactPicked(data);
                    break;
            }
        }
        else
        {
            Toast.makeText(this,"Failed to pick contact",Toast.LENGTH_LONG).show();
        }*/


    private void SelectContact(String textContact1, String textContact2, String textContact3) {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser firebaseuser=auth.getCurrentUser();
        //Enter user data into the firebase realtime database
        WriteContactDetails writeUserDetails=new WriteContactDetails(textContact1,textContact2,textContact3);

        //Extracting user reference from database for registered users
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(firebaseuser.getUid()).push().setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent=new Intent(SelectContactActivity.this,Home.class);
                    startActivity(intent);
                    finish();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}

