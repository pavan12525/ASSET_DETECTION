package com.example.asset_detection;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;


public class GPS_SERVICE extends Service implements LocationListener
{
    Location location;
    protected LocationManager locationManager;
    double latitude;
    double longitude;
    boolean GPS_STATE;
    public static final String TAG = "TAG";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 6000; // 1 minute
    public  GPS_SERVICE()
    {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                onLocationChanged(location);
            }
        });
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.d(TAG,"control inside on Location Changed");

        try {
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);
            GPS_STATE = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.d(TAG,"The GPS state is "+GPS_STATE);
            //System.out.println("The state is "+GPS_STATE);
           if (GPS_STATE) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {

                      //Toast.makeText(MyService.this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
                    return;
                }
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                // Log.d("GPS has been Enabled", "GPS is Enabled");

                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
                Log.d(TAG,"The latitude and longitude are"+latitude+" "+longitude);
               // System.out.println("The longitude is "+longitude+"The latitude is "+latitude);
                Intent done=new Intent("com.local.receiver1");
                Bundle b = new Bundle();
                b.putDouble("latitude", latitude);
                b.putDouble("longitude", longitude);
                done.putExtras(b);
                sendBroadcast(done);


            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }




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
}
