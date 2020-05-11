package com.igot.msig.ra_ulisboa;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.igot.msig.ra_ulisboa.Utils.Constant;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fabio on 30/01/2016.
 */
public class SensorService extends Service {
    public int counter = 0;
    public LocationManager locationManager;
    private Context mContext;
    private MainActivity mainActivity;

    public SensorService(MainActivity mainActivity) {
        //super();
        this.mainActivity = mainActivity;
        Log.i("HERE", "here I am!");
    }

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (Constant.flagTimer)
            startTimer();

        Log.i("HERE", "here I am!2");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 10000, 10000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter++));
                getLocation();
            }
        };
    }

    private void getLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Check gps is enable or not

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Write Function To enable gps

            Log.e("msg", "ativar gps");
            return;
            //OnGPS();
        }

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


        Location LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        if (LocationGps != null) {
            double lat = LocationGps.getLatitude();
            double longi = LocationGps.getLongitude();

            MainActivity.sendPosition(String.valueOf(lat), String.valueOf(longi), false);

            Log.e("latitude", String.valueOf(lat));
            Log.e("longitude", String.valueOf(longi));


            //showLocationTxt.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
        } else if (LocationNetwork != null) {
            double lat = LocationNetwork.getLatitude();
            double longi = LocationNetwork.getLongitude();

            MainActivity.sendPosition(String.valueOf(lat), String.valueOf(longi), false);

            Log.e("latitude-lasted", String.valueOf(lat));
            Log.e("longitude-lasterd", String.valueOf(longi));

            //showLocationTxt.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
        } else if (LocationPassive != null) {
            double lat = LocationPassive.getLatitude();
            double longi = LocationPassive.getLongitude();

            MainActivity.sendPosition(String.valueOf(lat), String.valueOf(longi), false);

            Log.e("latitude-passiveMode", String.valueOf(lat));
            Log.e("longitude-passiveMode", String.valueOf(longi));

            //showLocationTxt.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);


        } else {
            //oast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
        }


    }


    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
