package com.example.parkingfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    TTS tts;
    MaterialButton saveLocation, map;
    Button car;
    public double currentLat,currentLon;
    TextView txt, gpsConnection ;
    EditText numOfCars;
    RadioGroup allButtons;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveLocation = findViewById(R.id.save);
        map = findViewById(R.id.map);
        gpsConnection = findViewById(R.id.gpsConnection);
        map = findViewById(R.id.map);

        numOfCars = findViewById(R.id.txtcars);      //edit text for number of cars
        car = findViewById(R.id.car);                //button for putting the appropriate number of radio buttons
        allButtons = findViewById(R.id.radiogroup);  //radio group

        tts = new TTS(this);

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = numOfCars.getText().toString().trim();
                if (temp.matches("")) {
                    Toast.makeText(MainActivity.this, "You did not enter a number", Toast.LENGTH_SHORT).show();
                }else{
                    car.setEnabled(false);
                    int number = Integer.parseInt(numOfCars.getText().toString().trim());
                    addRadioButtons(number);
                }
            }
        });
        
        //asking for permission to use location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            if (lm != null) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
            Toast.makeText(this, "Waiting for GPS connection!", Toast.LENGTH_SHORT).show();
        }

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

    //Speech Recognition Methods
    public void speechRec(View view){
        tts.speak("How can I help you?");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please say something!");
        startActivityForResult(intent,742);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==742 && resultCode==RESULT_OK){
            getWordFromResult(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
        }
    }

    private void getWordFromResult(ArrayList<String> results) {
        for (String str : results) {
            if (str.equals("save position")) {
                saveLocation.performClick();
            }
            else if (str.equals("open map")) {
                map.performClick();
            }
        }
    }

    public void openMaps(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void saveLocation(View view) {
        Map<String, Object> location = new HashMap<>();
        location.put("latitude", currentLat);
        location.put("longitude", currentLon);

        db.collection("locations")
            .add(location)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "Added location with id: " + documentReference.getId());
                }

            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding location", e);
                }
            });
    }

    @Override
    public void onLocationChanged(Location location) {
        //get on location change the current lat and lon
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();
        if (currentLat != 0) {
            saveLocation.setEnabled(true);
        }
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
