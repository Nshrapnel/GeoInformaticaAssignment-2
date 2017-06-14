package nshrapnel.geoinformaticaassignment2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newLatLngZoom;


public class Map extends AppCompatActivity implements PermissionsListener {

    private MapView mapView;
    private MapboxMap map;
    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;
    private FloatingActionButton floatingActionButton;
    private String address;
    private ArrayList<String> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.accessToken));
        setContentView(R.layout.map);
        locationEngine = LocationSource.getLocationEngine(this);
        locationEngine.activate();
        Intent intent = getIntent();
        if (intent.hasExtra("Addresses")) {
            addresses = intent.getStringArrayListExtra("Addresses");
        }
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                if (addresses != null) {
                    for (String address : addresses) {
                        LatLng location = GeoHelper.getLocationFromAddress(address, Map.this);
                        map.addMarker(new MarkerViewOptions()
                                .position(location)
                                .title(address));
                    }
                    LatLng coordinates = GeoHelper.getLocationFromAddress(addresses.get(0), Map.this);
                    map.moveCamera(newLatLngZoom(coordinates, 10));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationEngineListener != null) {
            locationEngine.removeLocationEngineListener(locationEngineListener);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void enableLocation(boolean enabled) {
        if (enabled) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location lastLocation = locationEngine.getLastLocation();
            if (lastLocation != null) {
                LatLng coordinates = new LatLng(lastLocation);
                map.moveCamera(newLatLngZoom(coordinates, 10));
                address = GeoHelper.getAddressFromLocation(lastLocation.getLatitude(),
                        lastLocation.getLongitude(), this);
                map.addMarker(new MarkerViewOptions()
                        .position(new LatLng(coordinates.getLatitude(), coordinates.getLongitude()))
                        .title(address));
            }
            locationEngineListener = new LocationEngineListener() {
                @Override
                public void onConnected() {
                }

                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        LatLng coordinates = new LatLng(location);
                        map.moveCamera(newLatLngZoom(coordinates, 16));
                        locationEngine.removeLocationEngineListener(this);
                        address = GeoHelper.getAddressFromLocation(location.getLatitude(),
                                location.getLongitude(), getApplicationContext());
                        map.addMarker(new MarkerViewOptions()
                                .position(new LatLng(coordinates.getLatitude(), coordinates.getLongitude()))
                                .title(address));
                    }
                }
            };
            locationEngine.addLocationEngineListener(locationEngineListener);
            floatingActionButton.setImageResource(R.drawable.ic_location_disabled_24dp);
        } else {
            floatingActionButton.setImageResource(R.drawable.ic_my_location_24dp);
        }
        map.setMyLocationEnabled(enabled);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "This app needs location permissions in order to show its functionality.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (!granted) {
            finish();
        }
    }
}