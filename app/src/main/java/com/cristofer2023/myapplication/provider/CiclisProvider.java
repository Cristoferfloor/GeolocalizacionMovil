package com.cristofer2023.myapplication.provider;

import com.cristofer2023.myapplication.modelos.Ciclistas;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CiclisProvider {
    DatabaseReference mDatabase;

    public CiclisProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Ciclistas"); // Corrected the reference path
    }

    public Task<Void> create(Ciclistas ciclistas) {
        Map<String, Object> map = new HashMap<>(); // Use Object instead of Objects
        map.put("name", ciclistas.getName());
        map.put("email", ciclistas.getEmail());
        return mDatabase.child(ciclistas.getId()).setValue(map);
    }
}
