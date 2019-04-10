package com.example.android.locationtracker;

import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.Keep;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

public class KeepGpsOnAnimationActivity extends AppCompatActivity {

    Button btnOkGps;
    Boolean internetCheck;
    int checking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(KeepGpsOnAnimationActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_gps_on_animation);

        checking = getIntent().getIntExtra("check", 100);
        btnOkGps = findViewById(R.id.btnOkGps);

        btnOkGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checking == 1) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.after_keep_gps_on_load_dialog);
                    dialog.setCancelable(false);
                    dialog.show();


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            internetCheck = isInternetOn();
                            if (internetCheck == false) {
                                Intent intent = new Intent(KeepGpsOnAnimationActivity.this, NoInternetActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(KeepGpsOnAnimationActivity.this, MapsActivityAddress.class);
                                startActivity(intent);
                            }
                        }
                    }, 6000);


                } else if (checking == 2) {
                    internetCheck = isInternetOn();
                    if (internetCheck == false) {
                        Intent intent = new Intent(KeepGpsOnAnimationActivity.this, NoInternetActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(KeepGpsOnAnimationActivity.this, NearByPlacesActivity.class);
                        startActivity(intent);
                    }

                }
            }


        });
    }


    public final boolean isInternetOn() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (cm.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                cm.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                cm.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                cm.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet

            return true;

        } else if (
                cm.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        cm.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            return false;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(KeepGpsOnAnimationActivity.this, ContentActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
