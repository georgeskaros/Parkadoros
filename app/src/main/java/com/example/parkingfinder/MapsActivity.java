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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import io.opencensus.internal.Utils;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<LatLng> listPoints;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = MainActivity.class.getSimpleName();

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
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;      //demo code to check if it works
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LatLng location = new LatLng(document.getDouble("latitude"),document.getDouble("longitude"));       //get location from db
                                String vehicleType = document.getString("vehicleType");
                                MarkerOptions markerOptions = new MarkerOptions();                   //create marker options class so we can ad features to it
                                markerOptions.position(location);
                                markerOptions.title(vehicleType);               //setting a title to each of the markers , now the title is the LatLng
                                i++;                                                    //demo code to check
                                markerOptions.snippet(String.valueOf(i));               //can put anything in here , this is like a comment to the title
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));       //set a car icon
                                //Marker locationmarker = mMap.addMarker(new MarkerOptions().position(location));
                                Marker locationMarker = mMap.addMarker(markerOptions);
                                locationMarker.setDraggable(true);
                                locationMarker.showInfoWindow();
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,14.0f));
                            }
                        } else {
                            Toast.makeText(MapsActivity.this, "Can't find a saved location", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }
}
