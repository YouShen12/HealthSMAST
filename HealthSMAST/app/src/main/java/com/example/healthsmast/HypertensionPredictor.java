package com.example.healthsmast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class HypertensionPredictor extends AppCompatActivity {
    TextInputEditText Age;
    TextInputEditText Weight;
    TextInputEditText Height;
    TextInputEditText HeartRate;
    TextView Result;
    float Ageinput = 0;
    float Weightinput = 0;
    float Heightinput = 0;
    float HeartRateInput = 0;
    Interpreter hypertension_tflite;
    String inferredValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hypertension_predictor);
        try{ //load the machine learning model (hypertension_model.tflite) to interpreter hypertension_tflite
            hypertension_tflite = new Interpreter(loadModelFile());
        }catch(Exception e){
            e.printStackTrace();
        }

        Result = findViewById(R.id.HResult);
        Age = (TextInputEditText)findViewById(R.id.HpredictionAge);
        Weight = (TextInputEditText)findViewById(R.id.Hweight);
        Height = (TextInputEditText)findViewById(R.id.Hheight);
        HeartRate = (TextInputEditText)findViewById(R.id.Hheartrate);

        Button predictButton = findViewById(R.id.HPred);
        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //when predict button is clicked
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
                if((Ageinput == 0)||(Weightinput == 0)||(Heightinput == 0)||(HeartRateInput == 0)) //if one of the fields is empty
                    Result.setText("Please fill up all the fields required");
                else if ((Ageinput < 1)||(Ageinput > 125)) //if age input filed is empty or has incorrect value
                    Result.setText("Age must be between 1 to 125");
                else if ((Weightinput < 20)||(Weightinput > 300)) //if weight input field is empty or has incorrect value
                    Result.setText("Weight must be between 20 to 300");
                else if ((Heightinput < 70)||(Heightinput > 270)) //if height input field is empty or has incorrect value
                    Result.setText("Height must be between 70 to 270");
                else if (HeartRateInput < 40) //if heart rate input field is empty or has incorrect value
                    Result.setText("Heart rate must be more than 40");
                else if (HeartRateInput > 180) //if heart rate is too high
                    Result.setText("Your heart rate is too high, please seek medical help immediately");
                else{
                    float output = 0;
                    float bmi = Weightinput/((Heightinput * Heightinput)/10000);
                    float[][] classifierInput = new float[][]{{Ageinput,bmi,HeartRateInput}}; //put all of the values obtained into a 2d array
                    output = inference(classifierInput); //pass the 2d array to our inference function to make predictions
                    if (output == 0)
                        inferredValue = "You do not have hypertension, keep up the good work!";
                    else
                        inferredValue = "You have hypertension, please exercise regularly";
                    Result.setText(inferredValue); //set and display the predicted result
                }
            }
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException { //load the macine learning model from asset file
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("hypertension_model.tflite");
        FileInputStream fileinputstream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileinputstream.getChannel();
        long startOffSets = fileDescriptor.getStartOffset();
        long declaredLength =fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffSets,declaredLength);
    }

    public float inference(float[][] inputValue){ //make predictions based on the input 2d array
        float[][] outputValue = new float[1][2];
        hypertension_tflite.run(inputValue, outputValue); //run interpreter to make input
        float inferredVal1 = outputValue[0][0]; // get output value (probability of not having hypertension)
        float inferredVal2 = outputValue[0][1]; // get output value (probability of having hypertension)
        float inferredVal = 0;
        if (inferredVal1 >= inferredVal2) //if probability of not having hypertension is greater than having hypertension
            inferredVal = 0;
        else //if probability of having hypertension is greater than not having hypertension
            inferredVal = 1;
        return inferredVal;
    }



    public void callPredictorSubMenu(View view){ //call and enter predictor sub menu

        Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.PredictorSubMenu.class);
        startActivity(intent); //enter predictor sub menu
    }
}
