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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Date;

import co.edu.javeriana.enrutados.maps.helpers.FetchURL;
import co.edu.javeriana.enrutados.maps.helpers.TaskLoadedCallback;
import co.edu.javeriana.enrutados.model.LocationLog;
import co.edu.javeriana.enrutados.model.Point;
import co.edu.javeriana.enrutados.model.Route;

import static co.edu.javeriana.enrutados.Utils.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener, GoogleMap.OnMarkerClickListener, TaskLoadedCallback {

    public static final int REQUEST_CODE = 1;
    public static final int REQUEST_CODE_STORAGE = 2;
    public static final String REQUEST_REASON = "Location required in order to provide directions";
    public static final int MAP_STYLE_THRESHOLD = 5000;
    public static final String LOG_ENRUTADOS = ">enrutados";
    public static final String LOCATIONS_JSON = "locations.json";
    public static final String REQUEST_EXTERNAL_STORAGE_REASON = "Permission needed for saving current location";
    private GoogleMap mMap;
    private JSONArray localizaciones = new JSONArray();

    LocationManager locationManager;
    LocationListener locationListener;
    Route activeRoute;
    Marker userMarker;

    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorListener;

    Polyline currentPolyline;

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
            if (event.values[0] > MAP_STYLE_THRESHOLD && !mapLight) {
                Log.i(">MAPS", "LIGHT MAP " + event.values[0]);
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.map_light_style));
                mapLight = true;
            } else if (event.values[0] <= MAP_STYLE_THRESHOLD && mapLight) {
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
            switch (requestCode) {
                case REQUEST_CODE:
                    if (checkForPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    }
                    break;
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

        mMap.setOnMarkerClickListener(this);

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

    private void logLocation() {
        if (!checkForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.i(LOG_ENRUTADOS, "Sin permisos para almacenar en external storage");
            requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE_REASON, REQUEST_CODE_STORAGE);
            return;
        }

        LocationLog log = new LocationLog(
                String.valueOf(userMarker.getPosition().latitude),
                String.valueOf(userMarker.getPosition().longitude),
                (new Date(System.currentTimeMillis())).toString()
        );

        Writer output = null;
        String filename = LOCATIONS_JSON;
        localizaciones.put(log.toJSON());

        try {
            File file = new File(getBaseContext().getExternalFilesDir(null), filename);
            Log.i(LOG_ENRUTADOS, "Ubicacion del archivo: " + file);
            output = new BufferedWriter(new FileWriter(file));
            output.write(localizaciones.toString());
            Log.i(LOG_ENRUTADOS, localizaciones.toString());
            output.close();
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();

        Log.i(LOG_ENRUTADOS, "Se completo la consulta al API de Google");

        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return drawRouteFromUserMarker(marker);
    }

    public boolean drawRouteFromUserMarker(Marker marker)
    {
        if (userMarker == null || userMarker == marker)
            return false;

        new FetchURL(MapsActivity.this)
                .execute(
                        getUrl(
                                userMarker.getPosition(),
                                marker.getPosition(),
                                "driving"),
                        "driving"
                );

        return false;
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);

        Log.i(LOG_ENRUTADOS, "URL path: " + url);

        return url;
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
            logLocation();

            if (currentPolyline != null)
                currentPolyline.remove();

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
