package com.genix.foodgenix;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterUserActivity extends AppCompatActivity {

    @Bind(R.id.edUsername)
    EditText edUsername;
    @Bind(R.id.edPassword)
    EditText edPassword;
    @Bind(R.id.edCPassword)
    EditText edCPassword;
    @Bind(R.id.edName)
    EditText edName;
    @Bind(R.id.edAddress)
    EditText edAddress;
    @Bind(R.id.dpDate)
    DatePicker dpDate;
    @Bind(R.id.edEmail)
    EditText edEmail;
    @Bind(R.id.rbMale)
    RadioButton rbMale;
    @Bind(R.id.rbFemale)
    RadioButton rbFemale;
    @Bind(R.id.btnRegister)
    Button btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        ButterKnife.bind(this);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edPassword.getText().toString().equals(edCPassword.getText().toString())){

                    HashMap<String,String> params = new HashMap<String, String>();
                    params.put("NAME",edName.getText().toString());
                    params.put("ADDRESS",edAddress.getText().toString());
                    params.put("PHONE","-");
                    String date = dpDate.getYear()+"-"+dpDate.getMonth()+"-"+dpDate.getDayOfMonth();
                    params.put("DOB",date);
                    params.put("EMAIL",edEmail.getText().toString());
                    params.put("GENDER",(rbMale.isChecked()?"1":"0"));
                    params.put("USERNAME",edUsername.getText().toString());
                    params.put("PASSWORD",edPassword.getText().toString());
                    params.put("STATUS","1");
                    new RegisterUserTask().execute(params);

                }else{
                    Toast.makeText(getApplicationContext(),"Please ensure that password and confirm password are the same",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    class RegisterUserTask extends AsyncTask<HashMap<String,String>,Void,String>{
        @Override
        protected String doInBackground(HashMap<String, String>... params) {
            String response = EDWSRequest.Request("POST","user/register",params[0]);
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnRegister.setText("Registering ....");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            btnRegister.setText("Register Now");
            if(s.equals("Failed")){
                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
            }
            else{
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        }
    }
}
