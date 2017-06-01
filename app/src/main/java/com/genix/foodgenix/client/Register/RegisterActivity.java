package com.genix.foodgenix.client.Register;

import android.app.FragmentManager;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.genix.foodgenix.R;

public class RegisterActivity extends AppCompatActivity {
    Button nextRegister;
    String step="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client);

        findViewById(R.id.fragment2).setVisibility(View.INVISIBLE);

        nextRegister = (Button)findViewById(R.id.RegisterButton);
        nextRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    fragmentTransaction.replace(R.id.fragment2, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    findViewById(R.id.fragment2).setVisibility(View.VISIBLE);
                }
                else{

                }
            }
        });
    }

}
