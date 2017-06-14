package com.genix.foodgenix;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    Button btnSignIn;
    EditText edUserName,edPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSignIn = (Button)findViewById(R.id.btn_signin);
        edUserName = (EditText)findViewById(R.id.username);
        edPassword= (EditText)findViewById(R.id.password);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //login
                HashMap<String,String> params = new HashMap<String, String>();
                params.put("username",edUserName.getText().toString());
                params.put("password",edPassword.getText().toString());
                new loginTask().execute(params);
            }
        });
    }
    private class loginTask extends AsyncTask<HashMap<String,String>,Void,String>
    {
        @Override
        protected String doInBackground(HashMap<String,String>... params) {
            String response = EDWSRequest.Request("POST","restaurant/login",params[0]);
            return response;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnSignIn.setText("Signing In...");
            btnSignIn.setEnabled(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject nodeRoot  = new JSONObject(s);
                JSONObject nodeStats = nodeRoot.getJSONObject("result");
                String userID = nodeStats.getString("NO");
                SaveSharedPreferences.setUserID(LoginActivity.this,userID);
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finishAffinity();

            } catch (JSONException e) {
                Toast.makeText(LoginActivity.this,"Wrong Username or Password",Toast.LENGTH_SHORT).show();
            }
            btnSignIn.setText("Sign In");
            btnSignIn.setEnabled(true);
        }
    }
}
