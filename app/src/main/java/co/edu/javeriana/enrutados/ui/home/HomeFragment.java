package co.edu.javeriana.enrutados.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import co.edu.javeriana.enrutados.model.Task;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    RecyclerView listOfTasks;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter taskAdapter;

    List <Task> tasks;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        listOfTasks = (RecyclerView) root.findViewById(R.id.list_of_tasks);
        listOfTasks.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(root.getContext());
        listOfTasks.setLayoutManager(layoutManager);
        LoadData(root);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    private void LoadData(View v) {
        tasks = new ArrayList<Task>();
        taskAdapter = new TaskAdapter(v.getContext(), tasks);
        listOfTasks.setAdapter(taskAdapter);

        tasks.add(new Task("Puerto Claver", "Instalación de punto de red y mantenimiento preventivo", false));
        tasks.add(new Task("Margento", "Instalación de antena y puntos de red", false));
        tasks.add(new Task("Cuturú", "Reparación de teléfonos satelitales", false));
        tasks.add(new Task("Palanca", "Insertar caja de energía", true));
        tasks.add(new Task("Colorado", "Instalación de punto de red y mantenimiento preventivo", true));

        taskAdapter.notifyDataSetChanged();
    }
}
