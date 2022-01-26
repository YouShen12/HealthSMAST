package com.example.healthsmast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ContributeDataSubMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute_data_sub_menu);
    }

    public void callMainMenu(View view){ //call and enter main menu

        Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.MainMenu.class);
        startActivity(intent); //enter main menu
    }

    public void callContributeHeartDiseaseData(View view){ //call and enter contribute heart disease data page

        Intent intent = new Intent(getApplication().getApplicationContext(),ContributeHeartDiseaseData.class);
        startActivity(intent); //call and enter contribute heart disease data page
    }
    public void callContributeDiabetesData(View view){ //call and enter contribute diabetes data page

        Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.ContributeDiabetesData.class);
        startActivity(intent); //call and enter contribute diabetes data page
    }
    public void callContributeHypertensionData(View view){ //call and enter contribute hypertension data page

        Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.ContributeHyptertensionData.class);
        startActivity(intent); //call and enter contribute hypertension data page
    }
}