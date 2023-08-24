package com.firstapp.besafeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextLoginEmail,editTextLoginPassword;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG="LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");

        editTextLoginEmail=findViewById(R.id.editText_login_email);
        editTextLoginPassword=findViewById(R.id.editText_login_password);
        progressBar=findViewById(R.id.progressBar);

        authProfile=FirebaseAuth.getInstance();

        //Show Hide Password using eye icon
        ImageView imageViewShowHidePwd=findViewById(R.id.Imageview_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(editTextLoginPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If password is visible then hide it
                    editTextLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }
                else
                {
                    editTextLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        //Login User
        Button buttonLogin=findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String textEmail=editTextLoginEmail.getText().toString();
                String textPassword=editTextLoginPassword.getText().toString();

                if(TextUtils.isEmpty(textEmail))
                {
                    Toast.makeText(LoginActivity.this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is Required");
                    editTextLoginEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches())
                {
                    Toast.makeText(LoginActivity.this, "Please re-Enter Your Email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Valid Email is Required");
                    editTextLoginEmail.requestFocus();
                }
                else if(TextUtils.isEmpty(textPassword))
                {
                    Toast.makeText(LoginActivity.this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
                    editTextLoginPassword.setError("Password is Required");
                    editTextLoginPassword.requestFocus();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPassword);
                }
            }
        });
    }

    private void loginUser(String Email, String Password)
    {
        authProfile.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, "You are Logged in now", Toast.LENGTH_SHORT).show();

                    //Get instance of the current user
                    FirebaseUser firebaseUser=authProfile.getCurrentUser();
                    //Start the SelectingContactActivity
                    startActivity(new Intent(LoginActivity.this,Home.class));
                    finish(); //Close Login Activity
                    //Check if email is verified before user can access their profile
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT).show();
                        //Open User Profile
                    }
                    else{
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut(); //sign out user
                        //showAlertDialog();
                    }
                }
                else
                {
                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        editTextLoginEmail.setError("User does not exist or is no longer valid.Please register again");
                        editTextLoginEmail.requestFocus();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        editTextLoginEmail.setError("Invalid Email or Password.Kindly Check and re-enter");
                        editTextLoginEmail.requestFocus();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        if(authProfile.getCurrentUser()!=null){
            Toast.makeText(LoginActivity.this, "Already Logged In!", Toast.LENGTH_SHORT).show();

            //Start the Home Activity
            startActivity(new Intent(LoginActivity.this,Home.class));
            finish(); //Close Login Activity
        }
        else
        {
            Toast.makeText(LoginActivity.this, "You can Login now!", Toast.LENGTH_SHORT).show();
        }
    }

}

