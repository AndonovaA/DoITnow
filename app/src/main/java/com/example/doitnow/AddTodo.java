package com.example.doitnow;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.doitnow.adapters.TodosRecyclerAdapter;
import com.example.doitnow.databinding.CustomInfoContentsBinding;
import com.example.doitnow.databinding.ListTodosBinding;
import com.example.doitnow.databinding.TodoAddBinding;
import com.example.doitnow.db.AppDatabase;
import com.example.doitnow.models.TodoItem;
import com.example.doitnow.utils.DatabaseInitializer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class refers to the SecondFragment.
 */
public class AddTodo extends Fragment {

    private static final String TAG = AddTodo.class.getSimpleName();

    private TodoAddBinding binding;
    private ListTodosBinding todosBinding;
    TodosRecyclerAdapter adapter;
    List<TodoItem> todosList;

    private LocationManager locationManager;

    private static final int LOCATION_MIN_UPDATE_TIME = 10;
    private static final int LOCATION_MIN_UPDATE_DISTANCE = 1000;
    private static final int DEFAULT_ZOOM = 13;

    private MapView mapView;
    private GoogleMap googleMap;
    // The geographical location where the device is currently located.
    private Location location = null;

    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] likelyPlaceNames;
    private String[] likelyPlaceAddresses;
    private List[] likelyPlaceAttributions;
    private LatLng[] likelyPlaceLatLongs;

    CustomInfoContentsBinding infoWindowBinding;


    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            drawMarker(location, getText(R.string.your_location).toString());
            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = TodoAddBinding.inflate(inflater, container, false);
        todosBinding = ListTodosBinding.inflate(inflater, container, false);
        infoWindowBinding = CustomInfoContentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(savedInstanceState);
        setListeners();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_nearby_places){
            showNearbyPlaces();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init (Bundle savedInstanceState) {
        // Construct a PlacesClient
        Places.initialize(App.mContext, getResources().getString(R.string.google_maps_key));
        placesClient = Places.createClient(App.mContext);

        // Construct a FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(App.mContext);

        // initialize the Location Manager:
        Toast.makeText(App.mContext,"Detecting current location..",Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) App.mContext.getSystemService(Context.LOCATION_SERVICE);
        mapView = binding.maps;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::mapView_onMapReady);

        // Get the recycler view adapter:
        todosList = new ArrayList<>();
        adapter = (TodosRecyclerAdapter) todosBinding.recyclerView.getAdapter();
    }

    private void setListeners () {

        binding.submitItem.setOnClickListener(view1 -> {
            List<Boolean> pass = new ArrayList<>();
            // input validation
            if (binding.editTextTitle.getText().toString().trim().length() == 0) {
                binding.editTextTitle.setError("Required");
                pass.add(false);
            }
            if (binding.editTextDescription.getText().toString().trim().length() == 0) {
                binding.editTextDescription.setError("Required");
                pass.add(false);
            }
            if(pass.size() == 0 ){
                // validation pass, create entity record
                String title = binding.editTextTitle.getText().toString().trim();
                String description = binding.editTextDescription.getText().toString().trim();

                //dodavanje na nov podatok za TodoItem i vo listata i vo bazata
                // TODO: change location
                DatabaseInitializer.populateAsync(AppDatabase.getAppDatabase(), todosList, new TodoItem(title, description, ""));

                // back to the first fragment
                NavHostFragment.findNavController(AddTodo.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    private void initMap() {
        if (ContextCompat.checkSelfPermission(App.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(binding.maps.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setAllGesturesEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                // set onClick listener for the map:
                googleMap.setOnMapClickListener(latLng -> {
                    googleMap.clear();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    googleMap.addMarker(markerOptions);
                    // TODO: zemi lokacija od ovde
                });
            }
        } else {
            if (ContextCompat.checkSelfPermission(App.mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
            }
            if (ContextCompat.checkSelfPermission(App.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 13);
            }
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(App.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(App.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(App.mContext, getText(R.string.provider_failed), Toast.LENGTH_LONG).show();
            } else {
                location = null;
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_MIN_UPDATE_TIME, LOCATION_MIN_UPDATE_DISTANCE, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_MIN_UPDATE_TIME, LOCATION_MIN_UPDATE_DISTANCE, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (location != null) {
                    drawMarker(location, getText(R.string.your_location).toString());
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(App.mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
            }
            if (ContextCompat.checkSelfPermission(App.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 13);
            }
        }
    }

    private void showNearbyPlaces() {
        if (ContextCompat.checkSelfPermission(App.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(binding.maps.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {

                // Use fields to define the data types to return.
                List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                        Place.Field.LAT_LNG);

                // Use the builder to create a FindCurrentPlaceRequest.
                FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

                // Get the likely places - that is, the businesses and other points of interest that
                // are the best match for the device's current location.
                final Task<FindCurrentPlaceResponse> placeResult = placesClient.findCurrentPlace(request);

                placeResult.addOnCompleteListener (task -> {
                    if (task.isSuccessful() && task.getResult() != null) {

                        FindCurrentPlaceResponse likelyPlaces = task.getResult();

                        // Set the count, handling cases where less than 5 entries are returned.
                        int count;
                        if (likelyPlaces.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                            count = likelyPlaces.getPlaceLikelihoods().size();
                        } else {
                            count = M_MAX_ENTRIES;
                        }

                        int i = 0;
                        likelyPlaceNames = new String[count];
                        likelyPlaceAddresses = new String[count];
                        likelyPlaceAttributions = new List[count];
                        likelyPlaceLatLongs = new LatLng[count];

                        for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                            // Build a list of likely places to show the user.
                            likelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                            likelyPlaceAddresses[i] = placeLikelihood.getPlace().getAddress();
                            likelyPlaceAttributions[i] = placeLikelihood.getPlace()
                                    .getAttributions();
                            likelyPlaceLatLongs[i] = placeLikelihood.getPlace().getLatLng();
                            i++;
                            if (i > (count - 1)) {
                                break;
                            }
                        }

                        // Show a dialog offering the user the list of likely places, and add a
                        // marker at the selected place.
                        openPlacesDialog();
                    }
                    else {
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                });
            }
        } else {
            if (ContextCompat.checkSelfPermission(App.mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
            }
            if (ContextCompat.checkSelfPermission(App.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 13);
            }
        }
    }

    private void openPlacesDialog() {
        // Ask the user to choose place from suggested nearby places

        DialogInterface.OnClickListener listener = (dialog, which) -> {
            // The "which" argument contains the position of the selected item.
            LatLng markerLatLng = likelyPlaceLatLongs[which];
            String markerSnippet = likelyPlaceAddresses[which];

            if (likelyPlaceAttributions[which] != null) {
                markerSnippet = markerSnippet + "\n" + likelyPlaceAttributions[which];
            }

            // Add a marker for the selected place, with an info window
            // showing information about that place.
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions()
                    .title(likelyPlaceNames[which])
                    .position(markerLatLng)
                    .snippet(markerSnippet));

            // Position the map's camera at the location of the marker.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                    DEFAULT_ZOOM));

            // TODO: zemi lokacija od ovde
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Pick a place")
                .setItems(likelyPlaceNames, listener)
                .show();
    }

    private void mapView_onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        initMap();
        getCurrentLocation();

        // Use a custom info window adapter to handle multiple lines of text in the info window contents
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(@NonNull Marker arg0) {
                return null;
            }
            @Override
            public View getInfoContents(@NonNull Marker marker) {
                infoWindowBinding.title.setText(marker.getTitle());
                infoWindowBinding.snippet.setText(marker.getSnippet());
                return infoWindowBinding.getRoot();
            }
        });
    }

    private void drawMarker(Location location, String title) {
        if (this.googleMap != null) {
            googleMap.clear();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(title);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        getCurrentLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mapView.onDestroy();
    }
}