package com.example.healthsmast;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.net.URL;
import java.util.regex.Pattern;

public class CodeScanner extends AppCompatActivity {

    Button scanButton;
    String url ="";
    String searchContent = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scanner);

        scanButton = findViewById(R.id.bt_scan);

        scanButton.setOnClickListener(new View.OnClickListener() { //when scan button is clicked
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntergrator = new IntentIntegrator(CodeScanner.this);
                intentIntergrator.setPrompt("Use volume up key to toggle flash"); //prompt text
                intentIntergrator.setBeepEnabled(true); //enable beep sound
                intentIntergrator.setOrientationLocked(true); //lock the camera orientation
                intentIntergrator.setCaptureActivity(com.example.healthsmast.Capture.class); //start the camera
                intentIntergrator.initiateScan(); //start to scan

            }
        });
    }

    public static boolean checkURL(CharSequence input) {
        if (TextUtils.isEmpty(input)) //if there is no text detected
            return false;
        Pattern URL_PATTERN = Patterns.WEB_URL;
        boolean isURL = URL_PATTERN.matcher(input).matches();
        if (!isURL) { String urlString = input + "";
            if (URLUtil.isNetworkUrl(urlString))
            {// if the text detected is URL
                try { new URL(urlString); isURL = true;
                }
                catch (Exception e)
                { }
            }
        } return isURL; //return boolean value of whether the detected value is URL or not
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (intentResult.getContents() != null){
            //when result is not null initialize alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(CodeScanner.this);
            builder.setMessage(intentResult.getContents());
            builder.setPositiveButton("Open in Browser", new DialogInterface.OnClickListener() { //launch browser
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (checkURL(intentResult.getContents())) { //if the detected value is URL
                        url = intentResult.getContents().toString();
                        Intent in = new Intent(Intent.ACTION_VIEW);
                        in.setData(Uri.parse(url));
                        startActivity(in); //launch URL in the browser
                    }
                    else{ //if detected value is not a URL
                        searchContent = intentResult.getContents().toString();
                        Intent in = new Intent(Intent.ACTION_WEB_SEARCH);
                        in.putExtra(SearchManager.QUERY, searchContent);
                        startActivity(in); //search for the detected value in the browser
                    }

                    dialogInterface.dismiss();
                }
            });
            //show alert dialog
            builder.show();
        }else{
            //when result content is null display toast
            Toast.makeText(getApplicationContext(), "Barcode not detected",Toast.LENGTH_SHORT).show();

        }
    }

    public void callMainMenu(View view){ //call and enter main menu

        Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.MainMenu.class);
        startActivity(intent); //enter main menu
    }

}