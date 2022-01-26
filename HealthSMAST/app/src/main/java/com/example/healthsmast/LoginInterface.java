package com.example.healthsmast;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class LoginInterface extends AppCompatActivity {

    TextInputEditText Email;
    TextInputEditText Password;
    TextView Response;
    Button loginButton;
    FirebaseAuth fAuth;
    ProgressBar loginPB;
    CheckBox rememberMe;

    String emailInput = "";
    String passwordInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_interface);


        Email = (TextInputEditText)findViewById(R.id.Loginemail);
        Password = (TextInputEditText)findViewById(R.id.Loginpassword);
        Response = (TextView) findViewById(R.id.Loginresponse);
        rememberMe = (CheckBox)findViewById(R.id.rememberMe);
        fAuth = FirebaseAuth.getInstance();
        loginPB = findViewById(R.id.LoginProgress);
        Session session = new Session(LoginInterface.this);
        if(session.checkRememberMe()){ //check if there is previously stored user login info (when user check's remember me)
            HashMap<String,String> rememberMeDetails = session.getUserDetailFromSession(); //get previously stored user login info
            Email.setText(rememberMeDetails.get(session.sessionEmail)); //set previously stored user email
            Password.setText(rememberMeDetails.get(session.sessionPW)); //set previously stored user password
        }
        loginButton = (Button)findViewById(R.id.LoginSubButton);
        loginButton.setOnClickListener(new View.OnClickListener(){ //when login button is clicked

            @Override
            public void onClick(View view) { //when register button is clicked
                emailInput = Email.getText().toString().trim();
                passwordInput = Password.getText().toString().trim();
                if ((emailInput.equals(""))||(passwordInput.equals(""))) //if one of the field is empty
                    Response.setText("Please fill up all the fields required");
                else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) // if email format is invalid
                    Response. setText("Invalid email address");
                else{ //all data are correct
                    loginPB.setVisibility(View.VISIBLE); //display progress bar
                    fAuth.signInWithEmailAndPassword(emailInput,passwordInput).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){ //login is successful
                                if(rememberMe.isChecked()){ //if remember me checkbox is checked by the user
                                    Session session = new Session(LoginInterface.this); //save user's login info into a session
                                    session.createRememberMeSession(emailInput,passwordInput);
                                }
                                Toast.makeText(LoginInterface.this,"Logged In Successfully",Toast.LENGTH_SHORT).show(); //display login successful message
                                Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.MainMenu.class);
                                startActivity(intent); //enter main menu
                            }
                            else{ //login failed
                                Toast.makeText(LoginInterface.this,"Error! Login Failed"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                loginPB.setVisibility(View.INVISIBLE); //hide progress bar
                            }
                        }
                    });
                }
            }
        });
    }

    public void callMainActivity(View view){ //call and enter home page (main activity)

        Intent intent = new Intent(getApplication().getApplicationContext(),MainActivity.class);
        startActivity(intent); //enter home page
    }

    public void callForgotPasswordActivity(View view){ //call and enter forgot password interface

        Intent intent = new Intent(getApplication().getApplicationContext(),ForgotPasswordInterface.class);
        startActivity(intent); //enter forgot password interface
    }

    public void callRegisterInterface(View view){ //call and enter register interface

        Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.RegisterInterface.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.registerButton),"login_button_trans");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            //android version is lolipop and onwards
            //initialize transition animation
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginInterface.this,pairs);
            startActivity(intent, options.toBundle()); //enter register interface with transition animation
        }
        else
        { //android version older than lolipop
            startActivity(intent); //enter register interface
        }

    }
}