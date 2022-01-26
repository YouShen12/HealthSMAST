package com.example.healthsmast;

import android.Manifest;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class NameScanner extends AppCompatActivity {

    private Button captureButton, detectButton, searchButton;
    private ImageView inputImg;
    private TextView inputTxt;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;
    private String fullText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_scanner);
        captureButton = findViewById(R.id.captureImage);
        detectButton = findViewById(R.id.detectText);
        searchButton = findViewById(R.id.Search);
        inputImg = findViewById(R.id.inputImage);
        inputTxt = findViewById(R.id.inputText);

        //request camera permission from user
        if (ContextCompat.checkSelfPermission(NameScanner.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(NameScanner.this, new String[]{
                    Manifest.permission.CAMERA
            },100);
        }

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            } //capture image
        });

        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectText();
            } //detect text from image
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browserSearch();
            } //search the detected text using the browser
        });
    }


    //capture image
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    //set captured image into bitmap format
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            inputImg.setImageBitmap(imageBitmap);
        }
    }

    //search for detected text in browser
    public void browserSearch(){
        if (fullText != ""){
            String searchContent = inputTxt.getText().toString();
            Intent in = new Intent(Intent.ACTION_WEB_SEARCH);
            in.putExtra(SearchManager.QUERY, searchContent);
            startActivity(in);
        }
        else
        {
            Toast.makeText(NameScanner.this, "No text detected", Toast.LENGTH_SHORT).show();
        }
    }

    //detect text from the image using firebase machine learning kit
    private void detectText() {
        try{ //if image is taken
            InputImage image = InputImage.fromBitmap(imageBitmap, 0);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                @Override
                public void onSuccess(@NonNull Text text) { //when text is detected
                    fullText = "";
                    StringBuilder result = new StringBuilder();
                    for (Text.TextBlock block: text.getTextBlocks()){ //for every line of text detected
                        String blockText = block.getText();
                        fullText = fullText + "\n" + blockText; //combine the current line with the previous line
                        Point[] blockCornerPoint = block.getCornerPoints();
                        Rect blockFrame = block.getBoundingBox();
                        for(Text.Line line : block.getLines()){
                            String lineText = line.getText();
                            Point[] lineCornerPoint = line.getCornerPoints();
                            Rect linRect = line.getBoundingBox();
                            for(Text.Element element: line.getElements()){
                                String elementText = element.getText();
                                result.append(elementText);
                            }
                            inputTxt.setText(fullText); //display the detected text
                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() { //when text detection failed
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NameScanner.this, "Fail to detect text from image", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e){ //if image is not taken
            Toast.makeText(NameScanner.this, "No image taken", Toast.LENGTH_SHORT).show();
        }

    }

    public void callMainMenu(View view){ //call and enter main menu

        Intent intent = new Intent(getApplication().getApplicationContext(), com.example.healthsmast.MainMenu.class);
        startActivity(intent); //enter main menu
    }
}