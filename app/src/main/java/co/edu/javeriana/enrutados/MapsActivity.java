package co.edu.javeriana.enrutados;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import co.edu.javeriana.enrutados.model.Point;
import co.edu.javeriana.enrutados.model.Route;

import static co.edu.javeriana.enrutados.Utils.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {

    public static final int REQUEST_CODE = 1;
    public static final String REQUEST_REASON = "Location required in order to provide directions";
    public static final int MAP_STYLE_THRESHOLD = 5000;
    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;
    Route activeRoute;
    Marker userMarker;

    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorListener;

    private boolean mapLight = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        activeRoute = (Route) getIntent().getSerializableExtra("route");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListenerHandle();
        
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        startLocationService();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT && mMap != null) {
            if (event.values[0] > 5000 && !mapLight) {
                Log.i(">MAPS", "LIGHT MAP " + event.values[0]);
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.map_light_style));
                mapLight = true;
            } else if (event.values[0] <= 5000 && mapLight) {
                Log.i(">MAPS", "DARK MAP " + event.values[0]);
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.map_dark_style));
                mapLight = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void startLocationService() {
        if (!checkForPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_REASON, REQUEST_CODE);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkForPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Integer i = 0;

        if (mapLight) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_light_style));
        }

        for (Point point : activeRoute.getPoints()) {
            LatLng location = new LatLng(point.getLatitude(), point.getLongitude());
            MarkerOptions makerOptions = new MarkerOptions().position(location).title(point.getName());

            if (point.getFinished())
                makerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            mMap.addMarker(makerOptions);

            if (i.equals(0))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));

            i++;
        }
    }

    private class LocationListenerHandle implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            if (userMarker != null && SphericalUtil.computeDistanceBetween(userMarker.getPosition(), userLatLng) < 500)
                return;

            if (userMarker != null)
                userMarker.remove();

            userMarker = mMap.addMarker(new MarkerOptions().position(userLatLng).title("You are here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
