package com.example.android.locationtracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MapsActivityAddress extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    Button locationShare;
    int flag = 0;
    int count = 1;
    int flagForLocationReceived = 0;
    int flagForAutoEmailSent = 0;
    TextView txtAddress;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Location currentLocation;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String TAG = "MapsActivityAddress";
    DbHelper dbHelper;
    ArrayList<SuitCaseForDataBase> arrSuitCase = new ArrayList<>();
    ImageView img_for_auto_email;
    EditText edtForMailSent;
    EditText edtForMailSelf;
    String deviceId;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_address);

        locationShare = findViewById(R.id.locationShare);
        txtAddress = findViewById(R.id.txtAddress);
        edtForMailSelf = findViewById(R.id.edtForMailSelf);
        edtForMailSent = findViewById(R.id.edtForMailSent);
        dbHelper = DbHelper.getDB(getApplicationContext());
        img_for_auto_email = findViewById(R.id.img_for_auto_email);


        googleApiClient = new GoogleApiClient.Builder(MapsActivityAddress.this).
                addApi(LocationServices.API).
                addConnectionCallbacks(MapsActivityAddress.this).
                addOnConnectionFailedListener(MapsActivityAddress.this).
                build();

        locationRequest = new LocationRequest().
                setInterval(8 * 1000).
                setFastestInterval(5 * 1000).
                setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        toLocationOn();


        img_for_auto_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 1 && flagForLocationReceived == 1) {
                    if (edtForMailSelf.getText().toString().equals("") || edtForMailSent.getText().toString().equals("")) {
                        Toast.makeText(MapsActivityAddress.this, "Enter Required Fields", Toast.LENGTH_SHORT).show();
                     } else {
                        flagForAutoEmailSent = 1;
                    }
                } else
                    Toast.makeText(MapsActivityAddress.this, "Try After", Toast.LENGTH_SHORT).show();
            }
        });


        locationShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 1 && flagForLocationReceived == 1) {
                    String address = getAddressFromGeoCoder();
                    double lat = currentLocation.getLatitude();
                    double lng = currentLocation.getLongitude();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    if (address != null) {
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Latitude :" + lat + "\nLongitude :" + lng + "\nAddress :" + address);
                        intent.putExtra(Intent.EXTRA_TEXT, "Latitude :" + lat + "\nLongitude :" + lng + "\nAddress :" + address);
                    } else {
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Latitude :" + lat + "\nLongitude :" + lng);
                        intent.putExtra(Intent.EXTRA_TEXT, "Latitude :" + lat + "\nLongitude :" + lng);
                    }
                    startActivity(Intent.createChooser(intent, "Share..."));
                } else {
                    Toast.makeText(MapsActivityAddress.this, " Try again ", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void sentAutoEmail() {



            String strMailSent = edtForMailSent.getText().toString();
            String strMailSelf = edtForMailSelf.getText().toString();

            if (!dbHelper.checkDB()) {
                dbHelper.createDB(getApplicationContext());
            }
            dbHelper.openDB();
            final SuitCaseForDataBase suitCaseForDataBase = new SuitCaseForDataBase();
            suitCaseForDataBase.imei_no = getDeviceId();
            suitCaseForDataBase.lat = String.valueOf(currentLocation.getLatitude());
            suitCaseForDataBase.lng = String.valueOf(currentLocation.getLongitude());
            suitCaseForDataBase.date = getCurrentDateAndTime();
            suitCaseForDataBase.address = getAddressFromGeoCoder();


            dbHelper.insertData(suitCaseForDataBase);
            AndroidNetworking.initialize(getApplicationContext());
            AndroidNetworking.get("http://testsamrat.000webhostapp.com/mail.php?send=ok&email_to=" + strMailSelf + "&email_from=" + strMailSent + "&message=" + "imei_no :" + suitCaseForDataBase.imei_no + "Latitude :" + suitCaseForDataBase.lat + "Longitude :" + suitCaseForDataBase.lng + "Address :" + suitCaseForDataBase.address + "Date & Time :" + suitCaseForDataBase.date + "&subject=\n\n")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if(Integer.parseInt(response.getString("result"))==0)
                                {
                                    //arrSuitCase = dbHelper.getData();
                                    dbHelper.insertData(suitCaseForDataBase);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(MapsActivityAddress.this, "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    });

    }

    @SuppressLint("MissingPermission")
    public String getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            deviceId = telephonyManager.getDeviceId();
        } else {
            deviceId = "";
        }
        return deviceId;
    }

    public String getCurrentDateAndTime() {
        DateFormat df = new SimpleDateFormat("EEE,yyyy,mm");
        String dateTime = df.format(Calendar.getInstance().getTime());
        return dateTime;
    }

    public String getAddressFromGeoCoder() {
        String oldData, newData = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(MapsActivityAddress.this, Locale.getDefault());


        try {
            addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 2);

            if (addresses != null && addresses.size() > 0) {
                for (int i = 0; i < addresses.size(); i++) {
                    Address addressObj = addresses.get(i);
                    String Address = addressObj.getAddressLine(0);
                    String City = addressObj.getLocality();
                    String State = addressObj.getAdminArea();
                    String Country = addressObj.getCountryName();
                    String PostalCode = addressObj.getPostalCode();
                    String knownName = addressObj.getFeatureName();
                    oldData = txtAddress.getText().toString() + "\n\n";
                    newData = oldData + "Address: " + Address + "\n" + "City: " + City + "\n" + "State: " + State + "\n" + "Country: " + Country + "\n" + "Postal Code: " + PostalCode + "\n" + "Known Name: " + knownName;
                }
                Log.d("Address", newData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return newData;
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
    public void onLocationChanged(Location location) {
        currentLocation = location;
        updateMap();
        flagForLocationReceived = 1;
    }

    private void updateMap() {
        if (flag == 1) {
            mMap.clear();
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("I'm here "));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f), 4000, null);

            if (flagForAutoEmailSent == 1 && flagForLocationReceived == 1) {
                sentAutoEmail();
            }
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
    public void onConnectionSuspended(int i) {
        Toast.makeText(MapsActivityAddress.this, "Connection Suspended !", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MapsActivityAddress.this, "Connection Failed !", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapsActivityAddress.this, ContentActivity.class);
        startActivity(intent);
        super.onBackPressed();
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
                            status.startResolutionForResult(MapsActivityAddress.this, REQUEST_CHECK_SETTINGS);
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

