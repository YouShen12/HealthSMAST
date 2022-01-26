package com.example.healthsmast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut(); //logout
        startActivity(new Intent(getApplication(), MainActivity.class)); //enter landing page
        finish();
    }

    public void callCodeScanner(View view){ //call and enter code scanner

        Intent intent = new Intent(getApplication().getApplicationContext(),CodeScanner.class);
        startActivity(intent); //enter code scanner page
    }

    public void callNameScanner(View view){ //call and enter name scanner

        Intent intent = new Intent(getApplication().getApplicationContext(),NameScanner.class);
        startActivity(intent); //enter name scanner page
    }

    public void callContributeDataSubMenu(View view){ //call and enter contribute data sub menu

        Intent intent = new Intent(getApplication().getApplicationContext(),ContributeDataSubMenu.class);
        startActivity(intent); //call and enter contribute data sub menu
    }

    public void callDiseasePredictor(View view){ //call and enter disease predictor sub menu

        Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.PredictorSubMenu.class);
        startActivity(intent); //enter disease predictor sub menu
    }

    public void callMyProfile(View view){ //call and enter my profile page

        Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.MyProfile.class);
        startActivity(intent); //enter my profile page
    }
}