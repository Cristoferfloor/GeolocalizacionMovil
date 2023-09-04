package com.cristofer2023.myapplication.activities.ciclista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cristofer2023.myapplication.R;
import com.cristofer2023.myapplication.activities.AloadingDialog;
import com.cristofer2023.myapplication.includes.Mytolback;
import com.cristofer2023.myapplication.modelos.Ciclistas;
import com.cristofer2023.myapplication.provider.Authproviter;
import com.cristofer2023.myapplication.provider.CiclisProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    Button mButtonResgistro;

    TextInputEditText mtextImputEmail;
    TextInputEditText mtextImputPassword;
    TextInputEditText mtextImputname;

    AloadingDialog mDialog;

    Authproviter mAuthprovides;
    CiclisProvider mCiclisprovider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Mytolback.show(this,"Registro Usuario",true);
        mDialog = new AloadingDialog(RegisterActivity.this);
        mAuthprovides = new Authproviter();
        mCiclisprovider = new CiclisProvider();

        mButtonResgistro = findViewById(R.id.btnRegister);
        mtextImputname = findViewById(R.id.textimputname);
        mtextImputEmail = findViewById(R.id.textimputEmail);
        mtextImputPassword = findViewById(R.id.textimputPassword);

        mButtonResgistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickregistro();
            }
        });
    }

    private void clickregistro() {
        final String name = mtextImputname.getText().toString();
        final String email = mtextImputEmail.getText().toString(); // Corregido
        String password = mtextImputPassword.getText().toString(); // Corregido

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {
                mDialog.show();
                register(name,email,password);
            } else {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ingrese en todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
    void register(final String name,final String email, String password){
        mAuthprovides.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.hide();
                if (task.isSuccessful()) {
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Ciclistas ciclistas = new Ciclistas(id,name,email);
                    create(ciclistas);
                } else {
                    Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void create(Ciclistas ciclistas){
        mCiclisprovider.create(ciclistas).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    /*Toast.makeText(RegisterActivity.this, "Registro Exitoso", Toast.LENGTH_SHORT).show();*/
                    Intent intent = new Intent(RegisterActivity.this,MapCiclisActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(RegisterActivity.this, "No se pudo registrar", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}
