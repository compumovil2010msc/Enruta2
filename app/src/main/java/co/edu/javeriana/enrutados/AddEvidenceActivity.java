package co.edu.javeriana.enrutados;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class AddEvidenceActivity extends AppCompatActivity {

    private final static int LOCATION_CODE = 1;
    private Button buttonAddImage, buttonAddDocument, buttonAddLocation;
    SharedPreferences sharedPref;
    private FusedLocationProviderClient mFusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_evidence);

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        buttonAddImage = (Button) findViewById(R.id.button_add_image);
        buttonAddDocument = (Button) findViewById(R.id.button_add_document);
        buttonAddLocation = (Button) findViewById(R.id.button_add_location);

        buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GalleryActivity.class);
                startActivity(intent);
            }
        });
        buttonAddDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        buttonAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocation();
            }
        });
    }

    private void addLocation() {
        if (!Utils.checkForPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Utils.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "", LOCATION_CODE);
        } else {
            saveLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_CODE : {
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveLocation();
                }
                return;
            }
        }
    }

    private void saveLocation() {

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new
                OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.i(MainActivity.ENRUTADOS_LOG, "onSuccess location");
                        if (location != null) {
                            Log.i(MainActivity.ENRUTADOS_LOG, location.getLatitude() + ", " + location.getLongitude());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putFloat(getString(R.string.preference_lat_key), (float)location.getLatitude());
                            editor.putFloat(getString(R.string.preference_long_key), (float)location.getLongitude());
                            editor.apply();
                        }
                    }
                });

    }
}
