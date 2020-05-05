package co.edu.javeriana.enrutados;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class AddEvidenceActivity extends AppCompatActivity {

    private final static int LOCATION_CODE = 1;
    private final static int READ_STORAGE_REQUEST_ID = 2;
    private static final int FILE_SELECT_CODE = 1003;

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
                checkStoragePermission();
            }
        });
        buttonAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocation();
            }
        });
    }

    private void checkStoragePermission () {
        if (Utils.checkForPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showFileChooser();
        } else {
            Utils.requestPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    "",
                    READ_STORAGE_REQUEST_ID);
        }
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
            case READ_STORAGE_REQUEST_ID: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser();
                }
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

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE: {
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(MainActivity.ENRUTADOS_LOG, "File Uri: " + uri.toString());
                    // Get the path
                    String path = uri.getPath();
                    Log.d(MainActivity.ENRUTADOS_LOG, "File Path: " + path);
                    // Get the file instance
                    File file = new File(path);
                    saveFile("something", file);
                    // Initiate the upload
                }
                return;
            }
        }
    }

    private void saveFile(String fileName, File file) {
        try {
            FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(toBytes(file));
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] toBytes(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

}
