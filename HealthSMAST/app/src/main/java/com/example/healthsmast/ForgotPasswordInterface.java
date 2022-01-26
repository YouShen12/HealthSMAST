package com.example.healthsmast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordInterface extends AppCompatActivity {

    TextInputEditText Email;
    TextView Response;
    String emailInput = "";
    Button registerButton;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_interface);

        Email = (TextInputEditText)findViewById(R.id.FPIemail);
        Response = (TextView) findViewById(R.id.FPIresponse);
        fAuth = FirebaseAuth.getInstance();
        registerButton = (Button)findViewById(R.id.FPIsubmitButton);
        registerButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) { //when register button is clicked
                emailInput = Email.getText().toString().trim();
                if (emailInput.equals("")) //if email field is empty
                    Response.setText("Please fill up all the fields required");
                else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) //if email format is invalid
                    Response. setText("Invalid email address");
                else{
                    fAuth.sendPasswordResetEmail(emailInput).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) { //when password reset email is successfully sent
                            Toast.makeText(ForgotPasswordInterface.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) { //when password reset email failed to be sent
                            Toast.makeText(ForgotPasswordInterface.this, "Error! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void callLoginInterface(View view){ //call and enter login interface

        Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.LoginInterface.class);
        startActivity(intent); //enter login interface
    }


}