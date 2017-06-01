package com.genix.foodgenix.client.Register;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.genix.foodgenix.LoginActivity;
import com.genix.foodgenix.MainActivity;
import com.genix.foodgenix.R;
import com.genix.foodgenix.client.HomeActivity;

public class RegisterActivity extends AppCompatActivity {
    Button nextRegister,viewLogin,viewRegisterClient,viewRegisterRestaurant;
    String step="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.fragment).setVisibility(View.INVISIBLE);

        nextRegister = (Button)findViewById(R.id.RegisterButton);
        nextRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if(!nextRegister.getText().toString().equals("Register")){

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new Fragment();
                    if(step.equals("")){
                        fragment = new NamaFragment();
                        step = "Nama";
                        nextRegister.setText("Next");
                    }
                    else if(step.equals("Nama")){
                        fragment = new IdentitasFragment();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                        step ="finish";
                        nextRegister.setText("Register");
                    }
                    fragmentTransaction.replace(R.id.fragment, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    findViewById(R.id.fragment).setVisibility(View.VISIBLE);
                */
                    findViewById(R.id.LoginAsli).setVisibility(View.VISIBLE);
                    findViewById(R.id.RegisterAsliRestaurant).setVisibility(View.VISIBLE);
                    findViewById(R.id.RegisterAsliClient).setVisibility(View.VISIBLE);
                    findViewById(R.id.RegisterButton).setVisibility(View.INVISIBLE);
                /*
                }
                else{

                }
                */
            }
        });

        viewRegisterClient = (Button)findViewById(R.id.RegisterAsliClient);
        viewRegisterClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.fragment).setVisibility(View.VISIBLE);
                findViewById(R.id.LoginAsli).setVisibility(View.INVISIBLE);
                findViewById(R.id.RegisterAsliRestaurant).setVisibility(View.INVISIBLE);
                findViewById(R.id.RegisterAsliClient).setVisibility(View.INVISIBLE);
                findViewById(R.id.RegisterData).setVisibility(View.VISIBLE);

            }
        });

        viewRegisterRestaurant = (Button)findViewById(R.id.RegisterAsliRestaurant);
        viewRegisterRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Rrestaurant = new Intent(RegisterActivity.this, com.genix.foodgenix.restaurant.Register.RegisterActivity.class);
                startActivity(Rrestaurant);
            }
        });

        viewLogin = (Button)findViewById(R.id.LoginAsli);
        viewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(login);
            }
        });

    }

}
