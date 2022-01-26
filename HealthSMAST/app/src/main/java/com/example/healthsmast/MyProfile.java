package com.example.healthsmast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class MyProfile extends AppCompatActivity {

    // view for image view
    ImageView imgView;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    // instance for firebase authentication
    FirebaseAuth mAuth;

    DatabaseReference HPDatabase;
    TextView Name;
    TextView Allergy;
    TextView Health;
    TextView Age;
    TextView Medication;
    String uniqueKey;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // get the Firebase authentication
        mAuth = FirebaseAuth.getInstance();
        // get the Firebase storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // initialise views
        imgView = (ImageView)findViewById(R.id.pictureView);
        Name = (TextView)findViewById(R.id.nameView);
        Allergy = (TextView)findViewById(R.id.allergyView);
        Age = (TextView)findViewById(R.id.ageView);
        Health = (TextView)findViewById(R.id.healthView);
        Medication = (TextView)findViewById(R.id.medView);

        // defining the child of storageReference
        StorageReference profileRef = storageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>(){
                    @Override
                    public void onSuccess(@NonNull Uri uri) {
                        Picasso.get().load(uri).into(imgView);
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
                public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        uniqueKey = dataSnapshot1.getKey();
                        Allergy.setText(dataSnapshot1.child("User_Allergy").getValue(String.class));
                        Name.setText(dataSnapshot1.child("User_Name").getValue(String.class));
                        int intBYear = Integer.parseInt(dataSnapshot1.child("User_Birth_Year").getValue(String.class)); //retrieve user's birth year and convert it to int
                        int currentYear = Calendar.getInstance().get(Calendar.YEAR); //get current year
                        int intAge = currentYear - intBYear;//calculate the user's current age
                        String stringAge = String.valueOf(intAge); //convert user's birth year to string
                        Age.setText(stringAge);
                        Health.setText(dataSnapshot1.child("User_Condition").getValue(String.class));
                        Medication.setText(dataSnapshot1.child("User_Medication").getValue(String.class));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    public void callMainMenu(View view){ //call and enter main menu
        Intent intent = new Intent(getApplication().getApplicationContext(),MainMenu.class);
        startActivity(intent); //enter main menu
    }
    public void callEditProfile(View view){ //call and enter edit profile mode
        Intent intent = new Intent(getApplication().getApplicationContext(),EditProfile.class);
        startActivity(intent); //enter edit profile mode
    }
}