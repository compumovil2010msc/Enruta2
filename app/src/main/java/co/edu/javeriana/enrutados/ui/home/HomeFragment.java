package co.edu.javeriana.enrutados.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import co.edu.javeriana.enrutados.model.Point;
import co.edu.javeriana.enrutados.model.Post;
import co.edu.javeriana.enrutados.model.Route;
import co.edu.javeriana.enrutados.model.Task;
import co.edu.javeriana.enrutados.services.EnrutadosApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    RecyclerView listOfTasks;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter pointAdapter;
    private static final String ENRUTADOS_LOG = ">enrutados";
    private static final String JSON_PLACEHOLDER_URL = "https://fast-forest-61371.herokuapp.com/";
    List <Task> tasks;
    SharedPreferences sharedPref;

    private static EnrutadosApi enrutadosApi;

    static {
        Retrofit restClient = new Retrofit.Builder()
                .baseUrl(JSON_PLACEHOLDER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        enrutadosApi = restClient.create(EnrutadosApi.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        listOfTasks = (RecyclerView) root.findViewById(R.id.list_of_tasks);
        listOfTasks.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(root.getContext());
        listOfTasks.setLayoutManager(layoutManager);

        sharedPref = this.getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String email = sharedPref.getString(root.getContext().getString(R.string.preference_user_key), "");

        Log.d(ENRUTADOS_LOG, "User email: " + email);

        Call<List<Route>> call = enrutadosApi.getTechnicianRoutes(email);

        call.enqueue(new Callback<List<Route>>() {
            @Override
            public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                if (!response.isSuccessful()) {
                    Log.d(ENRUTADOS_LOG, String.valueOf(response.code()));
                    return;
                }

                List<Route> routes = response.body();
                if (routes.size() > 0) {
                    LoadData(root, routes.get(0).getPoints());
                }
            }

            @Override
            public void onFailure(Call<List<Route>> call, Throwable t) {
                Log.d(ENRUTADOS_LOG, t.getMessage());
            }
        });

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    private void LoadData(View v, List<Point> points) {
        pointAdapter = new PointAdapter(v.getContext(), points);
        listOfTasks.setAdapter(pointAdapter);
        pointAdapter.notifyDataSetChanged();
    }
}
