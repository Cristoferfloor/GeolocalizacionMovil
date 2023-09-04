package com.cristofer2023.myapplication.activities.ciclista;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cristofer2023.myapplication.R;
import com.cristofer2023.myapplication.activities.MainActivity;
import com.cristofer2023.myapplication.includes.Mytolback;

import com.cristofer2023.myapplication.provider.Authproviter;
import com.cristofer2023.myapplication.provider.GeofireProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseError;

import android.Manifest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapCiclisActivity extends AppCompatActivity implements OnMapReadyCallback {

    Authproviter mAuthprovider;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private LocationRequest mLocationRequest;

    private FusedLocationProviderClient mfuselocation;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private Marker mMarker;

    private Button mButtomConect;

    private boolean mIsCknnect = false;

    private LatLng mCurrentLating;

    private GeofireProvider mGeofireProvider;

    private List<Marker> mCiclisMarket = new ArrayList<>();

    private boolean mFirstime = true;

    private PlacesClient mPlaces;

    private String mOring;

    private LatLng mOriginLart;

    private AutocompleteSupportFragment mAutoComplete;

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    mCurrentLating = new LatLng(location.getLatitude(),location.getLongitude());

                      if (mMarker != null){
                          mMarker.remove();
                      }
                    mMarker = mMap.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(),location.getLongitude())
                    )
                                    .title("Tu Pocicion")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.iconsbi))

                    );
                    // Obtener la unicacion del usuario en tiempo real
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(

                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(18f)
                                    .build()
                    ));

                    updateLocation();

                    if (mFirstime){
                        mFirstime = false;
                        getciclistasActivos();
                    }
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_ciclis);
        Mytolback.show(this, "MAPA CICLISTA", false);

        // Inicializar proveedores y clientes
        mAuthprovider = new Authproviter();
        mGeofireProvider = new GeofireProvider();
        mfuselocation = LocationServices.getFusedLocationProviderClient(this);

        // Inicializar el mapa
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        // Configurar el botón de conexión
        mButtomConect = findViewById(R.id.btnconect);
        mButtomConect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsCknnect) {
                    disconnect();
                } else {
                    startLocation();
                }
            }
        });

        // Inicializar Places API si está disponible

    }


    private void updateLocation(){
       if (mAuthprovider.exisSession()&&mCurrentLating!=null){
           mGeofireProvider.saveLocation(mAuthprovider.getId(),mCurrentLating);
       }
    }


    private void getciclistasActivos(){
        mGeofireProvider.getActiveCiclistas(mCurrentLating).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //AÑADIREMOS LOS MARCADORES DE LOS CCICLISTAS
                 for (Marker marker: mCiclisMarket){
                     if (marker.getTag() != null){
                         if (marker.getTag().equals(key)){
                             return;
                         }
                     }
                 }

                 LatLng ciclisLating = new LatLng(location.latitude,location.longitude);
                 Marker marker = mMap.addMarker(new MarkerOptions().position(ciclisLating).title("Ciclistas Disponibles").icon(BitmapDescriptorFactory.fromResource(R.drawable.iconsbi)));
                 marker.setTag(key);
                 mCiclisMarket.add(marker);

            }

            @Override
            public void onKeyExited(String key) {
                for (Marker marker: mCiclisMarket){
                    if (marker.getTag() != null){
                        if (marker.getTag().equals(key)){

                            marker.remove();
                            mCiclisMarket.remove(marker);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

                //Actualizar posicion del los ciclistas

                for (Marker marker: mCiclisMarket){
                    if (marker.getTag() != null){
                        if (marker.getTag().equals(key)){
                            marker.setPosition(new LatLng(location.latitude,location.longitude));
                            return;
                        }
                    }
                }

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }






    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        ;
        mLocationRequest.setSmallestDisplacement(5);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    if (gpsActived()){
                        mfuselocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mMap.setMyLocationEnabled(true);

                    }else {
                        showAlertDialogNOGPS();
                    }


                }
            } else {
                checkLocationPermissions();

            }
        } else {
            checkLocationPermissions();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mfuselocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }else {
            showAlertDialogNOGPS();
        }
    }

    private void  showAlertDialogNOGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Activa tu Ubicacion para Continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),SETTINGS_REQUEST_CODE);

                    }
                })
                .create()
                .show();
    }

    private boolean gpsActived(){
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActive = true;
        }
        return isActive;
    }

    private void disconnect(){

        if (mfuselocation != null){
            mButtomConect.setText("Conectarse");
            mIsCknnect = false;
            mfuselocation.removeLocationUpdates(mLocationCallback);
            if (mAuthprovider.exisSession()){
                mGeofireProvider.removeLocation(mAuthprovider.getId());
            }

        }else {
            Toast.makeText(this,"No te puedes desconectar",Toast.LENGTH_SHORT).show();
        }

    }
    private void startLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActived()){
                    mButtomConect.setText("Desconectarse");
                    mIsCknnect = true;
                    mfuselocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mMap.setMyLocationEnabled(true);
                }else {
                    showAlertDialogNOGPS();
                }

            }else {
                checkLocationPermissions();
            }

        }else {
            if (gpsActived()){
                mfuselocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }else {
                showAlertDialogNOGPS();
            }


        }
    }
    private void checkLocationPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
             if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                 new AlertDialog.Builder(this)
                         .setTitle("Proporcionar los Permisos para continuar ")
                         .setMessage("Esta aplicacion requiere de los permisos de ubicacion para poder utilizarse ")
                         .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 ActivityCompat.requestPermissions(MapCiclisActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
                             }
                         })
                         .create()
                         .show();

             }
             else {
                 ActivityCompat.requestPermissions(MapCiclisActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

             }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ciclismenu, menu);
        return super.onCreateOptionsMenu(menu);
        // Return true instead of super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    void logout(){
        disconnect();
        mAuthprovider.logout();
        Intent intent = new Intent(MapCiclisActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
