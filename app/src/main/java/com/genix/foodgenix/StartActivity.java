package com.genix.foodgenix;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_view_non_home);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f18e41")));

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
        btnRegisterCli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this,RegisterUserActivity.class);
                startActivity(intent);
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
