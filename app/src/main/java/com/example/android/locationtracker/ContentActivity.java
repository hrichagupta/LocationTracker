package com.example.android.locationtracker;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class ContentActivity extends AppCompatActivity {

    Button btnAddress, btnNavigation, btnPlaces, btnExit, btnTest;
    int flag = 0;
    Boolean internetOn;
    Dialog exitDialog;
    Dialog creatorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        btnAddress = findViewById(R.id.btnAddress);
        btnExit = findViewById(R.id.btnExit);
        btnNavigation = findViewById(R.id.btnNavigation);
        btnPlaces = findViewById(R.id.btnPlaces);
        int check_for_dialog=getIntent().getIntExtra("check_for_dialog",0);


        creatorDialog = new Dialog(ContentActivity.this);
        creatorDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        creatorDialog.setContentView(R.layout.dailog_for_mentor_and_student);
        creatorDialog.setCancelable(false);
        Button btnOkAppCreator=creatorDialog.findViewById(R.id.btnOkAppCreator);

        btnOkAppCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatorDialog.dismiss();
            }
        });
        if(check_for_dialog==5) {
            creatorDialog.show();
        }


        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internetOn = isInternetOn();
                if (internetOn == true) {
                    try {
                        String strUri = "http://maps.google.com/maps?q=loc:26.2702848,72.8904576";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                    } catch (ActivityNotFoundException activityNotFound) {
                        try {
                            String strUri = "http://maps.google.com/maps?q=loc:26.2702848,72.8904576";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(ContentActivity.this, "Enable Maps or Browser", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Intent intent = new Intent(ContentActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                }
            }

        });

        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentActivity.this, KeepGpsOnAnimationActivity.class);
                intent.putExtra("check", 1);
                startActivity(intent);
            }
        });
        btnPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentActivity.this, KeepGpsOnAnimationActivity.class);
                intent.putExtra("check", 2);
                startActivity(intent);
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (flag == 0) {
            exitDialog = new Dialog(ContentActivity.this);
            exitDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            exitDialog.setContentView(R.layout.exit_dialog);
            exitDialog.setCancelable(false);
            Button btnYes, btnNo;
            btnNo = exitDialog.findViewById(R.id.btnNo);
            btnYes = exitDialog.findViewById(R.id.btnYes);
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exitDialog.dismiss();
                }
            });
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAffinity();
                }
            });
            exitDialog.show();
            flag = 1;
        } else {
            finishAffinity();
            super.onBackPressed();
        }
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


}
