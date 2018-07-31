package com.example.phoenix.requestdatabase;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    public TextView textView;
    public TextView textView1;
    LocationManager locationManager;
    public EditText busno;
    public Button button;
    DatabaseReference database;
    double longitude;
    double latitude;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    String name;
    @Override
    public void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance().getReference();
        //Code to check if the internet connection is working or not
        if (isOnline()) {

            //To grant location Services
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 1);
            }
            //Check if location services are enabled or not
            statusCheck();
            textView = (TextView) findViewById(R.id.lati);
            textView1 = (TextView) findViewById(R.id.longi);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            busno = (EditText) findViewById(R.id.buttonid);
            button = (Button) findViewById(R.id.buttoni);
            button.setOnTouchListener(new View.OnTouchListener() {
                AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                            v.invalidate();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            v.getBackground().clearColorFilter();
                            v.invalidate();
                            break;
                        }
                    }
                    return false;
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name = busno.getText().toString().trim();
                    if(name.matches(""))
                    {
                        Toast.makeText(MainActivity.this,"Empty Field!",Toast.LENGTH_LONG).show();
                    }
                    if (!TextUtils.isEmpty(name)) {

                        database.child(name).child("Longitude").setValue(longitude);
                        database.child(name).child("Latitude").setValue(latitude);
                    } else {
                    }
                }
            });
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        //get the latitude
                        latitude = location.getLatitude();
                        //get the longitude
                        longitude = location.getLongitude();
                        String lon = new Double(latitude).toString();
                        String lang = new Double(longitude).toString();
                        textView1.setText("Longitude: "+lon);
                        textView.setText("Latitude: "+lang);
                        name = busno.getText().toString().trim();
                        if (!TextUtils.isEmpty(name)) {

                            database.child(name).child("Longitude").setValue(longitude);
                            database.child(name).child("Latitude").setValue(latitude);
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
                });
            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        //get the latitude
                        latitude = location.getLatitude();
                        //get the longitude
                        longitude = location.getLongitude();

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
                });
            }
        } else {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();

                alertDialog.setTitle("Info");
                alertDialog.setMessage("Internet not available, Cross check your internet connec" +
                        "tivity and try again");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.show();
            } catch (Exception e) {

            }
        }
    }
    //Check if the location services are on or not
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }
    //Check if the location services are on or not
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    //Isonline funcion to check the internet connectivity
    public boolean isOnline(){
        ConnectivityManager conMgr=(ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo=conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(this,"No Internet Connection!",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
