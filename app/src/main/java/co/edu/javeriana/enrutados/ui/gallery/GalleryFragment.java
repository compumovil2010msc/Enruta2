package co.edu.javeriana.enrutados.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

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
    EditText messageText;
    ImageButton sendBtn;
    List<Alert> alerts;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        sendBtn = root.findViewById(R.id.button_send_message);
        messageText = root.findViewById(R.id.edit_text_message);
        listOfAlerts = root.findViewById(R.id.list_of_alerts);
        listOfAlerts.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(root.getContext());
        listOfAlerts.setLayoutManager(layoutManager);
        loadData(root);

        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.v("ImageButton", "Clicked");
            }
        });

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


        alerts.add(new Alert(true, "Coordinador", "Hola Ramón", "Mar 29, 8:31"));
        alerts.add(new Alert(false, "Ramón", "Hola.", "Mar 29, 8:37"));
        alerts.add(new Alert(true, "Coordinador", "Ya te aprobé el punto.", "Mar 29, 8:45"));

        alerts.add(new Alert("Han llegado los repuestos!", "Mar 29, 8:47"));

        alerts.add(new Alert(false, "Ramón", "Gracias, ya estoy terminando aquí.", "Mar 29, 8:50"));
        alerts.add(new Alert(true, "Coordinador", "Cualquier cosa me avisdas, vale?", "Mar 29, 8:51"));
        alertAdapter.notifyDataSetChanged();
    }


}
