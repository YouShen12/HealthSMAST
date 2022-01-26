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

public class HeartDiseasePredictor extends AppCompatActivity {

    TextInputEditText Age;
    TextInputEditText BP;
    RadioGroup GenderGroup;
    RadioButton GButton;
    RadioGroup CPGroup;
    RadioButton CPButton;
    TextView Result;
    float Gender;
    float CP;
    float Ageinput = 0;
    float BPinput = 0;
    Interpreter heart_tflite;
    String inferredValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_disease_predictor);

        try{ //load the machine learning model (heart_model.tflite) to interpreter heart_tflite
            heart_tflite = new Interpreter(loadModelFile());
        }catch(Exception e){
            e.printStackTrace();
        }

        GenderGroup = findViewById(R.id.HDGRadGrp);
        CPGroup = findViewById(R.id.HDCPRadGrp);
        Result = findViewById(R.id.HDResult);
        Age = (TextInputEditText)findViewById(R.id.HDpredictionAge);
        BP = (TextInputEditText)findViewById(R.id.HDtrest);

        Button predictButton = findViewById(R.id.HDPred);
        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //when predict button is clicked
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
                    Gender = 1; //if male button is selected, then gender value is 1
                else
                    Gender = 0; //if female button is selected, then gender value is 0
                int radioIdCP = CPGroup.getCheckedRadioButtonId();
                CPButton = (RadioButton)findViewById(radioIdCP);
                if(CPButton.getText().equals("YES"))
                    CP = 1; //if yes button is selected, then cp value is 1
                else
                    CP = 0; //if no button is selected, then cp value is 0
                if((Ageinput == 0)||(BPinput == 0)) //if one of the fields is empty
                    Result.setText("Please fill up all the fields required");
                else if ((Ageinput < 1)||(Ageinput > 125)) //if age input filed is empty or has incorrect value
                    Result.setText("Age must be between 1 to 125");
                else if ((BPinput < 70)||(BPinput > 300)) //if bp input field is empty or has incorrect value
                    Result.setText("Blood Pressure must be between 70 to 300");
                else{
                        float output = 0;
                        float[][] classifierInput = new float[][]{{Ageinput,Gender,BPinput,CP}}; //put all of the values obtained into a 2d array
                        output = inference(classifierInput); //pass the 2d array to our inference function to make predictions
                        if (output == 0)
                            inferredValue = "You do not have heart disease, keep up the good work!";
                        else
                            inferredValue = "You have heart disease, please seek medical attention if you have not consulted a doctor regarding this.";
                        Result.setText(inferredValue); //set and display the predicted result
                }
            }
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException { //load the macine learning model from asset file
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("heart_model.tflite");
        FileInputStream fileinputstream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileinputstream.getChannel();
        long startOffSets = fileDescriptor.getStartOffset();
        long declaredLength =fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffSets,declaredLength);
    }

    public float inference(float[][] inputValue){ //make predictions based on the input 2d array
        float[][] outputValue = new float[1][2];
        heart_tflite.run(inputValue, outputValue); //run interpreter to make input
        float inferredVal1 = outputValue[0][0]; // get output value (probability of not having heart disease)
        float inferredVal2 = outputValue[0][1]; // get output value (probability of having heart disease)
        float inferredVal = 0;
        if (inferredVal1 >= inferredVal2) //if probability of not having heart disease is greater than having heart disease
            inferredVal = 0;
        else //if probability of having heart disease is greater than not having heart disease
            inferredVal = 1;
        return inferredVal;
    }



    public void callPredictorSubMenu(View view){ //call and enter predictor sub menu

        Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.PredictorSubMenu.class);
        startActivity(intent); //enter predictor sub menu
    }


}