package co.edu.javeriana.enrutados.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import co.edu.javeriana.enrutados.CoordinatorActivity;
import co.edu.javeriana.enrutados.MainActivity;
import co.edu.javeriana.enrutados.R;
import co.edu.javeriana.enrutados.model.Route;
import co.edu.javeriana.enrutados.model.User;
import co.edu.javeriana.enrutados.services.EnrutadosApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddRouteActivity extends AppCompatActivity {

    EditText createRouteName, createRouteMail;
    Button createRouteButton;
    Route newRoute;
    SharedPreferences sharedPref;
    String email;
    String tech;
    long routeId;

    private static final String ENRUTADOS_API_URL = "https://fast-forest-61371.herokuapp.com/";
    private static EnrutadosApi enrutadosApi;

    static {
        Retrofit restClient = new Retrofit.Builder()
                .baseUrl(ENRUTADOS_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        enrutadosApi = restClient.create(EnrutadosApi.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        email = sharedPref.getString(getString(R.string.preference_user_key), "");
        Log.d(MainActivity.ENRUTADOS_LOG, "User email: " + email);

        createRouteName = (EditText) findViewById(R.id.create_route_name);
        createRouteMail = (EditText) findViewById(R.id.create_route_mail);
        createRouteButton = (Button) findViewById(R.id.create_route_button);

        createRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRoute = new Route();
                newRoute.setName(createRouteName.getText().toString());
                tech = createRouteMail.getText().toString();
                postRoute();
            }
        });
    }

    private void postRoute() {
        enrutadosApi.createRoute(newRoute).enqueue(new AddRouteActivity.PostRouteHandler());
    }

    private void getRoutes() {
        enrutadosApi.getRoutes().enqueue(new AddRouteActivity.GetRoutesHandler());
    }

    private void putRoute(Route route) {
        enrutadosApi.setTech(email, route.getId()).enqueue(new AddRouteActivity.PutTechRouteHandler());
        goBack();
    }

    private void goBack() {
        Intent intent = new Intent(AddRouteActivity.this, CoordinatorActivity.class);
        startActivity(intent);
    }

    class PostRouteHandler implements Callback<Route> {
        @Override
        public void onResponse(Call<Route> call, Response<Route> response) {
            getRoutes();
            //getRoutes();
        }

        @Override
        public void onFailure(Call<Route> call, Throwable t) {
            Log.d(MainActivity.ENRUTADOS_LOG, t.getMessage());
            getRoutes();
            //goBack();
        }

    }

    class GetRoutesHandler implements Callback<List<Route>> {
        @Override
        public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
            if (!response.isSuccessful()) {
                Log.d(MainActivity.ENRUTADOS_LOG, String.valueOf(response.code()));
                return;
            }

            List <Route> allRoutes = response.body();
            Route createdRoute = findRoute(allRoutes);
            putRoute(createdRoute);
        }

        @Override
        public void onFailure(Call<List<Route>> call, Throwable t) {
            Log.d(MainActivity.ENRUTADOS_LOG, t.getMessage());
        }

        private Route findRoute(List<Route> routes) {
            for (Route r: routes) {
                if (r.getName().equals(createRouteName.getText().toString())) {
                    return r;
                }
            }
            return null;
        }
    }

    class PutTechRouteHandler implements Callback<Route> {
        @Override
        public void onResponse(Call<Route> call, Response<Route> response) {
            goBack();
        }

        @Override
        public void onFailure(Call<Route> call, Throwable t) {
            Log.d(MainActivity.ENRUTADOS_LOG, t.getMessage());
            goBack();
        }

    }
}
