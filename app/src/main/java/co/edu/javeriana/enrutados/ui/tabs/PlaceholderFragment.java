package co.edu.javeriana.enrutados.ui.tabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import co.edu.javeriana.enrutados.AddEvidenceActivity;
import co.edu.javeriana.enrutados.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    FloatingActionButton addEvidence;
    TextView labelCurrentLocation;
    SharedPreferences sharedPref;

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
}