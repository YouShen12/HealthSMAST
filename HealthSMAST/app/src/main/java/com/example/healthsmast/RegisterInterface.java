package com.example.healthsmast;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class RegisterInterface extends AppCompatActivity {

    TextInputEditText Email;
    TextInputEditText Password;
    TextInputEditText ConfirmPassword;
    TextInputEditText BYear;
    TextInputEditText Name;
    TextView Response;
    Button registerButton;
    FirebaseAuth fAuth;
    DatabaseReference HPDatabase;
    ProgressBar RegisterPB;

    String nameInput = "";
    int ageInput = 0;
    int intYearinput = 0;
    String emailInput = "";
    String passwordInput = "";
    String confirmPasswordInput = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_interface);

        Name = (TextInputEditText)findViewById(R.id.RegName);
        BYear = (TextInputEditText)findViewById(R.id.RegBYear);
        Email = (TextInputEditText)findViewById(R.id.Regemail);
        Password = (TextInputEditText)findViewById(R.id.Regpassword);
        ConfirmPassword = (TextInputEditText)findViewById(R.id.RegconfirmPassword);
        Response = (TextView) findViewById(R.id.Regresponse);
        RegisterPB = findViewById(R.id.RegisterProgress);
        registerButton = (Button)findViewById(R.id.registerAccount);


        fAuth = FirebaseAuth.getInstance();
        registerButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) { //when register button is clicked
                nameInput = Name.getText().toString();
                try{
                    intYearinput = Integer.parseInt(BYear.getText().toString()); //get birth year
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR); //get current year
                    ageInput = currentYear - intYearinput; //calculate the age of the user
                }
                catch (NumberFormatException e){
                    ageInput = 0;
                }
                emailInput = Email.getText().toString().trim().toLowerCase();
                passwordInput = Password.getText().toString().trim();
                confirmPasswordInput = ConfirmPassword.getText().toString().trim();
                if ((nameInput.equals(""))||(emailInput.equals(""))||(passwordInput.equals(""))||(confirmPasswordInput.equals(""))) //if one of the field is empty
                    Response.setText("Please fill up all the fields required");
                else if ((ageInput < 1)||(ageInput > 125)) //if birth year input filed is empty or has incorrect value
                    Response.setText("Incorrect birth year");
                else if(!passwordInput.equals(confirmPasswordInput)) // when password and confirm password are not the same
                    Response.setText("Password confirmation error");
                else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) // if email format is invalid
                    Response. setText("Invalid email address");
                //if email already exists in database
                else{ //all data are correct
                    RegisterPB.setVisibility(View.VISIBLE);
                    fAuth.createUserWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //send verification link
                                FirebaseUser Fuser = fAuth.getCurrentUser();
                                Fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RegisterInterface.this,"Verification email sent",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("tag", "onFailure: Email not sent"+e.getMessage());
                                    }
                                });
                                Toast.makeText(RegisterInterface.this,"Account Registered",Toast.LENGTH_SHORT).show();
                                HPDatabase = FirebaseDatabase.getInstance("https://healthsmast-12e4e-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference(); //initialize database
                                HashMap<String, String> dataMap = new HashMap<String, String>(); //initialize hashmap to store the values that will be added to the database
                                dataMap.put("User_Email", emailInput); //store user's email
                                dataMap.put("User_Name", nameInput); //store user's name
                                String stringYearInput = String.valueOf(intYearinput); //convert user's birth year to string
                                dataMap.put("User_Birth_Year", stringYearInput); //store user's birth year
                                dataMap.put("User_Allergy", "No allergy"); // store user's allergy status
                                dataMap.put("User_Condition", "No health condition"); //store user's health condition status
                                dataMap.put("User_Medication", "No medication"); //store user's medication status
                                HPDatabase.child("User").push().setValue(dataMap); //store user's data into firebase realtime database
                                Intent intent = new Intent(getApplication().getApplicationContext(),MainActivity.class);
                                startActivity(intent); //enter home page
                            }
                            else
                            {
                                RegisterPB.setVisibility(View.INVISIBLE); //hide progress bar
                                Toast.makeText(RegisterInterface.this,"Error! Account Not Registered"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    public void emailVerification(View view){

    }

    public void callMainActivity(View view){ //call and enter home page (main activity)

        Intent intent = new Intent(getApplication().getApplicationContext(),MainActivity.class);
        startActivity(intent); //enter home page
    }

    public void callLoginInterface(View view){ //call and enter login interface

        Intent intent = new Intent(getApplication().getApplicationContext(),LoginInterface.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.loginButton),"login_button_trans");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            //android version is lolipop and onwards
            //initialize transition animation
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterInterface.this,pairs);
            startActivity(intent, options.toBundle()); //enter login interface with transition animation
        }
        else
        { //android version older than lolipop
            startActivity(intent); //enter login interface
        }

    }
}