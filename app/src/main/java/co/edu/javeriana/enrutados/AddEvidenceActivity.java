package co.edu.javeriana.enrutados;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AddEvidenceActivity extends AppCompatActivity {

    private final static int LOCATION_CODE = 1;
    private Button buttonAddImage, buttonAddDocument, buttonAddLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_evidence);

        buttonAddImage = (Button) findViewById(R.id.button_add_image);
        buttonAddDocument = (Button) findViewById(R.id.button_add_document);
        buttonAddLocation = (Button) findViewById(R.id.button_add_location);

        buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        if (!Utils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
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
        Toast.makeText(this, "I have permission", Toast.LENGTH_SHORT).show();
    }
}
