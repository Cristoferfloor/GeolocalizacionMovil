package com.cristofer2023.myapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cristofer2023.myapplication.R;
import com.cristofer2023.myapplication.activities.ciclista.MapCiclisActivity;
import com.cristofer2023.myapplication.activities.ciclista.RegisterActivity;
import com.cristofer2023.myapplication.includes.Mytolback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {


    TextInputEditText mtextImputEmail;
    TextInputEditText mtextImputPassword;

    Button mButtonLogin;


    FirebaseAuth mAuth;

    DatabaseReference mDatabase;



    AloadingDialog mDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Mytolback.show(this,"Login Usuario",true);

        mtextImputEmail=findViewById(R.id.textinputEmail);
        mtextImputPassword=findViewById(R.id.textimputpassword);
        mButtonLogin = findViewById(R.id.btnlogin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDialog = new AloadingDialog(LoginActivity.this);


        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }


        });
    }

    private void login() {
        String email = mtextImputEmail.getText().toString();
        String password = mtextImputPassword.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {
                mDialog.show();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    /*Toast.makeText(LoginActivity.this, "El inicio de sesión se realizó exitosamente", Toast.LENGTH_SHORT).show();*/

                                    Intent intent = new Intent(LoginActivity.this, MapCiclisActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);// Aquí puedes realizar acciones adicionales después del inicio de sesión exitoso.
                                } else {
                                    Toast.makeText(LoginActivity.this, "El inicio de sesión falló. Verifica tu correo y contraseña.", Toast.LENGTH_SHORT).show();
                                }
                                mDialog.dismiss();


                            }
                        });
            } else {
                Toast.makeText(LoginActivity.this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Ingresa tu correo y contraseña.", Toast.LENGTH_SHORT).show();
        }
    }

}