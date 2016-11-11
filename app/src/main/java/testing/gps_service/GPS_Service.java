package testing.gps_service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by filipp on 6/16/2016.
 */
public class GPS_Service extends Service implements LocationListener {

    private Timer timer;
    private final int interval = 10000; // 10 Second
    private final Context mContext = getBaseContext();

    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;
    private Location location; // location
    private double latitude; // latitude
    private double longitude; // longitude
    private Runnable runnable;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 10000; // 0 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }


    @Override
    public void onCreate() {
        // Start Location Getting
        getLocation();
        Toast.makeText(getApplicationContext(), " Started", Toast.LENGTH_SHORT).show();
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 1000, interval);
        //new Timer().schedule(task, after);  for One Time
    }

    TimerTask timerTask = new TimerTask() {
        String values = "" + latitude;

        @Override
        public void run() {
            Intent i = new Intent("location_update");
            if (values != null) {
                //i.putExtra("coordinates", latitude + " " + longitude);
                i.putExtra("coordinates", latitude + " " + longitude);
                //  i.putExtra("coordinates", getLocation().getLongitude() + " " + getLocation().getLatitude());
                sendBroadcast(i);
            }
            Log.d("Runnung", "Runnung");
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();

        if (locationManager != null) {
            //noinspection MissingPermission
            locationManager.removeUpdates(this);
            Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT).show();

        }

    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(getApplicationContext(), "Both are not active", Toast.LENGTH_SHORT).show();
                location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        //      return TODO;
                    }

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            Toast.makeText(getApplicationContext(), "lat = " + latitude + " long = " + longitude, Toast.LENGTH_SHORT).show();

                        }
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;

    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

    }


    private void locationMsg(String msg) {

        if (!msg.equals(null) && !msg.equals("")) {
            Message msgObj = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putString("message", msg);
            msgObj.setData(b);
            handler.sendMessageDelayed(msgObj, 10000);
        }
    }


    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {

            getLocation();
        }
    };

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("onStatusChanged", provider + " # " + status + " # " + extras);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getApplicationContext(), "  GPS Connected", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Toast.makeText(getApplicationContext(), " Check GPS Connection ", Toast.LENGTH_SHORT).show();
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

    }


}