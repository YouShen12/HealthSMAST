package com.example.healthsmast;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class DiabetesPredictor extends AppCompatActivity {

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
    TextView Result;
    float Gender;
    float Polyuria;
    float Polydipsia;
    float Polyphagia;
    float Ageinput = 0;
    float Weightinput = 0;
    float Heightinput = 0;
    Interpreter diabetes_tflite;
    String inferredValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diabetes_predictor);

        try{ //load the machine learning model (diabetes_model.tflite) to interpreter diabetes_tflite
            diabetes_tflite = new Interpreter(loadModelFile());
        }catch(Exception e){
            e.printStackTrace();
        }

        GenderGroup = findViewById(R.id.DiaGdrGrp);
        PolyuriaGroup = findViewById(R.id.DiaPolyuriaGrp);
        PolydipsiaGroup = findViewById(R.id.DiaPolydipsiaGrp);
        PolyphagiaGroup = findViewById(R.id.DiaPolyphagiaGrp);
        Result = findViewById(R.id.DiaResult);
        Age = (TextInputEditText)findViewById(R.id.DiapredictionAge);
        Weight = (TextInputEditText)findViewById(R.id.DiaWeight);
        Height = (TextInputEditText)findViewById(R.id.DiaHeight);

        Button predictButton = findViewById(R.id.DiaPred);
        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //when predict button is clicked
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
                    Gender = 1; //if male button is selected, then gender value is 1
                else
                    Gender = 0; //if female button is selected, then gender value is 0
                int radioIdPolyuria = PolyuriaGroup.getCheckedRadioButtonId();
                PolyuriaButton = (RadioButton)findViewById(radioIdPolyuria);
                if(PolyuriaButton.getText().equals("YES"))
                    Polyuria = 1; //if yes button is selected, then cp value is 1
                else
                    Polyuria = 0; //if no button is selected, then cp value is 0
                int radioIdPolydipsia = PolydipsiaGroup.getCheckedRadioButtonId();
                PolydipsiaButton = (RadioButton)findViewById(radioIdPolydipsia);
                if(PolydipsiaButton.getText().equals("YES"))
                    Polydipsia = 1; //if yes button is selected, then cp value is 1
                else
                    Polydipsia = 0; //if no button is selected, then cp value is 0
                int radioIdPolyphagia = PolyphagiaGroup.getCheckedRadioButtonId();
                PolyphagiaButton = (RadioButton)findViewById(radioIdPolyphagia);
                if(PolyphagiaButton.getText().equals("YES"))
                    Polyphagia = 1; //if yes button is selected, then cp value is 1
                else
                    Polyphagia = 0; //if no button is selected, then cp value is 0
                if((Ageinput == 0)||(Weightinput == 0)||(Heightinput == 0)) //if one of the fields is empty
                    Result.setText("Please fill up all the fields required");
                else if ((Ageinput < 1)||(Ageinput > 125)) //if age input filed is empty or has incorrect value
                    Result.setText("Age must be between 1 to 125");
                else if (Weightinput < 1) //if weight input field is empty or has incorrect value
                    Result.setText("Weight must be greater than 0");
                else if (Heightinput < 1) //if height input field is empty or has incorrect value
                    Result.setText("Height must be greater than 0");
                else{
                    float obesity = 0;
                    if (Weightinput / (Heightinput*Heightinput) >= 25.0) {
                        obesity = 1;
                    }
                    else
                        obesity = 0;
                    float output = 0;
                    float[][] classifierInput = new float[][]{{Ageinput,Gender,Polyuria,Polydipsia,Polyphagia,obesity}}; //put all of the values obtained into a 2d array
                    output = inference(classifierInput); //pass the 2d array to our inference function to make predictions
                    if (output == 0)
                        inferredValue = "You do not have diabetes, keep up the good work!";
                    else
                        inferredValue = "You have diabetes, please seek medical attention if you have not consulted a doctor regarding this.";
                    Result.setText(inferredValue); //set and display the predicted result
                }
            }
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException { //load the macine learning model from asset file
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("diabetes_model.tflite");
        FileInputStream fileinputstream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileinputstream.getChannel();
        long startOffSets = fileDescriptor.getStartOffset();
        long declaredLength =fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffSets,declaredLength);
    }

    public float inference(float[][] inputValue){ //make predictions based on the input 2d array
        float[][] outputValue = new float[1][2];
        diabetes_tflite.run(inputValue, outputValue); //run interpreter to make input
        float inferredVal1 = outputValue[0][0]; // get output value (probability of not having diabetes)
        float inferredVal2 = outputValue[0][1]; // get output value (probability of having diabetes)
        float inferredVal = 0;
        if (inferredVal1 >= inferredVal2) //if probability of not having diabetes is greater than having diabetes
            inferredVal = 0;
        else //if probability of having diabetes is greater than not having diabetes
            inferredVal = 1;
        return inferredVal;
    }



    public void callPredictorSubMenu(View view){ //call and enter predictor sub menu

        Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.PredictorSubMenu.class);
        startActivity(intent); //enter predictor sub menu
    }


}