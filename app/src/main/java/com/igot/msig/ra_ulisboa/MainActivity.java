package com.igot.msig.ra_ulisboa;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.igot.msig.ra_ulisboa.Utils.Constant;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class MainActivity extends AppCompatActivity {


    AccessToken accessToken;
    String nome;
    String email;
    String id;
    String genero;
    String localizacao;
    String FaixaEtaria;
    String aniversario;
    String amigos;
    String cidade;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference root = database.getReference();

    // Create a VideoView variable, a MediaPlayer variable, and an int to hold the current
    // video position.
    private VideoView videoBG;
    MediaPlayer mMediaPlayer;
    int mCurrentVideoPosition;

    private SensorService mSensorService = null;
    Intent mServiceIntent;

    private Button start;
    private Button stop;
    private Button getone;

    public TextView latitudet;
    private TextView longitudet;

    public LocationManager locationManager;

    public static String data = "";
    public static String day = "";
    public static String mesName = "";
    public static String yearName = "";

    Context ctx;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //myRef.setValue("Hello, World!");

        ctx = this;

        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        getone = findViewById(R.id.getone);
        latitudet = findViewById(R.id.latitude);
        longitudet = findViewById(R.id.longitude);

        getone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                getLocation();
            }
        });

        mSensorService = new SensorService(MainActivity.this);
        mServiceIntent = new Intent(MainActivity.this, mSensorService.getClass());

        Constant.flagTimer = true;
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }

        //getone.setEnabled(false);

        start.setEnabled(false);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {

                mSensorService = new SensorService(MainActivity.this);
                mServiceIntent = new Intent(MainActivity.this, mSensorService.getClass());

                Constant.flagTimer = true;
                if (!isMyServiceRunning(mSensorService.getClass())) {
                    startService(mServiceIntent);
                }

                //getone.setEnabled(false);
                start.setEnabled(false);
                stop.setEnabled(true);

            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                stopService(mServiceIntent);
                Constant.flagTimer = false;
                //getone.setEnabled(true);
                start.setEnabled(true);
                stop.setEnabled(false);
            }
        });

        if (!checkLocationPermission())
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hook up the VideoView to our UI.
        videoBG = (VideoView) findViewById(R.id.videoView);

        // Build your video Uri
        Uri uri = Uri.parse("android.resource://" // First start with this,
                + getPackageName() // then retrieve your package name,
                + "/" // add a slash,
                + R.raw.drawableheineke); // and then finally add your video resource. Make sure it is stored
        // in the raw folder.

        // Set the new Uri to our VideoView
        videoBG.setVideoURI(uri);
        // Start the VideoView
        videoBG.start();

        // Set an OnPreparedListener for our VideoView. For more information about VideoViews,
        // check out the Android Docs: https://developer.android.com/reference/android/widget/VideoView.html
        videoBG.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mMediaPlayer = mediaPlayer;
                // We want our video to play over and over so we set looping to true.
                mMediaPlayer.setLooping(true);
                // We then seek to the current posistion if it has been set and play the video.
                if (mCurrentVideoPosition != 0) {
                    mMediaPlayer.seekTo(mCurrentVideoPosition);
                    mMediaPlayer.start();

                }


            }
        });

    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void onRequestPermissionsResult( int requestCode, String permissions[], int[] grantResults ) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private boolean isMyServiceRunning( Class <?> serviceClass ) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
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

            Log.e("latitude", String.valueOf(lat));
            Log.e("longitude", String.valueOf(longi));

            sendPosition(String.valueOf(lat), String.valueOf(longi), true);

            latitudet.setText(String.valueOf(lat));
            longitudet.setText(String.valueOf(longi));


            //showLocationTxt.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
        } else if (LocationNetwork != null) {
            double lat = LocationNetwork.getLatitude();
            double longi = LocationNetwork.getLongitude();

            latitudet.setText(String.valueOf(lat));
            longitudet.setText(String.valueOf(longi));

            sendPosition(String.valueOf(lat), String.valueOf(longi), true);

            Log.e("latitude-lasted", String.valueOf(lat));
            Log.e("longitude-lasterd", String.valueOf(longi));

            //showLocationTxt.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
        } else if (LocationPassive != null) {
            double lat = LocationPassive.getLatitude();
            double longi = LocationPassive.getLongitude();

            sendPosition(String.valueOf(lat), String.valueOf(longi), true);

            Log.e("latitude-passiveMode", String.valueOf(lat));
            Log.e("longitude-passiveMode", String.valueOf(longi));

            //showLocationTxt.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);


        } else {
            //oast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
        }


    }

    public static void sendPosition( String latitude, String longitude, Boolean getonce ) {

        DatabaseReference send = null;

        FirebaseDatabase.getInstance().getReference("user").child(Constant.id).child("btLocation");

        if (!getonce)
            send = FirebaseDatabase.getInstance().getReference("user").child(Constant.id).child("currentLocation");


        HashMap <String, Object> tipe = new HashMap();
        tipe.put(getDate(), latitude + "," + longitude);

        send.updateChildren(tipe).addOnSuccessListener(new OnSuccessListener <Void>() {
            @Override
            public void onSuccess( Void aVoid ) {

            }
        });

    }

    private static String getDate() {
        day = String.valueOf(Integer.parseInt(new SimpleDateFormat("d").format(Calendar.getInstance().getTime())));
        int month = Calendar.getInstance().get(2);
        yearName = String.valueOf(Calendar.getInstance().get(1));
        mesName = getMesName(month);
        data = day + "-" + mesName + "-" + yearName + "/" + gethours();
        Log.e("date", data);
        return data;
    }

    private static String getMesName( int month ) {
        if (month == 0) {
            mesName = "Janeiro";
        } else if (month == 1) {
            mesName = "Fevereiro";
        } else if (month == 2) {
            mesName = "Marco";
        } else if (month == 3) {
            mesName = "Abril2";
        } else if (month == 4) {
            mesName = "Maio";
        } else if (month == 5) {
            mesName = "Junho";
        } else if (month == 6) {
            mesName = "Julho";
        } else if (month == 7) {
            mesName = "Agosto";
        } else if (month == 8) {
            mesName = "Setembro";
        } else if (month == 9) {
            mesName = "Outubro";
        } else if (month == 10) {
            mesName = "Novembro";
        } else if (month == 11) {
            mesName = "Dezembro";
        }
        return mesName;
    }

    private static String gethours() {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

          /*================================ Important Section! ================================
    We must override onPause(), onResume(), and onDestroy() to properly handle our
    VideoView.
     */

    @Override
    protected void onPause() {
        super.onPause();
        // Capture the current video position and pause the video.
        mCurrentVideoPosition = mMediaPlayer.getCurrentPosition();
        videoBG.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restart the video when resuming the Activity
        videoBG.start();
    }

    @Override
    protected void onDestroy() {

        //Método Geolocalizaçao
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

        // When the Activity is destroyed, release our MediaPlayer and set it to null.
        //Projeto Video View
        mMediaPlayer.release();
        mMediaPlayer = null;

    }

    @Override
    protected void onStart() {
        super.onStart();

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            finish();

        }

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted( JSONObject object, GraphResponse response ) {
                try {
                    nome = object.getString("name");
                    email = object.getString("email");
                    id = object.getString("id");
                    genero = object.getString("gender");
                   /* localizacao = object.getString("location");
                    FaixaEtaria = object.getString("age_range");
                    aniversario = object.getString("birthday");
                    amigos = object.getString("friends");
                    cidade = object.getString("hometown");*/

                    Constant.id = id;

                    root.child("user").child(object.getString("id")).child("nome").setValue(nome);
                    root.child("user").child(object.getString("id")).child("email").setValue(email);
                    root.child("user").child(object.getString("id")).child("genero").setValue(genero);
                    /*root.child("user").child(object.getString("id")).child("localizacao").setValue(localizacao);
                    root.child("user").child(object.getString("id")).child("FaixaEtaria").setValue(FaixaEtaria);
                    root.child("user").child(object.getString("id")).child("amigos").setValue(amigos);
                    root.child("user").child(object.getString("id")).child("cidade").setValue(cidade);
                    root.child("user").child(object.getString("id")).child("aniversario").setValue(aniversario);*/

                } catch (JSONException e) {e.printStackTrace();}
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }
}