package com.genix.foodgenix;

import android.Manifest;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastKnownLocation ;
    public static double latitude, longitude;
    TextView noResult;
    private boolean mLocationPermissionGranted = false;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    BottomNavigationView navigation;
    int pageNow;
    int alert_id;
    public RecyclerView lv;
    public ArrayList<Restoran> arrRestoran = new ArrayList<>();
    public RestaurantAdapter adapter;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        /*
        MenuItem logout = (MenuItem) menu.findItem(R.id.logout);
        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("Are you sure want to log out ?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SaveSharedPreferences.getUserID(MainActivity.this);
                                SaveSharedPreferences.setUserID(MainActivity.this,"");
                                checkLogin();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                return false;
            }
        });*/

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("Are you sure want to log out ?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SaveSharedPreferences.getUserID(MainActivity.this);
                            SaveSharedPreferences.setUserID(MainActivity.this,"");
                            checkLogin();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return false;
        }
        else if(item.getItemId() == R.id.myrestaurant){
            Intent intent = new Intent(getApplicationContext(),MyRestaurantActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_view_home);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f18e41")));

        buildGoogleApiClient();
        mGoogleApiClient.connect();

        checkLogin();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        pageNow = R.id.navigation_restaurant;
        noResult = (TextView)findViewById(R.id.noresultText);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() != pageNow) {
                    pageNow = item.getItemId();
                    switch (item.getItemId()) {
                        case R.id.navigation_restaurant:
                            arrRestoran.clear();
                            HashMap<String,String> params = new HashMap<>();
                            new listRestaurantTask().execute(params);
                            return true;
                        case R.id.navigation_nearby:
                            arrRestoran.clear();
                            new nearbyRestaurantTask().execute();
                            return true;
                        case R.id.navigation_promo:
                            return true;
                    }
                }
                return false;
            }
        });

        //tambahan gabungan punya darren

        lv = (RecyclerView) findViewById(R.id.listview);
        HashMap<String,String> params = new HashMap<>();
        new listRestaurantTask().execute(params);


        // end of tambahan
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
        switch (alert_id) {
            case 1:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
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
                break;
        }
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission. ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            alert_id = 1;
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

    private class listRestaurantTask extends AsyncTask<HashMap<String,String>,Void,String>
    {
        @Override
        protected String doInBackground(HashMap<String,String>... params) {
            String response = EDWSRequest.Request("GET", "restaurant/findAll",params[0]);
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
                JSONObject nodeRoot = new JSONObject(s);
                JSONArray nodeArray = nodeRoot.getJSONArray("result");
                HashMap<String,String> params = new HashMap<>();
                for(int i=0;i<nodeArray.length();i++){
                    String responseRating = EDWSRequest.Request("GET","restaurant/rating/"+nodeArray.getJSONObject(i).getString("NO"),params);
                    JSONObject nodeResponse = new JSONObject(responseRating);
                    float rating = Float.parseFloat(nodeResponse.getString("Rating"));
                    arrRestoran.add(new Restoran(Integer.parseInt(nodeArray.getJSONObject(i).getString("NO")),nodeArray.getJSONObject(i).getString("NAME"),nodeArray.getJSONObject(i).getString("ADDRESS"),nodeArray.getJSONObject(i).getString("BIO"),rating));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new RestaurantAdapter(arrRestoran);
            adapter.setClickListener(new RestaurantAdapter.ClickListener() {
                @Override
                public void ItemClicked(View v, int position) {
                    Intent intent = new Intent(getApplicationContext(),RestaurantActivity.class);
                    intent.putExtra("restaurantid",arrRestoran.get(position).getId());
                    startActivity(intent);

                }
            });
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            lv.setLayoutManager(mLayoutManager);
            lv.setItemAnimator(new DefaultItemAnimator());
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            if(arrRestoran.size()==0){
                noResult.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);
            }
            else{
                noResult.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
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
                longitude = mLastKnownLocation.getLongitude();
                response = latitude + " - " + longitude;

                URL url = new URL(EDWSRequest.SERVER_URL+"restaurant/findNearbyRestaurant"); //untuk emulator

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
                    try {
                        JSONObject nodeRoot = new JSONObject(s);
                        JSONArray nodeArray = nodeRoot.getJSONArray("result");
                        HashMap<String,String> params = new HashMap<>();
                        for(int i=0;i<nodeArray.length();i++){
                            String responseRating = EDWSRequest.Request("GET","restaurant/rating/"+nodeArray.getJSONObject(i).getString("NO"),params);
                            JSONObject nodeResponse = new JSONObject(responseRating);
                            float rating = Float.parseFloat(nodeResponse.getString("Rating"));
                            arrRestoran.add(new Restoran(Integer.parseInt(nodeArray.getJSONObject(i).getString("NO")),nodeArray.getJSONObject(i).getString("NAME"),nodeArray.getJSONObject(i).getString("ADDRESS"),nodeArray.getJSONObject(i).getString("BIO"),rating));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    adapter = new RestaurantAdapter(arrRestoran);
                    adapter.setClickListener(new RestaurantAdapter.ClickListener() {
                        @Override
                        public void ItemClicked(View v, int position) {
                            Intent intent = new Intent(getApplicationContext(),RestaurantActivity.class);
                            intent.putExtra("restaurantid",arrRestoran.get(position).getId());
                            startActivity(intent);
                        }
                    });
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    lv.setLayoutManager(mLayoutManager);
                    lv.setItemAnimator(new DefaultItemAnimator());
                    lv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    if(arrRestoran.size()==0){
                        noResult.setVisibility(View.VISIBLE);
                        lv.setVisibility(View.GONE);
                    }
                    else{
                        noResult.setVisibility(View.GONE);
                        lv.setVisibility(View.VISIBLE);
                    }
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
