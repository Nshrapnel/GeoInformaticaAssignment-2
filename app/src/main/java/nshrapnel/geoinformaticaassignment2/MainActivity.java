package nshrapnel.geoinformaticaassignment2;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;

import java.util.List;


public class MainActivity extends AppCompatActivity implements PermissionsListener {

    private MapView mapView;
    private MapboxMap map;
    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;
    private FloatingActionButton floatingActionButton;
    private String address;

    @SuppressLint("HandlerLeak")
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String result;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    result = bundle.getString("address");
                    break;
                default:
                    result = null;
            }
            address = result;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.accessToken));
        setContentView(R.layout.activity_main);
        locationEngine = LocationSource.getLocationEngine(this);
        locationEngine.activate();
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
            }
        });
        floatingActionButton = (FloatingActionButton) findViewById(R.id.location_toggle_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    toggleGps(!map.isMyLocationEnabled());
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

    private void toggleGps(boolean enableGps) {
        if (enableGps) {
            permissionsManager = new PermissionsManager(this);
            if (!PermissionsManager.areLocationPermissionsGranted(this)) {
                permissionsManager.requestLocationPermissions(this);
            } else {
                enableLocation(true);
            }
        } else {
            enableLocation(false);
        }
    }

    private void enableLocation(boolean enabled) {
        if (enabled) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location lastLocation = locationEngine.getLastLocation();
            if (lastLocation != null) {
                LatLng coordinates = new LatLng(lastLocation);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10));
                GeoHelper.getAddressFromLocation(lastLocation, this, new GeocoderHandler());
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
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 16));
                        locationEngine.removeLocationEngineListener(this);
                        GeoHelper.getAddressFromLocation(location, getApplicationContext(), new GeocoderHandler());
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
        if (granted) {
            enableLocation(true);
        } else {
            finish();
        }
    }
}