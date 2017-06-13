package com.genix.foodgenix;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastKnownLocation ;
    private double latitude, longitude;
    private boolean mLocationPermissionGranted = false;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    EditText edSearch;
    Button btnListOfRestaurant, btnNearbyRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLogin();
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        edSearch = (EditText) findViewById(R.id.edSearch);
        btnListOfRestaurant = (Button) findViewById(R.id.btnListOfRestaurant);
        btnNearbyRestaurant = (Button) findViewById(R.id.btnNearbyRestaurant);
        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Toast.makeText(MainActivity.this, edSearch.getText().toString(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        btnListOfRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new listRestaurantTask().execute();
            }
        });
        btnNearbyRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new nearbyRestaurantTask().execute();
            }
        });
    }

    protected void checkLogin() {
        if (SaveSharedPreferences.getUserID(MainActivity.this) != "") {
            //wes login
        } else {
            //blm login
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    protected void createLocationRequest() {
        checkLocationPermission();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission. ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(500);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.i(TAG, " Location: " + mLastKnownLocation);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission. ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(500);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.i(TAG, " Location: " + mLastKnownLocation);
        }
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission. ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected!");
        createLocationRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Play services connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode() + " - ConnectionResult.getErrorMessage() = "+ connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location Changed!");
        Log.i(TAG, " Location: " + location);
        mLastKnownLocation = location;
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "Service destroyed!");
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    private class listRestaurantTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... params) {
            String response = "";
            try
            {
                URL url = new URL("http://10.0.2.2/edws/restaurant/findAll"); //untuk emulator

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("APIKEY",SaveSharedPreferences.getApiKey());
                conn.setRequestMethod("POST");

                OutputStream outputstream = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputstream,"UTF-8"));

                writer.flush();
                writer.close();
                outputstream.close();

                int responseCode = conn.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK)
                {
                    String line ="";
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while((line = reader.readLine())!= null)
                    {
                        response += line;
                    }
                    reader.close();
                }
                else
                {
                    response = "Gagal konek ke server";
                }

            }catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Intent intent = new Intent(MainActivity.this,ListRestaurantActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("restaurant", s);
                intent.putExtras(mBundle);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this,"Something goes wrong",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class nearbyRestaurantTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... params) {
            String response = "";
            try {
                latitude = mLastKnownLocation.getLatitude();
                longitude = mLastKnownLocation.getLatitude();
                response = latitude + " - " + longitude;

                URL url = new URL("http://10.0.2.2/edws/restaurant/findNearbyRestaurant"); //untuk emulator

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("APIKEY",SaveSharedPreferences.getApiKey());
                conn.setRequestMethod("POST");
                String parameter = "latitudeHere="+latitude+"&longitudeHere="+longitude;

                OutputStream outputstream = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputstream,"UTF-8"));

                writer.write(parameter);
                writer.flush();
                writer.close();
                outputstream.close();

                int responseCode = conn.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK)
                {
                    String line ="";
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while((line = reader.readLine())!= null)
                    {
                        response += line;
                    }
                    reader.close();
                }
                else
                {
                    response = "Gagal konek ke server";
                }

            }catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(mLocationPermissionGranted) {
                try {
                    Intent intent = new Intent(MainActivity.this, ListRestaurantActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("restaurant", s);
                    intent.putExtras(mBundle);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Something goes wrong", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                createLocationRequest();
            }
        }
    }
}
