package com.example.parkingfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Button saveNumber, saveCarId;
    EditText numberOfCars;
    RadioGroup allButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        saveCarId = findViewById(R.id.saveCarId);
        numberOfCars = findViewById(R.id.numberOfCars);         //edit text for number of cars
        saveNumber = findViewById(R.id.saveNum);                //button for putting the appropriate number of radio buttons
        allButtons = findViewById(R.id.radiogroup);             //radio group

        int carNum = sharedPreferences.getInt("cars", 1);
        if (carNum > 1) {
            addRadioButtons(carNum);
            saveCarId.setVisibility(View.VISIBLE);
        }
    }

    public void saveCars(View view) {
        if (numberOfCars.getText() != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("cars", Integer.parseInt(numberOfCars.getText().toString()));
            editor.apply();

            Intent refresh = new Intent(this, SettingsActivity.class);
            finish();
            startActivity(refresh);

            Toast.makeText(this, "You can use " + sharedPreferences.getInt("cars", 1) + " cars", Toast.LENGTH_SHORT ).show();
        }
        else {
            Toast.makeText(this, "You have to enter a number first", Toast.LENGTH_SHORT ).show();
        }
    }

    public void setGreek(View view) {
        setLocale("el");
        Toast.makeText(this, "Χρησιμοποιείτε Ελληνικά", Toast.LENGTH_SHORT).show();
    }

    public void setEnglish(View view) {
        setLocale("en");
        Toast.makeText(this, "Using English", Toast.LENGTH_SHORT).show();
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);
    }

    //for the db to take the type of the vehicle , IF return is Null there is no button selected , IF return is string then there is a button selected
    public void getVehicleId(View view){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (allButtons.getCheckedRadioButtonId() != -1)
        {
            int selectedId = allButtons.getCheckedRadioButtonId();
            editor.putInt("defaultCar", selectedId);
            editor.apply();

            if (selectedId != 420)
                Toast.makeText(this, "You are using car " + sharedPreferences.getInt("defaultCar", 0), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "You are viewing all your car parking spots", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "You must choose a vehicle first", Toast.LENGTH_SHORT).show();
        }
    }

    public void addRadioButtons(int number){
        allButtons.setOrientation(LinearLayout.VERTICAL);
        for (int i = 1; i <= number; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(i);
            radioButton.setText("Vehicle " + radioButton.getId());
            allButtons.addView(radioButton);
            saveCarId.setVisibility(View.VISIBLE);
            if (sharedPreferences.getInt("cars", 1) > 1){
                if (sharedPreferences.getInt("cars", 1) == i) {
                    radioButton.setChecked(true);
                }
            }
        }

        int allParkingSpots = 420;
        RadioButton rb = new RadioButton(this);
        rb.setId(allParkingSpots);
        rb.setText("All parking spots");
        allButtons.addView(rb);
    }
}
