package com.genix.foodgenix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    Button btn_signin;
    EditText username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_signin = (Button)findViewById(R.id.btn_signin);
        username = (EditText)findViewById(R.id.username);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSharedPreferences.setUserName(LoginActivity.this,username.getText().toString());
                Intent homepage = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(homepage);
            }
        });

    }
}
