package com.firstapp.besafeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserProfileActivity extends AppCompatActivity {

    public EditText editTextFullName,editTextEmail,editTextDob,editTextWomenChild,editTextContact;
    public TextView textViewWelcome;
    public ProgressBar progressBar;
    public String fullName,email,dob,womenChild,ContactNo;
    private String textFullName,textDob,textWomenChild,textContactNo;
    private ImageView imageView;
    private FirebaseAuth authProfile;
    private RadioButton updateWomenChildSelected,buttonWomen,buttonChild;
    private RadioGroup radioGroupUpdateWomenChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setTitle("Profile");

        textViewWelcome=findViewById(R.id.textView_show_welcome);
        editTextFullName=findViewById(R.id.editText_show_full_name);
        editTextEmail=findViewById(R.id.editText_show_email);
        editTextDob=findViewById(R.id.editText_show_DOB);
        radioGroupUpdateWomenChild=findViewById(R.id.radio_group_update_womenChild);
        editTextContact=findViewById(R.id.editText_show_CONTACT);
        buttonWomen=findViewById(R.id.radio_women);
        buttonChild=findViewById(R.id.radio_child);
        imageView=findViewById(R.id.imageView_profile_dp);
        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseuser=authProfile.getCurrentUser();

        //Set OnClickListener on ImageView to open UploadProfile Activity
        /*ImageView uploadPic=findViewById(R.id.upload_pic);
        uploadPic.setImageResource(R.drawable.upload_image);
        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserProfileActivity.this,UploadProfilePicActivity.class);
                startActivity(intent);
            }
        });*/

        ImageView editIcon=findViewById(R.id.ic_edit);
        editIcon.setImageResource(R.drawable.ic_edit_default);
        buttonWomen.setClickable(false);
        buttonChild.setClickable(false);
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editIcon.setImageResource(R.drawable.ic_edit_onclick);
                editTextFullName.setFocusableInTouchMode(true);
                editTextEmail.setFocusableInTouchMode(true);
                buttonWomen.setClickable(true);
                buttonChild.setClickable(true);
                editTextContact.setFocusableInTouchMode(true);


                //Setting up DatePicker on editText
                editTextDob.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        //Extract saved dd,m,yyyy into different variables by creating an array delimited by "/"
                        String textSADob[]=dob.split("/");

                        int day=Integer.parseInt(textSADob[0]);
                        int month=Integer.parseInt(textSADob[1])-1; // to take care of month index starting from 0
                        int year=Integer.parseInt(textSADob[2]);
                        DatePickerDialog picker;
                        //Date Picker Dialog
                        picker=new DatePickerDialog(UserProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                editTextDob.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                            }
                        },year,month,day);
                        picker.show();;
                    }
                });
                Button btnUpdateProfile=findViewById(R.id.btnUpdateProfile);
                btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        updateProfile(firebaseuser);
                    }
                });
            }
        });


        if(firebaseuser==null){
            Toast.makeText(UserProfileActivity.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
        }
        else {

            showUserProfile(firebaseuser);

        }
    }


    private void showUserProfile(FirebaseUser firebaseuser)
    {
        String userID=firebaseuser.getUid();

        //Extracting user reference from database for registered users
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
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

                    textViewWelcome.setText("Welcome" + " " + fullName + " " + "!");
                    editTextFullName.setText(fullName);
                    editTextEmail.setText(email);
                    editTextDob.setText(dob);
                    editTextContact.setText(ContactNo);
                    Uri uri=firebaseuser.getPhotoUrl();
                    Picasso.with(UserProfileActivity.this).load(uri).into(imageView);

                    //show gender through radio button
                    if (womenChild.equals("Women")) {
                        updateWomenChildSelected = findViewById(R.id.radio_women);
                    } else {
                        updateWomenChildSelected = findViewById(R.id.radio_child);
                    }
                    updateWomenChildSelected.setChecked(true);
                } else {
                    Toast.makeText(UserProfileActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(UserProfileActivity.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void updateProfile(FirebaseUser firebaseuser)
    {
        int selectedId=radioGroupUpdateWomenChild.getCheckedRadioButtonId();
        updateWomenChildSelected=findViewById(selectedId);

        //Validate mobile number using matcher and pattern
        String contactRegex="[6-9][0-9]{9}"; //First no. can be {6,7,8,9} and rest 9 nos. can be any no.
        Matcher contactMatcher;
        Pattern contactPattern=Pattern.compile(contactRegex);
        contactMatcher=contactPattern.matcher(ContactNo);


        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(UserProfileActivity.this, "Please Enter Full Name", Toast.LENGTH_LONG).show();
            editTextFullName.setError("Full name is required");
            editTextFullName.requestFocus();
        }
        else if (TextUtils.isEmpty(email)) {
            Toast.makeText(UserProfileActivity.this, "Please Enter Your Email", Toast.LENGTH_LONG).show();
            editTextEmail.setError("Email is Required");
            editTextEmail.requestFocus();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(UserProfileActivity.this, "Please re-Enter Your Email", Toast.LENGTH_LONG).show();
            editTextEmail.setError(" Valid Email is Required");
            editTextEmail.requestFocus();
        }
        else if (TextUtils.isEmpty(dob)) {
            Toast.makeText(UserProfileActivity.this, "Please Enter Your Date of Birth", Toast.LENGTH_LONG).show();
            editTextDob.setError("Date of Birth is Required");
            editTextDob.requestFocus();
        }
        else if (TextUtils.isEmpty(updateWomenChildSelected.getText())) {
            Toast.makeText(UserProfileActivity.this, "Please select You are a Women or a Child", Toast.LENGTH_LONG).show();
            updateWomenChildSelected.setError("Selecting Women or Child is Required");
            updateWomenChildSelected.requestFocus();
        }
        else if (TextUtils.isEmpty(ContactNo)) {
            Toast.makeText(UserProfileActivity.this, "Please Enter Your Contact No.", Toast.LENGTH_LONG).show();
            editTextContact.setError("Contact No. is Required");
            editTextContact.requestFocus();
        }
        else if (ContactNo.length() != 10) {
            Toast.makeText(UserProfileActivity.this, "Please re-Enter Your Contact No.", Toast.LENGTH_LONG).show();
            editTextContact.setError("Contact No. should be 10 digits");
            editTextContact.requestFocus();
        }
        else if(!contactMatcher.find())
        {
            Toast.makeText(UserProfileActivity.this, "Please re-Enter Your Contact No.", Toast.LENGTH_LONG).show();
            editTextContact.setError("Contact No. is not valid");
            editTextContact.requestFocus();
        }
        else
        {
            Map<String,Object> map=new HashMap<>();
            map.put("womenChild",updateWomenChildSelected.getText().toString());
            map.put("fullName",editTextFullName.getText().toString());
            map.put("dob",editTextDob.getText().toString());
            map.put("ContactNo",editTextContact.getText().toString());
            String userID=firebaseuser.getUid();
            FirebaseDatabase.getInstance().getReference("Registered Users").child(userID).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(UserProfileActivity.this,"Update Successful",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(UserProfileActivity.this,Home.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserProfileActivity.this,"Update Failed",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}