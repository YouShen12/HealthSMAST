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

public class ContributeDiabetesData extends AppCompatActivity {

    TextInputEditText Age;
    TextInputEditText Weight;
    TextInputEditText Height;
    RadioGroup GenderGroup;
    RadioButton GButton;
    RadioGroup PolyuriaGroup;
    RadioButton PolyuriaButton;
    RadioGroup PolydipsiaGroup;
    RadioButton PolydipsiaButton;
    RadioGroup PolyphagiaGroup;
    RadioButton PolyphagiaButton;
    RadioGroup DGroup;
    RadioButton DButton;
    TextView Response;
    String Gender;
    String  Polyuria;
    String  Polydipsia;
    String  Polyphagia;
    String Target;
    float Ageinput = 0;
    float Weightinput = 0;
    float Heightinput = 0;
    DatabaseReference DDatabase;
    ProgressBar SubmitPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute_diabetes_data);
        GenderGroup = findViewById(R.id.CDGRadGrp);
        PolyuriaGroup = findViewById(R.id.CDPolyuriaRadGrp);
        PolydipsiaGroup = findViewById(R.id.CDPolydipsiaRadGrp);
        PolyphagiaGroup = findViewById(R.id.CDPolyphagiaRadGrp);
        DGroup = findViewById(R.id.CDDiabetes);
        Response = findViewById(R.id.CDResponse);
        Age = (TextInputEditText)findViewById(R.id.CDpredictionAge);
        Weight = (TextInputEditText)findViewById(R.id.CDweight);
        Height = (TextInputEditText)findViewById(R.id.CDheight);
        SubmitPB = findViewById(R.id.CDPSubmitProgress);

        Button submitButton = findViewById(R.id.CDSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() { //when submit button is clicked
            @Override
            public void onClick(View view) {
                if (Age.getText().toString().equals("")) //if age field is empty
                {
                    Ageinput = 0;
                }
                else if (Weight.getText().toString().equals("")) //if weight field is empty
                {
                    Weightinput = 0;
                }
                else if (Height.getText().toString().equals("")) //if height field is empty
                {
                    Heightinput = 0;
                }
                else{ //if all fields are filled
                    int intAgeinput = Integer.parseInt(Age.getText().toString()); //convert the inputs to int
                    int intWeightinput = Integer.parseInt(Weight.getText().toString());
                    int intHeightinput = Integer.parseInt(Height.getText().toString());
                    Ageinput = intAgeinput;
                    Weightinput = intWeightinput;
                    Heightinput = intHeightinput;
                }
                int radioIdG = GenderGroup.getCheckedRadioButtonId();
                GButton = (RadioButton) findViewById(radioIdG);
                if(GButton.getText().equals("MALE"))
                    Gender = "1"; //if male button is selected, then gender value is 1
                else
                    Gender = "0"; //if female button is selected, then gender value is 0
                int radioIdPolyuria = PolyuriaGroup.getCheckedRadioButtonId();
                PolyuriaButton = (RadioButton)findViewById(radioIdPolyuria);
                if(PolyuriaButton.getText().equals("YES"))
                    Polyuria = "1"; //if yes button is selected, then cp value is 1
                else
                    Polyuria = "0"; //if no button is selected, then cp value is 0
                int radioIdPolydipsia = PolydipsiaGroup.getCheckedRadioButtonId();
                PolydipsiaButton = (RadioButton)findViewById(radioIdPolydipsia);
                if(PolydipsiaButton.getText().equals("YES"))
                    Polydipsia = "1"; //if yes button is selected, then cp value is 1
                else
                    Polydipsia = "0"; //if no button is selected, then cp value is 0
                int radioIdPolyphagia = PolyphagiaGroup.getCheckedRadioButtonId();
                PolyphagiaButton = (RadioButton)findViewById(radioIdPolyphagia);
                if(PolyphagiaButton.getText().equals("YES"))
                    Polyphagia = "1"; //if yes button is selected, then cp value is 1
                else
                    Polyphagia = "0"; //if no button is selected, then cp value is 0
                int radioIdD = DGroup.getCheckedRadioButtonId();
                DButton = (RadioButton)findViewById(radioIdD);
                if(DButton.getText().equals("YES"))
                    Target = "1"; //if yes button is selected, then cp value is 1
                else
                    Target = "0"; //if no button is selected, then cp value is 0
                if((Ageinput == 0)||(Weightinput == 0)||(Heightinput == 0)) //if one of the fields is empty
                    Response.setText("Please fill up all the fields required");
                else if ((Ageinput < 1)||(Ageinput > 125)) //if age input filed is empty or has incorrect value
                    Response.setText("Age must be between 1 to 125");
                else if (Weightinput < 1) //if bp input field is empty or has incorrect value
                    Response.setText("Weight must be greater than 0");
                else if (Heightinput < 1) //if bp input field is empty or has incorrect value
                    Response.setText("Height must be greater than 0");
                else{ //submit data to database
                    float Obesity = 0;
                    if (Weightinput / (Heightinput*Heightinput) >= 25.0) {
                        Obesity = 1;
                    }
                    else
                        Obesity = 0;
                    SubmitPB.setVisibility(View.VISIBLE);
                    String age = Age.getText().toString();
                    String weight = Weight.getText().toString();
                    String height = Height.getText().toString();
                    DDatabase = FirebaseDatabase.getInstance("https://healthsmast-12e4e-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference(); //initialize database
                    HashMap<String, String> dataMap = new HashMap<String, String>(); //initialize hashmap to store the values that will be added to the database
                    dataMap.put("D_age", age); //store age
                    dataMap.put("D_obesity", weight); //store weight
                    dataMap.put("D_height", height); //store height
                    dataMap.put("D_gender", Gender); //store gender
                    dataMap.put("D_polyuria", Polyuria); //store gender
                    dataMap.put("D_polydipsia", Polydipsia); //store gender
                    dataMap.put("D_polyphagia", Polyphagia); //store gender
                    dataMap.put("D_target", Target); //store target
                    DDatabase.child("Diabetes").push().setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {//store user's data into firebase realtime database
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) { //if data is successfully submitted to realtime database
                                Toast.makeText(ContributeDiabetesData.this, "Data submitted, thank you for your contribution", Toast.LENGTH_SHORT).show();
                            } else { //if data is not successfully submitted to realtime database
                                Toast.makeText(ContributeDiabetesData.this, "Data not submitted, please try again", Toast.LENGTH_SHORT).show();
                            }
                            Intent intent = new Intent(getApplication().getApplicationContext(),MainMenu.class);
                            startActivity(intent); //enter main menu
                        }
                    });
                }
            }
        });
    }


    public void callContributeDataSubMenu(View view){ //call and enter contribute data sub menu

        Intent intent = new Intent(getApplication().getApplicationContext(),ContributeDataSubMenu.class);
        startActivity(intent); //call and enter contribute data sub menu
    }
}
