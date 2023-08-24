package com.firstapp.besafeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName,editTextRegisterEmail,editTextRegisterDob,editTextRegisterContactNo,editTextRegisterPassword,editTextRegisterConfirmPassword;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegister;
    private RadioButton radioButtonRegisterSelected;
    private DatePickerDialog picker;
    private static  final String TAG="RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Sign Up");

        Toast.makeText(RegisterActivity.this,"You can Sign Up now",Toast.LENGTH_LONG).show();

        progressBar=findViewById(R.id.progressbar);
        editTextRegisterFullName=findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail=findViewById(R.id.editText_register_email);
        editTextRegisterDob=findViewById(R.id.editText_register_dob);
        editTextRegisterContactNo=findViewById(R.id.editText_register_contact_no);
        editTextRegisterPassword=findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPassword=findViewById(R.id.editText_register_confirm_password);

        //radio button for women or child
        radioGroupRegister=findViewById(R.id.radio_group_women_child);
        radioGroupRegister.clearCheck();

        //Setting up DatePicker on editText
        editTextRegisterDob.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Calendar calender=Calendar.getInstance();
                int day=calender.get(Calendar.DAY_OF_MONTH);
                int month=calender.get(Calendar.MONTH);
                int year=calender.get(Calendar.YEAR);

                //Date Picker Dialog
                picker=new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDob.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();;
            }
        });

        TextView textRegister = findViewById(R.id.button_register);
        textRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void  onClick(View v) {
                int selectedId = radioGroupRegister.getCheckedRadioButtonId();
                radioButtonRegisterSelected = findViewById(selectedId);

                //Obtain the entered data
                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDob = editTextRegisterDob.getText().toString();
                String textContactNo = editTextRegisterContactNo.getText().toString();
                String textPassword = editTextRegisterPassword.getText().toString();
                String textConfirmPassword = editTextRegisterConfirmPassword.getText().toString();
                String textWomenChild; //Cant obtain the value before verifying if any button was selected or not

                //Validate mobile number using matcher and pattern
                String contactRegex="[6-9][0-9]{9}"; //First no. can be {6,7,8,9} and rest 9 nos. can be any no.
                Matcher contactMatcher;
                Pattern contactPattern=Pattern.compile(contactRegex);
                contactMatcher=contactPattern.matcher(textContactNo);


                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Full Name", Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                }
                else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is Required");
                    editTextRegisterEmail.requestFocus();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-Enter Your Email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError(" Valid Email is Required");
                    editTextRegisterEmail.requestFocus();
                }
                else if (TextUtils.isEmpty(textDob)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Date of Birth", Toast.LENGTH_LONG).show();
                    editTextRegisterDob.setError("Date of Birth is Required");
                    editTextRegisterDob.requestFocus();
                }
                else if (radioGroupRegister.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Please select You are a Women or a Child", Toast.LENGTH_LONG).show();
                    radioButtonRegisterSelected.setError("Selecting Women or Child is Required");
                    radioButtonRegisterSelected.requestFocus();
                }
                else if (TextUtils.isEmpty(textContactNo)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Contact No.", Toast.LENGTH_LONG).show();
                    editTextRegisterContactNo.setError("Contact No. is Required");
                    editTextRegisterContactNo.requestFocus();
                }
                else if (textContactNo.length() != 10) {
                    Toast.makeText(RegisterActivity.this, "Please re-Enter Your Contact No.", Toast.LENGTH_LONG).show();
                    editTextRegisterContactNo.setError("Contact No. should be 10 digits");
                    editTextRegisterContactNo.requestFocus();
                }
                else if(!contactMatcher.find())
                {
                    Toast.makeText(RegisterActivity.this, "Please re-Enter Your Contact No.", Toast.LENGTH_LONG).show();
                    editTextRegisterContactNo.setError("Contact No. is not valid");
                    editTextRegisterContactNo.requestFocus();
                }
                else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Password", Toast.LENGTH_LONG).show();
                    editTextRegisterPassword.setError("Password is Required");
                    editTextRegisterPassword.requestFocus();
                }
                else if (textPassword.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                    editTextRegisterPassword.setError("Password too weak");
                    editTextRegisterPassword.requestFocus();
                }
                else if (TextUtils.isEmpty(textConfirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please Confirm Your Password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPassword.setError("Password Confirmation is Required");
                    editTextRegisterConfirmPassword.requestFocus();
                }
                else if (!textPassword.equals(textConfirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Same Password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPassword.setError("Password Confirmation is Required");
                    editTextRegisterConfirmPassword.requestFocus();
                    //Clear the entered Passwords
                    editTextRegisterPassword.clearComposingText();
                    editTextRegisterConfirmPassword.clearComposingText();
                }
                else {
                    textWomenChild = radioButtonRegisterSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDob, textWomenChild, textContactNo, textPassword);
                }
            }
        });
    }

    private void registerUser(String textFullName, String textEmail, String textDob, String textWomenChild, String textContactNo, String textPassword) {
        FirebaseAuth auth=FirebaseAuth.getInstance();

        //Create User Profile
        auth.createUserWithEmailAndPassword(textEmail,textPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)  {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this,"User Registered Successfully",Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseuser=auth.getCurrentUser();

                    //Update display name of user
                    /*UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseuser.updateProfile(profileChangeRequest);*/


                    //Enter user data into the firebase realtime database
                    ReadWriteUserDetails writeUserDetails=new ReadWriteUserDetails(textFullName,textDob,textWomenChild,textContactNo);

                    //Extracting user reference from database for registered users
                    DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseuser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //send verification email
                                firebaseuser.sendEmailVerification();

                                Toast.makeText(RegisterActivity.this, "Now Select 3 Contacts", Toast.LENGTH_LONG).show();

                                //Open user profile after successful registration
                                Intent intent = new Intent(RegisterActivity.this, SelectContactActivity.class);
                                //To Prevent user from returning back to register activity on pressing back button after registration
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); //to close Register activity
                            } else {
                                Toast.makeText(RegisterActivity.this, "User Registration Failed. Please try again", Toast.LENGTH_LONG).show();
                            }
                            //Hide progressbar whether user creation is successful or failed
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                else
                {
                    try
                    {
                        throw task.getException();
                    }
                    catch(FirebaseAuthWeakPasswordException e)
                    {
                        editTextRegisterPassword.setError("Your password is too weak. Kindly use a mix of alphabets,numbers and special characters");
                        editTextRegisterPassword.requestFocus();
                    }
                    catch(FirebaseAuthInvalidCredentialsException e)
                    {
                        editTextRegisterEmail.setError("Your Email is invalid or already in use. Kindly re-enter.");
                        editTextRegisterEmail.requestFocus();
                    }
                    catch(FirebaseAuthUserCollisionException e)
                    {
                        editTextRegisterEmail.setError("User is already registered with this email.Use another email.");
                        editTextRegisterEmail.requestFocus();
                    }catch(Exception e)
                    {
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                    //Hide progressbar whether user creation is successful or failed
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}


