package co.edu.javeriana.enrutados;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddEvidenceActivity extends AppCompatActivity {

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

    }
}
