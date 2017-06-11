package com.genix.foodgenix;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private double latitude, longitude;
    EditText edSearch;
    Button btnListOfRestaurant, btnNearbyRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (SaveSharedPreferences.getUserID(MainActivity.this) != "") {
            //wes login
        } else {
            //blm login
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation();

        Toast.makeText(MainActivity.this, latitude + " - " + longitude, Toast.LENGTH_SHORT).show();
        
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
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                try {
//                    new nearbyRestaurantTask().execute();
                }catch (Exception e)
                {
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
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

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private class listRestaurantTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... params) {
            String response = "";
            // URL url = new URL("http://google.com/controller/function"); untuk server
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
            // URL url = new URL("http://google.com/controller/function"); untuk server
            try
            {
                URL url = new URL("http://10.0.2.2/edws/restaurant/findNearbyRestaurant"); //untuk emulator

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("APIKEY",SaveSharedPreferences.getApiKey());
                conn.setRequestMethod("POST");
                String parameter = "latitudeHere="+params[0]+"&longitudeHere="+params[1];

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
}
