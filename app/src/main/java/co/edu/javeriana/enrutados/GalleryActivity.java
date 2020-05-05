package co.edu.javeriana.enrutados;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static java.io.File.separator;

public class GalleryActivity extends AppCompatActivity {

    private final static int IMAGE_PICKER_REQUEST = 1001;
    private final static int TAKE_PHOTO_REQUEST = 1002;
    private final static int READ_STORAGE_REQUEST_ID = 2;
    private final static int CAMERA_REQUEST_ID = 3;
    //this is part of read but it's good to know what to do when granted
    private final static int WRITE_STORAGE_REQUEST_ID = 4;

    Button buttonGetImage, buttonCamera, buttonSaveImage;
    ImageView imgToAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        buttonGetImage = (Button) findViewById(R.id.button_get_image);
        buttonCamera = (Button) findViewById(R.id.button_camera);
        buttonSaveImage = (Button) findViewById(R.id.button_save_image);
        imgToAdd = (ImageView) findViewById(R.id.img_to_add);

        buttonGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermission(READ_STORAGE_REQUEST_ID);
            }
        });
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });
        buttonSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermission(WRITE_STORAGE_REQUEST_ID);
            }
        });

    }

    private void checkStoragePermission (int code) {
        if (Utils.checkForPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (code == READ_STORAGE_REQUEST_ID) {
                askForImage();
            } else if (code == WRITE_STORAGE_REQUEST_ID) {
                prepareBitmap();
            }
        } else {
            Utils.requestPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    "",
                    code);
        }
    }

    private void checkCameraPermission () {
        if (Utils.checkForPermission(this, Manifest.permission.CAMERA)) {
            takePhoto();
        } else {
            Utils.requestPermission(
                    this,
                    Manifest.permission.CAMERA,
                    "",
                    CAMERA_REQUEST_ID);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_STORAGE_REQUEST_ID: {
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    askForImage();
                }
            }
            case CAMERA_REQUEST_ID: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
            }
            case WRITE_STORAGE_REQUEST_ID: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prepareBitmap();
                }
            }
        }
    }

    private void askForImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICKER_REQUEST);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_PHOTO_REQUEST);
        }
    }

    private void prepareBitmap() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable)imgToAdd.getDrawable();
        if (bitmapDrawable == null) {
            Toast.makeText(this, getString(R.string.error_image_not_selected), Toast.LENGTH_SHORT).show();
        } else {
            Bitmap bitmap = bitmapDrawable.getBitmap();
            try {
                saveImage(bitmap, "routes_1");
                Toast.makeText(this, getString(R.string.image_saved), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent (this, AddEvidenceActivity.class);
                startActivity(intent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_PICKER_REQUEST: {
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imgToAdd.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
            case TAKE_PHOTO_REQUEST: {
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imgToAdd.setImageBitmap(imageBitmap);
                }
                return;
            }
        }
    }

    private void saveImage(Bitmap bitmap, String folderName) throws FileNotFoundException {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName);
            values.put(MediaStore.Images.Media.IS_PENDING, true);
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                saveImageToStream(bitmap, getContentResolver().openOutputStream(uri));
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                getContentResolver().update(uri, values, null, null);
            }
        } else {
            File directory = new File(Environment.getExternalStorageDirectory().toString() + separator + folderName);
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = "" + System.currentTimeMillis() + ".png";
            File file = new File(directory, fileName);
            saveImageToStream(bitmap, new FileOutputStream(file));
            if (file.getAbsolutePath() != null) {
                ContentValues values = contentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                // .DATA is deprecated in API 29
                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
    }

    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values;
    }

    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
