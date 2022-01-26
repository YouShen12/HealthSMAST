package com.example.healthsmast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ContributeHyptertensionData extends AppCompatActivity {

    TextInputEditText Age;
    TextInputEditText Weight;
    TextInputEditText Height;
    TextInputEditText HeartRate;
    RadioGroup HGroup;
    RadioButton HButton;
    TextView Response;
    String Target;
    float Ageinput = 0;
    float Weightinput = 0;
    float Heightinput = 0;
    float HeartRateInput = 0;
    DatabaseReference HPDatabase;
    ProgressBar SubmitPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute_hyptertension_data);
        HGroup = findViewById(R.id.CHypertension);
        Response = findViewById(R.id.CHResponse);
        Age = (TextInputEditText)findViewById(R.id.CHpredictionAge);
        Weight = (TextInputEditText)findViewById(R.id.CHweight);
        Height = (TextInputEditText)findViewById(R.id.Cheight);
        HeartRate = (TextInputEditText)findViewById(R.id.Cheartrate);
        SubmitPB = findViewById(R.id.CHPSubmitProgress);

        Button submitButton = findViewById(R.id.CHSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() { //when submit button is clicked
            @Override
            public void onClick(View view) {
                if (Age.getText().toString().equals("")) //if age field is empty
                {
                    Ageinput = 0;
                }
                else if (Weight.getText().toString().equals("")) //if Weight field is empty
                {
                    Weightinput = 0;
                }
                else if (Height.getText().toString().equals("")) //if Height field is empty
                {
                    Heightinput = 0;
                }
                else if (HeartRate.getText().toString().equals("")) //if Heart Rate field is empty
                {
                    HeartRateInput = 0;
                }
                else{ //if all fields are filled
                    int intAgeinput = Integer.parseInt(Age.getText().toString()); //convert the inputs to int
                    int intWeightinput = Integer.parseInt(Weight.getText().toString());
                    int intHeightinput = Integer.parseInt(Height.getText().toString());
                    int intHRinput = Integer.parseInt(HeartRate.getText().toString());
                    Ageinput = intAgeinput;
                    Weightinput = intWeightinput;
                    Heightinput = intHeightinput;
                    HeartRateInput = intHRinput;
                }
                int radioIdH = HGroup.getCheckedRadioButtonId();
                HButton = (RadioButton)findViewById(radioIdH);
                if(HButton.getText().equals("YES"))
                    Target = "1"; //if yes button is selected, then target value is 1
                else
                    Target = "0"; //if no button is selected, then target value is 0
                if((Ageinput == 0)||(Weightinput == 0)||(Heightinput == 0)||(HeartRateInput == 0)) //if one of the fields is empty
                    Response.setText("Please fill up all the fields required");
                else if ((Ageinput < 1)||(Ageinput > 125)) //if age input filed is empty or has incorrect value
                    Response.setText("Age must be between 1 to 125");
                else if ((Weightinput < 20)||(Weightinput > 300)) //if weight input field is empty or has incorrect value
                    Response.setText("Weight must be between 20 to 300");
                else if ((Heightinput < 70)||(Heightinput > 270)) //if height input field is empty or has incorrect value
                    Response.setText("Height must be between 70 to 270");
                else if (HeartRateInput < 40) //if heart rate input field is empty or has incorrect value
                    Response.setText("Heart rate must be more than 40");
                else if (HeartRateInput > 180) //if heart rate is too high
                    Response.setText("Your heart rate is too high, please seek medical help immediately");
                else{ //submit data to database
                    SubmitPB.setVisibility(View.VISIBLE);
                    String age = Age.getText().toString();
                    String weight = Weight.getText().toString();
                    String height = Height.getText().toString();
                    String heartrate = HeartRate.getText().toString();
                    HPDatabase = FirebaseDatabase.getInstance("https://healthsmast-12e4e-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference(); //initialize database
                    HashMap<String, String> dataMap = new HashMap<String, String>(); //initialize hashmap to store the values that will be added to the database
                    dataMap.put("H_age", age); //store age
                    dataMap.put("H_weight", weight); //store weight
                    dataMap.put("H_height", height); //store height
                    dataMap.put("H_target", Target); //store target
                    HPDatabase.child("Hypertension").push().setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {//store user's data into firebase realtime database
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) { //if data is successfully submitted to realtime database
                                Toast.makeText(ContributeHyptertensionData.this, "Data submitted, thank you for your contribution", Toast.LENGTH_SHORT).show();
                            } else { //if data is not successfully submitted to realtime database
                                Toast.makeText(ContributeHyptertensionData.this, "Data not submitted, please try again", Toast.LENGTH_SHORT).show();
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