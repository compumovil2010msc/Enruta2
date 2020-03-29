package co.edu.javeriana.enrutados.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.edu.javeriana.enrutados.R;
import co.edu.javeriana.enrutados.model.Alert;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    RecyclerView listOfAlerts;
    RecyclerView.Adapter alertAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<Alert> alerts;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        listOfAlerts = root.findViewById(R.id.list_of_alerts);
        listOfAlerts.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(root.getContext());
        listOfAlerts.setLayoutManager(layoutManager);
        loadData(root);

        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    private void loadData(View v) {
        alerts = new ArrayList<>();
        alertAdapter = new AlertAdapter(v.getContext(), alerts);
        listOfAlerts.setAdapter(alertAdapter);


        alerts.add(new Alert(true, "Coordinador", "Hola Ramón", "8:31"));
        alerts.add(new Alert(false, "Ramón", "Hola.", "8:37"));
        alerts.add(new Alert(true, "Coordinador", "Ya te aprobé el punto.", "8:45"));

        alerts.add(new Alert("Han llegado los repuestos!", "8:47"));

        alerts.add(new Alert(false, "Ramón", "Gracias, ya estoy terminando aquí.", "8:50"));
        alerts.add(new Alert(true, "Coordinador", "Cualquier cosa me avisdas, vale?", "8:51"));
        alertAdapter.notifyDataSetChanged();
    }
}
