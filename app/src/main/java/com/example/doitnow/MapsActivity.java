package com.example.doitnow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.doitnow.adapters.TodosRecyclerAdapter;
import com.example.doitnow.databinding.ActivityMainBinding;
import com.example.doitnow.databinding.ActivityMapsBinding;
import com.example.doitnow.db.AppDatabase;
import com.example.doitnow.helpers.GeofenceHelper;
import com.example.doitnow.models.TodoItem;
import com.example.doitnow.utils.DatabaseInitializer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final String TAG = "MapsActivity";
    private static final int DEFAULT_ZOOM = 16;

    private ActivityMapsBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    // The geographical location where the device is currently located
    private Location lastLocation = null;

    TodosRecyclerAdapter adapter;
    List<TodoItem> todosList;
    ActivityMainBinding activityMainBinding;

    private float GEOFENCE_RADIUS = 150;    // meters
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        // Get the recycler view adapter:
        todosList = new ArrayList<>();
        adapter = (TodosRecyclerAdapter) activityMainBinding.recyclerView.getAdapter();

        setListeners();
    }

    /**
     * This callback is triggered when the map is ready to be used.
     * Firstly a marker on the current location is added.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        enableUserLocation();
        mMap.setOnMapClickListener(this);
    }

    @SuppressLint("MissingPermission")
    private void initMap(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        lastLocation = location;
                        LatLng lastKnownLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, DEFAULT_ZOOM));
                    }
                });
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initMap();
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Log.d(TAG, "Permissions granted!");
                initMap();
            } else {
                Log.e(TAG, "No permissions granted!");
            }
        }
        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "Background permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission
                Toast.makeText(this, "ERROR: Background location access is necessary for geofences to trigger!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setListeners() {

        binding.submitItem.setOnClickListener(view1 -> {
            List<Boolean> pass = new ArrayList<>();
            // input validation
            if (binding.editTextTitle.getText() != null) {
                if (binding.editTextTitle.getText().toString().trim().length() == 0) {
                    binding.editTextTitle.setError("Required");
                    pass.add(false);
                }
            }
            if (binding.editTextDescription.getText() != null) {
                if (binding.editTextDescription.getText().toString().trim().length() == 0) {
                    binding.editTextDescription.setError("Required");
                    pass.add(false);
                }
            }
            if (binding.editTextLatitude.getText() != null) {
                if (binding.editTextLatitude.getText().toString().trim().length() == 0) {
                    pass.add(false);
                }
            }
            if (binding.editTextLongitude.getText() != null) {
                if (binding.editTextLongitude.getText().toString().trim().length() == 0) {
                    pass.add(false);
                }
            }
            if (pass.size() == 0) {
                // validation pass, create entity record
                String title = binding.editTextTitle.getText().toString().trim();
                String description = binding.editTextDescription.getText().toString().trim();
                String latitude = binding.editTextLatitude.getText().toString().trim();
                String longitude = binding.editTextLongitude.getText().toString().trim();

                // adding geofence for location with Lat: latitude and Lng: longitude:
                String geofenceID = addGeofence(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), GEOFENCE_RADIUS, title);

                // adding new TodoItem in the array and the database:
                DatabaseInitializer.populateAsync(AppDatabase.getAppDatabase(), todosList, new TodoItem(title, description, geofenceID));

                // back to MainActivity
                Intent returnIntent = new Intent(MapsActivity.this, MainActivity.class);
                returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                MapsActivity.this.startActivity(returnIntent);
            }
        });
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapClick(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }
        } else {
            handleMapClick(latLng);
        }
    }

    private void handleMapClick(LatLng latLng) {
        addMarker(latLng);
//        addCircle(latLng, GEOFENCE_RADIUS);
        // fill the latitude and longitude input fields:
        binding.editTextLatitude.setText(String.valueOf(latLng.latitude));
        binding.editTextLongitude.setText(String.valueOf(latLng.longitude));
    }

    @SuppressLint("MissingPermission")
    private String addGeofence(LatLng latLng, float radius, String title) {

        String geofenceRequestID = UUID.randomUUID().toString() + title.replace(" ", "-");
        Geofence geofence = geofenceHelper.getGeofence(geofenceRequestID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        Log.d(TAG, "addGeofence: ID: " + geofenceRequestID + "Request ID: "+ geofence.getRequestId());

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: Geofence Added..."))
                .addOnFailureListener(e -> {
                    String errorMessage = geofenceHelper.getErrorString(e);
                    Log.d(TAG, "onFailure: " + errorMessage);
                });

        return geofenceRequestID;
    }

    private void addMarker(LatLng latLng) {
        if (mMap != null){
            mMap.clear();
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        }
    }

//    private void addCircle(LatLng latLng, float radius) {
//        CircleOptions circleOptions = new CircleOptions();
//        circleOptions.center(latLng);
//        circleOptions.radius(radius);
//        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
//        circleOptions.fillColor(Color.argb(64, 255, 0,0));
//        circleOptions.strokeWidth(4);
//        mMap.addCircle(circleOptions);
//    }

}
