package com.firstapp.besafeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class UploadProfilePicActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView imageViewUploadPic;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseuser;
    private static final int PICK_IMAGE_REQUEST=1;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);

        getSupportActionBar().setTitle("Upload Profile Pic");

        Button buttonUploadPicChoose=findViewById(R.id.upload_Button);
        Button buttonUploadPic=findViewById(R.id.upload_pic_button);
        progressBar=findViewById(R.id.progress3);
        imageViewUploadPic=findViewById(R.id.imageView_Profile_dp);

        authProfile=FirebaseAuth.getInstance();
        firebaseuser=authProfile.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference("DisplayProfilePic");
        Uri uri=firebaseuser.getPhotoUrl();

        //Set User's current DP in imageview(If uploaded already, picasso imageViewer)
        //Regular URI's
        Picasso.with(UploadProfilePicActivity.this).load(uri).into(imageViewUploadPic);
        buttonUploadPicChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();

            }
        });

        buttonUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(view.VISIBLE);
                UploadPic();
            }
        });
    }
    private void openFileChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uriImage=data.getData();
            imageViewUploadPic.setImageURI(uriImage);

        }

    }

    private void UploadPic(){
        if(uriImage!=null){
            //set the image with uid of the currently logged User
            StorageReference fileReference=storageReference.child(authProfile.getCurrentUser().getUid() + "."
                    + getFileExtension(uriImage));
            //upload image to storage
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri=uri;
                            firebaseuser=authProfile.getCurrentUser();
                            //finally set the display image of the user after upload
                            UserProfileChangeRequest profileUpdates=new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                            firebaseuser.updateProfile(profileUpdates);
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadProfilePicActivity.this,"Upload Successfully",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(UploadProfilePicActivity.this,UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadProfilePicActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(UploadProfilePicActivity.this,"No File Selected!",Toast.LENGTH_LONG).show();

        }
    }
    //obtain File Extension
    private String getFileExtension(Uri uri){
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}