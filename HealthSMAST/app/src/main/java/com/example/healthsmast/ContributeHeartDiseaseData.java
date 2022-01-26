package com.example.healthsmast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ContributeHeartDiseaseData extends AppCompatActivity {

    TextInputEditText Age;
    TextInputEditText BP;
    RadioGroup GenderGroup;
    RadioButton GButton;
    RadioGroup CPGroup;
    RadioButton CPButton;
    RadioGroup HDGroup;
    RadioButton HDButton;
    TextView Response;
    String Gender;
    String  CP;
    String Target;
    float Ageinput = 0;
    float BPinput = 0;
    DatabaseReference HPDatabase;
    ProgressBar SubmitPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute_heart_disease_data);
        GenderGroup = findViewById(R.id.CHDGRadGrp);
        CPGroup = findViewById(R.id.CHDCPRadGrp);
        HDGroup = findViewById(R.id.CHDHeartDisease);
        Response = findViewById(R.id.CHDResponse);
        Age = (TextInputEditText)findViewById(R.id.CHDpredictionAge);
        BP = (TextInputEditText)findViewById(R.id.CHDtrest);
        SubmitPB = findViewById(R.id.CHPSubmitProgress);

        Button submitButton = findViewById(R.id.CHDSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() { //when submit button is clicked
            @Override
            public void onClick(View view) {
                if (Age.getText().toString().equals("")) //if age field is empty
                {
                    Ageinput = 0;
                }
                else if (BP.getText().toString().equals("")) //if BP field is empty
                {
                    BPinput = 0;
                }
                else{ //if both age field and BP field are filled
                    int intAgeinput = Integer.parseInt(Age.getText().toString()); //convert the inputs to int
                    int intBPinput = Integer.parseInt(BP.getText().toString());
                    Ageinput = intAgeinput;
                    BPinput = intBPinput;
                }
                int radioIdG = GenderGroup.getCheckedRadioButtonId();
                GButton = (RadioButton) findViewById(radioIdG);
                if(GButton.getText().equals("MALE"))
                    Gender = "1"; //if male button is selected, then gender value is 1
                else
                    Gender = "0"; //if female button is selected, then gender value is 0
                int radioIdCP = CPGroup.getCheckedRadioButtonId();
                CPButton = (RadioButton)findViewById(radioIdCP);
                if(CPButton.getText().equals("YES"))
                    CP = "1"; //if yes button is selected, then cp value is 1
                else
                    CP = "0"; //if no button is selected, then cp value is 0
                int radioIdHD = HDGroup.getCheckedRadioButtonId();
                HDButton = (RadioButton)findViewById(radioIdHD);
                if(HDButton.getText().equals("YES"))
                    Target = "1"; //if yes button is selected, then target value is 1
                else
                    Target = "0"; //if no button is selected, then target value is 0
                if((Ageinput == 0)||(BPinput == 0)) //if one of the fields is empty
                    Response.setText("Please fill up all the fields required");
                else if ((Ageinput < 1)||(Ageinput > 125)) //if age input filed is empty or has incorrect value
                    Response.setText("Age must be between 1 to 125");
                else if ((BPinput < 70)||(BPinput > 300)) //if bp input field is empty or has incorrect value
                    Response.setText("Blood Pressure must be between 70 to 300");
                else{ //submit data to database
                    SubmitPB.setVisibility(View.VISIBLE);
                    String age = Age.getText().toString();
                    String bp = BP.getText().toString();
                    HPDatabase = FirebaseDatabase.getInstance("https://healthsmast-12e4e-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference(); //initialize database
                    HashMap<String, String> dataMap = new HashMap<String, String>(); //initialize hashmap to store the values that will be added to the database
                    dataMap.put("HD_age", age); //store age
                    dataMap.put("HD_trest", bp); //store bp
                    dataMap.put("HD_exang", CP); //store chest pain after exercise value
                    dataMap.put("HD_gender", Gender); //store gender
                    dataMap.put("HD_target", Target); //store target
                    HPDatabase.child("Heart_Disease").push().setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {//store user's data into firebase realtime database
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) { //if data is successfully submitted to realtime database
                                Toast.makeText(ContributeHeartDiseaseData.this, "Data submitted, thank you for your contribution", Toast.LENGTH_SHORT).show();
                            } else { //if data is not successfully submitted to realtime database
                                Toast.makeText(ContributeHeartDiseaseData.this, "Data not submitted, please try again", Toast.LENGTH_SHORT).show();
                            }
                            Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.MainMenu.class);
                            startActivity(intent); //enter main menu
                        }
                    });
                }
            }
        });
    }


    public void callContributeDataSubMenu(View view){ //call and enter contribute data sub menu

        Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.ContributeDataSubMenu.class);
        startActivity(intent); //call and enter contribute data sub menu
    }
}