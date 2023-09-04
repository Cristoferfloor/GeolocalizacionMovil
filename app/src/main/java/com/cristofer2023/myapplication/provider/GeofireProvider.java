package com.cristofer2023.myapplication.provider;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeofireProvider {
    private DatabaseReference mDatabase;
    private GeoFire mGeofire;


    public GeofireProvider(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Ciclistas_Activos");
        mGeofire = new GeoFire(mDatabase);

    }

    public void saveLocation(String idCiclis, LatLng latLng){

        mGeofire.setLocation(idCiclis,new GeoLocation(latLng.latitude,latLng.longitude));



    }

    public  void  removeLocation(String idCiclis ){
        mGeofire.removeLocation(idCiclis);
    }
    public GeoQuery getActiveCiclistas(LatLng latLng){
        GeoQuery geoQuery = mGeofire.queryAtLocation(new GeoLocation(latLng.latitude,latLng.longitude),80);
       geoQuery.removeAllListeners();
       return geoQuery;

    }





}
