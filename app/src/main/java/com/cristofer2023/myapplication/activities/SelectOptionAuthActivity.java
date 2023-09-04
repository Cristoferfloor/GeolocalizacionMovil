package com.cristofer2023.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cristofer2023.myapplication.R;
import com.cristofer2023.myapplication.activities.ciclista.RegisterActivity;
import com.cristofer2023.myapplication.includes.Mytolback;

public class SelectOptionAuthActivity extends AppCompatActivity {





    Button mButtonLogin;
    Button mButtonRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_auth);

        Mytolback.show(this,"Seleciona Opcion",true);

        mButtonLogin=findViewById(R.id.btnGotologin);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }

            private void goToLogin() {

                Intent intent = new Intent(SelectOptionAuthActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        mButtonRegister=findViewById(R.id.btnRegister);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goRegister();
            }

            private void goRegister() {
                Intent intent = new Intent(SelectOptionAuthActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}