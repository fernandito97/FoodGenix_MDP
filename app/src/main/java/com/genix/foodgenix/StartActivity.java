package com.genix.foodgenix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    Button btnStart;
    Button btnLogin;
    Button btnRegisterCli;
    Button btnRegisterRes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        btnStart = (Button)findViewById(R.id.btnStart);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnRegisterCli = (Button)findViewById(R.id.btnRegisterClient);
        btnRegisterRes = (Button)findViewById(R.id.btnRegisterRestaurant);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRegisterCli.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.VISIBLE);
                btnRegisterRes.setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.INVISIBLE);
            }
        });
        btnRegisterRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this,RegisterRestaurantActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });
        if(SaveSharedPreferences.getAlreadyRun(StartActivity.this)){
            btnRegisterCli.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
            btnRegisterRes.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.INVISIBLE);
        }

        SaveSharedPreferences.setAlreadyRun(StartActivity.this);
    }
}
