package com.example.healthsmast;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void callLoginInterface(View view){ //call and enter login interface

        Intent intent = new Intent(getApplication().getApplicationContext(),LoginInterface.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.login_button),"login_button_trans");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            //android version is lolipop and onwards
            //initialize transition animation
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
            startActivity(intent, options.toBundle()); //enter login interface with transition animation
        }
        else
        { //android version older than lolipop
            startActivity(intent); //enter login interface
        }

    }

    public void callRegisterInterface(View view){ //call and enter register interface

        Intent intent = new Intent(getApplication().getApplicationContext(),RegisterInterface.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.register_button),"login_button_trans");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            //android version is lolipop and onwards
            //initialize transition animation
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
            startActivity(intent, options.toBundle()); //enter register interface with transition animation
        }
        else
        { //android version older than lolipop
            startActivity(intent); //enter register interface
        }

    }
}