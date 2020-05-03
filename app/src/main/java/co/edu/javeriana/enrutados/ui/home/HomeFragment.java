package co.edu.javeriana.enrutados.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

import co.edu.javeriana.enrutados.MapsActivity;
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
    private static final String ENRUTADOS_LOG = ">enrutados";
    private static final String ENRUTADOS_API_URL = "https://fast-forest-61371.herokuapp.com/";
    private static EnrutadosApi enrutadosApi;

    View root;
    Route activeRoute;

    static {
        Retrofit restClient = new Retrofit.Builder()
                .baseUrl(ENRUTADOS_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        enrutadosApi = restClient.create(EnrutadosApi.class);
    }

    RecyclerView listOfPoints;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter pointAdapter;
    List <Task> tasks;
    SharedPreferences sharedPref;
    ImageButton mapButton;
    TextView tvRouteName, tvCreatedAt, tvFinishedAt, tvCoordinatorEmail;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        mapButton = root.findViewById(R.id.map_button);
        mapButton.setOnClickListener(new MapButtonHandler());

        tvRouteName = root.findViewById(R.id.route_name);
        tvCreatedAt = root.findViewById(R.id.label_created_at);
        tvFinishedAt = root.findViewById(R.id.label_finished_at);
        tvCoordinatorEmail = root.findViewById(R.id.label_coordinator_email);

        linearLayoutInit();
        fetchActiveRoute();

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        return root;
    }

    private void linearLayoutInit()
    {
        listOfPoints = root.findViewById(R.id.list_of_tasks);
        listOfPoints.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(root.getContext());
        listOfPoints.setLayoutManager(layoutManager);
    }

    private void fetchActiveRoute()
    {
        sharedPref = this.getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String email = sharedPref.getString(root.getContext().getString(R.string.preference_user_key), "");
        Log.d(ENRUTADOS_LOG, "User email: " + email);
        enrutadosApi.getTechnicianActiveRoute(email).enqueue(new GetTechnicianActiveRouteHandler());
    }

    private class MapButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(root.getContext(), MapsActivity.class);
            intent.putExtra("route", activeRoute);
            startActivity(intent);
        }
    }

    class GetTechnicianActiveRouteHandler implements Callback<Route> {
        @Override
        public void onResponse(Call<Route> call, Response<Route> response) {
            if (!response.isSuccessful()) {
                Log.d(ENRUTADOS_LOG, String.valueOf(response.code()));
                return;
            }

            activeRoute = response.body();
            displayRouteData();
        }

        @Override
        public void onFailure(Call<Route> call, Throwable t) {
            Log.d(ENRUTADOS_LOG, t.getMessage());
        }

        private void displayRouteData() {
            String finishedAt = activeRoute.getFinishedAt() != null ? activeRoute.getFinishedAt().substring(0, 10) : "-";

            tvRouteName.setText(activeRoute.getName());
            tvCreatedAt.setText(String.format(tvCreatedAt.getText().toString(), activeRoute.getCreatedAt().substring(0, 10)));
            tvFinishedAt.setText(String.format(tvFinishedAt.getText().toString(), finishedAt));
            tvCoordinatorEmail.setText(activeRoute.getCoordinatorName());

            pointAdapter = new PointAdapter(root.getContext(), activeRoute.getPoints());
            listOfPoints.setAdapter(pointAdapter);
            pointAdapter.notifyDataSetChanged();
        }
    }
}