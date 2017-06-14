package com.genix.foodgenix;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import com.loopj.android.http.*;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MyRestaurantActivity extends AppCompatActivity {

    @Bind(R.id.swOpenSchedule)
    Switch swOpenSchedule;
    @Bind(R.id.layoutTime)
    LinearLayout layoutTime;
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.btnSave)
    Button btnSave;
    Image image = null;
    @Bind(R.id.edAddress)
    EditText edAddress;
    @Bind(R.id.edPhoneNumber)
    EditText edPhoneNumber;
    @Bind(R.id.edEmail)
    EditText edEmail;
    @Bind(R.id.txtNama)
    TextView txtNama;
    @Bind(R.id.spinDay)
    Spinner spinDay;
    @Bind(R.id.rbOpen)
    RadioButton rbOpen;
    @Bind(R.id.rbClose)
    RadioButton rbClose;
    @Bind(R.id.timePicker)
    TimePicker timePicker;
    private static final int RC_REQUEST = 2000;
    private int restaurant_id = 5;

    String fieldTimeName = "TIME_OPEN_MONDAY";
    HashMap<String,String> timeSet = new HashMap<>();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_restaurant);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        restaurant_id = Integer.parseInt(SaveSharedPreferences.getUserID(getApplicationContext()));
        timeSet.put("TIME_OPEN_MONDAY","00:00:00");
        timeSet.put("TIME_OPEN_TUESDAY","00:00:00");
        timeSet.put("TIME_OPEN_WEDNESDAY","00:00:00");
        timeSet.put("TIME_OPEN_THURSDAY","00:00:00");
        timeSet.put("TIME_OPEN_FRIDAY","00:00:00");
        timeSet.put("TIME_OPEN_SATURDAY","00:00:00");
        timeSet.put("TIME_OPEN_SUNDAY","00:00:00");
        timeSet.put("TIME_CLOSE_MONDAY","00:00:00");
        timeSet.put("TIME_CLOSE_TUESDAY","00:00:00");
        timeSet.put("TIME_CLOSE_WEDNESDAY","00:00:00");
        timeSet.put("TIME_CLOSE_THURSDAY","00:00:00");
        timeSet.put("TIME_CLOSE_FRIDAY","00:00:00");
        timeSet.put("TIME_CLOSE_SATURDAY","00:00:00");
        timeSet.put("TIME_CLOSE_SUNDAY","00:00:00");

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        ButterKnife.bind(this);
        layoutTime.setVisibility(View.GONE);
        swOpenSchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (swOpenSchedule.isChecked()) {
                    layoutTime.setVisibility(View.VISIBLE);
                } else {
                    layoutTime.setVisibility(View.GONE);
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();

            }
        });
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        timePicker.setIs24HourView(true);
        bindRestaurant(restaurant_id);
        rbOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(rbOpen.isChecked()) {
                    fieldTimeName = "TIME_OPEN_" + spinDay.getSelectedItem().toString().toUpperCase();
                    String time = timeSet.get(fieldTimeName);
                    String[] valueTime = time.split(":");
                    int hour = Integer.parseInt(valueTime[0]);
                    int minute = Integer.parseInt(valueTime[1]);
                    int second = Integer.parseInt(valueTime[2]);
                    timePicker.setHour(hour);
                    timePicker.setMinute(minute);

                }
            }
        });
        rbClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(rbClose.isChecked()){
                    fieldTimeName = "TIME_CLOSE_"+spinDay.getSelectedItem().toString().toUpperCase();
                    String time = timeSet.get(fieldTimeName);
                    String[] valueTime = time.split(":");
                    int hour = Integer.parseInt(valueTime[0]);
                    int minute = Integer.parseInt(valueTime[1]);
                    int second = Integer.parseInt(valueTime[2]);
                    timePicker.setHour(hour);
                    timePicker.setMinute(minute);
                }
            }
        });
        spinDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] result = fieldTimeName.split("_");
                fieldTimeName = result[0]+"_"+result[1]+"_"+spinDay.getSelectedItem().toString().toUpperCase();
                String time = timeSet.get(fieldTimeName);
                String[] valueTime = time.split(":");
                int hour = Integer.parseInt(valueTime[0]);
                int minute = Integer.parseInt(valueTime[1]);
                int second = Integer.parseInt(valueTime[2]);
                timePicker.setHour(hour);
                timePicker.setMinute(minute);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                timeSet.put(fieldTimeName,hourOfDay+":"+minute+":00");
            }
        });
    }

    public int uploadFile(final String selectedFilePath){

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length-1];


        try{
            FileInputStream fileInputStream = new FileInputStream(selectedFile);
            URL url = new URL("http://10.0.2.2/edws/upload.php");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);//Allow Inputs
            connection.setDoOutput(true);//Allow Outputs
            connection.setUseCaches(false);//Don't use a cached Copy
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("uploaded_file",selectedFilePath);
            connection.setRequestProperty("nama","restaurant/"+restaurant_id+".jpg");
            //creating new dataoutputstream
            dataOutputStream = new DataOutputStream(connection.getOutputStream());

            //writing bytes to data outputstream
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                    + selectedFilePath + "\"" + lineEnd);

            dataOutputStream.writeBytes(lineEnd);

            //returns no. of bytes present in fileInputStream
            bytesAvailable = fileInputStream.available();
            //selecting the buffer size as minimum of available bytes or 1 MB
            bufferSize = Math.min(bytesAvailable,maxBufferSize);
            //setting the buffer as byte array of size of bufferSize
            buffer = new byte[bufferSize];

            //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
            bytesRead = fileInputStream.read(buffer,0,bufferSize);

            //loop repeats till bytesRead = -1, i.e., no bytes are left to read
            while (bytesRead > 0){
                //write the bytes read from inputstream
                dataOutputStream.write(buffer,0,bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                bytesRead = fileInputStream.read(buffer,0,bufferSize);
            }

            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            //closing the input and output streams
            fileInputStream.close();
            dataOutputStream.flush();
            dataOutputStream.close();



        } catch (FileNotFoundException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyRestaurantActivity.this,"File Not Found",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(MyRestaurantActivity.this, "URL error!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MyRestaurantActivity.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
        }
        return serverResponseCode;


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void bindRestaurant(int id) {
        HashMap<String, String> params = new HashMap<>();
        String response = EDWSRequest.Request("GET", "restaurant/findById/" + restaurant_id, params);
        JSONObject nodeRoot  = null;
        try {
            nodeRoot = new JSONObject(response);
            JSONObject node = nodeRoot.getJSONObject("result");
            txtNama.setText(node.getString("NAME"));
            edAddress.setText(node.getString("ADDRESS"));
            edEmail.setText(node.getString("EMAIL"));
            edPhoneNumber.setText(node.getString("PHONE"));
            String BIO = node.getString("BIO");
            JSONObject nodeTimeOpen = node.getJSONObject("time_open");
            timeSet.put("TIME_OPEN_MONDAY",nodeTimeOpen.getString("TIME_OPEN_MONDAY"));
            timeSet.put("TIME_OPEN_TUESDAY",nodeTimeOpen.getString("TIME_OPEN_TUESDAY"));
            timeSet.put("TIME_OPEN_WEDNESDAY",nodeTimeOpen.getString("TIME_OPEN_WEDNESDAY"));
            timeSet.put("TIME_OPEN_THURSDAY",nodeTimeOpen.getString("TIME_OPEN_THURSDAY"));
            timeSet.put("TIME_OPEN_FRIDAY",nodeTimeOpen.getString("TIME_OPEN_FRIDAY"));
            timeSet.put("TIME_OPEN_SATURDAY",nodeTimeOpen.getString("TIME_OPEN_SATURDAY"));
            timeSet.put("TIME_OPEN_SUNDAY",nodeTimeOpen.getString("TIME_OPEN_SUNDAY"));
            timeSet.put("TIME_CLOSE_MONDAY",nodeTimeOpen.getString("TIME_CLOSE_MONDAY"));
            timeSet.put("TIME_CLOSE_TUESDAY",nodeTimeOpen.getString("TIME_CLOSE_TUESDAY"));
            timeSet.put("TIME_CLOSE_WEDNESDAY",nodeTimeOpen.getString("TIME_CLOSE_WEDNESDAY"));
            timeSet.put("TIME_CLOSE_THURSDAY ",nodeTimeOpen.getString("TIME_CLOSE_THURSDAY"));
            timeSet.put("TIME_CLOSE_FRIDAY",nodeTimeOpen.getString("TIME_CLOSE_FRIDAY"));
            timeSet.put("TIME_CLOSE_SATURDAY",nodeTimeOpen.getString("TIME_CLOSE_SATURDAY"));
            timeSet.put("TIME_CLOSE_SUNDAY",nodeTimeOpen.getString("TIME_CLOSE_SUNDAY"));
            Picasso.with(getApplicationContext()).setLoggingEnabled(true);
            Picasso.Builder builder = new Picasso.Builder(this);
            builder.listener(new Picasso.Listener()
            {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
                {
                    exception.printStackTrace();
                }
            });
            builder.build().load(EDWSRequest.SERVER_URL+"uploads/restaurant/"+node.getString("NO")+".jpg")
                    .error(R.drawable.restaurant_512)
                    .into(imageView);

            //imageView.setImageBitmap(decodedBitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void start() {


        ImagePicker imagePicker = ImagePicker.create(this)
                .folderMode(true) // set folder mode (false by default)
                .folderTitle("Folder") // folder selection title
                .imageTitle("Tap to select"); // image selection title

        imagePicker.limit(1) // max images can be selected (99 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                .start(RC_REQUEST); // start image picker activity with request code

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_REQUEST && resultCode == RESULT_OK && data != null) {
            image = ImagePicker.getImages(data).get(0);
            imageView.setImageBitmap(getBitmap(Uri.parse(image.getPath())));
        }
    }

    public static Bitmap getBitmap(Uri uri) {
        File imgFile = new File(uri.getPath());
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return myBitmap;
        }
        return null;
    }
    private HashMap<String,String> getData(){
        HashMap<String,String> params = new HashMap<>();
        params.put("PHONE",edPhoneNumber.getText().toString());
        params.put("EMAIL",edEmail.getText().toString());
        params.put("ADDRESS",edAddress.getText().toString());
        params.put("NAME",txtNama.getText().toString());
        params.put("BIO",restaurant_id+".jpg");
        params.put("STATUS","1");
        HashMap<String,String> mapAll = new HashMap<>();
        mapAll.putAll(params);
        mapAll.putAll(timeSet);
        return mapAll;
    }
    private void save() {
        //on upload button Click
        if(image != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //creating new thread to handle Http Operations
                    uploadFile(image.getPath());
                }
            }).start();
        }
        new UpdateTask().execute(getData());

    }
    private class UpdateTask extends AsyncTask<HashMap<String,String>,Void,String>{
        @Override
        protected String doInBackground(HashMap<String, String>... params) {
            String response = EDWSRequest.Request("PUT","restaurant/update/"+restaurant_id,params[0]);
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnSave.setText("Loading...");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            btnSave.setText("Save");
            finish();
        }
    }
}