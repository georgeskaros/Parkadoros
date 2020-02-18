package com.example.parkingfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener {
    //initiate button ,text view and two public variables for current
    Button btn,map,mic,car;
    public double currentlat,currentlon;
    TextView txt ;
    TextToSpeech txtSp;
    SpeechRecognizer speech;
    EditText numofcars;
    RadioGroup allButtons;
    String TAG = "TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button)findViewById(R.id.btn);
        txt = (TextView)findViewById(R.id.txt);
        map = (Button)findViewById(R.id.map);
        mic = (Button)findViewById(R.id.mic);

        numofcars = findViewById(R.id.txtcars);      //edit text for number of cars
        car = findViewById(R.id.car);                //button for putting the appropriate number of radio buttons
        allButtons = findViewById(R.id.radiogroup);  //radio group

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MapsActivity.class));
            }
        });

        //asking for permission to use location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            doStuff();

        }

        txtSp = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR){
                    txtSp.setLanguage(Locale.UK);
                }
            }
        });

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = numofcars.getText().toString().trim();
                if (temp.matches("")) {
                    Toast.makeText(MainActivity.this, "You did not enter a number", Toast.LENGTH_SHORT).show();
                }else{
                    car.setEnabled(false);
                    int number = Integer.parseInt(numofcars.getText().toString().trim());
                    addRadioButtons(number);
                }
            }
        });
        //initiate the function of the button


        clickbtn();
        clickmic();
        initializeSpeechRecognizer();

    }


    public void addRadioButtons(int number){
        allButtons.setOrientation(LinearLayout.VERTICAL);
        for (int i = 1; i <= number; i++) {
            RadioButton rdbtn = new RadioButton(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rdbtn.setId(View.generateViewId());
            }
            rdbtn.setText("Vehicle " + rdbtn.getId());
            rdbtn.setOnClickListener(this);
            allButtons.addView(rdbtn);

        }


    }
    private void clickmic(){
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSp.speak("Hello",TextToSpeech.QUEUE_FLUSH,null);
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
                speech.startListening(intent);

            }
        });
    }

    private void initializeSpeechRecognizer(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        }
            if(SpeechRecognizer.isRecognitionAvailable(this)){
                speech = SpeechRecognizer.createSpeechRecognizer(this);
                speech.setRecognitionListener(new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle params) {

                    }

                    @Override
                    public void onBeginningOfSpeech() {

                    }

                    @Override
                    public void onRmsChanged(float rmsdB) {

                    }

                    @Override
                    public void onBufferReceived(byte[] buffer) {

                    }

                    @Override
                    public void onEndOfSpeech() {

                    }

                    @Override
                    public void onError(int error) {

                    }

                    @Override
                    public void onResults(Bundle results) {
                        List<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        processResult(result.get(0));
                    }

                    @Override
                    public void onPartialResults(Bundle partialResults) {

                    }

                    @Override
                    public void onEvent(int eventType, Bundle params) {

                    }
                });
            }
    }

    private void processResult(String command) {
        command = command.toLowerCase();
        if(command.contains("open")||command.contains("view")||command.contains("show")){
            if(command.contains("map")){
                txtSp.speak("Sure bro", TextToSpeech.QUEUE_FLUSH,null);
                map.performClick();
                map.setPressed(true);
                map.invalidate();
                map.setPressed(false);
                map.invalidate();

        }else {
            txtSp.speak("Could you try again? Maybe i heard something wrong.", TextToSpeech.QUEUE_FLUSH, null);
        }

        }
    }

    private void clickbtn(){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on button click print on text view the current values
                txt.setText(currentlat +" "+ currentlon);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        //get on location change the current lat and lon
        currentlat = location.getLatitude();
        currentlon = location.getLongitude();
    }

    private void doStuff(){
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (lm != null){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
            //commented, this is from the old version
            // this.onLocationChanged(null);
        }
        Toast.makeText(this,"Waiting for GPS connection!", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {
       // Log.d(TAG, " Name " + ((RadioButton)v).getText() +" Id is "+v.getId());
    }
}
