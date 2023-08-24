package com.firstapp.besafeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResetContactActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private String userOldCon1,userOldCon2,userOldCon3,userNewCon1,userNewCon2,userNewCon3;
    private Button buttonUpdate;
    private EditText editTextCon1,editTextCon2,editTextCon3;
    private TextView textView1,textView2,textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_contact);

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        getSupportActionBar().setTitle("Reset Contacts");
        progressBar=findViewById(R.id.progressbar);
        editTextCon1=findViewById(R.id.EditText_reset_contact_new);
        editTextCon2=findViewById(R.id.EditText_reset_contact_new2);
        editTextCon3=findViewById(R.id.EditText_reset_contact_new3);
        buttonUpdate=findViewById(R.id.button_authenticate);
        textView1=findViewById(R.id.textView_reset_contact_old);
        textView2=findViewById(R.id.textView_reset_contact_old2);
        textView3=findViewById(R.id.textView_reset_contact_old_3);
        final String[] childU = new String[1];
        String userId=firebaseUser.getUid();
        //Extracting user reference from db for registered user
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference().child("Registered Users");
        referenceProfile.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot locationSnapshot: snapshot.getChildren()) {
                    for (DataSnapshot bumpSnapshot : locationSnapshot.getChildren()) {
                        WriteContactDetails writeUserDetails = locationSnapshot.getValue(WriteContactDetails.class);
                        childU[0] =locationSnapshot.getKey();
                        if (writeUserDetails != null) {
                            userOldCon1 = writeUserDetails.Contact1;
                            userOldCon2 = writeUserDetails.Contact2;
                            userOldCon3 = writeUserDetails.Contact3;

                            textView1.setText(userOldCon1);
                            textView2.setText(userOldCon2);
                            textView3.setText(userOldCon3);

                        }
                        else {
                            Toast.makeText(ResetContactActivity.this,"Error",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ResetContactActivity.this,"Something wet wrong",Toast.LENGTH_LONG).show();
            }
        });
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userNewCon1=editTextCon1.getText().toString();
                userNewCon2=editTextCon2.getText().toString();
                userNewCon3=editTextCon3.getText().toString();
                progressBar.setVisibility(View.VISIBLE);


                if (userNewCon1.equals(userOldCon1)) {
                    Toast.makeText(ResetContactActivity.this, "Please Enter Different Number", Toast.LENGTH_LONG).show();
                    editTextCon1.setError("Number Confirmation is Required");
                    editTextCon1.requestFocus();
                    //Clear the entered Number
                    editTextCon1.clearComposingText();
                }
                else if (userNewCon2.equals(userOldCon2)) {
                    Toast.makeText(ResetContactActivity.this, "Please Enter Different Number", Toast.LENGTH_LONG).show();
                    editTextCon2.setError("Number Confirmation is Required");
                    editTextCon2.requestFocus();
                    //Clear the entered Number
                    editTextCon2.clearComposingText();
                }
                else if (userNewCon3.equals(userOldCon3)) {
                    Toast.makeText(ResetContactActivity.this, "Please Enter Different Number", Toast.LENGTH_LONG).show();
                    editTextCon3.setError("Number Confirmation is Required");
                    editTextCon3.requestFocus();
                    //Clear the entered Number
                    editTextCon3.clearComposingText();
                }
                else {
                    WriteContactDetails changeUserDetails=new WriteContactDetails(userNewCon1,userNewCon2,userNewCon3);

                    //DatabaseReference referenceProfile=FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(userId).child(childU[0]).setValue(changeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ResetContactActivity.this,"Update Successful",Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(ResetContactActivity.this,Home.class);
                                startActivity(intent);
                                finish();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

}

