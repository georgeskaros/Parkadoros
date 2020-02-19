package com.example.parkingfinder;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.opencensus.internal.Utils;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<LatLng> listPoints;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = MainActivity.class.getSimpleName();
    double lat = 2.2;
    double lng = 2.2;
    String vehicleType = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listPoints = new ArrayList<>();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.9),(int)(height*.7));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);

        db.collection("locations")
                .whereEqualTo("vehicleType","Vehicle 2")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            //the following block of code is to find the biggest date that fits the where clause that i had put
                            //first we take the current date and put it to a variable
                            final Date currentDate = Calendar.getInstance().getTime();
                            Date minDate = currentDate;
                            //with this for loop we find the oldest date there is in our results
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Date dateTemp = document.getDate("time");
                                if(minDate.compareTo(dateTemp)>0){
                                    minDate = dateTemp;
                                }
                            }
                            //we put the minDate to a maxDate to do the reverse
                            //when we find a date greater than maxDate we put that date on max date
                            //and we store latitude, longitude, and vehicleType to some variables so we can use them later
                            // to make a marker for the most resent entry of that vehicle type
                            Date maxDate = minDate;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Date dateTemp = document.getDate("time");
                                if(maxDate.compareTo(dateTemp)<0){
                                    maxDate = dateTemp;
                                    lat = document.getDouble("latitude");
                                    lng = document.getDouble("longitude");
                                    vehicleType = document.getString("vehicleType");
                                }
                            }
                            LatLng location = new LatLng(lat,lng);
                            MarkerOptions markerOptions = new MarkerOptions();                   //create marker options class so we can ad features to it
                            markerOptions.position(location);
                            markerOptions.title(vehicleType);                                   //setting a title to the marker , now the title is
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
                            Marker locationMarker = mMap.addMarker(markerOptions);
                            locationMarker.setDraggable(true);
                            locationMarker.showInfoWindow();
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,14.0f));

                            ///////////////////////////////////////////////////////
                            //code to use in order to print all entries to the DB//
                            ///////////////////////////////////////////////////////
                            /*for (QueryDocumentSnapshot document : task.getResult()) {
                               // if (document.getLong("time")>maxTime){Toast.makeText(MapsActivity.this, "ok", Toast.LENGTH_SHORT).show();}else{
                                LatLng location = new LatLng(document.getDouble("latitude"),document.getDouble("longitude"));       //get location from db
                                String vehicle = document.getString("vehicleType");
                                MarkerOptions markerOptions = new MarkerOptions();                   //create marker options class so we can ad features to it
                                markerOptions.position(location);
                                markerOptions.title(vehicle);               //setting a title to each of the markers , now the title is the LatLng
                                markerOptions.snippet("comment");               //can put anything in here , this is like a comment to the title
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));       //set a car icon
                                //Marker locationmarker = mMap.addMarker(new MarkerOptions().position(location));
                                Marker locationMarker = mMap.addMarker(markerOptions);
                                locationMarker.setDraggable(true);
                                locationMarker.showInfoWindow();
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,14.0f));
                            }*/
                        } else {
                            Toast.makeText(MapsActivity.this, "Can't find a saved location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
