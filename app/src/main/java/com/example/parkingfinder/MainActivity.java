package com.example.parkingfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener {

    ImageButton mic, settings;
    SharedPreferences sharedPreferences;
    TTS tts;
    MaterialButton saveLocation, map, login, register;
    public double currentLat,currentLon;

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        tts = new TTS(this);

        mic = findViewById(R.id.microphone);
        settings = findViewById(R.id.settingsButton);
        saveLocation = findViewById(R.id.save);
        map = findViewById(R.id.map);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    saveLocation.setVisibility(View.VISIBLE);
                    map.setVisibility(View.VISIBLE);
                    mic.setVisibility(View.VISIBLE);
                    settings.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);
                    register.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, "Login to continue", Toast.LENGTH_SHORT).show();
                }
            }
        };

        //asking for permission to use location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            if (lm != null) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
//            Toast.makeText(this, "Waiting for GPS connection!", Toast.LENGTH_SHORT).show();
        }

    }

    public void openLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void openMaps(View view) {
        int carId = sharedPreferences.getInt("defaultCar", 0);

        if (carId > 0) {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("radioBtnValue", carId);
            startActivity(intent);
        }else{
            Toast.makeText(MainActivity.this, "Can not open map without a default vehicle selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    
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
            if (str.contains("save position")||str.contains("save location")) {
                saveLocation.performClick();
            }
            else if (str.contains("open map")||str.contains("open maps")) {
                map.performClick();
            }
        }
    }

    public void saveLocation(View view) {
        int carId = sharedPreferences.getInt("defaultCar", 0);

        if ((carId > 0)&&(carId != 420)) {
            Map<String, Object> location = new HashMap<>();
            final Date currentTime = Calendar.getInstance().getTime();
            location.put("latitude", currentLat);
            location.put("longitude", currentLon);
            location.put("vehicleId", carId);
            location.put("time",currentTime);
            db.collection("locations")
                    .add(location)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(MainActivity.this, "Saved location", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error saving location", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(MainActivity.this, "Can't save a location without a default vehicle selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
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
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
