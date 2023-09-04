package com.cristofer2023.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cristofer2023.myapplication.R;
import com.cristofer2023.myapplication.activities.ciclista.MapCiclisActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button mbuttonBici;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mbuttonBici = findViewById(R.id.btnAnBici);
        mbuttonBici.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSelectAuth();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(MainActivity.this, MapCiclisActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        // You can perform any necessary actions when the activity starts here
    }

    private void goToSelectAuth() {
        Intent intent = new Intent(MainActivity.this, SelectOptionAuthActivity.class);
        startActivity(intent);
    }
}
