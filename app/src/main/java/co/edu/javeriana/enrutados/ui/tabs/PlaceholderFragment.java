package co.edu.javeriana.enrutados.ui.tabs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.edu.javeriana.enrutados.AddEvidenceActivity;
import co.edu.javeriana.enrutados.R;
import co.edu.javeriana.enrutados.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private final static int READ_STORAGE_REQUEST_ID = 2;

    private PageViewModel pageViewModel;
    FloatingActionButton addEvidence;
    TextView labelCurrentLocation;
    SharedPreferences sharedPref;
    GridView evidenceImages;
    List <String> filepaths;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tabbed, container, false);

        addEvidence = root.findViewById(R.id.add_evidence);
        labelCurrentLocation = root.findViewById(R.id.label_curent_location);
        evidenceImages = root.findViewById(R.id.evidence_images);

        sharedPref = this.getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        float latitude = sharedPref.getFloat(root.getContext().getString(R.string.preference_lat_key), (float)1000.0);
        float longitude = sharedPref.getFloat(root.getContext().getString(R.string.preference_long_key), (float)1000.0);
        if (latitude < 999.0 && longitude < 999.9) {
            labelCurrentLocation.setText(getString(R.string.location_lat_long, latitude, longitude));
        }

        addEvidence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddEvidenceActivity.class);
                startActivity(intent);
            }
        });

        final ScrollView infoSegment = root.findViewById(R.id.info_segment);
        final FrameLayout evidenceSegment = root.findViewById(R.id.evidence_segment);
        // TODO null pointer, file not found
        //checkImages(this.getActivity());


        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    if (s.equals("1")) {
                        infoSegment.setVisibility(View.VISIBLE);
                    } else if (s.equals("2")) {
                        evidenceSegment.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        return root;
    }

    private void checkImages(Activity context) {
        if (Utils.checkForPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            fillAdapter();
            ImageEvidenceAdapter imageEvidenceAdapter = new ImageEvidenceAdapter(context, filepaths);
            evidenceImages.setAdapter(imageEvidenceAdapter);
        } else {
            Utils.requestPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    "",
                    READ_STORAGE_REQUEST_ID);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_STORAGE_REQUEST_ID: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fillAdapter();
                }
            }
        }
    }

    private void fillAdapter() {
        filepaths = new ArrayList<String>();

        String path = Environment.getExternalStorageDirectory().toString()+"/Pictures/routes_1";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }
    }
}