package com.example.healthsmast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class PredictorSubMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictor_sub_menu);
    }

    public void callMainMenu(View view){ //call and enter main menu

        Intent intent = new Intent(getApplication().getApplicationContext(),MainMenu.class);
        startActivity(intent); //enter main menu
    }

    public void callHeartDiseasePredictor(View view){ //call and enter heart disease predictor

        Intent intent = new Intent(getApplication().getApplicationContext(), HeartDiseasePredictor.class);
        startActivity(intent); //enter heart disease predictor
    }

    public void callDiabetesPredictor(View view){ //call and enter diabetes predictor

        Intent intent = new Intent(getApplication().getApplicationContext(), DiabetesPredictor.class);
        startActivity(intent); //enter diabetes predictor
    }

    public void callHypertensionPredictor(View view){ //call and enter hypertension predictor

        Intent intent = new Intent(getApplication().getApplicationContext(), HypertensionPredictor.class);
        startActivity(intent); //enter hypertension predictor
    }

}