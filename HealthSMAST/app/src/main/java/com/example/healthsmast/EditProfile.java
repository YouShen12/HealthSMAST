package com.example.healthsmast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

public class EditProfile extends AppCompatActivity{

    // view for image view
    ImageView imgEdit, btnChoose;
    // Uri indicates, where the image will be picked from
    Uri filePath;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    // instance for firebase authentication
    FirebaseAuth mAuth;

    DatabaseReference HPDatabase;
    TextInputEditText Username;
    TextInputEditText Byear;
    TextInputEditText Allergy;
    TextInputEditText Health;
    TextInputEditText Medication;
    Button btnSave;
    String uniqueKey;
    int currentYear = Calendar.getInstance().get(Calendar.YEAR); //get current year

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // get the Firebase authentication
        mAuth = FirebaseAuth.getInstance();
        // get the Firebase storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // initialise views
        btnChoose = (ImageView)findViewById(R.id.choosePic);
        imgEdit = (ImageView)findViewById(R.id.viewPic);
        Username = (TextInputEditText)findViewById(R.id.Rusername);
        Byear = (TextInputEditText)findViewById(R.id.Rbyear);
        Allergy = (TextInputEditText)findViewById(R.id.Rallergy);
        Health = (TextInputEditText)findViewById(R.id.Rhealth);
        Medication = (TextInputEditText)findViewById(R.id.Rmed);

        // defining the child of storageReference
        StorageReference profileRef = storageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>(){
                    @Override
                    public void onSuccess(@NonNull Uri uri) {
                        Picasso.get().load(uri).into(imgEdit);
                    }
                });

        //initialize database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        HPDatabase = FirebaseDatabase.getInstance("https://healthsmast-12e4e-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("User");
        if(user != null){
            // get email
            String email = user.getEmail();
            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();
            //retrieve data
            Query locateData = HPDatabase.orderByChild("User_Email").equalTo(email);
            locateData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        System.out.println(dataSnapshot1.child("User_Allergy").getValue(String.class));
                        System.out.println(dataSnapshot1.child("User_Email").getValue(String.class));
                        uniqueKey = dataSnapshot1.getKey();
                        Allergy.setText(dataSnapshot1.child("User_Allergy").getValue(String.class));
                        Username.setText(dataSnapshot1.child("User_Name").getValue(String.class));
                        Byear.setText(dataSnapshot1.child("User_Birth_Year").getValue(String.class));
                        Health.setText(dataSnapshot1.child("User_Condition").getValue(String.class));
                        Medication.setText(dataSnapshot1.child("User_Medication").getValue(String.class));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            // on pressing btnChoose SelectImage() is called
            btnChoose.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    SelectImage();
                }
            });
            // on pressing btnSave update() is called (image and data tgt)
            btnSave = (Button)findViewById(R.id.Rupdate); // save as btnSave
            btnSave.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                    if(TextUtils.isEmpty(Username.getText().toString())){
                        Toast.makeText(EditProfile.this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
                    }
                    else if((currentYear - Integer.parseInt(Byear.getText().toString()) < 1)||(currentYear - Integer.parseInt(Byear.getText().toString()) > 125)){
                        Toast.makeText(EditProfile.this, "Incorrect Birth Year", Toast.LENGTH_SHORT).show();
                    }
                    else if(TextUtils.isEmpty(Byear.getText().toString())){
                        Toast.makeText(EditProfile.this, "Please Enter Your Age", Toast.LENGTH_SHORT).show();
                    }
                    else if(TextUtils.isEmpty(Allergy.getText().toString())){
                        Toast.makeText(EditProfile.this, "Please Enter Your Allergy Status", Toast.LENGTH_SHORT).show();
                    }
                    else if(TextUtils.isEmpty(Health.getText().toString())){
                        Toast.makeText(EditProfile.this, "Please Enter Your Health Condition", Toast.LENGTH_SHORT).show();
                    }
                    else if(TextUtils.isEmpty(Medication.getText().toString())){
                        Toast.makeText(EditProfile.this, "Please Enter Your Medication", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        update(filePath);
                        HashMap hashMap = new HashMap();
                        hashMap.put("User_Name", Username.getText().toString());
                        hashMap.put("User_Birth_Year", Byear.getText().toString());
                        hashMap.put("User_Allergy", Allergy.getText().toString());
                        hashMap.put("User_Condition", Health.getText().toString());
                        hashMap.put("User_Medication", Medication.getText().toString());
                        HPDatabase.child(uniqueKey).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener(){
                            @Override
                            public void onSuccess(@NonNull Object o) {
                                Toast.makeText(EditProfile.this, "Data is updated", Toast.LENGTH_SHORT).show();
                                // Intent intent = new Intent(getApplication().getApplicationContext(),Profile.class);
                                // startActivity(intent); //enter main menu
                            }
                        });
                    }
                }
            });
        }
    }

    public void callMainMenu(View view){ //call and enter profile page
        Intent intent = new Intent(getApplication().getApplicationContext(),MyProfile.class);
        startActivity(intent); //enter profile page
    }

    // Select Image method
    private void SelectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){
                @Override
                public void onActivityResult(ActivityResult result){
                    // here we will handle the result of our intent
                    if(result.getResultCode() == Activity.RESULT_OK){
                        // image picked
                        // get uri of image
                        Intent data = result.getData();
                        filePath = data.getData();
                        imgEdit.setImageURI(filePath);
                    }
                    else{
                        // cancelled
                        Toast.makeText(EditProfile.this, "Cancelled...",Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    // UploadImage method
    private void update(Uri filePath){
        if(filePath != null){
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            final StorageReference ref = storageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");

            // adding listeners on upload or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
                                @Override
                                public void onSuccess(@NonNull Uri uri) {
                                    Picasso.get().load(uri).into(imgEdit);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception e){
                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>(){
                        // Progress Listener for loading percentage on the dialog box
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot){
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int)progress + "%");
                        }
                    });
        }
    }
}