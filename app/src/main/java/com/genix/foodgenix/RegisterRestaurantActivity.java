package com.genix.foodgenix;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterRestaurantActivity extends AppCompatActivity{

    @Bind(R.id.input_name)
    EditText input_name;
    @Bind(R.id.input_email)
    EditText input_email;
    @Bind(R.id.input_address)
    EditText input_address;
    @Bind(R.id.input_phone)
    EditText input_phone;
    @Bind(R.id.input_user)
    EditText input_user;
    @Bind(R.id.input_password)
    EditText input_password;
    @Bind(R.id.input_cpassword)
    EditText input_cpassword;
    @Bind(R.id.btn_signup)
    Button btn_signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_restaurant);
        ButterKnife.bind(this);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_cpassword.getText().toString().equals(input_password.getText().toString())){

                    HashMap<String,String> params = new HashMap<String, String>();
                    params.put("TIME_OPEN_MONDAY","08:00:00");
                    params.put("TIME_OPEN_TUESDAY","08:00:00");
                    params.put("TIME_OPEN_WEDNESDAY","08:00:00");
                    params.put("TIME_OPEN_THURSDAY","08:00:00");
                    params.put("TIME_OPEN_FRIDAY","08:00:00");
                    params.put("TIME_OPEN_SATURDAY","08:00:00");
                    params.put("TIME_OPEN_SUNDAY","08:00:00");
                    params.put("TIME_CLOSE_MONDAY","17:00:00");
                    params.put("TIME_CLOSE_TUESDAY","17:00:00");
                    params.put("TIME_CLOSE_WEDNESDAY","17:00:00");
                    params.put("TIME_CLOSE_THURSDAY","17:00:00");
                    params.put("TIME_CLOSE_FRIDAY","17:00:00");
                    params.put("TIME_CLOSE_SATURDAY","17:00:00");
                    params.put("TIME_CLOSE_SUNDAY","17:00:00");
                    params.put("NAME",input_name.getText().toString());
                    params.put("ADDRESS",input_address.getText().toString());
                    params.put("PHONE",input_phone.getText().toString());
                    params.put("EMAIL",input_email.getText().toString());
                    params.put("LATITUDE",String.valueOf(MainActivity.latitude));
                    params.put("LONGITUDE",String.valueOf(MainActivity.longitude));
                    params.put("BIO","");
                    params.put("USERNAME",input_user.getText().toString());
                    params.put("PASSWORD",input_password.getText().toString());
                    params.put("STATUS","1");
                    new RegisterRestaurantTask().execute(params);
                }
                else{
                   Toast.makeText(getApplicationContext(),"Please ensure that password and confirm password are the same", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    class RegisterRestaurantTask extends AsyncTask<HashMap<String,String>,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btn_signup.setText("Registering...");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            btn_signup.setText("Register Now");
            if(s.equals("Failed")){
                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
            }
            else{
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finishAffinity();
            }
        }

        @Override
        protected String doInBackground(HashMap<String, String>... params) {
            String response = EDWSRequest.Request("POST","restaurant/register",params[0]);
            return response;
        }
    }
}
