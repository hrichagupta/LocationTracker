package com.example.android.locationtracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivityPlaces extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    int flag = 0;
    int flagForNetworking = 10;
    int flagForNoJsonResult = 0;
    int flagForMapUpdate = 0;

    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Location currentLocation;
    int checking;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String TAG = "MapsActivityPlaces";


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_places2);

        checking = getIntent().getIntExtra("checkPosition", 1000000);
        googleApiClient = new GoogleApiClient.Builder(MapsActivityPlaces.this).
                addApi(LocationServices.API).
                addConnectionCallbacks(MapsActivityPlaces.this).
                addOnConnectionFailedListener(MapsActivityPlaces.this).
                build();

        locationRequest = new LocationRequest().
                setInterval(3 * 1000).
                setFastestInterval(1 * 1000).
                setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toLocationOn();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        flag = 1;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocation();
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MapsActivityPlaces.this, "Connection Suspended !", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MapsActivityPlaces.this, "Connection Failed !", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if (flagForMapUpdate == 0) {
            updateMap();
        }

    }

    private void updateMap() {
        if (flag == 1) {
            mMap.clear();
            if (flagForNetworking == 10) {
                forParsing();
                flagForNetworking = 100;
            }
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.radius(700);
            Log.d("circle", String.valueOf(latLng));
            circleOptions.strokeColor(Color.BLUE);
            circleOptions.fillColor(0X110000ff);
            circleOptions.strokeWidth(2);
            flagForMapUpdate = 1;
            mMap.addCircle(circleOptions);
            mMap.addMarker(new MarkerOptions().position(latLng).title("I'm here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f), 4000, null);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapsActivityPlaces.this, NearByPlacesActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    protected void forParsing() {
        if (flag == 1) {

            AndroidNetworking.initialize(getApplicationContext());
            AndroidNetworking.get(jsonString())
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONArray results = response.getJSONArray("results");

                                for (int i = 0; i < results.length(); i++) {
                                    JSONObject objName = results.getJSONObject(i);
                                    String title = objName.getString("name");
                                    JSONObject objGeometry = objName.getJSONObject("geometry");
                                    JSONObject objLocation = objGeometry.getJSONObject("location");
                                    String strLat = objLocation.getString("lat");
                                    String strLng = objLocation.getString("lng");
                                    double lat = Double.parseDouble(strLat);
                                    double lng = Double.parseDouble(strLng);
                                    LatLng latLng = new LatLng(lat, lng);
                                    mMap.addMarker(new MarkerOptions().position(latLng).title(title));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(MapsActivityPlaces.this, "JSON Parsing Exception " + e, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(MapsActivityPlaces.this, "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    public String jsonString() {
        String str = "", returnStr;

        Log.d("check", "" + checking);

        if (checking == 0)
            str = "accounting";
        else if (checking == 1)
            str = "airport";
        else if (checking == 2)
            str = "aquarium";
        else if (checking == 3)
            str = "atm";
        else if (checking == 4)
            str = "bakery";
        else if (checking == 5)
            str = "bank";
        else if (checking == 6)
            str = "bar";
        else if (checking == 7)
            str = "beauty_salon";
        else if (checking == 8)
            str = "bus_station";
        else if (checking == 9)
            str = "cafe";
        else if (checking == 10)
            str = "church";
        else if (checking == 11)
            str = "city_hall";
        else if (checking == 12)
            str = "clothing_store";
        else if (checking == 13)
            str = "dentist";
        else if (checking == 14)
            str = "doctor";
        else if (checking == 15)
            str = "electrician";
        else if (checking == 16)
            str = "electronics_store";
        else if (checking == 17)
            str = "fire_station";
        else if (checking == 18)
            str = "gas_station";
        else if (checking == 19)
            str = "gym";
        else if (checking == 20)
            str = "hair_care";
        else if (checking == 21)
            str = "hardware_store";
        else if (checking == 22)
            str = "hindu_temple";
        else if (checking == 23)
            str = "home_goods_store";
        else if (checking == 24)
            str = "hospital";
        else if (checking == 25)
            str = "jewelry_store";
        else if (checking == 26)
            str = "laundry";
        else if (checking == 27)
            str = "lawyer";
        else if (checking == 28)
            str = "library";
        else if (checking == 29)
            str = "mosque";
        else if (checking == 30)
            str = "movie_theater";
        else if (checking == 31)
            str = "moving_company";
        else if (checking == 32)
            str = "museum";
        else if (checking == 33)
            str = "night_club";
        else if (checking == 34)
            str = "park";
        else if (checking == 35)
            str = "parking";
        else if (checking == 36)
            str = "pet_store";
        else if (checking == 37)
            str = "pharmacy";
        else if (checking == 38)
            str = "police";
        else if (checking == 39)
            str = "post_office";
        else if (checking == 40)
            str = "restaurant";
        else if (checking == 41)
            str = "school";
        else if (checking == 42)
            str = "shoe_store";
        else if (checking == 43)
            str = "shopping_mall";
        else if (checking == 44)
            str = "supermarket";
        else if (checking == 45)
            str = "train_station";


        returnStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&radius=1500&type=" + str + "&keyword=&key=AIzaSyANkzBK35nwdBIG2KYlQic5UBGVYnKIpfA";
        return returnStr;
    }

    private void toLocationOn() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MapsActivityPlaces.this, REQUEST_CHECK_SETTINGS);
                        } catch (Exception e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }
}


